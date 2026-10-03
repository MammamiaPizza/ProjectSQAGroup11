@Test
public void nestedObjectArrayPathsAndScalarValuesAreTracked() throws java.io.IOException {
  com.google.gson.JsonArray values = new com.google.gson.JsonArray();
  values.add(new com.google.gson.JsonPrimitive(true));
  values.add(com.google.gson.JsonNull.INSTANCE);
  values.add(new com.google.gson.JsonPrimitive("2.5"));
  values.add(new com.google.gson.JsonPrimitive(4));

  com.google.gson.JsonObject object = new com.google.gson.JsonObject();
  object.add("items", values);

  JsonTreeReader reader = new JsonTreeReader(object);
  reader.beginObject();
  assertEquals("items", reader.nextName());
  assertEquals("$.items", reader.getPath());

  reader.beginArray();
  assertEquals("$.items[0]", reader.getPath());
  assertTrue(reader.nextBoolean());
  assertEquals("$.items[1]", reader.getPath());
  reader.nextNull();
  assertEquals("$.items[2]", reader.getPath());
  assertEquals(Double.valueOf(2.5), Double.valueOf(reader.nextDouble()));
  assertEquals(4, reader.nextInt());

  reader.endArray();
  assertFalse(reader.hasNext());
  reader.endObject();
  assertEquals(com.google.gson.stream.JsonToken.END_DOCUMENT, reader.peek());
}

@Test
public void promoteNameToValueMakesObjectNameReadableAsString() throws java.io.IOException {
  com.google.gson.JsonObject object = new com.google.gson.JsonObject();
  object.addProperty("first", "value");

  JsonTreeReader reader = new JsonTreeReader(object);
  reader.beginObject();
  reader.promoteNameToValue();

  assertEquals(com.google.gson.stream.JsonToken.STRING, reader.peek());
  assertEquals("first", reader.nextString());
  assertEquals("value", reader.nextString());
  assertFalse(reader.hasNext());

  reader.endObject();
  assertEquals(com.google.gson.stream.JsonToken.END_DOCUMENT, reader.peek());
}

@Test(expected = IllegalStateException.class)
public void nextBooleanRejectsStringToken() throws java.io.IOException {
  JsonTreeReader reader = new JsonTreeReader(new com.google.gson.JsonPrimitive("true"));
  reader.nextBoolean();
}