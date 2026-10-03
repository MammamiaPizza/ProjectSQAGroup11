@org.junit.Test
public void customBlockSizeConstructorFlushesOneConfiguredBlockOnFinish() throws Exception {
    final java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
    final org.apache.commons.compress.archivers.tar.TarArchiveOutputStream out =
        new org.apache.commons.compress.archivers.tar.TarArchiveOutputStream(bytes, 2048);

    out.finish();

    org.junit.Assert.assertEquals(512, out.getRecordSize());
    org.junit.Assert.assertEquals(2048, bytes.size());
    org.junit.Assert.assertEquals(2048, out.getBytesWritten());
}

@org.junit.Test
public void cannotCreateArchiveEntryAfterFinishing() throws Exception {
    final org.apache.commons.compress.archivers.tar.TarArchiveOutputStream out =
        new org.apache.commons.compress.archivers.tar.TarArchiveOutputStream(
            new java.io.ByteArrayOutputStream());
    out.finish();

    try {
        out.createArchiveEntry(new java.io.File("entry-after-finish"), "entry-after-finish");
        org.junit.Assert.fail("Creating an entry after finishing must fail");
    } catch (java.io.IOException expected) {
        org.junit.Assert.assertEquals(true, expected.getMessage().contains("already been finished"));
    }
}

@org.junit.Test
public void rejectsLongFileNamesInDefaultLongFileMode() throws Exception {
    final org.apache.commons.compress.archivers.tar.TarArchiveOutputStream out =
        new org.apache.commons.compress.archivers.tar.TarArchiveOutputStream(
            new java.io.ByteArrayOutputStream());
    final String longName = new String(new char[101]).replace('\u0000', 'a');
    final org.apache.commons.compress.archivers.tar.TarArchiveEntry entry =
        new org.apache.commons.compress.archivers.tar.TarArchiveEntry(longName);
    entry.setSize(0);

    try {
        out.putArchiveEntry(entry);
        org.junit.Assert.fail("Long file names must be rejected in the default mode");
    } catch (RuntimeException expected) {
        org.junit.Assert.assertEquals(true, expected.getMessage().contains("too long"));
    }
}

@org.junit.Test
public void gnuLongFileModeWritesLongNameEntryAndArchive() throws Exception {
    final java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
    final org.apache.commons.compress.archivers.tar.TarArchiveOutputStream out =
        new org.apache.commons.compress.archivers.tar.TarArchiveOutputStream(bytes);
    out.setLongFileMode(org.apache.commons.compress.archivers.tar.TarArchiveOutputStream.LONGFILE_GNU);

    final String longName = new String(new char[101]).replace('\u0000', 'a');
    final org.apache.commons.compress.archivers.tar.TarArchiveEntry entry =
        new org.apache.commons.compress.archivers.tar.TarArchiveEntry(longName);
    entry.setSize(0);
    out.putArchiveEntry(entry);
    out.closeArchiveEntry();
    out.finish();

    org.junit.Assert.assertEquals(10240, bytes.size());
    org.junit.Assert.assertEquals(10240, out.getBytesWritten());
}