package org.jsoup.helper;

import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DataUtilBOMRegressionTest {
    private static final String BASE_URI = "http://example.com/";

    @Test
    public void parseByteDataDiscardsUtf8BomWhenCharsetIsInferred() throws Exception {
        String html = "<html><head></head><body>One</body></html>";
        ByteBuffer bytes = ByteBuffer.wrap(withUtf8Bom(html));

        Document document = DataUtil.parseByteData(bytes, null, BASE_URI, Parser.htmlParser());

        assertEquals("One", document.body().text());
    }

    @Test
    public void loadDiscardsUtf8BomWhenCharsetIsExplicitlySpecified() throws Exception {
        String html = "<html><head></head><body>One</body></html>";

        Document document = DataUtil.load(
            new ByteArrayInputStream(withUtf8Bom(html)),
            "UTF-8",
            BASE_URI
        );

        assertEquals("One", document.body().text());
    }

    @Test
    public void loadParsesNormalUtf8HtmlWhenCharsetIsInferred() throws Exception {
        String html = "<html><head></head><body>Normal content</body></html>";

        Document document = DataUtil.load(
            new ByteArrayInputStream(html.getBytes("UTF-8")),
            null,
            BASE_URI
        );

        assertEquals("Normal content", document.body().text());
    }

    @Test
    public void loadRedecodesDocumentUsingCharsetDeclaredInMetaTag() throws Exception {
        String html = "<html><head><meta charset=\"ISO-8859-1\"></head><body>caf\u00e9</body></html>";

        Document document = DataUtil.load(
            new ByteArrayInputStream(html.getBytes("ISO-8859-1")),
            null,
            BASE_URI
        );

        assertEquals("café", document.body().text());
    }

    @Test(expected = IllegalArgumentException.class)
    public void loadRejectsEmptyExplicitCharsetName() throws Exception {
        DataUtil.load(
            new ByteArrayInputStream("<html><body>One</body></html>".getBytes("UTF-8")),
            "",
            BASE_URI
        );
    }

    @Test
    public void getCharsetFromContentTypeHandlesQuotedCharsetAndMissingCharset() {
        assertEquals("EUC-JP",
            DataUtil.getCharsetFromContentType("text/html; boundary=x; charset=\"euc-jp\""));
        assertNull(DataUtil.getCharsetFromContentType("text/html; boundary=x"));
    }

    private static byte[] withUtf8Bom(String value) throws Exception {
        byte[] content = value.getBytes("UTF-8");
        byte[] bytes = new byte[content.length + 3];
        bytes[0] = (byte) 0xEF;
        bytes[1] = (byte) 0xBB;
        bytes[2] = (byte) 0xBF;
        System.arraycopy(content, 0, bytes, 3, content.length);
        return bytes;
    }
}
