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
     assertEquals(4, consumed); // without semi-colon we expect 4, not 5
 }

 @Test(expected = StringIndexOutOfBoundsException.class)
 public void testUnfinishedEntityNoDigits() throws IOException {
     // "&#" without digits; code tries to read past the end
     unescaper.translate("&#", 0, new StringWriter());
 }

 @Test(expected = StringIndexOutOfBoundsException.class)
 public void testUnfinishedHexEntity() throws IOException {
     // "&#x" with no hex digits
     unescaper.translate("&#x", 0, new StringWriter());
 }

 @Test
 public void testIndexBeyondLength() throws IOException {
     StringWriter sw = new StringWriter();
     int consumed = unescaper.translate("ABC", 3, sw);
     assertEquals("", sw.toString());
     assertEquals(0, consumed);
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
     assertEquals(10, consumed); // "&#1114111;" length
 }

 @Test
 public void testNoHashAfterAmpersand() throws IOException {
     StringWriter sw = new StringWriter();
     int consumed = unescaper.translate("&amp;", 0, sw);
     assertEquals("", sw.toString());
     assertEquals(0, consumed);
 }

 @Test
 public void testEmptyInput() throws IOException {
     StringWriter sw = new StringWriter();
     int consumed = unescaper.translate("", 0, sw);
     assertEquals("", sw.toString());
     assertEquals(0, consumed);
 }

 @Test
 public void testIndexAtLastChar() throws IOException {
     StringWriter sw = new StringWriter();
     int consumed = unescaper.translate("A&", 1, sw); // '&' is last char, no next char
     assertEquals("", sw.toString());
     assertEquals(0, consumed);
 }

 @Test
 public void testUnfinishedEntityAtEndOfString() throws IOException {
     // "Test &#" length 7, index 5 points at '&'; no digits after #
     String input = "Test &#";
     StringWriter sw = new StringWriter();
     try {
         unescaper.translate(input, 5, sw);
     } catch (StringIndexOutOfBoundsException e) {
         // expected in buggy version; nothing written
         assertEquals("", sw.toString());
         return;
     }
     // If no exception thrown (hypothetical fix), ensure no output and return 0
     assertEquals("", sw.toString());
     assertEquals(0, -1); // force failure to highlight that we expected an exception
 }

}