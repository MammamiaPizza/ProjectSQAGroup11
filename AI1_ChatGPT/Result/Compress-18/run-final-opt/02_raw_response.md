package org.apache.commons.compress.archivers.tar;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

public class TarArchiveOutputStreamBug18Test {

    @Test
    public void testWriteNonAsciiDirectoryNamePosixMode() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        TarArchiveOutputStream output =
            new TarArchiveOutputStream(bytes, "US-ASCII");
        output.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
        output.setAddPaxHeadersForNonAsciiNames(true);

        TarArchiveEntry directory = new TarArchiveEntry("\u00e6\u00f6\u00f6/");
        directory.setSize(0);

        output.putArchiveEntry(directory);
        output.closeArchiveEntry();
        output.finish();

        assertTrue("a completed tar archive should contain records",
                   bytes.size() > 0);
    }

    @Test
    public void testEmptyDirectoryCanBeClosedAndFinishedWithoutPayload()
        throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        TarArchiveOutputStream output = new TarArchiveOutputStream(bytes);

        TarArchiveEntry directory = new TarArchiveEntry("directory/");
        directory.setSize(0);

        output.putArchiveEntry(directory);
        output.closeArchiveEntry();
        output.finish();

        assertTrue(bytes.size() > 0);
    }

    @Test
    public void testWritingPayloadToZeroSizeEntryFails() throws Exception {
        TarArchiveOutputStream output =
            new TarArchiveOutputStream(new ByteArrayOutputStream());
        TarArchiveEntry entry = new TarArchiveEntry("empty");
        entry.setSize(0);
        output.putArchiveEntry(entry);

        try {
            output.write(new byte[] { 1 }, 0, 1);
            fail("writing bytes beyond the declared entry size must fail");
        } catch (IOException expected) {
            assertTrue(expected.getMessage().contains("exceeds size"));
        }
    }

    @Test
    public void testClosingEntryBeforeDeclaredPayloadIsWrittenFails()
        throws Exception {
        TarArchiveOutputStream output =
            new TarArchiveOutputStream(new ByteArrayOutputStream());
        TarArchiveEntry entry = new TarArchiveEntry("short");
        entry.setSize(2);
        output.putArchiveEntry(entry);
        output.write(new byte[] { 1 }, 0, 1);

        try {
            output.closeArchiveEntry();
            fail("closing an incompletely written entry must fail");
        } catch (IOException expected) {
            assertTrue(expected.getMessage().contains("before the"));
        }
    }

    @Test
    public void testExactDeclaredPayloadCanBeClosedAndFinished()
        throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        TarArchiveOutputStream output = new TarArchiveOutputStream(bytes);
        TarArchiveEntry entry = new TarArchiveEntry("payload");
        entry.setSize(2);

        output.putArchiveEntry(entry);
        output.write(new byte[] { 10, 20 }, 0, 2);
        output.closeArchiveEntry();
        output.finish();

        assertTrue(bytes.size() > 0);
    }

    @Test
    public void testClosingWithoutAnOpenEntryFails() throws Exception {
        TarArchiveOutputStream output =
            new TarArchiveOutputStream(new ByteArrayOutputStream());

        try {
            output.closeArchiveEntry();
            fail("closing without a current entry must fail");
        } catch (IOException expected) {
            assertTrue(expected.getMessage().contains("No current entry"));
        }
    }
}