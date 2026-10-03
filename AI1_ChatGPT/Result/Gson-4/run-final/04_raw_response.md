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

  public void testReaderAcceptsBomBeforeStrictTopLevelPrimitive() throws Exception {
    JsonReader reader = new JsonReader(new StringReader("\ufefffalse"));

    assertEquals(JsonToken.BOOLEAN, reader.peek());
    assertFalse(reader.nextBoolean());
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());

    reader.close();
  }

  public void testReaderLenientModeSupportsPrefixAlternativeNamesSeparatorsAndMissingArrayValue()
      throws Exception {
    JsonReader reader = new JsonReader(new StringReader(
        ")]}'\n{unquoted='value';second=>[1,,3]}"));
    reader.setLenient(true);

    reader.beginObject();

    assertEquals("unquoted", reader.nextName());
    assertEquals("value", reader.nextString());

    assertEquals("second", reader.nextName());
    reader.beginArray();
    assertEquals(1, reader.nextInt());
    reader.nextNull();
    assertEquals(3, reader.nextInt());
    reader.endArray();

    reader.endObject();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    reader.close();
  }

  public void testReaderRejectsQuotedNonFiniteDoubleInStrictModeAndAcceptsItWhenLenient()
      throws Exception {
    JsonReader strictReader = new JsonReader(new StringReader("[\"NaN\"]"));
    strictReader.beginArray();
    try {
      strictReader.nextDouble();
      fail("Strict JSON must reject a non-finite double");
    } catch (MalformedJsonException expected) {
      assertTrue(expected.getMessage().indexOf("JSON forbids NaN") >= 0);
    } finally {
      strictReader.close();
    }

    JsonReader lenientReader = new JsonReader(new StringReader("[NaN,Infinity,-Infinity]"));
    lenientReader.setLenient(true);
    lenientReader.beginArray();

    assertTrue(Double.isNaN(lenientReader.nextDouble()));
    assertEquals(Double.POSITIVE_INFINITY, lenientReader.nextDouble(), 0.0d);
    assertEquals(Double.NEGATIVE_INFINITY, lenientReader.nextDouble(), 0.0d);

    lenientReader.endArray();
    assertEquals(JsonToken.END_DOCUMENT, lenientReader.peek());
    lenientReader.close();
  }

  public void testReaderReportsMalformedUnicodeEscape() throws Exception {
    JsonReader reader = new JsonReader(new StringReader("\"\\u12x4\""));

    try {
      reader.nextString();
      fail("Invalid hexadecimal unicode escapes must be rejected");
    } catch (NumberFormatException expected) {
      assertEquals("\\u12x4", expected.getMessage());
    } finally {
      reader.close();
    }
  }

  public void testWriterLenientModeAllowsMultipleTopLevelValuesAndNonFiniteNumber() throws Exception {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);
    writer.setLenient(true);

    assertTrue(writer.isLenient());
    writer.value((Number) Double.valueOf(Double.NaN));
    writer.value((Number) Double.valueOf(Double.POSITIVE_INFINITY));
    writer.close();

    assertEquals("NaNInfinity", output.toString());
  }

  public void testWriterEscapesControlJavaScriptLineSeparatorsAndSupportsCompactIndent()
      throws Exception {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);

    writer.setIndent("");
    writer.beginArray();
    writer.value("\"\\\t\b\n\r\f\u0001\u2028\u2029");
    writer.value(false);
    writer.endArray();
    writer.close();

    assertEquals("[\"\\\"\\\\\\t\\b\\n\\r\\f\\u0001\\u2028\\u2029\",false]",
        output.toString());
  }

  public void testWriterRejectsDanglingNameAndOperationsAfterClose() throws Exception {
    JsonWriter danglingNameWriter = new JsonWriter(new StringWriter());
    danglingNameWriter.beginObject();
    danglingNameWriter.name("missing");

    try {
      danglingNameWriter.endObject();
      fail("An object with a pending name must not be closed");
    } catch (IllegalStateException expected) {
      assertEquals("Dangling name: missing", expected.getMessage());
    }

    StringWriter output = new StringWriter();
    JsonWriter closedWriter = new JsonWriter(output);
    closedWriter.nullValue();
    closedWriter.close();

    try {
      closedWriter.flush();
      fail("Flushing a closed writer must fail");
    } catch (IllegalStateException expected) {
      assertEquals("JsonWriter is closed.", expected.getMessage());
    }
  }
}
```

### Added test coverage

- **`testReaderAcceptsBomBeforeStrictTopLevelPrimitive`**
  - Covers `JsonReader.fillBuffer` handling of a leading UTF-8 BOM.
  - Confirms the Gson-4 fix also permits a primitive top-level value after BOM processing in strict mode.

- **`testReaderLenientModeSupportsPrefixAlternativeNamesSeparatorsAndMissingArrayValue`**
  - Covers lenient-only reader branches for:
    - The non-execute prefix `)]}'\n`.
    - Unquoted property names.
    - Single-quoted strings.
    - Semicolon object-member separators.
    - `=` / `=>` name-value separators.
    - Missing array elements interpreted as `null`.
  - Exercises `consumeNonExecutePrefix`, several `doPeek` branches, quoted/unquoted name handling, and recursive array parsing.

- **`testReaderRejectsQuotedNonFiniteDoubleInStrictModeAndAcceptsItWhenLenient`**
  - Covers strict rejection of `NaN` through the `nextDouble` finite-number validation branch.
  - Covers lenient parsing of `NaN`, positive infinity, and negative infinity.
  - Tests the documented behavior of `setLenient(true)` for non-finite numeric values.

- **`testReaderReportsMalformedUnicodeEscape`**
  - Covers malformed Unicode escape handling in `readEscapeCharacter`.
  - Verifies that invalid hexadecimal Unicode escape sequences throw `NumberFormatException`.

- **`testWriterLenientModeAllowsMultipleTopLevelValuesAndNonFiniteNumber`**
  - Covers the `JsonWriter.beforeValue` `NONEMPTY_DOCUMENT` lenient branch.
  - Covers lenient `Number` serialization of `NaN` and `Infinity`.
  - Complements the existing strict multiple-top-level-value rejection test.

- **`testWriterEscapesControlJavaScriptLineSeparatorsAndSupportsCompactIndent`**
  - Covers string replacement branches for quotes, backslashes, standard control-character escapes, generic low control characters, and `\u2028` / `\u2029`.
  - Covers the `setIndent("")` compact-output branch.
  - Covers boolean `false` serialization, complementing the existing `true` test.

- **`testWriterRejectsDanglingNameAndOperationsAfterClose`**
  - Covers `JsonWriter.close` scope validation for an object with a deferred property name.
  - Covers `flush` rejection after writer closure.