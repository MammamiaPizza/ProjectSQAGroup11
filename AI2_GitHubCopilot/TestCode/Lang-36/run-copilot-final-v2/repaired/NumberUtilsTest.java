package org.apache.commons.lang3.math;

 import static org.junit.Assert.*;
 import org.junit.Test;

 public class NumberUtilsTest {

     @Test(expected = NumberFormatException.class)
     public void testCreateNumberTrailingDecimal() {
         NumberUtils.createNumber("2.");
     }

     @Test
     public void testCreateNumberLeadingDecimal() {
         Number n = NumberUtils.createNumber(".2");
         assertNotNull(n);
         assertEquals(0.2, n.doubleValue(), 0.0001);
     }

     @Test(expected = NumberFormatException.class)
     public void testCreateNumberNegativeTrailing() {
         NumberUtils.createNumber("-3.");
     }

     @Test
     public void testCreateNumberZeroVariations() {
         try {
             NumberUtils.createNumber("0.");
             fail("Expected NumberFormatException");
         } catch (NumberFormatException e) { }
         assertEquals(0.0, NumberUtils.createNumber(".0").doubleValue(), 0.0001);
     }

     @Test(expected = NumberFormatException.class)
     public void testCreateNumberMultiDigitTrailing() {
         NumberUtils.createNumber("123.");
     }

     @Test
     public void testCreateNumberNormalDecimal() {
         assertEquals(2.5, NumberUtils.createNumber("2.5").doubleValue(), 0.0001);
         assertEquals(2.0, NumberUtils.createNumber("2.0").doubleValue(), 0.0001);
     }

     @Test
     public void testCreateNumberNull() {
         assertNull(NumberUtils.createNumber(null));
     }

     @Test
     public void testIsNumberTrailingDecimal() {
         assertFalse(NumberUtils.isNumber("2."));
         assertFalse(NumberUtils.isNumber("0."));
         assertFalse(NumberUtils.isNumber("-3."));
         assertFalse(NumberUtils.isNumber("123."));
     }

     @Test
     public void testIsNumberLeadingDecimal() {
         assertTrue(NumberUtils.isNumber(".2"));
         assertTrue(NumberUtils.isNumber(".0"));
     }

     @Test
     public void testIsNumberStandardValues() {
         assertTrue(NumberUtils.isNumber("2.0"));
         assertTrue(NumberUtils.isNumber("2.5"));
         assertTrue(NumberUtils.isNumber("2"));
         assertTrue(NumberUtils.isNumber("-1"));
     }

     @Test
     public void testIsNumberInvalidInputs() {
         assertFalse(NumberUtils.isNumber("2..3"));
         assertFalse(NumberUtils.isNumber("."));
         assertFalse(NumberUtils.isNumber(""));
     }

     @Test
     public void testIsNumberNull() {
         assertFalse(NumberUtils.isNumber(null));
     }
 }
