package com.google.gson.internal.bind;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonToken;
import org.junit.Test;

public class JsonTreeReaderGeneratedTest {

  @Test
  public void testSkipValue_emptyRootJsonObjectConsumesWholeValue() throws Exception {
    JsonTreeReader reader = new JsonTreeReader(new JsonObject());

    reader.skipValue();

    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  @Test
  public void testSkipValue_filledRootJsonObjectConsumesWholeValue() throws Exception {
    JsonObject object = new JsonObject();
    object.addProperty("name", "value");
    object.addProperty("number", 1);

    JsonTreeReader reader = new JsonTreeReader(object);

    reader.skipValue();

    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  @Test
  public void testSkipValue_nestedObjectLeavesContainingObjectReadable() throws Exception {
    JsonObject child = new JsonObject();
    child.addProperty("nested", "value");

    JsonObject root = new JsonObject();
    root.add("child", child);

    JsonTreeReader reader = new JsonTreeReader(root);
    reader.beginObject();
    assertEquals("child", reader.nextName());

    reader.skipValue();

    assertFalse(reader.hasNext());
    reader.endObject();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  @Test
  public void testReadMixedObjectAndArrayValues() throws Exception {
    JsonArray values = new JsonArray();
    values.add(new JsonPrimitive("text"));
    values.add(new JsonPrimitive(12));
    values.add(new JsonPrimitive(true));
    values.add(JsonNull.INSTANCE);

    JsonObject root = new JsonObject();
    root.add("values", values);
    root.addProperty("tail", "done");

    JsonTreeReader reader = new JsonTreeReader(root);

    assertEquals(JsonToken.BEGIN_OBJECT, reader.peek());
    reader.beginObject();
    assertEquals("values", reader.nextName());
    assertEquals("$.values", reader.getPath());

    reader.beginArray();
    assertEquals("$.values[0]", reader.getPath());
    assertTrue(reader.hasNext());
    assertEquals("text", reader.nextString());
    assertEquals("$.values[1]", reader.getPath());
    assertEquals(12, reader.nextInt());
    assertTrue(reader.nextBoolean());
    reader.nextNull();
    assertFalse(reader.hasNext());
    reader.endArray();

    assertTrue(reader.hasNext());
    assertEquals("tail", reader.nextName());
    assertEquals("done", reader.nextString());
    assertFalse(reader.hasNext());
    reader.endObject();

    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  @Test
  public void testNumericAndStringConversions() throws Exception {
    JsonArray values = new JsonArray();
    values.add(new JsonPrimitive("123"));
    values.add(new JsonPrimitive(456L));
    values.add(new JsonPrimitive("7.5"));

    JsonTreeReader reader = new JsonTreeReader(values);
    reader.beginArray();

    assertEquals(123L, reader.nextLong());
    assertEquals("456", reader.nextString());
    assertEquals(7.5d, reader.nextDouble(), 0.0d);

    reader.endArray();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  @Test
  public void testNonFiniteDoubleRequiresLenientMode() throws Exception {
    JsonTreeReader strictReader = new JsonTreeReader(new JsonPrimitive(Double.NaN));

    try {
      strictReader.nextDouble();
      fail("Strict JSON reader must reject NaN");
    } catch (NumberFormatException expected) {
      assertTrue(expected.getMessage().contains("NaN"));
    }

    JsonTreeReader lenientReader = new JsonTreeReader(new JsonPrimitive(Double.NaN));
    lenientReader.setLenient(true);

    assertTrue(Double.isNaN(lenientReader.nextDouble()));
    assertEquals(JsonToken.END_DOCUMENT, lenientReader.peek());
  }

  @Test
  public void testPromoteNameToValueReadsNameThenAssociatedValue() throws Exception {
    JsonObject object = new JsonObject();
    object.addProperty("answer", 42);

    JsonTreeReader reader = new JsonTreeReader(object);
    reader.beginObject();

    reader.promoteNameToValue();

    assertEquals(JsonToken.STRING, reader.peek());
    assertEquals("answer", reader.nextString());
    assertEquals(42, reader.nextInt());
    assertFalse(reader.hasNext());

    reader.endObject();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  @Test
  public void testWrongTypedReadThrowsAndDoesNotConsumeValue() throws Exception {
    JsonTreeReader reader = new JsonTreeReader(new JsonPrimitive("notBoolean"));

    try {
      reader.nextBoolean();
      fail("Reading a string as a boolean must fail");
    } catch (IllegalStateException expected) {
      assertTrue(expected.getMessage().contains("Expected BOOLEAN"));
    }

    assertEquals("notBoolean", reader.nextString());
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  @Test
  public void testCloseMakesReaderUnavailable() throws Exception {
    JsonTreeReader reader = new JsonTreeReader(new JsonPrimitive("value"));

    reader.close();

    try {
      reader.peek();
      fail("A closed reader must reject further reads");
    } catch (IllegalStateException expected) {
      assertEquals("JsonReader is closed", expected.getMessage());
    }
  }

  @Test
  public void testDeeplyNestedArraysGrowInternalStackAndCanBeRead() throws Exception {
    JsonArray outer = new JsonArray();
    JsonArray current = outer;
    for (int i = 0; i < 40; i++) {
      JsonArray nested = new JsonArray();
      current.add(nested);
      current = nested;
    }
    current.add(new JsonPrimitive("leaf"));

    JsonTreeReader reader = new JsonTreeReader(outer);

    for (int i = 0; i < 41; i++) {
      assertEquals(JsonToken.BEGIN_ARRAY, reader.peek());
      reader.beginArray();
    }

    assertEquals("leaf", reader.nextString());

    for (int i = 0; i < 41; i++) {
      assertFalse(reader.hasNext());
      reader.endArray();
    }

    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }
}
