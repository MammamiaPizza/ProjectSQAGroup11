```java
package com.google.gson.stream;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

/**
 * Tests RFC 7159 top-level values and core JsonReader / JsonWriter behavior.
 */
public class JsonStreamTopLevelValueTest extends TestCase {

  public void testReaderAcceptsAllPrimitiveTopLevelValueTypesInStrictMode() throws Exception {
    JsonReader stringReader = new JsonReader(new StringReader("\"text\""));
    assertEquals(JsonToken.STRING, stringReader.peek());
    assertEquals("text", stringReader.nextString());
    assertEquals(JsonToken.END_DOCUMENT, stringReader.peek());
    stringReader.close();

    JsonReader trueReader = new JsonReader(new StringReader("true"));
    assertEquals(JsonToken.BOOLEAN, trueReader.peek());
    assertTrue(trueReader.nextBoolean());
    assertEquals(JsonToken.END_DOCUMENT, trueReader.peek());
    trueReader.close();

    JsonReader falseReader = new JsonReader(new StringReader("false"));
    assertFalse(falseReader.nextBoolean());
    assertEquals(JsonToken.END_DOCUMENT, falseReader.peek());
    falseReader.close();

    JsonReader nullReader = new JsonReader(new StringReader("null"));
    assertEquals(JsonToken.NULL, nullReader.peek());
    nullReader.nextNull();
    assertEquals(JsonToken.END_DOCUMENT, nullReader.peek());
    nullReader.close();

    JsonReader integerReader = new JsonReader(new StringReader("-123"));
    assertEquals(JsonToken.NUMBER, integerReader.peek());
    assertEquals(-123L, integerReader.nextLong());
    assertEquals(JsonToken.END_DOCUMENT, integerReader.peek());
    integerReader.close();

    JsonReader decimalReader = new JsonReader(new StringReader("1.25e2"));
    assertEquals(JsonToken.NUMBER, decimalReader.peek());
    assertEquals(125.0d, decimalReader.nextDouble(), 0.0d);
    assertEquals(JsonToken.END_DOCUMENT, decimalReader.peek());
    decimalReader.close();
  }

  public void testReaderCanSkipPrimitiveTopLevelValuesInStrictMode() throws Exception {
    String[] values = { "\"value\"", "true", "false", "null", "42", "-1.5" };

    for (int i = 0; i < values.length; i++) {
      JsonReader reader = new JsonReader(new StringReader(values[i]));
      reader.skipValue();
      assertEquals("Skipped value should consume the complete document: " + values[i],
          JsonToken.END_DOCUMENT, reader.peek());
      reader.close();
    }
  }

  public void testWriterAcceptsPrimitiveTopLevelValueTypesInStrictMode() throws Exception {
    StringWriter stringOutput = new StringWriter();
    JsonWriter stringWriter = new JsonWriter(stringOutput);
    stringWriter.value("text");
    stringWriter.close();
    assertEquals("\"text\"", stringOutput.toString());

    StringWriter booleanOutput = new StringWriter();
    JsonWriter booleanWriter = new JsonWriter(booleanOutput);
    booleanWriter.value(true);
    booleanWriter.close();
    assertEquals("true", booleanOutput.toString());

    StringWriter nullOutput = new StringWriter();
    JsonWriter nullWriter = new JsonWriter(nullOutput);
    nullWriter.nullValue();
    nullWriter.close();
    assertEquals("null", nullOutput.toString());

    StringWriter longOutput = new StringWriter();
    JsonWriter longWriter = new JsonWriter(longOutput);
    longWriter.value(-123L);
    longWriter.close();
    assertEquals("-123", longOutput.toString());

    StringWriter doubleOutput = new StringWriter();
    JsonWriter doubleWriter = new JsonWriter(doubleOutput);
    doubleWriter.value(1.25d);
    doubleWriter.close();
    assertEquals("1.25", doubleOutput.toString());

    StringWriter numberOutput = new StringWriter();
    JsonWriter numberWriter = new JsonWriter(numberOutput);
    numberWriter.value((Number) Integer.valueOf(7));
    numberWriter.close();
    assertEquals("7", numberOutput.toString());

    StringWriter rawOutput = new StringWriter();
    JsonWriter rawWriter = new JsonWriter(rawOutput);
    rawWriter.jsonValue("false");
    rawWriter.close();
    assertEquals("false", rawOutput.toString());
  }

  public void testReaderTraversesNestedValuesAndSkipsUnknownObjectValue() throws Exception {
    JsonReader reader = new JsonReader(new StringReader(
        "{\"known\":[1],\"unknown\":{\"nested\":true},\"tail\":null}"));

    reader.beginObject();

    assertTrue(reader.hasNext());
    assertEquals("known", reader.nextName());
    reader.beginArray();
    assertTrue(reader.hasNext());
    assertEquals(1, reader.nextInt());
    assertFalse(reader.hasNext());
    reader.endArray();

    assertTrue(reader.hasNext());
    assertEquals("unknown", reader.nextName());
    reader.skipValue();

    assertTrue(reader.hasNext());
    assertEquals("tail", reader.nextName());
    assertEquals(JsonToken.NULL, reader.peek());
    reader.nextNull();

    assertFalse(reader.hasNext());
    reader.endObject();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    reader.close();
  }

  public void testReaderSupportsExactNumericConversionsAndRejectsOverflow() throws Exception {
    JsonReader reader = new JsonReader(new StringReader(
        "[-9223372036854775808,\"2.0\",2147483648]"));

    reader.beginArray();
    assertEquals(Long.MIN_VALUE, reader.nextLong());
    assertEquals(2, reader.nextInt());

    try {
      reader.nextInt();
      fail("An integer outside the int range must not be silently truncated");
    } catch (NumberFormatException expected) {
      assertTrue(expected.getMessage().indexOf("Expected an int") >= 0);
    }

    reader.close();
  }

  public void testReaderRejectsUnquotedAndMultipleTopLevelValuesInStrictMode() throws Exception {
    JsonReader unquotedReader = new JsonReader(new StringReader("unquoted"));
    try {
      unquotedReader.peek();
      fail("Unquoted top-level strings are not valid strict JSON");
    } catch (MalformedJsonException expected) {
      assertTrue(expected.getMessage().indexOf("setLenient(true)") >= 0);
    } finally {
      unquotedReader.close();
    }

    JsonReader multipleReader = new JsonReader(new StringReader("true false"));
    assertTrue(multipleReader.nextBoolean());
    try {
      multipleReader.peek();
      fail("Strict JSON must contain only one top-level value");
    } catch (MalformedJsonException expected) {
      assertTrue(expected.getMessage().indexOf("setLenient(true)") >= 0);
    } finally {
      multipleReader.close();
    }
  }

  public void testReaderLenientModeAcceptsCommentsAndUnquotedValues() throws Exception {
    JsonReader reader = new JsonReader(new StringReader("// comment\nunquoted"));
    reader.setLenient(true);

    assertTrue(reader.isLenient());
    assertEquals(JsonToken.STRING, reader.peek());
    assertEquals("unquoted", reader.nextString());
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());

    reader.close();
  }

  public void testWriterEscapesHtmlFormatsIndentedOutputAndSerializesArrayNulls() throws Exception {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);
    writer.setIndent("  ");
    writer.setHtmlSafe(true);

    writer.beginObject();
    writer.name("text").value("<&>");
    writer.name("items").beginArray();
    writer.nullValue();
    writer.endArray();
    writer.endObject();
    writer.close();

    assertEquals("{\n"
        + "  \"text\": \"\\u003c\\u0026\\u003e\",\n"
        + "  \"items\": [\n"
        + "    null\n"
        + "  ]\n"
        + "}", output.toString());
  }

  public void testWriterCanOmitNullObjectMembersWhenConfigured() throws Exception {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);
    writer.setSerializeNulls(false);

    assertFalse(writer.getSerializeNulls());
    writer.beginObject();
    writer.name("omitted").nullValue();
    writer.name("kept").value(1L);
    writer.endObject();
    writer.close();

    assertEquals("{\"kept\":1}", output.toString());
  }

  public void testWriterRejectsMultipleTopLevelValuesAndInvalidNames() throws Exception {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);

    writer.beginArray();
    writer.endArray();

    try {
      writer.value(true);
      fail("Strict JSON must not contain multiple top-level values");
    } catch (IllegalStateException expected) {
      assertTrue(expected.getMessage().indexOf("only one top-level value") >= 0);
    }

    try {
      new JsonWriter(new StringWriter()).name(null);
      fail("A JSON property name must not be null");
    } catch (NullPointerException expected) {
      assertEquals("name == null", expected.getMessage());
    }
  }

  public void testConstructorsRejectNullStreams() {
    try {
      new JsonReader(null);
      fail("JsonReader must reject a null input Reader");
    } catch (NullPointerException expected) {
      assertEquals("in == null", expected.getMessage());
    }

    try {
      new JsonWriter(null);
      fail("JsonWriter must reject a null output Writer");
    } catch (NullPointerException expected) {
      assertEquals("out == null", expected.getMessage());
    }
  }

  public void testWriterReportsIncompleteDocumentsOnClose() throws Exception {
    JsonWriter writer = new JsonWriter(new StringWriter());
    writer.beginArray();

    try {
      writer.close();
      fail("Closing an unclosed JSON array must report an incomplete document");
    } catch (IOException expected) {
      assertEquals("Incomplete document", expected.getMessage());
    }
  }
}
```

Test coverage summary:

- `testReaderAcceptsAllPrimitiveTopLevelValueTypesInStrictMode`: Targets the Gson-4 defect by requiring strict readers to accept RFC 7159 top-level strings, booleans, null, integers, and decimal/exponent numbers.
- `testReaderCanSkipPrimitiveTopLevelValuesInStrictMode`: Targets the reported `skipValue()` failure for strict top-level primitive values.
- `testWriterAcceptsPrimitiveTopLevelValueTypesInStrictMode`: Targets the Gson-4 writer defect by requiring strict writers to emit all primitive top-level value types.
- `testReaderTraversesNestedValuesAndSkipsUnknownObjectValue`: Exercises object/array traversal, `hasNext`, names, null handling, and recursive `skipValue`.
- `testReaderSupportsExactNumericConversionsAndRejectsOverflow`: Covers `nextLong`, quoted numeric conversion in `nextInt`, and integer overflow rejection.
- `testReaderRejectsUnquotedAndMultipleTopLevelValuesInStrictMode`: Verifies that permitting primitive top-level values does not accidentally permit malformed unquoted values or multiple strict top-level values.
- `testReaderLenientModeAcceptsCommentsAndUnquotedValues`: Covers lenient-mode parsing behavior.
- `testWriterEscapesHtmlFormatsIndentedOutputAndSerializesArrayNulls`: Covers indentation, HTML-safe escaping, object names, nested arrays, and array null serialization.
- `testWriterCanOmitNullObjectMembersWhenConfigured`: Covers `serializeNulls=false` behavior.
- `testWriterRejectsMultipleTopLevelValuesAndInvalidNames`: Covers writer state validation for multiple root values and null property names.
- `testConstructorsRejectNullStreams`: Covers constructor null-argument validation.
- `testWriterReportsIncompleteDocumentsOnClose`: Covers exceptional close behavior for incomplete JSON output.