@Test
public void fileConstructorCreatesRegularFileEntryWithFileMetadata() throws Exception {
    final java.io.File file = java.io.File.createTempFile("tar-entry", ".txt");
    try {
        final java.io.FileOutputStream output = new java.io.FileOutputStream(file);
        try {
            output.write(new byte[] { 1, 2, 3 });
        } finally {
            output.close();
        }

        final TarArchiveEntry entry = new TarArchiveEntry(file);

        assertTrue(entry.isFile());
        assertFalse(entry.isDirectory());
        assertEquals(TarArchiveEntry.DEFAULT_FILE_MODE, entry.getMode());
        assertEquals(3, entry.getSize());
        assertTrue(entry.getName().endsWith(file.getName()));
    } finally {
        file.delete();
    }
}

@Test
public void fileAndNameConstructorCreatesDirectoryEntryWithTrailingSlash() throws Exception {
    final java.io.File directory = java.nio.file.Files.createTempDirectory("tar-entry").toFile();
    try {
        final TarArchiveEntry entry = new TarArchiveEntry(directory, "archive-directory");

        assertEquals("archive-directory/", entry.getName());
        assertTrue(entry.isDirectory());
        assertFalse(entry.isFile());
        assertEquals(TarArchiveEntry.DEFAULT_DIR_MODE, entry.getMode());
    } finally {
        directory.delete();
    }
}

@Test
public void longNameEntryUsesGnuMagicAndVersion() {
    final TarArchiveEntry entry = new TarArchiveEntry(
            "long-name", org.apache.commons.compress.archivers.tar.TarConstants.LF_GNUTYPE_LONGNAME);

    assertEquals(org.apache.commons.compress.archivers.tar.TarConstants.MAGIC_GNU, entry.getMagic());
    assertEquals(org.apache.commons.compress.archivers.tar.TarConstants.VERSION_GNU_SPACE, entry.getVersion());
}