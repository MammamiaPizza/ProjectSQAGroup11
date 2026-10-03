package org.jsoup.helper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DataUtilCharsetNormalizationTest {
    @Test
    public void normalizesUnquotedUppercaseCharsetToLowercase() {
        assertEquals("utf-8", DataUtil.getCharsetFromContentType("text/html; charset=UTF-8"));
    }

    @Test
    public void normalizesQuotedUppercaseCharsetToLowercase() {
        assertEquals("utf-8", DataUtil.getCharsetFromContentType("text/html; charset=\"UTF-8\""));
    }

    @Test
    public void matchesCharsetParameterCaseInsensitivelyAndNormalizesValue() {
        assertEquals("iso-8859-1", DataUtil.getCharsetFromContentType("text/html; CHARSET=ISO-8859-1"));
    }
}