@Test
public void formatLongOctalBytesRoundTripsLargeBinaryValue() {
    final byte[] field = new byte[12];
    final long value = 1L << 40;

    TarUtils.formatLongOctalBytes(value, field, 0, field.length);

    org.junit.Assert.assertTrue((field[0] & 0x80) != 0);
    org.junit.Assert.assertEquals(value, TarUtils.parseOctal(field, 0, field.length));
}

@Test
public void formatLongOctalBytesRoundTripsNegativeBinaryValue() {
    final byte[] field = new byte[8];

    TarUtils.formatLongOctalBytes(-1L, field, 0, field.length);

    org.junit.Assert.assertEquals(-1L, TarUtils.parseOctal(field, 0, field.length));
}

@Test
public void formattedChecksumIsVerifiedAndDetectsHeaderChanges() {
    final byte[] header = new byte[512];
    for (int i = 148; i < 156; i++) {
        header[i] = (byte) ' ';
    }

    TarUtils.formatCheckSumOctalBytes(TarUtils.computeCheckSum(header), header, 148, 8);

    org.junit.Assert.assertTrue(TarUtils.verifyCheckSum(header));
    header[0] = 1;
    org.junit.Assert.assertFalse(TarUtils.verifyCheckSum(header));
}

@Test
public void formatNameBytesTruncatesToFieldLength() {
    final byte[] buffer = new byte[6];

    org.junit.Assert.assertEquals(5, TarUtils.formatNameBytes("abcdef", buffer, 1, 4));

    org.junit.Assert.assertEquals("abcd", TarUtils.parseName(buffer, 1, 4));
}