@org.junit.Test
public void formatsLargeLongValuesUsingARepresentationThatCanBeParsed() {
    final byte[] field = new byte[12];

    org.junit.Assert.assertEquals(12,
            TarUtils.formatLongOctalBytes(1L << 33, field, 0, field.length));

    org.junit.Assert.assertEquals(1L << 33,
            TarUtils.parseOctal(field, 0, field.length));
}

@org.junit.Test
public void computesChecksumUsingUnsignedByteValues() {
    final byte[] bytes = new byte[] { 0, 1, (byte) 0xff, (byte) 0x80 };

    org.junit.Assert.assertEquals(384L, TarUtils.computeCheckSum(bytes));
}

@org.junit.Test
public void verifiesHeaderChecksumAndDetectsChangesOutsideChecksumField() {
    final byte[] header = new byte[512];
    for (int i = 148; i < 156; i++) {
        header[i] = (byte) ' ';
    }
    final long checksum = TarUtils.computeCheckSum(header);

    org.junit.Assert.assertEquals(156,
            TarUtils.formatCheckSumOctalBytes(checksum, header, 148, 8));
    org.junit.Assert.assertTrue(TarUtils.verifyCheckSum(header));

    header[0] = 1;
    org.junit.Assert.assertFalse(TarUtils.verifyCheckSum(header));
}