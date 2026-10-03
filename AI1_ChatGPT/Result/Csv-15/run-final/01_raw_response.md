## 1. Public methods that should be tested

`CSVFormat` has no public constructor. Instances are created through predefined constants, `newFormat(char)`, and immutable `with...` methods.

### Factory and predefined-format API
- `static CSVFormat newFormat(char delimiter)`
- `static CSVFormat valueOf(String format)`
- Predefined constants:
  - `DEFAULT`, `EXCEL`, `INFORMIX_UNLOAD`, `INFORMIX_UNLOAD_CSV`, `MYSQL`,
    `POSTGRESQL_CSV`, `POSTGRESQL_TEXT`, `RFC4180`, `TDF`
- `CSVFormat.Predefined.getFormat()`

### General object contract / formatting
- `equals(Object)`
- `hashCode()`
- `toString()`
- `format(Object... values)`

### Configuration getters
- `getAllowMissingColumnNames()`
- `getCommentMarker()`
- `getDelimiter()`
- `getEscapeCharacter()`
- `getHeader()`
- `getHeaderComments()`
- `getIgnoreEmptyLines()`
- `getIgnoreHeaderCase()`
- `getIgnoreSurroundingSpaces()`
- `getNullString()`
- `getQuoteCharacter()`
- `getQuoteMode()`
- `getRecordSeparator()`
- `getSkipHeaderRecord()`
- `getTrailingDelimiter()`
- `getTrim()`
- `getAutoFlush()`
- `isCommentMarkerSet()`
- `isEscapeCharacterSet()`
- `isNullStringSet()`
- `isQuoteCharacterSet()`

### Parsing and printing API
- `parse(Reader)`
- `print(Appendable)`
- `printer()`
- `print(File, Charset)`
- `print(Path, Charset)`
- `print(Object, Appendable, boolean)`
- `println(Appendable)`
- `printRecord(Appendable, Object...)`

### Immutable configuration methods
- `withAllowMissingColumnNames()`
- `withAllowMissingColumnNames(boolean)`
- `withCommentMarker(char)`
- `withCommentMarker(Character)`
- `withDelimiter(char)`
- `withEscape(char)`
- `withEscape(Character)`
- `withFirstRecordAsHeader()`
- `withHeader(Class<? extends Enum<?>>)`
- `withHeader(ResultSet)`
- `withHeader(ResultSetMetaData)`
- `withHeader(String...)`
- `withHeaderComments(Object...)`
- `withIgnoreEmptyLines()`
- `withIgnoreEmptyLines(boolean)`
- `withIgnoreHeaderCase()`
- `withIgnoreHeaderCase(boolean)`
- `withIgnoreSurroundingSpaces()`
- `withIgnoreSurroundingSpaces(boolean)`
- `withNullString(String)`
- `withQuote(char)`
- `withQuote(Character)`
- `withQuoteMode(QuoteMode)`
- `withRecordSeparator(char)`
- `withRecordSeparator(String)`
- `withSkipHeaderRecord()`
- `withSkipHeaderRecord(boolean)`
- `withTrailingDelimiter()`
- `withTrailingDelimiter(boolean)`
- `withTrim()`
- `withTrim(boolean)`
- `withAutoFlush(boolean)`

For bug `Csv-15`, the directly relevant public methods are:

- `print(Object, Appendable, boolean)`
- `printRecord(Appendable, Object...)`
- `format(Object...)`
- indirectly, `print(Appendable)` / `CSVPrinter.printRecord(...)`
- configuration methods that preserve or alter quote behavior:
  - `withQuote(char/Character)`
  - `withQuoteMode(QuoteMode)`
  - `withRecordSeparator(...)`

---

## 2. Input types and valid input ranges

### Character configuration

| API | Input | Valid range / restrictions |
|---|---|---|
| `newFormat(char)` | delimiter | Must not be `'\r'` or `'\n'`. |
| `withDelimiter(char)` | delimiter | Must not be `'\r'` or `'\n'`; must also differ from configured quote, escape, and comment characters. |
| `withCommentMarker(char/Character)` | marker | `Character` may be `null` to disable. Non-null value must not be `'\r'` or `'\n'`, delimiter, quote character, or escape character. |
| `withEscape(char/Character)` | escape | `Character` may be `null` to disable. Non-null value must not be `'\r'` or `'\n'`, delimiter, or comment marker. |
| `withQuote(char/Character)` | quote | `Character` may be `null` to disable. Non-null value must not be `'\r'` or `'\n'`, delimiter, or comment marker. |
| `withRecordSeparator(char)` | separator | Any `char` is accepted by the supplied implementation, because it delegates to the String overload and no validation is performed. |
| `withRecordSeparator(String)` | separator | `String`, including `null`, is accepted by the supplied implementation. Javadoc claims an `IllegalArgumentException` for values other than CR, LF, or CRLF, but this source does not implement that validation. |

### Other configuration inputs

| API | Input type | Relevant valid cases |
|---|---|---|
| `withQuoteMode(QuoteMode)` | `QuoteMode` | Enum value or `null`. `null` means the printing implementation falls back to `QuoteMode.MINIMAL`. `QuoteMode.NONE` requires a non-null escape character. |
| `withNullString(String)` | `String` | Any string, including `null` and empty string. |
| Boolean `with...` methods | `boolean` | Both `true` and `false`. |
| `withHeader(String...)` | `String[]` varargs | `null` disables headers; empty array indicates automatic header parsing; non-empty array defines explicit headers. Entries must be unique, including duplicate `null` entries. |
| `withHeaderComments(Object...)` | `Object[]` varargs | `null` disables comments; each non-null object is converted via `toString()`. |
| `withHeader(Class<? extends Enum<?>>)` | enum class | `null` disables the header; otherwise enum constant names become headers. |
| `withHeader(ResultSet)` | JDBC `ResultSet` | `null` disables header; otherwise obtains metadata. |
| `withHeader(ResultSetMetaData)` | JDBC metadata | `null` disables header; otherwise labels from columns `1..getColumnCount()` become headers. |
| `format(Object...)`, `printRecord(...)` | `Object[]` varargs | Individual values may be `null`; values may be `CharSequence`, `Number`, or arbitrary objects with `toString()`. |
| `print(Object, Appendable, boolean)` | Object, appendable, flag | `value` may be `null`; `newRecord` controls whether a delimiter precedes the value. |
| `parse(Reader)` | `Reader` | A non-null readable source is required for meaningful parsing; null behavior is not documented in this class. |
| `print(File/Path, Charset)` | file/path and charset | Non-null writable destination and usable charset are required; filesystem conditions determine `IOException`. |
| `valueOf(String)` | predefined enum name | Exact case-sensitive `CSVFormat.Predefined` enum name, such as `"Default"` or `"Excel"`. |

---

## 3. Conditions and reachable branches

### Construction and validation branches

The private constructor invokes `validate()`. Every `with...` operation creates a new `CSVFormat` and therefore reaches validation.

Reachable validation failures:

1. Delimiter is CR or LF.
2. Quote character equals delimiter.
3. Escape character equals delimiter.
4. Comment marker equals delimiter.
5. Quote character equals comment marker.
6. Escape character equals comment marker.
7. `quoteMode == QuoteMode.NONE` and escape character is null.
8. Explicit header array contains duplicate entries, including duplicate `null`.

Additional pre-construction checks occur in:
- `withCommentMarker(Character)`: rejects CR/LF.
- `withEscape(Character)`: rejects CR/LF.
- `withQuote(Character)`: rejects CR/LF.
- `withDelimiter(char)`: rejects CR/LF.

### Printing branches

`print(Object, Appendable, boolean)` has these significant branches:

1. **Null value**
   - `nullString == null`: prints empty content.
   - `nullString != null` and quote mode is `QuoteMode.ALL`: constructs quoted null-string content.
   - `nullString != null` and quote mode is not `ALL`: uses null-string content without pre-quoting.

2. **Non-null value**
   - `CharSequence`: uses it directly.
   - Other object: calls `toString()`.

3. **Trimming**
   - `trim == true`: output value is trimmed before quote/escape decisions.
   - `trim == false`: original value is retained.

4. **Delimiter handling**
   - `newRecord == true`: no leading delimiter.
   - `newRecord == false`: emits the configured delimiter first.

5. **Output strategy**
   - Quote character set: `printAndQuote(...)`.
   - No quote, escape character set: `printAndEscape(...)`.
   - Neither quote nor escape: direct append.

### Quote-mode branches

`printAndQuote(...)` uses configured quote mode; a null quote mode behaves as `MINIMAL`.

- `ALL` and `ALL_NON_NULL`: always quote non-null values.
- `NON_NUMERIC`: quote unless original object is a `Number`.
- `NONE`: delegates to escape handling. This only works when an escape character exists due to validation.
- `MINIMAL`:
  - Empty first value (`newRecord == true`) is quoted.
  - First character may trigger quoting.
  - Any LF, CR, quote character, or delimiter anywhere triggers quoting.
  - A final character less than or equal to space triggers quoting.
  - Embedded quote characters are doubled.

### Escape branches

`printAndEscape(...)` escapes:
- LF as `\n`
- CR as `\r`
- delimiter
- escape character itself

using the configured escape character.

### Record termination branches

`println(Appendable)`:
- Adds delimiter first when `trailingDelimiter == true`.
- Adds `recordSeparator` only when it is non-null.

### Header branches

- `withHeader((String[]) null)`: header disabled.
- `withHeader()`: empty header array, intended to request header extraction during parsing.
- `withHeader("A", "B")`: explicit header.
- `withFirstRecordAsHeader()`: equivalent to `withHeader().withSkipHeaderRecord()`.
- `withHeader(Class)`: uses enum names.
- `withHeader(ResultSet/ResultSetMetaData)`: uses database column labels.
- Duplicate header names fail during new format construction.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

### Normal cases

- `CSVFormat.DEFAULT` has expected documented configuration.
- `newFormat(',')` creates a minimally configured format:
  - delimiter set;
  - quote, escape, comment, headers, record separator, and null-string unset;
  - booleans false.
- Every `with...` method returns a new format and leaves the original immutable.
- Getter values reflect configured state.
- Header/comment getters return defensive copies.
- Formatting ordinary values produces delimiter-separated output.
- A value containing delimiter, quote, or line break is quoted under minimal quoting.
- A quoted value doubles embedded quote characters.
- Escape-only formats escape delimiter, CR, LF, and escape character.
- `println` honors trailing delimiter and record separator settings.

### Boundary cases

- Delimiter values immediately around line breaks, but especially `'\r'` and `'\n'`.
- First value versus subsequent value (`newRecord` true/false).
- Empty first value versus empty non-first value.
- Values beginning with:
  - `'#'`
  - space
  - quote character
  - delimiter
  - control characters
  - ASCII `~` (`0x7E`)
  - non-ASCII Unicode such as `€` (`U+20AC`)
- Values ending with:
  - space (`0x20`)
  - control character
  - ordinary printable character.
- Null string cases:
  - `nullString == null`
  - empty null string
  - ordinary null string, e.g. `"NULL"`.
- Quote modes:
  - null/default (`MINIMAL`)
  - all four enum policies.
- `withHeader()` empty-header behavior.
- Zero-column `ResultSetMetaData`.
- A header array with one item, empty strings, and a single null entry.

### Invalid cases with explicit source behavior

Expected `IllegalArgumentException`:
- `CSVFormat.newFormat('\n')`, `CSVFormat.newFormat('\r')`.
- `withDelimiter('\n')`, `withDelimiter('\r')`.
- `withCommentMarker('\n')`, `withCommentMarker('\r')`.
- `withEscape('\n')`, `withEscape('\r')`.
- `withQuote('\n')`, `withQuote('\r')`.
- delimiter matching quote, escape, or comment marker.
- quote matching comment marker.
- escape matching comment marker.
- `withQuoteMode(QuoteMode.NONE)` while escape remains null.
- duplicate explicit header entries.

Expected enum lookup failure:
- `valueOf("unknown")`: `IllegalArgumentException` from `Enum.valueOf`.
- `valueOf(null)`: null failure from `Enum.valueOf` (normally `NullPointerException`).

### Null cases

Explicitly supported by the source/API:
- `withCommentMarker((Character) null)`.
- `withEscape((Character) null)`.
- `withQuote((Character) null)`.
- `withNullString(null)`.
- `withRecordSeparator((String) null)`.
- `withHeader((String[]) null)`.
- `withHeader((Class<? extends Enum<?>>) null)`.
- `withHeader((ResultSet) null)`.
- `withHeader((ResultSetMetaData) null)`.
- `withHeaderComments((Object[]) null)`.
- Individual `null` record values.

Null behavior is not reliably specified in the supplied class for:
- `parse(null)`;
- `print((Appendable) null)`;
- `print((File) null, charset)`;
- `print((Path) null, charset)`;
- `print(fileOrPath, null)`;
- a null varargs array passed explicitly, e.g. `printRecord(out, (Object[]) null)`.

The current source would likely throw `NullPointerException` in several of these cases, but no API contract in the supplied material establishes those as intended assertions.

### Exceptional cases requiring external behavior

- `parse(Reader)`: I/O errors may occur while `CSVParser` consumes the reader, but `CSVParser` implementation is not supplied.
- `print(File, Charset)` and `print(Path, Charset)`:
  - invalid path;
  - missing permissions;
  - inaccessible destination;
  - invalid/unsupported filesystem state;
  - writer creation failures.
- `print(Appendable)`, `printRecord`, `print`, and `println` may propagate `IOException` from a custom failing `Appendable`.
- `withHeader(ResultSet)` / `withHeader(ResultSetMetaData)` propagate `SQLException` from JDBC objects.

---

## 5. Required constructors, dependencies, and external objects

### Construction

`CSVFormat` constructor is private. Tests must use:
- `CSVFormat.newFormat(char)`,
- a predefined constant, typically `CSVFormat.DEFAULT`,
- or an immutable `with...` method chain.

### Dependencies for focused Csv-15 testing

A minimal bug-regression test only needs:
- `CSVFormat.DEFAULT`;
- a `StringBuilder` or `StringWriter`;
- either:
  - `CSVFormat.print(Object, Appendable, boolean)`, or
  - `CSVFormat.printRecord(Appendable, Object...)`, or
  - `CSVPrinter`, since the triggering test is in `CSVPrinterTest`.

No mocking framework is necessary for the reported bug.

### Dependencies for broader API coverage

- `java.io.StringReader` for parsing.
- `StringBuilder` / `StringWriter` for output.
- Custom `Appendable` implementation that throws `IOException`, if testing propagation.
- Temporary `File` or `Path` and a standard charset such as `StandardCharsets.UTF_8` for file/path print methods.
- `ResultSet` and `ResultSetMetaData` stubs, dynamic proxies, or a mocking library for JDBC-header methods. No mocking library is identified in the supplied project context, so tests should not assume Mockito or another library is available.
- `CSVParser`, `CSVPrinter`, `CSVRecord`, `QuoteMode`, and `Constants` are production dependencies referenced by the target class but their implementations are not supplied.

---

## 6. JUnit version and build tool

- **JUnit:** `junit-4.12.jar`
- **Build tool:** Maven

Tests should therefore use JUnit 4 conventions, such as:
- `org.junit.Test`
- `org.junit.Assert.*`
- `@Test(expected = SomeException.class)` where appropriate

No JUnit 5 features should be assumed.

---

## 7. Available test oracle

### Strongest oracle: supplied bug report and triggering test

The bug report is:

- **Bug ID:** Csv-15
- **Apache issue:** CSV-219
- **Triggering test:**  
  `org.apache.commons.csv.CSVPrinterTest::testDontQuoteEuroFirstChar`
- **Reported failure excerpt:**  
  `expected:<[€],Deux ...`

This establishes the intended behavior relevant to the defect:

> Under the normal/default minimal-quoting behavior, a record whose first value starts with the Euro sign (`€`) must not be quoted merely because that first character is non-ASCII.

The expected textual record content is therefore:

```text
€,Deux
```

The exact assertion should account for the API used:
- `CSVFormat.print(Object, builder, true)` should append exactly `€`.
- `CSVFormat.printRecord(builder, "€", "Deux")` under `CSVFormat.DEFAULT` should include the format’s record separator, which is documented and initialized as `"\r\n"`:
  ```text
  €,Deux\r\n
  ```
- `CSVFormat.format("€", "Deux")` removes leading/trailing whitespace using `String.trim()`, so it should return:
  ```text
  €,Deux
  ```

### Secondary oracle: Javadoc in the supplied class

The source Javadocs define intended behavior for:
- predefined formats;
- configuration methods;
- immutability;
- header behavior;
- null-string conversion;
- quoting and escaping;
- trim/trailing delimiter/record separator behavior.

### Source-versus-documentation discrepancy

`withRecordSeparator(String)` Javadoc says it throws `IllegalArgumentException` when the separator is not CR, LF, or CRLF. However, the supplied implementation does not validate it. The source accepts arbitrary strings and null.

Because the prompt requires not assuming the implementation is correct, this is an ambiguous point:
- The documentation supports a validation expectation.
- The supplied source supports acceptance.

No supplied bug report or existing test resolves that conflict. A reliable test expectation for invalid record separators therefore requires an explicit oracle decision; it should not be inferred from implementation alone.

---

## 8. Behaviors related to Csv-15 that should be tested

### Defect mechanism in the supplied source

In `printAndQuote(...)`, for `QuoteMode.MINIMAL`, the first-character logic is:

```java
if (newRecord && (c < 0x20 || c > 0x21 && c < 0x23 ||
        c > 0x2B && c < 0x2D || c > 0x7E)) {
    quote = true;
}
```

The `c > 0x7E` condition causes every non-ASCII UTF-16 character, including `€` (`U+20AC`), to be quoted when it is the first character of a new record.

This is the reachable condition that explains the triggering failure.

### Required regression behavior

A regression test should verify all of the following:

1. With the default format, minimal quoting is in effect when no explicit quote mode is configured.
2. When `€` is the first character of the first field of a record, it is emitted unquoted.
3. The output should be:
   ```text
   €,Deux
   ```
   plus `\r\n` if testing a record-printing API that emits a record separator.
4. The result must not be:
   ```text
   "€",Deux
   ```

### Important control cases

To ensure the test is specifically checking the faulty branch rather than merely all Unicode output:

- **First field / new record:** `€` must remain unquoted. This is the direct regression case.
- **Subsequent field:** `ASCII,€` should also leave `€` unquoted; the supplied defect is specifically in the `newRecord` first-character condition.
- **Still quote actual CSV-special content:** a first field containing comma, quote, LF, or CR must remain quoted appropriately. For example:
  - `"a,b"` should be quoted;
  - `"a\"b"` should be quoted with doubled quotes;
  - `"a\nb"` should be quoted.
- **ASCII start-character behavior:** existing minimal-quote behavior for values beginning with characters that are parser-sensitive should remain as specified by the implementation/Javadocs. This is useful only if broader regression coverage is desired.

### Recommended direct test surface

The most direct public API test is:

- call `CSVFormat.DEFAULT.printRecord(StringBuilder, "€", "Deux")`;
- assert output is `€,Deux\r\n`.

This tests the target-class path through `printRecord -> print -> printAndQuote` without depending on the separately implemented `CSVPrinter`.

A complementary integration-style test may use the triggering surface (`CSVPrinter`) if that class is available in the test source tree.

---

## 9. Missing context required for compilable and meaningful broader tests

The supplied information is sufficient to design a focused Csv-15 regression test against `CSVFormat` using `StringBuilder`.

However, the following context is missing for reliable broad test generation:

1. **Existing test suite content**
   - The body of `CSVPrinterTest::testDontQuoteEuroFirstChar` is not supplied.
   - It would determine the project’s preferred assertion style and exact expected line-separator handling.

2. **`CSVPrinter` implementation**
   - Needed to test constructor behavior, automatic headers/comments, auto-flush behavior, close semantics, and printer integration precisely.

3. **`CSVParser` and `CSVRecord` implementations**
   - Needed to make meaningful assertions for `parse(Reader)`, header parsing, missing-column-name behavior, case-insensitive headers, null-string reading, and parsing-specific flags.

4. **`QuoteMode` declaration**
   - Its expected enum values are inferred from switch cases (`ALL`, `ALL_NON_NULL`, `MINIMAL`, `NON_NUMERIC`, `NONE`), but the actual class is not supplied.

5. **Project POM/dependency list**
   - Maven is identified, but no dependency list is supplied. In particular, no mocking library can be assumed for JDBC tests.

6. **Oracle for record-separator validation**
   - The Javadoc and implementation conflict for invalid record separators. An authoritative specification or existing test is required before asserting whether arbitrary separators should be accepted or rejected.

7. **Oracle for null arguments not documented by this class**
   - Reliable expectations are missing for null `Reader`, `Appendable`, `File`, `Path`, `Charset`, and explicitly null varargs arrays.

8. **Filesystem test conventions**
   - No temporary-directory strategy, Java source compatibility level, or permitted test utilities are supplied. This matters for `File`/`Path` output tests.

No production-source modification is needed for the focused bug test.