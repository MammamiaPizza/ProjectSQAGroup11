package org.apache.commons.compress.archivers.zip;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.junit.Test;

public class ZipArchiveInputStreamUnknownSizeTest {

    @Test
    public void unknownUncompressedSizeDeflatedEntryIsMarkedUnreadable() throws Exception {
        final ZipArchiveInputStream in = new ZipArchiveInputStream(
                new ByteArrayInputStream(createDeflatedEntryWithDataDescriptor()));

        final ZipArchiveEntry entry = in.getNextZipEntry();

        assertEquals(ArchiveEntry.SIZE_UNKNOWN, entry.getSize());
        assertFalse(in.canReadEntryData(entry));
        in.close();
    }

    @Test(expected = UnsupportedZipFeatureException.class)
    public void readingUnknownUncompressedSizeDeflatedEntryIsRejected() throws Exception {
        final ZipArchiveInputStream in = new ZipArchiveInputStream(
                new ByteArrayInputStream(createDeflatedEntryWithDataDescriptor()));
        final ZipArchiveEntry entry = in.getNextZipEntry();

        assertFalse(in.canReadEntryData(entry));
        in.read(new byte[1]);
    }

    @Test
    public void knownSizeStoredEntryRemainsReadableBeforeAndAfterReading() throws Exception {
        final byte[] content = new byte[] { 1, 2, 3, 4, 5 };
        final ZipArchiveInputStream in = new ZipArchiveInputStream(
                new ByteArrayInputStream(createStoredEntry(content)));

        final ZipArchiveEntry entry = in.getNextZipEntry();

        assertEquals(content.length, entry.getSize());
        assertTrue(in.canReadEntryData(entry));

        final byte[] read = new byte[content.length];
        int offset = 0;
        while (offset < read.length) {
            final int count = in.read(read, offset, read.length - offset);
            if (count < 0) {
                break;
            }
            offset += count;
        }

        assertEquals(content.length, offset);
        assertArrayEquals(content, read);
        assertEquals(-1, in.read(new byte[1]));
        assertTrue(in.canReadEntryData(entry));
        in.close();
    }

    private byte[] createDeflatedEntryWithDataDescriptor() throws IOException {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final ZipArchiveOutputStream out = new ZipArchiveOutputStream(bytes);
        final ZipArchiveEntry entry = new ZipArchiveEntry("unknown-size.txt");
        entry.setMethod(ZipArchiveOutputStream.DEFLATED);

        out.putArchiveEntry(entry);
        out.write(new byte[] { 'u', 'n', 'k', 'n', 'o', 'w', 'n' });
        out.closeArchiveEntry();
        out.close();

        return bytes.toByteArray();
    }

    private byte[] createStoredEntry(final byte[] content) throws IOException {
        final CRC32 crc = new CRC32();
        crc.update(content);

        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final ZipArchiveOutputStream out = new ZipArchiveOutputStream(bytes);
        final ZipArchiveEntry entry = new ZipArchiveEntry("known-size.bin");
        entry.setMethod(ZipArchiveOutputStream.STORED);
        entry.setSize(content.length);
        entry.setCompressedSize(content.length);
        entry.setCrc(crc.getValue());

        out.putArchiveEntry(entry);
        out.write(content);
        out.closeArchiveEntry();
        out.close();

        return bytes.toByteArray();
    }
}
