package org.apache.commons.codec.binary;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

public class Base64InputStreamCodec105Test {

    @Test
    public void decodesThreeBytesWhenCallerBufferCanHoldOnlyTwo() throws IOException {
        Base64InputStream stream = new Base64InputStream(
                new ByteArrayInputStream(new byte[] { 'Y', 'W', 'J', 'j' }));

        byte[] first = new byte[2];
        assertEquals(2, stream.read(first));
        assertArrayEquals(new byte[] { 'a', 'b' }, first);

        byte[] second = new byte[2];
        assertEquals(1, stream.read(second));
        assertEquals((byte) 'c', second[0]);
        assertEquals(-1, stream.read(second));
    }

    @Test
    public void decodesFinalPartialQuantumAcrossBulkReads() throws IOException {
        Base64InputStream stream = new Base64InputStream(
                new ByteArrayInputStream(new byte[] {
                        'Y', 'W', 'J', 'j', 'Z', 'A', '=', '='
                }));

        assertArrayEquals(new byte[] { 'a', 'b', 'c', 'd' }, readAll(stream, 3));
        assertEquals(-1, stream.read(new byte[3]));
    }

    @Test
    public void repeatedlyReadsAcrossDecodedBufferBoundaries() throws IOException {
        Base64InputStream stream = new Base64InputStream(
                new ByteArrayInputStream(new byte[] {
                        'Y', 'W', 'J', 'j', 'Z', 'G', 'V', 'm', 'Z', 'w', '=', '='
                }));

        assertArrayEquals(
                new byte[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g' },
                readAll(stream, 2));
    }

    @Test
    public void bulkReadWithOffsetLeavesBytesOutsideRequestedRangeUntouched() throws IOException {
        Base64InputStream stream = new Base64InputStream(
                new ByteArrayInputStream(new byte[] { 'Y', 'W', 'J', 'j' }));
        byte[] destination = new byte[] { 9, 9, 9, 9, 9 };

        assertEquals(2, stream.read(destination, 1, 2));
        assertEquals((byte) 9, destination[0]);
        assertEquals((byte) 'a', destination[1]);
        assertEquals((byte) 'b', destination[2]);
        assertEquals((byte) 9, destination[3]);
        assertEquals((byte) 9, destination[4]);

        assertEquals(1, stream.read(destination, 1, 2));
        assertEquals((byte) 'c', destination[1]);
        assertEquals((byte) 'b', destination[2]);
        assertEquals((byte) 9, destination[3]);
        assertEquals(-1, stream.read(destination, 1, 2));
    }

    @Test
    public void singleByteReadsReturnUnsignedDecodedValuesAndEof() throws IOException {
        Base64InputStream stream = new Base64InputStream(
                new ByteArrayInputStream(new byte[] { '/', 'w', 'A', '=' }));

        assertEquals(255, stream.read());
        assertEquals(0, stream.read());
        assertEquals(-1, stream.read());
    }

    @Test
    public void zeroLengthReadReturnsZeroWithoutConsumingDecodedData() throws IOException {
        Base64InputStream stream = new Base64InputStream(
                new ByteArrayInputStream(new byte[] { 'T', 'Q', '=', '=' }));
        byte[] buffer = new byte[1];

        assertEquals(0, stream.read(buffer, 0, 0));
        assertEquals(1, stream.read(buffer));
        assertEquals((byte) 'M', buffer[0]);
        assertEquals(-1, stream.read(buffer));
    }

    private static byte[] readAll(Base64InputStream stream, int bufferSize) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[bufferSize];
        int count;
        while ((count = stream.read(buffer, 0, buffer.length)) != -1) {
            assertTrue(count > 0);
            output.write(buffer, 0, count);
        }
        return output.toByteArray();
    }

@Test
public void encodesDataAcrossSmallBulkReads() throws java.io.IOException {
    org.apache.commons.codec.binary.Base64InputStream stream =
            new org.apache.commons.codec.binary.Base64InputStream(
                    new java.io.ByteArrayInputStream(new byte[] { 'a', 'b', 'c' }), true);

    byte[] first = new byte[2];
    byte[] second = new byte[2];

    org.junit.Assert.assertEquals(2, stream.read(first));
    org.junit.Assert.assertArrayEquals(new byte[] { 'Y', 'W' }, first);
    org.junit.Assert.assertEquals(2, stream.read(second));
    org.junit.Assert.assertArrayEquals(new byte[] { 'J', 'j' }, second);
    org.junit.Assert.assertEquals(-1, stream.read(new byte[1]));
}

@Test
public void defaultInputStreamConstructorDecodesAndDoesNotSupportMarking() throws java.io.IOException {
    org.apache.commons.codec.binary.Base64InputStream stream =
            new org.apache.commons.codec.binary.Base64InputStream(
                    new java.io.ByteArrayInputStream(new byte[] { 'Y', 'Q', '=', '=' }));

    org.junit.Assert.assertFalse(stream.markSupported());
    org.junit.Assert.assertEquals((int) 'a', stream.read());
    org.junit.Assert.assertEquals(-1, stream.read());
}

@Test
public void nullLineSeparatorDisablesChunking() {
    org.apache.commons.codec.binary.Base64 codec =
            new org.apache.commons.codec.binary.Base64(4, null);

    org.junit.Assert.assertArrayEquals(
            new byte[] { 'Y', 'W', 'J', 'j', 'Z', 'G', 'V', 'm' },
            codec.encode(new byte[] { 'a', 'b', 'c', 'd', 'e', 'f' }));
}

@Test(expected = IllegalArgumentException.class)
public void rejectsLineSeparatorContainingBase64Characters() {
    new org.apache.commons.codec.binary.Base64(4, new byte[] { 'A' });
}
}
