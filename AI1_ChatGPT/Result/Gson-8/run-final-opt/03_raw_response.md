package com.google.gson.internal;

import junit.framework.TestCase;

public class UnsafeAllocatorBug8Test extends TestCase {

  private interface SampleInterface {
  }

  private abstract static class SampleAbstractClass {
  }

  public void testInterfaceInstantiationThrowsUnsupportedOperationException() throws Exception {
    UnsafeAllocator allocator = UnsafeAllocator.create();

    try {
      allocator.newInstance(SampleInterface.class);
      fail("Allocating an interface should fail");
    } catch (Exception expected) {
      assertEquals(UnsupportedOperationException.class, expected.getClass());
    }
  }

  public void testAbstractClassInstantiationThrowsUnsupportedOperationException() throws Exception {
    UnsafeAllocator allocator = UnsafeAllocator.create();

    try {
      allocator.newInstance(SampleAbstractClass.class);
      fail("Allocating an abstract class should fail");
    } catch (Exception expected) {
      assertEquals(UnsupportedOperationException.class, expected.getClass());
    }
  }
}