public void testBug4JsonReaderRejectsNullReader() {
  try {
    new com.google.gson.stream.JsonReader((java.io.Reader) null);
    fail();
  } catch (NullPointerException expected) {
    assertEquals("in == null", expected.getMessage());
  }
}

public void testBug4JsonReaderBeginsNestedArrayAndObject() throws Exception {
  com.google.gson.stream.JsonReader reader =
      new com.google.gson.stream.JsonReader(new java.io.StringReader("[{\"name\":\"value\"}]"));

  reader.beginArray();
  reader.beginObject();
  assertEquals("name", reader.nextName());
  assertEquals("value", reader.nextString());
  reader.endObject();
  assertFalse(reader.hasNext());
  reader.endArray();
  assertEquals(com.google.gson.stream.JsonToken.END_DOCUMENT, reader.peek());
}

public void testBug4JsonWriterRejectsNullName() throws Exception {
  com.google.gson.stream.JsonWriter writer =
      new com.google.gson.stream.JsonWriter(new java.io.StringWriter());

  try {
    writer.name(null);
    fail();
  } catch (NullPointerException expected) {
    assertEquals("name == null", expected.getMessage());
  }
}

public void testBug4JsonWriterRejectsSecondDeferredName() throws Exception {
  com.google.gson.stream.JsonWriter writer =
      new com.google.gson.stream.JsonWriter(new java.io.StringWriter());

  writer.beginObject();
  writer.name("first");
  try {
    writer.name("second");
    fail();
  } catch (IllegalStateException expected) {
  }
}