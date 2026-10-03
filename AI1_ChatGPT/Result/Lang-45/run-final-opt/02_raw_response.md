package org.apache.commons.lang;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WordUtilsAbbreviateLang45Test {

    @Test
    public void abbreviateReturnsNullForNullInput() {
        assertEquals(null, WordUtils.abbreviate(null, 0, 5, "..."));
    }

    @Test
    public void abbreviateReturnsEmptyStringForEmptyInput() {
        assertEquals("", WordUtils.abbreviate("", 0, 5, "..."));
    }

    @Test
    public void abbreviateDoesNotAppendSuffixWhenUpperAllowsEntireString() {
        assertEquals("0123456789", WordUtils.abbreviate("0123456789", 0, -1, "..."));
    }

    @Test
    public void abbreviateUsesWordBoundaryFoundAfterLowerLimit() {
        assertEquals("012 345...", WordUtils.abbreviate("012 345 6789", 5, 10, "..."));
    }

    @Test
    public void abbreviateUsesUpperLimitWhenNextWordBoundaryIsBeyondIt() {
        assertEquals("012...", WordUtils.abbreviate("012345 789", 0, 3, "..."));
    }

    @Test
    public void abbreviateTruncatesSingleWordAtUpperLimit() {
        assertEquals("01234...", WordUtils.abbreviate("0123456789", 0, 5, "..."));
    }

    @Test
    public void abbreviateUsesNoSuffixWhenAppendToEndIsNull() {
        assertEquals("01234", WordUtils.abbreviate("0123456789", 0, 5, null));
    }

    @Test
    public void abbreviateClampsLowerPastEndOfString() {
        assertEquals("0123456789abcde",
                WordUtils.abbreviate("0123456789abcde", 16, -1, "..."));
    }

    @Test
    public void abbreviateAcceptsLowerAtEndOfString() {
        assertEquals("0123456789abcde",
                WordUtils.abbreviate("0123456789abcde", 15, -1, "..."));
    }
}