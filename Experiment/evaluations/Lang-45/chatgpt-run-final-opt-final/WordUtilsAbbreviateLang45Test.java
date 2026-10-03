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

@org.junit.Test
public void abbreviateClampsUpperAfterClampingLowerPastEndOfString() {
    org.junit.Assert.assertEquals("0123456789abcde",
            WordUtils.abbreviate("0123456789abcde", 20, 10, "..."));
}

@org.junit.Test
public void capitalizeHandlesDefaultCustomAndEmptyDelimiters() {
    org.junit.Assert.assertEquals("I Am Fine", WordUtils.capitalize("i am fine"));
    org.junit.Assert.assertEquals("A.B c", WordUtils.capitalize("a.b c", new char[] { '.' }));
    org.junit.Assert.assertEquals("a.b c", WordUtils.capitalize("a.b c", new char[0]));
}

@org.junit.Test
public void capitalizeFullyHandlesDefaultCustomAndEmptyDelimiters() {
    org.junit.Assert.assertEquals("I Am Fine", WordUtils.capitalizeFully("i AM fINE"));
    org.junit.Assert.assertEquals("I am.Fine",
            WordUtils.capitalizeFully("i AM.fINE", new char[] { '.' }));
    org.junit.Assert.assertEquals("i AM.fINE",
            WordUtils.capitalizeFully("i AM.fINE", new char[0]));
}
}
