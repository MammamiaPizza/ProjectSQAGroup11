```java
package com.google.gson.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.StringReader;

import org.junit.Test;

public class JsonReaderDefects4JTest {

  @Test
  public void constructorRejectsNullReader() {
    try {
      new JsonReader(null);
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
      assertEquals("in == null", expected.getMessage());
    }
  }

  @Test
  public void readsNestedObjectAndConvertsCompatibleValues() throws Exception {
    JsonReader reader = new JsonReader(new StringReader(
        "{\"name\":\"A\\n\\u0042\",\"flag\":true,\"nothing\":null,"
            + "\"ratio\":\"1.5\",\"numbers\":[1,\"2\",-3]}"));

    assertEquals(JsonToken.BEGIN_OBJECT, reader.peek());
    reader.beginObject();

    assertTrue(reader.hasNext());
    assertEquals("name", reader.nextName());
    assertEquals("A\nB", reader.nextString());

    assertEquals("flag", reader.nextName());
    assertTrue(reader.nextBoolean());

    assertEquals("nothing", reader.nextName());
    assertEquals(JsonToken.NULL, reader.peek());
    reader.nextNull();

    assertEquals("ratio", reader.nextName());
    assertEquals(1.5d, reader.nextDouble(), 0.0d);

    assertEquals("numbers", reader.nextName());
    reader.beginArray();
    assertEquals(1, reader.nextInt());
    assertEquals(2, reader.nextInt());
    assertEquals(-3L, reader.nextLong());
    assertFalse(reader.hasNext());
    reader.endArray();

    assertFalse(reader.hasNext());
    reader.endObject();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  @Test
  public void nextStringPreservesNegativeZeroLexeme() throws Exception {
    JsonReader reader = new JsonReader(new StringReader("[-0]"));

    reader.beginArray();
    assertEquals("-0", reader.nextString());
    reader.endArray();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  @Test
  public void readsLongBoundaryValuesExactly() throws Exception {
    JsonReader reader = new JsonReader(new StringReader(
        "[-9223372036854775808,9223372036854775807]"));

    reader.beginArray();
    assertEquals(Long.MIN_VALUE, reader.nextLong());
    assertEquals(Long.MAX_VALUE, reader.nextLong());
    reader.endArray();

    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  @Test
  public void nextIntRejectsNonIntegralNumber() throws Exception {
    JsonReader reader = new JsonReader(new StringReader("[1.5]"));
    reader.beginArray();

    try {
      reader.nextInt();
      fail("Expected NumberFormatException");
    } catch (NumberFormatException expected) {
      assertTrue(expected.getMessage().contains("Expected an int"));
    }
  }

  @Test
  public void strictModeRejectsComments() throws Exception {
    JsonReader reader = new JsonReader(new StringReader("[/* comment */1]"));
    reader.beginArray();

    try {
      reader.nextInt();
      fail("Expected MalformedJsonException");
    } catch (MalformedJsonException expected) {
      assertTrue(expected.getMessage().contains("setLenient(true)"));
    }
  }

  @Test
  public void lenientModeAcceptsPrefixUnquotedNamesSingleQuotesAndAlternateSeparators()
      throws Exception {
    JsonReader reader = new JsonReader(new StringReader(
        ")]}'\n{unquoted='value';other=>2}"));

    assertFalse(reader.isLenient());
    reader.setLenient(true);
    assertTrue(reader.isLenient());

    reader.beginObject();
    assertEquals("unquoted", reader.nextName());
    assertEquals("value", reader.nextString());
    assertEquals("other", reader.nextName());
    assertEquals(2, reader.nextInt());
    assertFalse(reader.hasNext());
    reader.endObject();

    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  @Test
  public void strictModeRejectsNonFiniteDoubleButLenientModeAllowsIt() throws Exception {
    JsonReader strictReader = new JsonReader(new StringReader("[\"NaN\"]"));
    strictReader.beginArray();

    try {
      strictReader.nextDouble();
      fail("Expected MalformedJsonException");
    } catch (MalformedJsonException expected) {
      assertTrue(expected.getMessage().contains("JSON forbids NaN"));
    }

    JsonReader lenientReader = new JsonReader(new StringReader("[\"NaN\"]"));
    lenientReader.setLenient(true);
    lenientReader.beginArray();
    assertTrue(Double.isNaN(lenientReader.nextDouble()));
    lenientReader.endArray();
  }

  @Test
  public void skipValueSkipsNestedArraysAndObjects() throws Exception {
    JsonReader reader = new JsonReader(new StringReader(
        "{\"ignored\":[{\"deep\":1},2],\"kept\":\"ok\"}"));

    reader.beginObject();
    assertEquals("ignored", reader.nextName());
    reader.skipValue();

    assertTrue(reader.hasNext());
    assertEquals("kept", reader.nextName());
    assertEquals("ok", reader.nextString());
    assertFalse(reader.hasNext());
    reader.endObject();

    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  @Test
  public void getPathTracksArrayPosition() throws Exception {
    JsonReader reader = new JsonReader(new StringReader("[10,20]"));

    reader.beginArray();
    assertEquals("$[0]", reader.getPath());

    assertEquals(10, reader.nextInt());
    assertEquals("$[1]", reader.getPath());

    assertEquals(20, reader.nextInt());
    assertEquals("$[2]", reader.getPath());

    reader.endArray();
    assertEquals("$", reader.getPath());
  }

  @Test
  public void typeMismatchThrowsIllegalStateException() throws Exception {
    JsonReader reader = new JsonReader(new StringReader("[true]"));
    reader.beginArray();

    try {
      reader.nextString();
      fail("Expected IllegalStateException");
    } catch (IllegalStateException expected) {
      assertTrue(expected.getMessage().contains("Expected a string"));
    }
  }

  @Test
  public void closePreventsFurtherReading() throws Exception {
    JsonReader reader = new JsonReader(new StringReader("[]"));

    reader.close();

    try {
      reader.peek();
      fail("Expected IllegalStateException");
    } catch (IllegalStateException expected) {
      assertEquals("JsonReader is closed", expected.getMessage());
    }
  }
}
```

Test coverage summary:

- `constructorRejectsNullReader`: verifies constructor null validation.
- `readsNestedObjectAndConvertsCompatibleValues`: exercises normal object/array traversal, `peek`, `hasNext`, names, strings, escapes, booleans, nulls, and numeric conversions from both JSON numbers and quoted strings.
- `nextStringPreservesNegativeZeroLexeme`: targets Gson-13 directly; `-0` must remain `"-0"` when read with `nextString()`.
- `readsLongBoundaryValuesExactly`: covers exact `long` minimum and maximum parsing.
- `nextIntRejectsNonIntegralNumber`: verifies exact-representation enforcement for `nextInt()`.
- `strictModeRejectsComments`: covers strict-mode malformed-input rejection.
- `lenientModeAcceptsPrefixUnquotedNamesSingleQuotesAndAlternateSeparators`: covers configured lenient parsing, non-execute prefix handling, unquoted names, single-quoted strings, semicolon separators, and `=>`.
- `strictModeRejectsNonFiniteDoubleButLenientModeAllowsIt`: tests strict rejection and lenient acceptance of non-finite doubles.
- `skipValueSkipsNestedArraysAndObjects`: verifies recursive skipping of unknown nested values.
- `getPathTracksArrayPosition`: verifies JSON path index updates as array elements are consumed.
- `typeMismatchThrowsIllegalStateException`: verifies token-type contract enforcement.
- `closePreventsFurtherReading`: verifies closed-reader state behavior.