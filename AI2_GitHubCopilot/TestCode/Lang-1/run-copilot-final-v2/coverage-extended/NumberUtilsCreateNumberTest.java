package org.apache.commons.lang3.math;

import org.junit.Test;
import static org.junit.Assert.*;

public class NumberUtilsCreateNumberTest {

 @Test
 public void testCreateNumber_80000000() {
     Number result = NumberUtils.createNumber("80000000");
     assertNotNull("Result should not be null", result);
     assertTrue("Result should be Integer", result instanceof Integer);
     assertEquals(80000000, ((Integer) result).intValue());
 }

 @Test
 public void testCreateNumber_2147483648() {
     Number result = NumberUtils.createNumber("2147483648");
     assertNotNull(result);
     assertTrue("Result should be Long", result instanceof Long);
     assertEquals(2147483648L, ((Long) result).longValue());
 }

 @Test
 public void testCreateNumber_LongMaxValue() {
     Number result = NumberUtils.createNumber("9223372036854775807");
     assertNotNull(result);
     assertTrue("Result should be Long", result instanceof Long);
     assertEquals(Long.MAX_VALUE, ((Long) result).longValue());
 }

 @Test
 public void testCreateNumber_LongMinValue() {
     Number result = NumberUtils.createNumber("-9223372036854775808");
     assertNotNull(result);
     assertTrue("Result should be Long", result instanceof Long);
     assertEquals(Long.MIN_VALUE, ((Long) result).longValue());
 }

 @Test
 public void testCreateNumber_Zero() {
     Number result = NumberUtils.createNumber("0");
     assertNotNull(result);
     assertTrue("Result should be Integer", result instanceof Integer);
     assertEquals(0, ((Integer) result).intValue());
 }

 @Test
 public void testCreateNumber_NegativeOne() {
     Number result = NumberUtils.createNumber("-1");
     assertNotNull(result);
     assertTrue("Result should be Integer", result instanceof Integer);
     assertEquals(-1, ((Integer) result).intValue());
 }

 @Test
 public void testCreateNumber_HexPrefix_0x1A() {
     Number result = NumberUtils.createNumber("0x1A");
     assertNotNull(result);
     assertTrue("Result should be Integer", result instanceof Integer);
     assertEquals(26, ((Integer) result).intValue());
 }

 @Test
 public void testCreateNumber_HexPrefix_0x80000000() {
     Number result = NumberUtils.createNumber("0x80000000");
     assertNotNull(result);
     assertTrue("Result should be Long", result instanceof Long);
     assertEquals(2147483648L, ((Long) result).longValue());
 }

 @Test
 public void testCreateNumber_HexPrefix_0xA() {
     Number result = NumberUtils.createNumber("0xA");
     assertNotNull(result);
     assertTrue("Result should be Integer", result instanceof Integer);
     assertEquals(10, ((Integer) result).intValue());
 }

 @Test(expected = NumberFormatException.class)
 public void testCreateNumber_InvalidHexLike_1A_Throws() {
     NumberUtils.createNumber("1A");
 }

 @Test(expected = NumberFormatException.class)
 public void testCreateNumber_Invalid_0A_Throws() {
     NumberUtils.createNumber("0A");
 }

 @Test(expected = NumberFormatException.class)
 public void testCreateNumber_LeadingZeros_00080000000() {
     NumberUtils.createNumber("00080000000");
 }
}
