package org.apache.commons.lang3.text.translate;

 import static org.junit.Assert.assertEquals;

 import java.io.IOException;
 import java.io.StringWriter;

 import org.junit.Test;

 public class NumericEntityUnescaperTest {

     private final NumericEntityUnescaper unescaper = new NumericEntityUnescaper();

     @Test
     public void testValidDecimal() throws IOException {
         StringWriter sw = new StringWriter();
         int consumed = unescaper.translate("&#65;", 0, sw);
         assertEquals("A", sw.toString());
         assertEquals(5, consumed);
     }

     @Test
     public void testValidHex() throws IOException {
         StringWriter sw = new StringWriter();
         int consumed = unescaper.translate("&#x41;", 0, sw);
         assertEquals("A", sw.toString());
         assertEquals(6, consumed);
     }

     @Test
     public void testEntityWithoutSemicolon() throws IOException {
         StringWriter sw = new StringWriter();
         int consumed = unescaper.translate("&#65", 0, sw);
         assertEquals("A", sw.toString());
         assertEquals(4, consumed);
     }

     @Test
     public void testUnfinishedEntityNoDigits() throws IOException {
         StringWriter sw = new StringWriter();
         int consumed = unescaper.translate("&#", 0, sw);
         assertEquals("", sw.toString());
         assertEquals(0, consumed);
     }

     @Test
     public void testUnfinishedHexEntity() throws IOException {
         StringWriter sw = new StringWriter();
         int consumed = unescaper.translate("&#x", 0, sw);
         assertEquals("", sw.toString());
         assertEquals(0, consumed);
     }

     @Test(expected = StringIndexOutOfBoundsException.class)
     public void testIndexBeyondLength() throws IOException {
         unescaper.translate("ABC", 3, new StringWriter());
     }

     @Test
     public void testNegativeCodepoint() throws IOException {
         StringWriter sw = new StringWriter();
         int consumed = unescaper.translate("&#-1;", 0, sw);
         assertEquals("", sw.toString());
         assertEquals(0, consumed);
     }

     @Test
     public void testLargeCodepoint() throws IOException {
         StringWriter sw = new StringWriter();
         int consumed = unescaper.translate("&#1114111;", 0, sw);
         assertEquals(new String(Character.toChars(1114111)), sw.toString());
         assertEquals(10, consumed);
     }

     @Test
     public void testNoHashAfterAmpersand() throws IOException {
         StringWriter sw = new StringWriter();
         int consumed = unescaper.translate("&amp;", 0, sw);
         assertEquals("", sw.toString());
         assertEquals(0, consumed);
     }

     @Test(expected = StringIndexOutOfBoundsException.class)
     public void testEmptyInput() throws IOException {
         unescaper.translate("", 0, new StringWriter());
     }

     @Test
     public void testIndexAtLastChar() throws IOException {
         StringWriter sw = new StringWriter();
         int consumed = unescaper.translate("A&", 1, sw);
         assertEquals("", sw.toString());
         assertEquals(0, consumed);
     }

     @Test
     public void testUnfinishedEntityAtEndOfString() throws IOException {
         String input = "Test &#";
         StringWriter sw = new StringWriter();
         int consumed = unescaper.translate(input, 5, sw);
         assertEquals("", sw.toString());
         assertEquals(0, consumed);
     }

@Test
 public void testHexUpperCaseEntity() throws IOException {
     org.apache.commons.lang3.text.translate.NumericEntityUnescaper neu = new
org.apache.commons.lang3.text.translate.NumericEntityUnescaper();
     java.io.StringWriter sw = new java.io.StringWriter();
     int consumed = neu.translate("&#X41;", 0, sw);
     org.junit.Assert.assertEquals("A", sw.toString());
     org.junit.Assert.assertEquals(6, consumed);
 }

 @Test
 public void testAmpersandAsLastCharacter() throws IOException {
     org.apache.commons.lang3.text.translate.NumericEntityUnescaper neu = new
org.apache.commons.lang3.text.translate.NumericEntityUnescaper();
     java.io.StringWriter sw = new java.io.StringWriter();
     int consumed = neu.translate("&", 0, sw);
     org.junit.Assert.assertEquals("", sw.toString());
     org.junit.Assert.assertEquals(0, consumed);
 }

 @Test
 public void testDecimalEntityAtVeryEndNoSemicolon() throws IOException {
     org.apache.commons.lang3.text.translate.NumericEntityUnescaper neu = new
org.apache.commons.lang3.text.translate.NumericEntityUnescaper();
     java.io.StringWriter sw = new java.io.StringWriter();
     int consumed = neu.translate("&#97", 0, sw);
     org.junit.Assert.assertEquals("a", sw.toString());
     org.junit.Assert.assertEquals(4, consumed);
 }

 @Test
 public void testEntityWithTrailingNonSemicolon() throws IOException {
     org.apache.commons.lang3.text.translate.NumericEntityUnescaper neu = new
org.apache.commons.lang3.text.translate.NumericEntityUnescaper();
     java.io.StringWriter sw = new java.io.StringWriter();
     int consumed = neu.translate("&#98X", 0, sw);
     org.junit.Assert.assertEquals("b", sw.toString());
     org.junit.Assert.assertEquals(4, consumed);
 }
}
