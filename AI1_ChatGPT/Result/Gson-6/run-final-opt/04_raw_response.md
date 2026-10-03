public void testCreateReturnsNullForTypeWithoutJsonAdapter() {
  com.google.gson.internal.bind.JsonAdapterAnnotationTypeAdapterFactory factory =
      new com.google.gson.internal.bind.JsonAdapterAnnotationTypeAdapterFactory(
          new com.google.gson.internal.ConstructorConstructor(
              java.util.Collections.<java.lang.reflect.Type, com.google.gson.InstanceCreator<?>>emptyMap()));

  com.google.gson.TypeAdapter<java.lang.String> adapter = factory.create(
      new com.google.gson.Gson(),
      com.google.gson.reflect.TypeToken.get(java.lang.String.class));

  assertNull(adapter);
}

public void testJsonAdapterWithNullSafeDisabledHandlesNullValues() {
  com.google.gson.Gson gson = new com.google.gson.Gson();

  assertEquals("\"adapter-null\"", gson.toJson(null, NullSafeDisabledValue.class));

  NullSafeDisabledValue value = gson.fromJson("null", NullSafeDisabledValue.class);
  assertEquals("adapter-null", value.value);
}

@com.google.gson.annotations.JsonAdapter(
    value = NullSafeDisabledValueAdapter.class,
    nullSafe = false)
public static final class NullSafeDisabledValue {
  final java.lang.String value;

  NullSafeDisabledValue(java.lang.String value) {
    this.value = value;
  }
}

public static final class NullSafeDisabledValueAdapter
    extends com.google.gson.TypeAdapter<NullSafeDisabledValue> {
  public void write(com.google.gson.stream.JsonWriter out, NullSafeDisabledValue value)
      throws java.io.IOException {
    if (value == null) {
      out.value("adapter-null");
    } else {
      out.value(value.value);
    }
  }

  public NullSafeDisabledValue read(com.google.gson.stream.JsonReader in)
      throws java.io.IOException {
    if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
      in.nextNull();
      return new NullSafeDisabledValue("adapter-null");
    }
    return new NullSafeDisabledValue(in.nextString());
  }
}