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
        assertEquals("ISO-8859-1",
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
}