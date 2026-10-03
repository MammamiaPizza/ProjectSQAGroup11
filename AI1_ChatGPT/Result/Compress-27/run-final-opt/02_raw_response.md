package org.apache.commons.compress.archivers.tar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class TarUtilsParseOctalTest {

    @Test
    public void parsesSpaceFollowedByNullAsZero() {
        assertEquals(0L, TarUtils.parseOctal(new byte[] { ' ', 0 }, 0, 2));
    }

    @Test
    public void parsesOctalDigitsWithTrailingSpaceAndNull() {
        assertEquals(127L, TarUtils.parseOctal(
                new byte[] { '0', '1', '7', '7', ' ', 0 }, 0, 6));
    }

    @Test
    public void parsesFieldAtNonZeroOffsetWithPadding() {
        byte[] buffer = new byte[] { 'x', ' ', '1', '7', ' ', 0, 'x' };

        assertEquals(15L, TarUtils.parseOctal(buffer, 1, 5));
    }

    @Test
    public void parsesSingleOctalDigitWithTrailingNull() {
        assertEquals(7L, TarUtils.parseOctal(new byte[] { '7', 0 }, 0, 2));
    }

    @Test
    public void parsesAllSpacePaddingAsZero() {
        assertEquals(0L, TarUtils.parseOctal(new byte[] { ' ', ' ' }, 0, 2));
    }

    @Test
    public void parsesLeadingNullAsZero() {
        assertEquals(0L, TarUtils.parseOctal(new byte[] { 0, '7' }, 0, 2));
    }

    @Test
    public void rejectsNonOctalDigits() {
        try {
            TarUtils.parseOctal(new byte[] { '8', 0 }, 0, 2);
            fail("Expected an IllegalArgumentException for a non-octal digit");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Invalid byte"));
        }
    }

    @Test
    public void rejectsFieldsShorterThanTwoBytes() {
        try {
            TarUtils.parseOctal(new byte[] { '0' }, 0, 1);
            fail("Expected an IllegalArgumentException for a one-byte field");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("at least 2"));
        }
    }
}