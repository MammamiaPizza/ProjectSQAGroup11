package com.google.gson.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.Test;

public class JsonWriterGeneratedTest {

  @Test
  public void constructorRejectsNullWriter() {
    try {
      new JsonWriter(null);
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
      assertEquals("out == null", expected.getMessage());
    }
  }

  @Test
  public void writesNestedObjectArrayAndPrettyPrints() throws IOException {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);
    writer.setIndent("  ");

    assertSame(writer, writer.beginObject());
    writer.name("items").beginArray();
    writer.value(true);
    writer.nullValue();
    writer.endArray();
    writer.name("text").value("value");
    writer.endObject();

    assertEquals(
        "{\n"
            + "  \"items\": [\n"
            + "    true,\n"
            + "    null\n"
            + "  ],\n"
            + "  \"text\": \"value\"\n"
            + "}",
        output.toString());
  }

  @Test
  public void writesStringsWithRequiredAndHtmlSafeEscaping() throws IOException {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);
    writer.setHtmlSafe(true);

    writer.value("<>&='\t\n\"\\\u2028\u2029");

    assertEquals(
        "\""
            + "\\u003c"
            + "\\u003e"
            + "\\u0026"
            + "\\u003d"
            + "\\u0027"
            + "\\t"
            + "\\n"
            + "\\\""
            + "\\\\"
            + "\\u2028"
            + "\\u2029"
            + "\"",
        output.toString());
    assertTrue(writer.isHtmlSafe());
  }

  @Test
  public void compactIndentSettingRestoresCompactNameSeparator() throws IOException {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);

    writer.setIndent("  ");
    writer.setIndent("");
    writer.beginObject().name("a").value(1L).endObject();

    assertEquals("{\"a\":1}", output.toString());
  }

  @Test
  public void nullStringAndNullBooleanWriteNullLiterals() throws IOException {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);

    writer.beginArray();
    writer.value((String) null);
    writer.value((Boolean) null);
    writer.value((Number) null);
    writer.endArray();

    assertEquals("[null,null,null]", output.toString());
  }

  @Test
  public void disablingNullSerializationSkipsObjectMembersButNotArrayElements() throws IOException {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);
    writer.setSerializeNulls(false);

    assertFalse(writer.getSerializeNulls());
    writer.beginObject();
    writer.name("omitted").nullValue();
    writer.name("present").value(2L);
    writer.name("array").beginArray();
    writer.nullValue();
    writer.endArray();
    writer.endObject();

    assertEquals("{\"present\":2,\"array\":[null]}", output.toString());
  }

  @Test
  public void jsonValueWritesRawValueAndNullAsJsonNull() throws IOException {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);

    writer.beginArray();
    writer.jsonValue("{\"raw\":true}");
    writer.jsonValue(null);
    writer.endArray();

    assertEquals("[{\"raw\":true},null]", output.toString());
  }

  @Test
  public void finitePrimitiveAndNumberValuesAreWritten() throws IOException {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);

    writer.beginArray();
    writer.value(false);
    writer.value(42L);
    writer.value(1.5d);
    writer.value((Number) Integer.valueOf(-3));
    writer.endArray();

    assertEquals("[false,42,1.5,-3]", output.toString());
  }

  @Test
  public void strictWriterRejectsNonFinitePrimitiveDouble() throws IOException {
    JsonWriter writer = new JsonWriter(new StringWriter());

    try {
      writer.value(Double.NaN);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      assertEquals("Numeric values must be finite, but was NaN", expected.getMessage());
    }
  }

  @Test
  public void lenientWriterAllowsNonFinitePrimitiveDoubles() throws IOException {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);
    writer.setLenient(true);

    assertTrue(writer.isLenient());
    writer.beginArray();
    writer.value(Double.NaN);
    writer.value(Double.POSITIVE_INFINITY);
    writer.value(Double.NEGATIVE_INFINITY);
    writer.endArray();

    assertEquals("[NaN,Infinity,-Infinity]", output.toString());
  }

  @Test
  public void lenientWriterAllowsNonFiniteNumberValues() throws IOException {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);
    writer.setLenient(true);

    writer.beginArray();
    writer.value((Number) Double.valueOf(Double.NaN));
    writer.value((Number) Double.valueOf(Double.POSITIVE_INFINITY));
    writer.endArray();

    assertEquals("[NaN,Infinity]", output.toString());
  }

  @Test
  public void strictWriterRejectsSecondTopLevelValue() throws IOException {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);

    writer.beginArray().endArray();

    try {
      writer.beginArray();
      fail("Expected IllegalStateException");
    } catch (IllegalStateException expected) {
      assertEquals("JSON must have only one top-level value.", expected.getMessage());
    }

    assertEquals("[]", output.toString());
  }

  @Test
  public void rejectsRepeatedDeferredNameAndMismatchedArrayEnd() throws IOException {
    JsonWriter writer = new JsonWriter(new StringWriter());

    writer.name("first");
    try {
      writer.name("second");
      fail("Expected IllegalStateException for repeated name");
    } catch (IllegalStateException expected) {
      assertTrue(expected.getMessage() == null || expected.getMessage().length() >= 0);
    }

    JsonWriter separateWriter = new JsonWriter(new StringWriter());
    try {
      separateWriter.endArray();
      fail("Expected IllegalStateException for unmatched endArray");
    } catch (IllegalStateException expected) {
      assertEquals("Nesting problem.", expected.getMessage());
    }
  }

  @Test
  public void closeRejectsIncompleteDocumentAndCompletedWriterBecomesClosed() throws IOException {
    JsonWriter incomplete = new JsonWriter(new StringWriter());
    incomplete.beginArray();

    try {
      incomplete.close();
      fail("Expected IOException");
    } catch (IOException expected) {
      assertEquals("Incomplete document", expected.getMessage());
    }

    JsonWriter complete = new JsonWriter(new StringWriter());
    complete.beginArray().endArray();
    complete.close();

    try {
      complete.flush();
      fail("Expected IllegalStateException");
    } catch (IllegalStateException expected) {
      assertEquals("JsonWriter is closed.", expected.getMessage());
    }
  }

  @Test
  public void supportsNestingBeyondInitialStackCapacity() throws IOException {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);
    StringBuilder expected = new StringBuilder();

    for (int i = 0; i < 33; i++) {
      writer.beginArray();
      expected.append('[');
    }
    for (int i = 0; i < 33; i++) {
      writer.endArray();
      expected.append(']');
    }

    assertEquals(expected.toString(), output.toString());
  }

  @Test
  public void strictWriterRejectsPositiveInfinityPrimitiveDouble() throws IOException {
    JsonWriter writer = new JsonWriter(new StringWriter());

    try {
      writer.value(Double.POSITIVE_INFINITY);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      assertEquals("Numeric values must be finite, but was Infinity", expected.getMessage());
    }
  }

  @Test
  public void strictWriterRejectsAllNonFiniteNumberRepresentations() throws IOException {
    assertStrictNumberRejected(Double.valueOf(Double.NaN), "NaN");
    assertStrictNumberRejected(Double.valueOf(Double.POSITIVE_INFINITY), "Infinity");
    assertStrictNumberRejected(Double.valueOf(Double.NEGATIVE_INFINITY), "-Infinity");
  }

  private void assertStrictNumberRejected(Number value, String renderedValue) throws IOException {
    JsonWriter writer = new JsonWriter(new StringWriter());

    try {
      writer.value(value);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      assertEquals(
          "Numeric values must be finite, but was " + renderedValue,
          expected.getMessage());
    }
  }

  @Test
  public void lenientWriterAllowsTopLevelScalarAndMultipleTopLevelValues() throws IOException {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);
    writer.setLenient(true);

    writer.value(1L);
    writer.value(false);

    assertEquals("1false", output.toString());
  }

  @Test
  public void configurationFlagsCanBeRestoredToFalseAndDefaultsAreReported() {
    JsonWriter writer = new JsonWriter(new StringWriter());

    assertFalse(writer.isLenient());
    assertFalse(writer.isHtmlSafe());
    assertTrue(writer.getSerializeNulls());

    writer.setLenient(true);
    writer.setHtmlSafe(true);
    writer.setSerializeNulls(false);
    writer.setLenient(false);
    writer.setHtmlSafe(false);
    writer.setSerializeNulls(true);

    assertFalse(writer.isLenient());
    assertFalse(writer.isHtmlSafe());
    assertTrue(writer.getSerializeNulls());
  }

  @Test
  public void defaultStringEscapingDoesNotApplyHtmlEscapes() throws IOException {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);

    writer.value("<>&='");

    assertEquals("\"<>&='\"", output.toString());
  }

  @Test
  public void nameRejectsNullAndNamesOutsideObjectsCannotReceiveValues() throws IOException {
    JsonWriter nullNameWriter = new JsonWriter(new StringWriter());

    try {
      nullNameWriter.name(null);
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
      assertEquals("name == null", expected.getMessage());
    }

    JsonWriter topLevelNameWriter = new JsonWriter(new StringWriter());
    topLevelNameWriter.name("notAnObjectMember");
    try {
      topLevelNameWriter.value(1L);
      fail("Expected IllegalStateException");
    } catch (IllegalStateException expected) {
      assertEquals("Nesting problem.", expected.getMessage());
    }
  }

  @Test
  public void endObjectRejectsDanglingName() throws IOException {
    JsonWriter writer = new JsonWriter(new StringWriter());
    writer.beginObject().name("missingValue");

    try {
      writer.endObject();
      fail("Expected IllegalStateException");
    } catch (IllegalStateException expected) {
      assertEquals("Dangling name: missingValue", expected.getMessage());
    }
  }

  @Test
  public void closingWithoutAnyTopLevelValueRejectsIncompleteDocument() throws IOException {
    JsonWriter writer = new JsonWriter(new StringWriter());

    try {
      writer.close();
      fail("Expected IOException");
    } catch (IOException expected) {
      assertEquals("Incomplete document", expected.getMessage());
    }
  }

  @Test
  public void flushDelegatesToOpenUnderlyingWriter() throws IOException {
    final boolean[] flushed = new boolean[1];
    Writer output =
        new Writer() {
          @Override
          public void write(char[] cbuf, int off, int len) {
          }

          @Override
          public void flush() {
            flushed[0] = true;
          }

          @Override
          public void close() {
          }
        };

    JsonWriter writer = new JsonWriter(output);
    writer.flush();

    assertTrue(flushed[0]);
  }

  @Test
  public void nameRejectsCallsAfterWriterHasBeenClosed() throws IOException {
    JsonWriter writer = new JsonWriter(new StringWriter());
    writer.beginArray().endArray();
    writer.close();

    try {
      writer.name("afterClose");
      fail("Expected IllegalStateException");
    } catch (IllegalStateException expected) {
      assertEquals("JsonWriter is closed.", expected.getMessage());
    }
  }
}
