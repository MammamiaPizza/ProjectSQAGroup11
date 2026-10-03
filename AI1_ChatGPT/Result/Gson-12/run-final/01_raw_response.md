## 1. Public methods in `JsonTreeReader` to test

### Constructor
- `JsonTreeReader(JsonElement element)`

### Overridden public reader operations
- `beginArray()`
- `endArray()`
- `beginObject()`
- `endObject()`
- `hasNext()`
- `peek()`
- `nextName()`
- `nextString()`
- `nextBoolean()`
- `nextNull()`
- `nextDouble()`
- `nextLong()`
- `nextInt()`
- `close()`
- `skipValue()`
- `getPath()`
- `toString()`

### Additional public method
- `promoteNameToValue()`

### Relevant inherited API
The class extends `com.google.gson.stream.JsonReader`. The supplied class explicitly relies on inherited methods including:
- `isLenient()` — used by `nextDouble()`
- likely `setLenient(boolean)` — needed to establish strict versus lenient behavior for NaN/infinite doubles.

The source for `JsonReader` is not supplied, so inherited behavior beyond what is directly used here cannot be fully analyzed.

---

## 2. Input types and valid input ranges

### Constructor input
`JsonTreeReader` accepts one `JsonElement`.

The source identifies these meaningful runtime element subtypes:

| Element type | Reader token / expected use |
|---|---|
| `JsonObject` | `BEGIN_OBJECT`, object traversal |
| `JsonArray` | `BEGIN_ARRAY`, array traversal |
| `JsonPrimitive` containing a string | `STRING` |
| `JsonPrimitive` containing a boolean | `BOOLEAN` |
| `JsonPrimitive` containing a number | `NUMBER` |
| `JsonNull` | `NULL` |

A Java `null` constructor argument is not explicitly rejected. It is pushed onto the stack, but `peek()` has no branch for Java `null`; it eventually throws `AssertionError`. Therefore, Java `null` is not a meaningful valid input according to this implementation.

### Structural range
- Empty objects and empty arrays.
- Non-empty objects and arrays.
- Nested arrays and objects.
- Nesting depth below and above 32 levels, because `push` expands internal arrays once `stackSize == stack.length`.
- Object member names are cast to `String`; `JsonObject` entries are expected to have `String` keys.
- Arrays may contain all supported `JsonElement` types.

### Scalar ranges
The exact conversion behavior belongs to `JsonPrimitive`, whose source is not provided.

Based on this class:
- `nextString()` accepts tokens `STRING` and `NUMBER`.
- `nextBoolean()` accepts only `BOOLEAN`.
- `nextNull()` accepts only `NULL`.
- `nextDouble()`, `nextLong()`, and `nextInt()` accept `NUMBER` and `STRING`.
- `nextDouble()` rejects `NaN`, positive infinity, and negative infinity when the reader is not lenient.
- Numeric overflow, fractional conversion, malformed numeric strings, and conversion-specific exceptions cannot be fully specified without the `JsonPrimitive` implementation.

---

## 3. Reachable conditions and branches

### `peek()`
Reachable branches include:

1. `stackSize == 0`
   - Returns `JsonToken.END_DOCUMENT`.

2. Stack top is an `Iterator`
   - Parent stack item is a `JsonObject` and iterator has another entry:
     - Returns `JsonToken.NAME`.
   - Parent stack item is a `JsonArray` and iterator has another element:
     - Pushes the next element and recursively determines its token.
   - Iterator is exhausted and parent is an object:
     - Returns `JsonToken.END_OBJECT`.
   - Iterator is exhausted and parent is an array:
     - Returns `JsonToken.END_ARRAY`.

3. Stack top is a `JsonObject`
   - Returns `BEGIN_OBJECT`.

4. Stack top is a `JsonArray`
   - Returns `BEGIN_ARRAY`.

5. Stack top is a `JsonPrimitive`
   - String primitive: `STRING`
   - Boolean primitive: `BOOLEAN`
   - Number primitive: `NUMBER`
   - Primitive which is none of these: `AssertionError`

6. Stack top is `JsonNull`
   - Returns `NULL`.

7. Stack top is `SENTINEL_CLOSED`
   - Throws `IllegalStateException("JsonReader is closed")`.

8. Unexpected stack content, including Java `null`
   - Throws `AssertionError`.

### Begin/end container methods
- `beginArray()` succeeds only when `peek()` is `BEGIN_ARRAY`.
- `endArray()` succeeds only when `peek()` is `END_ARRAY`.
- `beginObject()` succeeds only when `peek()` is `BEGIN_OBJECT`.
- `endObject()` succeeds only when `peek()` is `END_OBJECT`.
- Wrong token state reaches `expect(...)` and throws `IllegalStateException`.

### `hasNext()`
- Returns `false` only when `peek()` is `END_OBJECT` or `END_ARRAY`.
- Returns `true` for all other tokens, including scalar values and `END_DOCUMENT`.

### Scalar methods
- `nextName()` succeeds only at `NAME`.
- `nextString()` succeeds at `STRING` or `NUMBER`.
- `nextBoolean()` succeeds only at `BOOLEAN`.
- `nextNull()` succeeds only at `NULL`.
- `nextDouble()`, `nextLong()`, and `nextInt()` succeed at `NUMBER` or `STRING`.
- Each successful scalar consumption increments the enclosing path index if there is an enclosing stack element.

### `nextDouble()`
- Strict reader plus `NaN`/infinite result:
  - Throws `NumberFormatException`.
- Lenient reader plus `NaN`/infinite result:
  - The value is accepted, provided `JsonPrimitive.getAsDouble()` yields it.
- Normal finite number:
  - Returned and popped.

### `skipValue()`
There are two branches:

1. `peek() == JsonToken.NAME`
   - Calls `nextName()`.
   - Updates a path-name entry to `"null"`.
   - Increments a path index entry.

2. All other tokens
   - Pops one stack element.
   - Updates `pathNames[stackSize - 1]` to `"null"`.
   - Increments `pathIndices[stackSize - 1]`.

The second branch is defective when the skipped value is the root element. After `popStack()`, `stackSize` becomes `0`; then `pathNames[stackSize - 1]` and `pathIndices[stackSize - 1]` access index `-1`.

This is the direct cause of the reported `ArrayIndexOutOfBoundsException: -1`.

### `close()`
- Replaces the stack with a one-element stack containing `SENTINEL_CLOSED`.
- Subsequent `peek()` should throw `IllegalStateException`.
- Repeated `close()` calls appear harmless based on the supplied implementation.

### `promoteNameToValue()`
- Valid only when the next token is `NAME`.
- Converts the current object member into:
  1. its value, followed by
  2. a `JsonPrimitive` containing the member name.
- Any non-`NAME` state throws `IllegalStateException` through `expect(JsonToken.NAME)`.

### Stack expansion
`push(...)` has a distinct boundary:
- No resize before capacity is reached.
- Resize occurs when `stackSize == stack.length`, initially at depth/capacity 32.
- The `stack`, `pathIndices`, and `pathNames` arrays are all doubled.

### `getPath()`
Path construction branches depend on stack entries:
- Array plus paired iterator: appends `[index]`.
- Object plus paired iterator: appends `.` and current property name when non-null.
- Other stack items do not directly append path segments.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

### Normal cases
- Read a root string, boolean, number, or null.
- Traverse an empty array/object.
- Traverse a populated array/object.
- Read nested object/array structures.
- Read strings and numeric values using `nextString()`.
- Read numeric string primitives using `nextInt()`, `nextLong()`, and `nextDouble()`.
- Verify `peek()`, `hasNext()`, and `getPath()` through normal traversal.
- Verify `toString()` returns the simple class name, `"JsonTreeReader"`.

### Boundary cases
- Empty root `JsonObject`.
- Filled root `JsonObject`.
- Empty root `JsonArray`.
- Filled root `JsonArray`.
- Root scalar values.
- Deep nesting around internal capacity 32, and beyond it, to execute resizing.
- Integer/long/double values at or near their type boundaries, subject to `JsonPrimitive` conversion semantics.
- Object and array completion: immediately before and after `endObject()` / `endArray()`.
- Reader state immediately after consuming the final root value, where `peek()` should reach `END_DOCUMENT`.

### Invalid state cases
- Calling `beginArray()` for a non-array token.
- Calling `beginObject()` for a non-object token.
- Calling end methods before the relevant iterator is exhausted.
- Calling end methods for the wrong container type.
- Calling `nextName()` outside object-name position.
- Calling scalar methods for incompatible token types.
- Calling `promoteNameToValue()` when the next token is not `NAME`.
- Calling reading methods after `close()`.

### Null cases
Two different cases must be distinguished:

1. **JSON null (`JsonNull`)**
   - Valid JSON input.
   - `peek()` should return `NULL`.
   - `nextNull()` should consume it.
   - Other incompatible scalar methods should fail through token checks.

2. **Java `null` passed to the constructor**
   - Not guarded by the constructor.
   - `peek()` reaches the final `AssertionError` branch.
   - No documented contract in the supplied material says this input must be supported.

### Exceptional cases
Known from the supplied class:
- `IllegalStateException`
  - Wrong expected token.
  - Calling `peek()` after `close()`.
- `NumberFormatException`
  - Strict-mode `nextDouble()` on NaN/infinite values.
  - Possibly malformed conversions from `JsonPrimitive`, but that depends on unavailable `JsonPrimitive` behavior.
- `AssertionError`
  - Java `null` or another unexpected stack object.
  - A `JsonPrimitive` which is neither string, boolean, nor number.
  - The unreadable reader is accessed; this should normally not occur through `JsonTreeReader`.
- **Current bug:** `ArrayIndexOutOfBoundsException: -1`
  - Root-level `skipValue()` for a token other than `NAME`, including root objects reported by the bug report.

All public operations declare `IOException` due to the parent API, but the supplied implementation does not perform ordinary I/O. The unreadable `Reader` throws `AssertionError` if used.

---

## 5. Required constructors, dependencies, and external objects

### Constructor required for the target
```java
new JsonTreeReader(JsonElement element)
```

### Required Gson objects
The target source directly depends on:
- `com.google.gson.JsonElement`
- `com.google.gson.JsonObject`
- `com.google.gson.JsonArray`
- `com.google.gson.JsonPrimitive`
- `com.google.gson.JsonNull`
- `com.google.gson.stream.JsonReader`
- `com.google.gson.stream.JsonToken`

### Java dependencies
- `java.io.Reader`
- `java.io.IOException`
- `java.util.Iterator`
- `java.util.Map`

### Test-fixture needs
Meaningful tests need construction of:
- an empty and populated `JsonObject`;
- an empty and populated `JsonArray`;
- string, boolean, and numeric `JsonPrimitive` values;
- a `JsonNull` value;
- nested structures.

However, source/API definitions for `JsonObject`, `JsonArray`, `JsonPrimitive`, and `JsonNull` are not supplied. Their exact construction and mutation APIs therefore cannot be confirmed solely from this prompt. In particular, the availability and signatures of constructors and methods such as object-member insertion or array-element insertion are not established by the supplied source.

---

## 6. JUnit version and build tool

Supplied project configuration states:
- **JUnit version:** `junit-4.12.jar`
- **Build tool:** Maven

No Maven `pom.xml`, Surefire configuration, source roots, Java language level, or dependency declarations are supplied. Therefore, exact test compilation configuration cannot be independently confirmed beyond the stated JUnit version and Maven build tool.

---

## 7. Available test oracle

### Explicit bug-report oracle
The supplied bug information is the strongest available oracle:

- Bug ID: Gson-12
- GitHub issue: 1013
- Triggering tests:
  - `JsonTreeReaderTest::testSkipValue_filledJsonObject`
  - `JsonTreeReaderTest::testSkipValue_emptyJsonObject`
- Observed failure:
  - `java.lang.ArrayIndexOutOfBoundsException: -1`
- Only modified production source:
  - `JsonTreeReader`

This reliably establishes that calling `skipValue()` for both an empty and a populated JSON object was intended not to fail with `ArrayIndexOutOfBoundsException: -1`.

### Source-derived oracle
The following expected behavior is directly derivable from the implementation structure:
- `peek()` token classification.
- Token-mismatch operations throw `IllegalStateException`.
- `close()` causes subsequent `peek()` to throw `IllegalStateException`.
- `toString()` returns `getClass().getSimpleName()`, which is `"JsonTreeReader"`.
- Strict `nextDouble()` rejects NaN and infinities.
- Root consumption normally leaves `stackSize == 0`, for which `peek()` returns `END_DOCUMENT`.

### Missing oracle material
Not supplied:
- The content of the listed triggering tests.
- The fixed revision source or patch.
- API documentation for `JsonTreeReader` or `JsonReader`.
- Existing project tests.
- The GitHub issue body, expected behavior discussion, or reproduction details.
- The source/API of `JsonElement`, `JsonObject`, `JsonArray`, `JsonPrimitive`, `JsonNull`, and `JsonReader`.

Accordingly, detailed expectations for some behaviors—especially numeric conversion semantics, exact path strings after every operation, and the intended full semantics of `skipValue()` when invoked at `NAME`—cannot be treated as fully specified by the supplied material.

---

## 8. Bug-report-specific behaviors to test

The directly reported regression coverage should include:

1. **`skipValue()` on an empty root `JsonObject`**
   - Must not throw `ArrayIndexOutOfBoundsException`.
   - The failure reported is index `-1`.

2. **`skipValue()` on a populated root `JsonObject`**
   - Must not throw `ArrayIndexOutOfBoundsException`.
   - The populated-object case is explicitly listed as a triggering test.

3. **Post-skip state for root objects**
   - The implementation’s stack model indicates that skipping a root object pops the root element; therefore `peek()` would be expected to reach `END_DOCUMENT` once the defect is corrected.
   - This is a source-derived inference, not an explicit statement from the supplied bug report.

4. **Other root-level non-`NAME` tokens**
   - The same faulty `else` branch is reached for root arrays, strings, numbers, booleans, and JSON null.
   - Thus root-level `skipValue()` for these values is plausibly affected by the same defect.
   - The bug report only explicitly identifies empty and filled JSON objects, so object cases are mandatory regression coverage; other root token cases are logical extension coverage rather than explicitly reported requirements.

5. **Nested/non-root `skipValue()` behavior**
   - The unsafe index access does not occur when there is an enclosing stack item after the value is popped.
   - Tests may distinguish root and nested contexts to ensure a fix does not alter normal nested behavior.
   - Precise expected semantics for `skipValue()` at an object `NAME` token are not fully documented in the supplied material and should not be over-specified without an existing test or API contract.

---

## 9. Missing context needed for fully reliable, compilable, and meaningful tests

The following information is missing:

1. **Existing `JsonTreeReaderTest` source**
   - Especially the two named triggering tests.
   - Their assertions would define the intended post-`skipValue()` state and expected path behavior.

2. **Bug report text or fixed patch**
   - The supplied report gives only summary metadata and failure locations.
   - It does not explicitly state the intended corrected behavior beyond avoiding the crash.

3. **Source/API of Gson model classes**
   - `JsonObject`, `JsonArray`, `JsonPrimitive`, `JsonNull`, and `JsonElement`.
   - Required to verify how test data can be constructed using only available project APIs.

4. **Source/API of `JsonReader`**
   - Needed to confirm inherited method availability and behavior, particularly:
     - leniency configuration;
     - documented `skipValue()` contract;
     - path semantics;
     - behavior after closure.

5. **Maven project configuration**
   - `pom.xml`, source/test layout, compiler version, and test plugin setup.
   - Needed to guarantee compilable test placement and imports.

6. **Formal contract for numeric conversions**
   - Exact behavior for malformed numeric strings, overflow, non-integral values passed to `nextInt()`/`nextLong()`, and conversion exception types comes from `JsonPrimitive`, not this supplied class.

The reported root-object regression can be meaningfully targeted from the supplied information. However, tests asserting exact semantics beyond “does not throw the reported `ArrayIndexOutOfBoundsException`” and the source-inferred terminal reader state would require the missing oracle material above.