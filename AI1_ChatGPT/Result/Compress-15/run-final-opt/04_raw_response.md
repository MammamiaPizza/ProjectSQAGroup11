@Test
public void constructingFromJavaZipEntryWithoutExtraDataInitializesEmptyExtraData() throws Exception {
    java.util.zip.ZipEntry source = new java.util.zip.ZipEntry("entry");
    source.setMethod(java.util.zip.ZipEntry.STORED);
    source.setSize(7);

    ZipArchiveEntry entry = new ZipArchiveEntry(source);

    assertEquals("entry", entry.getName());
    assertEquals(java.util.zip.ZipEntry.STORED, entry.getMethod());
    assertEquals(7L, entry.getSize());
    assertNotNull(entry.getLocalFileDataExtra());
    assertEquals(0, entry.getLocalFileDataExtra().length);
}

@Test
public void copyConstructorPreservesArchiveSpecificAttributes() throws Exception {
    ZipArchiveEntry source = new ZipArchiveEntry("entry");
    source.setMethod(java.util.zip.ZipEntry.DEFLATED);
    source.setInternalAttributes(12);
    source.setExternalAttributes(345L);

    ZipArchiveEntry copy = new ZipArchiveEntry(source);

    assertEquals("entry", copy.getName());
    assertEquals(java.util.zip.ZipEntry.DEFLATED, copy.getMethod());
    assertEquals(12, copy.getInternalAttributes());
    assertEquals(345L, copy.getExternalAttributes());
}

@Test
public void fileConstructorUsesFileSizeAndAddsDirectorySlash() throws Exception {
    java.io.File file = java.io.File.createTempFile("zip-entry", ".tmp");
    java.io.FileOutputStream output = new java.io.FileOutputStream(file);
    try {
        output.write(new byte[] { 1, 2, 3 });
    } finally {
        output.close();
    }

    java.io.File directory = new java.io.File(file.getParentFile(), file.getName() + "-dir");
    try {
        assertTrue(directory.mkdir());

        ZipArchiveEntry fileEntry = new ZipArchiveEntry(file, "file");
        ZipArchiveEntry directoryEntry = new ZipArchiveEntry(directory, "directory");

        assertEquals("file", fileEntry.getName());
        assertEquals(3L, fileEntry.getSize());
        assertEquals("directory/", directoryEntry.getName());
        assertTrue(directoryEntry.isDirectory());
    } finally {
        directory.delete();
        file.delete();
    }
}