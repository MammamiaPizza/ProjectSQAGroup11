package org.apache.commons.compress.archivers.tar;

import junit.framework.TestCase;

public class TarUtilsDefectsTest extends TestCase {

    public void testParseOctalRejectsFieldsShorterThanTwoBytes() {
        byte[] buffer = new byte[] { 'x', '7', 0 };

        assertParseOctalFails(buffer, 1, 1);
        assertParseOctalFails(buffer, 2, 0);
    }

    public void testParseOctalAcceptsMinimumValidTwoByteField() {
        assertEquals(7L, TarUtils.parseOctal(new byte[] { '7', 0 }, 0, 2));
        assertEquals(0L, TarUtils.parseOctal(new byte[] { '0', ' ' }, 0, 2));
    }

    public void testParseOctalParsesPaddedOctalValues() {
        assertEquals(83L, TarUtils.parseOctal(
                new byte[] { ' ', ' ', '1', '2', '3', ' ', 0 }, 0, 7));
        assertEquals(63L, TarUtils.parseOctal(
                new byte[] { '0', '0', '7', '7', 0, ' ' }, 0, 6));
        assertEquals(0L, TarUtils.parseOctal(
                new byte[] { 0, 0, 0, 0 }, 0, 4));
    }

    public void testParseOctalRejectsNonOctalBytes() {
        assertParseOctalFails(new byte[] { ' ', '7', '8', ' ', 0 }, 0, 5);
        assertParseOctalFails(new byte[] { 'a', ' ', 0 }, 0, 3);
    }

    private void assertParseOctalFails(byte[] buffer, int offset, int length) {
        try {
            TarUtils.parseOctal(buffer, offset, length);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }
}