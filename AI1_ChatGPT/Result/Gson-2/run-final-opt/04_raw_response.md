public void testNewFactoryWithTypeTokenMatchesOnlyRequestedType() {
  com.google.gson.TypeAdapter<String> adapter = new com.google.gson.TypeAdapter<String>() {
    @Override
    public String read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
      return in.nextString();
    }

    @Override
    public void write(com.google.gson.stream.JsonWriter out, String value) throws java.io.IOException {
      out.value(value);
    }
  };
  com.google.gson.reflect.TypeToken<String> stringType =
      com.google.gson.reflect.TypeToken.get(String.class);
  com.google.gson.TypeAdapterFactory factory =
      com.google.gson.internal.bind.TypeAdapters.newFactory(stringType, adapter);

  junit.framework.Assert.assertSame(
      adapter, factory.create(new com.google.gson.Gson(), stringType));
  junit.framework.Assert.assertNull(factory.create(
      new com.google.gson.Gson(), com.google.gson.reflect.TypeToken.get(Integer.class)));
}

public void testConstructorThrowsUnsupportedOperationException() throws Exception {
  java.lang.reflect.Constructor<com.google.gson.internal.bind.TypeAdapters> constructor =
      com.google.gson.internal.bind.TypeAdapters.class.getDeclaredConstructor();
  boolean accessible = constructor.isAccessible();
  constructor.setAccessible(true);
  try {
    constructor.newInstance();
    junit.framework.Assert.fail("Expected TypeAdapters constructor to throw");
  } catch (java.lang.reflect.InvocationTargetException expected) {
    junit.framework.Assert.assertTrue(
        expected.getCause() instanceof UnsupportedOperationException);
  } finally {
    constructor.setAccessible(accessible);
  }
}