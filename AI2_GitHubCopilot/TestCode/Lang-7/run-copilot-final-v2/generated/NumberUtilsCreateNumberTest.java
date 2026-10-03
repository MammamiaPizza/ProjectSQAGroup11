package org.apache.commons.lang3.math;

 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * JUnit tests for {@link NumberUtils#createNumber(String)} covering
  * LANG-822 (hex empty body must throw NumberFormatException) and
  * additional normal, boundary and invalid inputs.
  */
 public class NumberUtilsCreateNumberTest {

     @Test(expected = NumberFormatException.class)
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
         assertEquals(Long.valueOf(0L), NumberUtils.createNumber("0x0"));
     }

     @Test
     public void testCreateNumberHexValid() {
         assertEquals(Long.valueOf(26L), NumberUtils.createNumber("0x1A"));
         assertEquals(Long.valueOf(255L), NumberUtils.createNumber("0XFF"));
     }

     @Test
     public void testCreateNumberIntegerAndDouble() {
         assertEquals(Integer.valueOf(123), NumberUtils.createNumber("123"));
         assertEquals(Integer.valueOf(-456), NumberUtils.createNumber("-456"));
         assertEquals(Double.valueOf(-0.5), NumberUtils.createNumber("-0.5"));
         assertEquals(Double.valueOf(2.5), NumberUtils.createNumber("2.5"));
     }
 }
