public void testZipEntryConstructorInitializesEmptyExtraData() throws Exception {
    java.util.zip.ZipEntry source = new java.util.zip.ZipEntry("entry.txt");
    source.setMethod(java.util.zip.ZipEntry.DEFLATED);

    ZipArchiveEntry entry = new ZipArchiveEntry(source);

    assertEquals("entry.txt", entry.getName());
    assertEquals(java.util.zip.ZipEntry.DEFLATED, entry.getMethod());
    assertNotNull(entry.getLocalFileDataExtra());
    assertEquals(0, entry.getLocalFileDataExtra().length);
}

public void testZipArchiveEntryCopyConstructorPreservesAttributes() throws Exception {
    ZipArchiveEntry source = new ZipArchiveEntry("entry.txt");
    source.setMethod(java.util.zip.ZipEntry.STORED);
    source.setInternalAttributes(17);
    source.setExternalAttributes(123456L);

    ZipArchiveEntry copy = new ZipArchiveEntry(source);

    assertEquals(source.getName(), copy.getName());
    assertEquals(source.getMethod(), copy.getMethod());
    assertEquals(source.getInternalAttributes(), copy.getInternalAttributes());
    assertEquals(source.getExternalAttributes(), copy.getExternalAttributes());
}

public void testFileConstructorSetsFileSizeAndKeepsEntryName() throws Exception {
    java.io.File file = java.io.File.createTempFile("zip-entry", ".tmp");
    try {
        ZipArchiveEntry entry = new ZipArchiveEntry(file, "file.txt");

        assertEquals("file.txt", entry.getName());
        assertEquals(file.length(), entry.getSize());
        assertFalse(entry.isDirectory());
    } finally {
        file.delete();
    }
}

public void testFileConstructorAddsSlashForDirectoryEntry() throws Exception {
    java.io.File directory = java.io.File.createTempFile("zip-entry", ".dir");
    assertTrue(directory.delete());
    assertTrue(directory.mkdir());
    try {
        ZipArchiveEntry entry = new ZipArchiveEntry(directory, "directory");

        assertEquals("directory/", entry.getName());
        assertTrue(entry.isDirectory());
    } finally {
        directory.delete();
    }
}