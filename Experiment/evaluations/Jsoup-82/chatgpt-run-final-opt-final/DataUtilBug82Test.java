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

@org.junit.Test
public void crossStreamsCopiesAllBytes() throws java.io.IOException {
    byte[] source = "copy this content".getBytes("UTF-8");
    java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();

    DataUtil.crossStreams(new java.io.ByteArrayInputStream(source), output);

    org.junit.Assert.assertArrayEquals(source, output.toByteArray());
}

@org.junit.Test
public void loadDetectsUtf8AndUtf16ByteOrderMarks() throws java.io.IOException {
    byte[] utf8Html = "<p>€</p>".getBytes("UTF-8");
    byte[] utf8WithBom = new byte[utf8Html.length + 3];
    utf8WithBom[0] = (byte) 0xEF;
    utf8WithBom[1] = (byte) 0xBB;
    utf8WithBom[2] = (byte) 0xBF;
    System.arraycopy(utf8Html, 0, utf8WithBom, 3, utf8Html.length);

    org.jsoup.nodes.Document utf8Document = DataUtil.load(
        new java.io.ByteArrayInputStream(utf8WithBom), null, "");
    org.junit.Assert.assertEquals("€", utf8Document.select("p").text());
    org.junit.Assert.assertEquals("UTF-8", utf8Document.outputSettings().charset().name());

    byte[] utf16Html = "<p>é</p>".getBytes("UTF-16");
    org.jsoup.nodes.Document utf16Document = DataUtil.load(
        new java.io.ByteArrayInputStream(utf16Html), null, "");
    org.junit.Assert.assertEquals("é", utf16Document.select("p").text());
    org.junit.Assert.assertEquals("UTF-16", utf16Document.outputSettings().charset().name());
}

@org.junit.Test
public void readToByteBufferHonorsMaximumSize() throws java.io.IOException {
    java.nio.ByteBuffer buffer = DataUtil.readToByteBuffer(
        new java.io.ByteArrayInputStream("abcdef".getBytes("UTF-8")), 4);

    org.junit.Assert.assertEquals("abcd", new String(buffer.array(), "UTF-8"));
}

@org.junit.Test
public void loadFileParsesContentUsingProvidedCharset() throws java.io.IOException {
    java.io.File file = java.io.File.createTempFile("jsoup-datautil-", ".html");
    try {
        java.io.FileOutputStream output = new java.io.FileOutputStream(file);
        try {
            output.write("<p>file content</p>".getBytes("UTF-8"));
        } finally {
            output.close();
        }

        org.jsoup.nodes.Document document = DataUtil.load(file, "UTF-8", "http://example.com/");
        org.junit.Assert.assertEquals("file content", document.select("p").text());
    } finally {
        file.delete();
    }
}
}
