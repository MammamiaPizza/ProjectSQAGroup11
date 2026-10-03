package org.jsoup.helper;

import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DataUtilCharsetHandlingTest {

    @Test
    public void extractsAndNormalizesLowerCaseCharset() {
        assertEquals("iso-8859-1",
                DataUtil.getCharsetFromContentType("text/html; charset=iso-8859-1"));
    }

    @Test
    public void removesDuplicateCharsetPrefix() {
        assertEquals("ISO-8859-1",
                DataUtil.getCharsetFromContentType("text/html; charset=charset=ISO-8859-1"));
    }

    @Test
    public void acceptsSingleAndDoubleQuotedCharsets() {
        assertEquals("UTF-8",
                DataUtil.getCharsetFromContentType("text/html; charset='UTF-8'"));
        assertEquals("UTF-8",
                DataUtil.getCharsetFromContentType("text/html; charset=\"UTF-8\""));
    }

    @Test
    public void returnsNullForEmptyCharset() {
        assertNull(DataUtil.getCharsetFromContentType("text/html; charset="));
    }

    @Test
    public void selectsFirstCharsetWhenCharsetsAreCommaSeparated() {
        assertEquals("ISO-8859-1",
                DataUtil.getCharsetFromContentType(
                        "text/html; charset=ISO-8859-1, charset=UTF-8"));
    }

    @Test
    public void returnsNullForIllegalCharsetName() {
        assertNull(DataUtil.getCharsetFromContentType("text/html; charset=$HJKDF§$/("));
    }

    @Test
    public void returnsNullWhenNoCharsetIsPresent() {
        assertNull(DataUtil.getCharsetFromContentType("text/html; boundary=something"));
        assertNull(DataUtil.getCharsetFromContentType(null));
    }

    @Test
    public void parsesHtmlWithMalformedHtml5CharsetWithoutThrowing() {
        String html = "<html><head><meta charset=UTF-8\"><title>Works</title></head>"
                + "<body>café</body></html>";

        Document document = DataUtil.parseByteData(
                ByteBuffer.wrap(html.getBytes(Charset.forName("UTF-8"))),
                null,
                "http://example.com/",
                Parser.htmlParser());

        assertEquals("Works", document.title());
        assertEquals("café", document.body().text());
    }

    @Test
    public void reparsesUsingCharsetFoundInHttpEquivMetaTag() {
        String html = "<html><head>"
                + "<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\">"
                + "</head><body>café</body></html>";

        Document document = DataUtil.parseByteData(
                ByteBuffer.wrap(html.getBytes(Charset.forName("ISO-8859-1"))),
                null,
                "http://example.com/",
                Parser.htmlParser());

        assertEquals("café", document.body().text());
    }

@Test
public void loadsHtmlFromInputStreamWithProvidedCharset() throws java.io.IOException {
    String html = "<html><head><title>Stream title</title></head><body>café</body></html>";
    org.jsoup.nodes.Document document = DataUtil.load(
        new java.io.ByteArrayInputStream(html.getBytes("UTF-8")),
        "UTF-8",
        "http://example.com/");

    org.junit.Assert.assertEquals("Stream title", document.title());
    org.junit.Assert.assertEquals("café", document.body().text());
}

@Test
public void loadsHtmlFromFileAndClosesItsInputStream() throws java.io.IOException {
    java.io.File file = java.io.File.createTempFile("jsoup-datautil-", ".html");
    try {
        java.io.FileOutputStream output = new java.io.FileOutputStream(file);
        try {
            output.write("<html><head><title>File title</title></head><body>File body</body></html>".getBytes("UTF-8"));
        } finally {
            output.close();
        }

        org.jsoup.nodes.Document document = DataUtil.load(file, "UTF-8", "http://example.com/");
        org.junit.Assert.assertEquals("File title", document.title());
        org.junit.Assert.assertEquals("File body", document.body().text());
    } finally {
        file.delete();
    }
}

@Test
public void readsStreamsRespectingAndIgnoringMaximumSize() throws java.io.IOException {
    byte[] bytes = "abcde".getBytes("UTF-8");

    java.nio.ByteBuffer capped = DataUtil.readToByteBuffer(new java.io.ByteArrayInputStream(bytes), 3);
    org.junit.Assert.assertEquals(3, capped.remaining());
    org.junit.Assert.assertEquals("abc", new java.lang.String(capped.array(), "UTF-8"));

    java.nio.ByteBuffer unlimited = DataUtil.readToByteBuffer(new java.io.ByteArrayInputStream(bytes), 0);
    org.junit.Assert.assertEquals(5, unlimited.remaining());
    org.junit.Assert.assertEquals("abcde", new java.lang.String(unlimited.array(), "UTF-8"));
}
}
