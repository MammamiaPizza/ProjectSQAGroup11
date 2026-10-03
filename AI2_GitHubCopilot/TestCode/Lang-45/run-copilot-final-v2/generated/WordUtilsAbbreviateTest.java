package org.apache.commons.lang;

 import static org.junit.Assert.*;
 import org.junit.Test;

 /**
  * Tests for {@link WordUtils#abbreviate(String, int, int, String)} focusing on
  * the bug reported in LANG-419 (StringIndexOutOfBoundsException) and
  * boundary / normal cases.
  */
 public class WordUtilsAbbreviateTest {

     private static final String APPEND = "...";
     private static final String LONG_STR = "Now is the time for all good men";
     private static final String MEDIUM_STR = "abc def ghi jkl mno...";
     private static final String NO_SPACE = "abcdefghij";

     // ---------- null / empty ----------

     @Test
     public void testAbbreviateNullString() {
         assertNull(WordUtils.abbreviate(null, 0, 10, APPEND));
     }

     @Test
     public void testAbbreviateEmptyString() {
         assertEquals("", WordUtils.abbreviate("", 0, 10, APPEND));
     }

     // ---------- parameter adjustments ----------

     @Test
     public void testAbbreviateLowerGreaterThanUpper() {
         // lower > upper is adjusted: upper = lower (no abbreviation if lower >= length)
         String result = WordUtils.abbreviate("short", 20, 10, APPEND);
         assertNotNull(result);
         // upper set to lower=20 > length => upper set to length? ambiguous, but no exception
     }

     @Test
     public void testAbbreviateUpperLessThanLower() {
         // upper < lower → upper raised to lower
         String result = WordUtils.abbreviate("short", 3, 1, APPEND);
         assertNotNull(result);
     }

     @Test
     public void testAbbreviateLowerEqualToUpper() {
         // abbreviation must happen at exactly that length; no word boundary → cut at upper
         String result = WordUtils.abbreviate(MEDIUM_STR, 10, 10, APPEND);
         assertNotNull(result);
         assertEquals(13, result.length()); // 10 + "...".length()
     }

     @Test
     public void testAbbreviateNegativeLower() {
         // lower negative should not cause StringIndexOutOfBoundsException
         String result = WordUtils.abbreviate(MEDIUM_STR, -1, 15, APPEND);
         assertNotNull(result);
     }

     // ---------- abbreviation occurs ----------

     @Test
     public void testAbbreviateUpperEqualToStrLength() {
         // upper equals str length → no abbreviation, no append
         assertEquals(LONG_STR,
                      WordUtils.abbreviate(LONG_STR, 5, LENG_STR.length(), APPEND));
     }

     @Test
     public void testAbbreviateUpperExceedsStrLength() {
         // upper > length → set to length → no abbreviation
         assertEquals(LONG_STR,
                      WordUtils.abbreviate(LONG_STR, 5, 999, APPEND));
     }

     @Test
     public void testAbbreviateWordBoundaryInsideRange() {
         // word boundary within [lower, upper] → break there
         String result = WordUtils.abbreviate("Hello world", 3, 10, APPEND);
         assertNotNull(result);
         assertEquals("Hello ...", result);
     }

     @Test
     public void testAbbreviateNoWordBoundaryInRange() {
         // no space in range → cut at upper and append
         String result = WordUtils.abbreviate(NO_SPACE, 3, 5, APPEND);
         assertNotNull(result);
         assertEquals("abcde...", result);
     }

     @Test
     public void testAbbreviateLowerZero() {
         // lower = 0, first space is used if inside upper; otherwise cut at upper
         String result = WordUtils.abbreviate("a b c d e f", 0, 5, APPEND);
         assertNotNull(result);
         // first space at index 1, which is <= upper 5 → break at ' '
         assertTrue(result.startsWith("a "));
         assertTrue(result.endsWith(APPEND));
     }

     // ---------- append handling ----------

     @Test
     public void testAbbreviateAppendNull() {
         // append null is treated as empty string
         String result = WordUtils.abbreviate(LONG_STR, 5, 20, null);
         assertNotNull(result);
         assertFalse(result.endsWith(APPEND));
         assertTrue(result.length() <= 20);
     }

     // ---------- bug reproduction: LANG-419 ----------

     @Test
     public void testAbbreviateBugLANG419() {
         // This input triggers StringIndexOutOfBoundsException in the buggy
         // version when lower=4, upper=15, append="...".
         // Expected: no exception, result "abc def...".
         String result = WordUtils.abbreviate(MEDIUM_STR, 4, 15, APPEND);
         assertNotNull("Exception expected", result);
         assertEquals("abc def...", result);
     }
 }
