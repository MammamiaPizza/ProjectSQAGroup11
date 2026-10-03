## 1. Public API surface that should be tested

`JsonReader` has one public constructor and the following public methods:

| Member | Inputs | Primary observable behavior |
|---|---|---|
| `JsonReader(Reader in)` | `Reader` | Creates a reader over a character source; rejects `null`. |
| `setLenient(boolean)` | `boolean` | Enables/disables acceptance of non-standard JSON syntax. |
| `isLenient()` | none | Returns the configured leniency flag. |
| `beginArray()` | none | Consumes a `[` token and enters array scope. |
| `endArray()` | none | Consumes a `]` token and leaves array scope. |
| `beginObject()` | none | Consumes a `{` token and enters object scope. |
| `endObject()` | none | Consumes a `}` token and leaves object scope. |
| `hasNext()` | none | Indicates whether the current array/object has another member/element. |
| `peek()` | none | Reports the next `JsonToken` without consuming it. |
| `nextName()` | none | Reads an object member name. |
| `nextString()` | none | Reads a string, or returns the textual representation of a numeric token. |
| `nextBoolean()` | none | Reads `true` or `false`. |
| `nextNull()` | none | Consumes `null`. |
| `nextDouble()` | none | Reads a numeric token/string as a `double`. |
| `nextLong()` | none | Reads a numeric token/string as an exactly representable `long`. |
| `nextInt()` | none | Reads a numeric token/string as an exactly representable `int`. |
| `close()` | none | Closes the underlying `Reader` and transitions this reader to closed state. |
| `skipValue()` | none | Recursively skips the next value, including nested arrays/objects. |
| `getPath()` | none | Returns the current JSONPath-like location. |
| `toString()` | none | Returns class name plus location information. |

Also relevant but **not public API**:

- `doPeek()` is package-private and drives token classification; it is exercised indirectly through all token-reading methods.
- `JsonReaderInternalAccess.INSTANCE.promoteNameToValue(...)` is configured by the static initializer, but testing it directly would require internal-package context and is outside the public API.

---

## 2. Input types and valid input ranges

### Constructor input

- `Reader in`
  - Valid: any non-null `java.io.Reader`, such as `StringReader`, file readers, buffered readers, or a custom reader.
  - Invalid: `null`, explicitly rejected with `NullPointerException("in == null")`.
  - The underlying reader may throw `IOException`; these failures can propagate during parsing or closing.

### JSON stream input

The reader accepts a character stream representing JSON. The valid forms depend on strict vs. lenient mode.

#### Strict mode, default

The supplied documentation says strict mode requires:

- Exactly one top-level JSON value.
- Top-level value must be an object or array.
- Standard JSON strings using double quotes.
- Standard JSON object names using double quotes.
- Standard `:` separators and `,` element/member separators.
- Standard literals: `true`, `false`, and `null` (the source also accepts uppercase spellings such as `TRUE`, `FALSE`, and `NULL` due to `peekKeyword()`).
- Numeric forms accepted by `peekNumber()`:
  - Integer: `0`, `-0`, `123`, `-123`
  - Fraction: `1.0`, `-1.25`
  - Exponent: `1e2`, `1E2`, `1e-2`, `1e+2`
  - Boundary integer values including `Long.MIN_VALUE` and `Long.MAX_VALUE`, subject to the requested accessor.
- A leading byte order mark (`\ufeff`) is explicitly handled on the first buffer fill.

Malformed forms should fail, generally with `MalformedJsonException`, `EOFException`, `IllegalStateException`, or numeric parsing exceptions depending on where and how they are consumed.

#### Lenient mode

When `setLenient(true)` is used before parsing, the documented additional accepted syntax includes:

- Non-execute prefix `")]}'\n"`.
- Multiple top-level values.
- Any top-level value type.
- `NaN`, infinities, and values which `Double.parseDouble` accepts, when read using `nextDouble()`.
- `//`, `#`, and `/* ... */` comments.
- Unquoted names and values.
- Single-quoted names and values.
- Semicolon separators.
- Missing array elements, interpreted as `null`.
- `=` and `=>` name/value separators.

### Numeric accessor ranges

| Accessor | Exact accepted range/condition |
|---|---|
| `nextInt()` | Must be exactly representable as Java `int`: `-2,147,483,648` through `2,147,483,647`. Numeric strings are also attempted. Fractional values only succeed if conversion produces exactly the same numeric value, e.g. `"1.0"` can succeed as `1`; `"1.5"` must fail. |
| `nextLong()` | Must be exactly representable as Java `long`: `-9,223,372,036,854,775,808` through `9,223,372,036,854,775,807`. Numeric strings are also attempted. Fractional/exponent values only succeed if exactly integral after conversion. |
| `nextDouble()` | Inputs parseable by `Double.parseDouble`, including numeric strings. In strict mode, `NaN`, positive infinity, and negative infinity must be rejected after parsing. |
| `nextString()` | Quoted and unquoted string values, and numeric values converted/preserved in textual form. The Gson-13 defect specifically concerns preservation of the input text `"-0"`. |

---

## 3. Conditions and reachable branches

The class is a stateful streaming parser. Its main branch dimensions are:

### A. Parser state / token position

The reader tracks document, array, and object scopes. Public methods need tests for valid and invalid transitions:

- Empty document.
- Non-empty document.
- Empty and non-empty array.
- Empty and non-empty object.
- Object after a property name, awaiting a value.
- Closed state.
- Nested arrays/objects.
- Stack growth beyond the initial stack capacity of 32 nested scopes.

### B. Token categories

`peek()` can report:

- `BEGIN_OBJECT`
- `END_OBJECT`
- `BEGIN_ARRAY`
- `END_ARRAY`
- `NAME`
- `BOOLEAN`
- `NULL`
- `STRING`
- `NUMBER`
- `END_DOCUMENT`

Each token category should be consumed by the matching method and rejected by incompatible methods.

### C. Quoting/value representation

Reachable value representations include:

- Double-quoted strings.
- Single-quoted strings in lenient mode.
- Unquoted strings in lenient mode.
- Buffered numeric strings after a numeric conversion attempt.
- Numeric long tokens.
- Numeric non-long tokens, including decimal/exponent forms.
- Boolean and null keywords.
- Quoted, unquoted, and single-quoted object names.

### D. Strict versus lenient parsing

Many branches are gated by `checkLenient()`. Relevant strict/lenient pairs include:

- Single quotes.
- Unquoted names/values.
- Comments.
- Semicolon separators.
- Missing array elements.
- Alternative object separators (`=`, `=>`).
- Multiple top-level values.
- Non-execute prefix.
- Non-finite doubles.

### E. Numeric parsing branches

`peekNumber()` has meaningful branches for:

- Positive and negative integral numbers.
- Zero and **negative zero**.
- Leading-zero numeric text, such as `01` or `-01`, which does not qualify as a normal JSON number and is subsequently treated as an unquoted literal only in lenient mode.
- Decimal forms.
- Exponent forms, including signed exponents.
- Invalid/incomplete forms: `-`, `.`, `1.`, `1e`, `1e+`, etc.
- Values that fit in `long`.
- Values too large for `long`.
- Very long potential numeric literals reaching the 1024-character buffer threshold.
- Number followed by a literal character, e.g. `123abc`, which is not recognized as a numeric token.
- `Long.MIN_VALUE`, which has special handling.
- Long-to-int overflow in `nextInt()`.
- Exact versus inexact conversion from a decimal/exponent value to `int` or `long`.

### F. String parsing and escapes

Relevant branches include:

- Empty strings.
- Strings entirely in one buffer.
- Strings spanning buffer fills.
- Valid two-character escapes: `\t`, `\b`, `\n`, `\r`, `\f`, `\'`, `\"`, `\\`, `\/`.
- Valid Unicode escapes, such as `\u0041`.
- Newline handling in quoted strings and escaped line continuations.
- Invalid escape characters.
- Invalid hexadecimal Unicode escapes.
- Unterminated escapes.
- Unterminated quoted strings.

### G. Whitespace/comments and source boundaries

- Whitespace: space, tab, carriage return, newline, form feed where applicable to token scanning.
- EOF before a required token.
- EOF after a complete top-level document.
- Reader data split over multiple `Reader.read(...)` operations.
- Comments, including unterminated block comments in lenient mode.
- Optional BOM.
- I/O failures from the wrapped reader.

### H. Skipping and location/path tracking

- `skipValue()` over primitive values.
- `skipValue()` over nested arrays and objects.
- Skipping object names and their values.
- Correct continuation after skipping.
- `getPath()` after entering arrays/objects, consuming values, reading names, skipping values, and closing nested scopes.
- `toString()` location rendering.
- Error locations contain line, column, and path information; exact messages are implementation details except where explicitly asserted by an established oracle.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

### Normal cases

- Read an empty array/object.
- Read a mixed nested document containing strings, numbers, booleans, nulls, arrays, and objects.
- Repeated `peek()` before consumption should return the same token.
- Consume values using their matching accessor.
- Read number tokens with `nextString()`.
- Read numeric strings with `nextInt()`, `nextLong()`, or `nextDouble()`.
- Read an object using `beginObject()`, `hasNext()`, `nextName()`, value accessors, and `endObject()`.
- Read an array using `beginArray()`, `hasNext()`, value accessors, and `endArray()`.
- Skip primitive and nested unknown values, then consume following values successfully.
- Close a reader and verify its wrapped `Reader` is closed.

### Boundary cases

- Empty input.
- Empty arrays and objects: `[]`, `{}`.
- One-element arrays/one-member objects.
- Deeply nested input, including depth greater than 32 to exercise internal stack resizing.
- Strings and literals near, at, and beyond the 1024-character buffer size.
- Values or delimiters split at a buffer boundary.
- First-character BOM.
- Number boundaries:
  - `0`
  - `-0`
  - `Long.MIN_VALUE`
  - `Long.MAX_VALUE`
  - `Integer.MIN_VALUE`
  - `Integer.MAX_VALUE`
  - values one outside those integer ranges
  - exact integral decimal/exponent values such as `1.0` or `1e3`
  - fractional non-integral values such as `1.1`
- Line/column tracking around newline and CRLF input.

### Invalid cases

- Mismatched structural calls:
  - Calling `beginArray()` when next token is an object.
  - Calling `endArray()` when the current token is not `]`.
  - Calling `nextName()` outside an object/member-name position.
  - Calling a scalar accessor on another token type.
- Missing required punctuation:
  - missing `,`
  - missing `:`
  - missing closing `]` or `}`
  - malformed object names
- Invalid strict-mode syntax:
  - comments
  - single quotes
  - unquoted names/values
  - semicolons
  - omitted array values
  - multiple top-level values
- Invalid strings:
  - bad escape sequences
  - malformed `\u` escape
  - unterminated string
- Invalid numeric forms or non-exact numeric conversions.
- Invalid non-finite values in strict mode when consumed via `nextDouble()`.

### Null cases

- `new JsonReader(null)` must throw `NullPointerException`.
- JSON `null`:
  - `peek()` should report `JsonToken.NULL`.
  - `nextNull()` should consume it.
  - Calling scalar methods on it should fail with `IllegalStateException`.
  - `skipValue()` should consume it.
- There are no nullable parameters on public parsing methods other than the constructor’s `Reader`.

### Exceptional cases identifiable from the source

| Situation | Expected exception type |
|---|---|
| `JsonReader` constructed with `null` | `NullPointerException` |
| Unexpected token for structural/scalar accessor | `IllegalStateException` |
| Operation requiring parsing after `close()` | `IllegalStateException` via closed parser state |
| Required input missing at EOF | `EOFException` in applicable tokenization paths |
| JSON syntax violation | `MalformedJsonException` |
| Invalid escape sequence | `MalformedJsonException` |
| Unterminated string/comment/escape | `MalformedJsonException` |
| Invalid Unicode hexadecimal escape digits | `NumberFormatException` |
| Numeric token/string not parseable as requested type | `NumberFormatException` |
| Numeric value not exactly representable as requested `int`/`long` | `NumberFormatException` |
| Strict-mode `NaN`/infinity through `nextDouble()` | `MalformedJsonException` |
| Wrapped `Reader` fails during read/close | propagated `IOException` |

The exact exception message text should not generally be treated as a stable API contract unless an existing project test or bug report specifically establishes it as an oracle.

---

## 5. Required constructors, dependencies, and external objects

### Required production construction

```java
new JsonReader(Reader)
```

For isolated tests, `java.io.StringReader` is sufficient for most parser inputs.

### Useful external objects available from the supplied source context

- `java.io.Reader`
- `java.io.StringReader` — not shown in imports, but part of the JDK and appropriate for tests.
- Custom `Reader` implementations may be needed to test:
  - buffer-boundary behavior,
  - `IOException` propagation,
  - whether `close()` delegates to the underlying reader.
- `com.google.gson.stream.JsonToken` — needed for `peek()` assertions.
- `com.google.gson.stream.MalformedJsonException` — needed for syntax-error assertions.
- JUnit 4.12 assertion and exception facilities.

### Internal dependencies

The class references:

- `com.google.gson.internal.JsonReaderInternalAccess`
- `com.google.gson.internal.bind.JsonTreeReader`
- `JsonScope`
- `JsonToken`
- `MalformedJsonException`

No dependency injection, mocking framework, file system, network access, or clock is required for ordinary `JsonReader` tests.

The static initialization relating to `JsonReaderInternalAccess` and `JsonTreeReader` may be covered indirectly by higher-level Gson functionality, but the supplied bug and public API do not require direct tests of that internal integration.

---

## 6. JUnit version and build tool

Supplied project configuration states:

- **JUnit:** `junit-4.12.jar`
- **Build tool:** Maven
- **Project:** Gson
- **Source version:** `Gson-13b`

Therefore, eventual tests should use JUnit 4 conventions, such as:

- `org.junit.Test`
- `org.junit.Assert.*`
- `@Test(expected = SomeException.class)` or explicit `try/catch` assertions where post-exception state must also be inspected.

The actual `pom.xml`, Maven Surefire configuration, source/target Java version, test source directory, and dependency declarations were not supplied. Those are necessary to confirm the exact Maven command and compilation configuration, but not to identify the JUnit API version because it was explicitly provided.

---

## 7. Available test oracles

The supplied material provides these reliable or partially reliable oracles:

### A. Public Javadoc in `JsonReader`

The class and method documentation specifies:

- Streaming traversal behavior.
- Strict and lenient parsing rules.
- Token/accessor contracts.
- Numeric conversion rules.
- Exception categories for public accessors.
- Closing behavior.
- `getPath()` purpose.
- Non-execute prefix behavior.

This is the primary specification for broad tests.

### B. Source-level observable behavior

The supplied source can establish behavior for implementation-specific cases, including:

- Constructor null handling.
- Whether uppercase keywords are accepted.
- BOM handling.
- Which exception types arise on many malformed cases.
- Exact conversion logic in numeric readers.
- Path updates.
- State changes after `close()`.

However, source behavior should not be assumed correct where it conflicts with the documented API or known defect. In particular, the numeric negative-zero behavior is known to be defective in this source version.

### C. Bug report/triggering-test evidence

The supplied defect information gives a direct expected-result oracle:

```text
com.google.gson.stream.JsonReaderTest::testNegativeZero
expected:<[-]0> but was:<[]0>
```

This establishes that, for the triggering scenario, the expected string representation is `"-0"` and the buggy implementation returns `"0"`.

The likely relevant public operation is `nextString()` applied to JSON numeric input `-0`, because `nextString()` is explicitly documented to return a number’s string form. The source confirms that `-0` is classified as `PEEKED_LONG`, stored as numeric zero, and then rendered with `Long.toString(peekedLong)`, producing `"0"`.

### D. Existing test name only

The existing test class/method is named:

```text
com.google.gson.stream.JsonReaderTest::testNegativeZero
```

Its complete code was not supplied. Therefore, its exact input setup, exact assertion API, and any additional expected behavior cannot be used as a source-level oracle beyond the reported expected/actual comparison.

### E. Fixed revision metadata only

A fixed revision ID is supplied, but the fixed source/diff is not supplied and must not be inferred or requested under the stated protocol. It cannot be used as an oracle beyond confirming that the target class was the only modified source.

---

## 8. Behaviors related to Gson-13 that should be tested

The reported failure is specifically preservation of **negative zero lexical form** when the value is read as a string.

### Core regression behavior

A test should verify that parsing the JSON numeric literal:

```json
-0
```

and consuming it with `nextString()` returns:

```java
"-0"
```

rather than:

```java
"0"
```

For a complete valid strict-mode document, the input may need to be contained in an array or object, consistent with the class documentation’s strict top-level restriction. For example:

```json
[-0]
```

The relevant sequence would be:

1. Construct `JsonReader` with the JSON input.
2. Enter the array.
3. Assert that the next value can be read with `nextString()`.
4. Assert the returned value is exactly `"-0"`.
5. Complete the document correctly.

### Important neighboring cases

To avoid overfitting the regression test and to distinguish negative zero from ordinary zero, related tests should cover:

- `0` read through `nextString()` returns `"0"`.
- `-0` read through `nextString()` returns `"-0"`.
- `-0` read through `nextLong()` returns numeric `0L`; numeric conversion naturally cannot preserve a sign for integral zero.
- `-0` read through `nextInt()` returns numeric `0`.
- `-0` read through `nextDouble()` returns a negative-zero `double` if the parser’s conversion semantics preserve it; this can be checked with `Double.doubleToRawLongBits(...)` rather than ordinary equality, because `0.0 == -0.0`.
- Other negative integral numeric strings, such as `-1`, continue to preserve their textual representation through `nextString()`.
- Positive `0` is unaffected.

### Scope limitation

The supplied bug report establishes only the expected `"-0"` versus `"0"` string result. It does **not** establish requirements for alternative lexical forms such as `-0.0`, `-0e0`, or quoted `"-0"`, although tests for those may be justified separately by the general API contract. They should not be presented as direct Gson-13 regression requirements without an additional oracle.

---

## 9. Missing context required for fully reliable, compilable, and meaningful tests

The supplied information is enough to design a focused Gson-13 regression test and many public API unit tests. However, the following context is missing for a complete test suite with fully reliable expectations:

1. **The complete existing `JsonReaderTest` source**
   - Needed to avoid duplicating existing coverage.
   - Needed to match project conventions, helpers, package placement, resource usage, and intended edge-case behavior.
   - Needed to see the exact original `testNegativeZero` setup.

2. **The Maven `pom.xml` and project layout**
   - Needed to confirm test source root, Maven module structure, Java source level, Surefire plugin behavior, and dependency resolution.
   - The prompt states Maven and JUnit 4.12, but does not provide build configuration details.

3. **The source for related classes**
   - `JsonToken`, `JsonScope`, `MalformedJsonException`, `JsonReaderInternalAccess`, and `JsonTreeReader` are referenced but not supplied.
   - Their package names and usage are known, but their exact definitions and any project-specific behavior are unavailable.
   - Basic tests of `JsonReader` can still compile if those classes exist in the project as expected.

4. **Full bug report content**
   - Only the issue number, URL, summary metadata, and triggering assertion are supplied.
   - No explicit report narrative or acceptance criteria are available.
   - Therefore, the only defect-specific expected behavior that can be asserted with high confidence is preservation of `"-0"` in the triggering string-read scenario.

5. **Behavioral decision for undocumented edge cases**
   - Some behavior is implementation-driven rather than clearly documented, such as uppercase keyword acceptance and some malformed-input exception distinctions.
   - Tests should avoid locking in undocumented implementation details unless existing tests or an explicit specification establish them as required behavior.

6. **Exact target test scope**
   - The class is large and has extensive parsing behavior. The prompt does not state whether the eventual goal is:
     - only the Gson-13 regression test,
     - branch coverage for the modified class,
     - a minimal Defects4J-triggering test, or
     - a broad API conformance suite.
   - This affects how many tests are appropriate, but does not prevent creation of the focused regression test.

No production-source modification is required or appropriate for this analysis.