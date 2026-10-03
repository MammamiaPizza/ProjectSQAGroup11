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

@org.junit.Test
public void testRandomDefaultProducesRequestedLength() {
    org.junit.Assert.assertEquals(32, org.apache.commons.lang3.RandomStringUtils.random(32).length());
}

@org.junit.Test
public void testRandomWithStringCharactersUsesOnlySuppliedCharacters() {
    final String value = org.apache.commons.lang3.RandomStringUtils.random(40, "pq");
    org.junit.Assert.assertEquals(40, value.length());
    for (int i = 0; i < value.length(); i++) {
        org.junit.Assert.assertTrue(value.charAt(i) == 'p' || value.charAt(i) == 'q');
    }
}

@org.junit.Test
public void testRandomLowSurrogateCharacterIsPairedWithHighSurrogate() {
    final String value = org.apache.commons.lang3.RandomStringUtils.random(
            2, 0, 0, false, false, new char[] { '\uDC00' });
    org.junit.Assert.assertEquals(2, value.length());
    org.junit.Assert.assertEquals('\uDC00', value.charAt(1));
    org.junit.Assert.assertTrue(java.lang.Character.isSurrogatePair(value.charAt(0), value.charAt(1)));
}

@org.junit.Test
public void testRandomHighSurrogateCharacterIsPairedWithLowSurrogate() {
    final String value = org.apache.commons.lang3.RandomStringUtils.random(
            2, 0, 0, false, false, new char[] { '\uD800' });
    org.junit.Assert.assertEquals(2, value.length());
    org.junit.Assert.assertEquals('\uD800', value.charAt(0));
    org.junit.Assert.assertTrue(java.lang.Character.isSurrogatePair(value.charAt(0), value.charAt(1)));
}
}
