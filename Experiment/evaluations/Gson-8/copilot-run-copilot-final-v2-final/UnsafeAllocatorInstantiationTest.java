package com.google.gson.internal;

 import junit.framework.TestCase;

 public class UnsafeAllocatorInstantiationTest extends TestCase {

   private interface Interface {}

   private static abstract class AbstractClass {}

   private static class ConcreteClass {}

   public void testInterfaceInstantiation() throws Exception {
     assertUnsupportedOperationException(Interface.class);
   }

   public void testAbstractClassInstantiation() throws Exception {
     assertUnsupportedOperationException(AbstractClass.class);
   }

   public void testConcreteInstantiation() throws Exception {
     Object instance = UnsafeAllocator.create().newInstance(ConcreteClass.class);
     assertNotNull(instance);
     assertEquals(ConcreteClass.class, instance.getClass());
   }

   private void assertUnsupportedOperationException(Class<?> c) throws Exception {
     try {
       UnsafeAllocator.create().newInstance(c);
       fail("Allocation unexpectedly succeeded for " + c.getName());
     } catch (UnsupportedOperationException expected) {
       // expected failure for interfaces and abstract classes
     } catch (Exception unexpected) {
       fail("Expected UnsupportedOperationException but got " + unexpected.getClass().getName()
           + " for " + c.getName());
     }
   }
 }
