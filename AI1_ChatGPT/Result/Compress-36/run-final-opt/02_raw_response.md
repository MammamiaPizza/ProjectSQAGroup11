package org.apache.commons.compress.archivers.sevenz;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SevenZFileEmptyEntryReadTest {

    @Test
    public void readSingleByteFromEmptyEntryReturnsEndOfFile() throws Exception {
        final File archive = createArchiveContainingEmptyEntry();
        SevenZFile sevenZ = null;
        try {
            sevenZ = new SevenZFile(archive);
            final SevenZArchiveEntry entry = sevenZ.getNextEntry();

            assertNotNull(entry);
            assertEquals(0L, entry.getSize());
            assertEquals(-1, sevenZ.read());
        } finally {
            closeAndDelete(sevenZ, archive);
        }
    }

    @Test
    public void readByteArrayFromEmptyEntryReturnsEndOfFileWithoutChangingBuffer() throws Exception {
        final File archive = createArchiveContainingEmptyEntry();
        SevenZFile sevenZ = null;
        try {
            sevenZ = new SevenZFile(archive);
            assertNotNull(sevenZ.getNextEntry());

            final byte[] buffer = new byte[] { 11, 22, 33 };
            assertEquals(-1, sevenZ.read(buffer));
            assertEquals(11, buffer[0]);
            assertEquals(22, buffer[1]);
            assertEquals(33, buffer[2]);
        } finally {
            closeAndDelete(sevenZ, archive);
        }
    }

    @Test
    public void readByteArrayRangeFromEmptyEntryReturnsEndOfFileWithoutChangingBuffer() throws Exception {
        final File archive = createArchiveContainingEmptyEntry();
        SevenZFile sevenZ = null;
        try {
            sevenZ = new SevenZFile(archive);
            assertNotNull(sevenZ.getNextEntry());

            final byte[] buffer = new byte[] { 44, 55, 66, 77 };
            assertEquals(-1, sevenZ.read(buffer, 1, 2));
            assertEquals(44, buffer[0]);
            assertEquals(55, buffer[1]);
            assertEquals(66, buffer[2]);
            assertEquals(77, buffer[3]);
        } finally {
            closeAndDelete(sevenZ, archive);
        }
    }

    @Test(expected = IllegalStateException.class)
    public void readBeforeSelectingAnyEntryIsRejected() throws Exception {
        final File archive = createArchiveContainingEmptyEntry();
        SevenZFile sevenZ = null;
        try {
            sevenZ = new SevenZFile(archive);
            sevenZ.read();
        } finally {
            closeAndDelete(sevenZ, archive);
        }
    }

    private File createArchiveContainingEmptyEntry() throws IOException {
        final File archive = File.createTempFile("seven-z-empty-entry", ".7z");
        SevenZOutputFile output = null;
        try {
            output = new SevenZOutputFile(archive);
            final SevenZArchiveEntry entry = new SevenZArchiveEntry();
            entry.setName("empty");
            entry.setSize(0);
            output.putArchiveEntry(entry);
            output.closeArchiveEntry();
        } finally {
            if (output != null) {
                output.close();
            }
        }
        return archive;
    }

    private void closeAndDelete(final SevenZFile sevenZ, final File archive) throws IOException {
        try {
            if (sevenZ != null) {
                sevenZ.close();
            }
        } finally {
            if (archive != null) {
                archive.delete();
            }
        }
    }
}