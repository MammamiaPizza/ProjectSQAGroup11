package com.google.gson.internal.bind;

 import com.google.gson.JsonArray;
 import com.google.gson.JsonElement;
 import com.google.gson.JsonNull;
 import com.google.gson.JsonObject;
 import com.google.gson.JsonPrimitive;
 import com.google.gson.stream.JsonToken;
 import java.io.IOException;
 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for {@link JsonTreeReader#skipValue()} revealing a stack underflow bug
  * (ArrayIndexOutOfBoundsException) when skipping a root-level value.
  */
 public class JsonTreeReaderTest {

     // --- Root-level skipValue (the buggy area) ---

     @Test
     public void testSkipValue_RootPrimitive() throws IOException {
         JsonTreeReader reader = new JsonTreeReader(new JsonPrimitive("hello"));
         reader.skipValue();
         assertEquals(JsonToken.END_DOCUMENT, reader.peek());
     }

     @Test
     public void testSkipValue_RootEmptyObject() throws IOException {
         JsonTreeReader reader = new JsonTreeReader(new JsonObject());
         reader.skipValue();
         assertEquals(JsonToken.END_DOCUMENT, reader.peek());
     }

     @Test
     public void testSkipValue_RootFilledObject() throws IOException {
         JsonObject obj = new JsonObject();
         obj.addProperty("key", "value");
         JsonTreeReader reader = new JsonTreeReader(obj);
         reader.skipValue();
         assertEquals(JsonToken.END_DOCUMENT, reader.peek());
     }

     @Test
     public void testSkipValue_RootEmptyArray() throws IOException {
         JsonTreeReader reader = new JsonTreeReader(new JsonArray());
         reader.skipValue();
         assertEquals(JsonToken.END_DOCUMENT, reader.peek());
     }

     @Test
     public void testSkipValue_RootFilledArray() throws IOException {
         JsonArray arr = new JsonArray();
         arr.add(new JsonPrimitive(42));
         JsonTreeReader reader = new JsonTreeReader(arr);
         reader.skipValue();
         assertEquals(JsonToken.END_DOCUMENT, reader.peek());
     }

     @Test
     public void testSkipValue_RootNull() throws IOException {
         JsonTreeReader reader = new JsonTreeReader(JsonNull.INSTANCE);
         reader.skipValue();
         assertEquals(JsonToken.END_DOCUMENT, reader.peek());
     }

     // --- Non-root skipValue (should work even on buggy version) ---

     @Test
     public void testSkipValue_InsideArray_EmptyObject() throws IOException {
         JsonArray outer = new JsonArray();
         outer.add(new JsonObject());
         JsonTreeReader reader = new JsonTreeReader(outer);
         reader.beginArray();
         assertEquals(JsonToken.BEGIN_OBJECT, reader.peek());
         reader.skipValue();                // skip the empty object inside the array
         assertFalse(reader.hasNext());
         reader.endArray();
         assertEquals(JsonToken.END_DOCUMENT, reader.peek());
     }

     @Test
     public void testSkipValue_InsideArray_FilledObject() throws IOException {
         JsonObject inner = new JsonObject();
         inner.addProperty("a", 1);
         JsonArray outer = new JsonArray();
         outer.add(inner);
         JsonTreeReader reader = new JsonTreeReader(outer);
         reader.beginArray();
         assertEquals(JsonToken.BEGIN_OBJECT, reader.peek());
         reader.skipValue();                // skip the filled object
         assertFalse(reader.hasNext());
         reader.endArray();
         assertEquals(JsonToken.END_DOCUMENT, reader.peek());
     }

     @Test
     public void testSkipValue_AfterNextName_SkipsValue() throws IOException {
         JsonObject obj = new JsonObject();
         obj.addProperty("name", "Copilot");
         JsonTreeReader reader = new JsonTreeReader(obj);
         reader.beginObject();
         assertEquals("name", reader.nextName());
         reader.skipValue();                // skip "Copilot"
         assertFalse(reader.hasNext());
         reader.endObject();
         assertEquals(JsonToken.END_DOCUMENT, reader.peek());
     }

     @Test
     public void testSkipValue_AfterNextName_NestedObject() throws IOException {
         JsonObject inner = new JsonObject();
         inner.addProperty("x", "y");
         JsonObject outer = new JsonObject();
         outer.add("inner", inner);
         JsonTreeReader reader = new JsonTreeReader(outer);
         reader.beginObject();
         assertEquals("inner", reader.nextName());
         reader.skipValue();                // skip the whole inner object
         assertFalse(reader.hasNext());
         reader.endObject();
         assertEquals(JsonToken.END_DOCUMENT, reader.peek());
     }

     @Test
     public void testSkipValue_MultipleValuesInArray() throws IOException {
         JsonArray arr = new JsonArray();
         arr.add(new JsonPrimitive(1));
         arr.add(new JsonPrimitive(2));
         arr.add(new JsonPrimitive(3));
         JsonTreeReader reader = new JsonTreeReader(arr);
         reader.beginArray();
         reader.skipValue();                // skip 1
         assertEquals(JsonToken.NUMBER, reader.peek());
         assertEquals(2, reader.nextInt());
         reader.skipValue();                // skip 3
         assertFalse(reader.hasNext());
         reader.endArray();
         assertEquals(JsonToken.END_DOCUMENT, reader.peek());
     }

     @Test
     public void testSkipValue_OnEndDocumentThrows() throws IOException {
         JsonTreeReader reader = new JsonTreeReader(new JsonPrimitive(1));
         reader.nextInt();                  // consume the only value
         assertEquals(JsonToken.END_DOCUMENT, reader.peek());
         // skipValue on exhausted input also hits the stack underflow bug
         try {
             reader.skipValue();
             fail("Expected ArrayIndexOutOfBoundsException");
         } catch (ArrayIndexOutOfBoundsException expected) {
             // the bug manifests here as well
         }
     }
 }