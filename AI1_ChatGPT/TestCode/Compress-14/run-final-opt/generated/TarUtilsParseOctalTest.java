package org.apache.commons.compress.archivers.tar;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TarUtilsParseOctalTest {

    @Test
    public void parsesOctalFieldWithLeadingNulAndSpacePaddingAtAnOffset() {
        byte[] buffer = new byte[] {
            'x', 'x',
            ' ', 0, '1', '2', '3', ' ', 0,
            'y'
        };

        assertEquals(83L, TarUtils.parseOctal(buffer, 2, 7));
    }

    @Test
    public void parsesPositiveOctalValueWithStandardTrailingSpaceAndNul() {
        byte[] buffer = new byte[] { '0', '0', '0', '1', '7', ' ', 0 };

        assertEquals(15L, TarUtils.parseOctal(buffer, 0, buffer.length));
    }

    @Test
    public void parsesAllNulFieldAsZero() {
        byte[] buffer = new byte[] { 0, 0, 0, 0 };

        assertEquals(0L, TarUtils.parseOctal(buffer, 0, buffer.length));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNonOctalDigits() {
        byte[] buffer = new byte[] { '1', '8', 0 };

        TarUtils.parseOctal(buffer, 0, buffer.length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsFieldsShorterThanTwoBytes() {
        TarUtils.parseOctal(new byte[] { 0 }, 0, 1);
    }
}
