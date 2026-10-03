package org.apache.commons.compress.archivers.tar;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TarUtilsCompress45Test {

    @Test
    public void formatsAndParsesLargestRequiredNegativeEightByteBinaryValue() {
        final long value = -72057594037927935L;
        final byte[] buffer = new byte[8];

        final int end = TarUtils.formatLongOctalOrBinaryBytes(value, buffer, 0, buffer.length);

        assertEquals(buffer.length, end);
        assertEquals(value, TarUtils.parseOctalOrBinary(buffer, 0, buffer.length));
    }

    @Test
    public void formatsAndParsesLargestPositiveEightByteBinaryValue() {
        final long value = 72057594037927935L;
        final byte[] buffer = new byte[8];

        assertEquals(8, TarUtils.formatLongOctalOrBinaryBytes(value, buffer, 0, buffer.length));
        assertEquals(value, TarUtils.parseOctalOrBinary(buffer, 0, buffer.length));
    }

    @Test
    public void formatsAndParsesNegativeOneUsingEightByteBinaryEncoding() {
        final byte[] buffer = new byte[8];

        TarUtils.formatLongOctalOrBinaryBytes(-1L, buffer, 0, buffer.length);

        assertEquals(-1L, TarUtils.parseOctalOrBinary(buffer, 0, buffer.length));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsPositiveValueOutsideEightByteBinaryRange() {
        TarUtils.formatLongOctalOrBinaryBytes(1L << 56, new byte[8], 0, 8);
    }

    @Test
    public void formatOctalBytesRoundTripsAtItsSixDigitBoundary() {
        final byte[] buffer = new byte[10];
        final long value = 262143L;

        final int end = TarUtils.formatOctalBytes(value, buffer, 1, 8);

        assertEquals(9, end);
        assertEquals(value, TarUtils.parseOctal(buffer, 1, 8));
        assertEquals((byte) ' ', buffer[7]);
        assertEquals((byte) 0, buffer[8]);
    }

    @Test
    public void formatLongOctalBytesRoundTripsAtItsSevenDigitBoundary() {
        final byte[] buffer = new byte[8];
        final long value = 2097151L;

        assertEquals(8, TarUtils.formatLongOctalBytes(value, buffer, 0, buffer.length));
        assertEquals(value, TarUtils.parseOctal(buffer, 0, buffer.length));
        assertEquals((byte) ' ', buffer[7]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseOctalRejectsNonOctalDigits() {
        TarUtils.parseOctal(new byte[] { '0', '8', ' ' }, 0, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseOctalRequiresAtLeastTwoBytes() {
        TarUtils.parseOctal(new byte[] { '0' }, 0, 1);
    }

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
}
