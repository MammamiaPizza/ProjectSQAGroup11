package org.apache.commons.compress.archivers.tar;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TarUtilsTest {

    @Test
    public void parsesAsciiOctalWithLeadingSpacesAndTrailingPadding() {
        byte[] field = new byte[] { ' ', ' ', '1', '7', '5', ' ', 0 };

        assertEquals(125L, TarUtils.parseOctal(field, 0, field.length));
    }

    @Test
    public void parsesPositiveBase256ValueInSmallestSupportedField() {
        byte[] field = new byte[] { (byte) 0x80, (byte) 0xff };

        assertEquals(255L, TarUtils.parseOctal(field, 0, field.length));
    }

    @Test
    public void parsesNegativeBase256ValueInSmallestSupportedField() {
        byte[] field = new byte[] { (byte) 0xff, (byte) 0x80 };

        assertEquals(-128L, TarUtils.parseOctal(field, 0, field.length));
    }

    @Test
    public void parsesPositiveBase256ValueUsingBigIntegerSizedField() {
        byte[] field = new byte[] {
            (byte) 0x80, 0, 0, 0, 0, 0, 0, 0, 42
        };

        assertEquals(42L, TarUtils.parseOctal(field, 0, field.length));
    }

    @Test
    public void parsesMaximumSignedLongBase256Value() {
        byte[] field = new byte[] {
            (byte) 0x80,
            0x7f,
            (byte) 0xff,
            (byte) 0xff,
            (byte) 0xff,
            (byte) 0xff,
            (byte) 0xff,
            (byte) 0xff,
            (byte) 0xff
        };

        assertEquals(Long.MAX_VALUE, TarUtils.parseOctal(field, 0, field.length));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsInvalidAsciiOctalDigit() {
        byte[] field = new byte[] { '1', '8', ' ', 0 };

        TarUtils.parseOctal(field, 0, field.length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsAsciiFieldWithoutTrailingSpaceOrNul() {
        byte[] field = new byte[] { '1', '2', '3', '4' };

        TarUtils.parseOctal(field, 0, field.length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsFieldLengthsSmallerThanTwoBytes() {
        TarUtils.parseOctal(new byte[] { '0' }, 0, 1);
    }
}
