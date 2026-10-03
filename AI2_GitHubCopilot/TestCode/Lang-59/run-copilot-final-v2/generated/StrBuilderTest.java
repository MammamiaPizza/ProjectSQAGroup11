package org.apache.commons.lang.text;

 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests targeting LANG-299 bug in StrBuilder: ArrayIndexOutOfBoundsException
  * in append/insert operations due to incorrect buffer capacity management.
  */
 public class StrBuilderTest {

     @Test(expected = ArrayIndexOutOfBoundsException.class)
     public void testAppendFixedWidthPadRightOverflow() {
         // Bug: when strLen >= width, the method writes strLen chars into a buffer
         // sized only for 'width' additional chars, causing AIOOBE.
         StrBuilder sb = new StrBuilder();
         sb.appendFixedWidthPadRight("hello", 3, ' ');
     }

     @Test
     public void testAppendFixedWidthPadRightPadSmaller() {
         // Normal case: strLen < width, padding fills the gap.
         StrBuilder sb = new StrBuilder();
         sb.appendFixedWidthPadRight("hi", 5, '-');
         assertEquals("hi---", sb.toString());
     }

     @Test
     public void testAppendFixedWidthPadRightExactFit() {
         // Boundary: strLen == width, no padding needed, writes exactly width chars.
         StrBuilder sb = new StrBuilder();
         sb.appendFixedWidthPadRight("abc", 3, '_');
         assertEquals("abc", sb.toString());
     }

     @Test
     public void testAppendStringValidSubstring() {
         StrBuilder sb = new StrBuilder();
         sb.append("hello", 1, 3);
         assertEquals("ell", sb.toString());
     }

     @Test
     public void testAppendStringZeroLength() {
         StrBuilder sb = new StrBuilder("test");
         sb.append("hello", 2, 0);
         assertEquals("test", sb.toString());
     }

     @Test(expected = StringIndexOutOfBoundsException.class)
     public void testAppendStringNegativeStartIndex() {
         new StrBuilder().append("hello", -1, 3);
     }

     @Test(expected = StringIndexOutOfBoundsException.class)
     public void testAppendStringNegativeLength() {
         new StrBuilder().append("hello", 1, -1);
     }

     @Test(expected = StringIndexOutOfBoundsException.class)
     public void testAppendStringStartPlusLengthExceedsSource() {
         // startIndex + length > str.length() must throw
         new StrBuilder().append("hello", 1, 5);
     }

     @Test
     public void testAppendAfterBufferResize() {
         // Force buffer resize by appending beyond initial capacity.
         StrBuilder sb = new StrBuilder(5);
         sb.append("12345");
         sb.append("67890");
         assertEquals("1234567890", sb.toString());
     }

     @Test(expected = StringIndexOutOfBoundsException.class)
     public void testInsertCharArrayInvalidOffset() {
         new StrBuilder().insert(0, new char[] { 'a', 'b' }, -1, 1);
     }

     @Test(expected = StringIndexOutOfBoundsException.class)
     public void testInsertCharArrayInvalidLength() {
         new StrBuilder().insert(0, new char[] { 'a', 'b' }, 0, 3);
     }

     @Test
     public void testAppendStringBufferValidSubstring() {
         StrBuilder sb = new StrBuilder();
         StringBuffer buf = new StringBuffer("hello");
         sb.append(buf, 1, 3);
         assertEquals("ell", sb.toString());
     }
 }
