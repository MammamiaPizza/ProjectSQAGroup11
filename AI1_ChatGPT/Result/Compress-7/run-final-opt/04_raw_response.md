public void testFormatOctalBytesWritesSpaceAndNullTerminators() {
    byte[] buffer = new byte[8];

    assertEquals(8, TarUtils.formatOctalBytes(42, buffer, 0, buffer.length));
    assertEquals((byte) '0', buffer[0]);
    assertEquals((byte) '0', buffer[1]);
    assertEquals((byte) '0', buffer[2]);
    assertEquals((byte) '0', buffer[3]);
    assertEquals((byte) '5', buffer[4]);
    assertEquals((byte) '2', buffer[5]);
    assertEquals((byte) ' ', buffer[6]);
    assertEquals(0, buffer[7]);
}

public void testFormatLongOctalBytesFormatsZeroWithTrailingSpace() {
    byte[] buffer = new byte[8];

    assertEquals(8, TarUtils.formatLongOctalBytes(0, buffer, 0, buffer.length));
    assertEquals((byte) '0', buffer[0]);
    assertEquals((byte) '0', buffer[1]);
    assertEquals((byte) '0', buffer[2]);
    assertEquals((byte) '0', buffer[3]);
    assertEquals((byte) '0', buffer[4]);
    assertEquals((byte) '0', buffer[5]);
    assertEquals((byte) '0', buffer[6]);
    assertEquals((byte) ' ', buffer[7]);
}

public void testChecksumAndChecksumFormattingUseUnsignedByteValues() {
    byte[] data = new byte[] { (byte) -1, 0, 1, 127, (byte) -128 };
    byte[] buffer = new byte[8];

    assertEquals(511, TarUtils.computeCheckSum(data));
    assertEquals(8, TarUtils.formatCheckSumOctalBytes(511, buffer, 0, buffer.length));
    assertEquals((byte) '0', buffer[0]);
    assertEquals((byte) '0', buffer[1]);
    assertEquals((byte) '0', buffer[2]);
    assertEquals((byte) '7', buffer[3]);
    assertEquals((byte) '7', buffer[4]);
    assertEquals((byte) '7', buffer[5]);
    assertEquals(0, buffer[6]);
    assertEquals((byte) ' ', buffer[7]);
}