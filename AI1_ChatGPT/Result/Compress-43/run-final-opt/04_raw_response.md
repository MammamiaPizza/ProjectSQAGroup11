@Test
public void nonEmptyRawStoredEntryWithKnownMetadataDoesNotUseDataDescriptor() throws Exception {
    final byte[] original = { 1, 2, 3, 4, 5 };
    final java.util.zip.CRC32 crc = new java.util.zip.CRC32();
    crc.update(original);

    final org.apache.commons.compress.archivers.zip.ZipArchiveEntry entry =
            new org.apache.commons.compress.archivers.zip.ZipArchiveEntry("raw-stored.bin");
    entry.setMethod(org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream.STORED);
    entry.setSize(original.length);
    entry.setCompressedSize(original.length);
    entry.setCrc(crc.getValue());

    final java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
    final org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream archiveOutput =
            new org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream(output);
    archiveOutput.addRawArchiveEntry(entry, new java.io.ByteArrayInputStream(original));
    archiveOutput.finish();

    final byte[] archive = output.toByteArray();
    final int nameLength = zipTestUnsignedShort(archive, 26);
    final int extraLength = zipTestUnsignedShort(archive, 28);
    final int centralDirectoryOffset = 30 + nameLength + extraLength + original.length;

    org.junit.Assert.assertEquals(0x04034b50L, zipTestUnsignedInt(archive, 0));
    org.junit.Assert.assertEquals(0, zipTestUnsignedShort(archive, 6) & 0x0008);
    org.junit.Assert.assertEquals(crc.getValue(), zipTestUnsignedInt(archive, 14));
    org.junit.Assert.assertEquals(original.length, zipTestUnsignedInt(archive, 18));
    org.junit.Assert.assertEquals(original.length, zipTestUnsignedInt(archive, 22));
    org.junit.Assert.assertEquals(0x02014b50L, zipTestUnsignedInt(archive, centralDirectoryOffset));
    org.junit.Assert.assertEquals(0, zipTestUnsignedShort(archive, centralDirectoryOffset + 8) & 0x0008);
}

private static int zipTestUnsignedShort(final byte[] data, final int offset) {
    return (data[offset] & 0xff) | ((data[offset + 1] & 0xff) << 8);
}

private static long zipTestUnsignedInt(final byte[] data, final int offset) {
    return (data[offset] & 0xffL)
            | ((data[offset + 1] & 0xffL) << 8)
            | ((data[offset + 2] & 0xffL) << 16)
            | ((data[offset + 3] & 0xffL) << 24);
}