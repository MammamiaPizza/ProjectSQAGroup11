public void testJsonTreeWriterRejectsNaNBeforeLenientMode() throws java.lang.Exception {
  com.google.gson.internal.bind.JsonTreeWriter writer =
      new com.google.gson.internal.bind.JsonTreeWriter();

  try {
    writer.value(new java.lang.Double(java.lang.Double.NaN));
    fail();
  } catch (java.lang.IllegalArgumentException expected) {
  }

  writer.setLenient(true);
  writer.value(new java.lang.Double(java.lang.Double.NaN));

  assertTrue(java.lang.Double.isNaN(writer.get().getAsDouble()));
}

public void testJsonTreeWriterRejectsEndingArrayWithoutAnOpenArray() throws java.lang.Exception {
  com.google.gson.internal.bind.JsonTreeWriter writer =
      new com.google.gson.internal.bind.JsonTreeWriter();

  try {
    writer.endArray();
    fail();
  } catch (java.lang.IllegalStateException expected) {
  }
}

public void testClassTypeAdapterRejectsClassSerialization() throws java.lang.Exception {
  com.google.gson.stream.JsonWriter writer =
      new com.google.gson.stream.JsonWriter(new java.io.StringWriter());

  try {
    com.google.gson.internal.bind.TypeAdapters.CLASS.write(writer, java.lang.String.class);
    fail();
  } catch (java.lang.UnsupportedOperationException expected) {
  }
}