package com.google.gson.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Test;

public class JsonWriterNonFiniteNumbersTest {

  @Test
  public void lenientDoubleNaNIsWritten() throws IOException {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);
    writer.setLenient(true);

    writer.value(Double.NaN);

    assertEquals("NaN", output.toString());
  }

  @Test
  public void lenientDoubleInfinitiesAreWrittenInArray() throws IOException {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);
    writer.setLenient(true);

    writer.beginArray();
    writer.value(Double.POSITIVE_INFINITY);
    writer.value(Double.NEGATIVE_INFINITY);
    writer.endArray();

    assertEquals("[Infinity,-Infinity]", output.toString());
  }

  @Test
  public void lenientNumberNonFiniteValuesAreWritten() throws IOException {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);
    writer.setLenient(true);

    writer.beginArray();
    writer.value((Number) Double.NaN);
    writer.value((Number) Double.POSITIVE_INFINITY);
    writer.value((Number) Double.NEGATIVE_INFINITY);
    writer.endArray();

    assertEquals("[NaN,Infinity,-Infinity]", output.toString());
  }

  @Test
  public void strictDoubleNaNIsRejectedWithFiniteValueMessage() throws IOException {
    JsonWriter writer = new JsonWriter(new StringWriter());
    assertFalse(writer.isLenient());

    try {
      writer.value(Double.NaN);
      fail("Expected non-finite double to be rejected in strict mode");
    } catch (IllegalArgumentException expected) {
      assertEquals("Numeric values must be finite, but was NaN", expected.getMessage());
    }
  }

  @Test
  public void strictNumberInfinityIsRejectedWithFiniteValueMessage() throws IOException {
    JsonWriter writer = new JsonWriter(new StringWriter());

    try {
      writer.value((Number) Double.POSITIVE_INFINITY);
      fail("Expected non-finite Number to be rejected in strict mode");
    } catch (IllegalArgumentException expected) {
      assertEquals("Numeric values must be finite, but was Infinity", expected.getMessage());
    }
  }

  @Test
  public void finiteDoublesAreWrittenInBothModes() throws IOException {
    StringWriter strictOutput = new StringWriter();
    JsonWriter strictWriter = new JsonWriter(strictOutput);
    strictWriter.value(1.25d);

    StringWriter lenientOutput = new StringWriter();
    JsonWriter lenientWriter = new JsonWriter(lenientOutput);
    lenientWriter.setLenient(true);
    assertTrue(lenientWriter.isLenient());
    lenientWriter.value(-0.5d);

    assertEquals("1.25", strictOutput.toString());
    assertEquals("-0.5", lenientOutput.toString());
  }

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
}
