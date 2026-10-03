package org.jsoup.helper;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;

 import org.junit.Test;

 /**
  * Unit tests for {@link StringUtil#normaliseWhitespace(String)} and
  * {@link StringUtil#appendNormalisedWhitespace(StringBuilder, String, boolean)}
  * focusing on stripping invisible characters (soft hyphen, zero-width space,
  * zero-width non-joiner, zero-width joiner) as defined in the bug report.
  */
 public class StringUtilTest {

     // --- normaliseWhitespace tests ---

     @Test
     public void testStripsSoftHyphen() {
         // U+00AD (soft hyphen) should be completely removed
         assertEquals("helloworld",
                 StringUtil.normaliseWhitespace("hello\u00ADworld"));
     }

     @Test
     public void testStripsZeroWidthSpace() {
         // U+200B (zero width space)
         assertEquals("foobar",
                 StringUtil.normaliseWhitespace("foo\u200Bbar"));
     }

     @Test
     public void testStripsZeroWidthNonJoiner() {
         // U+200C (zero width non-joiner)
         assertEquals("ab",
                 StringUtil.normaliseWhitespace("a\u200Cb"));
     }

     @Test
     public void testStripsZeroWidthJoiner() {
         // U+200D (zero width joiner)
         assertEquals("xy",
                 StringUtil.normaliseWhitespace("x\u200Dy"));
     }

     @Test
     public void testStripsMultipleInvisible() {
         // consecutive invisible characters should all be stripped
         assertEquals("onetwo",
                 StringUtil.normaliseWhitespace("one\u00AD\u200Btwo"));
     }

     @Test
     public void testOnlyInvisibleReturnsEmpty() {
         // input consisting entirely of invisible chars → empty string
         String invisibleOnly = "\u00AD\u200B\u200C\u200D";
         assertEquals("",
                 StringUtil.normaliseWhitespace(invisibleOnly));
     }

     @Test
     public void testLeadingTrailingInvisible() {
         // invisible chars at start and end are removed without adding spaces
         assertEquals("text",
                 StringUtil.normaliseWhitespace("\u200Btext\u200B"));
     }

     @Test
     public void testInvisibleWithSurroundingSpaces() {
         // invisible between spaces: invisible stripped, spaces collapse to single
         assertEquals("hello world",
                 StringUtil.normaliseWhitespace("hello \u00AD world"));
     }

     @Test
     public void testInvisibleMixedWithWhitespace() {
         // invisible among tabs, newlines, spaces – all whitespace collapses, invisible removed
         assertEquals("a b",
                 StringUtil.normaliseWhitespace("a\u00AD\t\n b"));
     }

     @Test
     public void testNormaliseWhitespaceCollapsesSpaces() {
         // ensure normal whitespace normalization is preserved (regression check)
         assertEquals("hello world",
                 StringUtil.normaliseWhitespace("hello   \n\t world"));
     }

     @Test
     public void testEmptyString() {
         assertEquals("",
                 StringUtil.normaliseWhitespace(""));
     }

     // --- appendNormalisedWhitespace tests ---

     @Test
     public void testAppendNormalisedStripsLeadingInvisibleWhenStripLeading() {
         // stripLeading = true: leading invisible chars removed
         StringBuilder sb = new StringBuilder();
         StringUtil.appendNormalisedWhitespace(sb, "\u200Bhello", true);
         assertEquals("hello", sb.toString());
     }

     @Test
     public void testAppendNormalisedStripsInvisibleWithoutStripLeading() {
         // stripLeading = false: invisible chars still stripped, no extra spaces
         StringBuilder sb = new StringBuilder();
         StringUtil.appendNormalisedWhitespace(sb, "a\u200Cb", false);
         assertEquals("ab", sb.toString());
     }
 }
