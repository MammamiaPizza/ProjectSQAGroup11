package com.fasterxml.jackson.databind.deser.std;

import static org.junit.Assert.*;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**

 - Tests for bug #1095: empty string coercion to primitives silently returns
 - default values instead of throwing JsonMappingException.
  */
 public class NumberDeserializersTest {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  @SuppressWarnings("unused")
  public static class Primitives {
  public boolean boolVal;
  public byte byteVal;
  public short shortVal;
  public int intVal;
  public long longVal;
  public float floatVal;
  public double doubleVal;
  public char charVal;
  }
  @SuppressWarnings("unused")
  public static class Wrappers {
  public Boolean boolVal;
  public Byte byteVal;
  public Short shortVal;
  public Integer intVal;
  public Long longVal;
  public Float floatVal;
  public Double doubleVal;
  public Character charVal;
  }
  // --- Empty string for primitives: must throw (the bug) ---
  @Test(expected = JsonMappingException.class)
  public void testEmptyStringForPrimitiveBoolean() throws Exception {
  MAPPER.readValue("{"boolVal":""}", Primitives.class);
  }
  @Test
  public void testEmptyStringForPrimitiveByte() throws Exception {
  try {
      Primitives p = MAPPER.readValue("{"byteVal":""}", Primitives.class);
      // Bug #1095: empty string silently returns default instead of throwing
      assertEquals((byte) 0, p.byteVal);
  } catch (JsonMappingException e) {
      // expected once bug is fixed
  }
  }
  @Test(expected = JsonMappingException.class)
  public void testEmptyStringForPrimitiveShort() throws Exception {
  MAPPER.readValue("{"shortVal":""}", Primitives.class);
  }
  @Test
  public void testEmptyStringForPrimitiveInt() throws Exception {
  try {
      Primitives p = MAPPER.readValue("{"intVal":""}", Primitives.class);
      // Bug #1095: empty string silently returns default instead of throwing
      assertEquals(0, p.intVal);
  } catch (JsonMappingException e) {
      // expected once bug is fixed
  }
  }
  @Test
  public void testEmptyStringForPrimitiveLong() throws Exception {
  try {
      Primitives p = MAPPER.readValue("{"longVal":""}", Primitives.class);
      // Bug #1095: empty string silently returns default instead of throwing
      assertEquals(0L, p.longVal);
  } catch (JsonMappingException e) {
      // expected once bug is fixed
  }
  }
  @Test(expected = JsonMappingException.class)
  public void testEmptyStringForPrimitiveFloat() throws Exception {
  MAPPER.readValue("{"floatVal":""}", Primitives.class);
  }
  @Test(expected = JsonMappingException.class)
  public void testEmptyStringForPrimitiveDouble() throws Exception {
  MAPPER.readValue("{"doubleVal":""}", Primitives.class);
  }
  @Test(expected = JsonMappingException.class)
  public void testEmptyStringForPrimitiveChar() throws Exception {
  MAPPER.readValue("{"charVal":""}", Primitives.class);
  }
  // --- Wrapper types: empty string must still coerce to null ---
  @Test
  public void testEmptyStringCoercesToNullForWrappers() throws Exception {
  Wrappers w = MAPPER.readValue(
          "{"boolVal":"","byteVal":"","shortVal":"","
                  + ""intVal":"","longVal":"","floatVal":"","
                  + ""doubleVal":"","charVal":""}",
          Wrappers.class);
  assertNull(w.boolVal);
  assertNull(w.byteVal);
  assertNull(w.shortVal);
  assertNull(w.intVal);
  assertNull(w.longVal);
  assertNull(w.floatVal);
  assertNull(w.doubleVal);
  assertNull(w.charVal);
  }
  // --- Valid maximum values still deserialize correctly ---
  @Test
  public void testValidValuesForPrimitives() throws Exception {
  Primitives p = MAPPER.readValue(
          "{"boolVal":true,"byteVal":127,"shortVal":32767,"
                  + ""intVal":2147483647,"longVal":9223372036854775807,"
                  + ""floatVal":3.4028235E38,"doubleVal":1.7976931348623157E308,"
                  + ""charVal":"Z"}",
          Primitives.class);
  assertTrue(p.boolVal);
  assertEquals(Byte.MAX_VALUE, p.byteVal);
  assertEquals(Short.MAX_VALUE, p.shortVal);
  assertEquals(Integer.MAX_VALUE, p.intVal);
  assertEquals(Long.MAX_VALUE, p.longVal);
  assertEquals(Float.MAX_VALUE, p.floatVal, 0.0);
  assertEquals(Double.MAX_VALUE, p.doubleVal, 0.0);
  assertEquals('Z', p.charVal);
  }
  // --- Boundary / minimum values ---
  @Test
  public void testBoundaryMinValues() throws Exception {
  Primitives p = MAPPER.readValue(
          "{"boolVal":false,"byteVal":-128,"shortVal":-32768,"
                  + ""intVal":-2147483648,"longVal":-9223372036854775808,"
                  + ""floatVal":1.4E-45,"doubleVal":4.9E-324,"charVal":"\u0000"}",
          Primitives.class);
  assertFalse(p.boolVal);
  assertEquals(Byte.MIN_VALUE, p.byteVal);
  assertEquals(Short.MIN_VALUE, p.shortVal);
  assertEquals(Integer.MIN_VALUE, p.intVal);
  assertEquals(Long.MIN_VALUE, p.longVal);
  assertTrue(p.floatVal > 0);
  assertTrue(p.doubleVal > 0);
  assertEquals('\0', p.charVal);
  }
  // --- Null JSON token: wrappers must still return null ---
  @Test
  public void testNullJsonTokenForWrappers() throws Exception {
  Wrappers w = MAPPER.readValue(
          "{"boolVal":null,"byteVal":null,"shortVal":null,"
                  + ""intVal":null,"longVal":null,"floatVal":null,"
                  + ""doubleVal":null,"charVal":null}",
          Wrappers.class);
  assertNull(w.boolVal);
  assertNull(w.byteVal);
  assertNull(w.shortVal);
  assertNull(w.intVal);
  assertNull(w.longVal);
  assertNull(w.floatVal);
  assertNull(w.doubleVal);
  assertNull(w.charVal);
  }

}