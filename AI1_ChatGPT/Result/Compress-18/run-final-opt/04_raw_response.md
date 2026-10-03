@Test
public void testNonAsciiFileNameInPosixModeCanBeWrittenWithPayload() throws Exception {
    final java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
    final TarArchiveOutputStream output = new TarArchiveOutputStream(bytes);
    final byte[] payload = new byte[] { 1, 2, 3 };

    output.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
    final org.apache.commons.compress.archivers.tar.TarArchiveEntry entry =
        new org.apache.commons.compress.archivers.tar.TarArchiveEntry("\u00e4-file");
    entry.setSize(payload.length);

    output.putArchiveEntry(entry);
    output.write(payload);
    output.closeArchiveEntry();
    output.finish();

    org.junit.Assert.assertTrue(bytes.size() > payload.length);
    output.close();
}

@Test
public void testBigNumberErrorModeRejectsOversizedEntrySize() throws Exception {
    final TarArchiveOutputStream output =
        new TarArchiveOutputStream(new java.io.ByteArrayOutputStream());
    output.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_ERROR);

    final org.apache.commons.compress.archivers.tar.TarArchiveEntry entry =
        new org.apache.commons.compress.archivers.tar.TarArchiveEntry("large");
    entry.setSize(1L << 34);

    try {
        output.putArchiveEntry(entry);
        org.junit.Assert.fail("oversized entry sizes must be rejected in error mode");
    } catch (java.io.IOException expected) {
        // expected
    } finally {
        output.close();
    }
}

@Test
public void testBlockSizeConstructorUsesTheDefaultRecordSize() throws Exception {
    final TarArchiveOutputStream defaultOutput =
        new TarArchiveOutputStream(new java.io.ByteArrayOutputStream());
    final TarArchiveOutputStream blockSizeOutput =
        new TarArchiveOutputStream(new java.io.ByteArrayOutputStream(), 10240);

    org.junit.Assert.assertEquals(defaultOutput.getRecordSize(),
        blockSizeOutput.getRecordSize());

    defaultOutput.close();
    blockSizeOutput.close();
}

@Test
public void testCloseFinishesArchiveAndClosesUnderlyingStream() throws Exception {
    final boolean[] closed = new boolean[] { false };
    final java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream() {
        @Override
        public void close() throws java.io.IOException {
            closed[0] = true;
            super.close();
        }
    };
    final TarArchiveOutputStream output = new TarArchiveOutputStream(bytes);

    output.close();

    org.junit.Assert.assertTrue(closed[0]);
    org.junit.Assert.assertTrue(bytes.size() > 0);
}