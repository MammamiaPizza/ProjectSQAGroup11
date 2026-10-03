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
}