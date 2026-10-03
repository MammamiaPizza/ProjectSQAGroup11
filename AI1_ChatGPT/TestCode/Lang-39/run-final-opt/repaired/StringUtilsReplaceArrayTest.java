package org.apache.commons.lang3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class StringUtilsReplaceArrayTest {

    @Test
    public void replacesMultipleSearchStringsAcrossTheInput() {
        assertEquals("X-Y-X", StringUtils.replaceEach("ab-cd-ab",
                new String[] { "ab", "cd" },
                new String[] { "X", "Y" }));
    }

    @Test
    public void replacesEveryOccurrenceWithoutReprocessingReplacementText() {
        assertEquals("bc", StringUtils.replaceEach("ab",
                new String[] { "a", "b" },
                new String[] { "b", "c" }));
    }

    @Test
    public void choosesTheEarliestMatchWhenSearchStringsOverlap() {
        assertEquals("XXY", StringUtils.replaceEach("ababa",
                new String[] { "ab", "a" },
                new String[] { "X", "Y" }));
    }

    @Test
    public void supportsEmptyReplacementStringsForDeletion() {
        assertEquals("bc", StringUtils.replaceEach("abc",
                new String[] { "a" },
                new String[] { "" }));
    }

    @Test
    public void returnsNullWhenTheInputTextIsNull() {
        assertNull(StringUtils.replaceEach(null,
                new String[] { "a" },
                new String[] { "b" }));
    }

    @Test
    public void returnsOriginalTextForNullOrEmptyReplacementArrays() {
        String text = "abc";

        assertSame(text, StringUtils.replaceEach(text, null, null));
        assertSame(text, StringUtils.replaceEach(text, new String[0], new String[0]));
    }

    @Test
    public void ignoresNullAndEmptySearchEntriesAndNullReplacementEntries() {
        assertEquals("xbc", StringUtils.replaceEach("abc",
                new String[] { "a", null, "", "c" },
                new String[] { "x", "ignored", "ignored", null }));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsSearchAndReplacementArraysOfDifferentLengths() {
        StringUtils.replaceEach("abc",
                new String[] { "a", "b" },
                new String[] { "x" });
    }
}
