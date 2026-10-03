@Test
public void readsPaxUidLargerThanUnsignedIntRange() throws Exception {
    org.apache.commons.compress.archivers.tar.TarArchiveInputStream input =
        new org.apache.commons.compress.archivers.tar.TarArchiveInputStream(
            new java.io.ByteArrayInputStream(createPaxArchive("uid", "4294967294")));
    try {
        assertNotNull(input.getNextTarEntry());
        assertEquals(4294967294L, input.getCurrentEntry().getLongUserId());
    } finally {
        input.close();
    }
}

@Test
public void appliesPaxPathToFollowingEntry() throws Exception {
    org.apache.commons.compress.archivers.tar.TarArchiveInputStream input =
        new org.apache.commons.compress.archivers.tar.TarArchiveInputStream(
            new java.io.ByteArrayInputStream(createPaxArchive("path", "renamed-file")));
    try {
        assertNotNull(input.getNextTarEntry());
        assertEquals("renamed-file", input.getCurrentEntry().getName());
        assertNull(input.getNextTarEntry());
    } finally {
        input.close();
    }
}

@Test
public void availableIsCappedForEntriesLargerThanAnInt() throws Exception {
    org.apache.commons.compress.archivers.tar.TarArchiveInputStream input =
        new org.apache.commons.compress.archivers.tar.TarArchiveInputStream(
            new java.io.ByteArrayInputStream(tarHeader("large", 2147483648L, (byte) '0')));
    try {
        assertNotNull(input.getNextTarEntry());
        assertEquals(Integer.MAX_VALUE, input.available());
    } finally {
        input.close();
    }
}

private byte[] createPaxArchive(String key, String value) throws java.io.IOException {
    byte[] record = paxRecord(key, value);
    java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
    output.write(tarHeader("PaxHeader", record.length, (byte) 'x'));
    output.write(record);
    int padding = (512 - record.length % 512) % 512;
    output.write(new byte[padding]);
    output.write(tarHeader("file", 0, (byte) '0'));
    output.write(new byte[1024]);
    return output.toByteArray();
}

private byte[] paxRecord(String key, String value) throws java.io.IOException {
    String contents = key + "=" + value + "\n";
    int length = contents.getBytes("UTF-8").length + 2;
    while (true) {
        byte[] record = (length + " " + contents).getBytes("UTF-8");
        if (record.length == length) {
            return record;
        }
        length = record.length;
    }
}

private byte[] tarHeader(String name, long size, byte type) throws java.io.IOException {
    byte[] header = new byte[512];
    byte[] nameBytes = name.getBytes("US-ASCII");
    System.arraycopy(nameBytes, 0, header, 0, nameBytes.length);
    writeOctal(header, 100, 8, 0644);
    writeOctal(header, 124, 12, size);
    header[156] = type;
    byte[] magic = "ustar".getBytes("US-ASCII");
    System.arraycopy(magic, 0, header, 257, magic.length);
    header[262] = 0;
    header[263] = '0';
    header[264] = '0';
    for (int i = 148; i < 156; i++) {
        header[i] = (byte) ' ';
    }
    long checksum = 0;
    for (int i = 0; i < header.length; i++) {
        checksum += header[i] & 0xff;
    }
    writeOctal(header, 148, 8, checksum);
    return header;
}

private void writeOctal(byte[] buffer, int offset, int length, long value) {
    buffer[offset + length - 1] = 0;
    for (int i = offset + length - 2; i >= offset; i--) {
        buffer[i] = (byte) ('0' + (value & 7));
        value >>>= 3;
    }
}