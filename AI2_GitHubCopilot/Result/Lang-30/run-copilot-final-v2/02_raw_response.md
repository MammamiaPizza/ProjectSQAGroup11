package org.apache.commons.lang3;

 import static org.junit.Assert.*;
 import org.junit.Test;

 /**
  * Tests for supplementary character handling in {@link StringUtils} methods
  * that accept search character sets: containsNone, containsAny, indexOfAny,
  * indexOfAnyBut (char[] and String overloads).
  * <p>
  * The bug (LANG-607) causes supplementary characters to be treated as two
  * separate surrogate characters, leading to false positive/negative matches
  * and incorrect index values.
  * </p>
  */
 public class StringUtilsSupplementaryTest {

     // A supplementary code point used in tests
     private static final int SUPP_CP = 0x1F600; // Grinning Face
     private static final String SUPP_STR = new String(Character.toChars(SUPP_CP));
     private static final char SUPP_HIGH = Character.highSurrogate(SUPP_CP);
     private static final char SUPP_LOW  = Character.lowSurrogate(SUPP_CP);
     private static final char[] SUPP_ARR = SUPP_STR.toCharArray();

     // A different supplementary code point
     private static final int SUPP2_CP = 0x1F601; // Grinning Face with Smiling Eyes
     private static final String SUPP2_STR = new String(Character.toChars(SUPP2_CP));
     private static final char[] SUPP2_ARR = SUPP2_STR.toCharArray();

     // Unpaired surrogates (bad surrogates)
     private static final String BAD_HIGH = "\uD800";
     private static final String BAD_LOW = "\uDC00";

     // ========== containsNone : char[] ==========

     @Test
     public void testContainsNoneCharArraySupplementaryNotFound() {
         // Supplementary char not present in cs
         assertTrue(StringUtils.containsNone("hello world", SUPP_ARR));
         // cs contains another supplementary, but not the search char
         assertTrue(StringUtils.containsNone("x" + SUPP2_STR + "y", SUPP_ARR));
         // cs contains a lone high surrogate matching the search supplementary
         assertTrue(StringUtils.containsNone("x" + SUPP_HIGH + "y", SUPP_ARR));
         // cs contains a lone low surrogate matching the search supplementary
         assertTrue(StringUtils.containsNone("x" + SUPP_LOW + "y", SUPP_ARR));
     }

     @Test
     public void testContainsNoneCharArraySupplementaryFound() {
         // Supplementary char present once
         assertFalse(StringUtils.containsNone("abc" + SUPP_STR + "def", SUPP_ARR));
         // Supplementary char present multiple times
         assertFalse(StringUtils.containsNone(SUPP_STR + " " + SUPP_STR, SUPP_ARR));
     }

     // ========== containsNone : String ==========

     @Test
     public void testContainsNoneStringSupplementaryNotFound() {
         assertTrue(StringUtils.containsNone("plain text", SUPP_STR));
         assertTrue(StringUtils.containsNone("x" + SUPP2_STR + "y", SUPP_STR));
         // Bad high surrogate alone
         assertTrue(StringUtils.containsNone("x" + SUPP_HIGH + "y", SUPP_STR));
         // Unpaired low surrogate alone
         assertTrue(StringUtils.containsNone("x" + SUPP_LOW + "y", SUPP_STR));
     }

     @Test
     public void testContainsNoneStringSupplementaryFound() {
         assertFalse(StringUtils.containsNone("a" + SUPP_STR + "b", SUPP_STR));
         assertFalse(StringUtils.containsNone(SUPP_STR + SUPP_STR, SUPP_STR));
     }

     // ========== containsAny : char[] ==========

     @Test
     public void testContainsAnyCharArraySupplementaryFound() {
         assertTrue(StringUtils.containsAny("ab" + SUPP_STR + "cd", SUPP_ARR));
         assertTrue(StringUtils.containsAny(SUPP_STR, SUPP_ARR));
     }

     @Test
     public void testContainsAnyCharArraySupplementaryNotFound() {
         // No supplementary char in cs
         assertFalse(StringUtils.containsAny("plain", SUPP_ARR));
         // cs contains another supplementary, not the search char
         assertFalse(StringUtils.containsAny("x" + SUPP2_STR + "y", SUPP_ARR));
         // Bad high surrogate alone – should NOT match the full supplementary
         assertFalse(StringUtils.containsAny("a" + SUPP_HIGH + "b", SUPP_ARR));
         // Bad low surrogate alone
         assertFalse(StringUtils.containsAny("a" + SUPP_LOW + "b", SUPP_ARR));
     }

     // ========== containsAny : String ==========

     @Test
     public void testContainsAnyStringSupplementaryFound() {
         assertTrue(StringUtils.containsAny("ab" + SUPP_STR + "cd", SUPP_STR));
         assertTrue(StringUtils.containsAny(SUPP_STR, SUPP_STR));
     }

     @Test
     public void testContainsAnyStringSupplementaryNotFound() {
         assertFalse(StringUtils.containsAny("plain", SUPP_STR));
         assertFalse(StringUtils.containsAny("x" + SUPP2_STR + "y", SUPP_STR));
         assertFalse(StringUtils.containsAny("a" + SUPP_HIGH + "b", SUPP_STR));
         assertFalse(StringUtils.containsAny("a" + SUPP_LOW + "b", SUPP_STR));
     }

     // ========== indexOfAny : char[] ==========

     @Test
     public void testIndexOfAnyCharArraySupplementary() {
         // Supplementary present at a known position (char index)
         assertEquals(2, StringUtils.indexOfAny("ab" + SUPP_STR + "cd", SUPP_ARR));
         // Not present
         assertEquals(-1, StringUtils.indexOfAny("abcd", SUPP_ARR));
         // Bad high surrogate only must not match the supplementary character
         assertEquals(-1, StringUtils.indexOfAny("x" + SUPP_HIGH + "z", SUPP_ARR));
         // Bad low surrogate only
         assertEquals(-1, StringUtils.indexOfAny("x" + SUPP_LOW + "z", SUPP_ARR));
     }

     // ========== indexOfAny : String ==========

     @Test
     public void testIndexOfAnyStringSupplementary() {
         assertEquals(2, StringUtils.indexOfAny("ab" + SUPP_STR + "cd", SUPP_STR));
         assertEquals(-1, StringUtils.indexOfAny("abcd", SUPP_STR));
         assertEquals(-1, StringUtils.indexOfAny("x" + SUPP_HIGH + "z", SUPP_STR));
         assertEquals(-1, StringUtils.indexOfAny("x" + SUPP_LOW + "z", SUPP_STR));
     }

     // ========== indexOfAnyBut : char[] ==========

     @Test
     public void testIndexOfAnyButCharArraySupplementary() {
         // All chars are in the search set (including supplementary) -> -1
         String allValid = "abc" + SUPP_STR + "xyz";
         char[] allValidArr = (allValid + "extra").toCharArray(); // include all
         assertEquals(-1, StringUtils.indexOfAnyBut(allValid, allValidArr));

         // First char not in set is a supplementary char
         String cs1 = "ab" + SUPP_STR;
         char[] set1 = "ab".toCharArray(); // only 'a','b' are valid
         assertEquals(2, StringUtils.indexOfAnyBut(cs1, set1)); // SUPP at index 2

         // A bad high surrogate should be considered NOT in set even if the set
         // contains the full supplementary (as two surrogate chars)
         String cs2 = "a" + SUPP_HIGH + "b";
         char[] set2 = ("ab" + SUPP_STR).toCharArray();
         assertEquals(1, StringUtils.indexOfAnyBut(cs2, set2)); // index of bad high

         // A bad low surrogate similarly
         String cs3 = "a" + SUPP_LOW + "b";
         assertEquals(1, StringUtils.indexOfAnyBut(cs3, set2)); // index of bad low
     }

     // ========== indexOfAnyBut : String ==========

     @Test
     public void testIndexOfAnyButStringSupplementary() {
         // All chars valid
         String allValid = "abc" + SUPP_STR + "xyz";
         assertEquals(-1, StringUtils.indexOfAnyBut(allValid, allValid + "extra"));

         // Supplementary char is not in set
         assertEquals(2, StringUtils.indexOfAnyBut("ab" + SUPP_STR, "ab"));

         // Bad high surrogate not in set (even if set contains full supplementary)
         assertEquals(1, StringUtils.indexOfAnyBut("a" + SUPP_HIGH + "b", "ab" + SUPP_STR));

         // Bad low surrogate
         assertEquals(1, StringUtils.indexOfAnyBut("a" + SUPP_LOW + "b", "ab" + SUPP_STR));
     }
 }