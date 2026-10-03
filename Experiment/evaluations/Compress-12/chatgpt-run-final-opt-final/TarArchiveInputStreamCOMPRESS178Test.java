package org.apache.commons.compress.archivers.tar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TarArchiveInputStreamCOMPRESS178Test {

    @Test
    public void readsEntryWhoseModeContainsAnEmbeddedNulAndContinuesToNextEntry()
            throws Exception {
        byte[] archive = createArchive();
        setModeWithEmbeddedNul(archive, 0);
        updateChecksum(archive, 0);

        TarArchiveInputStream input =
                new TarArchiveInputStream(new ByteArrayInputStream(archive));
        try {
            TarArchiveEntry first = input.getNextTarEntry();
            assertNotNull(first);
            assertEquals("first", first.getName());

            byte[] firstContents = new byte[8];
            assertEquals(3, input.read(firstContents, 0, firstContents.length));
            assertEquals('o', firstContents[0]);
            assertEquals('n', firstContents[1]);
            assertEquals('e', firstContents[2]);
            assertEquals(-1, input.read(firstContents, 0, firstContents.length));

            TarArchiveEntry second = input.getNextTarEntry();
            assertNotNull(second);
            assertEquals("second", second.getName());

            byte[] secondContents = new byte[4];
            assertEquals(1, input.read(secondContents, 0, secondContents.length));
            assertEquals('x', secondContents[0]);
            assertEquals(-1, input.read(secondContents, 0, secondContents.length));
            assertEquals(null, input.getNextTarEntry());
        } finally {
            input.close();
        }
    }

    private byte[] createArchive() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        TarArchiveOutputStream output = new TarArchiveOutputStream(bytes);
        try {
            TarArchiveEntry first = new TarArchiveEntry("first");
            first.setSize(3);
            output.putArchiveEntry(first);
            output.write(new byte[] { 'o', 'n', 'e' });
            output.closeArchiveEntry();

            TarArchiveEntry second = new TarArchiveEntry("second");
            second.setSize(1);
            output.putArchiveEntry(second);
            output.write(new byte[] { 'x' });
            output.closeArchiveEntry();
        } finally {
            output.close();
        }
        return bytes.toByteArray();
    }

    private void setModeWithEmbeddedNul(byte[] archive, int headerOffset) {
        int modeOffset = headerOffset + 100;
        archive[modeOffset] = '0';
        archive[modeOffset + 1] = '0';
        archive[modeOffset + 2] = '0';
        archive[modeOffset + 3] = '0';
        archive[modeOffset + 4] = '7';
        archive[modeOffset + 5] = '6';
        archive[modeOffset + 6] = '5';
        archive[modeOffset + 7] = 0;
    }

    private void updateChecksum(byte[] archive, int headerOffset) {
        int checksumOffset = headerOffset + 148;
        for (int i = 0; i < 8; i++) {
            archive[checksumOffset + i] = ' ';
        }

        long checksum = 0;
        for (int i = 0; i < 512; i++) {
            checksum += archive[headerOffset + i] & 0xff;
        }

        String value = Long.toOctalString(checksum);
        for (int i = 0; i < 6; i++) {
            archive[checksumOffset + i] = '0';
        }
        for (int i = 0; i < value.length(); i++) {
            archive[checksumOffset + 6 - value.length() + i] =
                    (byte) value.charAt(i);
        }
        archive[checksumOffset + 6] = 0;
        archive[checksumOffset + 7] = ' ';
    }

@org.junit.Test
public void appliesPaxHeadersToTheFollowingEntry() throws Exception {
    String pax = compress12PaxRecord("path", "pax-name")
            + compress12PaxRecord("linkpath", "pax-link")
            + compress12PaxRecord("gid", "42")
            + compress12PaxRecord("gname", "pax-group")
            + compress12PaxRecord("uid", "24")
            + compress12PaxRecord("uname", "pax-user")
            + compress12PaxRecord("size", "3");
    byte[] paxBytes = compress12Ascii(pax);

    org.apache.commons.compress.archivers.tar.TarArchiveInputStream input =
            new org.apache.commons.compress.archivers.tar.TarArchiveInputStream(
                    new java.io.ByteArrayInputStream(compress12Archive(
                            new String[] { "PaxHeader", "ignored" },
                            new long[] { paxBytes.length, 3 },
                            new byte[] { (byte) 'x', (byte) '0' },
                            new byte[][] { paxBytes, compress12Ascii("abc") })));

    org.apache.commons.compress.archivers.tar.TarArchiveEntry entry = input.getNextTarEntry();

    org.junit.Assert.assertNotNull(entry);
    org.junit.Assert.assertEquals("pax-name", entry.getName());
    org.junit.Assert.assertEquals("pax-link", entry.getLinkName());
    org.junit.Assert.assertEquals(42, entry.getGroupId());
    org.junit.Assert.assertEquals("pax-group", entry.getGroupName());
    org.junit.Assert.assertEquals(24, entry.getUserId());
    org.junit.Assert.assertEquals("pax-user", entry.getUserName());
    org.junit.Assert.assertEquals(3, entry.getSize());
}

@org.junit.Test
public void availableIsCappedForEntriesLargerThanAnInt() throws Exception {
    org.apache.commons.compress.archivers.tar.TarArchiveInputStream input =
            new org.apache.commons.compress.archivers.tar.TarArchiveInputStream(
                    new java.io.ByteArrayInputStream(compress12Archive(
                            new String[] { "large" },
                            new long[] { 4294967296L },
                            new byte[] { (byte) '0' },
                            new byte[][] { new byte[0] })),
                    10240);

    org.junit.Assert.assertNotNull(input.getNextTarEntry());
    org.junit.Assert.assertEquals(Integer.MAX_VALUE, input.available());
}

private static String compress12PaxRecord(String key, String value) {
    String contents = key + "=" + value + "\n";
    int length = contents.length() + 2;
    while (true) {
        int calculatedLength = String.valueOf(length).length() + 1 + contents.length();
        if (calculatedLength == length) {
            return length + " " + contents;
        }
        length = calculatedLength;
    }
}

private static byte[] compress12Archive(String[] names, long[] sizes, byte[] types,
        byte[][] contents) throws java.io.IOException {
    java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();

    for (int i = 0; i < names.length; i++) {
        output.write(compress12Header(names[i], sizes[i], types[i]));
        output.write(contents[i]);
        int padding = (int) ((512 - (contents[i].length % 512)) % 512);
        if (padding > 0) {
            output.write(new byte[padding]);
        }
    }

    output.write(new byte[1024]);
    int blockPadding = (int) ((10240 - (output.size() % 10240)) % 10240);
    if (blockPadding > 0) {
        output.write(new byte[blockPadding]);
    }
    return output.toByteArray();
}

private static byte[] compress12Header(String name, long size, byte type) {
    byte[] header = new byte[512];
    byte[] nameBytes = compress12Ascii(name);
    System.arraycopy(nameBytes, 0, header, 0, nameBytes.length);

    compress12WriteOctal(header, 100, 8, 0644);
    compress12WriteOctal(header, 108, 8, 0);
    compress12WriteOctal(header, 116, 8, 0);
    compress12WriteOctal(header, 124, 12, size);
    compress12WriteOctal(header, 136, 12, 0);
    for (int i = 148; i < 156; i++) {
        header[i] = (byte) ' ';
    }
    header[156] = type;

    long checksum = 0;
    for (int i = 0; i < header.length; i++) {
        checksum += header[i] & 0xff;
    }
    String checksumText = Long.toOctalString(checksum);
    for (int i = 0; i < 6; i++) {
        int source = i - (6 - checksumText.length());
        header[148 + i] = source < 0 ? (byte) '0' : (byte) checksumText.charAt(source);
    }
    header[154] = 0;
    header[155] = (byte) ' ';
    return header;
}

private static void compress12WriteOctal(byte[] target, int offset, int length, long value) {
    int position = offset + length - 2;
    do {
        target[position--] = (byte) ('0' + (value & 7));
        value >>>= 3;
    } while (value > 0 && position >= offset);
    while (position >= offset) {
        target[position--] = (byte) '0';
    }
}

private static byte[] compress12Ascii(String value) {
    byte[] bytes = new byte[value.length()];
    for (int i = 0; i < value.length(); i++) {
        bytes[i] = (byte) value.charAt(i);
    }
    return bytes;
}
}
