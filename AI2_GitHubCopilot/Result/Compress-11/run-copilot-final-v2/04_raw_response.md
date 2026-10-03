@Test(expected = IllegalArgumentException.class)
    public void testCreateArchiveInputStreamNullArchiverName() throws Exception {
        new ArchiveStreamFactory().createArchiveInputStream((String) null, new
java.io.ByteArrayInputStream(new byte[]{0x50, 0x4B, 0x03, 0x04}));
    }

 @Test(expected = IllegalArgumentException.class)
 public void testCreateArchiveInputStreamNullInputStream() throws Exception {
     new ArchiveStreamFactory().createArchiveInputStream("zip", null);
 }

 @Test
 public void testCreateArchiveInputStreamJarByName() throws Exception {
     java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(new byte[]{0x50, 0x4B, 0x03,
0x04});
     ArchiveInputStream ais = new ArchiveStreamFactory().createArchiveInputStream("jar", in);
     assertTrue(ais instanceof org.apache.commons.compress.archivers.jar.JarArchiveInputStream);
 }

 @Test
 public void testDumpIsDetected() throws Exception {
     byte[] dumpSig = new byte[]{0x1F, 0x9D, (byte) 0x90, 0x00};
     java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(dumpSig);
     ArchiveInputStream ais = new ArchiveStreamFactory().createArchiveInputStream(in);
     assertTrue(ais instanceof org.apache.commons.compress.archivers.dump.DumpArchiveInputStream);
 }