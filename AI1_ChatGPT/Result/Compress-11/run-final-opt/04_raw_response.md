@Test
public void detectsZipArchiveFromSignature() throws Exception {
    byte[] signature = new byte[] { 'P', 'K', 3, 4 };
    org.apache.commons.compress.archivers.ArchiveInputStream stream =
        new ArchiveStreamFactory().createArchiveInputStream(
            new java.io.ByteArrayInputStream(signature));
    try {
        assertTrue(stream instanceof org.apache.commons.compress.archivers.zip.ZipArchiveInputStream);
    } finally {
        stream.close();
    }
}

@Test
public void detectsArArchiveFromSignature() throws Exception {
    byte[] signature = new byte[] { '!', '<', 'a', 'r', 'c', 'h', '>', '\n' };
    org.apache.commons.compress.archivers.ArchiveInputStream stream =
        new ArchiveStreamFactory().createArchiveInputStream(
            new java.io.ByteArrayInputStream(signature));
    try {
        assertTrue(stream instanceof org.apache.commons.compress.archivers.ar.ArArchiveInputStream);
    } finally {
        stream.close();
    }
}

@Test
public void detectsCpioArchiveFromSignature() throws Exception {
    byte[] signature = new byte[] { '0', '7', '0', '7', '0', '1' };
    org.apache.commons.compress.archivers.ArchiveInputStream stream =
        new ArchiveStreamFactory().createArchiveInputStream(
            new java.io.ByteArrayInputStream(signature));
    try {
        assertTrue(stream instanceof org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream);
    } finally {
        stream.close();
    }
}

@Test
public void detectsTarArchiveFromUstarSignature() throws Exception {
    byte[] header = new byte[512];
    header[257] = 'u';
    header[258] = 's';
    header[259] = 't';
    header[260] = 'a';
    header[261] = 'r';
    org.apache.commons.compress.archivers.ArchiveInputStream stream =
        new ArchiveStreamFactory().createArchiveInputStream(
            new java.io.ByteArrayInputStream(header));
    try {
        assertTrue(stream instanceof org.apache.commons.compress.archivers.tar.TarArchiveInputStream);
    } finally {
        stream.close();
    }
}