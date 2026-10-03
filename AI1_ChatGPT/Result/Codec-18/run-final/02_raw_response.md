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

Test coverage summary:

- `testEqualsHandlesIdentityAndNullValues`: Covers reference identity, both-null, and one-null branches.
- `testEqualsHandlesStringsCaseSensitively`: Covers the optimized `String` comparison path and case-sensitive unequal values.
- `testEqualsHandlesEquivalentNonStringCharSequences`: Covers equal non-`String` `CharSequence` instances through `CharSequenceUtils.regionMatches`.
- `testEqualsReturnsFalseForDifferentLengthNonStringCharSequences`: Targets CODEC-231. It verifies unequal-length non-`String` sequences return `false` rather than throwing `StringIndexOutOfBoundsException`; both length directions are covered.
- `testEqualsReturnsFalseForDifferentNonStringCharacters`: Covers a same-length mismatch in the non-`String` comparison path.
- `testGetByteBufferUtf8EncodesContentAndInitialBufferState`: Verifies UTF-8 byte-buffer contents, position, and limit.
- `testByteArrayEncodingMethodsUseTheirDocumentedCharsets`: Exercises all fixed-charset byte-array encoding methods.
- `testGetBytesUncheckedUsesNamedCharset`: Covers successful named-charset encoding.
- `testGetBytesUncheckedWrapsUnsupportedEncodingException`: Covers conversion of an unsupported encoding failure to `IllegalStateException`.
- `testNewStringUsesNamedCharset`: Covers successful named-charset decoding.
- `testNewStringDecodingMethodsUseTheirDocumentedCharsets`: Exercises all fixed-charset decoding methods.
- `testNewStringWrapsUnsupportedEncodingException`: Covers named decoding failure conversion to `IllegalStateException`.
- `testEncodingAndDecodingMethodsReturnNullForNullInput`: Covers documented null-input behavior for every public encoder and decoder.
- `testNamedCharsetMethodsRejectNullCharsetNameWhenInputIsPresent`: Covers invalid null charset-name behavior when input is non-null.