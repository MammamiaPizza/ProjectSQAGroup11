## Scope and project information

- **Project:** Gson  
- **Bug:** Gson-9 / GitHub PR 836  
- **Source version:** `Gson-9b` (buggy version supplied)  
- **Target classes:**
  - `com.google.gson.internal.bind.JsonTreeWriter`
  - `com.google.gson.internal.bind.TypeAdapters`
  - `com.google.gson.stream.JsonWriter`
- **Build tool:** Maven (as supplied)
- **JUnit version:** JUnit 3.8.2 (`junit-3.8.2.jar`), therefore tests should use JUnit 3 style (`extends TestCase`, `test...` methods) if generated later.

The provided bug context identifies the known regression trigger:

- `com.google.gson.stream.JsonWriterTest::testBoxedBooleans`
- Failure: `NullPointerException`

No source code for that existing test, Maven `pom.xml`, or the fixed diff is included. The supplied class documentation and reported failure are sufficient to identify the likely intended behavior, but not to reproduce exact existing-test conventions or project configuration details.

---

# 1. Public methods that should be tested

## `JsonTreeWriter`

### Constructor
- `public JsonTreeWriter()`

### Public operational methods
- `public JsonElement get()`
- `public JsonWriter beginArray() throws IOException`
- `public JsonWriter endArray() throws IOException`
- `public JsonWriter beginObject() throws IOException`
- `public JsonWriter endObject() throws IOException`
- `public JsonWriter name(String name) throws IOException`
- `public JsonWriter value(String value) throws IOException`
- `public JsonWriter nullValue() throws IOException`
- `public JsonWriter value(boolean value) throws IOException`
- `public JsonWriter value(double value) throws IOException`
- `public JsonWriter value(long value) throws IOException`
- `public JsonWriter value(Number value) throws IOException`
- `public void flush() throws IOException`
- `public void close() throws IOException`

### Inherited public configuration methods from `JsonWriter`
- `setIndent(String)`
- `setLenient(boolean)`
- `isLenient()`
- `setHtmlSafe(boolean)`
- `isHtmlSafe()`
- `setSerializeNulls(boolean)`
- `getSerializeNulls()`
- `jsonValue(String)`

`jsonValue(String)` is inherited but is not overridden by `JsonTreeWriter`; because the parent implementation writes to the deliberately unwritable underlying `Writer`, its use on a `JsonTreeWriter` is relevant as an exceptional/unsupported case.

---

## `TypeAdapters`

`TypeAdapters` itself cannot be instantiated because its only constructor is private and throws `UnsupportedOperationException`.

Its public API consists primarily of public static adapter and factory constants, plus public static factory methods.

### Public static factory methods
- `newFactory(TypeToken<TT> type, TypeAdapter<TT> typeAdapter)`
- `newFactory(Class<TT> type, TypeAdapter<TT> typeAdapter)`
- `newFactory(Class<TT> unboxed, Class<TT> boxed, TypeAdapter<? super TT> typeAdapter)`
- `newFactoryForMultipleTypes(Class<TT> base, Class<? extends TT> sub, TypeAdapter<? super TT> typeAdapter)`
- `newTypeHierarchyFactory(Class<T1> clazz, TypeAdapter<T1> typeAdapter)`

### Public static adapters/factories
The class exposes adapters and corresponding factories for:
- `Class`
- `BitSet`
- `Boolean`
- `Boolean` represented as JSON string
- `Byte`
- `Short`
- `Integer`
- `AtomicInteger`
- `AtomicBoolean`
- `AtomicIntegerArray`
- `Long`
- `Float`
- `Double`
- `Number`
- `Character`
- `String`
- `BigDecimal`
- `BigInteger`
- `StringBuilder`
- `StringBuffer`
- `URL`
- `URI`
- `InetAddress`
- `UUID`
- `Currency`
- `Timestamp`
- `Calendar` / `GregorianCalendar`
- `Locale`
- `JsonElement`
- `Enum`

For this bug, the directly relevant adapter is:

- `public static final TypeAdapter<Boolean> BOOLEAN`

Its write implementation is:

```java
@Override
public void write(JsonWriter out, Boolean value) throws IOException {
  if (value == null) {
    out.nullValue();
    return;
  }
  out.value(value);
}
```

The expression `out.value(value)` invokes `JsonWriter.value(boolean)` by unboxing the `Boolean`. This works for non-null values because the adapter checks for `null`. However, the triggering test name and changed files strongly indicate that Gson’s expected API includes an overload accepting boxed `Boolean`, and that this buggy source lacks that overload in `JsonWriter` and `JsonTreeWriter`.

---

## `JsonWriter`

### Constructor
- `public JsonWriter(Writer out)`

### Configuration/accessor methods
- `public final void setIndent(String indent)`
- `public final void setLenient(boolean lenient)`
- `public boolean isLenient()`
- `public final void setHtmlSafe(boolean htmlSafe)`
- `public final boolean isHtmlSafe()`
- `public final void setSerializeNulls(boolean serializeNulls)`
- `public final boolean getSerializeNulls()`

### JSON structure methods
- `public JsonWriter beginArray() throws IOException`
- `public JsonWriter endArray() throws IOException`
- `public JsonWriter beginObject() throws IOException`
- `public JsonWriter endObject() throws IOException`
- `public JsonWriter name(String name) throws IOException`

### Value-writing methods currently present
- `public JsonWriter value(String value) throws IOException`
- `public JsonWriter jsonValue(String value) throws IOException`
- `public JsonWriter nullValue() throws IOException`
- `public JsonWriter value(boolean value) throws IOException`
- `public JsonWriter value(double value) throws IOException`
- `public JsonWriter value(long value) throws IOException`
- `public JsonWriter value(Number value) throws IOException`

### Stream lifecycle methods
- `public void flush() throws IOException`
- `public void close() throws IOException`

### Relevant missing API in this source
There is **no** `value(Boolean value)` method in either supplied writer class.

That absence is central to the reported boxed-Boolean bug. A test which directly compiles `writer.value(Boolean.TRUE)` will compile by selecting and unboxing to `value(boolean)`, but `writer.value((Boolean) null)` will throw a `NullPointerException` before the method body is entered.

---

# 2. Input types and valid input ranges

## `JsonWriter`

| Method/category | Input | Valid range / contract inferred from source and Javadoc |
|---|---|---|
| Constructor | `Writer` | Must be non-null. |
| `setIndent` | `String` | Non-null in practice; implementation calls `indent.length()`. Empty string selects compact output; non-empty string selects formatted output. Javadoc says whitespace, but implementation does not validate whitespace. |
| `setLenient`, `setHtmlSafe`, `setSerializeNulls` | `boolean` | Both values valid. |
| `name` | `String` | Must be non-null; valid only inside an object and only when another name is not pending. |
| `value(String)` | `String` | Any string, including empty and escaped characters; `null` writes JSON `null`. |
| `jsonValue(String)` | `String` | Any raw text accepted by implementation; `null` writes JSON `null`. It does not validate whether non-null text is valid JSON. |
| `value(boolean)` | primitive `boolean` | `true` or `false`. |
| `value(double)` | primitive `double` | Source rejects `NaN`, positive infinity, and negative infinity unconditionally. Finite values, including `-0.0`, are accepted. The Javadoc says lenient mode should permit non-finite values, but this implementation does not do so. |
| `value(long)` | primitive `long` | Entire `long` range. |
| `value(Number)` | `Number` | `null` writes JSON `null`; otherwise output is based on `toString()`. In non-lenient mode, strings exactly equal to `"-Infinity"`, `"Infinity"`, and `"NaN"` are rejected. |
| Structural methods | No arguments | Must respect JSON nesting and document state. |

A strict writer permits one top-level JSON value only. Its Javadoc says strict top-level values must be arrays or objects, but this implementation’s state logic permits a first top-level primitive and only prevents multiple top-level values; this discrepancy should not be silently treated as a reliable oracle without existing tests or a stronger specification.

## `JsonTreeWriter`

The value input types are the same as `JsonWriter`, except that output is accumulated into a `JsonElement` tree rather than written to a stream.

Additional practical constraints:
- Object member names are represented by `String`; the implementation does not explicitly reject `null` names.
- `value(String null)` and `value(Number null)` delegate to `nullValue()`.
- `value(double)` and `value(Number)` reject non-finite numeric values when `isLenient()` is false.
- `setSerializeNulls(false)` suppresses object properties whose values are JSON null; it does not suppress null elements in arrays.
- Only one completed top-level product can be returned by `get()`.

## `TypeAdapters.BOOLEAN`

| Operation | Input | Expected valid behavior inferred from code |
|---|---|---|
| `read(JsonReader)` | JSON `null` | Returns Java `null`. |
| `read(JsonReader)` | JSON boolean | Returns matching `Boolean`. |
| `read(JsonReader)` | JSON string | Uses `Boolean.parseBoolean`; `"true"` becomes `true`, all other strings become `false`. |
| `write(JsonWriter, Boolean)` | `Boolean.TRUE` / `Boolean.FALSE` | Writes corresponding JSON boolean. |
| `write(JsonWriter, null)` | `null` | Calls `out.nullValue()`. |

---

# 3. Conditions and reachable branches

## `JsonWriter` branches

### Constructor
- `out == null` → `NullPointerException`.
- Non-null writer → initialized with `EMPTY_DOCUMENT` scope.

### Formatting/configuration
- Empty indent → compact output (`separator = ":"`).
- Non-empty indent → pretty output (`separator = ": "`).
- HTML-safe enabled/disabled → different escaping tables.
- Null serialization enabled/disabled → object null-member emitted or omitted.

### Structural state
Reachable states include:
- Empty document.
- Non-empty document.
- Empty/non-empty array.
- Empty/non-empty object.
- Dangling object name.
- Closed writer.

Important branches:
- Correct array/object opening and closing.
- Closing array when current scope is object or document → `IllegalStateException`.
- Closing object when current scope is array or document → `IllegalStateException`.
- Ending an object while a property name lacks a value → `IllegalStateException`.
- Naming outside an object is accepted initially but fails when the deferred name is written through a subsequent value/structure operation; repeated `name` calls fail immediately.
- A second top-level value in strict mode → `IllegalStateException`.
- A second top-level value in lenient mode → accepted.
- Any operation requiring `peek()` after close → `IllegalStateException`.

### Null handling
- `value((String) null)` → `nullValue()`.
- `value((Number) null)` → `nullValue()`.
- `nullValue()` with:
  - no deferred name → emits `null`;
  - deferred name and `serializeNulls == true` → emits name and `null`;
  - deferred name and `serializeNulls == false` → removes the deferred name and emits neither name nor value.

### Numeric branches
- `value(double)`:
  - finite → output number;
  - NaN or either infinity → `IllegalArgumentException`, irrespective of `lenient`.
- `value(Number)`:
  - null → JSON null;
  - strict and textual `NaN`/`Infinity`/`-Infinity` → `IllegalArgumentException`;
  - lenient → writes textual number representation.

### Closing/flushing
- `flush()` after close → `IllegalStateException`.
- `close()`:
  - exactly one complete top-level value → closes wrapped writer and succeeds;
  - incomplete structure or no completed top-level value → closes wrapped writer, then throws `IOException("Incomplete document")`;
  - repeated close behavior depends on underlying writer and stack state; source does not explicitly make `close()` idempotent.

## `JsonTreeWriter` branches

### Tree insertion (`put`)
- Pending property name:
  - non-null JSON element or `serializeNulls == true` → adds property to current `JsonObject`;
  - JSON null and `serializeNulls == false` → skips property;
  - always clears pending name.
- Empty stack and no pending name → replaces top-level `product`.
- Array on top of stack → adds element.
- Any other stack-top element → `IllegalStateException`.

### Structure methods
- Valid `beginArray` / `endArray`.
- Valid `beginObject` / `endObject`.
- `endArray` when stack empty, when an object is topmost, or while a name is pending → `IllegalStateException`.
- `endObject` when stack empty, when an array is topmost, or while a name is pending → `IllegalStateException`.
- `name`:
  - valid only with a top-level `JsonObject` and no pending name;
  - array, empty stack, or a second pending name → `IllegalStateException`.
  - Unlike `JsonWriter.name`, no explicit null-name check exists.

### `get()`
- Empty stack, including a newly created writer before structures begin → returns current product (initially `JsonNull.INSTANCE`).
- Stack not empty because document is unfinished → `IllegalStateException`.
- Stack contains close sentinel after successful `close()` → `IllegalStateException`.

### Numeric branches
- Strict mode rejects NaN and infinities for both `double` and `Number`.
- Lenient mode accepts them and stores them as `JsonPrimitive`.

### Lifecycle
- `flush()` is a no-op.
- `close()`:
  - unfinished tree stack → `IOException("Incomplete document")`;
  - completed tree → adds a sentinel so future structural/value operations fail;
  - unlike `JsonWriter.close()`, it does not close an external stream.

## `TypeAdapters` relevant branches

### `BOOLEAN`
- Input token `NULL` → consumes JSON null and returns Java null.
- Input token `STRING` → parses Boolean-compatible string.
- Other expected boolean input → delegates to `JsonReader.nextBoolean()`.
- Write null → `out.nullValue()`.
- Write true/false → `out.value(value)`.

### Factory methods
- `newFactory(TypeToken, adapter)` matches exact `TypeToken.equals`.
- `newFactory(Class, adapter)` matches exact raw class identity.
- primitive/boxed factory matches either exact type.
- multiple-types factory matches either specified class.
- hierarchy factory:
  - non-assignable requested type → returns null;
  - assignable requested type → wraps adapter;
  - deserialized non-null result incompatible with requested subtype → `JsonSyntaxException`.

---

# 4. Normal, boundary, invalid, null, and exceptional cases

## Essential cases for the reported bug

1. Write `Boolean.TRUE` through `TypeAdapters.BOOLEAN` to a `JsonWriter`.
   - Expected JSON: `true`.

2. Write `Boolean.FALSE` through `TypeAdapters.BOOLEAN` to a `JsonWriter`.
   - Expected JSON: `false`.

3. Write `null` through `TypeAdapters.BOOLEAN`.
   - Expected JSON: `null`.
   - The supplied adapter already has a null branch, so this alone does not expose the reported NPE.

4. Direct boxed-Boolean writer behavior:
   - `JsonWriter.value(Boolean.TRUE)`.
   - `JsonWriter.value(Boolean.FALSE)`.
   - **`JsonWriter.value((Boolean) null)`**, if the intended overload exists in the fixed API.
   - Expected intended behavior, based on the existing `String` and `Number` overload contracts and the bug report: boxed null should serialize as JSON `null`, not cause auto-unboxing `NullPointerException`.

5. Equivalent `JsonTreeWriter` behavior:
   - boxed true/false should produce boolean `JsonPrimitive`s;
   - boxed null should produce `JsonNull.INSTANCE`;
   - inside an object with `serializeNulls(false)`, boxed null should omit the property;
   - inside an array, boxed null should remain a null array element.

The last two points are especially important because the target change list explicitly includes `JsonTreeWriter`, not just `JsonWriter`.

## Other representative cases

### `JsonWriter`
- Constructor with `null`.
- Empty vs non-empty indentation.
- HTML-safe escaping for `<`, `>`, `&`, `=`, and `'`.
- General JSON escaping: quote, backslash, control characters, U+2028, U+2029.
- String `null`, Number `null`, explicit `nullValue`.
- Object null-member serialization enabled and disabled.
- Array null values when serialize-null setting is disabled.
- Valid nested object/array writing.
- Empty array and object output.
- Primitive, object, and array top-level values as allowed/rejected by actual established project tests.
- NaN and infinities through `double` and `Number`, strict and lenient.
- Bad nesting and dangling property name.
- Second top-level value strict/lenient.
- Incomplete document close.
- `flush` and operations after close.
- Underlying writer propagation of `IOException` during write/flush/close.

### `JsonTreeWriter`
- Initial `get()` returns JSON null.
- Completed object/array/scalar tree retrieval.
- Nested array/object construction.
- Suppressed null object properties.
- Null array elements retained when serialize-null setting is false.
- `get()` before closing a nested structure.
- Invalid begin/end/name ordering.
- Strict and lenient NaN/infinity behavior.
- `close()` on complete/incomplete tree.
- Operations and `get()` after close.
- `jsonValue` inherited behavior is expected to reach `UNWRITABLE_WRITER` and fail with `AssertionError`; however, it is not documented as supported by `JsonTreeWriter`, so this is an implementation-observation test rather than a bug-contract test.

### `TypeAdapters`
For broad adapter coverage, test each adapter’s documented/implemented null, normal serialization, normal deserialization, malformed input, and conversion-specific cases. Examples:
- `BIT_SET`: number, boolean, string bit values; malformed strings and unsupported token types.
- `CHARACTER`: null, one-character string, empty/multiple-character strings.
- `NUMBER`: null, number, non-number token.
- `BIG_DECIMAL`/`BIG_INTEGER`: valid and invalid numeric strings.
- URI/URL/UUID: null and invalid values.
- `JSON_ELEMENT`: all JSON token types, nested object/array, and unsupported token state.
- Enum aliases supplied by `@SerializedName`.
- Factory matching/mismatch and hierarchy result-type validation.

These broad cases are not all directly related to Gson-9’s boxed-Boolean defect.

---

# 5. Constructors, dependencies, and external objects required

## `JsonWriter`
Required:
- A non-null `java.io.Writer`, commonly `StringWriter` for output assertions.

Useful dependencies:
- `StringWriter` for normal serialized-output tests.
- A custom `Writer` that throws `IOException` for propagation tests.
- `JsonReader` only when performing round-trip tests; direct output assertions do not need it.

## `JsonTreeWriter`
Required:
- No constructor arguments.
- `JsonElement`, `JsonObject`, `JsonArray`, `JsonPrimitive`, and `JsonNull` are required for assertions.

No external I/O object is needed because it creates an in-memory JSON tree.

## `TypeAdapters`
Required according to adapter under test:
- A `JsonWriter` and typically `StringWriter` for write tests.
- A `JsonReader`, normally created from `new StringReader(json)`, for read tests.
- `Gson` for tests of `TIMESTAMP_FACTORY` and general `TypeAdapterFactory.create`.
- `TypeToken<T>` for factory creation tests.
- Domain/JDK values appropriate to each adapter: `BitSet`, `AtomicInteger`, `Calendar`, `Locale`, `UUID`, etc.

For the bug-specific adapter test:
- `TypeAdapters.BOOLEAN`
- `JsonWriter` with `StringWriter`
- optionally `JsonTreeWriter` to cover the modified in-memory writer path.

---

# 6. JUnit version and build tool

- **JUnit:** 3.8.2, supplied explicitly.
  - No JUnit 4 annotations should be assumed.
  - Generated tests should use JUnit 3 conventions unless project test sources prove a compatible custom arrangement.
- **Build tool:** Maven, supplied explicitly.

The actual Maven module layout, source/test directories, compiler source level, Surefire configuration, and Gson dependency version are not included.

---

# 7. Available test oracle

## Strongest available oracles

1. **Bug report metadata**
   - The triggering test is named `JsonWriterTest::testBoxedBooleans`.
   - The observed failure is `NullPointerException`.
   - Therefore, behavior involving boxed Boolean values, particularly a null boxed Boolean, is the core regression target.

2. **Source-level method contracts and Javadocs**
   - `JsonWriter.value(String)` explicitly accepts null and emits JSON null.
   - `JsonWriter.value(Number)` explicitly accepts null and emits JSON null.
   - `JsonWriter.nullValue()` explicitly emits JSON `null`.
   - `JsonWriter` states malformed operation sequences throw `IllegalStateException`.
   - `JsonTreeWriter` implementation provides observable tree semantics for arrays, objects, null handling, and nesting.

3. **Directly observable output/tree state**
   - Serialized text in a `StringWriter`.
   - `JsonElement` trees from `JsonTreeWriter.get()`.
   - Explicit exception classes and, where stable from source, messages.

4. **Implemented adapter behavior**
   - The supplied `TypeAdapters.BOOLEAN` code defines read/write behavior for ordinary boolean, string, and null JSON tokens.

## Insufficient or absent oracle information

The prompt does **not** include:
- the actual body of `JsonWriterTest.testBoxedBooleans`;
- the fixed revision diff or fixed source;
- an explicit API specification for a `value(Boolean)` overload;
- Maven `pom.xml` and test layout;
- existing test utility classes or assertion conventions;
- a formal contract for `JsonTreeWriter.jsonValue`;
- a clarified intended result for strict-mode top-level primitives or lenient `value(double)` with non-finite numbers, where the Javadoc and implementation are not fully aligned.

Therefore, expected outcomes in those ambiguous areas should not be asserted solely as “correct” without relying on existing project tests or the specific bug-fix contract.

---

# 8. Bug-report behaviors that should be tested

The modified files and trigger identify the following regression requirements.

## A. `JsonWriter` must support boxed Boolean values without null unboxing failure

A test should establish the intended behavior for:
- `Boolean.TRUE` → `true`
- `Boolean.FALSE` → `false`
- `null` boxed Boolean → `null`

The buggy source currently has only:

```java
public JsonWriter value(boolean value)
```

Calling it with a nullable `Boolean` causes implicit unboxing. If the value is null, Java throws `NullPointerException` before entering `value(boolean)`.

A fixed implementation is expected to avoid that behavior, normally by providing a nullable boxed overload such as `value(Boolean)` which delegates to `nullValue()` for null and `value(boolean)` otherwise. This exact method is not present in the supplied source and must not be added to production code during testing.

## B. `JsonTreeWriter` must have equivalent boxed-Boolean/null behavior

Because `JsonTreeWriter` is separately listed as modified, tests should cover its corresponding behavior:
- boxed `true` creates a boolean `JsonPrimitive`;
- boxed `false` creates a boolean `JsonPrimitive`;
- boxed null creates `JsonNull.INSTANCE`;
- behavior respects `setSerializeNulls(false)` for object properties;
- array null entries remain present.

## C. `TypeAdapters.BOOLEAN` integration must not regress

Tests should cover boolean serialization through the public adapter:
- `TypeAdapters.BOOLEAN.write(writer, Boolean.TRUE)`
- `TypeAdapters.BOOLEAN.write(writer, Boolean.FALSE)`
- `TypeAdapters.BOOLEAN.write(writer, null)`

Although the shown adapter guards null before calling `out.value(value)`, the source change list includes `TypeAdapters`; this means the final test design should include adapter-level integration rather than only direct writer calls.

## Limitation

The direct expression `writer.value((Boolean) null)` can compile against the buggy source by selecting `value(boolean)` through unboxing, but it will fail at runtime. A test designed specifically to require a new `value(Boolean)` overload may fail to compile only if it depends on overload-resolution details unavailable in this source. The exact fixed public signature cannot be proven from the supplied buggy source alone; it is inferred from the trigger name, NPE, and three modified files.

---

# 9. Missing context required for fully reliable, compilable, meaningful tests

The following information is absent:

1. **The body of `JsonWriterTest.testBoxedBooleans`.**  
   This is the most important missing item for reproducing the exact regression scenario and assertions.

2. **The fixed implementation or patch for revision `874e74...`.**  
   The prompt provides a fixed revision ID but not its source/diff. Without it, the exact intended API addition and delegation behavior cannot be confirmed from source alone.

3. **`pom.xml` / Maven module structure.**  
   Maven is identified, but test source location, artifact/module, Java compiler level, dependencies, and Surefire configuration are not supplied.

4. **Existing test infrastructure.**  
   There is no information about package placement, existing base test classes, helper methods, naming style, or how tests are selected.

5. **Full API contract for boxed `Boolean` writer calls.**  
   The bug report strongly implies boxed-null support is intended, but the supplied `JsonWriter` Javadoc documents primitive `boolean`, not a boxed overload.

6. **Expected semantics for implementation/Javadoc conflicts.**  
   In particular:
   - `JsonWriter.value(double)` rejects non-finite values even in lenient mode, contrary to the class-level leniency documentation.
   - strict top-level primitive behavior is not fully clear from the actual state implementation versus class documentation.

7. **Whether tests are intended to run against the buggy source, fixed source, or both under an external Defects4J protocol.**  
   The prompt identifies the buggy source version and prohibits requesting another version unless permitted. A regression test may correctly fail against this source; however, confirmation that it passes on the fixed revision cannot be obtained from the supplied material alone.

No production changes should be made. The bug-specific tests can be designed as regression tests expected to fail on `Gson-9b` and pass after the intended fix, but exact fixed-version API verification is not possible from the provided source alone.