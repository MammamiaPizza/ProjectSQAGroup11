## Scope analyzed

Only the supplied buggy source version, `Csv-8b`, of:

- `org.apache.commons.csv.CSVFormat`

and the supplied project/bug metadata were analyzed. No assumptions are made about unprovided implementations of `CSVParser`, `CSVPrinter`, `CSVRecord`, `Quote`, Maven configuration, or existing test source.

---

## 1. Public methods that should be tested

### Factory method

| Method | Main test focus |
|---|---|
| `static CSVFormat newFormat(char delimiter)` | Valid delimiter construction; CR/LF rejection; initial/default property values. |

### Formatting and parsing

| Method | Main test focus |
|---|---|
| `String format(Object... values)` | Delegation to `CSVPrinter`; normal formatting, empty argument list, null values/varargs as supported by external printer behavior, quote/escape/null/record-separator configuration effects. |
| `CSVParser parse(Reader in) throws IOException` | Constructs/returns a parser using this format; behavior for valid readers, malformed input, null reader, and I/O failures depends partly on `CSVParser`, which is not supplied. |

### Accessors

| Method |
|---|
| `Character getCommentStart()` |
| `char getDelimiter()` |
| `Character getEscape()` |
| `String[] getHeader()` |
| `boolean getIgnoreEmptyLines()` |
| `boolean getIgnoreSurroundingSpaces()` |
| `String getNullString()` |
| `Character getQuoteChar()` |
| `Quote getQuotePolicy()` |
| `String getRecordSeparator()` |
| `boolean getSkipHeaderRecord()` |

Important accessor-specific test:

- `getHeader()` must return a defensive copy, not the internal array.
- Returned scalar/string/boxed-character values should reflect configuration established through factory/`with...` methods.

### State-query methods

| Method | Main test focus |
|---|---|
| `boolean isCommentingEnabled()` | True iff `commentStart != null`. |
| `boolean isEscaping()` | True iff `escape != null`. |
| `boolean isNullHandling()` | True iff `nullString != null`. |
| `boolean isQuoting()` | True iff `quoteChar != null`. |

### Object-contract methods

| Method | Main test focus |
|---|---|
| `boolean equals(Object obj)` | Reflexivity; null; unrelated type; equal independently-created formats; each differing configuration field. |
| `int hashCode()` | Equal formats must have equal hash codes; changing a field should generally be represented, though unequal objects need not have unequal hashes. |
| `String toString()` | Required/conditional content based on enabled configuration options; exact output is defined by this source and can be asserted if desired. |

### Immutable configuration methods

| Method |
|---|
| `CSVFormat withCommentStart(char commentStart)` |
| `CSVFormat withCommentStart(Character commentStart)` |
| `CSVFormat withDelimiter(char delimiter)` |
| `CSVFormat withEscape(char escape)` |
| `CSVFormat withEscape(Character escape)` |
| `CSVFormat withHeader(String... header)` |
| `CSVFormat withIgnoreEmptyLines(boolean ignoreEmptyLines)` |
| `CSVFormat withIgnoreSurroundingSpaces(boolean ignoreSurroundingSpaces)` |
| `CSVFormat withNullString(String nullString)` |
| `CSVFormat withQuoteChar(char quoteChar)` |
| `CSVFormat withQuoteChar(Character quoteChar)` |
| `CSVFormat withQuotePolicy(Quote quotePolicy)` |
| `CSVFormat withRecordSeparator(char recordSeparator)` |
| `CSVFormat withRecordSeparator(String recordSeparator)` |
| `CSVFormat withSkipHeaderRecord(boolean skipHeaderRecord)` |

For each `with...` method, tests should verify:

1. It returns a new `CSVFormat` instance rather than mutating the original.
2. The designated property changes.
3. All unrelated properties are preserved.
4. Documented validation/rejection behavior occurs where applicable.

---

## 2. Input types and valid input ranges

### Character inputs

| Input/methods | Type | Valid range according to supplied source |
|---|---|---|
| Delimiter: `newFormat`, `withDelimiter` | primitive `char` | Any Java `char` except LF (`'\n'`) and CR (`'\r'`). |
| Comment start: primitive overload | primitive `char` | Any `char` except LF and CR. |
| Comment start: boxed overload | `Character` | Any non-line-break character, or `null` to disable comments. |
| Escape: primitive overload | primitive `char` | Any `char` except LF and CR. |
| Escape: boxed overload | `Character` | Any non-line-break character, or `null` to disable escaping. |
| Quote character: primitive overload | primitive `char` | Any `char` except LF and CR. |
| Quote character: boxed overload | `Character` | Any non-line-break character, or `null` to disable quoting. |
| Record separator char overload | primitive `char` | No validation in this class; all Java `char` values are accepted and converted to a one-character `String`. |

Although configurations where delimiter equals quote/escape/comment are constructible through `with...` methods, they are considered inconsistent by package-private `validate()`.

### Boolean inputs

| Methods | Valid values |
|---|---|
| `withIgnoreEmptyLines(boolean)` | `true`, `false` |
| `withIgnoreSurroundingSpaces(boolean)` | `true`, `false` |
| `withSkipHeaderRecord(boolean)` | `true`, `false` |

### String inputs

| Method | Valid values from supplied source |
|---|---|
| `withNullString(String)` | Any string, including empty string and `null`. `null` disables null-string conversion. |
| `withRecordSeparator(String)` | Any string, including empty string and `null`; no validation in `CSVFormat`. |
| `withHeader(String...)` | `null` varargs array means header support disabled; an empty array means automatically parse header according to Javadoc; otherwise zero or more names. Individual null elements are not prohibited by this class. |

### Quote policy input

| Method | Type | Valid values |
|---|---|---|
| `withQuotePolicy(Quote)` | `Quote` | Any available `Quote` enum/value or `null`; this class does not reject null. A relevant validation rule applies specifically when policy is `Quote.NONE`. The full `Quote` declaration is not supplied. |

### Other inputs

| Method | Input | Information available |
|---|---|---|
| `format(Object... values)` | Arbitrary object values | Values are passed to `CSVPrinter`. Exact conversion and exceptional behavior are unavailable without `CSVPrinter`. |
| `parse(Reader in)` | `Reader` | Any `Reader` supported by `CSVParser`; null and I/O semantics cannot be established from this source alone. |
| `equals(Object)` | Any reference, including null | Fully determinable from supplied source. |

---

## 3. Conditions and reachable branches

### Factory/constructor validation

The private constructor rejects a delimiter that is:

- `'\n'` / LF
- `'\r'` / CR

This is reachable through:

- `newFormat`
- `withDelimiter`
- static initialization only if constants were invalid, which is not indicated.

### Character-setting methods

For the boxed `Character` forms of comment, escape, and quote:

- `null`: accepted and disables the feature.
- LF or CR: throws `IllegalArgumentException`.
- Any other character: accepted.

Primitive overloads delegate to the corresponding boxed overload, so they share those branches.

### Header copying and immutability

The constructor has two branches:

- `header == null`: stores `null`.
- `header != null`: stores `header.clone()`.

`getHeader()` likewise has two branches:

- Internal header is `null`: returns `null`.
- Internal header exists: returns a clone.

Tests should cover mutation of:

1. the caller’s original input header array after `withHeader(...)`, and
2. the array obtained from `getHeader()`.

Neither mutation should alter the format.

### `equals(Object)`

Reachable branches include:

- Same reference: `true`.
- Null: `false`.
- Different runtime class: `false`.
- Same class but differing fields: `false`.
- Equal values, including null/non-null pair handling: `true`.

Each configuration field participates in equality:

- delimiter
- quote policy
- quote character
- comment marker
- escape character
- null string
- header array contents/order
- surrounding-space option
- empty-line option
- skip-header option
- record separator

### Boolean state methods

Each has true and false branches based on nullness of its related field:

- `isCommentingEnabled()`
- `isEscaping()`
- `isNullHandling()`
- `isQuoting()`

### `toString()`

Conditional output branches exist for:

- Escape configured/not configured.
- Quote character configured/not configured.
- Comment start configured/not configured.
- Null-string configured/not configured.
- Record separator configured/not configured.
- Empty-line ignoring enabled/disabled.
- Surrounding-space ignoring enabled/disabled.
- Header configured/not configured.

The delimiter and `SkipHeaderRecord` fragments are always included.

### Package-private `validate()`

`validate()` is not public but is central to the reported defect. Its reachable checks are:

1. Quote character equals delimiter.
2. Escape character equals delimiter.
3. Comment character equals delimiter.
4. Quote character equals comment character.
5. Escape character equals comment character.
6. `quotePolicy == Quote.NONE` while `escape == null`.
7. Header is non-null and has duplicate values.
8. Valid configuration with none of the above conflicts.

All current branches in this source throw `IllegalStateException` for inconsistencies.

The duplicate-header branch is:

```java
if (header != null) {
    final Set<String> set = new HashSet<String>(header.length);
    set.addAll(Arrays.asList(header));
    if (set.size() != header.length) {
        throw new IllegalStateException(...);
    }
}
```

This identifies duplicates according to `HashSet`/`String` equality, including repeated `null` header entries.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

### Normal cases

- Create a format through `newFormat(',')`.
- Use every `with...` method with valid values.
- Verify predefined formats’ documented settings:
  - `DEFAULT`
  - `RFC4180`
  - `EXCEL`
  - `TDF`
  - `MYSQL`
- Verify immutability: configuration methods must not alter their receiver.
- Verify getters and state-query methods.
- Verify equal separately-created configurations are equal and share hash codes.
- Verify parse/format integration only to the extent supported by the project’s supplied external classes.

### Boundary cases

- Delimiter equal to CR and LF: both must be rejected.
- Comment, escape, and quote character equal to CR and LF: both must be rejected.
- Empty header: `withHeader()` / empty header array. Javadoc says this requests automatic header parsing.
- `withHeader((String[]) null)`: Javadoc says `null` disables headers.
- Empty string as null string.
- Empty string as record separator.
- Null record separator.
- Single header.
- Duplicate headers adjacent and non-adjacent, such as `{"a", "a"}` and `{"a", "b", "a"}`.
- Potential duplicate null header values, such as `{null, null}`. The implementation’s duplicate-detection code treats this as a duplicate, but whether this is part of the intended public contract is not explicitly documented.

### Invalid/exceptional cases fully determined by this source

| Case | Current behavior in Csv-8b |
|---|---|
| `newFormat('\n')` / `newFormat('\r')` | `IllegalArgumentException` |
| `withDelimiter('\n')` / `withDelimiter('\r')` | `IllegalArgumentException` |
| `withCommentStart('\n')` / `withCommentStart('\r')` | `IllegalArgumentException` |
| `withEscape('\n')` / `withEscape('\r')` | `IllegalArgumentException` |
| `withQuoteChar('\n')` / `withQuoteChar('\r')` | `IllegalArgumentException` |
| Invalid cross-character configurations when `validate()` is reached | Currently `IllegalStateException` |
| Duplicate headers when `validate()` is reached | Currently `IllegalStateException`, but this conflicts with the supplied bug oracle, which requires `IllegalArgumentException`. |

### Null cases

| Input | What can be concluded |
|---|---|
| `withCommentStart((Character) null)` | Supported; disables comments. |
| `withEscape((Character) null)` | Supported; disables escaping. |
| `withQuoteChar((Character) null)` | Supported; disables quoting. |
| `withNullString(null)` | Supported; disables null conversion. |
| `withRecordSeparator((String) null)` | Accepted by this class. |
| `withHeader((String[]) null)` | Supported according to Javadoc; getter returns null. |
| `withQuotePolicy(null)` | Accepted by this class; no supplied contract says whether downstream use is valid. |
| `parse(null)` | Not determinable without `CSVParser`. |
| `format((Object[]) null)` | Not determinable without `CSVPrinter`; it is distinct from formatting one null value. |
| `equals(null)` | Returns false. |

### Cases whose expected result is insufficiently specified

Reliable expected results cannot be determined solely from this prompt for:

- Exact CSV output from `format(...)`, because formatting is delegated to the unprovided `CSVPrinter`.
- Exact parsing behavior, record contents, header mapping, parser lifecycle, and malformed-input behavior, because `CSVParser` and `CSVRecord` are not supplied.
- Whether `parse(null)` throws `NullPointerException`, another exception, or succeeds until later use.
- Exact behavior for a null quote policy when formatting/parsing, because `Quote` and downstream consumers are not supplied.
- Whether arbitrary/null header names are intended to be supported, other than the observable behavior of the supplied `validate()` implementation.

---

## 5. Required constructors, dependencies, and external objects

### Construction

`CSVFormat` has no public constructor. Instances must be obtained through:

- `CSVFormat.newFormat(char)`
- Predefined public constants:
  - `CSVFormat.DEFAULT`
  - `CSVFormat.RFC4180`
  - `CSVFormat.EXCEL`
  - `CSVFormat.TDF`
  - `CSVFormat.MYSQL`
- Chaining public `with...` methods from an existing instance.

The actual constructor is private.

### Direct dependencies visible in the source

| Dependency | Usage |
|---|---|
| `org.apache.commons.csv.Constants` | Static characters/strings used to define defaults and line-break validation. |
| `org.apache.commons.csv.Quote` | Quote policy type and `Quote.NONE` validation case. |
| `org.apache.commons.csv.CSVPrinter` | Used by `format(Object...)`. |
| `org.apache.commons.csv.CSVParser` | Constructed by `parse(Reader)`. |
| `java.io.Reader` | Input to `parse`. |
| `java.io.StringWriter` | Internally used by `format`. |
| `java.io.IOException` | Declared by `parse`, caught in `format`. |
| `java.util.Arrays`, `HashSet`, `Set` | Equality, hash code, header cloning/display, duplicate-header validation. |

### Test-package requirement for `validate()`

`validate()` has package-private visibility:

```java
void validate() throws IllegalStateException
```

A direct unit test of `validate()` must be placed in package:

```java
org.apache.commons.csv
```

However, tests of the public API should preferably exercise validation through the actual public operation that invokes it. That invocation cannot be identified from the supplied `CSVFormat` source alone; it likely involves `CSVParser`, but its implementation was not provided.

---

## 6. JUnit version and build tool

Supplied project metadata states:

- **JUnit:** `junit-4.11.jar`
- **Build tool:** Maven

Thus future tests should use JUnit 4.11-compatible constructs, such as:

- `@Test`
- `@Test(expected = IllegalArgumentException.class)` where suitable
- `Assert.assertEquals`, `assertTrue`, `assertFalse`, `assertNull`, `assertNotSame`, etc.

The actual `pom.xml`, Maven source/test directories, Java source level, and dependency versions are not supplied. Maven is explicitly identified, but the precise test compilation configuration cannot be independently verified from the prompt.

---

## 7. Available test oracles

### Strongest oracle: supplied bug report / triggering-test result

The supplied defect information is explicit:

- Bug report: **CSV-114**
- Triggering test: `org.apache.commons.csv.CSVFormatTest::testDuplicateHeaderElements`
- Expected exception: **`IllegalArgumentException`**
- Actual exception in `Csv-8b`: **`IllegalStateException`**
- Modified production source: `org.apache.commons.csv.CSVFormat`

This is a reliable oracle for the defect behavior:

> Duplicate header elements must result in `IllegalArgumentException` at the operation/path exercised by the original triggering test, rather than `IllegalStateException`.

### Javadoc within `CSVFormat`

The supplied class Javadoc is an oracle for:

- Immutability.
- Meaning of built-in formats.
- Header configuration semantics.
- Null disabling for comment/escape/quote characters.
- Null-string behavior.
- Constraints against using CR/LF as delimiter/comment/escape/quote.
- Header array behavior: null disables headers; empty requests automatic parsing; non-empty specifies names.

### Source-level behavioral oracle

The implementation itself can be used to establish observable current behavior, especially for:

- Getters.
- Equality/hash code.
- `toString()`.
- Defensive header copying.
- Predefined format configurations.
- CR/LF validation.

However, the current implementation must **not** be treated as the oracle where it conflicts with the supplied bug report. The duplicate-header exception type is exactly such a conflict.

### Missing existing test source

The body of `CSVFormatTest::testDuplicateHeaderElements` was not supplied. Therefore, the exact public method chain and input used by the original test cannot be reconstructed with certainty.

---

## 8. Bug-report behaviors that should be tested

The defect-specific tests should establish all of the following:

1. **Duplicate header names are rejected.**
   - Example categories: duplicate adjacent names and duplicate separated names.
   - The exact concrete input should mirror the original test if that source is made available under the experimental protocol; it is not available here.

2. **The exception type must be `IllegalArgumentException`.**
   - This is the key regression assertion.
   - A test that only checks “some exception” would not detect Csv-8b’s defect.

3. **The validation must be exercised through the relevant supported path.**
   - The supplied class itself does not call `validate()` from a public method.
   - `validate()` is package-private.
   - The original triggering test confirms that an execution path exists that reaches it, but the supplied information does not identify whether this was parser construction, parser iteration, or another package-level interaction.

4. **Nonduplicate headers should remain accepted through the same path.**
   - This distinguishes a correct duplicate-header validation fix from an overbroad rejection of all headers.

5. **Null/empty header handling should not be conflated with ordinary duplicate-name handling.**
   - `null` header array and an empty header array have explicitly documented meanings.
   - Repeated null elements are detected as duplicates by the current implementation, but this exact intended contract is not explicitly stated in the supplied Javadoc; such a test would need caution unless supported by existing tests/specification.

### Current defect location

In `Csv-8b`, duplicate-header validation currently throws:

```java
throw new IllegalStateException(
    "The header contains duplicate names: " + Arrays.toString(header));
```

The bug metadata says the expected exception is `IllegalArgumentException`. Therefore, tests must be designed so that Csv-8b fails specifically due to receiving `IllegalStateException`.

---

## 9. Missing context required for compilable and meaningful tests

The supplied information is sufficient to design many unit tests for configuration, getters, immutability, equality, and the documented character validations. It is insufficient to reliably generate all integration-level tests or to reproduce the original bug trigger exactly.

### Missing items

1. **`CSVParser` source/API behavior**
   - Needed to determine how/when `CSVFormat.validate()` is invoked.
   - Needed for reliable `parse(Reader)` behavior and exception assertions.
   - Needed to determine the publicly reachable route for duplicate-header validation.

2. **`CSVPrinter` source/API behavior**
   - Needed for exact expected strings from `format(Object...)`.
   - Needed for null values, escaping, quote policies, record separators, and possible exceptions.

3. **`Quote` declaration**
   - Needed to enumerate policy values and determine expected formatting semantics.
   - Only `Quote.NONE` is known from this source.

4. **The actual triggering test body**
   - Needed to reproduce the exact intended public API sequence for `testDuplicateHeaderElements`.
   - The test name and expected/actual exception types are provided, but not its setup or invocation.

5. **Project `pom.xml` / Maven test configuration**
   - Maven and JUnit 4.11 are stated, but exact source level, artifact coordinates, test source path, and dependency configuration are not supplied.

6. **Existing project tests**
   - They may contain established conventions and assertions for `CSVFormat`, especially formatting and parser behavior. Only one test name is supplied.

---

## Testability conclusion

A meaningful test plan can already cover:

- Format creation and configuration.
- All getters.
- Immutable-copy behavior for headers.
- Boolean capability methods.
- CR/LF validation.
- Equality/hash code/toString behavior.
- Predefined format properties.
- The package-private validation rules if tests are in `org.apache.commons.csv`.
- The known regression requirement that duplicate headers must produce `IllegalArgumentException`, not `IllegalStateException`.

However, generating a fully reliable, compilable regression test that mirrors the original public bug trigger requires the missing `CSVParser` behavior or the original `testDuplicateHeaderElements` source. Without that context, direct testing of `validate()` is possible only from the same package, but it would test a non-public method rather than conclusively demonstrate the original public-facing path.