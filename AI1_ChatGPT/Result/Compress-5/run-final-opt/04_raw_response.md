public void testMatchesRecognizesZipSignaturesAndRejectsOtherData() {
    assertTrue(org.apache.commons.compress.archivers.zip.ZipArchiveInputStream.matches(
            new byte[] { 'P', 'K', 3, 4 }, 4));
    assertTrue(org.apache.commons.compress.archivers.zip.ZipArchiveInputStream.matches(
            new byte[] { 'P', 'K', 5, 6 }, 4));
    assertFalse(org.apache.commons.compress.archivers.zip.ZipArchiveInputStream.matches(
            new byte[] { 'P', 'K', 1, 2 }, 4));
}

public void testGetNextEntryStopsAtCentralDirectory() throws Exception {
    byte[] centralDirectory = new byte[30];
    centralDirectory[0] = 'P';
    centralDirectory[1] = 'K';
    centralDirectory[2] = 1;
    centralDirectory[3] = 2;

    org.apache.commons.compress.archivers.zip.ZipArchiveInputStream in =
            new org.apache.commons.compress.archivers.zip.ZipArchiveInputStream(
                    new java.io.ByteArrayInputStream(centralDirectory));

    assertNull(in.getNextEntry());
    assertNull(in.getNextZipEntry());
}

public void testGetNextZipEntryRejectsUnknownHeaderSignature() throws Exception {
    org.apache.commons.compress.archivers.zip.ZipArchiveInputStream in =
            new org.apache.commons.compress.archivers.zip.ZipArchiveInputStream(
                    new java.io.ByteArrayInputStream(new byte[30]));

    assertNull(in.getNextZipEntry());
}

public void testReadAfterCloseThrowsIOException() throws Exception {
    org.apache.commons.compress.archivers.zip.ZipArchiveInputStream in =
            new org.apache.commons.compress.archivers.zip.ZipArchiveInputStream(
                    new java.io.ByteArrayInputStream(new byte[0]));
    in.close();

    try {
        in.read(new byte[1], 0, 1);
        fail("Reading from a closed stream must throw an IOException");
    } catch (java.io.IOException expected) {
    }
}