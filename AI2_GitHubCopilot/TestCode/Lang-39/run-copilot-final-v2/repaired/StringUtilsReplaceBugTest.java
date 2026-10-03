package org.apache.commons.lang3;

 import static org.junit.Assert.*;
 import org.junit.Test;

 public class StringUtilsReplaceBugTest {

     private static final String TEXT = "The quick brown fox jumps over the lazy dog";

     // Test 1: null text should return null (commons-lang convention)
     @Test
     public void testReplaceNullText() {
         assertNull(StringUtils.replaceEach(
                 null,
                 new String[]{"a"},
                 new String[]{"X"}));
     }

     // Test 2: null searchList should return text unchanged (no-op)
     @Test
     public void testReplaceNullSearchList() {
         assertEquals(TEXT,
                 StringUtils.replaceEach(TEXT, (String[]) null, new String[]{"X"}));
     }

     // Test 3: null replacementList should return text unchanged (no-op)
     @Test
     public void testReplaceNullReplacementList() {
         assertEquals(TEXT,
                 StringUtils.replaceEach(TEXT, new String[]{"a"}, (String[]) null));
     }

     // Test 4: both arrays null should return text unchanged
     @Test
     public void testReplaceBothArraysNull() {
         assertEquals(TEXT,
                 StringUtils.replaceEach(TEXT, (String[]) null, (String[]) null));
     }

     // Test 5: empty search and replacement arrays should return text unchanged
     @Test
     public void testReplaceEmptyArrays() {
         assertEquals(TEXT,
                 StringUtils.replaceEach(TEXT,
                         new String[]{}, new String[]{}));
     }

     // Test 6: single-element normal replacement
     @Test
     public void testReplaceSingleElement() {
         assertEquals("The quick brown f0x jumps over the lazy d0g",
                 StringUtils.replaceEach(TEXT,
                         new String[]{"o"},
                         new String[]{"0"}));
     }

     // Test 7: multiple replacement pairs
     @Test
     public void testReplaceMultiplePairs() {
         assertEquals("Th$ qu1ck br0wn f0x jumps 0v$r th$ l1zy d0g",
                 StringUtils.replaceEach(TEXT,
                         new String[]{"e", "o", "a", "i"},
                         new String[]{"$", "0", "1", "1"}));
     }

     // Test 8: overlapping search strings - shorter replacements first
     @Test
     public void testReplaceOverlappingSearches() {
         assertEquals("XYZquXYZck",
                 StringUtils.replaceEach("The quick",
                         new String[]{"The", "i"},
                         new String[]{"XYZ", "XYZ"}));
     }

     // Test 9: no match found should return original text
     @Test
     public void testReplaceNoMatch() {
         assertEquals(TEXT,
                 StringUtils.replaceEach(TEXT,
                         new String[]{"xyz", "123"},
                         new String[]{"ABC", "DEF"}));
     }

     // Test 10: null element in searchList should not throw (skip that entry)
     @Test
     public void testReplaceNullElementInSearchList() {
         assertEquals(TEXT,
                 StringUtils.replaceEach(TEXT,
                         new String[]{null, "the"},
                         new String[]{"X", "Y"}));
     }

     // Test 11: replacement with empty string (deletion)
     @Test
     public void testReplaceWithEmptyString() {
         assertEquals("Th quck brown fox jumps ovr th lazy dog",
                 StringUtils.replaceEach(TEXT,
                         new String[]{"e", "e"},
                         new String[]{"", ""}));
     }

     // Test 12: unequal array lengths - extra search strings treated as
     //           matching themselves (no replacement) per replaceEach contract
     @Test
     public void testReplaceUnequalArrayLengths() {
         String result = StringUtils.replaceEach("abc",
                 new String[]{"a", "b", "c"},
                 new String[]{"1"});
         // Extra search strings "b" and "c" beyond replacement length get
         // replaced with themselves (no change). Only "a" -> "1".
         assertEquals("1bc", result);
     }
 }
