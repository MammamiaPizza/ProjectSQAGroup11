package org.apache.commons.compress.archivers.tar;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TarUtilsParseOctalRegressionTest {

    @Test
    public void parsesTwelveDigitOctalFieldWithoutTrailingTerminator() {
        final byte[] field = "777777777777".getBytes();

        assertEquals(8589934591L, TarUtils.parseOctal(field, 0, field.length));
    }

    @Test
    public void parsesOctalFieldWithLeadingSpaceTrailingNulAndOffset() {
        final byte[] buffer = new byte[] {
            'x', ' ', '0', '0', '0', '7', '1', 0, 'y'
        };

        assertEquals(57L, TarUtils.parseOctal(buffer, 1, 7));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNonOctalDigitInsideField() {
        final byte[] field = new byte[] { '0', '0', '0', '8', 0 };

        TarUtils.parseOctal(field, 0, field.length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsFieldsShorterThanTwoBytes() {
        TarUtils.parseOctal(new byte[] { 0 }, 0, 1);
    }
}