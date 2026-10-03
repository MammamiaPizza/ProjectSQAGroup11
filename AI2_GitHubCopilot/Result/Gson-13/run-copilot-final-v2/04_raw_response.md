@Test
public void testNegativeZeroInArray() throws IOException {
    JsonReader reader = new JsonReader(new java.io.StringReader("[-0]"));
    reader.beginArray();
    assertTrue(reader.hasNext());
    double d = reader.nextDouble();
    assertEquals("Negative zero in array must be -0.0", 0x8000000000000000L,
Double.doubleToRawLongBits(d));
    assertFalse(reader.hasNext());
    reader.endArray();
}

@Test
public void testNegativeZeroInObject() throws IOException {
    JsonReader reader = new JsonReader(new java.io.StringReader("{"k":-0}"));
    reader.beginObject();
    assertTrue(reader.hasNext());
    assertEquals("k", reader.nextName());
    double d = reader.nextDouble();
    assertEquals("Negative zero in object must be -0.0", 0x8000000000000000L,
Double.doubleToRawLongBits(d));
    assertFalse(reader.hasNext());
    reader.endObject();
}

@Test
public void testClose() throws IOException {
    JsonReader reader = new JsonReader(new java.io.StringReader("{}"));
    reader.beginObject();
    reader.endObject();
    reader.close();
    try {
        reader.peek();
        fail("Expected IllegalStateException after close");
    } catch (IllegalStateException expected) {
    }
}

@Test
public void testStrictRejectsSingleQuotedValue() throws IOException {
    JsonReader reader = new JsonReader(new java.io.StringReader("{"a":'b'}"));
    reader.beginObject();
    reader.nextName();
    try {
        reader.nextString();
        fail("Expected IOException for single-quoted value in strict mode");
    } catch (IOException expected) {
    }
}