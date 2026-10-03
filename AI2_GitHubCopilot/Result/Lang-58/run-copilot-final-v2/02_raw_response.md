package org.apache.commons.lang.math;

 import static org.junit.Assert.*;

 import org.junit.Test;

 /**
  * Tests for LANG-300 bug: NumberUtils.createNumber("1l") throws NumberFormatException
  * instead of returning Long(1L). Also tests related isNumber behavior and common edge cases.
  */
 public class NumberUtilsLang300Test {

     // ====== Normal: lower‑case 'l' suffix ============================================

     @Test
     public void testCreateNumberWithLowercaseL() {
         assertEquals(Long.valueOf(1L), NumberUtils.createNumber("1l"));
         assertEquals(Long.valueOf(2L), NumberUtils.createNumber("2l"));
         assertEquals(Long.valueOf(0L), NumberUtils.createNumber("0l"));
     }

     @Test
     public void testCreateNumberWithNegativeLowercaseL() {
         assertEquals(Long.valueOf(-1L), NumberUtils.createNumber("-1l"));
     }

     @Test
     public void testCreateNumberWithMultiDigitLowercaseL() {
         assertEquals(Long.valueOf(123L), NumberUtils.createNumber("123l"));
     }

     // ====== Boundry: large values that fit long but not int ======================

     @Test
     public void testCreateNumberWithLowercaseLAboveIntegerMax() {
         assertEquals(Long.valueOf(2147483648L), NumberUtils.createNumber("2147483648l"));
     }

     // ====== Uppper‑case 'L' suffix (regression guard) ==========================----

     @Test
     public void testCreateNumberWithUppercaseL() {
         assertEquals(Long.valueOf(1L), NumberUtils.createNumber("1L"));
     }

     // ====== isNumber with lower‑case 'l' ======================================

     @Test
     public void testIsNumberTrueForLowercaseL() {
         assertTrue(NumberUtils.isNumber("1l"));
         assertTrue(NumberUtils.isNumber("2l"));
         assertTrue(NumberUtils.isNumber("0l"));
         assertTrue(NumberUtils.isNumber("-1l"));
     }

     @Test
     public void testIsNumberTrueForUppercaseL() {
         assertTrue(NumberUtils.isNumber("1L"));
     }

     // ====== isNumber false for invalid combinations ==============================

     @Test
     public void testIsNumberFalseForInvalidSuffixCombinations() {
         assertFalse(NumberUtils.isNumber("1.0l"));   // decimal with long suffix
         assertFalse(NumberUtils.isNumber("1f"));      // float suffix when long expected
         assertFalse(NumberUtils.isNumber("l"));       // standalone 'l'
     }

     // ====== Exceptions: empty and malforematted strigs ==============================

     @Test(expected = NumberFormatException.class)
     public void testCreateNumberWithEmptyString() {
         NumberUtils.createNumber("");
     }

     @Test(expected = NumberFormatException.class)
     public void testCreateNumberWithDecimalAndLowercaseL() {
         NumberUtils.createNumber("1.0l");
     }

     @Test(expected = NumberFormatException.class)
     public void testCreateNumberWithFloatSuffixInLongContext() {
         NumberUtils.createNumber("1f");
     }

     // ====== createInteger regression ==============================================

     @Test
     public void testCreateIntegerValid() {
         assertEquals(Integer.valueOf(123), NumberUtils.createInteger("123"));
         assertEquals(Integer.valueOf(-1), NumberUtils.createInteger("-1"));
         assertEquals(Integer.valueOf(0), NumberUtils.createInteger("0"));
     }

     // ====== createLong regression ==============================================

     @Test
     public void testCreateLongValid() {
         assertEquals(Long.valueOf(123L), NumberUtils.createLong("123"));
         assertEquals(Long.valueOf(-1L), NumberUtils.createLong("-1"));
         assertEquals(Long.valueOf(0L), NumberUtils.createLong("0"));
     }

     // ====== Sanity: isNumber true ⇒ createNumber succeds ======================

     @Test
     public void testIsNumberImpliesCreateNumberSuccess() {
         // sample strings known to be valid
         String[] valid = {"1l", "1L", "123" };
         for (String str : valid) {
             if (NumberUtils.isNumber(str)) {
                 assertNotNull("createNumber must not return null for " + str,
                              NumberUtils.createNumber(str));
             }
         }
     }
 }