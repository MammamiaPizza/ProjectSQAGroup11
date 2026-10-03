package org.jsoup.helper;

import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DataUtilTest {

    @Test
    public void loadsEmptyStreamWithoutThrowingWhenCharsetIsDetected() throws IOException {
        Document document = DataUtil.load(
            new ByteArrayInputStream(new byte[0]),
            null,
            "http://example.com/empty"
        );

        assertNotNull(document);
        assertEquals("http://example.com/empty", document.location());
    }

    @Test
    public void loadsEmptyStreamWithoutThrowingWhenCharsetIsSpecified() throws IOException {
        Document document = DataUtil.load(
            new ByteArrayInputStream(new byte[0]),
            "UTF-8",
            "http://example.com/empty"
        );

        assertNotNull(document);
        assertEquals("http://example.com/empty", document.location());
    }

    @Test
    public void loadsEmptyStreamWithAlternateParser() throws IOException {
        Document document = DataUtil.load(
            new ByteArrayInputStream(new byte[0]),
            null,
            "http://example.com/xml",
            Parser.xmlParser()
        );

        assertNotNull(document);
        assertEquals("http://example.com/xml", document.location());
    }

    @Test
    public void readsEmptyStreamToEmptyByteBuffer() throws IOException {
        ByteBuffer buffer = DataUtil.readToByteBuffer(new ByteArrayInputStream(new byte[0]), 0);

        assertNotNull(buffer);
        assertEquals(0, buffer.remaining());
    }

    @Test
    public void readsNonEmptyStreamToByteBuffer() throws IOException {
        byte[] expected = "jsoup".getBytes(StandardCharsets.UTF_8);

        ByteBuffer buffer = DataUtil.readToByteBuffer(new ByteArrayInputStream(expected), 0);
        byte[] actual = new byte[buffer.remaining()];
        buffer.get(actual);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void respectsMaximumByteBufferSize() throws IOException {
        ByteBuffer buffer = DataUtil.readToByteBuffer(
            new ByteArrayInputStream("abcdef".getBytes(StandardCharsets.UTF_8)),
            3
        );
        byte[] actual = new byte[buffer.remaining()];
        buffer.get(actual);

        assertArrayEquals("abc".getBytes(StandardCharsets.UTF_8), actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNegativeMaximumByteBufferSize() throws IOException {
        DataUtil.readToByteBuffer(new ByteArrayInputStream(new byte[0]), -1);
    }

    @Test
    public void loadsNonEmptyHtmlNormally() throws IOException {
        Document document = DataUtil.load(
            new ByteArrayInputStream("<html><head><title>Title</title></head><body>Body</body></html>"
                .getBytes(StandardCharsets.UTF_8)),
            null,
            "http://example.com/page"
        );

        assertEquals("Title", document.title());
        assertEquals("Body", document.body().text());
    }

    @Test
    public void treatsNullInputAsEmptyDocument() throws IOException {
        Document document = DataUtil.load((java.io.InputStream) null, null, "http://example.com/null");

        assertNotNull(document);
        assertEquals("http://example.com/null", document.location());
    }
}