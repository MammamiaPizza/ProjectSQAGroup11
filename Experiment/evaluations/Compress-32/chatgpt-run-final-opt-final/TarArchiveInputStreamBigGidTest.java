package org.apache.commons.compress.archivers.tar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

public class TarArchiveInputStreamBigGidTest {

    @Test
    public void readsPaxGidLargerThanUnsignedIntRange() throws Exception {
        TarArchiveEntry entry = readEntryWithPaxGid("4294967294");

        assertNotNull(entry);
        assertEquals(4294967294L, entry.getLongGroupId());
    }

    @Test
    public void readsPaxGidJustBeyondSignedIntMaximum() throws Exception {
        TarArchiveEntry entry = readEntryWithPaxGid("2147483648");

        assertNotNull(entry);
        assertEquals(2147483648L, entry.getLongGroupId());
    }

    @Test
    public void readsOrdinaryPaxGidAndReachesEndOfArchive() throws Exception {
        byte[] archive = createArchiveWithPaxGid("42");
        TarArchiveInputStream input = new TarArchiveInputStream(
                new ByteArrayInputStream(archive));
        try {
            TarArchiveEntry entry = input.getNextTarEntry();

            assertNotNull(entry);
            assertEquals("file", entry.getName());
            assertEquals(42L, entry.getLongGroupId());
            assertNull(input.getNextTarEntry());
        } finally {
            input.close();
        }
    }

    private TarArchiveEntry readEntryWithPaxGid(String gid) throws Exception {
        TarArchiveInputStream input = new TarArchiveInputStream(
                new ByteArrayInputStream(createArchiveWithPaxGid(gid)));
        try {
            return input.getNextTarEntry();
        } finally {
            input.close();
        }
    }

    private byte[] createArchiveWithPaxGid(String gid) throws Exception {
        byte[] paxData = paxRecord("gid", gid);
        ByteArrayOutputStream archive = new ByteArrayOutputStream();
        archive.write(createHeader("PaxHeader", paxData.length, (byte) 'x'));
        archive.write(paxData);
        writePadding(archive, paxData.length);
        archive.write(createHeader("file", 0, (byte) '0'));
        archive.write(new byte[1024]);
        return archive.toByteArray();
    }

    private byte[] paxRecord(String key, String value) throws Exception {
        String payload = key + "=" + value + "\n";
        int length = payload.getBytes("US-ASCII").length + 2;
        while (true) {
            String record = length + " " + payload;
            int actualLength = record.getBytes("US-ASCII").length;
            if (actualLength == length) {
                return record.getBytes("US-ASCII");
            }
            length = actualLength;
        }
    }

    private byte[] createHeader(String name, long size, byte type) throws Exception {
        byte[] header = new byte[512];
        writeString(header, 0, 100, name);
        writeOctal(header, 100, 8, 0644);
        writeOctal(header, 108, 8, 0);
        writeOctal(header, 116, 8, 0);
        writeOctal(header, 124, 12, size);
        writeOctal(header, 136, 12, 0);

        for (int i = 148; i < 156; i++) {
            header[i] = (byte) ' ';
        }

        header[156] = type;
        writeString(header, 257, 6, "ustar");
        writeString(header, 263, 2, "00");

        long checksum = 0;
        for (int i = 0; i < header.length; i++) {
            checksum += header[i] & 0xff;
        }

        String checksumText = Long.toOctalString(checksum);
        for (int i = 148; i < 154; i++) {
            header[i] = (byte) '0';
        }
        int checksumStart = 154 - checksumText.length();
        for (int i = 0; i < checksumText.length(); i++) {
            header[checksumStart + i] = (byte) checksumText.charAt(i);
        }
        header[154] = 0;
        header[155] = (byte) ' ';
        return header;
    }

    private void writePadding(ByteArrayOutputStream output, int size)
            throws IOException {
        int remainder = size % 512;
        if (remainder != 0) {
            output.write(new byte[512 - remainder]);
        }
    }

    private void writeString(byte[] target, int offset, int length, String value)
            throws Exception {
        byte[] bytes = value.getBytes("US-ASCII");
        System.arraycopy(bytes, 0, target, offset,
                Math.min(bytes.length, length));
    }

    private void writeOctal(byte[] target, int offset, int length, long value) {
        String octal = Long.toOctalString(value);
        for (int i = 0; i < length; i++) {
            target[offset + i] = (byte) '0';
        }
        int start = offset + length - octal.length() - 1;
        for (int i = 0; i < octal.length(); i++) {
            target[start + i] = (byte) octal.charAt(i);
        }
        target[offset + length - 1] = 0;
    }
}
