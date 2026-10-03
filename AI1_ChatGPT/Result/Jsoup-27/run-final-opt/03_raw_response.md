package org.jsoup.helper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DataUtilCharsetNormalizationTest {
    @Test
    public void returnsUnquotedUppercaseCharset() {
        assertEquals("UTF-8", DataUtil.getCharsetFromContentType("text/html; charset=UTF-8"));
    }

    @Test
    public void returnsQuotedUppercaseCharset() {
        assertEquals("UTF-8", DataUtil.getCharsetFromContentType("text/html; charset=\"UTF-8\""));
    }

    @Test
    public void matchesCharsetParameterCaseInsensitively() {
        assertEquals("ISO-8859-1", DataUtil.getCharsetFromContentType("text/html; CHARSET=ISO-8859-1"));
    }
}