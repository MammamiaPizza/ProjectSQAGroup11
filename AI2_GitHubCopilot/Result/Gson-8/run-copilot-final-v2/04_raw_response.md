public void testFallbackAllocatorExceptionMessageForInterface() {
        try {
            UnsafeAllocator.create().newInstance(Interface.class);
            fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            String msg = e.getMessage();
            assertTrue(msg.contains("Cannot allocate"));
            assertTrue(msg.contains(Interface.class.getName()));
        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }
    }

 public void testAllocatorWithoutUnsafe() throws Exception {
     try {
         java.lang.reflect.Field unsafeField =
Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");
         unsafeField.setAccessible(true);
         Object original = unsafeField.get(null);
         unsafeField.set(null, null);
         try {
             UnsafeAllocator allocator = UnsafeAllocator.create();
             Object instance = allocator.newInstance(ConcreteClass.class);
             assertNotNull(instance);
             assertTrue(instance instanceof ConcreteClass);
         } finally {
             unsafeField.set(null, original);
         }
     } catch (ClassNotFoundException e) {
         // sun.misc.Unsafe not available; path cannot be tested
     }
 }