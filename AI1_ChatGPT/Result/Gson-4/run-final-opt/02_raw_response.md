package com.google.gson.stream;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import junit.framework.TestCase;

public class JsonStreamTopLevelValueTest extends TestCase {

  public void testReaderReadsTopLevelString() throws Exception {
    JsonReader reader = new JsonReader(new StringReader("\"hello\""));

    assertEquals(JsonToken.STRING, reader.peek());
    assertEquals("hello", reader.nextString());
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testReaderReadsTopLevelNumber() throws Exception {
    JsonReader reader = new JsonReader(new StringReader("42"));

    assertEquals(JsonToken.NUMBER, reader.peek());
    assertEquals(42L, reader.nextLong());
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testReaderReadsTopLevelBoolean() throws Exception {
    JsonReader reader = new JsonReader(new StringReader("true"));

    assertEquals(JsonToken.BOOLEAN, reader.peek());
    assertTrue(reader.nextBoolean());
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testReaderReadsTopLevelNull() throws Exception {
    JsonReader reader = new JsonReader(new StringReader("null"));

    assertEquals(JsonToken.NULL, reader.peek());
    reader.nextNull();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testReaderSkipsEachTopLevelScalar() throws Exception {
    assertSkippedToEnd("\"value\"");
    assertSkippedToEnd("7");
    assertSkippedToEnd("false");
    assertSkippedToEnd("null");
  }

  private void assertSkippedToEnd(String json) throws IOException {
    JsonReader reader = new JsonReader(new StringReader(json));
    reader.skipValue();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testWriterWritesTopLevelScalarValues() throws Exception {
    StringWriter stringOutput = new StringWriter();
    new JsonWriter(stringOutput).value("hello");
    assertEquals("\"hello\"", stringOutput.toString());

    StringWriter numberOutput = new StringWriter();
    new JsonWriter(numberOutput).value(42L);
    assertEquals("42", numberOutput.toString());

    StringWriter booleanOutput = new StringWriter();
    new JsonWriter(booleanOutput).value(false);
    assertEquals("false", booleanOutput.toString());

    StringWriter nullOutput = new StringWriter();
    new JsonWriter(nullOutput).nullValue();
    assertEquals("null", nullOutput.toString());
  }

  public void testWriterStillWritesTopLevelArrayAndObject() throws Exception {
    StringWriter arrayOutput = new StringWriter();
    JsonWriter arrayWriter = new JsonWriter(arrayOutput);
    arrayWriter.beginArray();
    arrayWriter.value("a");
    arrayWriter.value(2L);
    arrayWriter.endArray();
    assertEquals("[\"a\",2]", arrayOutput.toString());

    StringWriter objectOutput = new StringWriter();
    JsonWriter objectWriter = new JsonWriter(objectOutput);
    objectWriter.beginObject();
    objectWriter.name("enabled").value(true);
    objectWriter.endObject();
    assertEquals("{\"enabled\":true}", objectOutput.toString());
  }

  public void testWriterRejectsSecondTopLevelValueInStrictMode() throws Exception {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);
    writer.value("first");

    try {
      writer.value("second");
      fail("Expected a second top-level value to be rejected");
    } catch (IllegalStateException expected) {
      assertEquals("\"first\"", output.toString());
    }
  }

  public void testWriterCloseRejectsIncompleteDocument() throws Exception {
    JsonWriter writer = new JsonWriter(new StringWriter());
    writer.beginArray();

    try {
      writer.close();
      fail("Expected closing an incomplete document to fail");
    } catch (IOException expected) {
      assertNotNull(expected.getMessage());
    }
  }
}