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
}
