package org.jsoup.helper;

import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DataUtilBug82Test {
    @Test
    public void fallsBackToUtf8WhenMetaDeclaresCharsetThatCannotEncode() throws Exception {
        String html = "<html><head><meta charset=\"ISO-2022-CN\"></head><body><p>plain text</p></body></html>";

        Document document = DataUtil.load(
            new ByteArrayInputStream(html.getBytes("UTF-8")),
            null,
            "http://example.com/",
            Parser.htmlParser());

        assertEquals("UTF-8", document.outputSettings().charset().name());
        assertEquals("UTF-8", document.select("meta[charset]").first().attr("charset"));
    }

    @Test
    public void fallsBackToUtf8WhenExplicitCharsetCannotEncode() throws Exception {
        String html = "<html><body><p>plain text</p></body></html>";

        Document document = DataUtil.load(
            new ByteArrayInputStream(html.getBytes("UTF-8")),
            "ISO-2022-CN",
            "http://example.com/",
            Parser.htmlParser());

        assertEquals("UTF-8", document.outputSettings().charset().name());
    }

    @Test
    public void preservesSupportedCharsetDeclaredInMetaAndDecodesItsContent() throws Exception {
        String html = "<html><head><meta charset=\"ISO-8859-1\"></head><body><p>caf\u00e9</p></body></html>";

        Document document = DataUtil.load(
            new ByteArrayInputStream(html.getBytes("ISO-8859-1")),
            null,
            "http://example.com/",
            Parser.htmlParser());

        assertEquals(Charset.forName("ISO-8859-1"), document.outputSettings().charset());
        assertEquals("caf\u00e9", document.select("p").text());
    }

    @Test
    public void unsupportedMetaCharsetUsesUtf8Fallback() throws Exception {
        String html = "<html><head><meta charset=\"not-a-real-charset\"></head><body><p>\u20ac</p></body></html>";

        Document document = DataUtil.load(
            new ByteArrayInputStream(html.getBytes("UTF-8")),
            null,
            "http://example.com/",
            Parser.htmlParser());

        assertEquals("UTF-8", document.outputSettings().charset().name());
        assertEquals("\u20ac", document.select("p").text());
    }

    @Test
    public void contentTypeCharsetExtractionValidatesSupportedAndInvalidCharsets() {
        assertEquals("ISO-8859-1",
            DataUtil.getCharsetFromContentType("text/html; charset=\"ISO-8859-1\""));
        assertNull(DataUtil.getCharsetFromContentType("text/html; charset=not-a-real-charset"));
        assertNull(DataUtil.getCharsetFromContentType("text/html; charset="));
    }
}