package org.apache.commons.cli;

import junit.framework.TestCase;

/**

 - Regression tests for the cli-3 bug: TypeHandler.createNumber forwards to
 - NumberUtils and returns Float/Integer, while the public contract states that
 - decimal literals must be Double and integral literals must be Long.
  */
 public class TypeHandlerBug3Test extends TestCase {
  public void testCreateNumberDecimalReturnsDouble() {
  Number number = TypeHandler.createNumber("4.5");
  assertNotNull("4.5 should produce a Number", number);
  assertEquals(Double.class, number.getClass());
  assertEquals(4.5, number.doubleValue(), 0.0d);
  }
  public void testCreateNumberFractionalBoundaryReturnsDouble() {
  Number number = TypeHandler.createNumber("0.5");
  assertNotNull("0.5 should produce a Number", number);
  assertEquals(Double.class, number.getClass());
  assertEquals(0.5, number.doubleValue(), 0.0d);
  }
  public void testCreateNumberWholeDecimalReturnsDouble() {
  Number number = TypeHandler.createNumber("5.0");
  assertNotNull("5.0 should produce a Number", number);
  assertEquals(Double.class, number.getClass());
  assertEquals(5.0, number.doubleValue(), 0.0d);
  }
  public void testCreateNumberIntegralReturnsLong() {
  Number number = TypeHandler.createNumber("5");
  assertNotNull("5 should produce a Number", number);
  assertEquals(Long.class, number.getClass());
  assertEquals(5L, number.longValue());
  }
  public void testCreateNumberNegativeIntegralReturnsLong() {
  Number number = TypeHandler.createNumber("-7");
  assertNotNull("-7 should produce a Number", number);
  assertEquals(Long.class, number.getClass());
  assertEquals(-7L, number.longValue());
  }
  public void testCreateNumberZeroReturnsLong() {
  Number number = TypeHandler.createNumber("0");
  assertNotNull("0 should produce a Number", number);
  assertEquals(Long.class, number.getClass());
  assertEquals(0L, number.longValue());
  }
  public void testCreateNumberLargeIntegralReturnsLong() {
  Number number = TypeHandler.createNumber("2147483648");
  assertNotNull("2147483648 should produce a Number", number);
  assertEquals(Long.class, number.getClass());
  assertEquals(2147483648L, number.longValue());
  }
  public void testCreateNumberInvalidStringReturnsNull() {
  assertNull("A non-numeric string should produce null",
          TypeHandler.createNumber("not-a-number"));
  }
  public void testCreateValueNumberClassDecimalReturnsDouble() {
  Object value = TypeHandler.createValue("4.5", PatternOptionBuilder.NUMBER_VALUE);
  assertTrue("Expected a Number result", value instanceof Number);
  assertEquals(Double.class, value.getClass());
  assertEquals(4.5, ((Number) value).doubleValue(), 0.0d);
  }
  public void testCreateValueNumberClassIntegralReturnsLong() {
  Object value = TypeHandler.createValue("9", PatternOptionBuilder.NUMBER_VALUE);
  assertTrue("Expected a Number result", value instanceof Number);
  assertEquals(Long.class, value.getClass());
  assertEquals(9L, ((Number) value).longValue());
  }
  public void testCreateValueObjectOverloadReturnsDouble() {
  Object value = TypeHandler.createValue("2.5", (Object) PatternOptionBuilder.NUMBER_VALUE);
  assertTrue("Expected a Number result", value instanceof Number);
  assertEquals(Double.class, value.getClass());
  assertEquals(2.5, ((Number) value).doubleValue(), 0.0d);
  }
  public void testCreateValueUnknownClassReturnsNull() {
  assertNull("An unmatched target type should produce null",
          TypeHandler.createValue("anything", UnsupportedOperationException.class));
  }

}
