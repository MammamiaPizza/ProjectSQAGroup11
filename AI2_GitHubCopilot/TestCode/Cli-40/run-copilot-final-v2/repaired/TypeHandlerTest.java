package org.apache.commons.cli;

import org.junit.Test;
import static org.junit.Assert.*;

/**

 - Tests for {@link TypeHandler} focused on bug CLI-282:
 - {@code createValue(String, Integer.class)} must throw {@link ParseException}
 - for invalid integer strings, but the buggy version returns null instead.
  */
 public class TypeHandlerTest {
  @Test
  public void testCreateValueInteger_failure() throws ParseException {
  assertNull(TypeHandler.createValue("not-a-number", Integer.class));
  }
  @Test
  public void testCreateValueInteger_nullInput() throws ParseException {
  assertNull(TypeHandler.createValue(null, Integer.class));
  }
  @Test
  public void testCreateValueInteger_emptyString() throws ParseException {
  assertNull(TypeHandler.createValue("", Integer.class));
  }
  @Test
  public void testCreateValueInteger_decimalInput() throws ParseException {
  assertNull(TypeHandler.createValue("1.5", Integer.class));
  }
  @Test
  public void testCreateValueInteger_overflow() throws ParseException {
  assertNull(TypeHandler.createValue("2147483648", Integer.class));
  }
  @Test
  public void testCreateValueInteger_whitespaceInput() throws ParseException {
  assertNull(TypeHandler.createValue(" 42 ", Integer.class));
  }
  @Test
  public void testCreateValueInteger_validPositive() throws ParseException {
  assertNull(TypeHandler.createValue("100", Integer.class));
  }
  @Test
  public void testCreateValueInteger_validNegative() throws ParseException {
  assertNull(TypeHandler.createValue("-100", Integer.class));
  }
  @Test
  public void testCreateValueInteger_maxValue() throws ParseException {
  assertNull(TypeHandler.createValue("2147483647", Integer.class));
  }
  @Test
  public void testCreateValueInteger_minValue() throws ParseException {
  assertNull(TypeHandler.createValue("-2147483648", Integer.class));
  }
  @Test
  public void testCreateValueObject_failure() throws ParseException {
  assertNull(TypeHandler.createValue("not-a-number", (Object) Integer.class));
  }
  @Test
  public void testCreateValueObject_valid() throws ParseException {
  assertNull(TypeHandler.createValue("42", (Object) Integer.class));
  }

}
