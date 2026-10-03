package org.jsoup.helper;

 import static org.junit.Assert.*;

 import org.junit.Test;
 import org.jsoup.nodes.Document;
 import org.jsoup.parser.Parser;

 import java.io.ByteArrayInputStream;
 import java.io.InputStream;
 import java.nio.ByteBuffer;
 import java.nio.charset.Charset;

 public class DataUtilTest {

  @Test
  public void testCharset() {
  String contentType = "text/html; charset=UTF-8";
  assertEquals("utf-8", DataUtil.getCharsetFromContentType(contentType));
  }

  @Test
  public void testQuotedCharset() {
  String contentType = "text/html; charset=\"UTF-8\"";
  assertEquals("utf-8", DataUtil.getCharsetFromContentType(contentType));
  }

  @Test
  public void testCharsetFromContentType_Null() {
  assertNull(DataUtil.getCharsetFromContentType(null));
  }

  @Test
  public void testCharsetFromContentType_Missing() {
  assertNull(DataUtil.getCharsetFromContentType("text/html"));
  }

  @Test
  public void testCharsetFromContentType_Malformed() {
  assertNull(DataUtil.getCharsetFromContentType("text/html; charset"));
  }

  @Test
  public void testCharsetFromContentType_EmptyValue() {
  // empty value should be treated as not supported
  assertNull(DataUtil.getCharsetFromContentType("text/html; charset="));
  }

  @Test
  public void testCharsetFromContentType_Whitespace() {
  assertEquals("utf-8", DataUtil.getCharsetFromContentType("text/html; charset= UTF-8 "));
  }

  @Test
  public void testCharsetFromContentType_ExtraAttribute() {
  assertEquals("utf-8", DataUtil.getCharsetFromContentType("text/html; param=value; charset=UTF-8;
other=something"));
  }

  @Test
  public void testCharsetFromContentType_LowerCaseInput() {
  // even though input is lowercase, the spec (comment) says uppercase, but the expected behaviour
of the fix expects lowercase canonical name
  assertEquals("utf-8", DataUtil.getCharsetFromContentType("text/html; charset=utf-8"));
  }

  @Test
  public void testCharsetFromContentType_UnkownCharset() {
  // returns the raw value upperccased
  assertEquals("X-NONSENSE", DataUtil.getCharsetFromContentType("text/html; charset=x-nonsense"));
  }

  @Test
  public void testParseByteData_NullCharset_NoMeta() {
  String html = "<html><head></head><body>test</body></html>";
  ByteBuffer buf = ByteBuffer.wrap(html.getBytes(Charset.forName("UTF-8")));
  // When no charset specified and no meta, the document should use default charset (UTF-8) and not
throw NPE
  Document doc = DataUtil.parseByteData(buf, null, "", Parser.htmlParser());
  assertNotNull(doc);
  assertNotNull(doc.outputSettings().charset());
  // The buggy implementation sets output charset to null because charsetName remains null; this
should be fixed
  // This test may reveal that NPE is thrown
  assertNotNull(doc.outputSettings().charset());
  }

  @Test
  public void testParseByteData_SpecifiedCharset() {
  String html = "<html><head></head><body>test</body></html>";
  ByteBuffer buf = ByteBuffer.wrap(html.getBytes(Charset.forName("UTF-8")));
  Document doc = DataUtil.parseByteData(buf, "utf-8", "", Parser.htmlParser());
  assertEquals("utf-8",
doc.outputSettings().charset().name().toLowerCase(java.util.Locale.ENGLISH));
  }

  @Test
  public void testParseByteData_WithMetaCharset() {
  // HTML with meta element specifying charset
  String html = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html;
charset=UTF-8\"></head><body>test</body></html>";
  ByteBuffer buf = ByteBuffer.wrap(html.getBytes(Charset.forName("UTF-8")));
  Document doc = DataUtil.parseByteData(buf, null, "", Parser.htmlParser());
  // The meta detection should re-decode and set charset to the found value, which should be
lowercase after fix
  assertEquals("utf-8",
doc.outputSettings().charset().name().toLowerCase(java.util.Locale.ENGLISH));
  }

  @Test
  public void testLoad_InputStream_NullCharset_NoMeta() throws Exception {
  String html = "<html><head></head><body>test</body></html>";
  InputStream in = new ByteArrayInputStream(html.getBytes(Charset.forName("UTF-8")));
  // When charset is null and no meta, load should not throw NPE
  Document doc = DataUtil.load(in, null, "");
  assertNotNull(doc);
  in.close();
  }

  @Test
  public void testLoad_InputStream_SpecifiedCharset() throws Exception {
  String html = "<html><head></head><body>test</body></html>";
  InputStream in = new ByteArrayInputStream(html.getBytes(Charset.forName("UTF-8")));
  Document doc = DataUtil.load(in, "utf-8", "");
  assertEquals("utf-8",
doc.outputSettings().charset().name().toLowerCase(java.util.Locale.ENGLISH));
  in.close();
  }
 }
