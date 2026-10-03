## 1. Public methods and types to test

### `ReflectiveTypeAdapterFactory`
Public API members visible in the supplied class:

1. **Constructor**
   ```java
   public ReflectiveTypeAdapterFactory(
       ConstructorConstructor constructorConstructor,
       FieldNamingStrategy fieldNamingPolicy,
       Excluder excluder)
   ```
   Requires three non-null collaborator objects for meaningful operation.

2. **`excludeField(Field f, boolean serialize)`**
   ```java
   public boolean excludeField(Field f, boolean serialize)
   ```
   Delegates to the static overload using the factory’s configured `Excluder`.

3. **`create(Gson gson, TypeToken<T> type)`**
   ```java
   public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type)
   ```
   Produces a reflective `TypeAdapter` for object types, or `null` for non-object/primitive raw types.

### `ReflectiveTypeAdapterFactory.Adapter<T>`
This nested class is `public static final`, and its operational methods are public through `TypeAdapter<T>`:

4. **`read(JsonReader in)`**
   ```java
   public T read(JsonReader in) throws IOException
   ```

5. **`write(JsonWriter out, T value)`**
   ```java
   public void write(JsonWriter out, T value) throws IOException
   ```

Its constructor is package-private:
```java
Adapter(ObjectConstructor<T> constructor, Map<String, BoundField> boundFields)
```
Therefore, tests outside `com.google.gson.internal.bind` cannot directly instantiate it unless they use the factory or are placed in the same package.

### Package-private API potentially testable from the same package

6. **Static `excludeField(Field f, boolean serialize, Excluder excluder)`**
   ```java
   static boolean excludeField(Field f, boolean serialize, Excluder excluder)
   ```

`BoundField`, `createBoundField`, `getFieldNames`, and `getBoundFields` are private or package-private implementation details and are most reliably exercised through `create`, `Gson.toJson`, and `Gson.fromJson`.

---

## 2. Input types and valid input ranges

There are no numeric ranges in this class. Inputs are Java reflection, Gson, and streaming JSON objects.

| Method | Inputs | Meaningful valid inputs |
|---|---|---|
| Constructor | `ConstructorConstructor`, `FieldNamingStrategy`, `Excluder` | Initialized Gson-internal collaborators. |
| `excludeField` | `Field`, `boolean` | A reflective field and direction flag: `true` for serialization, `false` for deserialization. |
| `create` | `Gson`, `TypeToken<T>` | A configured `Gson` instance and a type token. Object/class types are handled; non-object raw types produce `null`. |
| `Adapter.read` | `JsonReader` | JSON `null` or a JSON object. Field values must be readable by the chosen field adapter. |
| `Adapter.write` | `JsonWriter`, object value | A writable JSON writer and either `null` or an instance compatible with the adapter’s type. |

Field categories reachable through `create` include:

- Primitive fields, such as `int`.
- Boxed primitive fields, such as `Integer`.
- Strings, arrays, collections, maps, object fields, and parameterized generic fields.
- Inherited fields.
- Fields annotated with `@SerializedName`, including alternate names.
- Fields annotated with `@JsonAdapter`.
- Excluded fields, as determined by `Excluder`.
- Interfaces as target raw types.
- Classes whose fields resolve to duplicate JSON names.

---

## 3. Conditions and reachable branches

### `excludeField`
```java
return !excluder.excludeClass(f.getType(), serialize)
    && !excluder.excludeField(f, serialize);
```

Reachable outcomes:

- Returns `true` when neither the field’s type nor the field itself is excluded.
- Returns `false` when `excludeClass(...)` returns `true`.
- Returns `false` when `excludeField(...)` returns `true`.
- The method name is potentially misleading: a return value of `true` means the field is *included/eligible*, not excluded.

### `create`
```java
if (!Object.class.isAssignableFrom(raw)) {
  return null;
}
```

Branches:

- Raw type is not assignable to `Object` (for example primitive `int.class`) → returns `null`.
- Raw type is assignable to `Object` → obtains an `ObjectConstructor` and creates an `Adapter`.

### Field-name selection (`getFieldNames`, indirectly through `create`)
Branches:

- No `@SerializedName` → uses `FieldNamingStrategy.translateName(field)`.
- `@SerializedName` with no alternates → uses only `annotation.value()`.
- `@SerializedName` with alternates → first name is the primary serialized name; remaining names are accepted for deserialization.

### Field inclusion and hierarchy traversal (`getBoundFields`, indirectly through `create`)
Branches:

- Raw target type is an interface → returns an empty field map.
- Field excluded for both serialization and deserialization → skipped.
- Field enabled in at least one direction → bound.
- Alternate `@SerializedName` values:
  - Primary name remains serializable.
  - Alternate names are changed to `serialize = false`, so they are deserialization-only.
- Duplicate JSON field names across declared/inherited fields → `IllegalArgumentException`.
- Superclass fields are traversed until `Object.class`.

### Per-field adapter selection (`createBoundField`)
Branches:

- Field has no `@JsonAdapter` → uses `context.getAdapter(fieldType)`.
- Field has `@JsonAdapter` and `getTypeAdapter(...)` returns a non-null adapter → uses that adapter.
- Field has `@JsonAdapter` but `getTypeAdapter(...)` returns `null` → falls back to `context.getAdapter(fieldType)`.
- Primitive fields:
  - During deserialization, a `null` adapter result is not assigned to a primitive field.
- Non-primitive fields:
  - A `null` adapter result is assigned as `null`.
- Serialization avoids direct self-reference:
  ```java
  return fieldValue != value;
  ```
  A field referring to its containing object is omitted.

### Important bug-related serialization branch
The source declares:
```java
final boolean jsonAdapterPresent = mapped != null;
```
but does not use that variable.

During serialization, it always wraps the field adapter:
```java
TypeAdapter t =
    new TypeAdapterRuntimeTypeWrapper(context, typeAdapter, fieldType.getType());
t.write(writer, fieldValue);
```

This is the behavior implicated by Gson-10. A field-level `@JsonAdapter` should take precedence over the default/runtime adapter selection. The supplied triggering result demonstrates that, in the buggy source, runtime type wrapping can override the intended field-level adapter during serialization.

### `Adapter.read`
Branches:

- Input token is `NULL`:
  - Consumes `null`.
  - Returns `null`.
- Input is not `NULL`:
  - Constructs an instance via `ObjectConstructor.construct()`.
  - Reads an object.
- Encountered JSON name has no bound field → skips its value.
- Encountered bound field is not deserializable → skips its value.
- Encountered bound and deserializable field → invokes `BoundField.read`.
- `IllegalStateException` while reading → wrapped in `JsonSyntaxException`.
- `IllegalAccessException` while setting a field → wrapped in `AssertionError`.

### `Adapter.write`
Branches:

- Value is `null`:
  - Writes JSON `null`.
- Value is non-null:
  - Starts a JSON object.
  - Writes each bound field for which `writeField(value)` is `true`.
  - Omits fields that are not serializable.
  - Omits a field whose value directly references the enclosing object.
  - Converts `IllegalAccessException` to `AssertionError`.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

### Normal cases

- Serialize and deserialize a normal object with one or more included fields.
- Use the configured `FieldNamingStrategy` for unannotated fields.
- Serialize a `@SerializedName` field using its primary name.
- Deserialize the same field using its primary name.
- Deserialize using a `@SerializedName(alternate = ...)` name.
- Serialize/deserialize inherited fields.
- Ignore unknown JSON properties during deserialization.
- Omit fields excluded by the configured `Excluder`.
- Handle primitive and non-primitive field values.

### Boundary cases

- Empty class / class with no eligible fields serializes as `{}`.
- Interface target type creates an adapter with no bound fields; direct behavior depends on the supplied `ObjectConstructor`, which must be verified from surrounding Gson context.
- Field with an empty `alternate` array behaves as a single-name field.
- Field with one or multiple alternate names.
- Duplicate JSON names:
  - Two fields with the same `@SerializedName` primary name.
  - An inherited field and subclass field resolving to the same JSON name.
  - A primary/alternate name collision.
- Self-referential field where `fieldValue == containingObject` should not be serialized.

### Null cases

- `Adapter.write(..., null)` must write JSON `null`.
- `Adapter.read(...)` must return `null` for JSON `null`.
- A JSON `null` for a primitive field must not overwrite the primitive’s existing/default value because of:
  ```java
  if (fieldValue != null || !isPrimitive)
  ```
- A JSON `null` for a non-primitive field may set the field to `null`.
- Constructor dependencies, `Gson`, `TypeToken`, `Field`, `JsonReader`, and `JsonWriter` are not null-checked in this source. Passing `null` is expected to cause a `NullPointerException` at the point of dereference, but this is not documented as an intentional API contract.

### Invalid and exceptional cases

- Invalid JSON structure or an unexpected token that causes `IllegalStateException` in `JsonReader` is wrapped as `JsonSyntaxException` by `Adapter.read`.
- Duplicate resolved JSON field names cause `IllegalArgumentException` during adapter creation.
- I/O failures from `JsonReader`, `JsonWriter`, or field adapters propagate as `IOException`.
- Reflective illegal access is converted to `AssertionError`; however, fields are explicitly made accessible, so this is likely uncommon in normal execution.
- Invalid `@JsonAdapter` configuration behavior cannot be fully determined from this class alone because the relevant logic is delegated to `JsonAdapterAnnotationTypeAdapterFactory.getTypeAdapter(...)`.

---

## 5. Required constructors, dependencies, and external objects

### Direct factory construction
To directly construct `ReflectiveTypeAdapterFactory`, tests need:

```java
new ReflectiveTypeAdapterFactory(
    ConstructorConstructor constructorConstructor,
    FieldNamingStrategy fieldNamingPolicy,
    Excluder excluder)
```

Required types:

- `com.google.gson.internal.ConstructorConstructor`
- `com.google.gson.FieldNamingStrategy`
- `com.google.gson.internal.Excluder`

The exact constructors/factory methods for `ConstructorConstructor` and configuration requirements for `Excluder` are not included in the supplied context. Therefore, direct unit tests of the factory constructor and `create` cannot be written reliably without inspecting those existing project classes.

### Preferred integration route for the reported behavior
The supplied trigger identifies a Gson functional test. The target class is normally used through a configured `Gson` instance. Therefore, the most meaningful test route is likely:

- Build/configure `Gson`.
- Define a model class containing a field-level `@JsonAdapter`.
- Serialize it using `Gson.toJson(...)`.
- Assert the emitted JSON.

This path avoids manually constructing internal Gson collaborators while still exercising `ReflectiveTypeAdapterFactory.create`, field binding, and `BoundField.write`.

### Objects needed for `Adapter.read`/`write` tests
If testing `Adapter` directly, tests need:

- An `ObjectConstructor<T>`.
- A `Map<String, BoundField>`.
- A `JsonReader`, typically over a `StringReader`.
- A `JsonWriter`, typically over a `StringWriter`.

However, `BoundField` is package-private and abstract, and the `Adapter` constructor is package-private. Such direct tests must be located in package:
```java
com.google.gson.internal.bind
```
or use normal Gson integration instead.

---

## 6. JUnit version and build tool

Supplied project metadata states:

- **JUnit version:** `junit-3.8.2.jar`
- **Build tool:** Maven

Consequences for eventual tests:

- Tests should use JUnit 3 style, generally:
  ```java
  import junit.framework.TestCase;
  ```
  and methods named `test...`.
- JUnit 4 annotations such as `@Test` should not be assumed available.
- Maven is the declared build tool, though the supplied metadata also references a Defects4J project build file. The prompt explicitly identifies Maven as the project build tool, so Maven should be treated as the provided build context.

---

## 7. Available test oracle

The supplied reliable oracle is the bug-triggering test and its asserted output:

```text
com.google.gson.functional.JsonAdapterAnnotationOnFieldsTest
::testPrimitiveFieldAnnotationTakesPrecedenceOverDefault

expected: {"part":["42"]}
actual:   {"part":[42]}
```

This establishes the following expected behavior:

- A field-level `@JsonAdapter` applied to a primitive field must take precedence during serialization.
- The adapter’s intended serialization of the primitive value `42` as the JSON string `"42"` must be preserved.
- The output must be:
  ```json
  {"part":["42"]}
  ```
  not:
  ```json
  {"part":[42]}
  ```

The test name specifically indicates the intended precedence rule:

> A primitive field annotation takes precedence over the default adapter.

No detailed GitHub issue content, API documentation, or existing test source is supplied beyond the test name and assertion failure. Therefore, expectations beyond this reported behavior must be derived only from the visible implementation and should be limited to clearly observable behavior.

---

## 8. Bug-report behaviors that should be tested

The primary regression behavior to test is:

1. **Field-level `@JsonAdapter` takes precedence for serialization of a primitive field.**
   - Use a model field whose declared type is primitive.
   - Annotate it with `@JsonAdapter`.
   - Use an adapter which emits a JSON string for the primitive value.
   - Serialize through `Gson`.
   - Verify the JSON uses the adapter’s representation:
     ```json
     {"part":["42"]}
     ```
   - Verify it does not serialize as a numeric value:
     ```json
     {"part":[42]}
     ```

The failure strongly suggests a runtime-type adapter wrapper incorrectly supersedes a field-level adapter. Relevant coverage should therefore distinguish:

2. **Field adapter versus default adapter precedence**
   - Confirm the field-level adapter controls the output rather than Gson’s standard primitive-number adapter.

3. **Serialization-specific behavior**
   - The supplied failure is serialization output. Deserialization behavior is not explicitly identified as failing and should not be asserted beyond what the visible code and adapter behavior can support.

4. **Potential runtime-wrapper interaction**
   - The implementation currently creates `jsonAdapterPresent` but does not use it, then always applies `TypeAdapterRuntimeTypeWrapper`.
   - A regression test should exercise a situation where the wrapper can select a different adapter than the field annotation, exactly as the bug trigger does.

The supplied source alone does not establish whether related scenarios—such as field-level adapters on boxed primitives, object fields, collection elements, or class-level `@JsonAdapter`—were intended to be fixed by this bug. Those cases should not be treated as bug requirements without additional oracle material.

---

## 9. Missing context needed for compilable and meaningful tests

The supplied information is sufficient to define the core Gson-10 regression expectation, but it is insufficient to produce a fully reliable, compilable test class without access to existing project APIs/test conventions. Specifically missing are:

1. **The source of the triggering test**
   - `JsonAdapterAnnotationOnFieldsTest`
   - Its helper model classes.
   - Its custom `TypeAdapter` implementation.
   - Its package and exact Gson setup.

   Reusing its established fixtures would be the safest way to produce a minimal regression test consistent with project conventions.

2. **The exact version-specific `@JsonAdapter` API**
   - The supplied target imports `com.google.gson.annotations.JsonAdapter`, but the annotation members and supported values are not shown.
   - It is not possible to safely invent the syntax for a custom adapter declaration without that source or an existing project usage.

3. **The APIs of dependent internal classes**
   - `ConstructorConstructor`
   - `Excluder`
   - `JsonAdapterAnnotationTypeAdapterFactory`
   - `TypeAdapterRuntimeTypeWrapper`

   These are needed for direct unit construction of `ReflectiveTypeAdapterFactory`, especially to test `create` and exclusion logic in isolation.

4. **Existing project test layout and Maven configuration**
   - The prompt provides JUnit 3.8.2 and Maven, but not test source roots, artifact version, Surefire configuration, or whether internal-package tests are already accepted.
   - This is needed to place a test in the correct package and ensure it is discovered.

5. **Full bug specification**
   - Only the summary and one observed failing assertion are supplied.
   - There is no authoritative statement covering deserialization, non-primitive values, or all possible `@JsonAdapter` configurations.

Accordingly, a precise regression test for the reported serialization failure is supportable in principle, but a compilable implementation should rely on the project’s existing `JsonAdapterAnnotationOnFieldsTest` source or existing `@JsonAdapter` usage rather than inventing fixture APIs or annotation syntax.