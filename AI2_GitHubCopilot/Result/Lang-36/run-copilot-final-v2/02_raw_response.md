package org.apache.commons.lang3.math;

 import static org.junit.Assert.*;
 import org.junit.Test;

 public class NumberUtilsTest {

     @Test
     public void testCreateNumberTrailingDecimal() {
         Number n = NumberUtils.createNumber("2.");
         assertNotNull(n);
         assertTrue(n instanceof Double);
         assertEquals(2.0, n.doubleValue(), 0.0001);
     }

     @Test
     public void testCreateNumberLeadingDecimal() {
         Number n = NumberUtils.createNumber(".2");
         assertNotNull(n);
         assertEquals(0.2, n.doubleValue(), 0.0001);
     }

     @Test
     public void testCreateNumberNegativeTrailing() {
         assertEquals(-3.0, NumberUtils.createNumber("-3.").doubleValue(), 0.0001);
     }

     @Test
     public void testCreateNumberZeroVariations() {
         assertEquals(0.0, NumberUtils.createNumber("0.").doubleValue(), 0.0001);
         assertEquals(0.0, NumberUtils.createNumber(".0").doubleValue(), 0.0001);
     }

     @Test
     public void testCreateNumberMultiDigitTrailing() {
         assertEquals(123.0, NumberUtils.createNumber("123.").doubleValue(), 0.0001);
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
         assertTrue(NumberUtils.isNumber("2."));
         assertTrue(NumberUtils.isNumber("0."));
         assertTrue(NumberUtils.isNumber("-3."));
         assertTrue(NumberUtils.isNumber("123."));
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