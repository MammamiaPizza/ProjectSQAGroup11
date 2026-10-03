package org.jsoup.helper;

import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DataUtilBomRegressionTest {
    private static final byte[] UTF8_BOM = new byte[] {
        (byte) 0xEF, (byte) 0xBB, (byte) 0xBF
    };

    @Test
    public void loadsUtf8BomFromInputStreamWithoutIncludingBomInText() throws Exception {
        byte[] html = utf8BomHtml("BOM input stream");

        Document document = DataUtil.load(
            new ByteArrayInputStream(html), null, "http://example.com/");

        assertEquals("BOM input stream", document.body().text());
        assertEquals("UTF-8", document.outputSettings().charset().name());
    }

    @Test
    public void loadsUtf8BomFromFileWithoutIncludingBomInText() throws Exception {
        File file = File.createTempFile("jsoup-bom-", ".html");
        try {
            FileOutputStream out = new FileOutputStream(file);
            try {
                out.write(utf8BomHtml("BOM file"));
            } finally {
                out.close();
            }

            Document document = DataUtil.load(file, null, "http://example.com/");

            assertEquals("BOM file", document.body().text());
            assertEquals("UTF-8", document.outputSettings().charset().name());
        } finally {
            file.delete();
        }
    }

    @Test
    public void utf8BomOverridesConflictingExplicitCharset() throws Exception {
        Document document = DataUtil.load(
            new ByteArrayInputStream(utf8BomHtml("café")),
            "ISO-8859-1",
            "http://example.com/");

        assertEquals("café", document.body().text());
        assertEquals("UTF-8", document.outputSettings().charset().name());
    }

    @Test
    public void loadsUtf8InputWithoutBomNormally() throws Exception {
        byte[] html = "<html><body>plain café</body></html>".getBytes("UTF-8");

        Document document = DataUtil.load(
            new ByteArrayInputStream(html), null, "http://example.com/");

        assertEquals("plain café", document.body().text());
    }

    @Test
    public void detectsCharsetDeclaredInMetaWhenNoCharsetIsSupplied() throws Exception {
        byte[] html =
            "<html><head><meta charset=\"ISO-8859-1\"></head><body>café</body></html>"
                .getBytes("ISO-8859-1");

        Document document = DataUtil.load(
            new ByteArrayInputStream(html), null, "http://example.com/");

        assertEquals("café", document.body().text());
        assertEquals("ISO-8859-1", document.outputSettings().charset().name());
    }

    @Test
    public void loadsEmptyInputAsAnEmptyDocument() throws Exception {
        Document document = DataUtil.load(
            new ByteArrayInputStream(new byte[0]), null, "http://example.com/");

        assertNotNull(document);
        assertEquals("", document.body().text());
    }

    @Test
    public void readToByteBufferHonorsMaximumSize() throws Exception {
        ByteBuffer bytes = DataUtil.readToByteBuffer(
            new ByteArrayInputStream("abcdef".getBytes("US-ASCII")), 3);

        byte[] read = new byte[bytes.remaining()];
        bytes.get(read);

        assertEquals("abc", new String(read, "US-ASCII"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void readToByteBufferRejectsNegativeMaximumSize() throws Exception {
        DataUtil.readToByteBuffer(new ByteArrayInputStream(new byte[0]), -1);
    }

    private static byte[] utf8BomHtml(String bodyText) throws Exception {
        byte[] html = ("<html><body>" + bodyText + "</body></html>").getBytes("UTF-8");
        byte[] result = new byte[UTF8_BOM.length + html.length];
        System.arraycopy(UTF8_BOM, 0, result, 0, UTF8_BOM.length);
        System.arraycopy(html, 0, result, UTF8_BOM.length, html.length);
        return result;
    }
}
