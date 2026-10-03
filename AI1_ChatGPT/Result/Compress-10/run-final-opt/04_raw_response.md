@Test
public void constructorsUsingFileAndStringCanReadTheSameArchive() throws Exception {
    java.io.File archive = createZipArchive(new String[] { "entry.txt" });
    org.apache.commons.compress.archivers.zip.ZipFile byFile = null;
    org.apache.commons.compress.archivers.zip.ZipFile byString = null;
    org.apache.commons.compress.archivers.zip.ZipFile byFileWithEncoding = null;
    org.apache.commons.compress.archivers.zip.ZipFile byStringWithEncoding = null;
    try {
        byFile = new org.apache.commons.compress.archivers.zip.ZipFile(archive);
        assertReadableByte(byFile, "entry.txt", 1);

        byString = new org.apache.commons.compress.archivers.zip.ZipFile(archive.getPath());
        assertReadableByte(byString, "entry.txt", 1);

        byFileWithEncoding =
            new org.apache.commons.compress.archivers.zip.ZipFile(archive, "US-ASCII");
        assertReadableByte(byFileWithEncoding, "entry.txt", 1);

        byStringWithEncoding =
            new org.apache.commons.compress.archivers.zip.ZipFile(archive.getPath(), "US-ASCII");
        assertReadableByte(byStringWithEncoding, "entry.txt", 1);
    } finally {
        org.apache.commons.compress.archivers.zip.ZipFile.closeQuietly(byFile);
        org.apache.commons.compress.archivers.zip.ZipFile.closeQuietly(byString);
        org.apache.commons.compress.archivers.zip.ZipFile.closeQuietly(byFileWithEncoding);
        org.apache.commons.compress.archivers.zip.ZipFile.closeQuietly(byStringWithEncoding);
        archive.delete();
    }
}

@Test
public void entriesInPhysicalOrderFollowTheirLocalHeaderOrder() throws Exception {
    java.io.File archive = createZipArchive(new String[] { "second.txt", "first.txt" });
    org.apache.commons.compress.archivers.zip.ZipFile zipFile = null;
    try {
        zipFile = new org.apache.commons.compress.archivers.zip.ZipFile(archive);
        java.util.Enumeration<org.apache.commons.compress.archivers.zip.ZipArchiveEntry> entries =
            zipFile.getEntriesInPhysicalOrder();

        org.junit.Assert.assertTrue(entries.hasMoreElements());
        org.junit.Assert.assertEquals("second.txt", entries.nextElement().getName());
        org.junit.Assert.assertTrue(entries.hasMoreElements());
        org.junit.Assert.assertEquals("first.txt", entries.nextElement().getName());
        org.junit.Assert.assertFalse(entries.hasMoreElements());
    } finally {
        org.apache.commons.compress.archivers.zip.ZipFile.closeQuietly(zipFile);
        archive.delete();
    }
}

@Test
public void entriesReturnedByEnumerationCanBeRead() throws Exception {
    java.io.File archive = createZipArchive(new String[] { "one.txt", "two.txt" });
    org.apache.commons.compress.archivers.zip.ZipFile zipFile = null;
    try {
        zipFile = new org.apache.commons.compress.archivers.zip.ZipFile(archive);
        java.util.Enumeration<org.apache.commons.compress.archivers.zip.ZipArchiveEntry> entries =
            zipFile.getEntries();
        int entryCount = 0;

        while (entries.hasMoreElements()) {
            org.apache.commons.compress.archivers.zip.ZipArchiveEntry entry = entries.nextElement();
            org.junit.Assert.assertTrue(zipFile.canReadEntryData(entry));
            java.io.InputStream input = zipFile.getInputStream(entry);
            org.junit.Assert.assertNotNull(input);
            try {
                int value = input.read();
                org.junit.Assert.assertTrue(value == 1 || value == 2);
                org.junit.Assert.assertEquals(-1, input.read());
            } finally {
                input.close();
            }
            entryCount++;
        }

        org.junit.Assert.assertEquals(2, entryCount);
    } finally {
        org.apache.commons.compress.archivers.zip.ZipFile.closeQuietly(zipFile);
        archive.delete();
    }
}

private void assertReadableByte(org.apache.commons.compress.archivers.zip.ZipFile zipFile,
                                String name, int expectedByte) throws Exception {
    org.apache.commons.compress.archivers.zip.ZipArchiveEntry entry = zipFile.getEntry(name);
    org.junit.Assert.assertNotNull(entry);
    java.io.InputStream input = zipFile.getInputStream(entry);
    org.junit.Assert.assertNotNull(input);
    try {
        org.junit.Assert.assertEquals(expectedByte, input.read());
        org.junit.Assert.assertEquals(-1, input.read());
    } finally {
        input.close();
    }
}

private java.io.File createZipArchive(String[] names) throws java.io.IOException {
    java.io.File archive = java.io.File.createTempFile("zipfile-", ".zip");
    java.util.zip.ZipOutputStream output =
        new java.util.zip.ZipOutputStream(new java.io.FileOutputStream(archive));
    try {
        for (int i = 0; i < names.length; i++) {
            output.putNextEntry(new java.util.zip.ZipEntry(names[i]));
            output.write(i + 1);
            output.closeEntry();
        }
    } finally {
        output.close();
    }
    return archive;
}