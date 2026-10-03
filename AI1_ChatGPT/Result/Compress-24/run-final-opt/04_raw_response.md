@Test
public void parsesTwelveDigitOctalFieldWithoutTerminator() {
    final byte[] field = new byte[] {
        '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7'
    };

    org.junit.Assert.assertEquals(68719476735L, TarUtils.parseOctal(field, 0, field.length));
}

@Test
public void formatsAndParsesLargePositiveBinaryValue() {
    final byte[] field = new byte[12];
    final long value = 8589934592L;

    TarUtils.formatLongOctalBytes(value, field, 0, field.length);

    org.junit.Assert.assertEquals(value, TarUtils.parseOctal(field, 0, field.length));
}

@Test
public void formatsAndParsesNegativeBinaryValue() {
    final byte[] field = new byte[12];

    TarUtils.formatLongOctalBytes(-1L, field, 0, field.length);

    org.junit.Assert.assertEquals(-1L, TarUtils.parseOctal(field, 0, field.length));
}

@Test
public void computesChecksumUsingUnsignedByteValues() {
    final byte[] bytes = new byte[] { 0, 1, -1, 127, -128 };

    org.junit.Assert.assertEquals(511L, TarUtils.computeCheckSum(bytes));
}