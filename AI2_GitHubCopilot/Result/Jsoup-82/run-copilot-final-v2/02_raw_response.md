package org.jsoup.helper;

 import static org.junit.Assert.*;

 import org.jsoup.nodes.Document;
 import org.junit.Test;

 import java.io.ByteArrayInputStream;
 import java.io.InputStream;
 import java.lang.reflect.Method;
 import java.nio.charset.Charset;

 /**
  * Tests for {@link DataUtil}, focusing on charset validation/fallback,
  * content-type parsing, and input stream parsing with charset resolution.
  */
 public class DataUtilTest {

     /* Helper to invoke the private validateCharset method */
     private static String invokeValidateCharset(String cs) throws Exception {
         Method method = DataUtil.class.getDeclaredMethod("validateCharset", String.class);
         method.setAccessible(true);
         return (String) method.invoke(null, cs);
     }

     @Test
     public void testValidateCharsetUnsupported() throws Exception {
         // Bug: unsupported charsets (e.g. ISO-2022-CN) must fall back to UTF-8
         assertEquals("UTF-8", invokeValidateCharset("ISO-2022-CN"));
     }

     @Test
     public void testValidateCharsetEmpty() throws Exception {
         // Empty string should behave as default UTF-8
         assertEquals("UTF-8", invokeValidateCharset(""));
     }

     @Test
     public void testValidateCharsetBlank() throws Exception {
         // Whitespace-only should be treated as empty → UTF-8
         assertEquals("UTF-8", invokeValidateCharset("   "));
     }

     @Test
     public void testValidateCharsetNull() throws Exception {
         // Null input indicates “no charset” → return null
         assertNull(invokeValidateCharset(null));
     }

     @Test
     public void testValidateCharsetSupported() throws Exception {
         String result = invokeValidateCharset("utf-8");
         assertNotNull(result);
         assertTrue(Charset.isSupported(result));
     }

     @Test
     public void testValidateCharsetAlias() throws Exception {
         String result = invokeValidateCharset("UTF8");
         assertNotNull(result);
         assertTrue(Charset.isSupported(result));
     }

     @Test
     public void testValidateCharsetIsoAlias() throws Exception {
         String result = invokeValidateCharset("iso-8859-1");
         assertNotNull(result);
         assertTrue(Charset.isSupported(result));
     }

     @Test
     public void testGetCharsetFromContentTypeStandard() {
         assertEquals("UTF-8", DataUtil.getCharsetFromContentType("text/html; charset=UTF-8"));
     }

     @Test
     public void testGetCharsetFromContentTypeUnsupported() {
         // Unsupported charset must be mapped to UTF-8 during extraction
         assertEquals("UTF-8", DataUtil.getCharsetFromContentType("text/html;
charset=ISO-2022-CN"));
     }

     @Test
     public void testGetCharsetFromContentTypeNull() {
         assertNull(DataUtil.getCharsetFromContentType(null));
     }

     @Test
     public void testParseInputStreamFallbackToUtfIfCantEncode() throws Exception {
         String html = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html;
charset=ISO-2022-CN\">"
                 + "</head><body><p>Hello</p></body></html>";
         InputStream in = new ByteArrayInputStream(html.getBytes("UTF-8"));
         Document doc = DataUtil.load(in, null, "");
         assertEquals("UTF-8", doc.outputSettings().charset().name());
     }

     @Test
     public void testParseInputStreamValidCharset() throws Exception {
         String html = "<html><head><meta
charset=\"ISO-8859-1\"></head><body><p>Héllo</p></body></html>";
         InputStream in = new ByteArrayInputStream(html.getBytes("ISO-8859-1"));
         Document doc = DataUtil.load(in, null, "");
         assertEquals("ISO-8859-1", doc.outputSettings().charset().name());
     }

     @Test
     public void testParseInputStreamNoCharsetDefaultsUtf8() throws Exception {
         String html = "<html><head></head><body><p>Test</p></body></html>";
         InputStream in = new ByteArrayInputStream(html.getBytes("UTF-8"));
         Document doc = DataUtil.load(in, null, "");
         assertEquals("UTF-8", doc.outputSettings().charset().name());
     }
 }