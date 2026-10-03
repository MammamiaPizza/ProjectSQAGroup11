package org.apache.commons.compress.archivers.cpio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class CpioArchiveInputStreamPermissionModeTest {

    @Test
    public void parsesOldAsciiPermissionOnlyModeAsRegularFileAndReadsPayload()
            throws Exception {
        byte[] archive = archive(
                oldAsciiEntry("redline.txt", 0100644, ascii("redline")),
                trailer());

        CpioArchiveInputStream input =
                new CpioArchiveInputStream(new ByteArrayInputStream(archive));

        CpioArchiveEntry entry = input.getNextCPIOEntry();

        assertNotNull(entry);
        assertEquals("redline.txt", entry.getName());
        assertEquals(7, entry.getSize());
        assertEquals(0100644, entry.getMode());

        byte[] content = new byte[7];
        assertEquals(7, input.read(content));
        assertArrayEquals(ascii("redline"), content);
        assertEquals(-1, input.read(new byte[1]));
        assertNull(input.getNextCPIOEntry());
    }

    @Test
    public void advancingToNextEntrySkipsUnreadDataOfPermissionOnlyOldAsciiEntry()
            throws Exception {
        byte[] archive = archive(
                oldAsciiEntry("first", 0100600, ascii("unread-data")),
                oldAsciiEntry("second", 0100640, ascii("ok")),
                trailer());

        CpioArchiveInputStream input =
                new CpioArchiveInputStream(new ByteArrayInputStream(archive));

        CpioArchiveEntry first = input.getNextCPIOEntry();
        assertNotNull(first);
        assertEquals(0100600, first.getMode());

        CpioArchiveEntry second = input.getNextCPIOEntry();
        assertNotNull(second);
        assertEquals("second", second.getName());
        assertEquals(2, second.getSize());

        byte[] content = new byte[2];
        assertEquals(2, input.read(content));
        assertArrayEquals(ascii("ok"), content);
        assertNull(input.getNextCPIOEntry());
    }

    @Test
    public void preservesExplicitDirectoryTypeInOldAsciiMode() throws Exception {
        byte[] archive = archive(
                oldAsciiEntry("directory", 0040755, new byte[0]),
                trailer());

        CpioArchiveInputStream input =
                new CpioArchiveInputStream(new ByteArrayInputStream(archive));

        CpioArchiveEntry entry = input.getNextCPIOEntry();

        assertNotNull(entry);
        assertEquals("directory", entry.getName());
        assertEquals(0040755, entry.getMode());
        assertEquals(0, entry.getSize());
        assertEquals(-1, input.read(new byte[1]));
        assertNull(input.getNextCPIOEntry());
    }

    @Test(expected = IOException.class)
    public void rejectsZeroModeForNonTrailerOldAsciiEntry() throws Exception {
        byte[] archive = oldAsciiEntry("invalid", 0, new byte[0]);

        CpioArchiveInputStream input =
                new CpioArchiveInputStream(new ByteArrayInputStream(archive));

        input.getNextCPIOEntry();
    }

    private static byte[] trailer() throws Exception {
        return oldAsciiEntry("TRAILER!!!", 0, new byte[0]);
    }

    private static byte[] archive(byte[]... entries) throws Exception {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        for (int i = 0; i < entries.length; i++) {
            result.write(entries[i]);
        }
        return result.toByteArray();
    }

    private static byte[] oldAsciiEntry(String name, long mode, byte[] data)
            throws Exception {
        byte[] nameBytes = ascii(name);
        String header =
                "070707"
                + octal(0, 6)
                + octal(0, 6)
                + octal(mode, 6)
                + octal(0, 6)
                + octal(0, 6)
                + octal(1, 6)
                + octal(0, 6)
                + octal(0, 11)
                + octal(nameBytes.length + 1, 6)
                + octal(data.length, 11);

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        result.write(ascii(header));
        result.write(nameBytes);
        result.write(0);
        result.write(data);
        return result.toByteArray();
    }

    private static String octal(long value, int width) {
        String text = Long.toOctalString(value);
        StringBuilder result = new StringBuilder(width);
        for (int i = text.length(); i < width; i++) {
            result.append('0');
        }
        result.append(text);
        return result.toString();
    }

    private static byte[] ascii(String value) throws Exception {
        return value.getBytes("US-ASCII");
    }
}
