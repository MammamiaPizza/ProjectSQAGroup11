@org.junit.Test
public void parseOctalAcceptsZeroPaddedFieldWithTarTerminators() {
    final byte[] field = new byte[] {
        '0', '0', '0', '0', '0', '1', '2', '3', 0, ' '
    };

    org.junit.Assert.assertEquals(83L, TarUtils.parseOctal(field, 0, field.length));
}

@org.junit.Test
public void formatOctalBytesProducesAValueParseableByTarUtils() {
    final byte[] field = new byte[12];
    final long value = 01234567L;

    org.junit.Assert.assertEquals(field.length,
        TarUtils.formatOctalBytes(value, field, 0, field.length));
    org.junit.Assert.assertEquals(value, TarUtils.parseOctal(field, 0, field.length));
}

@org.junit.Test
public void formatLongOctalBytesRoundTripsLargeValuesUsingBinaryRepresentation() {
    final byte[] field = new byte[12];
    final long value = 1L << 40;

    org.junit.Assert.assertEquals(field.length,
        TarUtils.formatLongOctalBytes(value, field, 0, field.length));
    org.junit.Assert.assertEquals(value, TarUtils.parseOctal(field, 0, field.length));
}

@org.junit.Test
public void formatNameBytesTruncatesToTheAvailableTarFieldLength() {
    final byte[] field = new byte[8];

    org.junit.Assert.assertEquals(6,
        TarUtils.formatNameBytes("abcdef", field, 2, 4));
    org.junit.Assert.assertEquals("abcd", TarUtils.parseName(field, 2, 4));
}