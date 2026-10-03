@org.junit.Test
public void parsesOctalValueFollowedOnlyBySpacePadding() {
    final byte[] buffer = new byte[] { '0', '0', '0', '1', '2', '3', ' ', ' ' };

    org.junit.Assert.assertEquals(83L, TarUtils.parseOctal(buffer, 0, buffer.length));
}

@org.junit.Test
public void formatsLongOctalBytesWithTrailingSpace() {
    final byte[] buffer = new byte[7];

    org.junit.Assert.assertEquals(6, TarUtils.formatLongOctalBytes(83L, buffer, 1, 5));
    org.junit.Assert.assertEquals(83L, TarUtils.parseOctal(buffer, 1, 5));
    org.junit.Assert.assertEquals((byte) ' ', buffer[5]);
}

@org.junit.Test
public void formatsChecksumOctalBytesAndRejectsValuesThatDoNotFit() {
    final byte[] buffer = new byte[8];

    org.junit.Assert.assertEquals(7, TarUtils.formatCheckSumOctalBytes(83L, buffer, 1, 6));
    org.junit.Assert.assertEquals(83L, TarUtils.parseOctal(buffer, 1, 6));
    org.junit.Assert.assertEquals((byte) 0, buffer[5]);
    org.junit.Assert.assertEquals((byte) ' ', buffer[6]);

    try {
        TarUtils.formatCheckSumOctalBytes(4096L, new byte[4], 0, 4);
        org.junit.Assert.fail("Expected an IllegalArgumentException for a checksum that does not fit");
    } catch (final IllegalArgumentException expected) {
        // expected
    }
}

@org.junit.Test
public void computesChecksumUsingUnsignedByteValues() {
    final byte[] header = new byte[] { 0, 1, (byte) 0xff, 127, (byte) 0x80 };

    org.junit.Assert.assertEquals(511L, TarUtils.computeCheckSum(header));
}