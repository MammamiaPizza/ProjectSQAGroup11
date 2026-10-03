public void testFormatNameBytesTruncatesAndPads() {
    final byte[] truncated = new byte[6];
    assertEquals(5, TarUtils.formatNameBytes("abcdef", truncated, 1, 4));
    assertEquals((byte) 'a', truncated[1]);
    assertEquals((byte) 'b', truncated[2]);
    assertEquals((byte) 'c', truncated[3]);
    assertEquals((byte) 'd', truncated[4]);

    final byte[] padded = new byte[] { 1, 1, 1, 1, 1 };
    assertEquals(5, TarUtils.formatNameBytes("Z", padded, 1, 4));
    assertEquals((byte) 'Z', padded[1]);
    assertEquals((byte) 0, padded[2]);
    assertEquals((byte) 0, padded[3]);
    assertEquals((byte) 0, padded[4]);
}

public void testFormatOctalBytesAndFormatLongOctalBytes() {
    final byte[] octal = new byte[6];
    assertEquals(6, TarUtils.formatOctalBytes(83L, octal, 0, octal.length));
    assertEquals((byte) '0', octal[0]);
    assertEquals((byte) '1', octal[1]);
    assertEquals((byte) '2', octal[2]);
    assertEquals((byte) '3', octal[3]);
    assertEquals((byte) 0, octal[4]);
    assertEquals((byte) ' ', octal[5]);

    final byte[] longOctal = new byte[4];
    assertEquals(4, TarUtils.formatLongOctalBytes(0L, longOctal, 0, longOctal.length));
    assertEquals((byte) '0', longOctal[0]);
    assertEquals((byte) '0', longOctal[1]);
    assertEquals((byte) '0', longOctal[2]);
    assertEquals((byte) ' ', longOctal[3]);
}

public void testFormatCheckSumOctalBytesAndComputeCheckSum() {
    final byte[] checksum = new byte[10];
    assertEquals(9, TarUtils.formatCheckSumOctalBytes(511L, checksum, 1, 8));
    assertEquals((byte) '0', checksum[1]);
    assertEquals((byte) '0', checksum[2]);
    assertEquals((byte) '0', checksum[3]);
    assertEquals((byte) '7', checksum[4]);
    assertEquals((byte) '7', checksum[5]);
    assertEquals((byte) '7', checksum[6]);
    assertEquals((byte) 0, checksum[7]);
    assertEquals((byte) ' ', checksum[8]);

    assertEquals(256L, TarUtils.computeCheckSum(new byte[] { (byte) -1, 1, 0 }));
}

public void testFormatLongOctalBytesRejectsValuesThatDoNotFit() {
    try {
        TarUtils.formatLongOctalBytes(64L, new byte[3], 0, 3);
        fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
    }
}