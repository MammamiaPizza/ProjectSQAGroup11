package org.apache.commons.compress.archivers.tar;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

public class TarArchiveInputStreamTruncatedEntryTest {

    @Test
    public void readsExactlyDeclaredEntryDataAndThenReturnsEndOfEntry() throws Exception {
        byte[] data = new byte[] { 10, 20, 30, 40, 50 };
        TarArchiveInputStream input = new TarArchiveInputStream(
                new ByteArrayInputStream(createArchive(data, data.length, true)));

        TarArchiveEntry entry = input.getNextTarEntry();
        assertEquals("file", entry.getName());
        assertEquals(data.length, entry.getSize());

        byte[] buffer = new byte[8];
        assertEquals(data.length, input.read(buffer, 1, buffer.length - 1));
        for (int i = 0; i < data.length; i++) {
            assertEquals(data[i], buffer[i + 1]);
        }
        assertEquals(-1, input.read(buffer, 0, 1));

        input.close();
    }

    @Test(expected = IOException.class)
    public void throwsIOExceptionWhenReadingBeyondPhysicallyTruncatedEntryBody() throws Exception {
        byte[] partialBody = new byte[] { 1, 2 };
        TarArchiveInputStream input = new TarArchiveInputStream(
                new ByteArrayInputStream(createArchive(partialBody, 5, false)));

        assertEquals(5, input.getNextTarEntry().getSize());

        byte[] buffer = new byte[5];
        assertEquals(2, input.read(buffer, 0, buffer.length));

        input.read(buffer, 2, 3);
    }

    @Test
    public void returnsNoNextEntryWhenAdvancingPastUnreadTruncatedEntry() throws Exception {
        byte[] partialBody = new byte[] { 1, 2 };
        TarArchiveInputStream input = new TarArchiveInputStream(
                new ByteArrayInputStream(createArchive(partialBody, 5, false)));

        assertEquals(5, input.getNextTarEntry().getSize());

        assertEquals(null, input.getNextTarEntry());
    }

    private static byte[] createArchive(byte[] body, int declaredSize, boolean completeArchive)
            throws IOException {
        byte[] header = new byte[512];
        putAscii(header, 0, "file");
        writeOctal(header, 100, 8, 0644);
        writeOctal(header, 108, 8, 0);
        writeOctal(header, 116, 8, 0);
        writeOctal(header, 124, 12, declaredSize);
        writeOctal(header, 136, 12, 0);
        for (int i = 148; i < 156; i++) {
            header[i] = (byte) ' ';
        }
        header[156] = (byte) '0';
        putAscii(header, 257, "ustar");
        header[262] = 0;
        putAscii(header, 263, "00");
        writeChecksum(header);

        ByteArrayOutputStream archive = new ByteArrayOutputStream();
        archive.write(header);
        archive.write(body);

        if (completeArchive) {
            int padding = (512 - (body.length % 512)) % 512;
            archive.write(new byte[padding]);
            archive.write(new byte[1024]);
        }

        return archive.toByteArray();
    }

    private static void putAscii(byte[] target, int offset, String value) {
        for (int i = 0; i < value.length(); i++) {
            target[offset + i] = (byte) value.charAt(i);
        }
    }

    private static void writeOctal(byte[] target, int offset, int length, long value) {
        String octal = Long.toOctalString(value);
        int digits = length - 1;
        for (int i = 0; i < digits; i++) {
            target[offset + i] = (byte) '0';
        }
        for (int i = 0; i < octal.length(); i++) {
            target[offset + digits - octal.length() + i] = (byte) octal.charAt(i);
        }
        target[offset + length - 1] = 0;
    }

    private static void writeChecksum(byte[] header) {
        long checksum = 0;
        for (int i = 0; i < header.length; i++) {
            checksum += header[i] & 0xff;
        }

        String octal = Long.toOctalString(checksum);
        for (int i = 0; i < 6; i++) {
            header[148 + i] = (byte) '0';
        }
        for (int i = 0; i < octal.length(); i++) {
            header[148 + 6 - octal.length() + i] = (byte) octal.charAt(i);
        }
        header[154] = 0;
        header[155] = (byte) ' ';
    }
}