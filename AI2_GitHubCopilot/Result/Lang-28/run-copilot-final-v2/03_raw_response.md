package org.apache.commons.lang3.text.translate;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;

 import java.io.IOException;
 import java.io.StringWriter;

 import org.junit.Test;

 /**
  * Tests for {@link NumericEntityUnescaper}, focusing on the supplementary
  * codepoint bug (LANG-617) where codepoints above U+FFFF are not correctly
  * written as surrogate pairs.
  */
 public class NumericEntityUnescaperTest {

     private final NumericEntityUnescaper unescaper = new NumericEntityUnescaper();

     // Helper: translate a CharSequence fully using the public translate(String) method
     private String translate(String input) {
         return unescaper.translate(input);
     }

     @Test
     public void testNullInput() {
         assertNull(translate(null));
     }

     @Test
     public void testNoEntity() {
         assertEquals("Hello World", translate("Hello World"));
     }

     @Test
     public void testBMPHex() {
         // &#x41; -> 'A'
         assertEquals("A", translate("&#x41;"));
     }

     @Test
     public void testBMPDecimal() {
         // &#65; -> 'A'
         assertEquals("A", translate("&#65;"));
     }

     @Test
     public void testBMPMixedCaseHex() {
         // &#X4a; -> 'J'
         assertEquals("J", translate("&#X4a;"));
         assertEquals("J", translate("&#x4A;"));
     }

     @Test
     public void testSupplementaryHexDirectly() throws IOException {
         // The core of the bug: codepoint 0x10400 () must become
         // the surrogate pair \uD801\uDC00
         String entity = "&#x10400;";
         StringWriter out = new StringWriter();
         int consumed = unescaper.translate(entity, 0, out);
         assertEquals("Wrong number of characters consumed",
                 entity.length(), consumed);
         out.append(entity, consumed, entity.length());
         String expected = new String(Character.toChars(0x10400));
         assertEquals(expected, out.toString());
     }

     @Test
     public void testSupplementaryDecimalDirectly() throws IOException {
         // Decimal 66560 is the same as 0x10400
         String entity = "&#66560;";
         StringWriter out = new StringWriter();
         int consumed = unescaper.translate(entity, 0, out);
         assertEquals("Wrong number of characters consumed",
                 entity.length(), consumed);
         out.append(entity, consumed, entity.length());
         String expected = new String(Character.toChars(66560));
         assertEquals(expected, out.toString());
     }

     @Test
     public void testSupplementaryHex() {
         // Public API – should reveal the bug
         String input = "&#x10400;";
         String expected = new String(Character.toChars(0x10400));
         assertEquals(expected, translate(input));
     }

     @Test
     public void testSupplementaryDecimal() {
         String input = "&#66560;";
         String expected = new String(Character.toChars(66560));
         assertEquals(expected, translate(input));
     }

     @Test
     public void testMaxCodePointHex() {
         // U+10FFFF (non-character but valid maximum)
         String input = "&#x10FFFF;";
         String expected = new String(Character.toChars(0x10FFFF));
         assertEquals(expected, translate(input));
     }

     @Test
     public void testMaxCodePointDecimal() {
         String input = "&#1114111;";
         String expected = new String(Character.toChars(0x10FFFF));
         assertEquals(expected, translate(input));
     }

     @Test
     public void testInvalidCodepointTooLarge() {
         // 0x110000 is beyond valid range; entity should remain unescaped
         String input = "&#x110000;";
         assertFalse(input.equals(translate(input)));
     }

     @Test
     public void testInvalidHexChars() {
         // non-hex letters after x -> NumberFormatException -> no unescape
         String input = "&#xGHIJ;";
         assertEquals(input, translate(input));
     }

     @Test
     public void testEmptyEntity() {
         // &#; should parse to empty string for parseInt -> exception -> ignored
         String input = "&#;";
         assertEquals(input, translate(input));
     }

     @Test
     public void testMultipleEntitiesMixed() {
         String input = "&#x41; normal &#65; and &#x10400; supplementary &#66560; end";
         String expected = "A normal A and "
                 + new String(Character.toChars(0x10400))
                 + " supplementary "
                 + new String(Character.toChars(66560))
                 + " end";
         assertEquals(expected, translate(input));
     }
 }