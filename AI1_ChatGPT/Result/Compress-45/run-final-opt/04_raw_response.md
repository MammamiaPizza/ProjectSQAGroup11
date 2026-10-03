@Test
public void formatsAndParsesNearMinimumNegativeEightByteBinaryValueAtOffset() {
    final byte[] buffer = new byte[12];
    for (int i = 0; i < buffer.length; i++) {
        buffer[i] = (byte) 0x5a;
    }

    final long value = -72057594037927934L;
    final int end = TarUtils.formatLongOctalOrBinaryBytes(value, buffer, 2, 8);

    org.junit.Assert.assertEquals(10, end);
    org.junit.Assert.assertEquals((byte) 0x5a, buffer[1]);
    org.junit.Assert.assertEquals((byte) 0x5a, buffer[10]);
    org.junit.Assert.assertEquals(value, TarUtils.parseOctalOrBinary(buffer, 2, 8));
}

@Test
public void formatsNameAtOffsetAndClearsUnusedFieldBytes() {
    final byte[] buffer = new byte[10];
    for (int i = 0; i < buffer.length; i++) {
        buffer[i] = (byte) 0x7f;
    }

    final int end = TarUtils.formatNameBytes("abc", buffer, 2, 5);

    org.junit.Assert.assertEquals(7, end);
    org.junit.Assert.assertEquals("abc", TarUtils.parseName(buffer, 2, 5));
    org.junit.Assert.assertEquals((byte) 0, buffer[5]);
    org.junit.Assert.assertEquals((byte) 0, buffer[6]);
}

@Test
public void computesAndVerifiesChecksumUsingUnsignedByteValues() {
    final byte[] header = new byte[512];
    header[0] = (byte) 0xff;
    for (int i = 148; i < 156; i++) {
        header[i] = (byte) ' ';
    }

    final long checksum = TarUtils.computeCheckSum(header);
    org.junit.Assert.assertEquals(511L, checksum);

    TarUtils.formatCheckSumOctalBytes(checksum, header, 148, 8);
    org.junit.Assert.assertTrue(TarUtils.verifyCheckSum(header));

    header[1] = 1;
    org.junit.Assert.assertFalse(TarUtils.verifyCheckSum(header));
}

@Test
public void parsesBooleanByteValues() {
    final byte[] buffer = new byte[] { 1, 0 };

    org.junit.Assert.assertTrue(TarUtils.parseBoolean(buffer, 0));
    org.junit.Assert.assertFalse(TarUtils.parseBoolean(buffer, 1));
}