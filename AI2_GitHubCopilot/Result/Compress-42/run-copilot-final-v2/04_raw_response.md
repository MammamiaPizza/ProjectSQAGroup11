@Test
    public void testProtectedDefaultConstructor() {
        ZipArchiveEntry entry = new ZipArchiveEntry() {};
        assertEquals("", entry.getName());
    }

 @Test
 public void testConstructorWithDirectoryEntry() throws Exception {
     java.io.File tempDir = new java.io.File(System.getProperty("java.io.tmpdir"), "testDir" +
System.nanoTime());
     tempDir.mkdirs();
     try {
         ZipArchiveEntry entry = new ZipArchiveEntry(tempDir, "mydir");
         assertEquals("mydir/", entry.getName());
     } finally {
         tempDir.delete();
     }
 }

 @Test
 public void testConstructorWithRegularFile() throws Exception {
     java.io.File tempFile = java.io.File.createTempFile("testZipEntry", ".tmp");
     try {
         ZipArchiveEntry entry = new ZipArchiveEntry(tempFile, tempFile.getName());
         assertEquals(tempFile.length(), entry.getSize());
         assertEquals(tempFile.lastModified(), entry.getTime());
     } finally {
         tempFile.delete();
     }
 }

 @Test
 public void testConstructorFromZipArchiveEntryCopiesFields() throws Exception {
     ZipArchiveEntry original = new ZipArchiveEntry("original");
     original.setVersionMadeBy(3);
     original.setVersionRequired(20);
     original.setRawFlag(0x1234);
     ZipArchiveEntry copy = new ZipArchiveEntry(original);
     assertEquals(original.getName(), copy.getName());
     assertEquals(original.getVersionMadeBy(), copy.getVersionMadeBy());
     assertEquals(original.getVersionRequired(), copy.getVersionRequired());
     assertEquals(original.getRawFlag(), copy.getRawFlag());
 }