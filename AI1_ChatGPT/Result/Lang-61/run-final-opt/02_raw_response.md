package org.apache.commons.lang.text;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StrBuilderLang294RegressionTest {

    @Test
    public void testIndexOfDoesNotFindRemovedSuffixInUnusedBuffer() {
        StrBuilder builder = new StrBuilder("onetwothree");

        builder.delete(6, 11);

        assertEquals("onetwo", builder.toString());
        assertEquals(-1, builder.indexOf("three"));
    }

    @Test
    public void testIndexOfFromLogicalEndDoesNotSearchUnusedBuffer() {
        StrBuilder builder = new StrBuilder("onetwothree");

        builder.delete(6, 11);

        assertEquals(-1, builder.indexOf("three", builder.length()));
    }

    @Test
    public void testIndexOfFindsSuffixBeforeItIsRemoved() {
        StrBuilder builder = new StrBuilder("onetwothree");

        assertEquals(6, builder.indexOf("three", 0));
    }

    @Test
    public void testReplaceAllDeletingTerminalMatchDoesNotRescanUnusedBuffer() {
        StrBuilder builder = new StrBuilder("onetwothree");

        builder.replaceAll("three", "");

        assertEquals("onetwo", builder.toString());
    }

    @Test
    public void testReplaceAllDeletesMultipleMatchesIncludingTerminalMatch() {
        StrBuilder builder = new StrBuilder("threeXthree");

        builder.replaceAll("three", "");

        assertEquals("X", builder.toString());
    }

    @Test
    public void testReplaceAllWithNoMatchLeavesContentUnchanged() {
        StrBuilder builder = new StrBuilder("onetwothree");

        builder.replaceAll("four", "");

        assertEquals("onetwothree", builder.toString());
    }
}