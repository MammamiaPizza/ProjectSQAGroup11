package org.apache.commons.compress.archivers.cpio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

public class CpioArchiveOutputStreamTest extends TestCase implements CpioConstants {

    public void testCloseFinalizesArchiveWithTrailer() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        CpioArchiveOutputStream out = new CpioArchiveOutputStream(bytes);
        byte[] data = new byte[] { 10, 20, 30 };

        CpioArchiveEntry entry = createEntry(FORMAT_NEW, "file", data.length);
        out.putNextEntry(entry);
        out.write(data);
        out.closeArchiveEntry();
        out.close();

        CpioArchiveInputStream in =
            new CpioArchiveInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        CpioArchiveEntry read = in.getNextCPIOEntry();
        assertNotNull(read);
        assertEquals("file", read.getName());
        assertEntryData(in, data);
        assertNull(in.getNextCPIOEntry());
        in.close();
    }

    public void testNewFormatRoundTripsAlignedNamesAndConsecutivePayloads()
        throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        CpioArchiveOutputStream out = new CpioArchiveOutputStream(bytes, FORMAT_NEW);

        byte[] firstData = new byte[] { 1, 2, 3 };
        CpioArchiveEntry first = createEntry(FORMAT_NEW, "abc", firstData.length);
        out.putNextEntry(first);
        out.write(firstData);
        out.closeArchiveEntry();

        byte[] secondData = new byte[] { 4, 5, 6, 7, 8 };
        CpioArchiveEntry second =
            createEntry(FORMAT_NEW, "four", secondData.length);
        out.putNextEntry(second);
        out.write(secondData);
        out.closeArchiveEntry();
        out.finish();
        out.close();

        CpioArchiveInputStream in =
            new CpioArchiveInputStream(new ByteArrayInputStream(bytes.toByteArray()));

        CpioArchiveEntry readFirst = in.getNextCPIOEntry();
        assertNotNull(readFirst);
        assertEquals("abc", readFirst.getName());
        assertEntryData(in, firstData);

        CpioArchiveEntry readSecond = in.getNextCPIOEntry();
        assertNotNull(readSecond);
        assertEquals("four", readSecond.getName());
        assertEntryData(in, secondData);

        assertNull(in.getNextCPIOEntry());
        in.close();
    }

    public void testOldAsciiFormatRoundTripsEmptyAndNonEmptyEntries()
        throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        CpioArchiveOutputStream out =
            new CpioArchiveOutputStream(bytes, FORMAT_OLD_ASCII);

        CpioArchiveEntry empty = createEntry(FORMAT_OLD_ASCII, "empty", 0);
        out.putNextEntry(empty);
        out.closeArchiveEntry();

        byte[] data = new byte[] { 9, 8, 7 };
        CpioArchiveEntry content =
            createEntry(FORMAT_OLD_ASCII, "content", data.length);
        out.putNextEntry(content);
        out.write(data);
        out.closeArchiveEntry();
        out.finish();
        out.close();

        CpioArchiveInputStream in =
            new CpioArchiveInputStream(new ByteArrayInputStream(bytes.toByteArray()));

        CpioArchiveEntry readEmpty = in.getNextCPIOEntry();
        assertNotNull(readEmpty);
        assertEquals("empty", readEmpty.getName());
        assertEquals(0L, readEmpty.getSize());

        CpioArchiveEntry readContent = in.getNextCPIOEntry();
        assertNotNull(readContent);
        assertEquals("content", readContent.getName());
        assertEntryData(in, data);

        assertNull(in.getNextCPIOEntry());
        in.close();
    }

    public void testOldBinaryFormatRoundTripsPaddedPayload()
        throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        CpioArchiveOutputStream out =
            new CpioArchiveOutputStream(bytes, FORMAT_OLD_BINARY);

        byte[] data = new byte[] { 42, 43, 44 };
        CpioArchiveEntry entry =
            createEntry(FORMAT_OLD_BINARY, "bin", data.length);
        out.putNextEntry(entry);
        out.write(data);
        out.closeArchiveEntry();
        out.finish();
        out.close();

        CpioArchiveInputStream in =
            new CpioArchiveInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        CpioArchiveEntry read = in.getNextCPIOEntry();
        assertNotNull(read);
        assertEquals("bin", read.getName());
        assertEntryData(in, data);
        assertNull(in.getNextCPIOEntry());
        in.close();
    }

    public void testNewCrcChecksumUsesWrittenArrayOffset() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        CpioArchiveOutputStream out =
            new CpioArchiveOutputStream(bytes, FORMAT_NEW_CRC);

        byte[] source = new byte[] { 1, 2, 3, 4, 5 };
        CpioArchiveEntry entry =
            createEntry(FORMAT_NEW_CRC, "crc", 3);
        entry.setChksum(9);
        out.putNextEntry(entry);
        out.write(source, 1, 3);
        out.closeArchiveEntry();
        out.finish();
        out.close();

        CpioArchiveInputStream in =
            new CpioArchiveInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        CpioArchiveEntry read = in.getNextCPIOEntry();
        assertNotNull(read);
        assertEquals("crc", read.getName());
        assertEntryData(in, new byte[] { 2, 3, 4 });
        assertNull(in.getNextCPIOEntry());
        in.close();
    }

    public void testSingleByteWriteIsCountedAsEntryData() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        CpioArchiveOutputStream out = new CpioArchiveOutputStream(bytes);

        CpioArchiveEntry entry = createEntry(FORMAT_NEW, "one", 1);
        out.putNextEntry(entry);
        out.write(65);
        out.closeArchiveEntry();
        out.finish();
        out.close();

        CpioArchiveInputStream in =
            new CpioArchiveInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        CpioArchiveEntry read = in.getNextCPIOEntry();
        assertNotNull(read);
        assertEquals("one", read.getName());
        assertEntryData(in, new byte[] { 65 });
        assertNull(in.getNextCPIOEntry());
        in.close();
    }

    public void testCloseArchiveEntryRejectsIncompletePayload() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        CpioArchiveOutputStream out = new CpioArchiveOutputStream(bytes);

        CpioArchiveEntry entry = createEntry(FORMAT_NEW, "short", 2);
        out.putNextEntry(entry);
        out.write(new byte[] { 1 });

        try {
            out.closeArchiveEntry();
            fail("Closing an entry with too few bytes must fail");
        } catch (IOException expected) {
            assertTrue(expected.getMessage().indexOf("invalid entry size") >= 0);
        }
    }

    public void testFinishIsIdempotent() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        CpioArchiveOutputStream out = new CpioArchiveOutputStream(bytes);

        out.finish();
        int lengthAfterFirstFinish = bytes.size();
        out.finish();

        assertEquals(lengthAfterFirstFinish, bytes.size());
        out.close();
    }

    private CpioArchiveEntry createEntry(short format, String name, long size) {
        CpioArchiveEntry entry = new CpioArchiveEntry(format);
        entry.setName(name);
        entry.setSize(size);
        entry.setTime(0);
        return entry;
    }

    private void assertEntryData(CpioArchiveInputStream in, byte[] expected)
        throws IOException {
        byte[] actual = new byte[expected.length];
        int offset = 0;
        while (offset < actual.length) {
            int read = in.read(actual, offset, actual.length - offset);
            assertTrue("unexpected end of entry", read > 0);
            offset += read;
        }
        for (int i = 0; i < expected.length; i++) {
            assertEquals("byte " + i, expected[i] & 0xff, actual[i] & 0xff);
        }
    }
}