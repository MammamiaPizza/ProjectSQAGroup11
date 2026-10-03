package org.apache.commons.codec.binary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class BaseNCodecInputStreamCodec130Test {

    private static final String BASE64_HELLO_WORLD = "SGVsbG8gV29ybGQ=";
    private static final String BASE32_HELLO_WORLD = "JBSWY3DPEBLW64TMMQ======";

    private InputStream base64Stream(final String encoded) throws IOException {
        return new Base64InputStream(new ByteArrayInputStream(encoded.getBytes("US-ASCII")));
    }

    private InputStream base32Stream(final String encoded) throws IOException {
        return new Base32InputStream(new ByteArrayInputStream(encoded.getBytes("US-ASCII")));
    }

    private String readRemaining(final InputStream stream) throws IOException {
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        final byte[] buffer = new byte[4];
        int count;
        while ((count = stream.read(buffer, 0, buffer.length)) != -1) {
            result.write(buffer, 0, count);
        }
        return new String(result.toByteArray(), "UTF-8");
    }

    @Test
    public void testBase64SkipPreservesFollowingDecodedData() throws Exception {
        final InputStream stream = base64Stream(BASE64_HELLO_WORLD);

        assertEquals(1L, stream.skip(1));
        assertEquals("ello World", readRemaining(stream));
    }

    @Test
    public void testBase32SkipPreservesFollowingDecodedData() throws Exception {
        final InputStream stream = base32Stream(BASE32_HELLO_WORLD);

        assertEquals(1L, stream.skip(1));
        assertEquals("ello World", readRemaining(stream));
    }

    @Test
    public void testSkipExactlyToEndMakesBothDecodedStreamsReachEof() throws Exception {
        final InputStream base64 = base64Stream(BASE64_HELLO_WORLD);
        assertEquals(11L, base64.skip(11));
        assertEquals(-1, base64.read());
        assertEquals(0, base64.available());

        final InputStream base32 = base32Stream(BASE32_HELLO_WORLD);
        assertEquals(11L, base32.skip(11));
        assertEquals(-1, base32.read());
        assertEquals(0, base32.available());
    }

    @Test
    public void testSkipPastEndReturnsOnlyRemainingDecodedBytes() throws Exception {
        final InputStream base64 = base64Stream(BASE64_HELLO_WORLD);
        assertEquals(11L, base64.skip(1000));
        assertEquals(-1, base64.read());

        final InputStream base32 = base32Stream(BASE32_HELLO_WORLD);
        assertEquals(11L, base32.skip(1000));
        assertEquals(-1, base32.read());
    }

    @Test
    public void testLargeSkipIsCountedInDecodedBytes() throws Exception {
        final StringBuilder encoded = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            encoded.append("QUJD");
        }

        final InputStream stream = base64Stream(encoded.toString());
        assertEquals(3000L, stream.skip(100000));
        assertEquals(-1, stream.read());
    }

    @Test
    public void testNegativeSkipIsRejectedForBothDecodedStreams() throws Exception {
        try {
            base64Stream(BASE64_HELLO_WORLD).skip(-1);
            fail("Negative skip must throw IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            assertEquals(IllegalArgumentException.class, expected.getClass());
        }

        try {
            base32Stream(BASE32_HELLO_WORLD).skip(-1);
            fail("Negative skip must throw IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            assertEquals(IllegalArgumentException.class, expected.getClass());
        }
    }

    @Test
    public void testAvailableReportsOneUntilDecodedEof() throws Exception {
        final InputStream base64 = base64Stream(BASE64_HELLO_WORLD);
        assertEquals(BASE64_HELLO_WORLD.length(), base64.available());
        assertEquals('H', base64.read());
        assertEquals(0, base64.available());
        assertEquals("ello World", readRemaining(base64));
        assertEquals(0, base64.available());

        final InputStream base32 = base32Stream(BASE32_HELLO_WORLD);
        assertEquals(BASE32_HELLO_WORLD.length(), base32.available());
        assertEquals('H', base32.read());
        assertEquals(0, base32.available());
        assertEquals("ello World", readRemaining(base32));
        assertEquals(0, base32.available());
    }

    @Test
    public void testSingleByteReadProducesDecodedBytesForBothCodecs() throws Exception {
        final InputStream base64 = base64Stream(BASE64_HELLO_WORLD);
        final StringBuilder base64Result = new StringBuilder();
        int value;
        while ((value = base64.read()) != -1) {
            base64Result.append((char) value);
        }
        assertEquals("Hello World", base64Result.toString());

        final InputStream base32 = base32Stream(BASE32_HELLO_WORLD);
        final StringBuilder base32Result = new StringBuilder();
        while ((value = base32.read()) != -1) {
            base32Result.append((char) value);
        }
        assertEquals("Hello World", base32Result.toString());
    }
}
