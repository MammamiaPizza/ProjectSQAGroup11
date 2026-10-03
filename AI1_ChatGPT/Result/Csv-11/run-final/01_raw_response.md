## Scope analyzed

Only the supplied source for `org.apache.commons.csv.CSVParser`, the supplied project metadata, and the supplied bug context were analyzed. No assumptions were made about unprovided implementations such as `CSVFormat`, `CSVRecord`, `Lexer`, `Assertions`, or existing test bodies.

---

## 1. Public methods and constructor that should be tested

### Static factory methods

1. `public static CSVParser parse(File file, Charset charset, CSVFormat format) throws IOException`
2. `public static CSVParser parse(String string, CSVFormat format) throws IOException`
3. `public static CSVParser parse(URL url, Charset charset, CSVFormat format) throws IOException`

### Constructor

4. `public CSVParser(Reader reader, CSVFormat format) throws IOException`

### Instance methods

5. `public void close() throws IOException`
6. `public long getCurrentLineNumber()`
7. `public Map<String, Integer> getHeaderMap()`
8. `public long getRecordNumber()`
9. `public List<CSVRecord> getRecords() throws IOException`
10. `public <T extends Collection<CSVRecord>> T getRecords(T records) throws IOException`
11. `public boolean isClosed()`
12. `public Iterator<CSVRecord> iterator()`

### Package-private method relevant to behavior

13. `CSVRecord nextRecord() throws IOException`

`nextRecord()` is not public, so a conventional external API test should exercise it through `iterator()`, `getRecords()`, or a test in package `org.apache.commons.csv`.

---

## 2. Input types and valid input ranges

### Parser input sources

| API | Input | Explicit validation in supplied source |
|---|---|---|
| `parse(File, Charset, CSVFormat)` | `File`, `Charset`, `CSVFormat` | `file` and `format` checked non-null; `charset` is not explicitly checked |
| `parse(String, CSVFormat)` | `String`, `CSVFormat` | Both explicitly checked non-null |
| `parse(URL, Charset, CSVFormat)` | `URL`, `Charset`, `CSVFormat` | All explicitly checked non-null |
| `CSVParser(Reader, CSVFormat)` | `Reader`, `CSVFormat` | Both explicitly checked non-null |

### CSV content

The parser accepts CSV data through a `String`, `Reader`, `File`, or `URL`. The exact valid grammar depends on the provided `CSVFormat`, which is not supplied. Based on this class alone, meaningful input categories include:

- Empty input.
- One or more ordinary records.
- Records with multiple fields.
- End-of-record sequences.
- End-of-file after a field/value.
- Comment records/tokens.
- Tokens representing invalid CSV syntax.
- Inputs that produce I/O failures.
- Inputs whose first record is used as a header, where the format requests auto-detected headers.
- Configured header arrays, including duplicate, empty, and `null` entries.

### Header values

`initializeHeader()` processes `String[] formatHeader` obtained from `CSVFormat.getHeader()`.

Reachable categories include:

- `formatHeader == null`: no header map is initialized.
- `formatHeader.length == 0`: header is read from the first CSV record.
- `formatHeader.length > 0`: configured header names are used.
- Header elements that are:
  - Non-empty strings.
  - Empty strings.
  - Duplicate strings.
  - Duplicate empty strings.
  - `null`.

The supplied implementation invokes `header.trim()` without checking whether `header` is `null`; therefore, a configured or parsed `null` header element reaches a `NullPointerException`.

---

## 3. Conditions and reachable branches

### Factory methods

#### `parse(File, Charset, CSVFormat)`

Branches/conditions:

- Non-null `file` and `format`: constructs an `InputStreamReader` over a `FileInputStream`.
- `file == null`: `Assertions.notNull(file, "file")` determines the exception; Javadoc states `IllegalArgumentException`.
- `format == null`: `Assertions.notNull(format, "format")` determines the exception.
- `charset == null`: no explicit `Assertions.notNull`; `InputStreamReader` behavior determines the exception, likely a `NullPointerException`, but this cannot be guaranteed solely from this source.
- Missing/unreadable file or file-opening failure: `IOException`.
- Parsing/initial header initialization failures: propagated `IOException` or possible `IllegalArgumentException`.

#### `parse(String, CSVFormat)`

Branches/conditions:

- Valid non-null string and format: creates a parser over `StringReader`.
- `string == null`: validation exception from `Assertions`.
- `format == null`: validation exception from `Assertions`.
- Header initialization can consume the first record or reject headers.

#### `parse(URL, Charset, CSVFormat)`

Branches/conditions:

- Valid non-null URL, charset, and format: opens URL stream and creates parser.
- Any required argument null: validation exception from `Assertions`.
- `url.openStream()` failure: `IOException`.
- Header initialization/parsing failure: propagated exception.

### Constructor and header initialization

#### Header absent: `format.getHeader() == null`

- `headerMap` is set to `null`.
- `getHeaderMap()` returns `null`.
- No input is consumed specifically for a header.

#### Header read from input: `format.getHeader().length == 0`

- `nextRecord()` is called during construction.
- If a record exists, its values become `headerRecord`.
- If input is empty, `headerRecord` remains `null`, but an empty `LinkedHashMap` is returned as `headerMap`.
- The record consumed as the header is not subsequently emitted as a normal record.
- Duplicate and empty-header validation is applied to the values of the header record.
- A `null` value in the detected header record would reach `header.trim()` and fail with `NullPointerException`.

#### Configured explicit header: non-empty `format.getHeader()`

- If `format.getSkipHeaderRecord()` is `true`, one record is consumed and discarded.
- If `false`, input starts being parsed normally after construction.
- Configured header values become map keys and their positions become values.
- Duplicate header names are rejected except for the specific empty-header/ignore-empty-header case.
- A `null` configured header element reaches `header.trim()` and causes `NullPointerException` in this version.

#### Duplicate and empty-header branch

The condition is:

```java
if (containsHeader && (!emptyHeader ||
        (emptyHeader && !format.getIgnoreEmptyHeaders()))) {
    throw new IllegalArgumentException(...);
}
```

Reachable outcomes:

- Duplicate non-empty name: `IllegalArgumentException`.
- Duplicate empty name and `ignoreEmptyHeaders == false`: `IllegalArgumentException`.
- Duplicate empty name and `ignoreEmptyHeaders == true`: no exception; subsequent `put` replaces the prior empty-name mapping with the later index.
- First occurrence of any non-null header: inserted in the map.
- Null header: `NullPointerException` before these conditions can complete.

### Record parsing: `nextRecord()`

Token branches:

- `TOKEN`: adds current token value and continues parsing record.
- `EORECORD`: adds current value and finishes the record.
- `EOF`:
  - `isReady == true`: adds final token value.
  - `isReady == false`: does not add a value.
- `INVALID`: throws `IOException` containing the current line number.
- `COMMENT`:
  - Accumulates comment content.
  - Multiple comment tokens are joined using `Constants.LF`.
  - Continues to seek the next token for the same eventual record.
- Unexpected token type/default: `IllegalStateException`.

Post-token-loop branches:

- Non-empty `record`:
  - Increments `recordNumber`.
  - Produces a `CSVRecord`, with accumulated comment if any.
- Empty `record`:
  - Returns `null`, representing no next record / EOF.

### Record null-string conversion: `addRecordValue()`

- `format.getNullString() == null`: input token content is stored as-is.
- Non-null null-string:
  - Token content equal to null-string ignoring case becomes Java `null`.
  - Other token values are retained unchanged.

### `getHeaderMap()`

- `headerMap == null`: returns `null`.
- Non-null map: returns a new `LinkedHashMap`, not the internal map.

### `getRecords()`

- No remaining records: returns an empty `List`.
- One or more remaining records: returns them in parser order.
- Starts at the parser’s current position, including after a previous iteration or `nextRecord()` invocation.
- IOException from parsing is propagated.

### `getRecords(T records)`

- Adds all remaining records to the supplied collection.
- Returns the exact same collection object.
- No explicit null validation: `records == null` will cause a `NullPointerException` when a parsed record is added; if there are no records, it may return `null` without dereferencing it. This behavior is reachable from the implementation but is not documented as an API contract.
- Collection implementation may throw its own exception from `add`.

### Iterator behavior

#### `hasNext()`

- Closed parser: returns `false`.
- Current prefetched record exists: returns `true`.
- No prefetched record: invokes `nextRecord()`.
- End of input: returns `false`.
- IOException: wraps it in `RuntimeException`.

#### `next()`

- Closed parser: throws `NoSuchElementException("CSVParser has been closed")`.
- Prefetched record available: returns it.
- No prefetched record but another record available: returns it.
- No more records: throws `NoSuchElementException("No more CSV records available")`.
- IOException: wrapped in `RuntimeException` through `getNextRecord()`.

#### `remove()`

- Always throws `UnsupportedOperationException`.

### `close()` and `isClosed()`

- `close()` delegates to `lexer.close()`.
- The `lexer != null` condition is always true after successful construction, but is a reachable defensive branch only if object initialization behavior were unusual; ordinary tests need not target it separately.
- `isClosed()` delegates to `lexer.isClosed()`.
- Exact idempotence and post-close read behavior depend partly on `Lexer`, which was not supplied. Iterator-specific closed behavior is explicit and testable.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

### Normal cases

- Parse a valid single-record input.
- Parse several records.
- Parse fields separated/configured according to a known `CSVFormat`.
- Use each factory method with valid arguments.
- Use constructor with `StringReader` and valid format.
- Retrieve records through:
  - `getRecords()`
  - `getRecords(existingCollection)`
  - enhanced `for` loop / `iterator()`
  - iterator `hasNext()` followed by `next()`
  - iterator `next()` without a preceding `hasNext()`
- Verify `recordNumber` advances only for non-empty records returned by `nextRecord()`.
- Verify a configured header map maps names to zero-based positions.
- Verify a parser with no configured header returns `null` from `getHeaderMap()`.
- Verify `getHeaderMap()` returns a copy.

### Boundary cases

- Empty input.
- Input containing only a header row when headers are auto-detected.
- Input ending immediately after a field, without an explicit final record separator.
- Input ending at a record separator.
- One-column records.
- Header array of length zero, causing header detection from input.
- Header array containing one entry.
- Duplicate empty headers with `ignoreEmptyHeaders` both enabled and disabled.
- Calling `getRecords()` after records have already been consumed.
- Calling `hasNext()` repeatedly before `next()`.
- Calling `getCurrentLineNumber()` around record reads, especially for input with line breaks. Exact expected line-number values should be derived from existing tests or `Lexer` behavior, which is unavailable.

### Invalid cases

- Duplicate non-empty header names: documented implementation behavior is `IllegalArgumentException`.
- Duplicate empty names where empty headers are not ignored: `IllegalArgumentException`.
- Invalid lexer token sequence: `IOException`.
- `iterator().remove()`: `UnsupportedOperationException`.
- `iterator().next()` after all data has been consumed: `NoSuchElementException`.
- `iterator().next()` after closing parser: `NoSuchElementException`.

### Null cases

- `parse(String, format)` with null string.
- `parse(String, null)`.
- `parse(File, charset, format)` with null file.
- `parse(File, charset, null)`.
- `parse(URL, charset, format)` with null URL, charset, or format.
- Constructor with null reader or format.
- File-parser null charset: implementation does not explicitly validate it; resulting exception type is not reliably specified by this class.
- `getRecords(null)`: implementation behavior is inconsistent by remaining-input state and not explicitly specified.
- **Configured or detected null header name:** the supplied implementation calls `header.trim()` and throws `NullPointerException`. This is the behavior implicated by Csv-11.

### Exceptional I/O cases

- File does not exist, cannot be opened, or is unreadable.
- URL cannot be opened/read.
- Reader throws during parser construction while reading/skipping a header.
- Reader throws during iteration or `getRecords()`:
  - `getRecords()` propagates `IOException`.
  - Iterator wraps it in `RuntimeException`.

A deterministic I/O-error test requires a custom `Reader` implementation or an existing project helper. No such helper is supplied, but Java’s `Reader` can be subclassed in a test without production changes.

---

## 5. Required constructors, dependencies, and external objects

### Directly required production dependencies

The target class directly depends on:

- `CSVFormat`
- `CSVRecord`
- `Lexer`
- `Token`
- `ExtendedBufferedReader`
- `Assertions`
- `Constants`

These are project classes but their source/API is not provided.

### Java standard-library dependencies useful for tests

- `StringReader`
- `File`
- `FileInputStream` indirectly through parser
- `Charset`, for example `Charset.forName("UTF-8")`
- `URL`
- `Collection`, `ArrayList`, `LinkedHashSet`, etc.
- `Iterator`
- `IOException`
- `NoSuchElementException`

### Required format creation information

Tests need a usable `CSVFormat` object. The source does not show:

- Available predefined formats such as `CSVFormat.DEFAULT`, `CSVFormat.EXCEL`, etc.
- How to configure header arrays.
- How to set a null-string.
- How to configure comments.
- How to configure `ignoreEmptyHeaders`.
- How to configure `skipHeaderRecord`.
- Whether configuration is immutable or uses builder/`withXxx` APIs.

Therefore, compilable tests for header behavior—especially Csv-11—require the `CSVFormat` API or an existing test demonstrating its configuration.

---

## 6. JUnit version and build tool

Supplied project metadata states:

- **JUnit version:** `junit-4.11.jar`
- **Build tool:** Maven

Tests should therefore use JUnit 4 conventions, such as:

- `org.junit.Test`
- `org.junit.Assert`
- `@Test(expected = SomeException.class)` where appropriate

No JUnit 5 assumptions should be made.

---

## 7. Available test oracle sources

The supplied material provides the following possible oracles:

1. **Method Javadocs in `CSVParser`**
   - Required non-null inputs.
   - Stated exceptions.
   - Record-wise and in-memory parsing semantics.
   - Iterator behavior after close and at exhaustion.
   - Header-map copy semantics.
   - Current-position behavior of `getRecords()`.

2. **Explicit source behavior**
   - Header map index construction.
   - Duplicate-header rejection behavior.
   - Comment accumulation behavior.
   - Null-string conversion behavior.
   - Iterator exception behavior.
   - `recordNumber` increment logic.

3. **Bug report metadata**
   - Bug report identifier: `CSV-122`.
   - Triggering test: `org.apache.commons.csv.CSVParserTest::testHeaderMissingWithNull`.
   - Failure: `java.lang.NullPointerException`.
   - Modified source: only `CSVParser`.

4. **Fixed-version revision identifier**
   - `b67524da7fd146634c7112b23e95d1d45c398b82`.

However, the fixed source, issue description/body, and the actual triggering test implementation are not supplied. Thus, they cannot be used to establish the complete intended expected result.

---

## 8. Csv-11 / CSV-122 bug-related behavior to test

### Defect location

The relevant code is in `initializeHeader()`:

```java
final String header = headerRecord[i];
final boolean containsHeader = hdrMap.containsKey(header);
final boolean emptyHeader = header.trim().isEmpty();
```

When `header` is `null`, `header.trim()` throws `NullPointerException`.

### Trigger condition

A test must arrange for `headerRecord` to contain a `null` entry. Based on the available source, this can occur through either path:

1. **Configured header path**
   - `format.getHeader()` returns a non-empty `String[]` containing `null`.

2. **Input-derived header path**
   - `format.getHeader()` returns an empty array, causing the parser to read the first CSV record as the header.
   - The parsed header record contains `null`, likely requiring a `CSVFormat` null-string configuration that maps a token to `null`.

The triggering test name, `testHeaderMissingWithNull`, strongly indicates that a null header entry is intentionally supplied or produced. The exact setup is not available.

### Regression behavior that should be tested

At minimum, the regression test should verify that parser construction/header initialization with a null header value does **not** fail with the reported `NullPointerException`.

Further assertions should depend on the intended contract confirmed by the missing bug report or existing test source. Plausible semantic questions that cannot be answered reliably from the supplied implementation alone include:

- Should a `null` header be retained as a valid `null` key in `getHeaderMap()`?
- Should it be treated like an empty/missing header?
- Should it be ignored when `ignoreEmptyHeaders` is enabled?
- Should duplicate null header names be rejected?
- Should a null header result in a different deliberate exception rather than successful parser construction?

The fact that `LinkedHashMap` accepts null keys means retention of a null key is technically possible, but that is not sufficient to establish it as the intended API contract.

### Related cases worth distinguishing

Once the intended null-header contract is available, tests should distinguish:

- One null header.
- Duplicate null headers.
- Null plus valid named headers.
- Null header when the header is explicitly configured.
- Null header derived from an input header record.
- Null-string matching behavior ignoring case, if input-derived null headers are part of the intended scenario.
- Normal empty string header (`""`) versus Java null header (`null`), because the current code treats them differently and invokes `trim()` only successfully for the former.

---

## 9. Missing context required for compilable, meaningful tests

The following information is missing or needed to establish reliable expected outcomes:

1. **`CSVFormat` API/source**
   - Required to create a format with headers.
   - Required to configure an auto-detected header.
   - Required to configure a null-string.
   - Required to set `ignoreEmptyHeaders` and `skipHeaderRecord`.
   - Required to select a stable delimiter/record-separator configuration for parsing tests.

2. **`CSVRecord` API/source**
   - Needed to know supported assertion methods for field values, record number, comment, and header-name lookup.
   - The supplied parser invokes package-private or unknown methods such as `values()`, but their visibility and test-facing alternatives are unknown.

3. **The body of `CSVParserTest.testHeaderMissingWithNull`**
   - This is the most important missing oracle for Csv-11.
   - It would reveal exactly how a null header is configured and what post-fix outcome is expected.

4. **CSV-122 issue description or fixed-version diff**
   - Needed to determine intended semantics beyond “avoid the observed `NullPointerException`.”
   - Particularly needed to determine whether null headers should be mapped, ignored, or rejected with a deliberate exception.

5. **`Assertions` behavior**
   - Javadocs say certain null arguments cause `IllegalArgumentException`, but the exact behavior/message comes from the unprovided `Assertions.notNull`.
   - Tests can safely assert an exception only if the project’s existing conventions or `Assertions` source are available.

6. **`Lexer` and `ExtendedBufferedReader` behavior**
   - Needed for precise line-number expectations, exact CSV lexical edge cases, close idempotence, and malformed-input construction.

7. **Existing tests/project test helpers**
   - Needed to match established project conventions and avoid duplicating or contradicting existing expected behaviors.

Without at least the `CSVFormat` configuration API and the triggering test or CSV-122 description, a compilable and semantically reliable regression test for Csv-11 cannot be generated.