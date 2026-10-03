package com.fasterxml.jackson.databind.type;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.junit.Test;

/**

 - Tests targeting the bug in deprecated construct methods of CollectionType, MapType, and
SimpleType.
  */
 public class TypeConstructBug38Test {
  private void assertRejects(Class<?> cls, String expectedMsg) {
  try {
      SimpleType.construct(cls);
      fail("Expected IllegalArgumentException for " + cls);
  } catch (IllegalArgumentException e) {
      assertTrue("Message does not contain expected: " + e.getMessage(),
              e.getMessage().contains(expectedMsg));
  }
  }
  @Test
  public void testSimpleTypeConstructRejectsContainerClasses() {
  assertRejects(HashMap.class, "Can not construct SimpleType for a Map");
  assertRejects(ArrayList.class, "Can not construct SimpleType for a Collection");
  assertRejects(int[].class, "Can not construct SimpleType for an array");
  }
  @Test
  public void testSimpleTypeConstructAcceptsValidClass() {
  JavaType t = SimpleType.construct(String.class);
  assertNotNull(t);
  assertEquals(String.class, t.getRawClass());
  }
  @Test
  public void testCollectionTypeConstructPreservesElementType() {
  JavaType elemType = SimpleType.constructUnsafe(Integer.class);
  CollectionType ct = CollectionType.construct(ArrayList.class, elemType);
  assertNotNull("Content type must not be null", ct.getContentType());
  assertEquals(Integer.class, ct.getContentType().getRawClass());
  }
  @Test
  public void testCollectionTypeConstructNullElementType() {
  CollectionType ct = CollectionType.construct(ArrayList.class, null);
  assertNull(ct.getContentType());
  }
  @Test
  public void testMapTypeConstructNullKeyType() {
  JavaType valType = SimpleType.constructUnsafe(String.class);
  MapType mt = MapType.construct(HashMap.class, null, valType);
  assertNull(mt.getKeyType());
  assertEquals(String.class, mt.getContentType().getRawClass());
  }
  @Test
  public void testCollectionTypeWithStaticTyping() {
  CollectionType ct = CollectionType.construct(
          ArrayList.class, SimpleType.constructUnsafe(String.class));
  CollectionType ct1 = ct.withStaticTyping();
  assertNotSame(ct, ct1);
  CollectionType ct2 = ct1.withStaticTyping();
  assertSame(ct1, ct2);
  }
  @Test
  public void testMapTypeWithStaticTyping() {
  MapType mt = MapType.construct(
          HashMap.class, SimpleType.constructUnsafe(String.class),
          SimpleType.constructUnsafe(Integer.class));
  MapType mt1 = mt.withStaticTyping();
  assertNotSame(mt, mt1);
  MapType mt2 = mt1.withStaticTyping();
  assertSame(mt1, mt2);
  }
  @Test
  public void testCollectionTypeSuperClassNotObject() {
  CollectionType ct = CollectionType.construct(
          ArrayList.class, SimpleType.constructUnsafe(String.class));
  JavaType superType = ct.getSuperClass();
  assertNotNull("Superclass should not be null", superType);
  assertNotEquals("Superclass should not be Object",
          Object.class, superType.getRawClass());
  }
  @Test
  public void testMapTypeSuperClassNotObject() {
  MapType mt = MapType.construct(
          LinkedHashMap.class, SimpleType.constructUnsafe(String.class),
          SimpleType.constructUnsafe(Integer.class));
  JavaType superType = mt.getSuperClass();
  assertNotNull("Superclass should not be null", superType);
  assertNotEquals("Superclass should not be Object",
          Object.class, superType.getRawClass());
  }
  @Test
  public void testCollectionTypeNarrowSameClass() {
  CollectionType ct = CollectionType.construct(
          ArrayList.class, SimpleType.constructUnsafe(String.class));
  JavaType narrowed = ct._narrow(ArrayList.class);
  assertTrue(narrowed instanceof CollectionType);
  assertEquals(ArrayList.class, narrowed.getRawClass());
  }
  @Test
  public void testMapTypeNarrowSameClass() {
  MapType mt = MapType.construct(
          HashMap.class, SimpleType.constructUnsafe(String.class),
          SimpleType.constructUnsafe(Integer.class));
  JavaType narrowed = mt._narrow(HashMap.class);
  assertTrue(narrowed instanceof MapType);
  assertEquals(HashMap.class, narrowed.getRawClass());
  }
  @Test
  public void testSimpleTypeNarrowSameClassReturnsSelf() {
  SimpleType st = SimpleType.constructUnsafe(String.class);
  JavaType narrowed = st._narrow(String.class);
  assertSame(st, narrowed);
  }

}
