## 1. Public API surface that should be tested

`CSVFormat` is `final`, immutable, and has no public constructors. It is created through predefined constants, `newFormat(char)`, and the `with...` methods.

### Public constants
These should be verified for documented configuration where meaningful:

- `CSVFormat.DEFAULT`
- `CSVFormat.RFC4180`
- `CSVFormat.EXCEL`
- `CSVFormat.TDF`
- `CSVFormat.MYSQL`

### Public factory
- `static CSVFormat newFormat(char delimiter)`

### Public behavior methods
- `String format(Object... values)`
- `CSVParser parse(Reader in) throws IOException`
- `CSVPrinter print(Appendable out) throws IOException`

### Public getters / configuration-state methods
- `Character getCommentMarker()`
- `char getDelimiter()`
- `Character getEscapeCharacter()`
- `String[] getHeader()`
- `boolean getAllowMissingColumnNames()`
- `boolean getIgnoreEmptyLines()`
- `boolean getIgnoreSurroundingSpaces()`
- `String getNullString()`
- `Character getQuoteCharacter()`
- `QuoteMode getQuoteMode()`
- `String getRecordSeparator()`
- `boolean getSkipHeaderRecord()`
- `boolean isCommentMarkerSet()`
- `boolean isEscapeCharacterSet()`
- `boolean isNullStringSet()`
- `boolean isQuoteCharacterSet()`

### Object-contract / representation methods
- `boolean equals(Object obj)`
- `int hashCode()`
- `String toString()`

### Immutable configuration methods
- `CSVFormat withCommentMarker(char commentMarker)`
- `CSVFormat withCommentMarker(Character commentMarker)`
- `CSVFormat withDelimiter(char delimiter)`
- `CSVFormat withEscape(char escape)`
- `CSVFormat withEscape(Character escape)`
- `CSVFormat withHeader(String... header)`
- `CSVFormat withAllowMissingColumnNames(boolean allowMissingColumnNames)`
- `CSVFormat withIgnoreEmptyLines(boolean ignoreEmptyLines)`
- `CSVFormat withIgnoreSurroundingSpaces(boolean ignoreSurroundingSpaces)`
- `CSVFormat withNullString(String nullString)`
- `CSVFormat withQuote(char quoteChar)`
- `CSVFormat withQuote(Character quoteChar)`
- `CSVFormat withQuoteMode(QuoteMode quoteModePolicy)`
- `CSVFormat withRecordSeparator(char recordSeparator)`
- `CSVFormat withRecordSeparator(String recordSeparator)`
- `CSVFormat withSkipHeaderRecord(boolean skipHeaderRecord)`

---

## 2. Input types and documented/implemented valid ranges

| API/input | Type | Validity stated or enforced by this source |
|---|---|---|
| Delimiter | `char` | Must not be `'\r'` or `'\n'`. |
| Quote character | `char` / `Character` | `Character` may be `null` to disable quoting. Non-null must not be CR/LF and must differ from delimiter and comment marker. |
| Escape character | `char` / `Character` | `Character` may be `null` to disable escaping. Non-null must not be CR/LF and must differ from delimiter and comment marker. |
| Comment marker | `char` / `Character` | `Character` may be `null` to disable comments. Non-null must not be CR/LF and must differ from delimiter, quote character, and escape character. |
| Header | `String...` | May be `null` (disabled), empty (auto-read from input), or non-empty (explicit header). This source rejects any duplicate `String`, including duplicate `null` and duplicate `""`. |
| Missing-header-name setting | `boolean` | Both values valid. Intended behavior is relevant to the reported bug. |
| Empty-line setting | `boolean` | Both values valid. |
| Surrounding-space setting | `boolean` | Both values valid. |
| Null-string mapping | `String` | May be `null` to disable conversion. No other validation in this class. |
| Quote mode | `QuoteMode` | May apparently be `null`; no null check exists. `QuoteMode.NONE` requires a non-null escape character. Exact enum values are not supplied. |
| Record separator | `char` / `String` | Javadoc says valid strings are CR, LF, or CRLF, but this class does **not** validate that restriction. `String` may also be `null` in practice because no null check exists. |
| Values to format | `Object...` | Varargs may contain arbitrary objects and possibly `null`; resulting behavior ultimately depends on `CSVPrinter`, whose source is not supplied. |
| Parse input | `Reader` | Expected non-null reader is implied but not validated here; behavior for `null` depends on `CSVParser`. |
| Print output | `Appendable` | Expected non-null appendable is implied but not validated here; behavior for `null` and I/O failures depends on `CSVPrinter`/the appendable. |
| Equality argument | `Object` | Any object, including `null`. |

---

## 3. Reachable conditions and branches in `CSVFormat`

### Construction and validation branches

Every factory/configuration method creates a new instance and reaches the private constructor and `validate()`.

#### Delimiter validation
- Delimiter is `'\r'` or `'\n'` → `IllegalArgumentException`.
- Any other `char` → allowed unless it conflicts with another configured character.

#### Header validation
When `header == null`:
- Header remains disabled (`getHeader()` returns `null`).

When `header != null`:
- A cloned copy is stored.
- Every element is inserted into a `HashSet<String>`.
- If `HashSet.add(hdr)` returns `false`, construction throws `IllegalArgumentException`.
- Consequently, this implementation treats repeated ordinary names, repeated empty names (`""`), and repeated `null` entries all as duplicate entries.

This is the primary branch implicated by Csv-12.

#### Character-conflict validation
`validate()` contains the following exception branches:

1. Quote character equals delimiter.
2. Escape character equals delimiter.
3. Comment marker equals delimiter.
4. Quote character equals comment marker.
5. Escape character equals comment marker.
6. `quoteMode == QuoteMode.NONE` while escape character is `null`.

No check prohibits quote and escape characters from being equal to one another.

### Equality branches
`equals` covers:

- Reference identity (`this == obj`) → `true`.
- `obj == null` → `false`.
- Different runtime class → `false`.
- Each scalar/object configuration field equal/not equal.
- `header` comparison through `Arrays.equals`, including `null` versus non-null headers.
- `allowMissingColumnNames` is notably **not compared** by `equals`.

### Hash-code behavior
`hashCode()` includes most configuration fields and includes `Arrays.hashCode(header)`, but it also does **not include** `allowMissingColumnNames`.

Therefore, equality and hash code are internally aligned regarding that omitted field, though whether omitting this setting is semantically correct cannot be established from the supplied specification alone.

### String representation branches
`toString()` always includes:

- delimiter;
- `SkipHeaderRecord:<boolean>`.

It conditionally includes:

- escape;
- quote;
- comment marker;
- null string;
- record separator;
- ignored empty-lines marker;
- ignored surrounding-spaces marker;
- header.

It does not include `quoteMode` or `allowMissingColumnNames`.

### Delegation branches
- `format(...)` delegates to `CSVPrinter.printRecord(values)` using a `StringWriter`.
  - It returns `out.toString().trim()`.
  - Any `IOException` is wrapped in `IllegalStateException`, though the comment says this should not occur with `StringWriter`.
- `parse(Reader)` directly constructs `new CSVParser(in, this)`.
- `print(Appendable)` directly constructs `new CSVPrinter(out, this)`.

The detailed branches for CSV parsing and printing are outside the supplied target-class source.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

### Normal cases

1. **Predefined formats**
   - Verify documented getter values for `DEFAULT`, `RFC4180`, `EXCEL`, `TDF`, and `MYSQL`.
   - In particular, the supplied code makes `EXCEL` equal in configuration to `RFC4180`; both have `ignoreEmptyLines == false`.
   - Although `EXCEL` Javadoc says it permits missing column names, the supplied implementation initializes it from `DEFAULT.withIgnoreEmptyLines(false)` and does not set `allowMissingColumnNames` to `true`. This is a potentially important source/specification discrepancy.

2. **Custom format creation**
   - `newFormat(';')` should retain `';'` and otherwise use the constructor’s null/false defaults.

3. **Immutability**
   - Each `with...` call should produce a format reflecting only its specified change.
   - The original format should remain unchanged.
   - `getHeader()` must return a defensive copy; mutation of the returned array must not mutate the format.
   - `withHeader(String...)` must defensively copy its supplied array; mutation of the caller’s original array after creation should not mutate the format.

4. **Enable/disable nullable configuration**
   - Enable a character option with a non-null character.
   - Disable comment, escape, or quote options using the `Character` overload with `null`.
   - Set a non-null null-string and reset it with `null`.

5. **Boolean configuration**
   - Both `true` and `false` for all boolean setters.
   - Each matching getter must report the configured value.

6. **Header modes**
   - `withHeader((String[]) null)` → disabled header.
   - `withHeader()` → non-null empty array, documented as auto-read header mode.
   - `withHeader("A", "B")` → explicit header copy.

7. **Equality/hash code**
   - Same effective configuration: equal and same hash code.
   - Different ordinary fields: not equal.
   - `null`, unrelated type: not equal.
   - Reflexivity, symmetry, and stable repeated result.

8. **String representation**
   - Verify mandatory and enabled-option fragments.
   - Exact output can be asserted if considered part of the intended API; otherwise fragment assertions are less brittle.

### Boundary cases

1. Delimiter, quote, comment, and escape characters at the CR/LF boundary:
   - `'\r'`
   - `'\n'`
   - a normal non-line-break character such as `','`, `';'`, or `'\t'`.

2. Empty values:
   - Empty header array.
   - Empty header name `""`.
   - Empty null-string `""`.
   - Empty record separator `""`—the source accepts it, despite Javadoc suggesting otherwise.

3. Header arrays:
   - one name;
   - several unique names;
   - repeated names;
   - repeated empty names;
   - null header entry;
   - repeated null header entries.

4. Record separators:
   - CR, LF, CRLF, arbitrary strings, empty string, and `null`.
   - The source does not enforce the Javadoc’s stated CR/LF/CRLF restriction. Tests need an authoritative oracle before treating arbitrary values as valid or invalid expected behavior.

5. `QuoteMode`:
   - `QuoteMode.NONE` with escape set.
   - `QuoteMode.NONE` with escape disabled.
   - Other enum values, if available in the project source.
   - `null` quote mode, which this class permits structurally.

### Invalid/exceptional cases directly determinable from this source

All should expect `IllegalArgumentException`:

- `newFormat('\r')`, `newFormat('\n')`.
- `withDelimiter('\r')`, `withDelimiter('\n')`.
- `withCommentMarker('\r')`, `withCommentMarker('\n')`.
- `withEscape('\r')`, `withEscape('\n')`.
- `withQuote('\r')`, `withQuote('\n')`.
- Quote character equals delimiter.
- Escape character equals delimiter.
- Comment marker equals delimiter.
- Quote character equals comment marker.
- Escape character equals comment marker.
- `withQuoteMode(QuoteMode.NONE)` on a format whose escape character is `null`.
- Duplicate header elements under the **current source behavior**.

The final header case must be treated carefully for Bug 12: the current implementation exception is the reported defect for duplicated missing header names in an Excel parsing scenario. A regression test should represent the fixed/specification-required behavior, not encode the defective behavior as correct.

### Null cases

Directly supported:
- `withCommentMarker((Character) null)`;
- `withEscape((Character) null)`;
- `withQuote((Character) null)`;
- `withHeader((String[]) null)`;
- `withNullString(null)`;
- likely `withRecordSeparator((String) null)`;
- likely `withQuoteMode(null)`.

Not safely determinable without related-class source:
- `parse(null)`;
- `print(null)`;
- `format((Object[]) null)` or `format((Object) null)`;
- behavior if individual formatted values are null.

---

## 5. Constructors, dependencies, and external objects

### Construction
There are no public constructors.

The private constructor requires all configuration fields and cannot be called directly by a test. Tests must construct instances through:

- static constants;
- `CSVFormat.newFormat(char)`;
- immutable `with...` methods.

### Direct dependencies in the target class
- `org.apache.commons.csv.Constants` for character/string constants.
- `org.apache.commons.csv.QuoteMode`.
- `org.apache.commons.csv.CSVParser`.
- `org.apache.commons.csv.CSVPrinter`.
- `org.apache.commons.csv.CSVRecord` appears in documentation and is relevant for parsing integration.
- Java standard library:
  - `Reader`
  - `StringReader` would be an appropriate test input object, though not supplied explicitly
  - `Appendable`
  - `StringWriter`
  - `IOException`
  - arrays and collection classes.

### External objects needed for meaningful integration tests
- A `Reader`, normally a `StringReader`, for `parse`.
- A `StringWriter` or other `Appendable` for `print`.
- `CSVParser` iteration/access APIs to verify parsed records.
- `CSVRecord` access APIs to verify values/header mapping.

The APIs and implementations of `CSVParser`, `CSVPrinter`, `CSVRecord`, and `QuoteMode` have not been supplied. Therefore, precise parser/printer assertions beyond construction/delegation cannot be derived reliably from the target source alone.

---

## 6. Test framework and build tool

- **JUnit version:** `junit-4.11.jar`
- **Build tool:** Maven

Tests should therefore use JUnit 4 conventions, such as:

- `org.junit.Test`;
- `org.junit.Assert` assertions;
- `@Test(expected = IllegalArgumentException.class)` where only the exception type matters;
- or explicit `try`/`catch` for assertions on exception details.

No Maven `pom.xml`, source layout, package layout for test classes, compiler level, or existing test dependencies were supplied.

---

## 7. Available test oracles

### Strongest supplied oracle: bug report / triggering failure

Bug report:

- Project bug: `Csv-12`
- Issue: `CSV-128`
- Triggering test: `org.apache.commons.csv.CSVParserTest::testExcelHeaderCountLessThanData`
- Failure:
  ```text
  java.lang.IllegalArgumentException:
  The header contains a duplicate name: "" in [A, B, C, , ]
  ```

This establishes that the source version incorrectly rejects a parsed Excel header where trailing/missing column names produce repeated empty strings.

### API documentation in `CSVFormat`
The class Javadoc supplies useful expected behavior for:

- all predefined formats;
- immutability;
- header modes;
- null-string conversion;
- individual configuration method semantics;
- stated validation conditions.

### Limitations of available oracles
No actual existing test source is supplied, including the body of `CSVParserTest.testExcelHeaderCountLessThanData`. As a result, the exact CSV input, intended record count, expected values, header map behavior, and any required parser lifecycle behavior are unavailable.

The supplied source also contains documentation/implementation inconsistencies:

1. **EXCEL missing names**
   - Javadoc says `EXCEL` uses `withAllowMissingColumnNames(true)`.
   - Source construction does not visibly set that flag, so `EXCEL.getAllowMissingColumnNames()` evaluates according to `DEFAULT`’s configured value, which is `false`.

2. **Record separator validation**
   - Javadoc for `withRecordSeparator(String)` says invalid separators should throw `IllegalArgumentException`.
   - The supplied implementation performs no such validation.

These discrepancies prevent using documentation alone as a fully reliable expected-result oracle for those cases.

---

## 8. Bug-12-specific behaviors that should be tested

The regression scope should focus on the reported failure pathway:

1. **Excel parsing with auto-detected headers**
   - Configure parsing with `CSVFormat.EXCEL.withHeader()` or the equivalent setup used by the original triggering test, if confirmed from existing tests.
   - Provide a header row where the number of data columns is less than the header-related column count described by the test name, or, more directly, where header parsing yields:
     ```text
     [A, B, C, "", ""]
     ```
   - Verify that parsing does not throw the reported duplicate-header `IllegalArgumentException` when missing column names are permitted.

2. **Repeated missing/empty header names**
   - Specifically cover repeated `""` header names resulting from parsing, because this is exactly the failing condition:
     ```text
     The header contains a duplicate name: "" in [A, B, C, , ]
     ```

3. **Interaction with `allowMissingColumnNames`**
   - The intended distinction appears to be:
     - missing/empty header names allowed → repeated missing names should not cause the duplicate-header exception;
     - missing/empty header names not allowed → an exception may be appropriate.
   - However, the precise expected exception point and parser behavior must be verified from the parser source, fixed revision, issue text, or existing tests before writing a compilable, reliable test.

4. **Non-empty duplicate names**
   - A regression should avoid accidentally changing the policy for ordinary duplicate names unless the issue specification says so.
   - A useful companion case would distinguish repeated `""` names from repeated non-empty names such as `["A", "A"]`.
   - The supplied data does not conclusively state whether non-empty duplicate headers must remain rejected, although the current constructor intends to reject all duplicates.

5. **Do not test the defective behavior as expected**
   - A test asserting that `withHeader("A", "B", "C", "", "")` throws would match this buggy source, but it would conflict with the reported desired behavior if empty duplicate names are allowed in the relevant configuration.
   - Moreover, the bug occurs during parser-derived header creation, not necessarily direct `withHeader(...)` configuration. The appropriate API-level test target depends on `CSVParser` behavior not provided here.

---

## 9. Missing context needed for compilable and meaningful tests

The supplied information is insufficient to determine several reliable integration expectations.

### Required missing context

1. **Source or API details for `CSVParser`**
   Needed to determine:
   - how parser-created headers are constructed;
   - whether `withHeader()` produces `""` or `null` for missing trailing names;
   - where `allowMissingColumnNames` is enforced;
   - parser iteration API and expected records;
   - parser resource management/close behavior;
   - exact behavior for a null `Reader`.

2. **Source or API details for `CSVRecord`**
   Needed to assert:
   - record contents;
   - size;
   - named-column lookup;
   - behavior with unnamed/duplicate/missing columns.

3. **Source or API details for `CSVPrinter`**
   Needed to reliably test:
   - exact formatting/quoting;
   - treatment of null values;
   - header printing;
   - `format(Object...)` output;
   - behavior for null `Appendable` and I/O errors.

4. **`QuoteMode` enum definition**
   Needed to enumerate valid modes beyond the one visible reference, `QuoteMode.NONE`.

5. **Actual source of the triggering test**
   The test body for `CSVParserTest::testExcelHeaderCountLessThanData` is needed to reproduce the intended regression input and assertions exactly.

6. **Issue CSV-128 specification or fixed-version diff**
   The report summary gives the failure but does not explicitly define:
   - whether repeated empty names are legal only with `allowMissingColumnNames == true`;
   - whether this applies only to parser-extracted headers;
   - whether manual headers should follow the same rule;
   - whether repeated `null` header names are also allowed;
   - expected behavior for repeated non-empty names.

7. **Maven project configuration**
   A `pom.xml` or test compilation configuration is needed to ensure package placement, Java source level, dependencies, and test execution setup.

### Conclusion on sufficiency

The supplied target source is sufficient to design unit tests for most configuration getters, immutability, validation exceptions, equality/hash code, and basic representation behavior.

It is **not sufficient** to generate a reliable Bug-12 regression test with precise parser assertions, because the parser implementation, the triggering test body, and the full bug specification are absent. The failure message strongly identifies the defect—unconditional rejection of duplicated empty header names—but the exact corrected contract and the parser-level setup require the missing context above.