package com.google.gson.internal.bind;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonToken;
import org.junit.Test;

public class JsonTreeReaderRegressionTest {

  @Test
  public void skipValue_filledRootObjectEndsDocument() throws Exception {
    JsonObject object = new JsonObject();
    object.addProperty("name", "value");
    object.addProperty("count", 2);

    JsonTreeReader reader = new JsonTreeReader(object);
    reader.skipValue();

    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    assertEquals("$", reader.getPath());
  }

  @Test
  public void skipValue_emptyRootObjectEndsDocument() throws Exception {
    JsonTreeReader reader = new JsonTreeReader(new JsonObject());

    reader.skipValue();

    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    assertEquals("$", reader.getPath());
  }

  @Test
  public void skipValue_rootArrayEndsDocument() throws Exception {
    JsonArray array = new JsonArray();
    array.add(new JsonPrimitive("first"));
    array.add(new JsonPrimitive(2));

    JsonTreeReader reader = new JsonTreeReader(array);
    reader.skipValue();

    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  @Test
  public void skipValue_objectMemberValueLeavesFollowingMemberReadable() throws Exception {
    JsonObject object = new JsonObject();
    object.addProperty("ignored", "value");
    object.addProperty("kept", 7);

    JsonTreeReader reader = new JsonTreeReader(object);
    reader.beginObject();
    assertEquals("ignored", reader.nextName());

    reader.skipValue();

    assertTrueHasNext(reader);
    assertEquals("kept", reader.nextName());
    assertEquals(7, reader.nextInt());
    assertFalse(reader.hasNext());
    reader.endObject();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  @Test
  public void skipValue_atObjectNameConsumesNameAndLeavesItsValueReadable() throws Exception {
    JsonObject object = new JsonObject();
    object.addProperty("first", "value");
    object.addProperty("second", "kept");

    JsonTreeReader reader = new JsonTreeReader(object);
    reader.beginObject();

    reader.skipValue();

    assertEquals(JsonToken.STRING, reader.peek());
    assertEquals("value", reader.nextString());
    assertEquals("second", reader.nextName());
    assertEquals("kept", reader.nextString());
    assertFalse(reader.hasNext());
    reader.endObject();
  }

  @Test
  public void skipValue_arrayObjectElementLeavesFollowingElementReadable() throws Exception {
    JsonObject skipped = new JsonObject();
    skipped.addProperty("nested", true);
    JsonArray array = new JsonArray();
    array.add(skipped);
    array.add(new JsonPrimitive("after"));

    JsonTreeReader reader = new JsonTreeReader(array);
    reader.beginArray();

    reader.skipValue();

    assertEquals(JsonToken.STRING, reader.peek());
    assertEquals("after", reader.nextString());
    assertFalse(reader.hasNext());
    reader.endArray();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  @Test(expected = IllegalStateException.class)
  public void skipValue_afterCloseThrowsIllegalStateException() throws Exception {
    JsonTreeReader reader = new JsonTreeReader(new JsonPrimitive("value"));
    reader.close();

    reader.skipValue();
  }

  private static void assertTrueHasNext(JsonTreeReader reader) throws Exception {
    assertEquals(true, reader.hasNext());
  }
}