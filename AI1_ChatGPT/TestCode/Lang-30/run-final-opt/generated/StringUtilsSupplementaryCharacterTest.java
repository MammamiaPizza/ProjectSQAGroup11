package org.apache.commons.lang3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StringUtilsSupplementaryCharacterTest {

    private static final String PAIR_ONE = "\uD800\uDC00";
    private static final String PAIR_TWO = "\uD800\uDC01";
    private static final String BAD_HIGH_SURROGATE = "\uD800";

    @Test
    public void testContainsAnyMatchesCompleteSupplementaryPair() {
        assertTrue(StringUtils.containsAny("x" + PAIR_ONE, PAIR_ONE));
        assertTrue(StringUtils.containsAny("x" + PAIR_ONE, PAIR_ONE.toCharArray()));
        assertEquals(1, StringUtils.indexOfAny("x" + PAIR_ONE, PAIR_ONE));
        assertEquals(1, StringUtils.indexOfAny("x" + PAIR_ONE, PAIR_ONE.toCharArray()));
    }

    @Test
    public void testContainsNoneCharArrayDoesNotMatchDifferentSupplementaryPair() {
        assertTrue(StringUtils.containsNone(PAIR_ONE, PAIR_TWO.toCharArray()));
    }

    @Test
    public void testContainsNoneStringDoesNotMatchDifferentSupplementaryPair() {
        assertTrue(StringUtils.containsNone(PAIR_ONE, PAIR_TWO));
    }

    @Test
    public void testContainsAnyCharArrayDoesNotMatchMalformedSearchPair() {
        assertFalse(StringUtils.containsAny(PAIR_ONE, BAD_HIGH_SURROGATE.toCharArray()));
    }

    @Test
    public void testContainsAnyStringDoesNotMatchMalformedSearchPair() {
        assertFalse(StringUtils.containsAny(PAIR_ONE, BAD_HIGH_SURROGATE));
    }

    @Test
    public void testContainsNoneCharArrayIgnoresMalformedSearchPair() {
        assertTrue(StringUtils.containsNone(PAIR_ONE, BAD_HIGH_SURROGATE.toCharArray()));
    }

    @Test
    public void testContainsNoneStringIgnoresMalformedSearchPair() {
        assertTrue(StringUtils.containsNone(PAIR_ONE, BAD_HIGH_SURROGATE));
    }

    @Test
    public void testIndexOfAnyCharArraySkipsNonMatchingSupplementaryPair() {
        assertEquals(2, StringUtils.indexOfAny(PAIR_ONE + "a", (PAIR_TWO + "a").toCharArray()));
    }

    @Test
    public void testIndexOfAnyStringSkipsNonMatchingSupplementaryPair() {
        assertEquals(2, StringUtils.indexOfAny(PAIR_ONE + "a", PAIR_TWO + "a"));
    }

    @Test
    public void testIndexOfAnyButCharArrayReturnsStartOfUnexpectedSupplementaryPair() {
        assertEquals(2, StringUtils.indexOfAnyBut(PAIR_ONE + PAIR_TWO, PAIR_ONE.toCharArray()));
    }

    @Test
    public void testIndexOfAnyButStringReturnsStartOfUnexpectedSupplementaryPair() {
        assertEquals(2, StringUtils.indexOfAnyBut(PAIR_ONE + PAIR_TWO, PAIR_ONE));
    }

    @Test
    public void testMalformedSourceSurrogateDoesNotMatchCompleteSearchPair() {
        assertFalse(StringUtils.containsAny(BAD_HIGH_SURROGATE, PAIR_ONE));
        assertTrue(StringUtils.containsNone(BAD_HIGH_SURROGATE, PAIR_ONE));
    }
}
