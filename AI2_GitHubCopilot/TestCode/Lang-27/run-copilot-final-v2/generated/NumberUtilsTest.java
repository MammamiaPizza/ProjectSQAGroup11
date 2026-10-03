package org.apache.commons.lang3.math;

 import static org.junit.Assert.*;
 import org.junit.Test;

 public class NumberUtilsTest {

     @Test(expected = NumberFormatException.class)
     public void testCreateNumberNull() {
         NumberUtils.createNumber(null);
     }

     @Test(expected = NumberFormatException.class)
     public void testCreateNumberEmptyString() {
         NumberUtils.createNumber("");
     }

     @Test
     public void testCreateNumberValidHex() {
         Number num = NumberUtils.createNumber("0x1F");
         assertNotNull(num);
         assertEquals(Integer.valueOf(31), num);
     }

     @Test
     public void testCreateNumberValidNegativeHex() {
         Number num = NumberUtils.createNumber("-0xAB");
         assertNotNull(num);
         assertEquals(Integer.valueOf(-171), num);
     }

     @Test
     public void testCreateNumberValidHexUpperCase() {
         Number num = NumberUtils.createNumber("0XFF");
         assertNotNull(num);
         assertEquals(Integer.valueOf(255), num);
     }

     @Test
     public void testCreateNumberValidOctal() {
         Number num = NumberUtils.createNumber("0177");
         assertNotNull(num);
         assertEquals(Integer.valueOf(127), num);
     }

     @Test
     public void testCreateNumberValidZero() {
         Number num = NumberUtils.createNumber("0");
         assertNotNull(num);
         assertEquals(Integer.valueOf(0), num);
     }

     @Test
     public void testCreateNumberValidDecimal() {
         Number num = NumberUtils.createNumber("123");
         assertNotNull(num);
         assertEquals(Integer.valueOf(123), num);
     }

     @Test
     public void testCreateNumberValidHexMaxLong() {
         Number num = NumberUtils.createNumber("0x7FFFFFFFFFFFFFFF");
         assertNotNull(num);
         assertEquals(Long.valueOf(Long.MAX_VALUE), num);
     }

     @Test
     public void testCreateNumberValidFloatSuffix() {
         Number num = NumberUtils.createNumber("1.5f");
         assertNotNull(num);
         assertEquals(new Float(1.5f), num);
     }

     @Test
     public void testCreateNumberValidLongSuffix() {
         Number num = NumberUtils.createNumber("1L");
         assertNotNull(num);
         assertEquals(Long.valueOf(1L), num);
     }

     @Test(expected = NumberFormatException.class)
     public void testCreateNumberInvalidHexPrefixVariants() {
         String[] invalid = { "0x", "0X", "-0x", "+0x", "0x-", "0x+", "0xG", "0x " };
         for (String str : invalid) {
             NumberUtils.createNumber(str);
         }
     }
 }
