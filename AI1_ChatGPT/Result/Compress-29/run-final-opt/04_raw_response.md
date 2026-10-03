@org.junit.Test
public void defaultFactoryHasNoConfiguredEntryEncoding() {
    org.junit.Assert.assertNull(
            new org.apache.commons.compress.archivers.ArchiveStreamFactory().getEntryEncoding());
}

@org.junit.Test(expected = java.lang.IllegalArgumentException.class)
public void autodetectionRejectsStreamsWithoutMarkSupport() throws Exception {
    new org.apache.commons.compress.archivers.ArchiveStreamFactory().createArchiveInputStream(
            new java.io.InputStream() {
                @Override
                public int read() {
                    return -1;
                }
            });
}

@org.junit.Test(expected = org.apache.commons.compress.archivers.ArchiveException.class)
public void autodetectionRejectsUnknownSignatures() throws Exception {
    new org.apache.commons.compress.archivers.ArchiveStreamFactory().createArchiveInputStream(
            new java.io.ByteArrayInputStream(new byte[32]));
}

@org.junit.Test(expected = org.apache.commons.compress.archivers.StreamingNotSupportedException.class)
public void autodetectionReportsSevenZAsNotStreamable() throws Exception {
    new org.apache.commons.compress.archivers.ArchiveStreamFactory().createArchiveInputStream(
            new java.io.ByteArrayInputStream(new byte[] {
                    0x37, 0x7a, (byte) 0xbc, (byte) 0xaf, 0x27, 0x1c
            }));
}