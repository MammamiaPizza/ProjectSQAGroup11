@Test
public void parsesLeadingSpaceBeforeOctalDigits() {
    assertEquals(7L, TarUtils.parseOctal(new byte[] { ' ', '7', 0 }, 0, 3));
}

@Test
public void formatsAndParsesLargeBinaryOctalField() {
    final long value = 1L << 40;
    final byte[] field = new byte[8];

    assertEquals(8, TarUtils.formatLongOctalBytes(value, field, 0, field.length));
    assertEquals(value, TarUtils.parseOctal(field, 0, field.length));
}

@Test
public void formatsChecksumThatVerificationAcceptsAndDetectsChanges() {
    final byte[] header = new byte[512];
    final long checksum = 8L * ' ';

    TarUtils.formatCheckSumOctalBytes(checksum, header, 148, 8);
    assertTrue(TarUtils.verifyCheckSum(header));

    header[0] = 1;
    assertTrue(!TarUtils.verifyCheckSum(header));
}

@Test
public void formatsAndParsesAsciiNameWithinField() {
    final byte[] buffer = new byte[6];

    TarUtils.formatNameBytes("abc", buffer, 1, 5);

    assertEquals("abc", TarUtils.parseName(buffer, 1, 5));
}