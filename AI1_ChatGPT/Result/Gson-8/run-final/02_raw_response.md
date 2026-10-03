```java
package com.google.gson.internal;

import junit.framework.TestCase;

/**
 * Tests for {@link UnsafeAllocator}.
 */
public class UnsafeAllocatorGeneratedTest extends TestCase {

  public void testNewInstanceDoesNotInvokeConstructor() throws Exception {
    ConstructorTrackingType.constructorWasCalled = false;

    UnsafeAllocator allocator = UnsafeAllocator.create();
    ConstructorTrackingType instance = allocator.newInstance(ConstructorTrackingType.class);

    assertNotNull(instance);
    assertFalse("Unsafe allocation must bypass the constructor",
        ConstructorTrackingType.constructorWasCalled);
    assertEquals("Fields must retain their JVM default values when the constructor is bypassed",
        0, instance.value);
  }

  public void testNewInstanceRejectsInterfaceWithUnsupportedOperationException() throws Exception {
    assertUnsupportedInstantiation(AnInterface.class);
  }

  public void testNewInstanceRejectsAbstractClassWithUnsupportedOperationException() throws Exception {
    assertUnsupportedInstantiation(AnAbstractClass.class);
  }

  private void assertUnsupportedInstantiation(Class<?> type) throws Exception {
    UnsafeAllocator allocator = UnsafeAllocator.create();

    try {
      allocator.newInstance(type);
      fail("Expected UnsupportedOperationException when allocating " + type);
    } catch (Exception expected) {
      assertEquals("Interfaces and abstract classes must be rejected directly",
          UnsupportedOperationException.class, expected.getClass());
    }
  }

  private interface AnInterface {
  }

  private abstract static class AnAbstractClass {
  }

  public static class ConstructorTrackingType {
    static boolean constructorWasCalled;
    int value;

    public ConstructorTrackingType() {
      constructorWasCalled = true;
      value = 42;
    }
  }
}
```

- `testNewInstanceDoesNotInvokeConstructor` verifies the primary `UnsafeAllocator` contract: allocation creates an instance without executing its constructor, leaving instance fields at JVM default values.
- `testNewInstanceRejectsInterfaceWithUnsupportedOperationException` verifies that interfaces are rejected directly with `UnsupportedOperationException`, rather than allowing reflective allocation to produce an `InvocationTargetException`.
- `testNewInstanceRejectsAbstractClassWithUnsupportedOperationException` verifies the equivalent behavior for abstract classes, covering the second behavior identified by Gson bug 8.