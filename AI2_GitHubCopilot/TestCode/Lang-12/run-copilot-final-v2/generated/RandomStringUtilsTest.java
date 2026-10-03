package org.apache.commons.lang3;

 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for {@link RandomStringUtils} targeting bug LANG-805.
  * All tests are independent and deterministic.
  */
 public class RandomStringUtilsTest {

     @Test
     public void testLANG805_emptyCharArray() {
         // Calling random(int, char...) with an empty array exposed AIOOBE in the buggy version.
         // After fix it must not throw, must return a string of the requested length.
         int count = 5;
         String s = RandomStringUtils.random(count, new char[0]);
         assertNotNull("Result must not be null", s);
         assertEquals("Length must equal requested count", count, s.length());
         // Characters are unspecified but must be valid (no assertion on content beyond not
crashing).
     }

     @Test
     public void testNullCharArray() {
         int count = 3;
         String s = RandomStringUtils.random(count, (char[]) null);
         assertEquals("Length must equal count", count, s.length());
     }

     @Test
     public void testNullString() {
         int count = 3;
         String s = RandomStringUtils.random(count, (String) null);
         assertEquals("Length must equal count", count, s.length());
     }

     @Test(expected = IllegalArgumentException.class)
     public void testNegativeCountThrows() {
         RandomStringUtils.random(-1);
     }

     @Test
     public void testZeroCountReturnsEmpty() {
         // With no chars
         assertEquals("", RandomStringUtils.random(0));
         // With specific chars array
         assertEquals("", RandomStringUtils.random(0, 'a', 'b'));
         // With a string
         assertEquals("", RandomStringUtils.random(0, "abc"));
     }

     @Test
     public void testSingleChar() {
         int count = 10;
         String s = RandomStringUtils.random(count, 'A');
         assertEquals("Length must equal count", count, s.length());
         for (char c : s.toCharArray()) {
             assertEquals("Only the supplied char must appear", 'A', c);
         }
     }

     @Test
     public void testStringChars() {
         int count = 7;
         String allowed = "xyz";
         String s = RandomStringUtils.random(count, allowed);
         assertEquals("Length must equal count", count, s.length());
         for (char c : s.toCharArray()) {
             assertTrue("Character must be from allowed string",
                     allowed.indexOf(c) >= 0);
         }
     }

     @Test
     public void testStartEndRange() {
         int count = 4;
         int start = 65; // 'A'
         int end = 90;   // 'Z' exclusive, so up to 'Y' actually: end=90 -> range [65,89] = A-Y
         String s = RandomStringUtils.random(count, start, end, false, false);
         assertEquals("Length must equal count", count, s.length());
         for (char c : s.toCharArray()) {
             assertTrue("Character must be within [" + start + "," + (end - 1) + "]",
                     c >= start && c < end);
         }
     }

     @Test(expected = IllegalArgumentException.class)
     public void testStartGreaterThanEndThrows() {
         RandomStringUtils.random(5, 100, 50, false, false);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testStartEqualsEndThrows() {
         RandomStringUtils.random(5, 42, 42, false, false);
     }

     @Test
     public void testCharsWithInvalidStartEnd() {
         // The method signature (int,int,int,boolean,boolean,char...) may be called with
         // start/end unrelated to the chars array. The implementation should not
         // throw ArrayIndexOutOfBoundsException.
         int count = 4;
         String s = RandomStringUtils.random(count, 100, 200, false, false, 'A');
         assertEquals("Length must equal count", count, s.length());
         for (char c : s.toCharArray()) {
             assertEquals("Only the supplied chars must appear", 'A', c);
         }
     }

     @Test
     public void testLargeCountWithSmallSet() {
         int count = 100;
         String s = RandomStringUtils.random(count, 'X', 'Y');
         assertEquals("Length must equal count", count, s.length());
         for (char c : s.toCharArray()) {
             assertTrue("Character must be X or Y", c == 'X' || c == 'Y');
         }
     }
 }
