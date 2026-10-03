```java
package org.apache.commons.codec.binary;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.junit.Test;

public class StringUtilsGeneratedTest {

    @Test
    public void testPublicConstructorCreatesInstance() {
        assertNotNull(new StringUtils());
    }

    @Test
    public void testEqualsHandlesIdentityAndNullValues() {
        final CharSequence value = new StringBuilder("value");

        assertTrue(StringUtils.equals(null, null));
        assertTrue(StringUtils.equals(value, value));
        assertFalse(StringUtils.equals(null, "value"));
        assertFalse(StringUtils.equals("value", null));
    }

    @Test
    public void testEqualsHandlesStringsCaseSensitively() {
        assertTrue(StringUtils.equals("abc", new String("abc")));
        assertFalse(StringUtils.equals("abc", "ABC"));
        assertFalse(StringUtils.equals("abc", "abd"));
    }

    @Test
    public void testEqualsHandlesEquivalentNonStringCharSequences() {
        assertTrue(StringUtils.equals(new StringBuilder("abc"), new StringBuffer("abc")));
        assertTrue(StringUtils.equals(new StringBuilder(""), new StringBuffer("")));
    }

    @Test
    public void testEqualsHandlesMixedStringAndCharSequenceLengths() {
        assertTrue(StringUtils.equals("abc", new StringBuilder("abc")));
        assertTrue(StringUtils.equals(new StringBuilder("abc"), "abc"));

        assertFalse(StringUtils.equals("abc", new StringBuilder("abcd")));
        assertFalse(StringUtils.equals(new StringBuilder("abcd"), "abc"));
    }

    @Test
    public void testEqualsReturnsFalseForDifferentLengthNonStringCharSequences() {
        assertFalse(StringUtils.equals(new StringBuilder("abc"), new StringBuilder("abcd")));
        assertFalse(StringUtils.equals(new StringBuilder("abcd"), new StringBuilder("abc")));
    }

    @Test
    public void testEqualsReturnsFalseForDifferentNonStringCharacters() {
        assertFalse(StringUtils.equals(new StringBuilder("abc"), new StringBuffer("abd")));
    }

    @Test
    public void testGetByteBufferUtf8EncodesContentAndInitialBufferState() {
        final String input = "A\u00e9";
        final byte[] expected = input.getBytes(Charset.forName("UTF-8"));

        final ByteBuffer result = StringUtils.getByteBufferUtf8(input);

        assertNotNull(result);
        assertEquals(0, result.position());
        assertEquals(expected.length, result.limit());

        final byte[] actual = new byte[result.remaining()];
        result.get(actual);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testByteArrayEncodingMethodsUseTheirDocumentedCharsets() {
        final String input = "A\u00e9";

        assertArrayEquals(input.getBytes(Charset.forName("ISO-8859-1")),
                StringUtils.getBytesIso8859_1(input));
        assertArrayEquals(input.getBytes(Charset.forName("US-ASCII")),
                StringUtils.getBytesUsAscii(input));
        assertArrayEquals(input.getBytes(Charset.forName("UTF-16")),
                StringUtils.getBytesUtf16(input));
        assertArrayEquals(input.getBytes(Charset.forName("UTF-16BE")),
                StringUtils.getBytesUtf16Be(input));
        assertArrayEquals(input.getBytes(Charset.forName("UTF-16LE")),
                StringUtils.getBytesUtf16Le(input));
        assertArrayEquals(input.getBytes(Charset.forName("UTF-8")),
                StringUtils.getBytesUtf8(input));
    }

    @Test
    public void testGetBytesUncheckedUsesNamedCharset() {
        final String input = "A\u00e9";

        assertArrayEquals(input.getBytes(Charset.forName("UTF-8")),
                StringUtils.getBytesUnchecked(input, "UTF-8"));
    }

    @Test
    public void testGetBytesUncheckedWrapsUnsupportedEncodingException() {
        final String charsetName = "definitely-not-a-supported-charset";

        try {
            StringUtils.getBytesUnchecked("value", charsetName);
            fail("An unsupported charset name must be wrapped in IllegalStateException");
        } catch (final IllegalStateException expected) {
            assertTrue(expected.getMessage().contains(charsetName));
        }
    }

    @Test
    public void testNewStringUsesNamedCharset() {
        final byte[] input = "A\u00e9".getBytes(Charset.forName("UTF-8"));

        assertEquals("A\u00e9", StringUtils.newString(input, "UTF-8"));
    }

    @Test
    public void testNewStringDecodingMethodsUseTheirDocumentedCharsets() {
        final String expected = "A\u00e9";

        assertEquals(expected, StringUtils.newStringIso8859_1(
                expected.getBytes(Charset.forName("ISO-8859-1"))));
        assertEquals("A?", StringUtils.newStringUsAscii(
                expected.getBytes(Charset.forName("US-ASCII"))));
        assertEquals(expected, StringUtils.newStringUtf16(
                expected.getBytes(Charset.forName("UTF-16"))));
        assertEquals(expected, StringUtils.newStringUtf16Be(
                expected.getBytes(Charset.forName("UTF-16BE"))));
        assertEquals(expected, StringUtils.newStringUtf16Le(
                expected.getBytes(Charset.forName("UTF-16LE"))));
        assertEquals(expected, StringUtils.newStringUtf8(
                expected.getBytes(Charset.forName("UTF-8"))));
    }

    @Test
    public void testNewStringWrapsUnsupportedEncodingException() {
        final String charsetName = "definitely-not-a-supported-charset";

        try {
            StringUtils.newString(new byte[] { 1, 2, 3 }, charsetName);
            fail("An unsupported charset name must be wrapped in IllegalStateException");
        } catch (final IllegalStateException expected) {
            assertTrue(expected.getMessage().contains(charsetName));
        }
    }

    @Test
    public void testEncodingAndDecodingMethodsReturnNullForNullInput() {
        assertNull(StringUtils.getByteBufferUtf8(null));
        assertNull(StringUtils.getBytesIso8859_1(null));
        assertNull(StringUtils.getBytesUnchecked(null, "UTF-8"));
        assertNull(StringUtils.getBytesUsAscii(null));
        assertNull(StringUtils.getBytesUtf16(null));
        assertNull(StringUtils.getBytesUtf16Be(null));
        assertNull(StringUtils.getBytesUtf16Le(null));
        assertNull(StringUtils.getBytesUtf8(null));

        assertNull(StringUtils.newString(null, "UTF-8"));
        assertNull(StringUtils.newStringIso8859_1(null));
        assertNull(StringUtils.newStringUsAscii(null));
        assertNull(StringUtils.newStringUtf16(null));
        assertNull(StringUtils.newStringUtf16Be(null));
        assertNull(StringUtils.newStringUtf16Le(null));
        assertNull(StringUtils.newStringUtf8(null));
    }

    @Test
    public void testNamedCharsetMethodsRejectNullCharsetNameWhenInputIsPresent() {
        try {
            StringUtils.getBytesUnchecked("value", null);
            fail("A null charset name with non-null input should throw NullPointerException");
        } catch (final NullPointerException expected) {
            assertNotNull(expected);
        }

        try {
            StringUtils.newString(new byte[] { 1 }, null);
            fail("A null charset name with non-null input should throw NullPointerException");
        } catch (final NullPointerException expected) {
            assertNotNull(expected);
        }
    }
}
```

### New test coverage and bug-focused behavior

`testEqualsHandlesMixedStringAndCharSequenceLengths`

- Targets the partially covered compound condition in `StringUtils.equals`:

  ```java
  if (cs1 instanceof String && cs2 instanceof String)
  ```

- Existing tests cover:
  - both operands being `String` (`true && true`);
  - neither operand being `String` (`false && ...`).

- The new test covers the previously untested reachable case where:
  - `cs1 instanceof String` is `true`;
  - `cs2 instanceof String` is `false`.

- It verifies equal content across mixed `CharSequence` implementations:
  - `String` versus `StringBuilder`;
  - `StringBuilder` versus `String`.

- It directly tests the fault-related unequal-length behavior in both directions:
  - shorter `String` versus longer `StringBuilder`;
  - longer `StringBuilder` versus shorter `String`.

These assertions distinguish the required contract—unequal character sequences must return `false`—from the reported defective behavior, which could incorrectly compare only a prefix or throw `StringIndexOutOfBoundsException` when comparing unequal-length `CharSequence` implementations.