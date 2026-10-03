package org.apache.commons.lang;

import static org.junit.Assert.*;
import org.junit.Test;

public class StringEscapeUtilsTest {

 @Test
 public void testEscapeJava_Null() {
     assertNull(StringEscapeUtils.escapeJava(null));
 }

 @Test
 public void testEscapeJava_Empty() {
     assertEquals("", StringEscapeUtils.escapeJava(""));
 }

 @Test
 public void testEscapeJava_NoEscapeNeeded() {
     assertEquals("abc", StringEscapeUtils.escapeJava("abc"));
     assertEquals("123", StringEscapeUtils.escapeJava("123"));
 }

 @Test
 public void testEscapeJava_BackslashAndQuote() {
     assertEquals("\\\\", StringEscapeUtils.escapeJava("\\"));
     assertEquals("\\\"", StringEscapeUtils.escapeJava("\""));
     assertEquals("a\\\\b\\\"c", StringEscapeUtils.escapeJava("a\\b\"c"));
 }

 @Test
 public void testEscapeJava_ControlCharacters() {
     assertEquals("\\t", StringEscapeUtils.escapeJava("\t"));
     assertEquals("\\n", StringEscapeUtils.escapeJava("\n"));
     assertEquals("\\r", StringEscapeUtils.escapeJava("\r"));
     assertEquals("\\f", StringEscapeUtils.escapeJava("\f"));
     assertEquals("\\b", StringEscapeUtils.escapeJava("\b"));
     assertEquals("line1\\nline2\\tcol", StringEscapeUtils.escapeJava("line1\nline2\tcol"));
 }

 @Test
 public void testEscapeJava_SlashNotEscaped() {
     // LANG-421: slash must not be escaped
     assertEquals("/", StringEscapeUtils.escapeJava("/"));
     assertEquals("a/b/c", StringEscapeUtils.escapeJava("a/b/c"));
     assertEquals("hello/", StringEscapeUtils.escapeJava("hello/"));
     assertEquals("/world", StringEscapeUtils.escapeJava("/world"));
     assertEquals("//", StringEscapeUtils.escapeJava("//"));
     assertEquals("String with a slash (/) in it",
             StringEscapeUtils.escapeJava("String with a slash (/) in it"));
 }

 @Test
 public void testEscapeJavaScript_SlashNotEscaped() {
     // JavaScript escaping shares the same private method; slash should not be escaped
     assertEquals("/", StringEscapeUtils.escapeJavaScript("/"));
     assertEquals("a/b/c", StringEscapeUtils.escapeJavaScript("a/b/c"));
     assertEquals("String with a slash (/) in it",
             StringEscapeUtils.escapeJavaScript("String with a slash (/) in it"));
 }

 @Test
 public void testEscapeJava_SingleQuoteNotEscaped() {
     // escapeJava does NOT escape single quotes (escapeSingleQuotes=false)
     assertEquals("'", StringEscapeUtils.escapeJava("'"));
     assertEquals("It's fine", StringEscapeUtils.escapeJava("It's fine"));
 }

 @Test
 public void testEscapeJavaScript_SingleQuoteEscaped() {
     // escapeJavaScript DOES escape single quotes (escapeSingleQuotes=true)
     assertEquals("\\'", StringEscapeUtils.escapeJavaScript("'"));
     assertEquals("It\\'s fine", StringEscapeUtils.escapeJavaScript("It's fine"));
 }

 @Test
 public void testEscapeJava_RoundTripWithoutSlash() {
     // escapeJava followed by unescapeJava should return original for strings without slash
     String original = "Hello\\\\\\\"World\\\\n";
     String escaped = StringEscapeUtils.escapeJava(original);
     String unescaped = StringEscapeUtils.unescapeJava(escaped);
     assertEquals(original, unescaped);
 }

 @Test
 public void testEscapeJava_UnicodeCharacters() {
     // chars above 0x7f are escaped as \\u00XX etc.
     assertEquals("\\u00E9", StringEscapeUtils.escapeJava("\u00E9")); // é
     assertEquals("\\u00FF", StringEscapeUtils.escapeJava("\u00FF")); // ÿ
     assertEquals("\\u0FFF", StringEscapeUtils.escapeJava("\u0FFF"));
     assertEquals("\\u10FF", StringEscapeUtils.escapeJava("\u10FF"));
 }

 @Test
 public void testEscapeJava_MixedSpecialWithSlash() {
     // slash should not be escaped, but backslash and quote should be
     assertEquals("a/b\\\\c\\\"d", StringEscapeUtils.escapeJava("a/b\\c\"d"));
     assertEquals("/\\\\path\\n\\\"/", StringEscapeUtils.escapeJava("/\\path\n\"/"));
 }

}
