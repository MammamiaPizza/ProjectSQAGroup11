package com.fasterxml.jackson.core.failing;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

/**

 - Tests that target overflow and coercion behavior in ParserBase / ParserMinimalBase,
 - exercising the bug described in Defects4J JacksonCore-24.
 - <p>
 - The key contract is that {@code getLongValue()} must succeed for numbers that fit in
 - a {@code long} even when they exceed the {@code int} range, while
 - {@code getIntValue()} must throw for numbers outside the {@code int} range.
  */
 public class TestParserBaseOverflow {
  // ----------------------------------------------------------------------
  // Helper
  // ----------------------------------------------------------------------
  private static JsonParser intParser(String content) throws IOException {
  JsonParser p = new JsonFactory().createParser(content.getBytes("UTF-8"));
  assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
  return p;
  }
  private static JsonParser floatParser(String content) throws IOException {
  JsonParser p = new JsonFactory().createParser(content.getBytes("UTF-8"));
  assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
  return p;
  }
  private static void assertToken(JsonToken expected, JsonToken actual) {
  assertEquals(expected, actual);
  }
  // ----------------------------------------------------------------------
  // Int coercions
  // ----------------------------------------------------------------------
  @Test
  public void testIntCoercionBasic() throws IOException {
  try (JsonParser p = intParser("42")) {
      assertEquals(42, p.getIntValue());
      assertEquals(42L, p.getLongValue());
  }
  }
  @Test
  public void testIntBoundaryMinMax() throws IOException {
  try (JsonParser p = intParser(String.valueOf(Integer.MIN_VALUE))) {
      assertEquals(Integer.MIN_VALUE, p.getIntValue());
      assertEquals((long) Integer.MIN_VALUE, p.getLongValue());
  }
  try (JsonParser p = intParser(String.valueOf(Integer.MAX_VALUE))) {
      assertEquals(Integer.MAX_VALUE, p.getIntValue());
      assertEquals((long) Integer.MAX_VALUE, p.getLongValue());
  }
  }
  @Test
  public void testIntOverflowThrows() throws IOException {
  try (JsonParser p = intParser("2147483648")) { // Integer.MAX_VALUE + 1
      try {
          p.getIntValue();
          fail("Expected JsonParseException for out-of-int value");
      } catch (JsonParseException e) {
          assertTrue(e.getMessage().contains("out of range"));
      }
  }
  }
  // ----------------------------------------------------------------------
  // Long coercions
  // ----------------------------------------------------------------------
  @Test
  public void testLongCoercionOutsideIntRange() throws IOException {
  // value within long range but outside int range
  try (JsonParser p = intParser("12345678907")) {
      // getLongValue must succeed (bug: it threw int-out-of-range)
      assertEquals(12345678907L, p.getLongValue());
      // getIntValue must fail
      try {
          p.getIntValue();
          fail("Expected JsonParseException for out-of-int value");
      } catch (JsonParseException e) {
          assertTrue(e.getMessage().contains("out of range"));
      }
  }
  }
  @Test
  public void testLongBoundaryMinMax() throws IOException {
  try (JsonParser p = intParser(String.valueOf(Long.MIN_VALUE))) {
      assertEquals(Long.MIN_VALUE, p.getLongValue());
  }
  try (JsonParser p = intParser(String.valueOf(Long.MAX_VALUE))) {
      assertEquals(Long.MAX_VALUE, p.getLongValue());
  }
  }
  @Test
  public void testLongOverflowThrows() throws IOException {
  // Long.MAX_VALUE + 1
  try (JsonParser p = intParser("9223372036854775808")) {
      try {
          p.getLongValue();
          fail("Expected JsonParseException for out-of-long value");
      } catch (JsonParseException e) {
          assertTrue(e.getMessage().contains("out of range"));
      }
  }
  }
  @Test
  public void testLargeDigitNumberOverflowMessage() throws IOException {
  // 2000-digit number – beyond even BigInteger-as-long representation
  char[] digits = new char[2000];
  Arrays.fill(digits, '0');
  digits[0] = '1';
  String huge = new String(digits);
  try (JsonParser p = intParser(huge)) {
      try {
          p.getLongValue();
          fail("Expected JsonParseException");
      } catch (JsonParseException e) {
          String msg = e.getMessage();
          // truncated description: "[Integer with 2000 digits]"
          assertTrue(msg.contains("[Integer with") && msg.contains("digits]"));
      }
  }
  }
  // ----------------------------------------------------------------------
  // Negative zero
  // ----------------------------------------------------------------------
  @Test
  public void testNegativeZeroCoercion() throws IOException {
  try (JsonParser p = intParser("-0")) {
      assertEquals(0, p.getIntValue());
      assertEquals(0L, p.getLongValue());
  }
  }
  // ----------------------------------------------------------------------
  // Float → int / long coercions
  // ----------------------------------------------------------------------
  @Test
  public void testFloatCoercionToIntOutOfRange() throws IOException {
  try (JsonParser p = floatParser("1e100")) {
      try {
          p.getIntValue();
          fail("Expected JsonParseException");
      } catch (JsonParseException e) {
          assertTrue(e.getMessage().contains("out of range"));
      }
  }
  }
  @Test
  public void testFloatCoercionToLongWithinRange() throws IOException {
  try (JsonParser p = floatParser("1.23e2")) { // 123.0
      assertEquals(123L, p.getLongValue());
  }
  }
  @Test
  public void testFloatCoercionToIntWithinRange() throws IOException {
  try (JsonParser p = floatParser("1.23e2")) {
      assertEquals(123, p.getIntValue());
  }
  }
  // ----------------------------------------------------------------------
  // Number type detection
  // ----------------------------------------------------------------------
  @Test
  public void testGetNumberTypeAfterCoercion() throws IOException {
  try (JsonParser p = intParser("42")) {
      assertEquals(com.fasterxml.jackson.core.JsonParser.NumberType.INT, p.getNumberType());
  }
  try (JsonParser p = intParser("12345678907")) {
      assertEquals(com.fasterxml.jackson.core.JsonParser.NumberType.LONG, p.getNumberType());
  }
  try (JsonParser p = intParser("9223372036854775808")) { // exceeds long
      assertEquals(com.fasterxml.jackson.core.JsonParser.NumberType.BIG_INTEGER, p.getNumberType());
  }
  }

}