package org.jsoup.helper;

import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

public class DataUtilXmlCharsetTest {
    @Test
    public void detectsQuotedXmlEncodingDeclarationWhenCharsetIsNotSpecified() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><root>Hellö Wörld!</root>";
        ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes("ISO-8859-1"));

        Document document = DataUtil.load(input, null, "http://example.com/", Parser.xmlParser());

        assertEquals("Hellö Wörld!", document.text());
    }

    @Test
    public void explicitCharsetTakesPrecedenceOverXmlDeclaration() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><root>Hellö Wörld!</root>";
        ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes("UTF-8"));

        Document document = DataUtil.load(input, "UTF-8", "http://example.com/", Parser.xmlParser());

        assertEquals("Hellö Wörld!", document.text());
    }

    @Test
    public void utf8BomTakesPrecedenceOverXmlDeclaration() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><root>Hellö Wörld!</root>";
        byte[] body = xml.getBytes("UTF-8");
        byte[] withBom = new byte[body.length + 3];
        withBom[0] = (byte) 0xEF;
        withBom[1] = (byte) 0xBB;
        withBom[2] = (byte) 0xBF;
        System.arraycopy(body, 0, withBom, 3, body.length);

        Document document = DataUtil.load(
            new ByteArrayInputStream(withBom), null, "http://example.com/", Parser.xmlParser());

        assertEquals("Hellö Wörld!", document.text());
    }

    @Test
    public void unsupportedXmlEncodingDeclarationFallsBackToUtf8() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"X-Unsupported-Charset\"?><root>Hellö Wörld!</root>";
        ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes(Charset.forName("UTF-8")));

        Document document = DataUtil.load(input, null, "http://example.com/", Parser.xmlParser());

        assertEquals("Hellö Wörld!", document.text());
    }

@org.junit.Test
public void crossStreamsCopiesAllBytes() throws java.io.IOException {
    byte[] source = new byte[] {0, 1, 2, 3, 4};
    java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();

    DataUtil.crossStreams(new java.io.ByteArrayInputStream(source), output);

    org.junit.Assert.assertArrayEquals(source, output.toByteArray());
}

@org.junit.Test
public void getsCharsetFromContentTypeAndRejectsUnsupportedCharsets() {
    org.junit.Assert.assertEquals("ISO-8859-1",
        DataUtil.getCharsetFromContentType("text/html; charset=\"ISO-8859-1\""));
    org.junit.Assert.assertNull(DataUtil.getCharsetFromContentType("text/html; charset=not-a-real-charset"));
    org.junit.Assert.assertNull(DataUtil.getCharsetFromContentType("text/html"));
}

@org.junit.Test
public void loadsEmptyDocumentWhenInputStreamIsNull() throws java.io.IOException {
    org.jsoup.nodes.Document document = DataUtil.load((java.io.InputStream) null, null, "http://example.com/");

    org.junit.Assert.assertNotNull(document);
}

@org.junit.Test
public void readsInputStreamIntoByteBuffer() throws java.io.IOException {
    byte[] source = new byte[] {10, 20, 30};
    java.nio.ByteBuffer buffer = DataUtil.readToByteBuffer(new java.io.ByteArrayInputStream(source), 32);
    byte[] actual = new byte[buffer.remaining()];
    buffer.get(actual);

    org.junit.Assert.assertArrayEquals(source, actual);
}
}
