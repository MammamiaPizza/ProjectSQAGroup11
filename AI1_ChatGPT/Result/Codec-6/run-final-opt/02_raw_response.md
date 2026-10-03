package org.apache.commons.codec.binary;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class Base64InputStreamCodec101Test {

    @Test
    public void bulkReadSkipsNonBase64InputUntilDecodedDataIsAvailable() throws IOException {
        Base64InputStream stream = new Base64InputStream(
                new OneByteAtATimeInputStream(new byte[] { '\n', '\r', ' ', '\t', 'T', 'Q', '=', '=' }));
        byte[] result = new byte[1];

        assertEquals(1, stream.read(result, 0, result.length));
        assertEquals((byte) 'M', result[0]);
        assertEquals(-1, stream.read(result, 0, result.length));
    }

    @Test
    public void bulkReadsAcrossShortUnderlyingReadsReturnAllDecodedBytesBeforeEof() throws IOException {
        Base64InputStream stream = new Base64InputStream(
                new OneByteAtATimeInputStream(new byte[] { 'T', 'W', 'F', 'u', 'T', 'Q', '=', '=' }));
        byte[] oneByte = new byte[1];

        assertEquals(1, stream.read(oneByte, 0, 1));
        assertEquals((byte) 'M', oneByte[0]);
        assertEquals(1, stream.read(oneByte, 0, 1));
        assertEquals((byte) 'a', oneByte[0]);
        assertEquals(1, stream.read(oneByte, 0, 1));
        assertEquals((byte) 'n', oneByte[0]);
        assertEquals(1, stream.read(oneByte, 0, 1));
        assertEquals((byte) 'M', oneByte[0]);
        assertEquals(-1, stream.read(oneByte, 0, 1));
    }

    @Test
    public void singleByteReadReturnsUnsignedDecodedByteAfterShortUnderlyingReads() throws IOException {
        Base64InputStream stream = new Base64InputStream(
                new OneByteAtATimeInputStream(new byte[] { '/', 'w', '=', '=' }));

        assertEquals(255, stream.read());
        assertEquals(-1, stream.read());
    }

    @Test
    public void bulkReadHonorsOffsetAndPartialDestinationCapacity() throws IOException {
        Base64InputStream stream = new Base64InputStream(
                new ByteArrayInputStream(new byte[] { 'T', 'W', 'F', 'u' }));
        byte[] destination = new byte[] { 99, 99, 99, 99, 99 };

        assertEquals(2, stream.read(destination, 1, 2));
        assertEquals(1, stream.read(destination, 3, 2));
        assertArrayEquals(new byte[] { 99, 'M', 'a', 'n', 99 }, destination);
        assertEquals(-1, stream.read(destination, 0, destination.length));
    }

    @Test
    public void zeroLengthReadReturnsZeroWithoutConsumingDecodedData() throws IOException {
        Base64InputStream stream = new Base64InputStream(
                new ByteArrayInputStream(new byte[] { 'T', 'Q', '=', '=' }));
        byte[] destination = new byte[1];

        assertEquals(0, stream.read(destination, 0, 0));
        assertEquals(1, stream.read(destination, 0, 1));
        assertEquals((byte) 'M', destination[0]);
    }

    @Test(expected = NullPointerException.class)
    public void bulkReadRejectsNullDestination() throws IOException {
        new Base64InputStream(new ByteArrayInputStream(new byte[0])).read(null, 0, 0);
    }

    @Test
    public void bulkReadRejectsInvalidOffsetAndLength() throws IOException {
        Base64InputStream stream = new Base64InputStream(new ByteArrayInputStream(new byte[0]));
        byte[] destination = new byte[2];

        try {
            stream.read(destination, -1, 1);
            fail("Negative offset must be rejected");
        } catch (IndexOutOfBoundsException expected) {
            // expected
        }

        try {
            stream.read(destination, 0, -1);
            fail("Negative length must be rejected");
        } catch (IndexOutOfBoundsException expected) {
            // expected
        }

        try {
            stream.read(destination, 1, 2);
            fail("Offset plus length beyond the destination must be rejected");
        } catch (IndexOutOfBoundsException expected) {
            // expected
        }
    }

    private static final class OneByteAtATimeInputStream extends InputStream {
        private final byte[] data;
        private int position;

        private OneByteAtATimeInputStream(byte[] data) {
            this.data = data;
        }

        @Override
        public int read() {
            return position == data.length ? -1 : data[position++] & 0xff;
        }

        @Override
        public int read(byte[] buffer, int offset, int length) {
            if (buffer == null) {
                throw new NullPointerException();
            }
            if (offset < 0 || length < 0 || offset > buffer.length - length) {
                throw new IndexOutOfBoundsException();
            }
            if (length == 0) {
                return 0;
            }
            if (position == data.length) {
                return -1;
            }
            buffer[offset] = data[position++];
            return 1;
        }
    }
}