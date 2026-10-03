@org.junit.Test
public void readsPaxMetadataAfterBlankLine() throws Exception {
    final String pax = paxRecord("path", "renamed.txt")
        + "\n"
        + paxRecord("linkpath", "linked-file")
        + paxRecord("gid", "123")
        + paxRecord("gname", "group")
        + paxRecord("uid", "456")
        + paxRecord("uname", "user")
        + paxRecord("SCHILY.devminor", "7")
        + paxRecord("SCHILY.devmajor", "8");

    final org.apache.commons.compress.archivers.tar.TarArchiveInputStream input =
        new org.apache.commons.compress.archivers.tar.TarArchiveInputStream(
            new java.io.ByteArrayInputStream(createPaxArchive(pax)));

    try {
        final org.apache.commons.compress.archivers.tar.TarArchiveEntry entry =
            input.getNextTarEntry();
        org.junit.Assert.assertEquals("renamed.txt", entry.getName());
        org.junit.Assert.assertEquals("linked-file", entry.getLinkName());
        org.junit.Assert.assertEquals(123L, entry.getGroupId());
        org.junit.Assert.assertEquals("group", entry.getGroupName());
        org.junit.Assert.assertEquals(456L, entry.getUserId());
        org.junit.Assert.assertEquals("user", entry.getUserName());
        org.junit.Assert.assertEquals(7, entry.getDevMinor());
        org.junit.Assert.assertEquals(8, entry.getDevMajor());
        org.junit.Assert.assertNull(input.getNextTarEntry());
    } finally {
        input.close();
    }
}

@org.junit.Test
public void convenienceConstructorsUseTheDefaultRecordSize() throws Exception {
    final org.apache.commons.compress.archivers.tar.TarArchiveInputStream withEncoding =
        new org.apache.commons.compress.archivers.tar.TarArchiveInputStream(
            new java.io.ByteArrayInputStream(new byte[0]), "US-ASCII");
    final org.apache.commons.compress.archivers.tar.TarArchiveInputStream withBlockSize =
        new org.apache.commons.compress.archivers.tar.TarArchiveInputStream(
            new java.io.ByteArrayInputStream(new byte[0]), 1024);

    try {
        org.junit.Assert.assertEquals(512, withEncoding.getRecordSize());
        org.junit.Assert.assertEquals(512, withBlockSize.getRecordSize());
    } finally {
        withEncoding.close();
        withBlockSize.close();
    }
}

private static String paxRecord(final String key, final String value) {
    final String body = key + "=" + value + "\n";
    int length = body.length();
    while (true) {
        final int calculated = body.length() + 1 + java.lang.String.valueOf(length).length();
        if (calculated == length) {
            return length + " " + body;
        }
        length = calculated;
    }
}

private static byte[] createPaxArchive(final String pax) throws Exception {
    final byte[] paxData = pax.getBytes("US-ASCII");
    final java.io.ByteArrayOutputStream archive = new java.io.ByteArrayOutputStream();

    writeTarHeader(archive, "PaxHeader", paxData.length, (byte) 'x');
    archive.write(paxData);
    writePadding(archive, paxData.length);

    writeTarHeader(archive, "original.txt", 0, (byte) '0');
    archive.write(new byte[1024]);
    return archive.toByteArray();
}

private static void writeTarHeader(final java.io.ByteArrayOutputStream archive,
                                   final String name, final long size,
                                   final byte type) {
    final byte[] header = new byte[512];
    for (int i = 0; i < name.length() && i < 100; i++) {
        header[i] = (byte) name.charAt(i);
    }

    writeOctal(header, 100, 8, 0644);
    writeOctal(header, 108, 8, 0);
    writeOctal(header, 116, 8, 0);
    writeOctal(header, 124, 12, size);
    writeOctal(header, 136, 12, 0);

    for (int i = 148; i < 156; i++) {
        header[i] = (byte) ' ';
    }
    header[156] = type;
    header[257] = (byte) 'u';
    header[258] = (byte) 's';
    header[259] = (byte) 't';
    header[260] = (byte) 'a';
    header[261] = (byte) 'r';
    header[262] = 0;
    header[263] = (byte) '0';
    header[264] = (byte) '0';

    long checksum = 0;
    for (int i = 0; i < header.length; i++) {
        checksum += header[i] & 0xff;
    }
    final String checksumValue = java.lang.String.format("%06o", checksum);
    for (int i = 0; i < checksumValue.length(); i++) {
        header[148 + i] = (byte) checksumValue.charAt(i);
    }
    header[154] = 0;
    header[155] = (byte) ' ';

    archive.write(header, 0, header.length);
}

private static void writeOctal(final byte[] buffer, final int offset,
                               final int length, final long value) {
    for (int i = offset; i < offset + length - 1; i++) {
        buffer[i] = (byte) '0';
    }
    final String digits = java.lang.Long.toOctalString(value);
    final int start = offset + length - 1 - digits.length();
    for (int i = 0; i < digits.length(); i++) {
        buffer[start + i] = (byte) digits.charAt(i);
    }
    buffer[offset + length - 1] = 0;
}

private static void writePadding(final java.io.ByteArrayOutputStream archive,
                                 final int size) {
    final int padding = (512 - (size % 512)) % 512;
    if (padding > 0) {
        archive.write(new byte[padding], 0, padding);
    }
}