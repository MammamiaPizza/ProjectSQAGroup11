package org.apache.commons.codec.binary;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.junit.Test;

public class Base64ComprehensiveTest {

    @Test
    public void testStandardEncodingAndDecodingForCompleteAndPartialGroups() throws Exception {
        assertEquals("Zm9v", new String(Base64.encodeBase64("foo".getBytes("US-ASCII")), "US-ASCII"));
        assertEquals("Zg==", new String(Base64.encodeBase64("f".getBytes("US-ASCII")), "US-ASCII"));
        assertEquals("Zm8=", new String(Base64.encodeBase64("fo".getBytes("US-ASCII")), "US-ASCII"));

        byte[] original = new byte[] { 0, 1, 2, 3, 127, -128, -1 };
        assertArrayEquals(original, Base64.decodeBase64(Base64.encodeBase64(original)));
    }

    @Test
    public void testEncodeAndDecodeNullAndEmptyArrays() {
        assertNull(Base64.encodeBase64(null));
        assertNull(Base64.decodeBase64(null));

        byte[] empty = new byte[0];
        assertEquals(0, Base64.encodeBase64(empty).length);
        assertEquals(0, Base64.decodeBase64(empty).length);
    }

    @Test
    public void testUrlSafeEncodingOmitsPaddingAndDecodesWithEitherAlphabet() throws Exception {
        byte[] data = new byte[] { -1, -1, -1, -1 };

        byte[] encoded = Base64.encodeBase64URLSafe(data);
        assertEquals("_____w", new String(encoded, "US-ASCII"));
        assertArrayEquals(data, Base64.decodeBase64(encoded));

        assertArrayEquals(data, Base64.decodeBase64("/////w==".getBytes("US-ASCII")));
        assertTrue(new Base64(true).isUrlSafe());
        assertFalse(new Base64(false).isUrlSafe());
    }

    @Test
    public void testChunkedEncodingCreatesSeventySixCharacterLinesAndTrailingSeparators() throws Exception {
        byte[] input = new byte[58];
        for (int i = 0; i < input.length; i++) {
            input[i] = (byte) i;
        }

        byte[] encoded = Base64.encodeBase64Chunked(input);
        String value = new String(encoded, "US-ASCII");

        /*
         * 58 bytes encode to 80 Base64 characters. A CRLF is written after
         * the first 76 characters and another trailing CRLF is written at EOF.
         */
        assertEquals(84, encoded.length);
        assertEquals("\r\n", value.substring(76, 78));
        assertEquals("\r\n", value.substring(82, 84));
        assertTrue(value.endsWith("\r\n"));
        assertArrayEquals(input, Base64.decodeBase64(encoded));
    }

    @Test
    public void testCustomLineLengthIsAppliedByStreamingEncoder() throws Exception {
        Base64 base64 = new Base64(5, new byte[] { '~' });
        byte[] input = "abcdefghi".getBytes("US-ASCII");
        byte[] encoded = new byte[14];

        base64.encode(input, 0, input.length);
        base64.encode(input, 0, -1);

        assertEquals(14, base64.avail());
        assertEquals(14, base64.readResults(encoded, 0, encoded.length));
        assertEquals("YWJjZGVm~Z2hp~", new String(encoded, "US-ASCII"));
        assertEquals(-1, base64.readResults(new byte[1], 0, 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRejectsLineSeparatorContainingBase64Character() {
        new Base64(10, new byte[] { '\r', 'A', '\n' });
    }

    @Test
    public void testDecodeIgnoresWhitespaceNonAlphabetCharactersAndOptionalPadding() throws Exception {
        byte[] decorated = " T W#F\nu\r".getBytes("US-ASCII");

        assertArrayEquals("Man".getBytes("US-ASCII"), Base64.decodeBase64(decorated));
        assertArrayEquals("Ma".getBytes("US-ASCII"), Base64.decodeBase64("TWE".getBytes("US-ASCII")));
    }

    @Test
    public void testAlphabetValidationRecognizesStandardAndUrlSafeCharacters() {
        assertTrue(Base64.isBase64((byte) 'A'));
        assertTrue(Base64.isBase64((byte) '+'));
        assertTrue(Base64.isBase64((byte) '-'));
        assertTrue(Base64.isBase64((byte) '/'));
        assertTrue(Base64.isBase64((byte) '_'));
        assertTrue(Base64.isBase64((byte) '='));
        assertFalse(Base64.isBase64((byte) '\n'));
        assertFalse(Base64.isBase64((byte) '@'));

        assertTrue(Base64.isArrayByteBase64("T W\nE=".getBytes()));
        assertFalse(Base64.isArrayByteBase64("TWE@".getBytes()));
    }

    @Test
    public void testDiscardWhitespaceAndNonBase64Characters() throws Exception {
        assertEquals("TWE=",
                new String(Base64.discardWhitespace(" T\tW\nE=\r".getBytes("US-ASCII")), "US-ASCII"));
        assertEquals("TWE=",
                new String(Base64.discardNonBase64("!T W@E=#".getBytes("US-ASCII")), "US-ASCII"));
    }

    @Test
    public void testObjectEncoderAndDecoderAcceptByteArrays() throws Exception {
        Base64 base64 = new Base64(0);

        Object encoded = base64.encode((Object) "Man".getBytes("US-ASCII"));
        assertTrue(encoded instanceof byte[]);
        assertEquals("TWFu", new String((byte[]) encoded, "US-ASCII"));

        Object decoded = base64.decode(encoded);
        assertTrue(decoded instanceof byte[]);
        assertArrayEquals("Man".getBytes("US-ASCII"), (byte[]) decoded);
    }

    @Test(expected = EncoderException.class)
    public void testObjectEncoderRejectsNonByteArray() throws Exception {
        new Base64().encode((Object) "not bytes");
    }

    @Test(expected = DecoderException.class)
    public void testObjectDecoderRejectsNonByteArrayAndNull() throws Exception {
        new Base64().decode((Object) null);
    }

    @Test
    public void testIntegerEncodingRoundTrip() throws Exception {
        BigInteger value = new BigInteger("65537");

        byte[] encoded = Base64.encodeInteger(value);

        assertEquals("AQAB", new String(encoded, "US-ASCII"));
        assertEquals(value, Base64.decodeInteger(encoded));
        assertArrayEquals(new byte[] { (byte) 0x80 }, Base64.toIntegerBytes(BigInteger.valueOf(128)));
    }

    @Test(expected = NullPointerException.class)
    public void testEncodeIntegerRejectsNull() {
        Base64.encodeInteger(null);
    }

    @Test
    public void testStreamingEncodeFlushesRemainingBytesAtEof() throws Exception {
        Base64 base64 = new Base64(0);
        byte[] output = new byte[4];

        base64.encode("Ma".getBytes("US-ASCII"), 0, 2);
        base64.encode(null, 0, -1);

        assertEquals(4, base64.avail());
        assertEquals(4, base64.readResults(output, 0, output.length));
        assertEquals("TWE=", new String(output, "US-ASCII"));
        assertEquals(-1, base64.readResults(new byte[1], 0, 1));
    }

    @Test
    public void testStreamingEncodeOfEmptyInputProducesNoDataAtEof() {
        Base64 base64 = new Base64();

        base64.encode(new byte[0], 0, 0);
        base64.encode(new byte[0], 0, -1);

        assertFalse(base64.hasData());
        assertEquals(0, base64.avail());
        assertEquals(-1, base64.readResults(new byte[1], 0, 1));
    }

    @Test
    public void testStreamingDecodeProducesDataAndSignalsEndAfterBufferIsRead() throws Exception {
        Base64 base64 = new Base64();
        byte[] output = new byte[3];

        base64.decode("TWFu".getBytes("US-ASCII"), 0, 4);
        base64.decode(null, 0, -1);

        assertEquals(3, base64.avail());
        assertEquals(3, base64.readResults(output, 0, output.length));
        assertArrayEquals("Man".getBytes("US-ASCII"), output);
        assertEquals(-1, base64.readResults(new byte[1], 0, 1));
    }
}
