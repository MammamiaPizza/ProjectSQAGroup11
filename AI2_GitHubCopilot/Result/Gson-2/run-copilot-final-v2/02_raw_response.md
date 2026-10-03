package com.google.gson.internal.bind;

 import com.google.gson.JsonArray;
 import com.google.gson.JsonElement;
 import com.google.gson.JsonNull;
 import com.google.gson.JsonObject;
 import com.google.gson.JsonPrimitive;
 import com.google.gson.TypeAdapter;
 import com.google.gson.stream.JsonReader;
 import com.google.gson.stream.JsonWriter;
 import java.io.IOException;
 import java.io.StringReader;
 import java.io.StringWriter;
 import junit.framework.TestCase;

 public class TypeAdaptersTest extends TestCase {

     private TypeAdapter<JsonElement> adapter;

     @Override
     protected void setUp() {
         adapter = TypeAdapters.JSON_ELEMENT;
     }

     private JsonElement read(String json) throws IOException {
         JsonReader reader = new JsonReader(new StringReader(json));
         reader.setLenient(true);
         return adapter.read(reader);
     }

     private String write(JsonElement element) throws IOException {
         StringWriter sw = new StringWriter();
         JsonWriter writer = new JsonWriter(sw);
         adapter.write(writer, element);
         writer.close();
         return sw.toString();
     }

     public void testReadJsonPrimitiveString() throws IOException {
         JsonElement element = read("\"hello\"");
         assertNotNull(element);
         assertTrue(element instanceof JsonPrimitive);
         assertTrue(element.getAsJsonPrimitive().isString());
         assertEquals("hello", element.getAsString());
         // Bug focus: cast to a wrong type should throw state exception, not ClassCastException
         try {
             element.getAsJsonObject();
             fail("Expected IllegalStateException for string primitive as object");
         } catch (IllegalStateException expected) {
             // correct contract
         } catch (ClassCastException e) {
             fail("ClassCastException occurred – regression of bug 719");
         }
     }

     public void testReadJsonPrimitiveNumber() throws IOException {
         JsonElement element = read("42");
         assertNotNull(element);
         assertTrue(element instanceof JsonPrimitive);
         assertTrue(element.getAsJsonPrimitive().isNumber());
         assertEquals(42, element.getAsInt());
         try {
             element.getAsJsonArray();
             fail("Expected IllegalStateException for number primitive as array");
         } catch (IllegalStateException expected) {
             // correct
         } catch (ClassCastException e) {
             fail("ClassCastException occurred – regression");
         }
     }

     public void testReadJsonNull() throws IOException {
         JsonElement element = read("null");
         assertNotNull(element);
         assertTrue(element instanceof JsonNull);
         try {
             element.getAsJsonObject();
             fail("Expected IllegalStateException for null as object");
         } catch (IllegalStateException expected) {
             // correct
         }
     }

     public void testReadJsonArray() throws IOException {
         JsonElement element = read("[1, \"two\", true]");
         assertNotNull(element);
         assertTrue(element instanceof JsonArray);
         JsonArray array = element.getAsJsonArray();
         assertEquals(3, array.size());
         assertEquals(1, array.get(0).getAsInt());
         assertEquals("two", array.get(1).getAsString());
         assertTrue(array.get(2).getAsBoolean());
     }

     public void testReadEmptyJsonArray() throws IOException {
         JsonElement element = read("[]");
         assertTrue(element instanceof JsonArray);
         assertEquals(0, element.getAsJsonArray().size());
     }

     public void testReadJsonObject() throws IOException {
         JsonElement element = read("{\"key\":\"value\"}");
         assertNotNull(element);
         assertTrue(element instanceof JsonObject);
         JsonObject obj = element.getAsJsonObject();
         assertEquals(1, obj.entrySet().size());
         assertEquals("value", obj.get("key").getAsString());
     }

     public void testReadEmptyJsonObject() throws IOException {
         JsonElement element = read("{}");
         assertTrue(element instanceof JsonObject);
         assertEquals(0, element.getAsJsonObject().entrySet().size());
     }

     public void testReadNestedJson() throws IOException {
         JsonElement element = read("{\"outer\":{\"inner\":[1,2]}}");
         assertTrue(element instanceof JsonObject);
         JsonObject outer = element.getAsJsonObject();
         JsonElement inner = outer.get("outer");
         assertTrue(inner instanceof JsonObject);
         JsonArray innerArray = inner.getAsJsonObject().getAsJsonArray("inner");
         assertEquals(2, innerArray.size());
         assertEquals(1, innerArray.get(0).getAsInt());
     }

     public void testRoundTripJsonPrimitive() throws IOException {
         JsonPrimitive original = new JsonPrimitive("test");
         String json = write(original);
         JsonElement result = read(json);
         assertTrue(result instanceof JsonPrimitive);
         assertEquals("test", result.getAsString());
         assertFalse(result.getAsJsonPrimitive().isNumber());
     }

     public void testRoundTripJsonArray() throws IOException {
         JsonArray original = new JsonArray();
         original.add(new JsonPrimitive(1));
         original.add(new JsonPrimitive("two"));
         String json = write(original);
         JsonElement result = read(json);
         assertTrue(result instanceof JsonArray);
         JsonArray array = result.getAsJsonArray();
         assertEquals(2, array.size());
         assertEquals(1, array.get(0).getAsInt());
         assertEquals("two", array.get(1).getAsString());
     }

     public void testRoundTripJsonObject() throws IOException {
         JsonObject original = new JsonObject();
         original.addProperty("a", 1);
         original.addProperty("b", "str");
         String json = write(original);
         JsonElement result = read(json);
         assertTrue(result instanceof JsonObject);
         JsonObject obj = result.getAsJsonObject();
         assertEquals(1, obj.get("a").getAsInt());
         assertEquals("str", obj.get("b").getAsString());
     }

     public void testWriteNullReturnsNull() throws IOException {
         String json = write(null);
         assertEquals("null", json.trim());
     }
 }