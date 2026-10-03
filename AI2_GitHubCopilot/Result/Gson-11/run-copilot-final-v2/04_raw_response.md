@org.junit.Test
    public void testTypeAdaptersPrivateConstructorThrowsUnsupportedOperationException() throws
Exception {
        java.lang.reflect.Constructor constructor =
                com.google.gson.internal.bind.TypeAdapters.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
            org.junit.Assert.fail("Expected UnsupportedOperationException");
        } catch (java.lang.reflect.InvocationTargetException e) {
            org.junit.Assert.assertTrue(
                    "Expected UnsupportedOperationException",
                    e.getCause() instanceof UnsupportedOperationException);
        }
    }

 @org.junit.Test
 public void testNewFactoryWithTypeTokenReturnsAdapterForMatchingType() {
     com.google.gson.reflect.TypeToken<java.lang.Boolean> booleanType =
             com.google.gson.reflect.TypeToken.get(java.lang.Boolean.class);
     com.google.gson.TypeAdapterFactory factory =
             com.google.gson.internal.bind.TypeAdapters.newFactory(
                     booleanType, com.google.gson.internal.bind.TypeAdapters.BOOLEAN_AS_STRING);
     org.junit.Assert.assertNotNull(factory.create(null, booleanType));
 }

 @org.junit.Test
 public void testNewFactoryWithTypeTokenReturnsNullForNonMatchingType() {
     com.google.gson.reflect.TypeToken<java.lang.Boolean> booleanType =
             com.google.gson.reflect.TypeToken.get(java.lang.Boolean.class);
     com.google.gson.TypeAdapterFactory factory =
             com.google.gson.internal.bind.TypeAdapters.newFactory(
                     booleanType, com.google.gson.internal.bind.TypeAdapters.BOOLEAN_AS_STRING);
     org.junit.Assert.assertNull(
             factory.create(null, com.google.gson.reflect.TypeToken.get(java.lang.String.class)));
 }