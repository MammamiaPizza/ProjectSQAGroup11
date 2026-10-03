@Test
public void readsDeflatedDataDescriptorBeforeFollowingEntry() throws Exception {
    final java.io.ByteArrayOutputStream archive = new java.io.ByteArrayOutputStream();
    final java.util.zip.ZipOutputStream output = new java.util.zip.ZipOutputStream(archive);
    final byte[] firstData = new byte[4096];
    int state = 123456789;
    for (int i = 0; i < firstData.length; i++) {
        state = state * 1103515245 + 12345;
        firstData[i] = (byte) (state >>> 16);
    }

    output.putNextEntry(new java.util.zip.ZipEntry("first.bin"));
    output.write(firstData);
    output.closeEntry();
    output.putNextEntry(new java.util.zip.ZipEntry("second.txt"));
    output.write(new byte[] { 7, 8, 9 });
    output.closeEntry();
    output.close();

    final org.apache.commons.compress.archivers.zip.ZipArchiveInputStream input =
        new org.apache.commons.compress.archivers.zip.ZipArchiveInputStream(
            new java.io.ByteArrayInputStream(archive.toByteArray()));

    final org.apache.commons.compress.archivers.zip.ZipArchiveEntry first = input.getNextZipEntry();
    assertNotNull(first);
    assertEquals("first.bin", first.getName());
    assertArrayEquals(firstData, readZipStreamFully(input));

    final org.apache.commons.compress.archivers.zip.ZipArchiveEntry second = input.getNextZipEntry();
    assertNotNull(second);
    assertEquals("second.txt", second.getName());
    assertArrayEquals(new byte[] { 7, 8, 9 }, readZipStreamFully(input));
    assertNull(input.getNextZipEntry());
    input.close();
}

@Test
public void reportsEntriesWithUnsupportedCompressionMethodsAsUnreadable() throws Exception {
    final byte[] localFileHeader = new byte[] {
        0x50, 0x4b, 0x03, 0x04,
        20, 0,
        0, 0,
        99, 0,
        0, 0, 0, 0,
        0, 0, 0, 0,
        0, 0, 0, 0,
        0, 0, 0, 0,
        1, 0,
        0, 0,
        'x'
    };

    final org.apache.commons.compress.archivers.zip.ZipArchiveInputStream input =
        new org.apache.commons.compress.archivers.zip.ZipArchiveInputStream(
            new java.io.ByteArrayInputStream(localFileHeader));
    final org.apache.commons.compress.archivers.zip.ZipArchiveEntry entry = input.getNextZipEntry();

    assertNotNull(entry);
    assertEquals("x", entry.getName());
    assertFalse(input.canReadEntryData(entry));
    assertNull(input.getNextZipEntry());
    input.close();
}

private byte[] readZipStreamFully(final java.io.InputStream input) throws java.io.IOException {
    final java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
    final byte[] buffer = new byte[257];
    int read;
    while ((read = input.read(buffer)) != -1) {
        output.write(buffer, 0, read);
    }
    return output.toByteArray();
}