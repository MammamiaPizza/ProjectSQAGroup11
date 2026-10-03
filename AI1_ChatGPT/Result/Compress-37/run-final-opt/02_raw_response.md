package org.apache.commons.compress.archivers.tar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TarArchiveInputStreamPaxBlankLinesTest {

    private static final int RECORD_SIZE = 512;
    private static final Charset ASCII = Charset.forName("US-ASCII");

    @Test
    public void appliesValidPaxPathToFollowingEntry() throws Exception {
        String pax = paxRecord("path", "renamed.txt");

        assertPaxArchive(pax, "renamed.txt");
    }

    @Test
    public void survivesBlankLineBeforePaxRecord() throws Exception {
        String pax = "\n" + paxRecord("path", "before.txt");

        assertPaxArchive(pax, "before.txt");
    }

    @Test
    public void survivesBlankLineBetweenPaxRecords() throws Exception {
        String pax = paxRecord("path", "first.txt")
                + "\n"
                + paxRecord("path", "second.txt");

        assertPaxArchive(pax, "second.txt");
    }

    @Test
    public void survivesBlankLineAfterPaxRecord() throws Exception {
        String pax = paxRecord("path", "after.txt") + "\n";

        assertPaxArchive(pax, "after.txt");
    }

    @Test
    public void survivesPaxHeaderContainingOnlyBlankLine() throws Exception {
        assertPaxArchive("\n", "original.txt");
    }

    private void assertPaxArchive(String paxContents, String expectedName) throws Exception {
        TarArchiveInputStream input = new TarArchiveInputStream(
                new ByteArrayInputStream(createArchive(paxContents)));

        TarArchiveEntry entry = input.getNextTarEntry();
        assertEquals(expectedName, entry.getName());

        byte[] contents = new byte[4];
        assertEquals(4, input.read(contents));
        assertArrayEquals("data".getBytes(ASCII), contents);

        assertNull(input.getNextTarEntry());
        input.close();
    }

    private byte[] createArchive(String paxContents) throws IOException {
        ByteArrayOutputStream archive = new ByteArrayOutputStream();
        byte[] paxData = paxContents.getBytes(ASCII);
        byte[] fileData = "data".getBytes(ASCII);

        writeEntry(archive, "PaxHeader", (byte) 'x', paxData);
        writeEntry(archive, "original.txt", (byte) '0', fileData);
        archive.write(new byte[RECORD_SIZE * 2]);

        return archive.toByteArray();
    }

    private void writeEntry(ByteArrayOutputStream archive, String name, byte type,
                            byte[] data) throws IOException {
        byte[] header = new byte[RECORD_SIZE];

        putAscii(header, 0, 100, name);
        putOctal(header, 100, 8, 0644);
        putOctal(header, 108, 8, 0);
        putOctal(header, 116, 8, 0);
        putOctal(header, 124, 12, data.length);
        putOctal(header, 136, 12, 0);

        for (int i = 148; i < 156; i++) {
            header[i] = (byte) ' ';
        }

        header[156] = type;
        putAscii(header, 257, 6, "ustar");
        putAscii(header, 263, 2, "00");

        int checksum = 0;
        for (int i = 0; i < header.length; i++) {
            checksum += header[i] & 0xff;
        }
        putChecksum(header, checksum);

        archive.write(header);
        archive.write(data);

        int padding = (RECORD_SIZE - (data.length % RECORD_SIZE)) % RECORD_SIZE;
        if (padding > 0) {
            archive.write(new byte[padding]);
        }
    }

    private String paxRecord(String key, String value) {
        String data = key + "=" + value + "\n";
        int length = data.length() + 2;
        int previous;
        do {
            previous = length;
            length = data.length() + String.valueOf(length).length() + 1;
        } while (length != previous);
        return length + " " + data;
    }

    private void putAscii(byte[] buffer, int offset, int length, String value) {
        byte[] bytes = value.getBytes(ASCII);
        System.arraycopy(bytes, 0, buffer, offset, Math.min(length, bytes.length));
    }

    private void putOctal(byte[] buffer, int offset, int length, long value) {
        String octal = Long.toOctalString(value);
        int start = offset + length - octal.length() - 1;
        for (int i = offset; i < start; i++) {
            buffer[i] = (byte) '0';
        }
        putAscii(buffer, start, octal.length(), octal);
        buffer[offset + length - 1] = 0;
    }

    private void putChecksum(byte[] buffer, int checksum) {
        String octal = Long.toOctalString(checksum);
        int start = 148 + 6 - octal.length();
        for (int i = 148; i < start; i++) {
            buffer[i] = (byte) '0';
        }
        putAscii(buffer, start, octal.length(), octal);
        buffer[154] = 0;
        buffer[155] = (byte) ' ';
    }
}