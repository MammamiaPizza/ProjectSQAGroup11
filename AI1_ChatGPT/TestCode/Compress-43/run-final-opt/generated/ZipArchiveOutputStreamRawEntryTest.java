package org.apache.commons.compress.archivers.zip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Deflater;

import org.junit.Test;

public class ZipArchiveOutputStreamRawEntryTest {

    @Test
    public void rawDeflatedEntryWithKnownSizesDoesNotUseDataDescriptor() throws Exception {
        final byte[] original = "raw deflated entry content".getBytes("US-ASCII");
        final byte[] compressed = deflate(original);
        final CRC32 crc = new CRC32();
        crc.update(original);

        final ZipArchiveEntry entry = new ZipArchiveEntry("raw.txt");
        entry.setMethod(ZipArchiveOutputStream.DEFLATED);
        entry.setCrc(crc.getValue());
        entry.setSize(original.length);
        entry.setCompressedSize(compressed.length);

        final byte[] archive = writeRawEntry(entry, compressed);

        assertEquals(0x04034b50L, unsignedInt(archive, 0));
        assertEquals(0, unsignedShort(archive, 6) & 0x0008);
        assertEquals(crc.getValue(), unsignedInt(archive, 14));
        assertEquals(compressed.length, unsignedInt(archive, 18));
        assertEquals(original.length, unsignedInt(archive, 22));

        final int payloadOffset = 30 + unsignedShort(archive, 26) + unsignedShort(archive, 28);
        final int centralDirectoryOffset = payloadOffset + compressed.length;
        assertEquals(0x02014b50L, unsignedInt(archive, centralDirectoryOffset));
        assertEquals(0, unsignedShort(archive, centralDirectoryOffset + 8) & 0x0008);
    }

    @Test
    public void emptyRawStoredEntryWithKnownMetadataDoesNotUseDataDescriptor() throws Exception {
        final ZipArchiveEntry entry = new ZipArchiveEntry("empty");
        entry.setMethod(ZipArchiveOutputStream.STORED);
        entry.setCrc(0);
        entry.setSize(0);
        entry.setCompressedSize(0);

        final byte[] archive = writeRawEntry(entry, new byte[0]);

        assertEquals(0x04034b50L, unsignedInt(archive, 0));
        assertEquals(0, unsignedShort(archive, 6) & 0x0008);
        assertEquals(0, unsignedInt(archive, 14));
        assertEquals(0, unsignedInt(archive, 18));
        assertEquals(0, unsignedInt(archive, 22));

        final int payloadOffset = 30 + unsignedShort(archive, 26) + unsignedShort(archive, 28);
        assertEquals(0x02014b50L, unsignedInt(archive, payloadOffset));
        assertEquals(0, unsignedShort(archive, payloadOffset + 8) & 0x0008);
        assertTrue(archive.length > payloadOffset);
    }

    private static byte[] writeRawEntry(final ZipArchiveEntry entry, final byte[] rawData) throws IOException {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final ZipArchiveOutputStream output = new ZipArchiveOutputStream(bytes);
        output.addRawArchiveEntry(entry, new ByteArrayInputStream(rawData));
        output.finish();
        output.close();
        return bytes.toByteArray();
    }

    private static byte[] deflate(final byte[] input) {
        final Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION, true);
        try {
            deflater.setInput(input);
            deflater.finish();
            final ByteArrayOutputStream result = new ByteArrayOutputStream();
            final byte[] buffer = new byte[64];
            while (!deflater.finished()) {
                final int count = deflater.deflate(buffer);
                result.write(buffer, 0, count);
            }
            return result.toByteArray();
        } finally {
            deflater.end();
        }
    }

    private static int unsignedShort(final byte[] data, final int offset) {
        return (data[offset] & 0xff) | ((data[offset + 1] & 0xff) << 8);
    }

    private static long unsignedInt(final byte[] data, final int offset) {
        return (data[offset] & 0xffL)
                | ((data[offset + 1] & 0xffL) << 8)
                | ((data[offset + 2] & 0xffL) << 16)
                | ((data[offset + 3] & 0xffL) << 24);
    }
}
