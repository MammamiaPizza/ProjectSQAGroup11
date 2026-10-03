```java
package org.apache.commons.codec.binary;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.apache.commons.codec.CharEncoding;
import org.junit.Test;

public class StringUtilsGeneratedTest {

    private static final String SAMPLE = "H\u00e9llo";
    private static final byte[] UTF8_BYTES = SAMPLE.getBytes(Charset.forName(CharEncoding.UTF_8));

    @Test
    public void testPublicConstructorCanBeUsed() {
        assertNotNull(new StringUtils());
    }

    @Test
    public void testEqualsHandlesIdentityAndNullValues() {
        assertTrue(StringUtils.equals(null, null));

        final String value = new String("abc");
        assertTrue(StringUtils.equals(value, value));

        assertFalse(StringUtils.equals(null, "abc"));
        assertFalse(StringUtils.equals("abc", null));
    }

    @Test
    public void testEqualsHandlesStringsAndGenericCharSequences() {
        assertTrue(StringUtils.equals("abc", new String("abc")));
        assertFalse(StringUtils.equals("abc", "ABC"));
        assertFalse(StringUtils.equals("abc", "abcd"));

        assertTrue(StringUtils.equals(new StringBuilder("abc"), new StringBuilder("abc")));
        assertFalse(StringUtils.equals(new StringBuilder("abc"), new StringBuilder("abd")));
        assertFalse(StringUtils.equals(new StringBuilder("abc"), new StringBuilder("ab")));
    }

    @Test
    public void testGetBytesMethodsUseTheirSpecifiedCharsets() {
        assertArrayEquals(SAMPLE.getBytes(Charset.forName(CharEncoding.ISO_8859_1)),
                StringUtils.getBytesIso8859_1(SAMPLE));
        assertArrayEquals(SAMPLE.getBytes(Charset.forName(CharEncoding.US_ASCII)),
                StringUtils.getBytesUsAscii(SAMPLE));
        assertArrayEquals(SAMPLE.getBytes(Charset.forName(CharEncoding.UTF_16)),
                StringUtils.getBytesUtf16(SAMPLE));
        assertArrayEquals(SAMPLE.getBytes(Charset.forName(CharEncoding.UTF_16BE)),
                StringUtils.getBytesUtf16Be(SAMPLE));
        assertArrayEquals(SAMPLE.getBytes(Charset.forName(CharEncoding.UTF_16LE)),
                StringUtils.getBytesUtf16Le(SAMPLE));
        assertArrayEquals(UTF8_BYTES, StringUtils.getBytesUtf8(SAMPLE));
        assertArrayEquals(UTF8_BYTES, StringUtils.getBytesUnchecked(SAMPLE, CharEncoding.UTF_8));
    }

    @Test
    public void testGetByteBufferUtf8ContainsUtf8BytesAtInitialPosition() {
        final ByteBuffer result = StringUtils.getByteBufferUtf8(SAMPLE);

        assertNotNull(result);
        assertEquals(0, result.position());
        assertEquals(UTF8_BYTES.length, result.remaining());

        final byte[] actual = new byte[result.remaining()];
        result.get(actual);
        assertArrayEquals(UTF8_BYTES, actual);
    }

    @Test
    public void testByteEncodingMethodsReturnNullForNullString() {
        assertNull(StringUtils.getBytesIso8859_1(null));
        assertNull(StringUtils.getBytesUsAscii(null));
        assertNull(StringUtils.getBytesUtf16(null));
        assertNull(StringUtils.getBytesUtf16Be(null));
        assertNull(StringUtils.getBytesUtf16Le(null));
        assertNull(StringUtils.getBytesUtf8(null));
        assertNull(StringUtils.getBytesUnchecked(null, CharEncoding.UTF_8));
        assertNull(StringUtils.getByteBufferUtf8(null));
    }

    @Test
    public void testByteEncodingMethodsHandleEmptyString() {
        assertArrayEquals(new byte[0], StringUtils.getBytesUtf8(""));
        assertArrayEquals(new byte[0], StringUtils.getBytesUnchecked("", CharEncoding.UTF_8));

        final ByteBuffer result = StringUtils.getByteBufferUtf8("");
        assertNotNull(result);
        assertEquals(0, result.remaining());
    }

    @Test
    public void testGetBytesUncheckedWrapsUnsupportedEncodingException() {
        final String invalidCharset = "definitely-not-a-java-charset";

        try {
            StringUtils.getBytesUnchecked("value", invalidCharset);
        } catch (final IllegalStateException expected) {
            assertTrue(expected.getMessage().contains(invalidCharset));
            return;
        }

        throw new AssertionError("Expected IllegalStateException for an unsupported charset");
    }

    @Test(expected = NullPointerException.class)
    public void testGetBytesUncheckedRejectsNullCharsetNameForNonNullString() {
        StringUtils.getBytesUnchecked("value", null);
    }

    @Test
    public void testNewStringMethodsDecodeUsingTheirSpecifiedCharsets() {
        assertEquals(SAMPLE, StringUtils.newStringIso8859_1(
                SAMPLE.getBytes(Charset.forName(CharEncoding.ISO_8859_1))));
        assertEquals(SAMPLE, StringUtils.newStringUsAscii(
                "Hello".getBytes(Charset.forName(CharEncoding.US_ASCII))));
        assertEquals(SAMPLE, StringUtils.newStringUtf16(
                SAMPLE.getBytes(Charset.forName(CharEncoding.UTF_16))));
        assertEquals(SAMPLE, StringUtils.newStringUtf16Be(
                SAMPLE.getBytes(Charset.forName(CharEncoding.UTF_16BE))));
        assertEquals(SAMPLE, StringUtils.newStringUtf16Le(
                SAMPLE.getBytes(Charset.forName(CharEncoding.UTF_16LE))));
        assertEquals(SAMPLE, StringUtils.newStringUtf8(UTF8_BYTES));
        assertEquals(SAMPLE, StringUtils.newString(UTF8_BYTES, CharEncoding.UTF_8));
    }

    @Test
    public void testNewStringMethodsReturnNullForNullInputIncludingIso88591_CODEC229() {
        assertNull(StringUtils.newStringIso8859_1(null));
        assertNull(StringUtils.newStringUsAscii(null));
        assertNull(StringUtils.newStringUtf16(null));
        assertNull(StringUtils.newStringUtf16Be(null));
        assertNull(StringUtils.newStringUtf16Le(null));
        assertNull(StringUtils.newStringUtf8(null));
        assertNull(StringUtils.newString(null, CharEncoding.UTF_8));
    }

    @Test
    public void testNewStringMethodsDecodeEmptyByteArrayAsEmptyString() {
        final byte[] empty = new byte[0];

        assertEquals("", StringUtils.newStringIso8859_1(empty));
        assertEquals("", StringUtils.newStringUsAscii(empty));
        assertEquals("", StringUtils.newStringUtf16(empty));
        assertEquals("", StringUtils.newStringUtf16Be(empty));
        assertEquals("", StringUtils.newStringUtf16Le(empty));
        assertEquals("", StringUtils.newStringUtf8(empty));
        assertEquals("", StringUtils.newString(empty, CharEncoding.UTF_8));
    }

    @Test
    public void testNewStringWrapsUnsupportedEncodingException() {
        final String invalidCharset = "definitely-not-a-java-charset";

        try {
            StringUtils.newString(new byte[] { 1 }, invalidCharset);
        } catch (final IllegalStateException expected) {
            assertTrue(expected.getMessage().contains(invalidCharset));
            return;
        }

        throw new AssertionError("Expected IllegalStateException for an unsupported charset");
    }

    @Test(expected = NullPointerException.class)
    public void testNewStringRejectsNullCharsetNameForNonNullBytes() {
        StringUtils.newString(new byte[] { 1 }, null);
    }
}
```

Test coverage summary:

- `testPublicConstructorCanBeUsed`: Covers the implicit public constructor.
- `testEqualsHandlesIdentityAndNullValues`: Covers identical references, both-null equality, and one-null false branches.
- `testEqualsHandlesStringsAndGenericCharSequences`: Covers optimized `String` comparison and generic `CharSequence` comparison, including equal, differing-content, and differing-length cases.
- `testGetBytesMethodsUseTheirSpecifiedCharsets`: Verifies all public byte-encoding methods use their documented charset.
- `testGetByteBufferUtf8ContainsUtf8BytesAtInitialPosition`: Verifies UTF-8 byte-buffer content, initial position, and remaining-byte count.
- `testByteEncodingMethodsReturnNullForNullString`: Covers null-input behavior for every public encoding method.
- `testByteEncodingMethodsHandleEmptyString`: Covers empty-string boundary behavior.
- `testGetBytesUncheckedWrapsUnsupportedEncodingException`: Covers conversion of unsupported charset failures to `IllegalStateException`.
- `testGetBytesUncheckedRejectsNullCharsetNameForNonNullString`: Covers invalid null charset input for the unchecked encoder.
- `testNewStringMethodsDecodeUsingTheirSpecifiedCharsets`: Verifies every public decoder produces the expected string.
- `testNewStringMethodsReturnNullForNullInputIncludingIso88591_CODEC229`: Targets CODEC-229 directly. In particular, `newStringIso8859_1(null)` must return `null` rather than throwing `NullPointerException`.
- `testNewStringMethodsDecodeEmptyByteArrayAsEmptyString`: Covers empty-byte-array boundary behavior for all decoding methods.
- `testNewStringWrapsUnsupportedEncodingException`: Covers unsupported named charset failure handling for `newString`.
- `testNewStringRejectsNullCharsetNameForNonNullBytes`: Covers invalid null charset input for the named decoder.