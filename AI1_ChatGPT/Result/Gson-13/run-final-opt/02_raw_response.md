package com.google.gson.stream;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;

import org.junit.Test;

public class JsonReaderNegativeZeroTest {

  @Test
  public void negativeZeroIsReportedAsNumberAndPreservedAsString() throws Exception {
    JsonReader reader = new JsonReader(new StringReader("-0"));

    assertEquals(JsonToken.NUMBER, reader.peek());
    assertEquals("-0", reader.nextString());
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  @Test
  public void negativeZeroPreservesItsSignWhenReadAsDouble() throws Exception {
    JsonReader reader = new JsonReader(new StringReader("-0"));

    assertEquals(Double.doubleToRawLongBits(-0.0d),
        Double.doubleToRawLongBits(reader.nextDouble()));
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  @Test
  public void numericStringsInArrayRetainTheirOriginalNumberForms() throws Exception {
    JsonReader reader = new JsonReader(
        new StringReader("[-0,0,-17,-0.25,-0e+2]"));

    reader.beginArray();

    assertEquals(JsonToken.NUMBER, reader.peek());
    assertEquals("-0", reader.nextString());
    assertEquals("0", reader.nextString());
    assertEquals("-17", reader.nextString());
    assertEquals("-0.25", reader.nextString());
    assertEquals("-0e+2", reader.nextString());

    reader.endArray();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  @Test
  public void negativeZeroIsPreservedAsObjectMemberValue() throws Exception {
    JsonReader reader = new JsonReader(
        new StringReader("{\"negativeZero\":-0,\"following\":\"value\"}"));

    reader.beginObject();

    assertEquals("negativeZero", reader.nextName());
    assertEquals(JsonToken.NUMBER, reader.peek());
    assertEquals("-0", reader.nextString());

    assertEquals("following", reader.nextName());
    assertEquals("value", reader.nextString());

    reader.endObject();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  @Test(expected = MalformedJsonException.class)
  public void incompleteNegativeNumberIsRejectedInStrictMode() throws Exception {
    JsonReader reader = new JsonReader(new StringReader("-"));

    reader.nextString();
  }
}