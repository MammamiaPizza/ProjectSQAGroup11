## 1. Public methods to test

`org.apache.commons.codec.binary.StringUtils` exposes the following public static methods:

| Method | Input type(s) | Result |
|---|---|---|
| `equals(CharSequence cs1, CharSequence cs2)` | Two `CharSequence` values | `boolean` |
| `getByteBufferUtf8(String string)` | `String` | `ByteBuffer` |
| `getBytesIso8859_1(String string)` | `String` | `byte[]` |
| `getBytesUnchecked(String string, String charsetName)` | `String`, charset-name `String` | `byte[]` |
| `getBytesUsAscii(String string)` | `String` | `byte[]` |
| `getBytesUtf16(String string)` | `String` | `byte[]` |
| `getBytesUtf16Be(String string)` | `String` | `byte[]` |
| `getBytesUtf16Le(String string)` | `String` | `byte[]` |
| `getBytesUtf8(String string)` | `String` | `byte[]` |
| `newString(byte[] bytes, String charsetName)` | `byte[]`, charset-name `String` | `String` |
| `newStringIso8859_1(byte[] bytes)` | `byte[]` | `String` |
| `newStringUsAscii(byte[] bytes)` | `byte[]` | `String` |
| `newStringUtf16(byte[] bytes)` | `byte[]` | `String` |
| `newStringUtf16Be(byte[] bytes)` | `byte[]` | `String` |
| `newStringUtf16Le(byte[] bytes)` | `byte[]` | `String` |
| `newStringUtf8(byte[] bytes)` | `byte[]` | `String` |

Private helper methods should be covered indirectly through these public methods:

- `getBytes(String, Charset)`
- `getByteBuffer(String, Charset)`
- `newString(byte[], Charset)`
- `newIllegalStateException(String, UnsupportedEncodingException)`

---

## 2. Input types and valid input ranges

### `equals(CharSequence, CharSequence)`

- Both arguments accept any `CharSequence`, including:
  - `String`
  - `StringBuilder`
  - `StringBuffer`
  - custom `CharSequence` implementations
  - `null`
- Valid content includes:
  - empty sequences
  - ASCII text
  - Unicode text
  - equal and unequal lengths
  - equal and unequal character content
  - different object identities with equal contents

### String-to-byte methods

Methods:

- `getByteBufferUtf8`
- `getBytesIso8859_1`
- `getBytesUsAscii`
- `getBytesUtf16`
- `getBytesUtf16Be`
- `getBytesUtf16Le`
- `getBytesUtf8`

Input:

- Any Java `String`, including:
  - `null`
  - empty string
  - ASCII text
  - Unicode characters
  - supplementary Unicode characters/surrogate pairs
  - characters not representable by the selected charset

The fixed-charset methods use standard Java charset constants from `org.apache.commons.codec.Charsets`:

- `Charsets.ISO_8859_1`
- `Charsets.US_ASCII`
- `Charsets.UTF_16`
- `Charsets.UTF_16BE`
- `Charsets.UTF_16LE`
- `Charsets.UTF_8`

### Charset-name methods

Methods:

- `getBytesUnchecked(String, String)`
- `newString(byte[], String)`

Inputs:

- A `String` or `byte[]` that may be `null`.
- A charset-name `String`:
  - Valid supported charset names, such as `"UTF-8"` or constants from `CharEncoding`.
  - Unsupported or invalid names.
  - `null`, which is not documented as valid.
  - Empty name, which is not a supported charset name.

### Byte-to-string methods

Methods:

- `newStringIso8859_1`
- `newStringUsAscii`
- `newStringUtf16`
- `newStringUtf16Be`
- `newStringUtf16Le`
- `newStringUtf8`

Inputs:

- Any `byte[]`, including:
  - `null`
  - zero-length arrays
  - ASCII byte sequences
  - valid byte sequences for the relevant charset
  - malformed byte sequences

For malformed byte input, Java `new String(bytes, charset)` decoder replacement behavior is delegated to the JDK. The supplied source does not specify a custom malformed-input policy.

---

## 3. Reachable conditions and branches

### `equals(CharSequence, CharSequence)`

The method has four meaningful paths:

1. **Same reference**
   ```java
   if (cs1 == cs2) {
       return true;
   }
   ```
   Covers:
   - same non-null object
   - both inputs `null`

2. **Exactly one input is `null`**
   ```java
   if (cs1 == null || cs2 == null) {
       return false;
   }
   ```

3. **Both arguments are `String`**
   ```java
   if (cs1 instanceof String && cs2 instanceof String) {
       return cs1.equals(cs2);
   }
   ```
   Covers equal and unequal `String` contents.

4. **At least one argument is a non-`String` `CharSequence`**
   ```java
   return CharSequenceUtils.regionMatches(...);
   ```
   Covers equal and unequal `StringBuilder`/other `CharSequence` contents, including unequal lengths.

### Fixed-charset string-to-byte methods

For all methods delegating to `getBytes(String, Charset)`:

```java
if (string == null) {
    return null;
}
return string.getBytes(charset);
```

Reachable branches:

1. `string == null`: returns `null`.
2. `string != null`: returns encoded byte array.

### `getByteBufferUtf8`

Delegates to `getByteBuffer(String, Charset)`:

```java
if (string == null) {
    return null;
}
return ByteBuffer.wrap(string.getBytes(charset));
```

Branches:

1. `string == null`: returns `null`.
2. Non-null string: returns a newly wrapped `ByteBuffer`.
   - Empty string produces a non-null zero-length buffer.
   - Non-empty string produces a buffer containing UTF-8 encoded bytes.

### `getBytesUnchecked(String, String)`

```java
if (string == null) {
    return null;
}
try {
    return string.getBytes(charsetName);
} catch (final UnsupportedEncodingException e) {
    throw StringUtils.newIllegalStateException(charsetName, e);
}
```

Branches:

1. `string == null`: returns `null` without validating `charsetName`.
2. Non-null string and supported charset name: returns encoded bytes.
3. Non-null string and unsupported charset name: catches `UnsupportedEncodingException` and throws `IllegalStateException`.
4. Non-null string and `charsetName == null`: JDK call is expected to throw `NullPointerException`; it is not caught.
5. Non-null string and malformed/empty charset name: normally causes `UnsupportedEncodingException`, converted to `IllegalStateException`.

### `newString(byte[], String)`

```java
if (bytes == null) {
    return null;
}
try {
    return new String(bytes, charsetName);
} catch (final UnsupportedEncodingException e) {
    throw StringUtils.newIllegalStateException(charsetName, e);
}
```

Branches mirror `getBytesUnchecked`:

1. `bytes == null`: returns `null` without validating `charsetName`.
2. Non-null bytes and supported charset name: returns decoded string.
3. Non-null bytes and unsupported charset name: throws `IllegalStateException`.
4. Non-null bytes and `charsetName == null`: JDK `NullPointerException`, not caught.
5. Non-null bytes and malformed/empty charset name: converted `IllegalStateException`.

### Fixed-charset byte-to-string methods

The private helper is null-safe:

```java
private static String newString(final byte[] bytes, final Charset charset) {
    return bytes == null ? null : new String(bytes, charset);
}
```

Methods correctly delegating to this helper have two paths:

1. `bytes == null`: returns `null`.
2. `bytes != null`: returns decoded string.

These methods delegate correctly:

- `newStringUsAscii`
- `newStringUtf16`
- `newStringUtf16Be`
- `newStringUtf16Le`
- `newStringUtf8`

However, `newStringIso8859_1` does **not** use the null-safe helper:

```java
public static String newStringIso8859_1(final byte[] bytes) {
    return new String(bytes, Charsets.ISO_8859_1);
}
```

Its reachable behavior in the supplied buggy source is:

1. Non-null `bytes`: returns ISO-8859-1 decoded string.
2. `bytes == null`: throws `NullPointerException`.

This contradicts its Javadoc, which says it returns `null` for a null byte array.

---

## 4. Normal, boundary, invalid, null, and exceptional cases

### Normal cases

1. `equals` with:
   - equal strings
   - unequal strings
   - equal non-`String` `CharSequence` values
   - unequal non-`String` `CharSequence` values

2. Encoding valid text using each fixed charset:
   - ASCII text, e.g. `"abc"`
   - Unicode content for UTF encodings, e.g. `"é"` or `"€"`

3. Decoding valid byte sequences using each fixed charset.

4. Round trips where meaningful:
   - `newStringUtf8(getBytesUtf8(value))`
   - `newStringUtf16Be(getBytesUtf16Be(value))`
   - etc.

5. Valid named charset operations:
   - `getBytesUnchecked("text", "UTF-8")`
   - `newString(bytes, "UTF-8")`

Expected values can be independently derived with JDK APIs such as:

```java
value.getBytes(Charsets.UTF_8)
new String(bytes, Charsets.UTF_8)
```

### Boundary cases

1. Empty string encoding:
   - Expected non-null empty `byte[]`.
   - `getByteBufferUtf8("")` should return a non-null zero-length buffer.

2. Empty byte-array decoding:
   - Expected empty string.

3. Non-ASCII text:
   - Important for UTF-8 and UTF-16 byte ordering/encoding behavior.

4. Inputs containing characters not representable by US-ASCII or ISO-8859-1:
   - Exact results should follow the JDK encoder’s default replacement behavior.
   - Tests should preferably compare with the equivalent JDK charset operation rather than hard-code replacement bytes unless the required Java behavior is intentionally being asserted.

### Null cases

Expected according to the supplied API documentation:

| Method family | Null primary input expectation |
|---|---|
| `equals` | `equals(null, null)` is `true`; exactly one null is `false` |
| `getByteBufferUtf8(null)` | `null` |
| `getBytes*` fixed-charset methods with null string | `null` |
| `getBytesUnchecked(null, charsetName)` | `null` |
| `newString(null, charsetName)` | `null` |
| `newStringIso8859_1(null)` | **`null`, per Javadoc and bug report intent** |
| Other `newString*` fixed-charset methods with null bytes | `null` |

The supplied source version violates the documented expectation specifically for:

```java
StringUtils.newStringIso8859_1(null)
```

It throws `NullPointerException` rather than returning `null`.

### Invalid and exceptional cases

1. Unsupported charset names:
   - `getBytesUnchecked(nonNullString, unsupportedName)` must throw `IllegalStateException`.
   - `newString(nonNullBytes, unsupportedName)` must throw `IllegalStateException`.
   - The exception message is constructed as:
     ```java
     charsetName + ": " + e
     ```
     A test may assert exception type; asserting the complete JDK exception text would be fragile.

2. `null` charset names with non-null primary input:
   - The implementation delegates to JDK `String#getBytes(String)` / `String(byte[], String)`.
   - It does not catch `NullPointerException`.
   - Therefore a `NullPointerException` is expected from the JDK call, although this behavior is not explicitly part of this class’s documented API contract.

3. Null primary input with invalid/null charset name:
   - Both named-charset methods return `null` before evaluating the charset name:
     - `getBytesUnchecked(null, invalidOrNullCharset)`
     - `newString(null, invalidOrNullCharset)`

---

## 5. Required constructors, dependencies, and external objects

### Constructors

`StringUtils` declares no constructor. Java therefore supplies an implicit public no-argument constructor.

However:

- All exposed behavior is static.
- No object construction is required to test the public API.
- Testing the implicit constructor would add little behavioral value unless project conventions explicitly require utility-class constructor coverage.

### Dependencies

The target class depends on:

- JDK classes:
  - `java.nio.ByteBuffer`
  - `java.nio.charset.Charset`
  - `java.io.UnsupportedEncodingException`
  - `java.lang.String`
  - `java.lang.CharSequence`

- Commons Codec project classes:
  - `org.apache.commons.codec.Charsets`
  - `org.apache.commons.codec.CharEncoding`
  - `org.apache.commons.codec.binary.CharSequenceUtils`

No mocks, files, network connections, clocks, or other external services are required.

### External objects useful in tests

- `Charsets.UTF_8`, `Charsets.UTF_16`, etc., for independent expected JDK encoding/decoding values.
- `CharEncoding.UTF_8` or literal standard names such as `"UTF-8"` for named-charset overloads.
- `StringBuilder` instances to reach the non-`String` branch in `equals`.
- `ByteBuffer` inspection methods:
  - `remaining()`
  - `get(byte[])`
  - possibly `array()` only if tests deliberately rely on `ByteBuffer.wrap` implementation behavior. Checking buffer content via `duplicate()`/`get()` is less coupled.

---

## 6. JUnit version and build tool

The supplied project context explicitly specifies:

- **JUnit:** `junit-4.12.jar`
- **Build tool:** Maven

Therefore future tests should use JUnit 4 syntax, for example:

- `org.junit.Test`
- `org.junit.Assert.assertEquals`
- `org.junit.Assert.assertArrayEquals`
- `org.junit.Assert.assertNull`
- `org.junit.Assert.assertTrue`
- `org.junit.Assert.assertFalse`
- `@Test(expected = IllegalStateException.class)` where appropriate

No Maven `pom.xml`, source/target Java version, test source layout, or dependency configuration was supplied. Maven and JUnit 4.12 are known, but the exact module/source-root placement is not.

---

## 7. Available test oracles

The supplied information provides the following test oracles.

### API documentation/Javadocs in the target class

The Javadocs specify:

- `equals` null semantics and case-sensitive equality.
- Fixed-charset encoding methods return `null` for null strings.
- Fixed-charset decoding methods return `null` for null arrays.
- Named charset methods return `null` for null primary inputs.
- Unsupported named charsets are translated from `UnsupportedEncodingException` to `IllegalStateException`.

### Java platform APIs

The production implementation intentionally delegates to standard JDK encoding and decoding methods. Thus JDK operations are reliable behavioral oracles for non-null, valid inputs:

```java
input.getBytes(Charsets.UTF_8)
new String(bytes, Charsets.UTF_8)
ByteBuffer.wrap(input.getBytes(Charsets.UTF_8))
```

### Bug report metadata

For Codec-17 / CODEC-229:

- Modified source: `StringUtils`
- Triggering test:
  ```text
  org.apache.commons.codec.binary.StringUtilsTest::testNewStringNullInput_CODEC229
  ```
- Failure in the supplied version:
  ```text
  java.lang.NullPointerException
  ```

This strongly identifies the intended regression behavior: a null byte array passed to the relevant `newString...` method must not cause an NPE.

### Existing tests

An existing test class and method are named in the prompt, but their source is not provided:

```text
org.apache.commons.codec.binary.StringUtilsTest::testNewStringNullInput_CODEC229
```

Its exact assertions, test style, and coverage of individual methods cannot be determined from the supplied material.

---

## 8. Bug-report-related behaviors to test

The primary regression scenario is:

```java
StringUtils.newStringIso8859_1(null)
```

### Expected behavior

It should return `null`.

This expectation is supported by:

1. The Javadoc for `newStringIso8859_1`:
   > “or `null` if the input byte array was `null`.”

2. The null-safe private helper used by every comparable fixed-charset decoder:
   ```java
   private static String newString(final byte[] bytes, final Charset charset) {
       return bytes == null ? null : new String(bytes, charset);
   }
   ```

3. The bug report and triggering test name:
   ```text
   testNewStringNullInput_CODEC229
   ```

### Current buggy behavior

The supplied source calls the Java constructor directly:

```java
return new String(bytes, Charsets.ISO_8859_1);
```

With `bytes == null`, this throws `NullPointerException`.

### Regression test scope

A meaningful regression test should at least assert that:

```java
assertNull(StringUtils.newStringIso8859_1(null));
```

It is also appropriate to test null behavior consistently across all public fixed-charset decoding methods:

- `newStringIso8859_1(null)` — the bug-specific assertion
- `newStringUsAscii(null)`
- `newStringUtf16(null)`
- `newStringUtf16Be(null)`
- `newStringUtf16Le(null)`
- `newStringUtf8(null)`

This broader comparison verifies the intended common contract while ensuring the test specifically detects the ISO-8859-1 inconsistency.

A companion non-null ISO-8859-1 decoding test is useful to confirm that correcting null handling must preserve normal decoding behavior.

---

## 9. Missing context and limitations

The supplied material is sufficient to design focused, compilable JUnit 4 tests for the public static methods and the Codec-17 regression, subject to using the known target package/class.

However, the following context is missing:

1. **Existing `StringUtilsTest` source**
   - The prompt identifies a triggering test but does not provide it.
   - Its precise expected assertions and existing helper methods are unavailable.
   - A new test should not duplicate an existing test with the same class/method name unless the project test source is inspected.

2. **`pom.xml` and Maven module layout**
   - Maven is identified, but not:
     - source and test source paths,
     - artifact/module structure,
     - compiler Java version,
     - configured test plugins,
     - other test dependencies.
   - This prevents confirming the exact location and configuration for a compilable test file.

3. **Source for `Charsets`, `CharEncoding`, and `CharSequenceUtils`**
   - Their public usage is clear from imports and standard Commons Codec conventions.
   - The behavior of `CharSequenceUtils.regionMatches` is not supplied, so tests should rely on the documented `StringUtils.equals` contract rather than assert implementation details of `CharSequenceUtils`.

4. **Exact fixed-version patch**
   - The fixed revision identifier is supplied, but no fixed source is provided.
   - The expected null behavior is still reliably established by the target Javadocs and the bug-trigger information; no alternative program version is required.

5. **Malformed-byte decoding expectations**
   - No project-specific policy is supplied beyond use of `new String(bytes, charset)`.
   - Tests for malformed byte input should compare against JDK behavior instead of imposing undocumented replacement/error expectations.

In summary, the supplied source and bug metadata are sufficient for a reliable regression test for CODEC-229 and standard API tests. The principal known defect is that `newStringIso8859_1(null)` violates its documented null-return contract by throwing `NullPointerException`.