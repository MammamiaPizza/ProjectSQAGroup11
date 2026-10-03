package org.jsoup.helper;

 import org.jsoup.nodes.Document;
 import org.jsoup.parser.Parser;
 import org.junit.Test;

 import java.io.ByteArrayInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.nio.ByteBuffer;
 import java.nio.charset.StandardCharsets;

 import static org.junit.Assert.*;

 public class DataUtilTest {

     @Test
     public void readToByteBuffer_emptyStream_returnsEmptyBuffer() throws IOException {
         InputStream empty = new ByteArrayInputStream(new byte[0]);
         ByteBuffer result = DataUtil.readToByteBuffer(empty, 1024);
         assertNotNull(result);
         assertEquals(0, result.remaining());
     }

     @Test
     public void readToByteBuffer_emptyStream_unlimited() throws IOException {
         InputStream empty = new ByteArrayInputStream(new byte[0]);
         ByteBuffer result = DataUtil.readToByteBuffer(empty, 0);
         assertNotNull(result);
         assertEquals(0, result.remaining());
     }

     @Test
     public void readToByteBuffer_emptyStream_maxSizeOne() throws IOException {
         InputStream empty = new ByteArrayInputStream(new byte[0]);
         ByteBuffer result = DataUtil.readToByteBuffer(empty, 1);
         assertNotNull(result);
         assertEquals(0, result.remaining());
     }

     @Test
     public void readToByteBuffer_nonEmptyStream() throws IOException {
         byte[] data = "hello world".getBytes(StandardCharsets.UTF_8);
         InputStream in = new ByteArrayInputStream(data);
         ByteBuffer result = DataUtil.readToByteBuffer(in, 1024);
         assertNotNull(result);
         assertEquals(data.length, result.remaining());
         byte[] output = new byte[result.remaining()];
         result.get(output);
         assertArrayEquals(data, output);
     }

     @Test
     public void readToByteBuffer_nonEmptyStream_maxSizeLessThanData() throws IOException {
         byte[] data = "hello world".getBytes(StandardCharsets.UTF_8);
         InputStream in = new ByteArrayInputStream(data);
         ByteBuffer result = DataUtil.readToByteBuffer(in, 5);
         assertNotNull(result);
         assertEquals(5, result.remaining());
     }

     @Test
     public void load_emptyStream_withCharset_returnsEmptyDocument() throws IOException {
         InputStream empty = new ByteArrayInputStream(new byte[0]);
         Document doc = DataUtil.load(empty, "UTF-8", "http://example.com");
         assertNotNull(doc);
         assertEquals("http://example.com", doc.baseUri());
     }

     @Test
     public void load_emptyStream_nullCharset_returnsEmptyDocument() throws IOException {
         InputStream empty = new ByteArrayInputStream(new byte[0]);
         Document doc = DataUtil.load(empty, null, "http://example.com");
         assertNotNull(doc);
     }

     @Test
     public void load_emptyStream_htmlParser_returnsEmptyDocument() throws IOException {
         InputStream empty = new ByteArrayInputStream(new byte[0]);
         Document doc = DataUtil.load(empty, "UTF-8", "http://example.com", Parser.htmlParser());
         assertNotNull(doc);
     }

     @Test
     public void load_emptyStream_xmlParser_returnsEmptyDocument() throws IOException {
         InputStream empty = new ByteArrayInputStream(new byte[0]);
         Document doc = DataUtil.load(empty, "UTF-8", "http://example.com", Parser.xmlParser());
         assertNotNull(doc);
     }

     @Test
     public void load_nonEmptyStream_parsesCorrectly() throws IOException {
         String html = "<html><head><title>Test</title></head><body><p>Hello</p></body></html>";
         InputStream in = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));
         Document doc = DataUtil.load(in, "UTF-8", "http://example.com");
         assertNotNull(doc);
         assertEquals("Test", doc.title());
     }

     @Test(expected = IllegalArgumentException.class)
     public void readToByteBuffer_negativeMaxSize_throwsException() throws IOException {
         InputStream in = new ByteArrayInputStream(new byte[0]);
         DataUtil.readToByteBuffer(in, -1);
     }

     @Test
     public void load_nullInputStream_returnsDocument() throws IOException {
         Document doc = DataUtil.load((InputStream) null, "UTF-8", "http://example.com");
         assertNotNull(doc);
         assertEquals("http://example.com", doc.baseUri());
     }

@Test
    public void crossStreams_nonEmptyStream_transfersData() throws java.io.IOException {
        byte[] data = "hello world".getBytes("UTF-8");
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        DataUtil.crossStreams(in, out);
        org.junit.Assert.assertArrayEquals(data, out.toByteArray());
    }

 @Test
 public void getCharsetFromContentType_validCharset_returnsCharset() {
     String result = DataUtil.getCharsetFromContentType("text/html; charset=UTF-8");
     org.junit.Assert.assertEquals("UTF-8", result);
 }

 @Test
 public void getCharsetFromContentType_unsupportedCharset_returnsNull() {
     String result = DataUtil.getCharsetFromContentType("text/html; charset=unsupported");
     org.junit.Assert.assertNull(result);
 }

 @Test
 public void load_utf8BomStream_usesUtf8Charset() throws java.io.IOException {
     byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
     byte[] html = "<html><head></head><body>test</body></html>".getBytes("UTF-8");
     byte[] full = new byte[bom.length + html.length];
     System.arraycopy(bom, 0, full, 0, bom.length);
     System.arraycopy(html, 0, full, bom.length, html.length);
     java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(full);
     org.jsoup.nodes.Document doc = DataUtil.load(in, null, "http://example.com");
     org.junit.Assert.assertEquals("UTF-8", doc.charset().name());
 }
}
