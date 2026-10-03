@Test
public void constructorRejectsNullWriter() {
  try {
    new JsonWriter(null);
    fail("Expected null writer to be rejected");
  } catch (NullPointerException expected) {
    assertEquals("out == null", expected.getMessage());
  }
}

@Test
public void objectWithMultipleNamesAndNestedArrayIsWritten() throws Exception {
  java.io.StringWriter output = new java.io.StringWriter();
  JsonWriter writer = new JsonWriter(output);

  writer.beginObject();
  writer.name("first").value(1L);
  writer.name("values").beginArray();
  writer.value(true);
  writer.nullValue();
  writer.endArray();
  writer.endObject();

  assertEquals("{\"first\":1,\"values\":[true,null]}", output.toString());
}

@Test
public void closeRejectsIncompleteDocument() throws Exception {
  JsonWriter writer = new JsonWriter(new java.io.StringWriter());
  writer.beginArray();

  try {
    writer.close();
    fail("Expected incomplete document to be rejected");
  } catch (java.io.IOException expected) {
    assertEquals("Incomplete document", expected.getMessage());
  }
}

@Test
public void multipleTopLevelValuesRequireLenientMode() throws Exception {
  java.io.StringWriter strictOutput = new java.io.StringWriter();
  JsonWriter strictWriter = new JsonWriter(strictOutput);
  strictWriter.value("first");

  try {
    strictWriter.value("second");
    fail("Expected multiple top-level values to be rejected");
  } catch (IllegalStateException expected) {
    assertEquals("JSON must have only one top-level value.", expected.getMessage());
  }

  java.io.StringWriter lenientOutput = new java.io.StringWriter();
  JsonWriter lenientWriter = new JsonWriter(lenientOutput);
  lenientWriter.setLenient(true);
  lenientWriter.value("first");
  lenientWriter.value("second");

  assertEquals("\"first\"\"second\"", lenientOutput.toString());
}