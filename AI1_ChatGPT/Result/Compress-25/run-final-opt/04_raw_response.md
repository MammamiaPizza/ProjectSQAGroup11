@Test
public void skipsBytesWithinStoredEntryAndReadsRemainingBytes() throws Exception {
    final byte[] payload = new byte[] { 1, 2, 3, 4, 5, 6 };
    final java.util.zip.CRC32 crc = new java.util.zip.CRC32();
    crc.update(payload);

    final java.io.ByteArrayOutputStream archive = new java.io.ByteArrayOutputStream();
    final java.util.zip.ZipOutputStream output = new java.util.zip.ZipOutputStream(archive);
    final java.util.zip.ZipEntry entry = new java.util.zip.ZipEntry("stored");
    entry.setMethod(java.util.zip.ZipEntry.STORED);
    entry.setSize(payload.length);
    entry.setCompressedSize(payload.length);
    entry.setCrc(crc.getValue());
    output.putNextEntry(entry);
    output.write(payload);
    output.closeEntry();
    output.close();

    final ZipArchiveInputStream input =
            new ZipArchiveInputStream(new java.io.ByteArrayInputStream(archive.toByteArray()));
    org.junit.Assert.assertNotNull(input.getNextZipEntry());
    org.junit.Assert.assertEquals(3, input.skip(3));
    org.junit.Assert.assertArrayEquals(new byte[] { 4, 5, 6 },
            readAllFromZipArchiveInputStreamForCompress25(input));
    input.close();
}

@Test
public void readsDeflatedEntriesWrittenWithDataDescriptorsInSequence() throws Exception {
    final byte[] firstPayload = new byte[] { 10, 20, 30, 40, 50 };
    final byte[] secondPayload = new byte[] { 60, 70, 80, 90 };

    final java.io.ByteArrayOutputStream archive = new java.io.ByteArrayOutputStream();
    final java.util.zip.ZipOutputStream output = new java.util.zip.ZipOutputStream(archive);
    output.putNextEntry(new java.util.zip.ZipEntry("first"));
    output.write(firstPayload);
    output.closeEntry();
    output.putNextEntry(new java.util.zip.ZipEntry("second"));
    output.write(secondPayload);
    output.closeEntry();
    output.close();

    final ZipArchiveInputStream input =
            new ZipArchiveInputStream(new java.io.ByteArrayInputStream(archive.toByteArray()));
    org.junit.Assert.assertEquals("first", input.getNextZipEntry().getName());
    org.junit.Assert.assertArrayEquals(firstPayload,
            readAllFromZipArchiveInputStreamForCompress25(input));
    org.junit.Assert.assertEquals("second", input.getNextZipEntry().getName());
    org.junit.Assert.assertArrayEquals(secondPayload,
            readAllFromZipArchiveInputStreamForCompress25(input));
    input.close();
}

private static byte[] readAllFromZipArchiveInputStreamForCompress25(
        final ZipArchiveInputStream input) throws java.io.IOException {
    final java.io.ByteArrayOutputStream result = new java.io.ByteArrayOutputStream();
    final byte[] buffer = new byte[3];
    int read;
    while ((read = input.read(buffer, 0, buffer.length)) != -1) {
        result.write(buffer, 0, read);
    }
    return result.toByteArray();
}