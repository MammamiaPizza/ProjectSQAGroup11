## 1. Public API methods to test

`org.apache.commons.csv.CSVParser` exposes the following public methods/constructor:

### Static factory methods
- `CSVParser.parse(File file, CSVFormat format)`
- `CSVParser.parse(String string, CSVFormat format)`
- `CSVParser.parse(URL url, Charset charset, CSVFormat format)`

### Constructor
- `CSVParser(Reader reader, CSVFormat format)`

### Instance methods
- `void close()`
- `long getCurrentLineNumber()`
- `Map<String, Integer> getHeaderMap()`
- `long getRecordNumber()`
- `List<CSVRecord> getRecords()`
- `boolean isClosed()`
- `Iterator<CSVRecord> iterator()`

### Non-public but relevant parsing operation
- `CSVRecord nextRecord()` is package-private. It may be directly testable only from tests in package `org.apache.commons.csv`; otherwise it is exercised through `getRecords()` and `iterator()`.

---

## 2. Input types and valid input ranges

| API | Inputs | Stated validity requirements |
|---|---|---|
| `parse(File, CSVFormat)` | `File`, `CSVFormat` | Both must be non-null. File must be readable for successful parsing. |
| `parse(String, CSVFormat)` | `String`, `CSVFormat` | Both must be non-null. String may be empty or contain CSV data. |
| `parse(URL, Charset, CSVFormat)` | `URL`, `Charset`, `CSVFormat` | All three must be non-null. URL must be accessible/readable for successful parsing. |
| `CSVParser(Reader, CSVFormat)` | `Reader`, `CSVFormat` | Both must be non-null. `format.validate()` must accept the format. |
| `close()` | None | Can be called after successful construction. |
| `getHeaderMap()` | None | No stated precondition. This is the method implicated by the bug. |
| `getRecords()` | None | Reads all records from the parser’s current position. |
| `iterator()` | None | Parser must remain open for normal iteration. |
| `getCurrentLineNumber()`, `getRecordNumber()`, `isClosed()` | None | No stated preconditions. |

The source does not provide the full `CSVFormat` API or its validation rules. Therefore, valid delimiters, quote characters, comments, escaping configurations, header configuration APIs, and other format constraints cannot be fully enumerated from the supplied context.

---

## 3. Conditions and reachable branches

### Header initialization: `initializeHeader()`

The header-map behavior has three primary branches determined by `format.getHeader()`:

1. **No header configuration: `format.getHeader() == null`**
   - `hdrMap` remains `null`.
   - `this.headerMap` is assigned `null`.
   - Calling `getHeaderMap()` executes:
     ```java
     new LinkedHashMap<String, Integer>(this.headerMap)
     ```
     which throws `NullPointerException`.

2. **Auto-detected header: `format.getHeader().length == 0`**
   - The first parsed record is consumed as a header row.
   - If a record exists, its values become header names mapped to zero-based column indexes.
   - If input has no record, an empty `LinkedHashMap` is returned internally.

3. **Explicit header: `format.getHeader().length > 0`**
   - The provided header names are mapped to their zero-based positions.
   - If `format.getSkipHeaderRecord()` is true, one input record is consumed before normal parsing begins.
   - If duplicate header names occur, normal `Map.put` behavior means later indexes overwrite earlier entries. Whether duplicate headers are valid is not determinable without `CSVFormat.validate()` or API documentation for `CSVFormat`.

### Record parsing: `nextRecord()`

Reachable token-type branches:

- `TOKEN`
  - Adds the token content as a record value and continues reading.
- `EORECORD`
  - Adds the token content and completes the current record.
- `EOF`
  - Adds a final value only when `reusableToken.isReady` is true.
  - Otherwise returns no record if the buffer is empty.
- `INVALID`
  - Throws `IOException` with current line number in the message.
- `COMMENT`
  - Accumulates comments associated with the next record.
  - Comments alone do not create a record unless values are subsequently parsed into `record`.

Further record-value behavior:

- When `format.getNullString() == null`, every parsed token is retained as a string.
- When a null-string is configured, values equal to it ignoring case become Java `null`:
  ```java
  input.equalsIgnoreCase(nullString) ? null : input
  ```

### Iterator branches

`iterator()` returns a new iterator with these branches:

- `hasNext()` on a closed parser returns `false`.
- `hasNext()` lazily retrieves and caches the next record.
- `next()` on a closed parser throws `NoSuchElementException`.
- `next()` after exhaustion throws `NoSuchElementException`.
- `next()` without a preceding `hasNext()` reads the next record itself.
- `remove()` always throws `UnsupportedOperationException`.
- `IOException` encountered while iterating is wrapped in `RuntimeException`.

### Close-state branches

- `close()` delegates to `lexer.close()`.
- `isClosed()` delegates to `lexer.isClosed()`.
- The `lexer != null` check in `close()` is reachable in source but, after a successfully completed constructor, `lexer` is always initialized. A failed constructor does not produce a usable parser instance.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

### Normal cases

- Parse valid CSV from:
  - a `String`,
  - a `Reader`,
  - a readable `File`,
  - a readable `URL`.
- Retrieve records using:
  - `getRecords()`,
  - enhanced-for iteration / `iterator()`.
- Retrieve configured or auto-detected header mappings.
- Verify record count and line/record numbers as parsing progresses.
- Close the parser and verify `isClosed()`.

### Boundary cases

- Empty input.
- A single value with no record delimiter.
- Input ending immediately after a field versus ending with a record delimiter.
- Header-only input:
  - auto-detected header with no data records,
  - explicit header with no data records.
- Explicit empty header array, which triggers header autodetection.
- One-column records.
- Multi-line values, insofar as supported by the unspecified `CSVFormat`.
- Input containing only comments, insofar as comments are enabled by the format.
- Calling `getRecords()` after one or more records were already consumed; documentation states it begins at the current parser position.
- Calling `next()` without `hasNext()`.
- Repeated `hasNext()` before `next()`; the cached record should not be skipped.
- Iteration after end of input.
- Iteration after parser closure.

### Invalid and null cases explicitly documented

The factory methods and constructor use `Assertions.notNull`, and their Javadocs state `IllegalArgumentException` for null arguments:

- `parse((File) null, format)`
- `parse(file, null)`
- `parse((String) null, format)`
- `parse(string, null)`
- `parse((URL) null, charset, format)`
- `parse(url, null, format)`
- `parse(url, charset, null)`
- `new CSVParser(null, format)`
- `new CSVParser(reader, null)`

The source also calls `format.validate()`. Invalid or inconsistent format configurations should result in the exception behavior implemented by `CSVFormat.validate()`, but that behavior is not supplied.

### Exceptional cases

- Unreadable or missing file: `FileReader` may throw `IOException`/`FileNotFoundException`.
- Unreachable, malformed-at-runtime, or failing URL stream: `url.openStream()` may throw `IOException`.
- Reader failures while parsing: propagated as `IOException` from direct parsing methods, or wrapped in `RuntimeException` through iterator operations.
- Invalid lexer sequence: `nextRecord()` throws `IOException`.
- `Iterator.next()`:
  - closed parser → `NoSuchElementException`;
  - exhausted input → `NoSuchElementException`.
- `Iterator.remove()` → `UnsupportedOperationException`.

### Current bug behavior

For a format with no header definition (`format.getHeader() == null`):

1. `initializeHeader()` returns `null`.
2. `headerMap` is `null`.
3. `getHeaderMap()` dereferences that null value.
4. A `NullPointerException` occurs.

This matches the supplied defect information:

- Bug report: **CSV-100**
- Triggering test: `CSVParserTest::testNoHeaderMap`
- Failure: `java.lang.NullPointerException`

---

## 5. Required constructors, dependencies, and external objects

### Directly required to construct/use `CSVParser`

- `CSVFormat`
- One of:
  - `Reader` (such as `StringReader`),
  - `String`,
  - `File`,
  - `URL` plus `Charset`

### Internal dependencies used by the class

- `Assertions`
- `Lexer`
- `ExtendedBufferedReader`
- `Token` and `Token.Type`
- `CSVRecord`
- `Constants`

These are production dependencies, not necessarily objects tests need to construct directly. Most parsing behavior is controlled through `CSVFormat` and input content.

### External/resource dependencies for factory-method tests

- `File` tests need a readable temporary file or test resource.
- `URL` tests need a known readable URL. A local file URL could avoid network dependence, but whether this is appropriate depends on available test utilities and project conventions not supplied here.
- `Charset`, for example a standard Java charset, is needed for URL parsing.

---

## 6. JUnit version and build tool

Supplied project metadata states:

- **JUnit:** `junit-4.11.jar`
- **Build tool:** Maven

The Defects4J project configuration also references `Csv.build.xml`, but the prompt explicitly identifies Maven as the build tool. No Maven POM contents, test source roots, plugins, or dependency configuration were supplied.

---

## 7. Available test oracle sources

### Supplied sources that can serve as an oracle

1. **Class Javadocs**
   - Null parameter requirements.
   - Exceptions declared/documented.
   - `getRecords()` starts at the current parsing position.
   - Iterator behavior after close and on I/O failure.
   - Header-map documentation: “Returns a copy of the header map that iterates in column order.”
   - Header map values are documented as zero-based column indexes.

2. **Implementation-visible behavior**
   - Explicit and auto-detected header paths.
   - Null-string comparison is case-insensitive.
   - Iterator exception types.
   - `getRecords()` consumes remaining records.

3. **Bug report metadata**
   - CSV-100.
   - `CSVParserTest::testNoHeaderMap` is the triggering test.
   - The observed defect is a `NullPointerException`.

### Oracle limitations

The actual source for `CSVParserTest::testNoHeaderMap`, the fixed revision’s diff, the `CSVFormat` source/API, and the CSV-100 issue description are not included. Therefore, the exact intended assertion for `getHeaderMap()` with no configured header is not explicitly available.

It is reliable to state that the current `NullPointerException` is the reported erroneous behavior. It is strongly suggested by the method contract and test name that the intended non-header result is an empty map, but the supplied material does not explicitly state that expected result. A test asserting an exact empty map should be confirmed against the existing test, fixed revision, or issue specification before being treated as a definitive oracle.

---

## 8. Bug-report-related behaviors that should be tested

The central regression scenario is:

1. Create a `CSVParser` with a `CSVFormat` that does **not** define a header (`getHeader()` returns `null`).
2. Call `getHeaderMap()`.
3. Verify that it does not throw `NullPointerException`.

Additional directly related behavior to distinguish header modes:

- **No header configured**
  - Regression case: `getHeaderMap()` must be usable without an NPE.
  - The exact expected map content is not explicitly supplied, though an empty map is the likely intended result.

- **Auto-detected header**
  - Header row is consumed.
  - `getHeaderMap()` maps names to zero-based indexes.
  - Data records begin after the header row.

- **Explicit header**
  - `getHeaderMap()` maps explicit header names to zero-based indexes.
  - With `skipHeaderRecord == true`, the first input row is skipped.
  - With `skipHeaderRecord == false`, data parsing begins at the first input row.

- **Returned-map independence**
  - The documentation says `getHeaderMap()` returns a copy. A test should verify that mutating a returned map does not mutate the parser’s internally retained mapping, provided the intended map is non-null. This behavior is directly supported by the implementation for configured-header paths.

---

## 9. Missing context needed for compilable and meaningful tests

The following information is absent and limits reliable test generation:

1. **`CSVFormat` source or API**
   - How to create a format with:
     - no header,
     - auto-detected header,
     - explicit headers,
     - skipped header record,
     - null-string handling,
     - comments,
     - malformed/invalid parsing configurations.
   - The exact validation failures from `CSVFormat.validate()`.

2. **`CSVRecord` source/API**
   - The supported accessors and equality semantics needed to assert parsed field values, comments, record numbers, and header-based lookup.

3. **Existing `CSVParserTest`**
   - Especially `testNoHeaderMap`, identified as the trigger.
   - This is the most important missing oracle for the intended exact post-fix behavior.

4. **CSV-100 issue text or fixed-source diff**
   - Required to determine with certainty whether no-header `getHeaderMap()` should return:
     - an empty map,
     - `null`,
     - some other value,
     - or merely avoid throwing.
   - The present source and Javadocs imply returning a map is more consistent than returning `null`, but this is not explicit in the supplied bug report summary.

5. **Test-project conventions and resources**
   - Availability of temporary-file helpers.
   - Expected Java version.
   - Existing test package and base classes.
   - Maven dependency and Surefire configuration.

No JUnit test class should be generated yet because the required `CSVFormat` construction API and the definitive expected result for the no-header regression case are not included in the supplied context.