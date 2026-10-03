package com.google.gson.stream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.bind.JsonTreeWriter;
import com.google.gson.internal.bind.TypeAdapters;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import junit.framework.TestCase;

/**
 * Regression and behavioral tests for boxed Boolean handling in JsonWriter,
 * JsonTreeWriter, and the Boolean TypeAdapter.
 */
public class JsonWriterBoxedBooleanTest extends TestCase {

  public void testBoxedBooleansAreWrittenIncludingNull() throws IOException {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);

    Boolean trueValue = Boolean.TRUE;
    Boolean falseValue = Boolean.FALSE;
    Boolean nullValue = null;

    writer.beginArray();
    writer.value(trueValue);
    writer.value(falseValue);
    writer.value(nullValue);
    writer.endArray();
    writer.close();

    assertEquals("[true,false,null]", output.toString());
  }

  public void testBoxedBooleanNullIsWrittenAsNullObjectMember() throws IOException {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);

    Boolean nullValue = null;

    writer.beginObject();
    writer.name("flag");
    writer.value(nullValue);
    writer.endObject();
    writer.close();

    assertEquals("{\"flag\":null}", output.toString());
  }

  public void testBoxedBooleanNullHonorsSerializeNullsFalse() throws IOException {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);
    writer.setSerializeNulls(false);

    Boolean nullValue = null;

    writer.beginObject();
    writer.name("omitted");
    writer.value(nullValue);
    writer.name("present");
    writer.value(Boolean.TRUE);
    writer.endObject();
    writer.close();

    assertEquals("{\"present\":true}", output.toString());
  }

  public void testJsonTreeWriterBoxedBooleanValuesBuildExpectedTree() throws IOException {
    JsonTreeWriter writer = new JsonTreeWriter();

    Boolean nullValue = null;

    writer.beginObject();
    writer.name("trueValue");
    writer.value(Boolean.TRUE);
    writer.name("falseValue");
    writer.value(Boolean.FALSE);
    writer.name("nullValue");
    writer.value(nullValue);
    writer.endObject();

    JsonObject result = writer.get().getAsJsonObject();
    assertTrue(result.get("trueValue").getAsBoolean());
    assertFalse(result.get("falseValue").getAsBoolean());
    assertSame(JsonNull.INSTANCE, result.get("nullValue"));
  }

  public void testJsonTreeWriterBoxedBooleanNullHonorsSerializeNullsFalse() throws IOException {
    JsonTreeWriter writer = new JsonTreeWriter();
    writer.setSerializeNulls(false);

    Boolean nullValue = null;

    writer.beginObject();
    writer.name("omitted");
    writer.value(nullValue);
    writer.name("retained");
    writer.value(Boolean.FALSE);
    writer.endObject();

    JsonObject result = writer.get().getAsJsonObject();
    assertFalse(result.has("omitted"));
    assertFalse(result.get("retained").getAsBoolean());
  }

  public void testBooleanTypeAdapterWritesBoxedValuesAndNull() throws IOException {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);

    writer.beginArray();
    TypeAdapters.BOOLEAN.write(writer, Boolean.TRUE);
    TypeAdapters.BOOLEAN.write(writer, Boolean.FALSE);
    TypeAdapters.BOOLEAN.write(writer, null);
    writer.endArray();
    writer.close();

    assertEquals("[true,false,null]", output.toString());
  }

  public void testBooleanTypeAdapterReadsBooleanStringAndNull() throws IOException {
    JsonReader stringReader = new JsonReader(new StringReader("\"true\""));
    JsonReader nullReader = new JsonReader(new StringReader("null"));

    assertEquals(Boolean.TRUE, TypeAdapters.BOOLEAN.read(stringReader));
    assertNull(TypeAdapters.BOOLEAN.read(nullReader));
  }

  public void testJsonElementAdapterWritesNestedBooleanAndNullValues() throws IOException {
    JsonObject object = new JsonObject();
    JsonArray values = new JsonArray();
    values.add(new JsonPrimitive(true));
    values.add(JsonNull.INSTANCE);
    values.add(new JsonPrimitive(false));
    object.add("values", values);

    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);
    TypeAdapters.JSON_ELEMENT.write(writer, object);
    writer.close();

    assertEquals("{\"values\":[true,null,false]}", output.toString());
  }

  public void testJsonElementAdapterReadsNestedBooleanAndNullValues() throws IOException {
    JsonReader reader = new JsonReader(
        new StringReader("{\"values\":[true,null,false]}"));

    JsonElement element = TypeAdapters.JSON_ELEMENT.read(reader);
    JsonArray values = element.getAsJsonObject().get("values").getAsJsonArray();

    assertEquals(3, values.size());
    assertTrue(values.get(0).getAsBoolean());
    assertSame(JsonNull.INSTANCE, values.get(1));
    assertFalse(values.get(2).getAsBoolean());
  }

  public void testStrictWriterRejectsNonFiniteDouble() throws IOException {
    JsonWriter writer = new JsonWriter(new StringWriter());

    try {
      writer.beginArray();
      writer.value(Double.NaN);
      fail("Expected IllegalArgumentException for NaN in strict mode");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().indexOf("finite") >= 0);
    }
  }

  public void testWriterRejectsInvalidNestingAndNullName() throws IOException {
    JsonWriter writer = new JsonWriter(new StringWriter());

    try {
      writer.endArray();
      fail("Expected IllegalStateException when ending an unopened array");
    } catch (IllegalStateException expected) {
      assertTrue(expected.getMessage().indexOf("Nesting") >= 0);
    }

    try {
      writer.name(null);
      fail("Expected NullPointerException for a null property name");
    } catch (NullPointerException expected) {
      assertEquals("name == null", expected.getMessage());
    }
  }

  public void testTreeWriterRejectsIncompleteDocumentAndMismatchedClose() throws IOException {
    JsonTreeWriter writer = new JsonTreeWriter();

    try {
      writer.endArray();
      fail("Expected IllegalStateException when ending an unopened array");
    } catch (IllegalStateException expected) {
      assertNotNull(expected);
    }

    writer.beginObject();
    try {
      writer.close();
      fail("Expected IOException when closing an incomplete tree document");
    } catch (IOException expected) {
      assertEquals("Incomplete document", expected.getMessage());
    }
  }

  public void testTreeWriterGetRejectsIncompleteDocument() throws IOException {
    JsonTreeWriter writer = new JsonTreeWriter();
    writer.beginArray();
    writer.value(true);

    try {
      writer.get();
      fail("Expected IllegalStateException for an incomplete JSON tree");
    } catch (IllegalStateException expected) {
      assertTrue(expected.getMessage().indexOf("Expected one JSON element") >= 0);
    }
  }
}
