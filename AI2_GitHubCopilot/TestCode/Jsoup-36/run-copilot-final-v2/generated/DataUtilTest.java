package org.jsoup.helper;

 import org.jsoup.nodes.Document;
 import org.jsoup.parser.Parser;
 import org.junit.Test;

 import java.io.ByteArrayInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.nio.charset.Charset;
 import java.nio.charset.IllegalCharsetNameException;

 import static org.junit.Assert.*;

 /**
  * Tests for DataUtil that reveal charset sanitization issues.
  */
 public class DataUtilTest {

     @Test
     public void testGetCharsetFromContentType_Normal() {
         assertEquals("UTF-8", DataUtil.getCharsetFromContentType("text/html; charset=UTF-8"));
         assertEquals("ISO-8859-1", DataUtil.getCharsetFromContentType("text/html;
charset=ISO-8859-1"));
     }

     @Test
     public void testGetCharsetFromContentType_Null() {
         assertNull(DataUtil.getCharsetFromContentType(null));
     }

     @Test
     public void shouldNotThrowExceptionOnEmptyCharset() {
         assertNull(DataUtil.getCharsetFromContentType("text/html; charset="));
         assertNull(DataUtil.getCharsetFromContentType("text/html; charset= "));
     }

     @Test
     public void testQuoedCharset() {
         assertEquals("UTF-8", DataUtil.getCharsetFromContentType("text/html; charset=\"UTF-8\""));
         assertEquals("UTF-8", DataUtil.getCharsetFromContentType("text/html; charset='UTF-8'"));
     }

     @Test
     public void shouldSelectFirstCharsetOnWeirdMultileCharsetsInMetaTags() {
         assertEquals("ISO-8859-1", DataUtil.getCharsetFromContentType("text/html;
charset=ISO-8859-1,"));
         assertEquals("UTF-8", DataUtil.getCharsetFromContentType("text/html; charset=UTF-8,
ISO-8859-1"));
     }

     @Test
     public void shouldReturnNullForIllegalCharsetNames() {
         assertNull(DataUtil.getCharsetFromContentType("text/html; charset=$HJKDF§$%("));
         assertNull(DataUtil.getCharsetFromContentType("text/html; charset=###"));
     }

     @Test
     public void shouldCorrectCharsetForDuplicateCharsetString() {
         assertEquals("ISO-8859-1", DataUtil.getCharsetFromContentType(
                 "text/html; charset=\"ISO-8859-1\" charset=\"UTF-8\""));
     }

     @Test
     public void testBrokenHtml5CharsetWithSingleDoubleQuote() {
         assertEquals("UTF-8", DataUtil.getCharsetFromContentType("text/html; charset=UTF-8\""));
     }

     @Test
     public void testCharsetCaseInsensity() {
         assertEquals("UTF-8", DataUtil.getCharsetFromContentType("text/html; charset=utf-8"));
         assertEquals("UTF-8", DataUtil.getCharsetFromContentType("text/html; charset=\"utf-8\""));
     }

     @Test
     public void testParseWithMalformedCharsetInMeta() throws IOException {
         String html = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html;
charset='UTF-8'\"></head><body></body></html>";
         InputStream in = new ByteArrayInputStream(html.getBytes("US-ASCII"));
         Document doc = DataUtil.load(in, null, "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         assertNotNull(doc);
     }

     @Test
     public void testUnsuportedCharsetAfterCleaning() {
         assertNull(DataUtil.getCharsetFromContentType("text/html; charset='invalid-charset123'"));
     }

     @Test
     public void testCharsetWithExtrWhitespace() {
         assertEquals("UTF-8", DataUtil.getCharsetFromContentType("text/html; charset =
\"UTF-8\""));
         assertEquals("ISO-8859-1", DataUtil.getCharsetFromContentType("text/html; charset=
ISO-8859-1 "));
     }
 }
