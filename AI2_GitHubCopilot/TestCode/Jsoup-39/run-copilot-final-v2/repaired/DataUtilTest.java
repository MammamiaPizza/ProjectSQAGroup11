package org.jsoup.helper;

 import org.junit.Test;
 import static org.junit.Assert.*;
 import java.io.*;
 import org.jsoup.nodes.Document;
 import org.jsoup.parser.Parser;

 public class DataUtilTest {

     @Test
     public void discardsSpuriousByteOrderMarkWhenNoCharsetSet() throws IOException {
         byte[] bomAndContent = new byte[] {
             (byte)0xEF, (byte)0xBB, (byte)0xBF, // UTF-8 BOM
             'O', 'n', 'e'
         };
         InputStream in = new ByteArrayInputStream(bomAndContent);
         // charsetName null to trigger the bug path
         Document doc = DataUtil.load(in, null, "http://example.com");
         in.close();
         assertEquals("One", doc.body().text());
     }

     @Test
     public void testNoBomNoCharset() throws IOException {
         byte[] content = "Two".getBytes("UTF-8");
         InputStream in = new ByteArrayInputStream(content);
         Document doc = DataUtil.load(in, null, "http://example.com");
         in.close();
         assertEquals("Two", doc.body().text());
     }

     @Test
     public void testBomWithExplicitCharset() throws IOException {
         byte[] bomAndContent = new byte[] {
             (byte)0xEF, (byte)0xBB, (byte)0xBF,
             'T', 'h', 'r', 'e', 'e'
         };
         InputStream in = new ByteArrayInputStream(bomAndContent);
         // charset explicitly provided: should strip BOM correctly
         Document doc = DataUtil.load(in, "UTF-8", "http://example.com");
         in.close();
         assertEquals("Three", doc.body().text());
     }

     @Test
     public void testBomOnlyEmptyContent() throws IOException {
         byte[] bomOnly = new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF };
         InputStream in = new ByteArrayInputStream(bomOnly);
         Document doc = DataUtil.load(in, null, "http://example.com");
         in.close();
         assertTrue(doc.body().text().isEmpty());
     }

     @Test
     public void testBomFollowedBySpace() throws IOException {
         byte[] bomAndSpace = new byte[] {
             (byte)0xEF, (byte)0xBB, (byte)0xBF, ' ', 'A'
         };
         InputStream in = new ByteArrayInputStream(bomAndSpace);
         Document doc = DataUtil.load(in, null, "http://example.com");
         in.close();
         assertEquals(" A", doc.body().text());
     }

     @Test
     public void testGetCharsetFromContentTypeValid() {
         assertEquals("UTF-8",
                 DataUtil.getCharsetFromContentType("text/html; charset=UTF-8"));
     }

     @Test
     public void testGetCharsetFromContentTypeNull() {
         assertNull(DataUtil.getCharsetFromContentType(null));
     }

     @Test
     public void testGetCharsetFromContentTypeUnsupported() {
         assertNull(DataUtil.getCharsetFromContentType("text/html; charset=FOO-BAR"));
     }

     @Test
     public void testGetCharsetFromContentTypeEmpty() {
         assertNull(DataUtil.getCharsetFromContentType("text/html; charset="));
     }

     @Test
     public void testBomWithMetaCharsetSameAsDefault() throws IOException {
         // BOM followed by HTML containing meta charset=utf-8 (same as default)
         StringBuilder html = new StringBuilder()
                 .append("<html><head><meta charset=\"utf-8\"></head><body>Same</body></html>");
         byte[] metaBytes = ("\uFEFF" + html.toString()).getBytes("UTF-8");
         InputStream in = new ByteArrayInputStream(metaBytes);
         Document doc = DataUtil.load(in, null, "http://example.com");
         in.close();
         // The bug manifests here as well because doc is reused after initial parse
         assertEquals("Same", doc.body().text());
     }

     @Test
     public void testBomWithCharsetNameAndParser() throws IOException {
         byte[] bomAndContent = new byte[] {
             (byte)0xEF, (byte)0xBB, (byte)0xBF,
             '<','p','>','H','i','<','/','p','>'
         };
         InputStream in = new ByteArrayInputStream(bomAndContent);
         Document doc = DataUtil.load(in, "UTF-8", "http://example.com", Parser.htmlParser());
         in.close();
         assertEquals("Hi", doc.body().text());
     }

     @Test
     public void testLoadFromInputStreamWithNullCharsetAndParser() throws IOException {
         byte[] raw = "<p>Hello</p>".getBytes("UTF-8");
         InputStream in = new ByteArrayInputStream(raw);
         Document doc = DataUtil.load(in, null, "http://example.com", Parser.htmlParser());
         in.close();
         assertEquals("Hello", doc.body().text());
     }

 }
