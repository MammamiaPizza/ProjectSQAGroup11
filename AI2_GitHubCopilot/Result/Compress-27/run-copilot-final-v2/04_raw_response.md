@Test
public void testParseOctalSpaceMidInput() {
    byte[] buffer = new byte[] { 0x31, 0x20, 0x00 };
    assertEquals(1L, TarUtils.parseOctal(buffer, 0, 3));
}

@Test
public void testFormatCheckSumOctalBytes() {
    byte[] buf = new byte[8];
    int result = TarUtils.formatCheckSumOctalBytes(255L, buf, 0, 8);
    assertEquals(8, result);
    assertEquals(0, buf[6]);
    assertEquals((byte) ' ', buf[7]);
    for (int i = 0; i < 6; i++) {
        assertTrue(buf[i] >= '0' && buf[i] <= '7' || buf[i] == ' ');
    }
}

@Test
public void testComputeCheckSum() {
    byte[] buf = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    long expected = 0;
    for (byte b : buf) {
        expected += (b & 0xFF);
    }
    assertEquals(expected, TarUtils.computeCheckSum(buf));
}

@Test
public void testVerifyCheckSumValid() {
    byte[] header = new byte[512];
    for (int i = 0; i < header.length; i++) {
        header[i] = (byte) (i % 256);
    }
    for (int i = 148; i < 156; i++) {
        header[i] = (byte) ' ';
    }
    long sum = TarUtils.computeCheckSum(header);
    TarUtils.formatCheckSumOctalBytes(sum, header, 148, 8);
    assertTrue(TarUtils.verifyCheckSum(header));
}