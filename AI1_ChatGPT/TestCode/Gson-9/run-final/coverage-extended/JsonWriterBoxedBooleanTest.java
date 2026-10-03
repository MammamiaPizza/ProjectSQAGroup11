package com.google.gson.stream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.JsonTreeWriter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.reflect.TypeToken;
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

  public void testLenientWriterAcceptsNonFiniteDoubleValues() throws IOException {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);
    writer.setLenient(true);

    writer.beginArray();
    writer.value(Double.NaN);
    writer.value(Double.POSITIVE_INFINITY);
    writer.value(Double.NEGATIVE_INFINITY);
    writer.endArray();
    writer.close();

    assertEquals("[NaN,Infinity,-Infinity]", output.toString());
  }

  public void testWriterEscapesHtmlAndControlCharactersWhenConfigured() throws IOException {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);
    writer.setIndent("  ");
    writer.setHtmlSafe(true);

    writer.beginObject();
    writer.name("value");
    writer.value("<&>='\n\u2028\u2029");
    writer.endObject();
    writer.close();

    assertEquals("{\n"
        + "  \"value\": \"\\u003c\\u0026\\u003e\\u003d\\u0027\\n\\u2028\\u2029\"\n"
        + "}", output.toString());
  }

  public void testWriterJsonValueWritesRawJsonAndNullArrayElement() throws IOException {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);

    writer.beginArray();
    writer.jsonValue("{\"raw\":true}");
    writer.jsonValue(null);
    writer.endArray();
    writer.close();

    assertEquals("[{\"raw\":true},null]", output.toString());
  }

  public void testWriterRejectsDanglingNameAndFlushAfterClose() throws IOException {
    JsonWriter writer = new JsonWriter(new StringWriter());
    writer.beginObject();
    writer.name("missing");

    try {
      writer.endObject();
      fail("Expected IllegalStateException for a dangling property name");
    } catch (IllegalStateException expected) {
      assertTrue(expected.getMessage().indexOf("Dangling name") >= 0);
    }

    JsonWriter completedWriter = new JsonWriter(new StringWriter());
    completedWriter.beginArray();
    completedWriter.endArray();
    completedWriter.close();

    try {
      completedWriter.flush();
      fail("Expected IllegalStateException when flushing a closed writer");
    } catch (IllegalStateException expected) {
      assertTrue(expected.getMessage().indexOf("closed") >= 0);
    }
  }

  public void testWriterCloseRejectsIncompleteDocument() throws IOException {
    JsonWriter writer = new JsonWriter(new StringWriter());
    writer.beginArray();

    try {
      writer.close();
      fail("Expected IOException when closing an incomplete stream document");
    } catch (IOException expected) {
      assertEquals("Incomplete document", expected.getMessage());
    }
  }

  public void testTreeWriterLenientNumberHandlingAndClosedWriterState() throws IOException {
    JsonTreeWriter strictWriter = new JsonTreeWriter();

    try {
      strictWriter.value(Double.NaN);
      fail("Expected IllegalArgumentException for NaN in strict mode");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().indexOf("forbids") >= 0);
    }

    JsonTreeWriter lenientWriter = new JsonTreeWriter();
    lenientWriter.setLenient(true);
    lenientWriter.beginArray();
    lenientWriter.value(Double.NaN);
    lenientWriter.value((Number) Double.POSITIVE_INFINITY);
    lenientWriter.endArray();

    JsonArray result = lenientWriter.get().getAsJsonArray();
    assertTrue(Double.isNaN(result.get(0).getAsDouble()));
    assertEquals(Double.POSITIVE_INFINITY, result.get(1).getAsDouble());

    lenientWriter.close();
    try {
      lenientWriter.value("after close");
      fail("Expected IllegalStateException when writing after close");
    } catch (IllegalStateException expected) {
      assertNotNull(expected);
    }
  }

  public void testTypeHierarchyFactoryRejectsAdapterResultOfWrongRequestedSubtype()
      throws IOException {
    TypeAdapter<Number> numberAdapter = new TypeAdapter<Number>() {
      @Override public Number read(JsonReader in) throws IOException {
        in.nextDouble();
        return Double.valueOf(1.5d);
      }

      @Override public void write(JsonWriter out, Number value) throws IOException {
        out.value(value);
      }
    };

    TypeAdapterFactory factory =
        TypeAdapters.newTypeHierarchyFactory(Number.class, numberAdapter);
    TypeAdapter<Integer> integerAdapter =
        factory.create(null, TypeToken.get(Integer.class));

    assertNotNull(integerAdapter);

    try {
      integerAdapter.read(new JsonReader(new StringReader("1")));
      fail("Expected JsonSyntaxException when adapter result is not an Integer");
    } catch (JsonSyntaxException expected) {
      assertTrue(expected.getMessage().indexOf(Integer.class.getName()) >= 0);
      assertTrue(expected.getMessage().indexOf(Double.class.getName()) >= 0);
    }
  }
}
