package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import junit.framework.TestCase;

public final class TypeAdaptersJsonElementTest extends TestCase {
  private final Gson gson = new Gson();

  public void testPrimitiveCannotBeDeserializedAsJsonObject() {
    try {
      gson.fromJson("\"value\"", JsonObject.class);
      fail("Expected a JsonSyntaxException for a JsonPrimitive requested as JsonObject");
    } catch (JsonSyntaxException expected) {
      assertNotNull(expected);
    }
  }

  public void testArrayCannotBeDeserializedAsJsonObject() {
    try {
      gson.fromJson("[1,2]", JsonObject.class);
      fail("Expected a JsonSyntaxException for a JsonArray requested as JsonObject");
    } catch (JsonSyntaxException expected) {
      assertNotNull(expected);
    }
  }

  public void testObjectDeserializesAsJsonObject() {
    JsonObject object = gson.fromJson("{\"name\":\"gson\",\"enabled\":true}", JsonObject.class);

    assertNotNull(object);
    assertEquals("gson", object.get("name").getAsString());
    assertTrue(object.get("enabled").getAsBoolean());
  }

  public void testArrayDeserializesAsJsonArray() {
    JsonArray array = gson.fromJson("[1,true,\"text\",null]", JsonArray.class);

    assertNotNull(array);
    assertEquals(4, array.size());
    assertEquals(1, array.get(0).getAsInt());
    assertTrue(array.get(1).getAsBoolean());
    assertEquals("text", array.get(2).getAsString());
    assertTrue(array.get(3).isJsonNull());
  }

  public void testNullCannotBeDeserializedAsJsonObject() {
    try {
      gson.fromJson("null", JsonObject.class);
      fail("Expected a JsonSyntaxException for a JsonNull requested as JsonObject");
    } catch (JsonSyntaxException expected) {
      assertNotNull(expected);
    }
  }

  public void testJsonElementDeserializationPreservesElementKinds() {
    JsonElement object = gson.fromJson("{\"a\":1}", JsonElement.class);
    JsonElement array = gson.fromJson("[false]", JsonElement.class);
    JsonElement primitive = gson.fromJson("\"text\"", JsonElement.class);
    JsonElement nullElement = gson.fromJson("null", JsonElement.class);

    assertTrue(object.isJsonObject());
    assertEquals(1, object.getAsJsonObject().get("a").getAsInt());
    assertTrue(array.isJsonArray());
    assertFalse(array.getAsJsonArray().get(0).getAsBoolean());
    assertTrue(primitive.isJsonPrimitive());
    assertEquals("text", primitive.getAsString());
    assertTrue(nullElement.isJsonNull());
  }

  public void testJsonElementSerializationWritesPrimitiveAndNull() {
    assertEquals("\"text\"", gson.toJson(new JsonPrimitive("text"), JsonElement.class));
    assertEquals("null", gson.toJson(JsonNull.INSTANCE, JsonElement.class));
  }
}