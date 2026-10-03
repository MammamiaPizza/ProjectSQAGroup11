package org.apache.commons.codec.binary;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;

import org.apache.commons.codec.EncoderException;
import org.junit.Test;

public class Base64Codec4bTest {

    private static final Charset ASCII = Charset.forName("US-ASCII");

    private static byte[] ascii(String value) {
        return value.getBytes(ASCII);
    }

    @Test
    public void testStaticStandardEncodingAtGroupingBoundaries() {
        assertEquals("", Base64.encodeBase64String(new byte[0]));
        assertEquals("TQ==", Base64.encodeBase64String(ascii("M")));
        assertEquals("TWE=", Base64.encodeBase64String(ascii("Ma")));
        assertEquals("TWFu", Base64.encodeBase64String(ascii("Man")));
        assertEquals("TWFuTQ==", Base64.encodeBase64String(ascii("ManM")));
    }

    @Test
    public void testDefaultConstructorDoesNotChunkLongEncoding() {
        byte[] input = new byte[60];
        for (int i = 0; i < input.length; i++) {
            input[i] = 'a';
        }

        String expected =
                "YWFhYWFhYWFhYWFhYWFh"
              + "YWFhYWFhYWFhYWFhYWFh"
              + "YWFhYWFhYWFhYWFhYWFh"
              + "YWFhYWFhYWFhYWFhYWFh";

        Base64 codec = new Base64();
        assertEquals(expected, codec.encodeToString(input));
        assertArrayEquals(ascii(expected), codec.encode(input));
    }

    @Test
    public void testObjectAndByteArrayEncodingUseSameStandardRepresentation() throws Exception {
        Base64 codec = new Base64();
        byte[] input = ascii("Man");

        assertArrayEquals(ascii("TWFu"), codec.encode(input));
        assertArrayEquals(ascii("TWFu"), (byte[]) codec.encode((Object) input));
        assertEquals("TWFu", codec.encodeToString(input));
    }

    @Test
    public void testEncodeBase64StringDoesNotChunkOutput() {
        byte[] input = new byte[60];
        for (int i = 0; i < input.length; i++) {
            input[i] = 'a';
        }

        String expected =
                "YWFhYWFhYWFhYWFhYWFh"
              + "YWFhYWFhYWFhYWFhYWFh"
              + "YWFhYWFhYWFhYWFhYWFh"
              + "YWFhYWFhYWFhYWFhYWFh";

        assertEquals(expected, Base64.encodeBase64String(input));
        assertFalse(Base64.encodeBase64String(input).contains("\r\n"));
    }

    @Test
    public void testExplicitChunkedEncodingUsesMimeLineSeparator() {
        byte[] input = new byte[60];
        for (int i = 0; i < input.length; i++) {
            input[i] = 'a';
        }

        String unchunked =
                "YWFhYWFhYWFhYWFhYWFh"
              + "YWFhYWFhYWFhYWFhYWFh"
              + "YWFhYWFhYWFhYWFhYWFh"
              + "YWFhYWFhYWFhYWFhYWFh";

        assertArrayEquals(ascii(unchunked.substring(0, 76) + "\r\n"
                + unchunked.substring(76) + "\r\n"), Base64.encodeBase64Chunked(input));
    }

    @Test
    public void testUrlSafeEncodingUsesUrlAlphabetAndOmitsPadding() {
        byte[] input = new byte[] { (byte) 0xfb, (byte) 0xff, (byte) 0xff };

        assertArrayEquals(ascii("+///"), Base64.encodeBase64(input));
        assertArrayEquals(ascii("-___"), Base64.encodeBase64URLSafe(input));
        assertEquals("-___", Base64.encodeBase64URLSafeString(input));
        assertEquals("TQ", Base64.encodeBase64URLSafeString(ascii("M")));
        assertTrue(new Base64(true).isUrlSafe());
        assertFalse(new Base64().isUrlSafe());
    }

    @Test(expected = EncoderException.class)
    public void testEncodeObjectRejectsUnsupportedType() throws Exception {
        new Base64().encode((Object) "not bytes");
    }

    @Test
    public void testEncodeHandlesNullAndEmptyInput() {
        Base64 codec = new Base64();

        assertNull(codec.encode((byte[]) null));
        assertNull(Base64.encodeBase64(null));
        assertArrayEquals(new byte[0], codec.encode(new byte[0]));
        assertArrayEquals(new byte[0], Base64.encodeBase64(new byte[0]));
    }

    @Test
    public void testDecodeAcceptsWhitespaceAndRestoresOriginalBytes() {
        assertArrayEquals(ascii("Man"), Base64.decodeBase64("T W\nFu\r"));
        assertArrayEquals(ascii("Man"), new Base64().decode(ascii("TWFu")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLineSeparatorCannotContainBase64Character() {
        new Base64(4, ascii("A"));
    }
}
