package org.apache.commons.lang;

 import static org.junit.Assert.*;
 import org.junit.Test;

 public class StringEscapeUtilsTest {

  @Test
  public void testEscapeJava_Null() {
      assertNull(StringEscapeUtils.escapJava(null));
  }

  @Test
  public void testEscapeJava_Empty() {
      assertEquals("", StringEscapeUtils.escapJava(""));
  }

  @Test
  public void testEscapeJava_NoEscapeNeeded() {
      assertEquals("abc", StringEscapeUtils.escapJava("abc"));
      assertEquals("123", StringEscapeUtils.escapJava("123"));
  }

  @Test
  public void testEscapeJava_BackslashAndQuote() {
      assertEquals("\\\\", StringEscapeUtils.escapJava("\\"));
      assertEquals("\\\"", StringEscapeUtils.escapJava("\""));
      assertEquals("a\\\\b\\\"c", StringEscapeUtils.escapJava("a\\b\"c"));
  }

  @Test
  public void testEscapeJava_ControlCharacters() {
      assertEquals("\\t", StringEscapeUtils.escapJava("\t"));
      assertEquals("\\n", StringEscapeUtils.escapJava("\n"));
      assertEquals("\\r", StringEscapeUtils.escapJava("\r"));
      assertEquals("\\f", StringEscapeUtils.escapJava("\f"));
      assertEquals("\\b", StringEscapeUtils.escapJava("\b"));
      assertEquals("line1\\nline2\\tcol", StringEscapeUtils.escapJava("line1\nline2\tcol"));
  }

  @Test
  public void testEscapeJava_SlashNotEscaped() {
      // LANG-421: slash must not be escaped
      assertEquals("/", StringEscapeUtils.escapJava("/"));
      assertEquals("a/b/c", StringEscapeUtils.escapJava("a/b/c"));
      assertEquals("hello/", StringEscapeUtils.escapJava("hello/"));
      assertEquals("/world", StringEscapeUtils.escapJava("/world"));
      assertEquals("//", StringEscapeUtils.escapJava("//"));
      assertEquals("String with a slash (/) in it",
              StringEscapeUtils.escapJava("String with a slash (/) in it"));
  }

  @Test
  public void testEscapeJavaScript_SlashNotEscaped() {
      // JavaScript escaping currently escapes slash (remaining LANG-421 behavior)
      assertEquals("\\/", StringEscapeUtils.escapJavaScript("/"));
      assertEquals("a\\/b\\/c", StringEscapeUtils.escapJavaScript("a/b/c"));
      assertEquals("String with a slash (\\/) in it",
              StringEscapeUtils.escapJavaScript("String with a slash (/) in it"));
  }

  @Test
  public void testEscapeJava_SingleQuoteNotEscaped() {
      // escapeJava does NOT escape single quotes (escapeSingleQuotes=false)
      assertEquals("'", StringEscapeUtils.escapJava("'"));
      assertEquals("It's fine", StringEscapeUtils.escapJava("It's fine"));
  }

  @Test
  public void testEscapeJavaScript_SingleQuoteEscaped() {
      // escapeJavaScript DOES escape single quotes (escapeSingleQuotes=true)
      assertEquals("\\'", StringEscapeUtils.escapJavaScript("'"));
      assertEquals("It\\'s fine", StringEscapeUtils.escapJavaScript("It's fine"));
  }

  @Test
  public void testEscapeJava_RoundTripWithoutSlash() {
      // escapeJava followed by unescapeJava should return original for strings without slash
      String original = "Hello\\\\\\\"World\\\\n";
      String escaped = StringEscapeUtils.escapJava(original);
      String unescaped = StringEscapeUtils.unescapJava(escaped);
      assertEquals(original, unescaped);
  }

  @Test
  public void testEscapeJava_UnicodeCharacters() {
      // chars above 0x7f are escaped as \\u00XX etc.
      assertEquals("\\u00E9", StringEscapeUtils.escapJava("\u00E9")); // é
      assertEquals("\\u00FF", StringEscapeUtils.escapJava("\u00FF")); // ÿ
      assertEquals("\\u0FFF", StringEscapeUtils.escapJava("\u0FFF"));
      assertEquals("\\u10FF", StringEscapeUtils.escapJava("\u10FF"));
  }

  @Test
  public void testEscapeJava_MixedSpecialWithSlash() {
      // slash should not be escaped, but backslash and quote should be
      assertEquals("a/b\\\\c\\\"d", StringEscapeUtils.escapJava("a/b\\c\"d"));
      assertEquals("/\\\\path\\n\\\"/", StringEscapeUtils.escapJava("/\\path\n\"/"));
  }

 }