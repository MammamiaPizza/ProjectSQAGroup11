public void testComputeCheckSum() {
    byte[] buf = new byte[]{(byte) 1, (byte) 2, (byte) 255};
    assertEquals(258L, TarUtils.computeCheckSum(buf));
}

public void testParseName() {
    byte[] buf = new byte[]{'f', 'o', 'o', 0, 'x'};
    assertEquals("foo", TarUtils.parseName(buf, 0, 5));
}

public void testFormatCheckSumOctalBytes() {
    byte[] buf = new byte[8];
    int offset = 1;
    int length = 4;
    int result = TarUtils.formatCheckSumOctalBytes(0, buf, offset, length);
    assertEquals(offset + length, result);
    // trailing NUL and space
    assertEquals(0, buf[offset + length - 2]);
    assertEquals(' ', buf[offset + length - 1]);
}

public void testFormatLongOctalBytes() {
    byte[] buf = new byte[6];
    int offset = 1;
    int length = 3;
    int result = TarUtils.formatLongOctalBytes(7, buf, offset, length);
    assertEquals(offset + length, result);
    assertEquals(' ', buf[offset + length - 1]);
}