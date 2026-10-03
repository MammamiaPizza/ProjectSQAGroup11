package com.fasterxml.jackson.databind.type;

import static org.junit.Assert.*;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;

import java.util.List;

/**

 - JUnit test that exposes JacksonDatabind bug #95:
 - NullPointerException when parsing canonical names that contain
 - wildcards or type variables (missing null checks on TypeBindings
 - in _fromWildcard / _fromVariable).
  */
 public class TestTypeParserBug95 {
  private final TypeFactory factory = TypeFactory.defaultInstance();
  private final TypeParser parser = new TypeParser(factory);
  @Test
  public void testParsePrimitives() {
  JavaType intType = parser.parse("int");
  assertNotNull(intType);
  assertEquals(int.class, intType.getRawClass());
  assertTrue(intType.isPrimitive());
  JavaType boolType = parser.parse("boolean");
  assertNotNull(boolType);
  assertEquals(boolean.class, boolType.getRawClass());
  assertTrue(boolType.isPrimitive();
  JavaType longType = parser.parse("long");
  assertNotNull(longType);
  assertEquals(long.class, longType.getRawClass());
  assertTrue(longType.isPrimitive());
  }
  @Test
  public void testParseSimpleClasses() {
  JavaType stringType = parser.parse("java.lang.String");
  assertNotNull(stringType);
  assertEquals(String.class, stringType.getRawClass());
  JavaType dateType = parser.parse("java.util.Date");
  assertNotNull(dateType);
  assertEquals(java.util.Date.class, dateType.getRawClass());
  }
  @Test
  public void testParseParameterizedList() {
  JavaType listType = parser.parse("java.util.List<java.lang.String>");
  assertNotNull(listType);
  assertEquals(List.class, listType.getRawClass());
  assertEquals(1, listType.containedTypeCount());
  JavaType param = listType.getBindings().getBoundType(0);
  assertNotNull(param);
  assertEquals(String.class, param.getRawClass());
  }
  @Test
  public void testParseUnboundedWildcardList() {
  JavaType type = parser.parse("java.util.List<?>");
  assertNotNull(type);
  assertEquals(List.class, type.getRawClass());
  assertEquals(1, type.containedTypeCount());
  JavaType param = type.getBindings().getBoundType(0);
  assertNotNull("Wildcard type parameter must not be null", param);
  assertEquals(Object.class, param.getRawClass());
  }
  @Test
  public void testParseUpperBoundedWildcard() {
  JavaType type = parser.parse("java.util.List<? extends java.lang.Number>");
  assertNotNull(type);
  assertEquals(List.class, type.getRawClass());
  assertEquals(1, type.containedTypeCount());
  JavaType param = type.getBindings().getBoundType(0);
  assertNotNull(param);
  }
  @Test
  public void testParseLowerBoundedWildcard() {
  JavaType type = parser.parse("java.util.List<? super java.lang.Integer>");
  assertNotNull(type);
  assertEquals(List.class, type.getRawClass());
  assertEquals(1, type.containedTypeCount());
  JavaType param = type.getBindings().getBoundType(0);
  assertNotNull(param);
  }
  @Test
  public void testParseSimpleArray() {
  JavaType type = parser.parse("java.lang.String[]");
  assertNotNull(type);
  assertTrue(type.isArrayType());
  assertEquals(String.class, type.getContentType().getRawClass());
  }
  @Test
  public void testParseMultiDimArray() {
  JavaType type = parser.parse("int[][]");
  assertNotNull(type);
  assertTrue(type.isArrayType());
  assertEquals(int.class, type.getContentType().getContentType().getRawClass());
  }
  @Test(expected = IllegalArgumentException.class)
  public void testParseInvalidEmptyString() {
  parser.parse("");
  }
  @Test(expected = IllegalArgumentException.class)
  public void testParseNonExistentClass() {
  parser.parse("com.nonexistent.Foo");
  }
  @Test(expected = IllegalArgumentException.class)
  public void testParseInvalidStandaloneWildcard() {
  // standalone wildcard is not a valid type; should throw IAE, not NPE
  parser.parse("? extends java.lang.Number");
  }
  @Test
  public void testParseTypeEqualsFactoryConstructed() {
  JavaType parsed = parser.parse("java.util.List<java.lang.String>");
  JavaType constructed = factory.constructType(new TypeReference<List<String>>() {});
  assertEquals(constructed, parsed);
  }

}
