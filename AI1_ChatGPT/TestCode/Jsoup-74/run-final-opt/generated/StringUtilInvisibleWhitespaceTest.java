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
}
