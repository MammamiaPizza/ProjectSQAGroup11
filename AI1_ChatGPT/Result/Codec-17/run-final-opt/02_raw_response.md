package org.apache.commons.codec.binary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class StringUtilsCodec229Test {

    @Test
    public void testAllNewStringByteArrayWrappersReturnNullForNullInput() {
        assertNull(StringUtils.newStringIso8859_1(null));
        assertNull(StringUtils.newStringUsAscii(null));
        assertNull(StringUtils.newStringUtf16(null));
        assertNull(StringUtils.newStringUtf16Be(null));
        assertNull(StringUtils.newStringUtf16Le(null));
        assertNull(StringUtils.newStringUtf8(null));
    }

    @Test
    public void testNewStringWithCharsetNameReturnsNullForNullInput() {
        assertNull(StringUtils.newString(null, "UTF-8"));
    }

    @Test
    public void testNewStringUtf8DecodesAsciiAndMultibyteCharacters() {
        final byte[] bytes = new byte[] {
                0x48, 0x69, 0x20, (byte) 0xC3, (byte) 0xA9, 0x20,
                (byte) 0xE2, (byte) 0x82, (byte) 0xAC
        };

        assertEquals("Hi é €", StringUtils.newStringUtf8(bytes));
        assertEquals("Hi é €", StringUtils.newString(bytes, "UTF-8"));
    }

    @Test
    public void testNewStringIso88591AndUsAsciiDecodeExpectedBytes() {
        assertEquals("Aé", StringUtils.newStringIso8859_1(new byte[] { 0x41, (byte) 0xE9 }));
        assertEquals("ASCII 123", StringUtils.newStringUsAscii(
                new byte[] { 0x41, 0x53, 0x43, 0x49, 0x49, 0x20, 0x31, 0x32, 0x33 }));
    }

    @Test
    public void testNewStringUtf16VariantsDecodeCorrectByteOrder() {
        assertEquals("AΩ", StringUtils.newStringUtf16(
                new byte[] { (byte) 0xFE, (byte) 0xFF, 0x00, 0x41, 0x03, (byte) 0xA9 }));
        assertEquals("AΩ", StringUtils.newStringUtf16Be(
                new byte[] { 0x00, 0x41, 0x03, (byte) 0xA9 }));
        assertEquals("AΩ", StringUtils.newStringUtf16Le(
                new byte[] { 0x41, 0x00, (byte) 0xA9, 0x03 }));
    }

    @Test
    public void testNewStringWithUnsupportedCharsetThrowsIllegalStateException() {
        final String charsetName = "no-such-charset-codec229";
        try {
            StringUtils.newString(new byte[] { 0x41 }, charsetName);
            fail("Expected IllegalStateException for an unsupported charset");
        } catch (final IllegalStateException expected) {
            assertTrue(expected.getMessage().contains(charsetName));
        }
    }
}