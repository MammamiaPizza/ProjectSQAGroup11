@org.junit.Test
public void compress28DefaultRecordSizeIsConsistentAcrossConvenienceConstructors() {
    org.apache.commons.compress.archivers.tar.TarArchiveInputStream defaultStream =
            new org.apache.commons.compress.archivers.tar.TarArchiveInputStream(
                    new java.io.ByteArrayInputStream(new byte[0]));
    org.apache.commons.compress.archivers.tar.TarArchiveInputStream encodedStream =
            new org.apache.commons.compress.archivers.tar.TarArchiveInputStream(
                    new java.io.ByteArrayInputStream(new byte[0]), "UTF-8");
    org.apache.commons.compress.archivers.tar.TarArchiveInputStream blockStream =
            new org.apache.commons.compress.archivers.tar.TarArchiveInputStream(
                    new java.io.ByteArrayInputStream(new byte[0]), 10240);

    org.junit.Assert.assertEquals(defaultStream.getRecordSize(), encodedStream.getRecordSize());
    org.junit.Assert.assertEquals(defaultStream.getRecordSize(), blockStream.getRecordSize());
}

@org.junit.Test(expected = java.io.IOException.class)
public void compress28RejectsTruncatedFirstTarRecord() throws java.io.IOException {
    org.apache.commons.compress.archivers.tar.TarArchiveInputStream input =
            new org.apache.commons.compress.archivers.tar.TarArchiveInputStream(
                    new java.io.ByteArrayInputStream(new byte[] { 0 }));

    input.getNextTarEntry();
}