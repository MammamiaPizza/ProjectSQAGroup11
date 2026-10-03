@Test
public void stringConstructorNormalizesFatBackslashes() {
    ZipArchiveEntry entry = new ZipArchiveEntry("\u00e4\\\u00fc.txt");

    assertEquals("\u00e4/\u00fc.txt", entry.getName());
    assertFalse(entry.isDirectory());
}

@Test
public void zipEntryConstructorCopiesMetadataWithoutExtraData() throws Exception {
    java.util.zip.ZipEntry source = new java.util.zip.ZipEntry("stored.txt");
    source.setMethod(java.util.zip.ZipEntry.STORED);
    source.setSize(7);

    ZipArchiveEntry entry = new ZipArchiveEntry(source);

    assertEquals("stored.txt", entry.getName());
    assertEquals(java.util.zip.ZipEntry.STORED, entry.getMethod());
    assertEquals(7, entry.getSize());
    assertEquals(0, entry.getLocalFileDataExtra().length);
}

@Test
public void fileConstructorAppendsDirectorySeparatorAndSetsFileSize() throws Exception {
    java.io.File file = null;
    java.io.File directory = null;
    try {
        file = java.io.File.createTempFile("zip-entry", ".tmp");
        ZipArchiveEntry fileEntry = new ZipArchiveEntry(file, "file.txt");

        assertEquals("file.txt", fileEntry.getName());
        assertEquals(file.length(), fileEntry.getSize());

        directory = java.io.File.createTempFile("zip-entry", ".dir");
        assertTrue(directory.delete());
        assertTrue(directory.mkdir());

        ZipArchiveEntry directoryEntry = new ZipArchiveEntry(directory, "directory");

        assertEquals("directory/", directoryEntry.getName());
        assertTrue(directoryEntry.isDirectory());
    } finally {
        if (file != null) {
            file.delete();
        }
        if (directory != null) {
            directory.delete();
        }
    }
}