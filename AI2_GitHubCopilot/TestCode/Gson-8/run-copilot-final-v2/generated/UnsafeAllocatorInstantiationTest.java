package com.google.gson.internal;

import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

public class UnsafeAllocatorInstantiationTest extends TestCase {

  private interface Interface {}

  private static abstract class AbstractClass {}

  private static class ConcreteClass {}

  public void testInterfaceInstantiation() throws Exception {
    assertInvocationTargetException(Interface.class); }

  public void testAbstractClassInstantiation() throws Exception {
    assertInvocationTargetException(AbstractClass.class); }

  public void testConcreteInstantiation() throws Exception {
    Object instance = UnsafeAllocator.create().newInstance(ConcreteClass.class);
    assertNotNull(instance);
    assertEquals(ConcreteClass.class, instance.getClass()); }

  private void assertInvocationTargetException(Class<?> c) throws Exception {
    try {
      UnsafeAllocator.create().newInstance(c);
      fail("Allocation unexpectedly succeeded for " + c.getName());
    } catch (InvocationTargetException expected) {
      // expected failure for interfaces and abstract classes
    } catch (Exception unexpected) {
      fail("Expected InvocationTargetException but got " + unexpected.getClass().getName()
          + " for " + c.getName());
    } }
}
