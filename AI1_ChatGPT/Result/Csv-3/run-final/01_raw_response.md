## 1. Public methods to test

`org.apache.commons.csv.Lexer` is an abstract, package-private class. It exposes **no public methods**.

Its constructor and operational methods are package-private, so direct tests would need to be in package `org.apache.commons.csv`.

Testable package-visible methods:

- `Lexer(CSVFormat format, ExtendedBufferedReader in)`
- `long getLineNumber()`
- `int readEscape() throws IOException`
- `void trimTrailingSpaces(StringBuilder buffer)`
- `boolean readEndOfLine(int c) throws IOException`
- `boolean isWhitespace(int c)`
- `boolean isStartOfLine(int c)`
- `boolean isEndOfFile(int c)`
- `boolean isDelimiter(int c)`
- `boolean isEscape(int c)`
- `boolean isQuoteChar(int c)`
- `boolean isCommentStart(int c)`

Abstract method:

- `Token nextToken(Token reusableToken) throws IOException`

Because `Lexer` is abstract, it cannot be instantiated without either:
- a supplied concrete lexer implementation, or
- a test-local subclass implementing `nextToken`.

However, a meaningful test-local subclass and construction of `ExtendedBufferedReader` cannot be confirmed from the provided context because the required constructors/API are not supplied.

---

## 2. Input types and valid input ranges

| Method / constructor | Inputs | Validity / notable range |
|---|---|---|
| `Lexer` constructor | `CSVFormat`, `ExtendedBufferedReader` | `format` must effectively be non-null because it is dereferenced immediately. `in` is stored and used later; null will fail when methods access it. |
| `getLineNumber` | None | Depends on `ExtendedBufferedReader.getLineNumber()`. |
| `readEscape` | None directly | Requires the reader to be positioned immediately after an already-consumed configured escape character. This is a documented caller precondition; the method does not validate it. |
| `trimTrailingSpaces` | `StringBuilder` | Non-null mutable buffer. Empty and whitespace-only buffers are valid. Null causes `NullPointerException`. |
| `readEndOfLine` | `int c` | Intended for character values, especially `CR`, `LF`, and other reader-returned integer values such as `END_OF_STREAM`. |
| `isWhitespace` | `int c` | Any `int`; internally cast to `char` after checking delimiter equality. Normal character-domain use is expected. |
| `isStartOfLine` | `int c` | Any `int`; meaningful values include `CR`, `LF`, and `UNDEFINED`. |
| `isEndOfFile` | `int c` | Any `int`; true only for `END_OF_STREAM`. |
| delimiter/escape/quote/comment predicates | `int c` | Any `int`; compared against the configured character or the internal disabled sentinel. |
| `nextToken` | `Token reusableToken` | Contract not supplied beyond the signature. Null validity and token lifecycle cannot be determined. |

`CSVFormat` configuration values used by the constructor:

- delimiter: obtained through `format.getDelimiter()`
- escape: nullable `Character` from `format.getEscape()`
- quote character: nullable `Character` from `format.getQuoteChar()`
- comment-start character: nullable `Character` from `format.getCommentStart()`
- booleans:
  - `format.getIgnoreSurroundingSpaces()`
  - `format.getIgnoreEmptyLines()`

When escape, quote, or comment-start is `null`, the constructor maps it to the private sentinel `'\ufffe'`, making the corresponding predicate false for ordinary input characters.

---

## 3. Conditions and reachable branches

### Constructor

Reachable configuration branches:

- Escape is non-null vs. null.
- Quote character is non-null vs. null.
- Comment-start character is non-null vs. null.
- `ignoreSurroundingSpaces` true vs. false.
- `ignoreEmptyLines` true vs. false.

The latter booleans are stored but not used by the shown concrete methods.

### `readEscape()`

After `in.read()`, the branches are:

1. `'r'` → returns `CR`
2. `'n'` → returns `LF`
3. `'t'` → returns `TAB`
4. `'b'` → returns `BACKSPACE`
5. `'f'` → returns `FF`
6. Actual `CR` → returns `CR`
7. Actual `LF` → returns `LF`
8. Actual `FF` → returns `FF`
9. Actual `TAB` → returns `TAB`
10. Actual `BACKSPACE` → returns `BACKSPACE`
11. `END_OF_STREAM` → throws `IOException` with message:
    - `"EOF whilst processing escape sequence"`
12. Any other value → returns that value unchanged in the supplied source.

The Javadoc says it may return `END_OF_STREAM` “if char following the escape is invalid,” but the supplied implementation’s default branch returns any otherwise-unhandled character. The source does not define which escaped characters, if any, are “invalid.”

### `trimTrailingSpaces(StringBuilder)`

Branches:

- Empty buffer: no modification.
- Last character is not whitespace: no modification.
- One or more trailing `Character.isWhitespace` characters: removes all consecutive trailing whitespace.
- Internal or leading whitespace is retained.
- Null buffer: implicit `NullPointerException`.

### `readEndOfLine(int c)`

Branches:

- `c == CR` and `in.lookAhead() == LF`:
  - consumes the LF through `in.read()`;
  - returns `true`.
- `c == CR` and next character is not LF:
  - does not consume another character;
  - returns `true`.
- `c == LF`:
  - returns `true`;
  - does not read further.
- Any other value:
  - returns `false`;
  - does not read further.

### Predicate methods

- `isWhitespace(c)`:
  - false when `c` equals configured delimiter, even if that delimiter is a Unicode whitespace character;
  - otherwise follows `Character.isWhitespace((char) c)`.

- `isStartOfLine(c)`:
  - true for `LF`, `CR`, or `UNDEFINED`;
  - false otherwise.

- `isEndOfFile(c)`:
  - true only for `END_OF_STREAM`.

- `isDelimiter`, `isEscape`, `isQuoteChar`, `isCommentStart`:
  - each returns true only when the input matches the corresponding configured/internal character.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

### Normal cases

- Construct with a valid `CSVFormat` and usable `ExtendedBufferedReader`.
- Read each documented escape sequence:
  - `\r`, `\n`, `\t`, `\b`, `\f`
- Read literal control characters after an escape:
  - actual CR, LF, FF, TAB, BACKSPACE
- Trim trailing whitespace from a non-empty buffer.
- Recognize LF, CR, and CRLF line endings.
- Verify configured delimiter, escape, quote, and comment-start recognition.
- Verify disabled escape/quote/comment settings do not match normal characters.

### Boundary cases

- Empty input after an escape character.
- Empty `StringBuilder`.
- Buffer consisting solely of whitespace.
- Buffer with a single whitespace character.
- CR at end of input.
- CR followed by a non-LF character.
- CRLF, ensuring LF is consumed exactly once.
- `UNDEFINED`, `END_OF_STREAM`, and normal non-control characters passed to the predicate methods.
- A delimiter that is itself whitespace, to verify `isWhitespace` excludes delimiters.

### Invalid / unspecified cases

The following cannot be assigned reliable expected behavior beyond what the implementation mechanically does:

- An escape followed by a non-special character, such as `\N`, `\x`, `\/`, or `\"`:
  - The current implementation returns that following character.
  - The Javadoc refers to an “invalid” escaped character but does not define invalid characters.
  - The bug report strongly suggests this area is incorrect, but does not provide the exact expected behavior or input data.
- Calling `readEscape()` when the previous character was not an escape:
  - Violates the documented precondition.
- Calling `nextToken(null)`:
  - No contract or implementation is supplied.
- Constructing with a null `ExtendedBufferedReader`:
  - Construction itself succeeds if `format` is non-null, but subsequent reader access throws `NullPointerException`.

### Null cases

- `new Lexer(null, in)`:
  - Expected to fail immediately with `NullPointerException` due to `format.getDelimiter()`.
- `new Lexer(format, null)`:
  - Constructor itself does not dereference `in`; later calls such as `getLineNumber`, `readEscape`, or `readEndOfLine` fail with `NullPointerException`.
- `trimTrailingSpaces(null)`:
  - Fails with `NullPointerException`.
- Nullable format characters:
  - `format.getEscape() == null`
  - `format.getQuoteChar() == null`
  - `format.getCommentStart() == null`
  - These are explicitly supported and mapped to the disabled sentinel.

### Exceptional cases

- `readEscape()` throws `IOException` if `in.read()` returns `END_OF_STREAM`.
- `readEscape()` may propagate an `IOException` from `ExtendedBufferedReader.read()`.
- `readEndOfLine()` may propagate an `IOException` from `lookAhead()` or `read()`.

---

## 5. Required constructors, dependencies, and external objects

To directly test concrete `Lexer` behavior, the test requires:

1. **`CSVFormat`**
   - Required by the `Lexer` constructor.
   - Its creation API, predefined format constants, builder APIs, and validation behavior are not included.

2. **`ExtendedBufferedReader`**
   - Required by the `Lexer` constructor.
   - Needed to control:
     - `read()`
     - `lookAhead()`
     - `getLastChar()`
     - `getLineNumber()`
   - Its constructor(s), visibility, and behavior are not provided.

3. **A concrete `Lexer`**
   - `Lexer` is abstract because of `nextToken(Token)`.
   - A test-local subclass could theoretically implement `nextToken`, but this requires knowing enough about `Token` and whether the constructor is accessible.
   - A production concrete subclass is not supplied.

4. **`Token`**
   - Required only to satisfy the abstract `nextToken` signature if a test-local subclass is used.
   - No `Token` API or constructor is supplied.

5. **Constants**
   - `BACKSPACE`, `CR`, `END_OF_STREAM`, `FF`, `LF`, `TAB`, and `UNDEFINED`.
   - Their names and usage are visible, but their exact values are not supplied, except comments imply `END_OF_STREAM` is `-1`.

For parser-level regression coverage, the missing relevant dependency is:

6. **`CSVParser`**
   - One listed triggering test is `CSVParserTest::testBackslashEscaping`.
   - No parser constructor, parse API, record API, or CSV-format setup is supplied.

---

## 6. JUnit version and build tool

Supplied project context states:

- **JUnit version:** `junit-4.11.jar`
- **Build tool:** Maven

The triggering failure text includes `junit.framework.AssertionFailedError`, which is a JUnit 3-style assertion type/package. This does not contradict the supplied JUnit 4.11 dependency, because JUnit 4 retains compatibility classes, but it indicates that existing tests may use legacy JUnit 3-style assertions or inheritance.

No `pom.xml`, Surefire configuration, source/target Java version, or project test conventions are included.

---

## 7. Available test oracles

The supplied information provides the following limited oracles:

### Source-level contracts

- `readEscape()` Javadoc:
  - It processes an escape sequence.
  - EOF after the escape must throw `IOException`.
  - It says `END_OF_STREAM` may indicate that the following character is invalid, but does not define invalidity.
- `readEndOfLine()` Javadoc:
  - It accepts `\n`, `\r`, and `\r\n`.
  - For CRLF, it consumes the LF.
- `isStartOfLine()` Javadoc:
  - CR, LF, and start-of-file (`UNDEFINED`) count as start of line.
- `isWhitespace()` Javadoc:
  - It identifies whitespace, excluding the delimiter according to the implementation.
- Constructor behavior for null escape/quote/comment settings is directly visible.

### Bug-report-derived information

- Bug report: **CSV-58**
- Fixed revision: `2c6120826245f89fedf2f936ab4a0c3edd8717f3`
- Modified production file: `Lexer`
- Triggering tests:
  - `CSVLexerTest::testEscapedMySqlNullValue`
  - `CSVLexerTest::testEscapedCharacter`
  - `CSVParserTest::testBackslashEscaping`

### Failure output

The parser failure establishes at least one expected output fragment:

```text
expected: ... "quoted "" [/]" / string" ...
but was:   ... "quoted "" []" / string" ...
```

This shows that, under an unspecified parsing configuration and unspecified input, a character expected in a quoted parsed value was omitted by the buggy version.

However, the complete input, complete expected records, `CSVFormat` configuration, and assertions are not provided. Therefore, this fragment alone is not enough to create a reliable parser regression test.

---

## 8. Bug-report-related behavior that should be tested

Based strictly on the supplied bug metadata and triggering test names, regression coverage should address:

1. **Escaped MySQL null value**
   - The exact behavior of an escaped MySQL null marker must be tested.
   - The likely relevant value is suggested by the test name, but the actual input, expected parsed field, and MYSQL format configuration are not supplied.
   - In particular, expected handling of a sequence such as an escape followed by `N` cannot be safely inferred solely from this prompt.

2. **Escaped non-control / nonstandard characters**
   - `CSVLexerTest::testEscapedCharacter` indicates that a character escaped with the configured escape character requires regression coverage.
   - The escaped character and desired output are not supplied.

3. **Backslash escaping during parser integration**
   - `CSVParserTest::testBackslashEscaping` indicates that lexer behavior must preserve or decode backslash sequences correctly when used by the parser.
   - The failure demonstrates that a character was lost from a quoted field in the buggy version.
   - Exact parser input, format, and expected record values are missing.

4. **EOF after an escape**
   - This is explicitly specified in `readEscape()` and should be tested independently of CSV-58:
     - reader returns EOF immediately after the escape;
     - `readEscape()` must throw `IOException`.

5. **Known supported escape translations**
   - `r`, `n`, `t`, `b`, and `f` should produce their corresponding control characters.
   - Actual control characters immediately following the escape should be returned as written.

A key caution: the current implementation’s default `readEscape()` behavior is to return every other character unchanged. The comment:

```java
// Now check for meta-characters
return c;
```

suggests intended logic may be incomplete or incorrect, especially in view of the bug report, but the supplied material does not define the expected classification of “meta-characters” or invalid escape sequences. A test must not invent that policy.

---

## 9. Missing context required for compilable and meaningful tests

The supplied information is insufficient to generate reliable, compilable regression tests for CSV-58. The following context is needed:

1. **The source/API of `ExtendedBufferedReader`**
   - Constructor signature(s).
   - Whether it can wrap a `StringReader`.
   - Exact behavior of `read()`, `lookAhead()`, `getLastChar()`, and line-number tracking.

2. **The source/API of `CSVFormat`**
   - How to construct/configure formats.
   - Availability and definition of a MySQL format.
   - How escape, delimiter, quote, comment, null string, and parser options are configured.

3. **The source/API of `Token`**
   - Needed if a test-local concrete `Lexer` subclass is created.

4. **A concrete lexer implementation or existing lexer tests**
   - The target is abstract.
   - Existing `CSVLexerTest` source would establish the intended package test setup and how lexer behavior is exercised.

5. **The complete triggering test methods**
   - `CSVLexerTest.testEscapedMySqlNullValue`
   - `CSVLexerTest.testEscapedCharacter`
   - `CSVParserTest.testBackslashEscaping`
   - These are the strongest available regression oracle, but only names and partial failure output are supplied.

6. **The CSV-58 issue content or fixed-version behavioral change**
   - The bug report URL is supplied, but its content is not.
   - The fixed commit identifier is supplied, but no diff or fixed source is supplied.
   - Without one of these, the intended handling of invalid/non-meta escaped characters cannot be determined reliably.

7. **`CSVParser` API and record assertions**
   - Needed to reproduce the parser-level failure and confirm parsed output.

8. **Relevant project build configuration**
   - Maven `pom.xml` or the relevant test dependency/build configuration would confirm test source layout, Java version, and whether additional test dependencies are needed.

## Conclusion

Direct unit testing of basic helper behavior is conceptually possible, but compiling such tests requires at least the `ExtendedBufferedReader` and `CSVFormat` APIs. More importantly, reliable CSV-58 regression tests cannot yet be specified because the exact expected behavior for the failing escaped-character cases is absent. The triggering test source, bug-report details, or fixed-version diff is required to establish a non-invented test oracle.