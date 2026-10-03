@Test
public void matchesRecognizesSupportedCpioSignaturesAndRejectsInvalidOnes() {
    org.junit.Assert.assertFalse(CpioArchiveInputStream.matches(
            new byte[] { 0x30, 0x37, 0x30, 0x37, 0x30, 0x31 }, 5));
    org.junit.Assert.assertTrue(CpioArchiveInputStream.matches(
            new byte[] { 0x71, (byte) 0xc7, 0, 0, 0, 0 }, 6));
    org.junit.Assert.assertTrue(CpioArchiveInputStream.matches(
            new byte[] { (byte) 0xc7, 0x71, 0, 0, 0, 0 }, 6));
    org.junit.Assert.assertTrue(CpioArchiveInputStream.matches(
            new byte[] { 0x30, 0x37, 0x30, 0x37, 0x30, 0x31 }, 6));
    org.junit.Assert.assertTrue(CpioArchiveInputStream.matches(
            new byte[] { 0x30, 0x37, 0x30, 0x37, 0x30, 0x32 }, 6));
    org.junit.Assert.assertTrue(CpioArchiveInputStream.matches(
            new byte[] { 0x30, 0x37, 0x30, 0x37, 0x30, 0x37 }, 6));
    org.junit.Assert.assertFalse(CpioArchiveInputStream.matches(
            new byte[] { 0x30, 0x37, 0x30, 0x37, 0x30, 0x30 }, 6));
    org.junit.Assert.assertFalse(CpioArchiveInputStream.matches(
            new byte[] { 0x31, 0x37, 0x30, 0x37, 0x30, 0x31 }, 6));
}

@Test
public void getNextEntryRejectsUnknownMagic() throws Exception {
    CpioArchiveInputStream input = new CpioArchiveInputStream(
            new java.io.ByteArrayInputStream(new byte[] { 'b', 'a', 'd', 'c', 'p', 'i' }));
    try {
        input.getNextEntry();
        org.junit.Assert.fail("Expected an IOException for an unknown CPIO magic");
    } catch (java.io.IOException expected) {
        org.junit.Assert.assertTrue(expected.getMessage().contains("Unknown magic"));
    } finally {
        input.close();
    }
}

@Test
public void availableReportsOpenStreamAndFailsAfterClose() throws Exception {
    CpioArchiveInputStream input = new CpioArchiveInputStream(
            new java.io.ByteArrayInputStream(new byte[0]));
    org.junit.Assert.assertEquals(1, input.available());

    input.close();
    input.close();

    try {
        input.available();
        org.junit.Assert.fail("Expected an IOException after closing the stream");
    } catch (java.io.IOException expected) {
        org.junit.Assert.assertEquals("Stream closed", expected.getMessage());
    }
}