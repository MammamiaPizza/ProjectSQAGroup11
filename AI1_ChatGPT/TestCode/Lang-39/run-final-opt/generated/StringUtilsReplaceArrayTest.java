package org.apache.commons.lang3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class StringUtilsReplaceArrayTest {

    @Test
    public void replacesMultipleSearchStringsAcrossTheInput() {
        assertEquals("X-Y-X", StringUtils.replace("ab-cd-ab",
                new String[] { "ab", "cd" },
                new String[] { "X", "Y" }));
    }

    @Test
    public void replacesEveryOccurrenceWithoutReprocessingReplacementText() {
        assertEquals("bc", StringUtils.replace("ab",
                new String[] { "a", "b" },
                new String[] { "b", "c" }));
    }

    @Test
    public void choosesTheEarliestMatchWhenSearchStringsOverlap() {
        assertEquals("XXY", StringUtils.replace("ababa",
                new String[] { "ab", "a" },
                new String[] { "X", "Y" }));
    }

    @Test
    public void supportsEmptyReplacementStringsForDeletion() {
        assertEquals("bc", StringUtils.replace("abc",
                new String[] { "a" },
                new String[] { "" }));
    }

    @Test
    public void returnsNullWhenTheInputTextIsNull() {
        assertNull(StringUtils.replace(null,
                new String[] { "a" },
                new String[] { "b" }));
    }

    @Test
    public void returnsOriginalTextForNullOrEmptyReplacementArrays() {
        String text = "abc";

        assertSame(text, StringUtils.replace(text, null, null));
        assertSame(text, StringUtils.replace(text, new String[0], new String[0]));
    }

    @Test
    public void ignoresNullAndEmptySearchEntriesAndNullReplacementEntries() {
        assertEquals("xbc", StringUtils.replace("abc",
                new String[] { "a", null, "", "c" },
                new String[] { "x", "ignored", "ignored", null }));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsSearchAndReplacementArraysOfDifferentLengths() {
        StringUtils.replace("abc",
                new String[] { "a", "b" },
                new String[] { "x" });
    }
}
