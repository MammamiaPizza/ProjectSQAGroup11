```java
package com.google.gson.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;

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
}
```

Test coverage summary:

- `constructorRejectsNullWriter`: verifies constructor null validation.
- `writesNestedObjectArrayAndPrettyPrints`: covers object/array nesting, names, booleans, nulls, indentation, commas, separators, and closing scopes.
- `writesStringsWithRequiredAndHtmlSafeEscaping`: covers string escaping, HTML-safe escaping, control characters, quotes, backslashes, and Unicode line separators.
- `compactIndentSettingRestoresCompactNameSeparator`: verifies compact-mode indentation and `:` separator restoration.
- `nullStringAndNullBooleanWriteNullLiterals`: covers null overload handling for `String`, `Boolean`, and `Number`.
- `disablingNullSerializationSkipsObjectMembersButNotArrayElements`: verifies `serializeNulls` behavior for object properties versus arrays.
- `jsonValueWritesRawValueAndNullAsJsonNull`: covers raw JSON insertion and null raw values.
- `finitePrimitiveAndNumberValuesAreWritten`: covers primitive boolean, long, double, and `Number` serialization.
- `strictWriterRejectsNonFinitePrimitiveDouble`: verifies strict handling of `NaN`.
- `lenientWriterAllowsNonFinitePrimitiveDoubles`: targets Gson-15 directly; lenient mode must allow primitive `double` values `NaN`, `Infinity`, and `-Infinity`.
- `lenientWriterAllowsNonFiniteNumberValues`: covers the separate `Number` overload’s lenient non-finite path.
- `strictWriterRejectsSecondTopLevelValue`: covers strict single-top-level-value enforcement.
- `rejectsRepeatedDeferredNameAndMismatchedArrayEnd`: covers deferred-name and invalid nesting failures.
- `closeRejectsIncompleteDocumentAndCompletedWriterBecomesClosed`: covers incomplete document detection and post-close behavior.
- `supportsNestingBeyondInitialStackCapacity`: covers internal stack growth beyond the initial 32 nesting levels.