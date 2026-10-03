@Test
public void testComputeCheckSum() {
    byte[] buf = new byte[] { 10, 20, 30, 40, (byte) 200 };
    long expected = (10 & 0xFFL) + (20 & 0xFFL) + (30 & 0xFFL) + (40 & 0xFFL) + (200 & 0xFFL);
    assertEquals(expected, TarUtils.computeCheckSum(buf));
}

@Test
public void testFormatOctalBytesRoundtrip() {
    byte[] buf = new byte[4];
    TarUtils.formatOctalBytes(7L, buf, 0, 4);
    assertEquals(7L, TarUtils.parseOctal(buf, 0, 4));
}