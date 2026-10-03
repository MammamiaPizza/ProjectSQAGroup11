## 1. Public methods to test

`TypeInfoFactory` is package-private and final. Its only public callable factory methods are:

1. `public static TypeInfoArray getTypeInfoForArray(Type type)`
2. `public static TypeInfo getTypeInfoForField(Field f, Type typeDefiningF)`

The constructor is private and must not be tested through instantiation.

Although these methods are `public`, the containing class is package-private, so test code must be in package `com.google.gson` to access it directly.

---

## 2. Input types and valid input ranges

### `getTypeInfoForArray(Type type)`

**Input**
- `java.lang.reflect.Type`

**Apparently valid inputs**
- Any `Type` recognized as an array by `TypeUtils.isArray(type)`.
- Based on Java reflection, this may include:
  - A `Class<?>` representing a normal array, such as `String[].class`, `int[].class`, or `Object[][].class`.
  - A `GenericArrayType`, such as `T[]` or `List<String>[]`, obtained from a reflected generic field.

**Apparently invalid inputs**
- Non-array `Class<?>`, such as `String.class`.
- A non-array `ParameterizedType`, such as `List<String>`.
- A `TypeVariable<?>` that does not itself represent an array.
- A `WildcardType` that does not represent an array.
- `null`, subject to the unspecified behavior of `TypeUtils.isArray(null)` and `Preconditions.checkArgument(...)`.

### `getTypeInfoForField(Field f, Type typeDefiningF)`

**Inputs**
- `f`: `java.lang.reflect.Field`
- `typeDefiningF`: `java.lang.reflect.Type`, intended to represent the type that contains/defines the field.

**Potentially valid forms of `typeDefiningF`**
- A concrete raw class, for example `SomeClass.class`.
- A `ParameterizedType`, generally obtained through reflective generic type information or a Gson `TypeToken`, such as `Foo<String>`.

**Field generic-type forms handled internally**
The field’s `f.getGenericType()` can be:

1. `Class<?>`
   - Example: `String value;`
2. `ParameterizedType`
   - Example: `List<String> value;`
   - Example involving a type variable: `List<T> value;`
3. `GenericArrayType`
   - Example: `T[] value;`
   - Example: `List<T>[] value;`
4. `TypeVariable<?>`
   - Example: `T value;`
5. `WildcardType`
   - Example: `List<? extends Number> value;`

The supplied implementation rejects any other custom or unsupported `Type` implementation with `IllegalArgumentException`.

---

## 3. Conditions and reachable branches

### `getTypeInfoForArray(Type type)`

Reachable paths:

1. `TypeUtils.isArray(type)` returns `true`
   - Returns `new TypeInfoArray(type)`.

2. `TypeUtils.isArray(type)` returns `false`
   - `Preconditions.checkArgument(...)` fails.
   - The exact exception type/message cannot be confirmed without the supplied implementations of `Preconditions` and `TypeUtils`.

3. `type == null`
   - Behavior cannot be determined reliably without `TypeUtils.isArray` and `Preconditions`.

---

### `getTypeInfoForField(Field f, Type typeDefiningF)`

The method first executes:

```java
Class<?> classDefiningF = TypeUtils.toRawClass(typeDefiningF);
Type type = f.getGenericType();
Type actualType = getActualType(type, typeDefiningF, classDefiningF);
return new TypeInfo(actualType);
```

Therefore, the following are relevant:

- Conversion of `typeDefiningF` to its raw class through `TypeUtils.toRawClass`.
- Reflection behavior from `f.getGenericType()`.
- Resolution behavior in private method `getActualType`.

#### Private `getActualType(...)` reachable branches

1. **Field type is a `Class<?>`**
   ```java
   if (typeToEvaluate instanceof Class<?>)
   ```
   - Returns the same type unchanged.
   - Examples: `String`, `Integer`, `String[]`, a non-generic application class.

2. **Field type is a `ParameterizedType`**
   ```java
   else if (typeToEvaluate instanceof ParameterizedType)
   ```
   - Reads owner type.
   - Resolves each actual type argument through `extractRealTypes(...)`.
   - Returns a new `ParameterizedTypeImpl`.
   - Relevant subcases:
     - Parameterized type with only concrete type arguments, e.g. `List<String>`.
     - Parameterized type containing a type variable, e.g. `List<T>`.
     - Nested parameterized generic types, e.g. `Map<String, List<T>>`.
     - Parameterized inner/member types with an owner type. The supplied code retains the original owner type without resolving it.

3. **Field type is a `GenericArrayType`**
   ```java
   else if (typeToEvaluate instanceof GenericArrayType)
   ```
   - Resolves the generic component type.
   - If the component type remains equal to the original component type:
     - Returns the original `GenericArrayType`.
   - If resolution changes the component type:
     - If the resolved component is a `Class<?>`, returns an array `Class<?>` through `TypeUtils.wrapWithArray(...)`.
     - Otherwise, returns `new GenericArrayTypeImpl(actualType)`.

4. **Field type is a `TypeVariable<?>` and parent type is a `ParameterizedType`**
   ```java
   else if (typeToEvaluate instanceof TypeVariable<?>) {
     if (parentType instanceof ParameterizedType) {
   ```
   - Finds the matching class type variable in `rawParentClass.getTypeParameters()`.
   - Uses the matching index to return the actual type argument from `parentType`.
   - Example intended case:
     ```java
     class Foo<T> { T value; }
     ```
     with `typeDefiningF` representing `Foo<String>`, the field `value` should resolve to `String`.

5. **Field type is a `TypeVariable<?>` and parent type is not a `ParameterizedType`**
   ```java
   throw new UnsupportedOperationException(...)
   ```
   - This is the currently reported failing branch for Gson-1.
   - It produces:
     ```
     UnsupportedOperationException:
     Expecting parameterized type, got class ...Bar.
     ```
   - The bug report identifies this branch as the cause of failure in:
     `com.google.gson.functional.TypeVariableTest::testSingle`.

6. **Field type is a `WildcardType`**
   ```java
   else if (typeToEvaluate instanceof WildcardType)
   ```
   - Resolves only `castedType.getUpperBounds()[0]`.
   - Consequences visible from the source:
     - `? extends Number` resolves by evaluating `Number`.
     - An unbounded `?` normally has upper bound `Object`, so it likely resolves to `Object`.
     - Lower bounds (`? super X`) are not directly used by this implementation.
   - Exact expected public `TypeInfo` behavior cannot be confirmed without `TypeInfo`.

7. **Unsupported `Type` implementation**
   ```java
   else {
     throw new IllegalArgumentException(...)
   }
   ```
   - A custom implementation of `Type` that is not a `Class`, `ParameterizedType`, `GenericArrayType`, `TypeVariable`, or `WildcardType` reaches this branch.

#### `extractRealTypes(...)`

Reachable conditions:

- `actualTypeArguments != null`
  - Allocates a same-length result array.
  - Resolves each element through `getActualType(...)`.

- `actualTypeArguments == null`
  - Calls `Preconditions.checkNotNull(actualTypeArguments)`.
  - Exact exception type cannot be established without `Preconditions`.

In normal Java reflection, `ParameterizedType.getActualTypeArguments()` should not return `null`; a custom `ParameterizedType` could provoke this case.

#### `getIndex(...)`

Reachable conditions:

- Matching type variable is found:
  - Returns its index.

- Type variable is absent:
  - Throws `IllegalStateException`:
    ```
    How can the type variable not be present in the class declaration!
    ```

This could occur if a field’s `TypeVariable` does not belong to `rawParentClass`, including some inheritance or declaring-class arrangements. Whether this is expected to be supported is not established by the supplied material.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

### Normal cases

#### Array factory
- Concrete object array class, e.g. `String[].class`.
- Primitive array class, e.g. `int[].class`.
- Multidimensional array class, e.g. `String[][].class`.
- Generic array type obtained from a field such as `T[]`.

#### Field factory
- Non-generic concrete field, e.g. `String name`.
- Non-generic array field, e.g. `int[] values`.
- Concrete parameterized field, e.g. `List<String> values`.
- Type-variable field where `typeDefiningF` is parameterized, e.g.:
  ```java
  class Foo<T> { T value; }
  ```
  evaluated using a `Foo<String>` type.
- Parameterized field containing a type variable, e.g. `List<T>`.
- Generic array field containing a type variable, e.g. `T[]`.
- Wildcard field such as `List<? extends Number>`.

### Boundary cases

- Parameterized type with zero actual type arguments, if obtainable through a custom `ParameterizedType`.
- Nested generic structures:
  - `List<List<T>>`
  - `Map<String, List<T>>`
  - `T[][]`
- Generic arrays whose component resolves:
  - From `T` to `String`, expected internal branch converts to `String[].class`.
  - From `T` to another parameterized type, expected internal branch returns a `GenericArrayTypeImpl`.
- Wildcards:
  - Unbounded `?`.
  - `? extends SomeType`.
  - `? super SomeType`; source indicates upper bound is used, not lower bound.

### Invalid cases

- `getTypeInfoForArray` invoked with a non-array `Type`.
- `getTypeInfoForField` invoked with unsupported custom `Type` implementations returned by `Field` is not normally possible, but could be reached indirectly with reflective types/custom wrappers only if the field generic type can provide one.
- A type-variable field whose type variable cannot be found in `rawParentClass.getTypeParameters()`.

### Null cases

The supplied source does not specify null contracts, and dependent implementations are absent.

Relevant null scenarios:

| Scenario | Reliable expected result from supplied source? |
|---|---|
| `getTypeInfoForArray(null)` | No. Depends on `TypeUtils.isArray(null)` and `Preconditions.checkArgument`. |
| `getTypeInfoForField(null, validType)` | Likely `NullPointerException` at `f.getGenericType()`, but only after `TypeUtils.toRawClass(typeDefiningF)` succeeds. |
| `getTypeInfoForField(validField, null)` | No. Depends on `TypeUtils.toRawClass(null)`. |
| `getTypeInfoForField(null, null)` | No reliable single exception/order can be asserted because `typeDefiningF` is processed first. |

### Exceptional cases explicitly visible in this source

| Condition | Exception |
|---|---|
| Non-array passed to `getTypeInfoForArray` | Whatever `Preconditions.checkArgument` throws; likely an argument-related exception, but not confirmable from supplied context. |
| `TypeVariable` field with non-parameterized `parentType` | `UnsupportedOperationException` |
| Unsupported `Type` subtype in `getActualType` | `IllegalArgumentException` |
| Type variable not found in raw parent class variables | `IllegalStateException` |
| Null `actualTypeArguments` passed to `extractRealTypes` | Whatever `Preconditions.checkNotNull` throws |

---

## 5. Required constructors, dependencies, and external objects

### Direct construction/access requirements

- No `TypeInfoFactory` construction is possible or required:
  - Constructor is private.
  - Methods are static.

- Tests must access `TypeInfoFactory` from package `com.google.gson`, because the class itself is package-private.

### Required production dependencies referenced by this class

The following project classes are required for compilation/execution of meaningful tests:

- `com.google.gson.TypeInfo`
- `com.google.gson.TypeInfoArray`
- `com.google.gson.TypeUtils`
- `com.google.gson.Preconditions`
- `com.google.gson.ParameterizedTypeImpl`
- `com.google.gson.GenericArrayTypeImpl`

Their constructors, equality behavior, accessors, and null/exception behavior are not supplied.

### Java/JDK reflection dependencies

- `java.lang.reflect.Field`
- `java.lang.reflect.Type`
- `java.lang.reflect.ParameterizedType`
- `java.lang.reflect.GenericArrayType`
- `java.lang.reflect.TypeVariable`
- `java.lang.reflect.WildcardType`

Tests would need helper fixture classes with generic fields in order to obtain reflection objects. For example, fixture fields are necessary to obtain realistic `Field`, `TypeVariable`, `ParameterizedType`, `GenericArrayType`, and `WildcardType` instances.

No test fixture classes are supplied.

---

## 6. JUnit version and build tool

- **JUnit version:** `junit-3.8.2.jar`
- **Test style implication:** JUnit 3 style is required/expected:
  - Typically `extends TestCase`
  - Test methods named `test...`
  - Assertions such as `assertEquals`, `assertSame`, `assertTrue`, and `fail`
  - No JUnit 4 annotations such as `@Test`

- **Build tool:** Defects4J project build
- **Build file identified:** `/root/defects4j/framework/projects/Gson/Gson.build.xml`

No Maven, Gradle, Ant target names, source-root locations, or project test conventions were provided beyond this.

---

## 7. Available test oracle

The supplied information provides only a limited oracle.

### Explicit oracle from the bug report

The report states:

- Triggering test:
  - `com.google.gson.functional.TypeVariableTest::testSingle`
- Observed failure:
  ```text
  java.lang.UnsupportedOperationException:
  Expecting parameterized type, got class
  com.google.gson.functional.TypeVariableTest$Bar.
  ```

This establishes that, for at least one scenario involving `TypeVariableTest.Bar`, invoking the relevant Gson behavior must **not** fail with that `UnsupportedOperationException` after the bug is corrected.

### Oracle available from source behavior

For behavior unrelated to the defect, the source gives observable internal intent:

- Array inputs are accepted by `getTypeInfoForArray`.
- Non-array inputs are rejected through `Preconditions.checkArgument`.
- Concrete class field types are passed through unchanged.
- Type variables are intended to be resolved against actual type arguments of a parameterized parent type.
- Generic-array component types are recursively resolved.
- Wildcards are resolved through their first upper bound.

However, source code alone is not a reliable oracle for asserting the intended corrected behavior of the bug, because the current source is explicitly the buggy version and must not be assumed correct.

### Missing oracle information

Not supplied:

- The source of `TypeVariableTest`.
- The body and assertions of `testSingle`.
- The actual generic declarations of `TypeVariableTest`, `Bar`, or any related types.
- Any expected serialized/deserialized JSON behavior.
- API documentation for `TypeInfo` and `TypeInfoArray`.
- Equality/accessor contracts for `TypeInfo`, `TypeInfoArray`, `ParameterizedTypeImpl`, and `GenericArrayTypeImpl`.
- The actual patch or fixed-version behavior.

Therefore, a precise expected resolved type for the bug-triggering scenario cannot be determined reliably from the supplied information.

---

## 8. Behaviors related to Gson-1 that should be tested

The supplied bug report identifies a failure caused by this implementation path:

```java
if (typeToEvaluate instanceof TypeVariable<?>) {
  if (parentType instanceof ParameterizedType) {
    ...
  }

  throw new UnsupportedOperationException(
      "Expecting parameterized type, got " + parentType ...
  );
}
```

### Required defect-focused behavior to cover

A regression test should exercise the situation where:

1. A field’s declared generic type is a `TypeVariable<?>`.
2. The relevant defining/parent type is reported as a raw `Class<?>` rather than a `ParameterizedType`.
3. That raw class is `TypeVariableTest.Bar` in the original triggering scenario.
4. The operation that previously reached this branch must complete without the reported `UnsupportedOperationException`.
5. The resulting resolved type or higher-level Gson behavior should be asserted using the original test’s expected outcome.

### Important limitation

The supplied data does **not** reveal:

- Whether `Bar` extends or implements a parameterized generic type.
- Which field is being resolved.
- What type variable is expected to resolve to.
- Whether the expected result is a concrete class, a parameterized type, a generic array, or another reflective `Type`.
- Whether the original test exercises `TypeInfoFactory` directly or indirectly through serialization/deserialization.
- The expected JSON input/output or object state.

Thus, the key regression assertion that can be stated with confidence is only:

> The known `UnsupportedOperationException` must no longer occur for the original `TypeVariableTest::testSingle` scenario.

A stronger assertion about the correct resulting type or serialization result requires the missing triggering test source or a behavioral specification.

---

## 9. Missing context required for compilable, meaningful, and reliable tests

The following information is missing:

1. **Source for `com.google.gson.functional.TypeVariableTest`**
   - Especially `testSingle`.
   - This is the most important missing item because it is the identified regression test and contains the intended bug oracle.

2. **Definitions of the fixture types involved in the failure**
   - In particular, `TypeVariableTest.Bar`.
   - Any generic superclasses, interfaces, fields, nested classes, or type parameters associated with `Bar`.

3. **Source or API contracts for dependent Gson classes**
   - `TypeInfo`
   - `TypeInfoArray`
   - `TypeUtils`
   - `Preconditions`
   - `ParameterizedTypeImpl`
   - `GenericArrayTypeImpl`

4. **How `TypeInfo` and `TypeInfoArray` expose their represented types**
   - Needed to assert returned results directly.
   - Without getters/equality behavior, a test may only be able to assert non-null or indirect behavior, which is insufficient for a strong regression test.

5. **Project test conventions and source layout**
   - Test source root/package structure.
   - Whether tests in `com.google.gson` are already used for package-private access.
   - Any shared test utility classes or reflection helpers.

6. **Precise expected corrected behavior for raw-class parent types**
   - The bug report identifies what fails but does not define the required resolution algorithm.
   - It is unknown whether the correct behavior should:
     - resolve type variables through a parameterized superclass,
     - resolve through a parameterized interface,
     - preserve the type variable,
     - use bounds,
     - or perform some other mapping.

7. **Expected null and precondition behavior**
   - Required only if null/invalid-input tests are to make exact exception assertions.
   - This depends on unsupplied `Preconditions` and `TypeUtils` implementations.

## Conclusion

The supplied source is sufficient to identify test targets, major branches, and the defect-triggering exception path. It is not sufficient to produce a reliable regression assertion for Gson-1 beyond verifying that the known `UnsupportedOperationException` is avoided in the original `TypeVariableTest::testSingle` scenario.

The missing `TypeVariableTest` source and the generic structure of `TypeVariableTest.Bar` are required to generate a compilable, meaningful, and behaviorally precise JUnit 3 regression test.