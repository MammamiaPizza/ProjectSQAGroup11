package org.apache.commons.compress.utils;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.junit.Test;

public class ChecksumCalculatingInputStreamTest {

    @Test(expected = NullPointerException.class)
    public void constructorRejectsNullChecksum() {
        new ChecksumCalculatingInputStream(null, new ByteArrayInputStream(new byte[0]));
    }

    @Test(expected = NullPointerException.class)
    public void constructorRejectsNullInputStream() {
        new ChecksumCalculatingInputStream(new CRC32(), null);
    }

    @Test(expected = NullPointerException.class)
    public void constructorRejectsBothArgumentsBeingNull() {
        new ChecksumCalculatingInputStream(null, null);
    }

    @Test
    public void readSingleBytesUpdatesChecksumWithReadData() throws IOException {
        final byte[] data = new byte[] { 1, 2, (byte) 255 };
        final CRC32 checksum = new CRC32();
        final ChecksumCalculatingInputStream stream =
                new ChecksumCalculatingInputStream(checksum, new ByteArrayInputStream(data));

        assertEquals(1, stream.read());
        assertEquals(2, stream.read());
        assertEquals(255, stream.read());
        assertEquals(-1, stream.read());

        final CRC32 expected = new CRC32();
        expected.update(data, 0, data.length);
        assertEquals(expected.getValue(), stream.getValue());
    }

    @Test
    public void bulkReadUpdatesChecksumOnlyForBytesRead() throws IOException {
        final byte[] data = new byte[] { 10, 20, 30 };
        final CRC32 checksum = new CRC32();
        final ChecksumCalculatingInputStream stream =
                new ChecksumCalculatingInputStream(checksum, new ByteArrayInputStream(data));
        final byte[] buffer = new byte[5];

        assertEquals(3, stream.read(buffer, 1, 4));
        assertEquals(-1, stream.read(buffer, 0, buffer.length));

        final CRC32 expected = new CRC32();
        expected.update(data, 0, data.length);
        assertEquals(expected.getValue(), stream.getValue());
    }

    @Test
    public void emptyStreamHasInitialChecksumValue() throws IOException {
        final CRC32 checksum = new CRC32();
        final ChecksumCalculatingInputStream stream =
                new ChecksumCalculatingInputStream(checksum, new ByteArrayInputStream(new byte[0]));

        assertEquals(-1, stream.read());
        assertEquals(0L, stream.getValue());
    }
}
