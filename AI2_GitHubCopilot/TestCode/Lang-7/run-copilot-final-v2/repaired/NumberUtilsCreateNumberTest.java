package org.apache.commons.lang3.math;

 import org.junit.Test;
 import static org.junit.Assert.*;

 public class NumberUtilsCreateNumberTest {

     @Test(expected = NullPointerException.class)
     public void testCreateNumberNull() {
         NumberUtils.createNumber(null);
     }

     @Test(expected = NumberFormatException.class)
     public void testCreateNumberBlank() {
         NumberUtils.createNumber("");
     }

     @Test(expected = NumberFormatException.class)
     public void testCreateNumberWhitespace() {
         NumberUtils.createNumber("   ");
     }

     @Test(expected = NumberFormatException.class)
     public void testCreateNumberNoNumber() {
         NumberUtils.createNumber("notANumber");
     }

     @Test(expected = NumberFormatException.class)
     public void testCreateNumberDoubleDash() {
         NumberUtils.createNumber("--1");
     }

     @Test(expected = NumberFormatException.class)
     public void testCreateNumberHexEmptyBody0x() {
         NumberUtils.createNumber("0x");
     }

     @Test(expected = NumberFormatException.class)
     public void testCreateNumberHexEmptyBody0X() {
         NumberUtils.createNumber("0X");
     }

     @Test(expected = NumberFormatException.class)
     public void testCreateNumberHexWithMinusAfterPrefix() {
         NumberUtils.createNumber("0x-1");
     }

     @Test(expected = NumberFormatException.class)
     public void testCreateNumberHexWithPlusPrefix() {
         NumberUtils.createNumber("+0x");
     }

     @Test
     public void testCreateNumberHexZero() {
         Number num = NumberUtils.createNumber("0x0");
         assertNotNull("Hex '0x0' must not be null", num);
         assertEquals(0L, num.longValue());
     }

     @Test
     public void testCreateNumberHexValid() {
         Number num1 = NumberUtils.createNumber("0x1A");
         assertNotNull(num1);
         assertEquals(26L, num1.longValue());
         Number num2 = NumberUtils.createNumber("0XFF");
         assertNotNull(num2);
         assertEquals(255L, num2.longValue());
     }

     @Test
     public void testCreateNumberIntegerAndDouble() {
         Number num1 = NumberUtils.createNumber("123");
         assertNotNull(num1);
         assertEquals(123, num1.intValue());
         Number num2 = NumberUtils.createNumber("-456");
         assertNotNull(num2);
         assertEquals(-456, num2.intValue());
         Number num3 = NumberUtils.createNumber("-0.5");
         assertNotNull(num3);
         assertEquals(-0.5, num3.doubleValue(), 1e-10);
         Number num4 = NumberUtils.createNumber("2.5");
         assertNotNull(num4);
         assertEquals(2.5, num4.doubleValue(), 1e-10);
     }
 }
