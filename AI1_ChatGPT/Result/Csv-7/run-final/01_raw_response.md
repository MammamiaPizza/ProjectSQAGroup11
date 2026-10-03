## Scope analyzed

Only the supplied source of `org.apache.commons.csv.CSVParser`, the supplied Defects4J bug metadata, and project metadata were analyzed. No assumptions are made about behavior implemented in omitted classes such as `CSVFormat`, `Lexer`, `CSVRecord`, `Token`, or `Assertions`.

## 1. Public methods to test

### Static factory methods
1. `public static CSVParser parse(File file, CSVFormat format) throws IOException`
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

### Non-public method relevant to behavior
13. Package-private `CSVRecord nextRecord() throws IOException`

`nextRecord()` is not public but is the core method used by `iterator()`, `getRecords()`, and header initialization. It may be testable from a test in package `org.apache.commons.csv`, subject to project test conventions.

---

## 2. Input types and valid input ranges

| API | Inputs | Explicit validity requirements in supplied source/Javadoc |
|---|---|---|
| `parse(File, CSVFormat)` | `File`, `CSVFormat` | Both must be non-null. File must be readable for successful parsing. |
| `parse(String, CSVFormat)` | CSV source `String`, `CSVFormat` | Both must be non-null. String may be empty. Valid CSV syntax depends on `CSVFormat` and `Lexer`. |
| `parse(URL, Charset, CSVFormat)` | `URL`, `Charset`, `CSVFormat` | All must be non-null. URL must be accessible and its contents decodable by charset. |
| constructor | `Reader`, `CSVFormat` | Both must be non-null. `format.validate()` must succeed. |
| `getRecords(T)` | `T extends Collection<CSVRecord>` | No explicit null validation. A usable mutable collection is needed if records are parsed and added. |
| `iterator()` | none | Parser lifecycle and unread input position determine results. |
| `getHeaderMap()` | none | Result depends on `CSVFormat.getHeader()` and input-derived header data. |

Input size/range constraints are not stated. CSV records may contain multiple values, comments, end-of-record markers, and possibly multi-line values; exact syntax is delegated to `Lexer` and `CSVFormat`.

---

## 3. Conditions and reachable branches

### Construction and factory methods

#### `parse(File, CSVFormat)`
Reachable conditions:
- Non-null file and format: creates `FileReader` and parser.
- `file == null`: `Assertions.notNull` is invoked.
- `format == null`: `Assertions.notNull` is invoked.
- File access failure: `FileReader` can throw `IOException`/`FileNotFoundException`.
- Invalid format: constructor invokes `format.validate()`.

#### `parse(String, CSVFormat)`
Reachable conditions:
- Non-null string and format: wraps source in `StringReader`.
- Null string or format: `Assertions.notNull`.
- Invalid format: constructor validation.
- Header initialization may parse data and can throw `IOException`.

#### `parse(URL, Charset, CSVFormat)`
Reachable conditions:
- Non-null URL, charset, format: opens URL stream and creates parser.
- Null URL, charset, or format: `Assertions.notNull`.
- URL opening/read failure: `url.openStream()` can throw `IOException`.
- Invalid format: constructor validation.

#### Constructor and `initializeHeader()`
Header behavior has the following branches:
- `format.getHeader() == null`:
  - `headerMap` is `null`.
- `format.getHeader()` is a zero-length array:
  - Reads the first CSV record through `nextRecord()`.
  - If input has a first record, uses its values as header names.
  - If input is empty/no first record, produces an empty `LinkedHashMap`.
- `format.getHeader()` is a non-empty array:
  - If `format.getSkipHeaderRecord()` is `true`, consumes one record before parsing user-visible records.
  - Uses configured header values to populate the map.
- Header values are inserted with `hdrMap.put(header[i], i)`.

The supplied version has no duplicate-header rejection branch: repeated names overwrite previous map values. This is the behavior implicated by Csv-7.

### `nextRecord()`
The method can receive these token types from `Lexer`:
- `TOKEN`: adds a record value and continues parsing the same record.
- `EORECORD`: adds final value and finishes the record.
- `EOF`:
  - If `reusableToken.isReady` is true, adds a final value.
  - Otherwise does not add a value.
- `INVALID`: throws `IOException` containing the current line number.
- `COMMENT`:
  - Accumulates comments for the resulting record.
  - Multiple comments are joined with `Constants.LF`.
  - Continues parsing for a subsequent token.
- Any other token type: throws `IllegalStateException`.

After token processing:
- An empty internal record buffer returns `null`.
- A non-empty record increments `recordNumber` and returns a `CSVRecord`.
- A parsed record can contain `null` values when `format.getNullString()` is non-null and a field equals it ignoring case.

### `iterator()`
Reachable iterator branches:
- `hasNext()` on a closed parser returns `false`.
- `hasNext()` caches one next record, if available.
- `next()` on a closed parser throws `NoSuchElementException`.
- `next()` after cached `hasNext()` returns the cached record.
- `next()` without prior `hasNext()` fetches directly.
- `next()` at end of input throws `NoSuchElementException`.
- `remove()` always throws `UnsupportedOperationException`.
- `IOException` while obtaining the next record is wrapped in `RuntimeException`.

### `getRecords()`
- Creates and returns a new `ArrayList<CSVRecord>`.
- Reads from the parser’s *current* position, not necessarily from the beginning.
- Returns an empty list at EOF.
- Propagates `IOException` from `nextRecord()`.

### `getRecords(T records)`
- Repeatedly calls `nextRecord()` and adds each non-null record.
- Returns the same collection reference after population.
- An empty input produces no call to `records.add`.
- There is no explicit null check:
  - With at least one parsed record, a null `records` argument reaches `records.add(rec)` and causes `NullPointerException`.
  - With no records remaining, the method returns `null` unchanged. This is an implementation observation, not a documented API contract.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

### Normal cases
- Parse a simple one-record or multi-record CSV source through each applicable creation API.
- Iterate records using enhanced-for / `iterator()`.
- Collect records with `getRecords()`.
- Collect records into an existing mutable collection via `getRecords(existingCollection)`.
- Retrieve record number before and after parsing records.
- Retrieve current line number during/after parsing.
- Retrieve a configured or input-derived header map.
- Close the parser and verify closed-state behavior.
- Parse records with configured null-string values, provided a suitable `CSVFormat` can be constructed from available project APIs.

### Boundary cases
- Empty string/input.
- A final record without an end-of-record terminator.
- Header-only input when header is read from input.
- Configured header with no data rows.
- No configured header.
- Zero-length configured header array, which has special semantics: derive headers from the first input record.
- Repeated `hasNext()` before `next()`, ensuring a record is not skipped or duplicated.
- `next()` without first calling `hasNext()`.
- `next()` after end of iteration.
- `hasNext()` and `next()` after closure.
- `getRecords()` after one or more records have already been consumed.
- Verify returned header-map copy can be modified without changing a subsequently returned parser header map.

### Invalid/null cases
- Null `File`.
- Null CSV string.
- Null `URL`.
- Null `Charset`.
- Null `Reader`.
- Null `CSVFormat`.
- Invalid/inconsistent `CSVFormat`, as determined by `CSVFormat.validate()`.
- Null collection passed to `getRecords(T)`.
- Immutable/unmodifiable collection passed to `getRecords(T)`, when at least one record is available; expected failure type depends on that collection implementation, typically `UnsupportedOperationException`.
- Invalid CSV token sequences, if a reproducible input/format combination is known from `Lexer` behavior.

### Exceptional cases
- Unreadable or nonexistent file: likely `IOException` from `FileReader`.
- Inaccessible URL or stream read failure: `IOException`.
- Reader failure: `IOException` from parser construction/header initialization, `nextRecord()`, `getRecords()`, or wrapped in `RuntimeException` through iterator traversal.
- Invalid parser token: `IOException` in `nextRecord()`.
- Closed iterator `next()`: `NoSuchElementException`.
- End-of-input iterator `next()`: `NoSuchElementException`.
- Iterator `remove()`: `UnsupportedOperationException`.
- Unexpected lexer token: `IllegalStateException`; reachability requires an omitted `Token.Type`/`Lexer` implementation that can emit an unhandled type.

The exact exception type thrown by `Assertions.notNull` cannot be verified from supplied source because `Assertions` is omitted. Javadoc says `IllegalArgumentException`, but tests should verify that only if the project’s `Assertions` implementation is available and confirms it.

---

## 5. Required constructors, dependencies, and external objects

### Required production objects
- `CSVParser(Reader, CSVFormat)` is the direct constructor.
- Static factory methods require:
  - `File`
  - `String`
  - `URL`
  - `Charset`
  - `CSVFormat`

### Direct dependencies visible in the source
- `CSVFormat`
- `CSVRecord`
- `Lexer`
- `ExtendedBufferedReader`
- `Token` and `Token.Type`
- `Assertions`
- `Constants`
- Java I/O classes (`Reader`, `StringReader`, `FileReader`, etc.)
- Java collections and iterator classes

### Typical test-only external objects
- `StringReader` is sufficient for in-memory parser constructor tests.
- Temporary files may be needed for `parse(File, CSVFormat)`.
- A controlled local/file URL may be needed for `parse(URL, Charset, CSVFormat)`.
- A `Charset`, likely from Java standard APIs, is needed for URL parsing.
- The exact API for constructing or modifying `CSVFormat` is not supplied, so concrete format setup cannot safely be specified.

---

## 6. JUnit version and build tool

- **JUnit:** `junit-4.11.jar`
- **Build tool:** Maven

Tests should therefore use JUnit 4 conventions, such as:
- `org.junit.Test`
- `org.junit.Assert`
- `@Test(expected = ...)` where appropriate, or explicit try/fail assertions.

The supplied triggering failure uses `junit.framework.AssertionFailedError`, which is compatible with historical JUnit infrastructure, but the declared JUnit version is JUnit 4.11.

---

## 7. Available test oracle

### Explicit bug oracle
The supplied defect metadata is the strongest oracle:

- Bug report: **CSV-112**
- Triggering test:  
  `org.apache.commons.csv.CSVParserTest::testDuplicateHeaderEntries`
- Failure in source version Csv-7b:  
  **Expected exception: `java.lang.IllegalStateException`**
- Only modified source: `org.apache.commons.csv.CSVParser`

This establishes that, for the triggering duplicate-header configuration/input, the expected behavior is an `IllegalStateException`, while this supplied source version does not throw one.

### Source-derived behavioral oracle
The Javadoc and implementation provide additional, but sometimes limited, expectations:
- Static factory and constructor arguments must not be null.
- `getHeaderMap()` returns a copy or `null` if no header exists.
- `getRecords()` reads from the current parse position.
- Iterator I/O failures are wrapped in `RuntimeException`.
- Iterator `next()` on a closed parser or after exhaustion throws `NoSuchElementException`.
- `remove()` is unsupported.
- Record and line number semantics are documented.

### Missing oracle material
The actual `CSVParserTest`, fixed revision diff, CSV-112 text, and API/source of `CSVFormat` are not supplied. Those artifacts would be needed to determine the exact duplicate-header scenario and broad expected behavior without inference.

---

## 8. Bug-report-related behaviors that should be tested

The regression focus is duplicate header entries.

### Confirmed required regression behavior
A parser configuration/input that produces duplicate header names must cause `IllegalStateException` during parser initialization/header-map construction, because the provided triggering test explicitly expected that exception.

### Relevant duplicate-header paths in this class
There are two potential sources of header entries:

1. **Header read from the input**
   - `format.getHeader()` returns a zero-length array.
   - `initializeHeader()` reads the first parsed record and uses its values as headers.
   - Example conceptual input shape: a first CSV record with repeated column names.

2. **Explicitly configured header array**
   - `format.getHeader()` returns a non-empty array.
   - `initializeHeader()` uses that array as headers.
   - The expected behavior for duplicate names in this path is not explicitly established by the supplied bug metadata.

The triggering test name, `testDuplicateHeaderEntries`, does not reveal whether the duplicate headers came from input-derived headers, explicitly configured headers, or both. Therefore:
- A regression test should cover the exact scenario indicated by the available project test/API context once available.
- It is not reliable, based only on this prompt, to assert that *both* header sources must reject duplicates, though the common implementation location suggests they may be intended to share that rule.

### Why the source version fails
In `initializeHeader()`, each header is added through:

```java
hdrMap.put(header[i], Integer.valueOf(i));
```

For a duplicate key, `LinkedHashMap.put` replaces the previous index and does not throw. No duplicate detection is present in this version.

---

## 9. Missing context needed for compilable and meaningful tests

The following is missing and prevents reliably generating complete compilable tests, particularly for Csv-7 regression coverage:

1. **`CSVFormat` API and constants**
   - How to obtain standard formats, for example whether `CSVFormat.DEFAULT`, `CSVFormat.RFC4180`, or builder/`withHeader` methods exist in this exact source version.
   - How to configure:
     - input-derived headers,
     - explicit headers,
     - header skipping,
     - delimiter,
     - comments,
     - null string,
     - record separator.

2. **Existing `CSVParserTest` source**
   - Especially `testDuplicateHeaderEntries`.
   - This is necessary to identify the exact input and format setup intended by CSV-112.

3. **CSV-112 issue contents or fixed-version diff**
   - The metadata confirms an expected `IllegalStateException`, but not the full intended contract:
     - whether all duplicate header sources should fail,
     - when the exception should occur,
     - whether null or empty header names have special rules,
     - expected message, if any.

4. **Supporting class implementations**
   - `Assertions`: exact null-argument exception type/message.
   - `Lexer`, `Token`, and `ExtendedBufferedReader`: reliable invalid-input and line-number test scenarios.
   - `CSVRecord`: accessible assertions for values, comments, record number, and header-based access.
   - `CSVFormat.validate()`: invalid-format combinations and their exception behavior.

5. **Maven project configuration**
   - Although Maven and JUnit 4.11 are supplied, the actual `pom.xml` would confirm source/target Java version, test source layout, dependencies, and any test execution restrictions.

## Conclusion

The central regression requirement is clear: the duplicate-header scenario represented by `CSVParserTest::testDuplicateHeaderEntries` must throw `IllegalStateException`. The supplied Csv-7b implementation does not detect duplicates and therefore cannot satisfy that requirement.

A fully reliable, compilable test class cannot yet be generated from the supplied information without the exact `CSVFormat` construction API and, ideally, the existing triggering test or CSV-112 issue details.