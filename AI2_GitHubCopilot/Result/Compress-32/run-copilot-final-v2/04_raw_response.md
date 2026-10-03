@Test
public void testPaxHeadersWithVariousKeys() throws Exception {
    java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
    try (org.apache.commons.compress.archivers.tar.TarArchiveOutputStream tos = new
org.apache.commons.compress.archivers.tar.TarArchiveOutputStream(baos)) {
        org.apache.commons.compress.archivers.tar.TarArchiveEntry entry = new
org.apache.commons.compress.archivers.tar.TarArchiveEntry("file");
        entry.addPaxHeader("path", "modified-path");
        entry.addPaxHeader("linkpath", "link-target");
        entry.addPaxHeader("gname", "testgroup");
        entry.addPaxHeader("uname", "testuser");
        entry.addPaxHeader("size", "0");
        entry.addPaxHeader("mtime", "1234567.123");
        entry.addPaxHeader("SCHILY.devminor", "42");
        entry.addPaxHeader("SCHILY.devmajor", "99");
        tos.putArchiveEntry(entry);
        tos.closeArchiveEntry();
    }
    try (org.apache.commons.compress.archivers.tar.TarArchiveInputStream tis = new
org.apache.commons.compress.archivers.tar.TarArchiveInputStream(new
java.io.ByteArrayInputStream(baos.toByteArray()))) {
        org.apache.commons.compress.archivers.tar.TarArchiveEntry e = tis.getNextTarEntry();
        assertEquals("path", "modified-path", e.getName());
        assertEquals("linkpath", "link-target", e.getLinkName());
        assertEquals("group", "testgroup", e.getGroupName());
        assertEquals("user", "testuser", e.getUserName());
        assertEquals("size", 0L, e.getSize());
        assertEquals("mtime", (long) (1234567.123 * 1000), e.getModTime());
        assertEquals("devminor", 42, e.getDevMinor());
        assertEquals("devmajor", 99, e.getDevMajor());
    }
}

@Test
public void testAvailableMoreThanMaxInt() throws Exception {
    java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
    try (org.apache.commons.compress.archivers.tar.TarArchiveOutputStream tos = new
org.apache.commons.compress.archivers.tar.TarArchiveOutputStream(baos)) {
        org.apache.commons.compress.archivers.tar.TarArchiveEntry entry = new
org.apache.commons.compress.archivers.tar.TarArchiveEntry("dir/");
        entry.addPaxHeader("size", "3000000000");
        tos.putArchiveEntry(entry);
        tos.closeArchiveEntry();
    }
    try (org.apache.commons.compress.archivers.tar.TarArchiveInputStream tis = new
org.apache.commons.compress.archivers.tar.TarArchiveInputStream(new
java.io.ByteArrayInputStream(baos.toByteArray()))) {
        org.apache.commons.compress.archivers.tar.TarArchiveEntry e = tis.getNextTarEntry();
        assertEquals("size parsed", 3000000000L, e.getSize());
        assertEquals(Integer.MAX_VALUE, tis.available());
    }
}

@Test
public void testCanReadEntryDataForNonTarEntry() throws Exception {
    org.apache.commons.compress.archivers.ArchiveEntry nonTar = new
org.apache.commons.compress.archivers.ArchiveEntry() {
        @Override
        public String getName() { return null; }
        @Override
        public long getSize() { return 0; }
        @Override
        public boolean isDirectory() { return false; }
        @Override
        public java.util.Date getLastModifiedDate() { return new java.util.Date(); }
    };
    org.apache.commons.compress.archivers.tar.TarArchiveInputStream tis = new
org.apache.commons.compress.archivers.tar.TarArchiveInputStream(new java.io.ByteArrayInputStream(new
byte[0]));
    assertFalse(tis.canReadEntryData(nonTar));
    tis.close();
}

@Test
public void testConstructorVariants() throws Exception {
    java.io.InputStream empty = new java.io.ByteArrayInputStream(new byte[0]);
    org.apache.commons.compress.archivers.tar.TarArchiveInputStream t1 = new
org.apache.commons.compress.archivers.tar.TarArchiveInputStream(empty, "UTF-8");
    assertNotNull(t1);
    assertEquals(512, t1.getRecordSize());
    t1.close();
    empty = new java.io.ByteArrayInputStream(new byte[0]);
    org.apache.commons.compress.archivers.tar.TarArchiveInputStream t2 = new
org.apache.commons.compress.archivers.tar.TarArchiveInputStream(empty, 512);
    assertNotNull(t2);
    assertEquals(512, t2.getRecordSize());
    t2.close();
    empty = new java.io.ByteArrayInputStream(new byte[0]);
    org.apache.commons.compress.archivers.tar.TarArchiveInputStream t3 = new
org.apache.commons.compress.archivers.tar.TarArchiveInputStream(empty, 512, "UTF-8");
    assertNotNull(t3);
    assertEquals(512, t3.getRecordSize());
    t3.close();
}