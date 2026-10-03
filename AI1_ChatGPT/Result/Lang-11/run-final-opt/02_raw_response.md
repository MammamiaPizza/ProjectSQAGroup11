package org.apache.commons.lang3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class RandomStringUtilsLANG807Test {

    @Test
    public void testRandomRejectsEqualStartAndEndWithStartInMessage() {
        assertInvalidRangeMentionsStart(4, 7, 7);
    }

    @Test
    public void testRandomRejectsStartGreaterThanEndWithStartInMessage() {
        assertInvalidRangeMentionsStart(4, 9, 3);
    }

    @Test
    public void testZeroCountReturnsEmptyStringBeforeRangeIsUsed() {
        assertEquals("", RandomStringUtils.random(0, 9, 3, false, false));
    }

    @Test
    public void testRandomWithinExplicitCharacterRangeHasRequestedLengthAndBounds() {
        final String value = RandomStringUtils.random(80, 'A', 'Z' + 1, false, false);

        assertEquals(80, value.length());
        for (int i = 0; i < value.length(); i++) {
            assertTrue(value.charAt(i) >= 'A');
            assertTrue(value.charAt(i) <= 'Z');
        }
    }

    @Test
    public void testRandomWithCharacterArrayUsesOnlySuppliedCharacters() {
        final String value = RandomStringUtils.random(80, new char[] { 'x', 'y' });

        assertEquals(80, value.length());
        for (int i = 0; i < value.length(); i++) {
            assertTrue(value.charAt(i) == 'x' || value.charAt(i) == 'y');
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRandomRejectsNegativeCount() {
        RandomStringUtils.random(-1, false, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRandomRejectsEmptyCharacterArrayForPositiveCount() {
        RandomStringUtils.random(1, new char[0]);
    }

    private void assertInvalidRangeMentionsStart(final int count, final int start, final int end) {
        try {
            RandomStringUtils.random(count, start, end, false, false);
            fail("Expected IllegalArgumentException for invalid range");
        } catch (final IllegalArgumentException ex) {
            assertNotNull(ex.getMessage());
            assertTrue("Exception message should mention start: " + ex.getMessage(),
                    ex.getMessage().contains("start"));
        }
    }
}