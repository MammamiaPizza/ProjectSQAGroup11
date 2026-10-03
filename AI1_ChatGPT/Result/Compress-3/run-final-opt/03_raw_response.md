package org.apache.commons.compress.archivers.tar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

public class TarArchiveOutputStreamFinishTest extends TestCase {

    public void testFinishRejectsUnclosedEntry() throws Exception {
        TarArchiveOutputStream archive =
            new TarArchiveOutputStream(new ByteArrayOutputStream(), 512, 512);
        TarArchiveEntry entry = new TarArchiveEntry("unfinished.txt");
        entry.setSize(0);

        archive.putArchiveEntry(entry);

        try {
            archive.finish();
            fail("finish must reject an archive containing an unclosed entry");
        } catch (IOException expected) {
            assertNotNull(expected);
        }
    }

    public void testCloseRejectsUnclosedEntry() throws Exception {
        TarArchiveOutputStream archive =
            new TarArchiveOutputStream(new ByteArrayOutputStream(), 512, 512);
        TarArchiveEntry entry = new TarArchiveEntry("unfinished.txt");
        entry.setSize(0);

        archive.putArchiveEntry(entry);

        try {
            archive.close();
            fail("close must reject an archive containing an unclosed entry");
        } catch (IOException expected) {
            assertNotNull(expected);
        }
    }

    public void testFinishSucceedsAfterEntryIsClosed() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        TarArchiveOutputStream archive =
            new TarArchiveOutputStream(output, 512, 512);
        TarArchiveEntry entry = new TarArchiveEntry("file.txt");
        entry.setSize(1);

        archive.putArchiveEntry(entry);
        archive.write(new byte[] { 42 });
        archive.closeArchiveEntry();
        archive.finish();
        archive.close();

        assertEquals(2048, output.size());
    }

    public void testFinishWritesTwoEndOfArchiveRecordsWhenNoEntriesExist()
        throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        TarArchiveOutputStream archive =
            new TarArchiveOutputStream(output, 512, 512);

        archive.finish();
        archive.close();

        assertEquals(1024, output.size());
    }
}