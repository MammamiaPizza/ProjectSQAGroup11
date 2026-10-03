package org.jsoup.helper;

 import static org.junit.Assert.*;

 import org.jsoup.nodes.Document;
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;

 import java.io.*;
 import java.nio.charset.Charset;

 public class DataUtilTest {

     private File tempFile;

     @After
     public void tearDown() {
         if (tempFile != null && tempFile.exists()) {
             tempFile.delete();
         }
     }

     // -----------------------------------------------------------------------
     // helpers
     // -----------------------------------------------------------------------

     private File createTempFile(byte[] bytes) throws IOException {
         File f = File.createTempFile("jsoup", ".html");
         f.deleteOnExit();
         FileOutputStream fos = new FileOutputStream(f);
         try {
             fos.write(bytes);
         } finally {
             fos.close();
         }
         return f;
     }

     private byte[] bomAndContentBytes(String charsetName, String html) throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         if ("UTF-8".equals(charsetName)) {
             baos.write(0xEF);
             baos.write(0xBB);
             baos.write(0xBF);
         } else if ("UTF-16LE".equals(charsetName)) {
             baos.write(0xFF);
             baos.write(0xFE);
         } else if ("UTF-16BE".equals(charsetName)) {
             baos.write(0xFE);
             baos.write(0xFF);
         }
         baos.write(html.getBytes(charsetName));
         return baos.toByteArray();
     }

     // -----------------------------------------------------------------------
     // BOM detection and removal
     // -----------------------------------------------------------------------

     @Test
     public void testUtf8BomFile() throws IOException {
         byte[] bytes = bomAndContentBytes("UTF-8", "<html><head></head><body>Hello</body></html>");
         tempFile = createTempFile(bytes);

         Document doc = DataUtil.load(tempFile, null, "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         String text = doc.body().text();

         assertFalse("Text should not start with BOM", text.startsWith("\uFEFF"));
         assertEquals("Hello", text.trim());
         assertEquals("UTF-8", doc.outputSettings().charset().name());
     }

     @Test
     public void testUtf16LeBomFile() throws IOException {
         byte[] bytes = bomAndContentBytes("UTF-16LE",
"<html><head></head><body>Hello</body></html>");
         tempFile = createTempFile(bytes);

         Document doc = DataUtil.load(tempFile, null, "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         String text = doc.body().text();

         // Expected: no BOM, correct body text, correct charset
         assertFalse("Text should not start with BOM", text.startsWith("\uFEFF"));
         assertEquals("Hello", text.trim());
         assertEquals("UTF-16LE", doc.outputSettings().charset().name());
     }

     @Test
     public void testUtf16BeBomFile() throws IOException {
         byte[] bytes = bomAndContentBytes("UTF-16BE",
"<html><head></head><body>Hello</body></html>");
         tempFile = createTempFile(bytes);

         Document doc = DataUtil.load(tempFile, null, "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         String text = doc.body().text();

         assertFalse("Text should not start with BOM", text.startsWith("\uFEFF"));
         assertEquals("Hello", text.trim());
         assertEquals("UTF-16BE", doc.outputSettings().charset().name());
     }

     @Test
     public void testUtf8BomWithMetaCharset() throws IOException {
         String html = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html;
charset=ISO-8859-1\"></head><body>Hello</body></html>";
         byte[] bytes = bomAndContentBytes("UTF-8", html);
         tempFile = createTempFile(bytes);

         Document doc = DataUtil.load(tempFile, null, "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         String text = doc.body().text();

         // BOM should take precedence and the output charset is UTF-8, not ISO-8859-1
         assertFalse("Text should not start with BOM", text.startsWith("\uFEFF"));
         assertEquals("Hello", text.trim());
         assertEquals("UTF-8", doc.outputSettings().charset().name());
     }

     @Test
     public void testNoBomFileWithMetaCharset() throws IOException {
         String html = "<html><head><meta charset=\"ISO-8859-1\"></head><body>Hello</body></html>";
         byte[] bytes = html.getBytes("ISO-8859-1");
         tempFile = createTempFile(bytes);

         Document doc = DataUtil.load(tempFile, null, "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         String text = doc.body().text();

         assertEquals("Hello", text.trim());
         assertEquals("ISO-8859-1", doc.outputSettings().charset().name());
     }

     @Test
     public void testNoBomFileDefaultCharset() throws IOException {
         byte[] bytes = "<html><head></head><body>Hello</body></html>".getBytes("UTF-8");
         tempFile = createTempFile(bytes);

         Document doc = DataUtil.load(tempFile, null, "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;

         assertEquals("Hello", doc.body().text().trim());
         assertEquals("UTF-8", doc.outputSettings().charset().name());
     }

     @Test
     public void testExplicitCharsetOverriddenByBom() throws IOException {
         byte[] bytes = bomAndContentBytes("UTF-8", "<html><head></head><body>Hello</body></html>");
         tempFile = createTempFile(bytes);

         // Explicitly pass a different charset; BOM should override it to UTF-8
         Document doc = DataUtil.load(tempFile, "ISO-8859-1", "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         String text = doc.body().text();

         assertFalse("Text should not start with BOM", text.startsWith("\uFEFF"));
         assertEquals("Hello", text.trim());
         assertEquals("UTF-8", doc.outputSettings().charset().name());
     }

     @Test
     public void testBomRemovalDoesNotCorruptFirstCharacter() throws IOException {
         // Content starts with a non-ASCII character after BOM, e.g. '©' (U+00A9)
         String html = "<html><head></head><body>\u00A9 Hello</body></html>";
         byte[] bytes = bomAndContentBytes("UTF-8", html);
         tempFile = createTempFile(bytes);

         Document doc = DataUtil.load(tempFile, null, "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         String text = doc.body().text();

         assertFalse("Text should not start with BOM", text.startsWith("\uFEFF"));
         assertTrue("Text should start with the copyright sign", text.startsWith("\u00A9 Hello"));
         assertEquals("UTF-8", doc.outputSettings().charset().name());
     }

     // -----------------------------------------------------------------------
     // Edge cases
     // -----------------------------------------------------------------------

     @Test
     public void testEmptyFile() throws IOException {
         tempFile = createTempFile(new byte[0]);
         Document doc = DataUtil.load(tempFile, null, "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;

         assertNotNull(doc);
         assertEquals("", doc.body().text());
     }

     @Test
     public void testReadFileToByteBuffer() throws IOException {
         byte[] content = "dummy".getBytes("UTF-8");
         tempFile = createTempFile(content);

         ByteBuffer bb = DataUtil.readFileToByteBuffer(tempFile);
         byte[] actual = new byte[bb.remaining()];
         bb.get(actual);

         assertArrayEquals(content, actual);
     }

     // -----------------------------------------------------------------------
     // getCharsetFromContentType
     // -----------------------------------------------------------------------

     @Test
     public void testGetCharsetFromContentType() {
         assertEquals("EUC-JP", DataUtil.getCharsetFromContentType("text/html; charset=EUC-JP"));
         assertNull(DataUtil.getCharsetFromContentType(null));
         assertNull(DataUtil.getCharsetFromContentType("text/html"));
         // case-insensitive, quotation marks
         assertEquals("SHIFT_JIS", DataUtil.getCharsetFromContentType("text/html;
charset='SHIFT_JIS'"));
     }

     @Test
     public void testLoadInputStreamUtf8Bom() throws IOException {
         byte[] bytes = bomAndContentBytes("UTF-8",
"<html><head></head><body>Stream</body></html>");
         InputStream is = new ByteArrayInputStream(bytes);

         Document doc = DataUtil.load(is, null, "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         String text = doc.body().text();

         assertFalse("Text should not start with BOM", text.startsWith("\uFEFF"));
         assertEquals("Stream", text.trim());
         assertEquals("UTF-8", doc.outputSettings().charset().name());
     }
 }
