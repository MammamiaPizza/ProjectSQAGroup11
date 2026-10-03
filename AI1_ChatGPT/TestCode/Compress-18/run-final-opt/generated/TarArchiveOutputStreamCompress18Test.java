package org.apache.commons.compress.archivers.tar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TarArchiveOutputStreamCompress18Test {

    @Test
    public void writesNonAsciiDirectoryUsingPosixPaxHeaders() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        TarArchiveOutputStream archive = new TarArchiveOutputStream(output);
        archive.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
        archive.setAddPaxHeadersForNonAsciiNames(true);

        TarArchiveEntry directory = new TarArchiveEntry("f\u00f6\u00f6/");
        archive.putArchiveEntry(directory);
        archive.closeArchiveEntry();
        archive.finish();

        assertTrue("A completed tar archive must contain header and EOF records",
                archive.getBytesWritten() > 0);
        archive.close();
    }

    @Test(expected = IOException.class)
    public void rejectsPayloadForZeroSizeEntry() throws Exception {
        TarArchiveOutputStream archive =
                new TarArchiveOutputStream(new ByteArrayOutputStream());
        TarArchiveEntry entry = new TarArchiveEntry("empty");
        entry.setSize(0);

        archive.putArchiveEntry(entry);
        archive.write(new byte[] { 1 }, 0, 1);
    }

    @Test
    public void writesDeclaredPayloadAndFinishesArchive() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        TarArchiveOutputStream archive = new TarArchiveOutputStream(output);
        TarArchiveEntry entry = new TarArchiveEntry("data");
        entry.setSize(3);

        archive.putArchiveEntry(entry);
        archive.write(new byte[] { 1, 2, 3 }, 0, 3);
        archive.closeArchiveEntry();
        archive.finish();

        assertTrue("Finished archive should have written data", output.size() > 3);
        archive.close();
    }

    @Test(expected = IOException.class)
    public void rejectsClosingEntryBeforeDeclaredPayloadIsWritten() throws Exception {
        TarArchiveOutputStream archive =
                new TarArchiveOutputStream(new ByteArrayOutputStream());
        TarArchiveEntry entry = new TarArchiveEntry("incomplete");
        entry.setSize(2);

        archive.putArchiveEntry(entry);
        archive.write(new byte[] { 1 }, 0, 1);
        archive.closeArchiveEntry();
    }

    @Test(expected = IOException.class)
    public void rejectsFinishingArchiveWithOpenEntry() throws Exception {
        TarArchiveOutputStream archive =
                new TarArchiveOutputStream(new ByteArrayOutputStream());
        TarArchiveEntry entry = new TarArchiveEntry("open");
        entry.setSize(0);

        archive.putArchiveEntry(entry);
        archive.finish();
    }
}
