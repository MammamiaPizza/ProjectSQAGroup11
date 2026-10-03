@Test
public void javaZipEntryConstructorCopiesStandardZipEntryProperties() throws Exception {
    final java.util.zip.ZipEntry source = new java.util.zip.ZipEntry("source.txt");
    source.setMethod(java.util.zip.ZipEntry.DEFLATED);
    source.setSize(42L);
    source.setCompressedSize(17L);
    source.setCrc(1234L);
    source.setComment("comment");
    source.setTime(123456789000L);

    final ZipArchiveEntry copy = new ZipArchiveEntry(source);

    assertEquals(source.getName(), copy.getName());
    assertEquals(source.getMethod(), copy.getMethod());
    assertEquals(source.getSize(), copy.getSize());
    assertEquals(source.getCompressedSize(), copy.getCompressedSize());
    assertEquals(source.getCrc(), copy.getCrc());
    assertEquals(source.getComment(), copy.getComment());
    assertEquals(source.getTime(), copy.getTime());
    assertNotNull(copy.getExtra());
    assertEquals(0, copy.getExtra().length);
}

@Test
public void zipArchiveEntryCopyConstructorCopiesArchiveSpecificAttributes() throws Exception {
    final ZipArchiveEntry source = new ZipArchiveEntry("source.txt");
    source.setMethod(java.util.zip.ZipEntry.DEFLATED);
    source.setInternalAttributes(7);
    source.setExternalAttributes(123456L);
    source.setPlatform(ZipArchiveEntry.PLATFORM_UNIX);

    final ZipArchiveEntry copy = new ZipArchiveEntry(source);

    assertEquals(source.getName(), copy.getName());
    assertEquals(source.getMethod(), copy.getMethod());
    assertEquals(source.getInternalAttributes(), copy.getInternalAttributes());
    assertEquals(source.getExternalAttributes(), copy.getExternalAttributes());
    assertEquals(source.getPlatform(), copy.getPlatform());
}

@Test
public void fileConstructorUsesFileLengthForRegularFiles() throws Exception {
    final java.io.File file = java.io.File.createTempFile("compress", ".txt");
    try {
        final java.io.FileOutputStream output = new java.io.FileOutputStream(file);
        try {
            output.write(new byte[] { 1, 2, 3 });
        } finally {
            output.close();
        }

        final ZipArchiveEntry entry = new ZipArchiveEntry(file, "entry.txt");

        assertEquals("entry.txt", entry.getName());
        assertEquals(file.length(), entry.getSize());
    } finally {
        file.delete();
    }
}

@Test
public void fileConstructorAddsTrailingSlashForDirectories() throws Exception {
    final java.io.File directory = java.nio.file.Files.createTempDirectory("compress").toFile();
    try {
        final ZipArchiveEntry entry = new ZipArchiveEntry(directory, "directory");

        assertEquals("directory/", entry.getName());
    } finally {
        directory.delete();
    }
}