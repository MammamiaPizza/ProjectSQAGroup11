@org.junit.Test
public void typeAdapterFactoryCreatedForTypeTokenMatchesOnlyThatExactType() {
  com.google.gson.reflect.TypeToken<java.lang.String> stringType =
      new com.google.gson.reflect.TypeToken<java.lang.String>() {};
  com.google.gson.TypeAdapterFactory factory =
      com.google.gson.internal.bind.TypeAdapters.newFactory(
          stringType, com.google.gson.internal.bind.TypeAdapters.STRING);

  org.junit.Assert.assertSame(
      com.google.gson.internal.bind.TypeAdapters.STRING,
      factory.create(new com.google.gson.Gson(), stringType));
  org.junit.Assert.assertNull(
      factory.create(
          new com.google.gson.Gson(),
          new com.google.gson.reflect.TypeToken<java.lang.Integer>() {}));
}

@org.junit.Test
public void typeAdaptersConstructorRejectsInstantiation() throws java.lang.Exception {
  java.lang.reflect.Constructor<com.google.gson.internal.bind.TypeAdapters> constructor =
      com.google.gson.internal.bind.TypeAdapters.class.getDeclaredConstructor();
  constructor.setAccessible(true);

  try {
    constructor.newInstance();
    org.junit.Assert.fail("Expected constructor to reject instantiation");
  } catch (java.lang.reflect.InvocationTargetException expected) {
    org.junit.Assert.assertTrue(
        expected.getCause() instanceof java.lang.UnsupportedOperationException);
  }
}