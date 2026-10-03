## 1. Public methods that should be tested

`QuotedPrintableCodec` exposes the following public API:

### Constructors
- `QuotedPrintableCodec()`
- `QuotedPrintableCodec(String charset)`

### Static codec methods
- `static byte[] encodeQuotedPrintable(BitSet printable, byte[] bytes)`
- `static byte[] decodeQuotedPrintable(byte[] bytes) throws DecoderException`

### Binary codec methods
- `byte[] encode(byte[] bytes)`
- `byte[] decode(byte[] bytes) throws DecoderException`

### String codec methods
- `String encode(String pString) throws EncoderException`
- `String encode(String pString, String charset) throws UnsupportedEncodingException`
- `String decode(String pString) throws DecoderException`
- `String decode(String pString, String charset) throws DecoderException, UnsupportedEncodingException`

### Object-dispatch codec methods
- `Object encode(Object pObject) throws EncoderException`
- `Object decode(Object pObject) throws DecoderException`

### Configuration accessor
- `String getDefaultCharset()`

Private helper `encodeQuotedPrintable(int, ByteArrayOutputStream)` is not directly testable and should be covered through public encoding methods.

---

## 2. Input types and valid input ranges

| Method family | Input type | Relevant ranges / forms |
|---|---|---|
| Byte encoding | `byte[]` | `null`, empty, all byte values from `-128` through `127` (unsigned octets `0`–`255`) |
| Static byte encoding | `BitSet`, `byte[]` | `BitSet` may be `null`; supplied bit positions relevant to byte octets `0`–`255`; byte array may be `null` |
| Byte decoding | `byte[]` | Raw printable octets, `=` escape sequences, literal CR (`13`) / LF (`10`), malformed escape sequences, `null`, empty |
| String encoding/decoding | `String`, charset name `String` | `null`, empty, ASCII text, non-ASCII text, supported charset names, unsupported charset names; constructor charset may also be `null` because it is not validated |
| Object overloads | `Object` | `null`, `byte[]`, `String`, and unsupported types such as `Integer` or `Object` |
| Constructors | Charset name `String` | Default UTF-8 constructor; supported charset; unsupported charset is accepted at construction and only matters when string methods use it; `null` is accepted at construction |

For quoted-printable escape syntax, valid hexadecimal digits include both upper- and lower-case hexadecimal characters where accepted by `Utils.digit16`, though the exact `Utils.digit16` implementation is not supplied. Encoding itself produces uppercase hexadecimal digits through `Character.toUpperCase`.

---

## 3. Conditions and reachable branches

### `encodeQuotedPrintable(BitSet, byte[])`

Reachable branches:

1. `bytes == null` → returns `null`.
2. `printable == null` → substitutes internal `PRINTABLE_CHARS`.
3. Each input byte is converted from signed Java `byte` to unsigned octet `0`–`255`.
4. If `printable.get(unsignedByte)` is true → writes the byte unchanged.
5. Otherwise → encodes the byte as `=HH`.

The source contains comments describing RFC line wrapping, trailing whitespace, and soft line breaks, but the supplied implementation does not contain executable logic for:
- maximum safe quoted-printable line length,
- inserting soft line breaks (`=\r\n`),
- encoding whitespace only when it appears at the end of a line,
- special final-line handling.

Those missing executable branches are directly relevant to this bug.

### `decodeQuotedPrintable(byte[])`

Reachable branches:

1. `bytes == null` → returns `null`.
2. A non-`=` byte → written directly to output by the current implementation.
3. A `=` byte:
   - reads two following bytes;
   - passes each to `Utils.digit16`;
   - writes the resulting decoded byte.
4. `=` at the final position or penultimate position:
   - array indexing fails;
   - caught as `ArrayIndexOutOfBoundsException`;
   - wrapped in `DecoderException("Invalid quoted-printable encoding", e)`.
5. `=` followed by non-hex characters:
   - behavior depends on `Utils.digit16`;
   - the supplied bug evidence shows it throws `DecoderException` for CR (`13`) when decoding a soft line break.
6. `=\r\n`:
   - currently enters the hexadecimal-decoding path instead of being treated as a soft line break;
   - this is a demonstrated bug-related branch.
7. Literal CR and LF:
   - despite the comment claiming they are excluded, the implementation writes them to output because there is no actual CR/LF conditional branch;
   - this behavior is relevant to `testSkipNotEncodedCRLF`.

### Instance byte methods

- `encode(byte[])` delegates to `encodeQuotedPrintable(PRINTABLE_CHARS, bytes)`.
- `decode(byte[])` delegates to `decodeQuotedPrintable(bytes)`.

### String methods

`encode(String)`:
1. `pString == null` → `null`.
2. Otherwise delegates to `encode(pString, getDefaultCharset())`.
3. `UnsupportedEncodingException` → wrapped as `EncoderException`.

`encode(String, String)`:
1. `pString == null` → `null`, without evaluating the charset.
2. Otherwise obtains source bytes with `pString.getBytes(charset)`.
3. Byte encoding occurs.
4. Encoded bytes are converted to US-ASCII with `StringUtils.newStringUsAscii`.

`decode(String)`:
1. `pString == null` → `null`.
2. Otherwise delegates to `decode(pString, getDefaultCharset())`.
3. `UnsupportedEncodingException` → wrapped as `DecoderException`.

`decode(String, String)`:
1. `pString == null` → `null`.
2. Converts the encoded text to US-ASCII bytes using `StringUtils.getBytesUsAscii`.
3. Decodes quoted-printable bytes.
4. Builds a Java `String` using the supplied charset.

### Object-dispatch methods

Both `encode(Object)` and `decode(Object)` have four branches:

1. `null` → `null`.
2. `byte[]` → corresponding binary overload.
3. `String` → corresponding string overload.
4. Any other type → exception:
   - `EncoderException` for `encode(Object)`;
   - `DecoderException` for `decode(Object)`.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

### Normal cases

- Printable ASCII bytes remain unescaped when using the default printable set.
- The literal `=` byte is not in the default printable set and should be encoded as `=3D`.
- Non-printable bytes and bytes above ASCII range should become uppercase `=HH` escapes.
- A valid `=HH` sequence should decode to the represented byte.
- Binary round-trip behavior should be tested for data not requiring bug-specific line-break transformations.
- String encoding/decoding should be tested with UTF-8, the default charset.
- A specifically selected non-UTF-8 supported charset should be tested through explicit charset overloads, if that charset is supported by the Java runtime used to execute the test.

### Boundary cases

- `null` byte arrays.
- Empty byte arrays.
- Empty strings.
- Lowest and highest signed byte values: `Byte.MIN_VALUE` and `Byte.MAX_VALUE`.
- Bytes around printable boundaries:
  - `8`, `9` (TAB), `10` (LF), `13` (CR), `31`, `32` (SPACE), `33`, `60`, `61` (`=`), `62`, `126`, `127`.
- A custom `BitSet` with:
  - no printable bytes,
  - all byte positions `0`–`255` set,
  - only a selected byte set.
- `=` at the start, middle, and end of input.
- Escape sequences with lower-case and upper-case hex digits, subject to the behavior of unavailable `Utils.digit16`.
- Inputs around the quoted-printable line-length boundary, especially lengths that require a soft line break under the intended RFC behavior.

### Invalid and exceptional cases

- Incomplete quoted-printable escapes:
  - `"="`
  - `"=A"`
  - byte equivalents.
  These should result in `DecoderException` according to the current method’s documented contract and explicit exception handling.
- Invalid hexadecimal escape digits:
  - examples such as `"=G0"` and `"=0G"`.
  The exact exception message must not be asserted without the `Utils.digit16` source, but a `DecoderException` is supported by the method contract and bug evidence.
- Unsupported explicit charset in:
  - `encode(String, String)` → `UnsupportedEncodingException`;
  - `decode(String, String)` → `UnsupportedEncodingException`.
- Unsupported default charset configured in constructor:
  - `encode(String)` wraps the unsupported-charset failure in `EncoderException`;
  - `decode(String)` wraps it in `DecoderException`.
- Unsupported object type:
  - `encode(Object)` → `EncoderException`;
  - `decode(Object)` → `DecoderException`.

### Null cases

Supported explicitly:

- `encodeQuotedPrintable(..., null)` → `null`.
- `decodeQuotedPrintable(null)` → `null`.
- `encode((byte[]) null)` → `null`.
- `decode((byte[]) null)` → `null`.
- `encode((String) null)` → `null`.
- `decode((String) null)` → `null`.
- `encode((Object) null)` → `null`.
- `decode((Object) null)` → `null`.
- `encode(null, charset)` and `decode(null, charset)` return `null` before attempting charset use.

Potential runtime-null cases not explicitly documented:

- `new QuotedPrintableCodec(null)` is allowed by the constructor.
- Calling default string overloads afterward may result in a runtime null-related failure when `String.getBytes(null)` or `new String(bytes, null)` is reached. The supplied source does not define this behavior as a checked codec exception.
- Explicit calls with a `null` charset may similarly fail at Java library level. Tests should not invent a checked exception contract for this case.

Because overloads exist, calls using an untyped `null`, such as `codec.encode(null)`, are ambiguous at compile time. Tests must cast null arguments, for example `codec.encode((String) null)`.

---

## 5. Required constructors, dependencies, and external objects

### Required construction

For normal instance tests:
- `new QuotedPrintableCodec()` for default UTF-8 behavior.
- `new QuotedPrintableCodec("UTF-8")` or another supported charset for configured-default behavior.
- `new QuotedPrintableCodec("invalid-charset-name")` to test deferred charset failure through the default string methods.

### Direct external dependencies visible in the source

- `org.apache.commons.codec.CharEncoding`
  - Needed for `CharEncoding.UTF_8`.
- `org.apache.commons.codec.DecoderException`
- `org.apache.commons.codec.EncoderException`
- `org.apache.commons.codec.binary.StringUtils`
  - Used by string methods.
- `org.apache.commons.codec.net.Utils`
  - Used by decoding to convert hex digits.
- Standard Java classes:
  - `BitSet`
  - `ByteArrayOutputStream`
  - `UnsupportedEncodingException`

No collaborator injection, filesystem access, networking, clock, or mockable external service is required.

For assertions:
- JUnit 4 assertion APIs.
- `assertArrayEquals` is appropriate for byte-array results.
- `BitSet` is required for direct testing of the static encoding method with custom printable character policies.

---

## 6. JUnit version and build tool

Supplied project context specifies:

- **JUnit:** `junit-4.12-hamcrest-1.3.jar`
- **Build tool:** Maven

Tests should therefore use JUnit 4 style, such as:
- `org.junit.Test`
- `org.junit.Assert`
- `@Test(expected = ...)` or explicit `try/catch` assertions when exception details need verification.

The triggering failure text includes `junit.framework.AssertionFailedError`, but that does not change the explicitly supplied JUnit 4.12 target environment.

---

## 7. Available test oracles

The supplied information provides these potential oracles:

1. **Javadoc/API contract in the target class**
   - Null-return behavior for most overloads is explicitly implemented and documented by behavior.
   - Exception types for malformed decoding, unsupported object types, and unsupported charsets are documented.
   - The class claims compliance with quoted-printable rules from RFC 1521.

2. **RFC 1521 reference in the class Javadoc**
   - The class documentation explicitly identifies quoted-printable encoding as governed by RFC 1521.
   - The source comments identify relevant quoted-printable concepts:
     - encoded bytes use `=HH`;
     - trailing whitespace must be encoded;
     - soft line breaks;
     - restrictions involving final encoded lines.

3. **Bug report / triggering test information**
   - Exact triggering test names:
     - `testSkipNotEncodedCRLF`
     - `testSoftLineBreakDecode`
     - `testSoftLineBreakEncode`
     - `testUltimateSoftBreak`
     - `testTrailingSpecial`
   - Runtime evidence:
     - decoding `=\r\n` currently attempts to parse CR (`13`) as a hex digit and throws `DecoderException`;
     - encoding is expected to introduce soft line breaks in cases where the current code does not;
     - expected encoded output in the triggering tests includes `=\r\n` sequences and `=3D` handling.

4. **Current source behavior**
   - Suitable as an oracle only for behavior explicitly documented and not implicated by the bug.
   - It must not be treated as the expected behavior for soft line breaks, ultimate soft breaks, trailing special characters, or CR/LF handling, because the bug report establishes failures in precisely those areas.

The actual source of `QuotedPrintableCodecTest`, the full JIRA description, and the fixed revision’s test/source diff are not supplied. Therefore, the exact expected output strings and exact boundary vectors used by existing tests are unavailable.

---

## 8. Bug-report-related behaviors that should be tested

The bug report identifies five behavior areas that require regression coverage.

### A. Decoding soft line breaks

`testSoftLineBreakDecode` failed because the decoder processed CR (`13`) after `=` as a hex digit:

> `DecoderException: Invalid URL encoding: not a valid digit (radix 16): 13`

Regression tests should verify that quoted-printable soft line breaks represented as:

```text
=\r\n
```

are recognized as a line-continuation marker and do not attempt hexadecimal digit parsing.

The intended decoded output should concatenate content on the two sides of the soft break, subject to confirmation from the original test or RFC interpretation.

### B. Handling unencoded CR/LF during decoding

`testSkipNotEncodedCRLF` also failed with a CR-as-hex-digit decoding error. The test name indicates that literal, non-encoded CR/LF handling is part of the expected behavior.

The current implementation comment says:

```java
// every other octet is appended except for CR & LF
```

but the code unconditionally writes all non-`=` bytes, including CR and LF. This discrepancy should be tested.

However, the supplied material does not provide the exact input/output vector for `testSkipNotEncodedCRLF`. It is therefore not possible to state with full certainty whether the intended behavior is:
- omit literal CR/LF entirely,
- normalize them,
- preserve hard line breaks,
- or distinguish them based on context.

The test name and source comment strongly indicate literal CR/LF should be skipped by this implementation’s intended contract, but the complete existing test is needed to make the expected result fully reliable.

### C. Encoding soft line breaks

`testSoftLineBreakEncode` failed because expected output contains a soft line-break marker:

```text
... expected:<...matics is the most b[=
...
```

The current encoder never inserts `=\r\n`, regardless of output line length. Regression tests should cover an input whose encoded representation exceeds the quoted-printable safe line length and assert proper soft-break insertion.

Exact line-length placement should be based on RFC 1521 and ideally verified against the unavailable original test. The target source comments refer to keeping space available for the final bytes and mention line-length-related rules, confirming this is intended functionality.

### D. Ultimate/final soft-break handling

`testUltimateSoftBreak` failed with expected output containing a terminal soft-break-related sequence:

```text
... expected:<...There is no end to i[=
...
```

This indicates a special encoding case near the end of encoded data. Tests should exercise final bytes when the output line has insufficient remaining capacity for a valid final quoted-printable representation.

The source comments explicitly mention:

```java
// note #3: '=' *must not* be the ultimate or penultimate character
// simplification: if < 6 bytes left, do a soft line break as we may need
// exactly 6 bytes space for the last 2 bytes
```

The code implementing this policy is absent in this buggy version. Exact expected output requires the missing original test or a confirmed RFC-derived test vector.

### E. Trailing special character handling

`testTrailingSpecial` failed with expected output including:

```text
... might contain sp=3D[
...
```

This shows that a trailing special byte, apparently `=`, must be escaped as `=3D`, and its placement relative to line wrapping/soft breaks must remain valid.

The regression suite should combine:
- a line-length boundary,
- an unsafe/special final character such as `=`,
- expected `=3D` escaping,
- and soft-break insertion when needed.

The current encoder does correctly escape `=`, but it lacks the required line-length and final-output handling.

---

## 9. Missing context required for fully reliable, compilable, and meaningful tests

The supplied source is sufficient to create compilable JUnit 4 tests for many API-level behaviors. However, the following context is missing for a fully reliable bug-regression suite with exact expected results:

1. **The complete existing `QuotedPrintableCodecTest` source**
   - Especially the five named triggering test methods.
   - This is needed to recover exact test inputs and exact expected strings.

2. **The full CODEC-121 bug report content**
   - Only summary metadata, failure names, and partial assertion output are supplied.
   - The detailed intended behavior, discussion, and reproduction cases are absent.

3. **The fixed-version diff or fixed implementation**
   - The fixed revision identifier is supplied, but its source must not be assumed or retrieved under the stated analysis constraint.
   - Without it, the exact algorithm for line wrapping and final-byte behavior cannot be confirmed from this prompt alone.

4. **Source for `org.apache.commons.codec.net.Utils`**
   - The decoder delegates hex parsing to `Utils.digit16`.
   - The supplied failure establishes that it throws `DecoderException` for CR, but exact behavior/messages for other invalid bytes, lower-case hex, negative byte values, or non-ASCII input cannot be fully established.

5. **Source/contract for `StringUtils`**
   - String conversion is delegated to `StringUtils.getBytesUsAscii` and `StringUtils.newStringUsAscii`.
   - Exact behavior for non-ASCII encoded input and unsupported/invalid values is not visible here.

6. **Maven project descriptor/dependency configuration**
   - Maven is specified, as is the JUnit artifact, but no `pom.xml` is supplied.
   - A test can reasonably target JUnit 4.12, but exact source/test Java levels and any project-specific test conventions are unknown.

7. **A definitive interpretation of literal CR/LF decoding**
   - The source comment says CR/LF should be excluded, while the current code preserves them.
   - The triggering test name supports skip behavior, but its exact expected output is not provided.
   - This is the main behavioral ambiguity that should be resolved before asserting a precise expected result.

In summary, the available information is sufficient to identify the relevant methods, branch coverage, exception cases, dependencies, and the broad regression targets. It is not sufficient to determine all exact expected encoded strings for the line-wrapping and end-of-line bug cases without relying on unavailable existing test vectors or additional bug-report details.