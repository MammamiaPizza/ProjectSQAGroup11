@Test
public void testParseOctalThrowsForEmbeddedNul() {
    byte[] buffer = new byte[]{'1', '2', 0, '3'};
    try {
        TarUtils.parseOctal(buffer, 0, 4);
        fail("Expected IllegalArgumentException for embedded NUL");
    } catch (IllegalArgumentException expected) {
        // expected
    }
}

@Test
public void testComputeCheckSum() {
    byte[] buf = new byte[]{(byte) 1, (byte) 2, (byte) 255};
    assertEquals(258L, TarUtils.computeCheckSum(buf));
}

@Test
public void testFormatLongOctalBytesTrailingNulSpace() {
    byte[] buf = new byte[20];
    int offset = 0;
    int length = 12;
    TarUtils.formatLongOctalBytes(0x1FF, buf, offset, length);
    assertEquals("Trailing NUL", 0, buf[offset + length - 2]);
    assertEquals("Trailing space", (byte) ' ', buf[offset + length - 1]);
}

@Test
public void testFormatCheckSumOctalBytesTrailingNulSpace() {
    byte[] buf = new byte[10];
    int offset = 0;
    int length = 8;
    TarUtils.formatCheckSumOctalBytes(1000L, buf, offset, length);
    assertEquals("Trailing NUL", 0, buf[offset + length - 2]);
    assertEquals("Trailing space", (byte) ' ', buf[offset + length - 1]);
}