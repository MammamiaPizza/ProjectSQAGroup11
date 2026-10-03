package org.apache.commons.compress.archivers.tar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TarArchiveOutputStreamCountRegressionTest {

    @Test
    public void writesAndCountsCompleteDefaultBlockForSmallEntry() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        TarArchiveOutputStream out = new TarArchiveOutputStream(bytes);

        TarArchiveEntry entry = new TarArchiveEntry("small");
        entry.setSize(5);
        out.putArchiveEntry(entry);
        out.write(new byte[] { 1, 2, 3, 4, 5 });
        out.closeArchiveEntry();
        out.finish();

        assertEquals(10240, bytes.size());
        assertEquals(10240, out.getBytesWritten());
    }

    @Test
    public void writesAndCountsAdditionalBlockWhenEntryCrossesBlockBoundary() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        TarArchiveOutputStream out = new TarArchiveOutputStream(bytes);

        byte[] content = new byte[19 * 512];
        for (int i = 0; i < content.length; i++) {
            content[i] = (byte) i;
        }

        TarArchiveEntry entry = new TarArchiveEntry("large");
        entry.setSize(content.length);
        out.putArchiveEntry(entry);
        out.write(content);
        out.closeArchiveEntry();
        out.finish();

        assertEquals(20480, bytes.size());
        assertEquals(20480, out.getBytesWritten());
    }

    @Test
    public void closeAlsoFinishesAndCountsCompleteArchiveBlock() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        TarArchiveOutputStream out = new TarArchiveOutputStream(bytes);

        TarArchiveEntry entry = new TarArchiveEntry("empty");
        entry.setSize(0);
        out.putArchiveEntry(entry);
        out.closeArchiveEntry();
        out.close();

        assertEquals(10240, bytes.size());
        assertEquals(10240, out.getBytesWritten());
    }

    @Test
    public void rejectsWritingMoreBytesThanDeclaredEntrySize() throws Exception {
        TarArchiveOutputStream out = new TarArchiveOutputStream(new ByteArrayOutputStream());
        TarArchiveEntry entry = new TarArchiveEntry("limited");
        entry.setSize(1);
        out.putArchiveEntry(entry);

        try {
            out.write(new byte[] { 1, 2 });
            fail("Writing beyond the declared entry size must fail");
        } catch (IOException expected) {
            assertEquals(true, expected.getMessage().contains("exceeds size"));
        }
    }

    @Test
    public void rejectsClosingEntryBeforeAllDeclaredBytesHaveBeenWritten() throws Exception {
        TarArchiveOutputStream out = new TarArchiveOutputStream(new ByteArrayOutputStream());
        TarArchiveEntry entry = new TarArchiveEntry("short");
        entry.setSize(2);
        out.putArchiveEntry(entry);
        out.write(new byte[] { 1 });

        try {
            out.closeArchiveEntry();
            fail("Closing an undersized entry must fail");
        } catch (IOException expected) {
            assertEquals(true, expected.getMessage().contains("before the"));
        }
    }

    @Test
    public void rejectsFinishingArchiveWithUnclosedEntry() throws Exception {
        TarArchiveOutputStream out = new TarArchiveOutputStream(new ByteArrayOutputStream());
        TarArchiveEntry entry = new TarArchiveEntry("open");
        entry.setSize(0);
        out.putArchiveEntry(entry);

        try {
            out.finish();
            fail("Finishing an archive with an open entry must fail");
        } catch (IOException expected) {
            assertEquals(true, expected.getMessage().contains("unclosed entries"));
        }
    }
}
