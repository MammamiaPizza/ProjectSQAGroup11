package org.apache.commons.compress.compressors.bzip2;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class BZip2CompressorInputStreamZeroLengthReadTest {

    private static final byte[] EMPTY_BZIP2_STREAM = {
        'B', 'Z', 'h', '9',
        0x17, 0x72, 0x45, 0x38, 0x50, (byte) 0x90,
        0x00, 0x00, 0x00, 0x00
    };

    private BZip2CompressorInputStream newEmptyStream() throws IOException {
        return new BZip2CompressorInputStream(
                new ByteArrayInputStream(EMPTY_BZIP2_STREAM));
    }

    @Test
    public void zeroLengthReadAtEndOfStreamReturnsZero() throws IOException {
        BZip2CompressorInputStream in = newEmptyStream();
        byte[] buffer = { 1, 2, 3 };

        assertEquals(0, in.read(buffer, 0, 0));
        assertArrayEquals(new byte[] { 1, 2, 3 }, buffer);
        assertEquals(-1, in.read(buffer, 0, buffer.length));

        in.close();
    }

    @Test
    public void zeroLengthReadAllowsOffsetAtEndOfDestination() throws IOException {
        BZip2CompressorInputStream in = newEmptyStream();
        byte[] buffer = new byte[4];

        assertEquals(0, in.read(buffer, buffer.length, 0));

        in.close();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void zeroLengthReadRejectsNegativeOffset() throws IOException {
        BZip2CompressorInputStream in = newEmptyStream();
        try {
            in.read(new byte[1], -1, 0);
        } finally {
            in.close();
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void zeroLengthReadRejectsOffsetPastDestinationEnd() throws IOException {
        BZip2CompressorInputStream in = newEmptyStream();
        try {
            in.read(new byte[1], 2, 0);
        } finally {
            in.close();
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void readRejectsNegativeLength() throws IOException {
        BZip2CompressorInputStream in = newEmptyStream();
        try {
            in.read(new byte[1], 0, -1);
        } finally {
            in.close();
        }
    }

@org.junit.Test
    public void truncatedStreamIsRejectedDuringInitialization() {
        try {
            new org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream(
                    new java.io.ByteArrayInputStream(new byte[] { 'B', 'Z', 'h', '9' }));
            org.junit.Assert.fail("Expected truncated stream to be rejected");
        } catch (final java.io.IOException expected) {
            // expected
        }
    }

    @org.junit.Test
    public void nonBzip2HeaderIsRejected() {
        try {
            new org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream(
                    new java.io.ByteArrayInputStream(new byte[] { 'n', 'o', 'p', 'e' }));
            org.junit.Assert.fail("Expected invalid header to be rejected");
        } catch (final java.io.IOException expected) {
            // expected
        }
    }

    @org.junit.Test
    public void concatenatedModeValidatesFollowingStream() throws java.io.IOException {
        final byte[] firstStream = emptyBZip2Stream();
        final byte[] data = new byte[firstStream.length + 4];
        System.arraycopy(firstStream, 0, data, 0, firstStream.length);
        data[firstStream.length] = 'B';
        data[firstStream.length + 1] = 'Z';
        data[firstStream.length + 2] = 'h';
        data[firstStream.length + 3] = '9';

        final org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream singleStream =
                new org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream(
                        new java.io.ByteArrayInputStream(data), false);
        org.junit.Assert.assertEquals(-1, singleStream.read());

        try {
            new org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream(
                    new java.io.ByteArrayInputStream(data), true);
            org.junit.Assert.fail("Expected malformed concatenated stream to be rejected");
        } catch (final java.io.IOException expected) {
            // expected
        }
    }

    @org.junit.Test
    public void matchesRequiresCompleteBzip2Signature() {
        final byte[] signature = new byte[] { 'B', 'Z', 'h' };

        org.junit.Assert.assertTrue(
                org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream.matches(signature, 3));
        org.junit.Assert.assertFalse(
                org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream.matches(signature, 2));
    }

    private byte[] emptyBZip2Stream() {
        return new byte[] {
            'B', 'Z', 'h', '9',
            0x17, 0x72, 0x45, 0x38, 0x50, (byte) 0x90,
            0x00, 0x00, 0x00, 0x00
        };
    }
}
