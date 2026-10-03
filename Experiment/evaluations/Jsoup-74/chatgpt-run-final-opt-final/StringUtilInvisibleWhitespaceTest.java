package org.jsoup.helper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringUtilInvisibleWhitespaceTest {
    @Test
    public void normaliseWhitespacePreservesVisibleTextAndCollapsesWhitespace() {
        assertEquals(" alpha beta gamma ", StringUtil.normaliseWhitespace(" \talpha\nbeta\f\r gamma "));
    }

    @Test
    public void normaliseWhitespaceRemovesInvisibleCharactersEmbeddedInWords() {
        String text = "This\u00ADis\u200Bone\u200Clong\u200Dword";

        assertEquals("Thisisonelongword", StringUtil.normaliseWhitespace(text));
    }

    @Test
    public void appendNormalisedWhitespaceStripsLeadingWhitespaceAfterLeadingInvisibles() {
        StringBuilder builder = new StringBuilder("prefix");

        StringUtil.appendNormalisedWhitespace(builder, "\u200B\u00AD \talpha\nbeta", true);

        assertEquals("prefixalpha beta", builder.toString());
    }

    @Test
    public void appendNormalisedWhitespaceKeepsOneLeadingSpaceWhenNotStripping() {
        StringBuilder builder = new StringBuilder("prefix");

        StringUtil.appendNormalisedWhitespace(builder, "\u200C \talpha\u200Dbeta", false);

        assertEquals("prefix alphabeta", builder.toString());
    }

@org.junit.Test
public void normaliseWhitespaceIgnoresInvisiblesWithoutCreatingExtraSpaces() {
    String text = "\u200B \u00ADalpha\u200C\tbeta\u200D ";
    org.junit.Assert.assertEquals(" alpha beta ", org.jsoup.helper.StringUtil.normaliseWhitespace(text));
}

@org.junit.Test
public void blankNumericAndWhitespacePredicatesHandleEmptyWhitespaceAndText() {
    org.junit.Assert.assertTrue(org.jsoup.helper.StringUtil.isBlank(null));
    org.junit.Assert.assertTrue(org.jsoup.helper.StringUtil.isBlank("\t\n\r\f "));
    org.junit.Assert.assertFalse(org.jsoup.helper.StringUtil.isBlank(" text "));

    org.junit.Assert.assertFalse(org.jsoup.helper.StringUtil.isNumeric(null));
    org.junit.Assert.assertFalse(org.jsoup.helper.StringUtil.isNumeric(""));
    org.junit.Assert.assertTrue(org.jsoup.helper.StringUtil.isNumeric("012345"));
    org.junit.Assert.assertFalse(org.jsoup.helper.StringUtil.isNumeric("12a"));

    org.junit.Assert.assertTrue(org.jsoup.helper.StringUtil.isWhitespace('\n'));
    org.junit.Assert.assertFalse(org.jsoup.helper.StringUtil.isWhitespace('\u00A0'));
    org.junit.Assert.assertTrue(org.jsoup.helper.StringUtil.isActuallyWhitespace('\u00A0'));
}

@org.junit.Test
public void joinSupportsCollectionsArraysAndEmptyIterators() {
    org.junit.Assert.assertEquals("one|two|three",
        org.jsoup.helper.StringUtil.join(java.util.Arrays.asList("one", "two", "three"), "|"));
    org.junit.Assert.assertEquals("one,two",
        org.jsoup.helper.StringUtil.join(new String[] {"one", "two"}, ","));
    org.junit.Assert.assertEquals("",
        org.jsoup.helper.StringUtil.join(java.util.Collections.emptyList().iterator(), ","));
}

@org.junit.Test
public void paddingReturnsRequestedNumberOfSpaces() {
    org.junit.Assert.assertEquals("", org.jsoup.helper.StringUtil.padding(0));
    org.junit.Assert.assertEquals("   ", org.jsoup.helper.StringUtil.padding(3));
    org.junit.Assert.assertEquals(22, org.jsoup.helper.StringUtil.padding(22).length());
    org.junit.Assert.assertEquals("", org.jsoup.helper.StringUtil.padding(22).trim());
}
}
