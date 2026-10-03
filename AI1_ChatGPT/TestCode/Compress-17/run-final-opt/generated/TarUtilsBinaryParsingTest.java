package org.apache.commons.compress.archivers.tar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class TarUtilsBinaryParsingTest {

    @Test
    public void parsesOctalWithLeadingSpaceAndTwoTrailingPaddingBytes() {
        byte[] field = new byte[] { ' ', '0', '0', '7', '5', '5', ' ', 0 };

        assertEquals(493L, TarUtils.parseOctal(field, 0, field.length));
    }

    @Test
    public void parsesNullInitialOctalFieldAsZero() {
        byte[] field = new byte[] { 0, '7', '7', '7', ' ', 0 };

        assertEquals(0L, TarUtils.parseOctal(field, 0, field.length));
    }

    @Test
    public void rejectsOctalFieldsShorterThanTwoBytes() {
        try {
            TarUtils.parseOctal(new byte[] { '0' }, 0, 1);
            fail("A one-byte octal field must be rejected");
        } catch (IllegalArgumentException expected) {
            assertEquals(true, expected.getMessage().contains("at least 2"));
        }
    }

    @Test
    public void rejectsInvalidOctalDigits() {
        byte[] field = new byte[] { '0', '8', ' ', 0 };

        try {
            TarUtils.parseOctal(field, 0, field.length);
            fail("The digit 8 is not valid octal");
        } catch (IllegalArgumentException expected) {
            assertEquals(true, expected.getMessage().contains("Invalid byte"));
        }
    }

    @Test
    public void parsesLargestPositiveShortBinaryField() {
        byte[] field = new byte[8];
        field[0] = (byte) 0x80;
        for (int i = 1; i < field.length; i++) {
            field[i] = (byte) 0xff;
        }

        assertEquals((1L << 56) - 1L, TarUtils.parseOctalOrBinary(field, 0, field.length));
    }

    @Test
    public void parsesSmallestNegativeShortBinaryField() {
        byte[] field = new byte[] { (byte) 0xff, (byte) 0x80 };

        assertEquals(-128L, TarUtils.parseOctalOrBinary(field, 0, field.length));
    }

    @Test
    public void parsesNegativeBigIntegerBinaryField() {
        byte[] field = new byte[] {
            (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0xff, (byte) 0xff, (byte) 0xff, 0
        };

        assertEquals(-256L, TarUtils.parseOctalOrBinary(field, 0, field.length));
    }

    @Test
    public void parsesLargestSignedLongPositiveBigIntegerBinaryField() {
        byte[] field = new byte[] {
            (byte) 0x80, 0x7f, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff
        };

        assertEquals(Long.MAX_VALUE, TarUtils.parseOctalOrBinary(field, 0, field.length));
    }

    @Test
    public void parsesLongMinValueFromNegativeBigIntegerBinaryField() {
        byte[] field = new byte[] {
            (byte) 0xff, (byte) 0x80, 0, 0, 0, 0, 0, 0, 0
        };

        assertEquals(Long.MIN_VALUE, TarUtils.parseOctalOrBinary(field, 0, field.length));
    }

    @Test
    public void rejectsPositiveBigIntegerBinaryValueBeyondSignedLongRange() {
        byte[] field = new byte[] {
            (byte) 0x80, (byte) 0x80, 0, 0, 0, 0, 0, 0, 0
        };

        try {
            TarUtils.parseOctalOrBinary(field, 0, field.length);
            fail("A positive base-256 value greater than Long.MAX_VALUE must be rejected");
        } catch (IllegalArgumentException expected) {
            assertEquals(true, expected.getMessage().contains("exceeds maximum signed long"));
        }
    }
}
