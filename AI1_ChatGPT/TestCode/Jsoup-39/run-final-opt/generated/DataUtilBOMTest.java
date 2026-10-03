package org.jsoup.helper;

import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class DataUtilBOMTest {
    private static final byte[] UTF8_BOM_ONE = new byte[] {
        (byte) 0xEF, (byte) 0xBB, (byte) 0xBF, 'O', 'n', 'e'
    };

    @Test
    public void loadWithoutCharsetDiscardsUtf8BomAndRetainsText() throws Exception {
        Document document = DataUtil.load(
            new ByteArrayInputStream(UTF8_BOM_ONE), null, "http://example.com/");

        assertEquals("One", document.body().text());
    }

    @Test
    public void parseByteDataWithoutCharsetDiscardsUtf8BomAndRetainsText() {
        Document document = DataUtil.parseByteData(
            ByteBuffer.wrap(UTF8_BOM_ONE), null, "http://example.com/", Parser.htmlParser());

        assertEquals("One", document.body().text());
    }

    @Test
    public void loadWithExplicitUtf8CharsetDiscardsBomAndRetainsText() throws Exception {
        Document document = DataUtil.load(
            new ByteArrayInputStream(UTF8_BOM_ONE), "UTF-8", "http://example.com/");

        assertEquals("One", document.body().text());
    }

    @Test
    public void loadWithoutCharsetParsesOrdinaryUtf8Text() throws Exception {
        Document document = DataUtil.load(
            new ByteArrayInputStream(new byte[] { 'O', 'n', 'e' }), null, "http://example.com/");

        assertEquals("One", document.body().text());
    }
}
