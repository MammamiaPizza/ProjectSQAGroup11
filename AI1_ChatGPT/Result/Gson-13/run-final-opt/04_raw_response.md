@org.junit.Test(expected = NullPointerException.class)
public void constructorRejectsNullReader() {
  new com.google.gson.stream.JsonReader(null);
}

@org.junit.Test
public void beginStructureMethodsRejectMismatchedTokens() throws java.io.IOException {
  com.google.gson.stream.JsonReader objectReader =
      new com.google.gson.stream.JsonReader(new java.io.StringReader("{}"));
  try {
    objectReader.beginArray();
    org.junit.Assert.fail("Expected beginArray to reject an object");
  } catch (IllegalStateException expected) {
  }

  com.google.gson.stream.JsonReader arrayReader =
      new com.google.gson.stream.JsonReader(new java.io.StringReader("[]"));
  try {
    arrayReader.beginObject();
    org.junit.Assert.fail("Expected beginObject to reject an array");
  } catch (IllegalStateException expected) {
  }
}

@org.junit.Test
public void lenientReaderConsumesNonExecutePrefix() throws java.io.IOException {
  com.google.gson.stream.JsonReader reader =
      new com.google.gson.stream.JsonReader(new java.io.StringReader(")]}'\n0"));
  reader.setLenient(true);

  org.junit.Assert.assertEquals("0", reader.nextString());
  org.junit.Assert.assertEquals(com.google.gson.stream.JsonToken.END_DOCUMENT, reader.peek());
}

@org.junit.Test
public void strictReaderRejectsWrongClosingDelimiters() throws java.io.IOException {
  com.google.gson.stream.JsonReader arrayReader =
      new com.google.gson.stream.JsonReader(new java.io.StringReader("[1}"));
  arrayReader.beginArray();
  org.junit.Assert.assertEquals(1, arrayReader.nextInt());
  try {
    arrayReader.endArray();
    org.junit.Assert.fail("Expected wrong array delimiter to be rejected");
  } catch (java.io.IOException expected) {
  }

  com.google.gson.stream.JsonReader objectReader =
      new com.google.gson.stream.JsonReader(new java.io.StringReader("{\"a\":1]"));
  objectReader.beginObject();
  org.junit.Assert.assertEquals("a", objectReader.nextName());
  org.junit.Assert.assertEquals(1, objectReader.nextInt());
  try {
    objectReader.endObject();
    org.junit.Assert.fail("Expected wrong object delimiter to be rejected");
  } catch (java.io.IOException expected) {
  }
}