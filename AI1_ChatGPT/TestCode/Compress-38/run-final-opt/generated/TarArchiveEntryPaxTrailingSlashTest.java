package org.apache.commons.compress.archivers.tar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class TarArchiveEntryPaxTrailingSlashTest {

    @Test
    public void readsEntryAfterPaxHeaderWhoseNameEndsWithSlash() throws Exception {
        final byte[] paxData = paxRecord("path", "directory/");
        final ByteArrayOutputStream archive = new ByteArrayOutputStream();

        writeEntry(archive, "PaxHeaders.0/directory/",
                TarConstants.LF_PAX_EXTENDED_HEADER_LC, paxData);
        writeEntry(archive, "placeholder", TarConstants.LF_NORMAL, new byte[0]);
        archive.write(new byte[TarConstants.DEFAULT_RCDSIZE * 2]);

        final TarArchiveInputStream input =
                new TarArchiveInputStream(new ByteArrayInputStream(archive.toByteArray()));

        final TarArchiveEntry entry = input.getNextTarEntry();

        assertEquals("directory/", entry.getName());
        assertTrue(entry.isDirectory());
        assertNull(input.getNextTarEntry());
        input.close();
    }

    @Test
    public void trailingSlashNameCreatesDirectoryEntry() {
        final TarArchiveEntry entry = new TarArchiveEntry("directory/");

        assertTrue(entry.isDirectory());
        assertFalse(entry.isFile());
        assertEquals(TarArchiveEntry.DEFAULT_DIR_MODE, entry.getMode());
    }

    private static void writeEntry(final ByteArrayOutputStream archive, final String name,
            final byte type, final byte[] data) {
        final TarArchiveEntry entry = new TarArchiveEntry(name, type);
        entry.setSize(data.length);

        final byte[] header = new byte[TarConstants.DEFAULT_RCDSIZE];
        entry.writeEntryHeader(header);
        archive.write(header, 0, header.length);
        archive.write(data, 0, data.length);

        final int remainder = data.length % TarConstants.DEFAULT_RCDSIZE;
        if (remainder != 0) {
            archive.write(new byte[TarConstants.DEFAULT_RCDSIZE - remainder], 0,
                    TarConstants.DEFAULT_RCDSIZE - remainder);
        }
    }

    private static byte[] paxRecord(final String key, final String value) {
        final String body = key + "=" + value + "\n";
        int length = body.length() + 2;
        while (true) {
            final int calculated = body.length()
                    + Integer.toString(length).length() + 1;
            if (calculated == length) {
                return (length + " " + body).getBytes(StandardCharsets.US_ASCII);
            }
            length = calculated;
        }
    }
}
