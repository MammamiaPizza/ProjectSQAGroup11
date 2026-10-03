package org.apache.commons.lang.text;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StrBuilderLang412Test {

 @Test
 public void testLang412Left() {
     StrBuilder sb = new StrBuilder();
     sb.appendFixedWidthPadLeft(null, 10, '*');
     assertEquals("**********", sb.toString());
 }

 @Test
 public void testLang412Right() {
     StrBuilder sb = new StrBuilder();
     sb.appendFixedWidthPadRight(null, 10, '*');
     assertEquals("**********", sb.toString());
 }

 @Test
 public void testNullLeftIgnoresZeroWidth() {
     StrBuilder sb = new StrBuilder();
     sb.appendFixedWidthPadLeft(null, 0, '*');
     assertEquals("", sb.toString());
 }

 @Test
 public void testNullRightIgnoresNegativeWidth() {
     StrBuilder sb = new StrBuilder();
     sb.appendFixedWidthPadRight(null, -3, '*');
     assertEquals("", sb.toString());
 }

 @Test
 public void testLeftWidthEqualsStringLength() {
     StrBuilder sb = new StrBuilder();
     sb.appendFixedWidthPadLeft("abc", 3, '*');
     assertEquals("abc", sb.toString());
 }

 @Test
 public void testRightWidthEqualsStringLength() {
     StrBuilder sb = new StrBuilder();
     sb.appendFixedWidthPadRight("abc", 3, '*');
     assertEquals("abc", sb.toString());
 }

 @Test
 public void testLeftPadsShorterString() {
     StrBuilder sb = new StrBuilder();
     sb.appendFixedWidthPadLeft("abc", 5, '*');
     assertEquals("**abc", sb.toString());
 }

 @Test
 public void testRightPadsShorterString() {
     StrBuilder sb = new StrBuilder();
     sb.appendFixedWidthPadRight("abc", 5, '*');
     assertEquals("abc**", sb.toString());
 }

 @Test
 public void testLeftAppendsIntoExistingBuilderContent() {
     StrBuilder sb = new StrBuilder("x");
     sb.appendFixedWidthPadLeft("ab", 4, '-');
     assertEquals("x--ab", sb.toString());
 }

 @Test
 public void testRightAppendsIntoExistingBuilderContent() {
     StrBuilder sb = new StrBuilder("x");
     sb.appendFixedWidthPadRight("ab", 4, '-');
     assertEquals("xab--", sb.toString());
 }

 @Test
 public void testSetNullTextToNullStillPadsEmpty() {
     StrBuilder sb = new StrBuilder();
     sb.setNullText(null);
     sb.appendFixedWidthPadLeft(null, 3, '*');
     assertEquals("***", sb.toString());
 }

 @Test
 public void testConfiguredNullTextIsPadded() {
     StrBuilder sb = new StrBuilder();
     sb.setNullText("N");
     sb.appendFixedWidthPadLeft(null, 3, '*');
     assertEquals("**N", sb.toString());

     sb.clear();
     sb.appendFixedWidthPadRight(null, 4, '-');
     assertEquals("N---", sb.toString());
 }

}