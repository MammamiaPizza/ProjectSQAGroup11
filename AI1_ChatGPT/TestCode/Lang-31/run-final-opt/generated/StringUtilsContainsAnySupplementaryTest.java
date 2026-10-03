package org.apache.commons.lang3;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StringUtilsContainsAnySupplementaryTest {

    private static final char HIGH_SURROGATE = '\uD83D';
    private static final char LOW_SURROGATE = '\uDE00';
    private static final char OTHER_LOW_SURROGATE = '\uDE01';
    private static final String SUPPLEMENTARY_CHARACTER = "\uD83D\uDE00";

    @Test
    public void containsAnyCharArrayDoesNotMatchLoneHighSurrogateFromSupplementarySearchCharacter() {
        assertFalse(StringUtils.containsAny(String.valueOf(HIGH_SURROGATE),
                new char[] { HIGH_SURROGATE, LOW_SURROGATE }));
    }

    @Test
    public void containsAnyCharArrayDoesNotMatchLoneLowSurrogateFromSupplementarySearchCharacter() {
        assertFalse(StringUtils.containsAny(String.valueOf(LOW_SURROGATE),
                new char[] { HIGH_SURROGATE, LOW_SURROGATE }));
    }

    @Test
    public void containsAnyCharSequenceDoesNotMatchLoneHighSurrogateFromSupplementarySearchCharacter() {
        assertFalse(StringUtils.containsAny(String.valueOf(HIGH_SURROGATE),
                SUPPLEMENTARY_CHARACTER));
    }

    @Test
    public void containsAnyCharSequenceDoesNotMatchLoneLowSurrogateFromSupplementarySearchCharacter() {
        assertFalse(StringUtils.containsAny(String.valueOf(LOW_SURROGATE),
                SUPPLEMENTARY_CHARACTER));
    }

    @Test
    public void containsAnyRequiresBothSurrogatesOfSupplementarySearchCharacterToMatch() {
        String mismatchedPair = new String(new char[] { HIGH_SURROGATE, OTHER_LOW_SURROGATE });

        assertFalse(StringUtils.containsAny(mismatchedPair,
                new char[] { HIGH_SURROGATE, LOW_SURROGATE }));
        assertFalse(StringUtils.containsAny(mismatchedPair, SUPPLEMENTARY_CHARACTER));
    }

    @Test
    public void containsAnyMatchesCompleteSupplementaryCharacter() {
        assertTrue(StringUtils.containsAny("prefix" + SUPPLEMENTARY_CHARACTER + "suffix",
                new char[] { HIGH_SURROGATE, LOW_SURROGATE }));
        assertTrue(StringUtils.containsAny("prefix" + SUPPLEMENTARY_CHARACTER + "suffix",
                SUPPLEMENTARY_CHARACTER));
    }

    @Test
    public void containsAnyStillMatchesOrdinaryCharacters() {
        assertTrue(StringUtils.containsAny("abc", new char[] { 'x', 'b' }));
        assertTrue(StringUtils.containsAny("abc", "xb"));
    }

    @Test
    public void containsAnyReturnsFalseForNullOrEmptyInputs() {
        assertFalse(StringUtils.containsAny((CharSequence) null,
                new char[] { HIGH_SURROGATE, LOW_SURROGATE }));
        assertFalse(StringUtils.containsAny("", new char[] { HIGH_SURROGATE, LOW_SURROGATE }));
        assertFalse(StringUtils.containsAny("text", new char[0]));
        assertFalse(StringUtils.containsAny((CharSequence) null, SUPPLEMENTARY_CHARACTER));
        assertFalse(StringUtils.containsAny("", SUPPLEMENTARY_CHARACTER));
        assertFalse(StringUtils.containsAny("text", ""));
    }
}
