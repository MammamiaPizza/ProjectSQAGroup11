package com.google.gson.regression;

 import com.google.gson.Gson;
 import com.google.gson.TypeAdapter;
 import com.google.gson.annotations.JsonAdapter;
 import com.google.gson.stream.JsonReader;
 import com.google.gson.stream.JsonWriter;
 import com.google.gson.stream.JsonToken;
 import junit.framework.TestCase;
 import java.io.IOException;

 /**
  * Tests for {@link com.google.gson.internal.bind.JsonAdapterAnnotationTypeAdapterFactory}
  * ensuring that the {@link JsonAdapter#nullSafe()} attribute is properly respected.
  */
 public class JsonAdapterNullSafeTest extends TestCase {

     // Type that uses an adapter and relies on the default nullSafe=true
     @JsonAdapter(value = SafeTypeAdapter.class)
     public static class SafeType {
         String data;
         SafeType(String data) { this.data = data; }
     }

     // Type that uses an adapter with nullSafe explicitly turned off
     @JsonAdapter(value = UnsafeTypeAdapter.class, nullSafe = false)
     public static class UnsafeType {
         String data;
         UnsafeType(String data) { this.data = data; }
     }

     // Adapters that record whether they received a null value (or null JSON token)
     public static class SafeTypeAdapter extends TypeAdapter<SafeType> {
         static boolean writeReceivedNull = false;
         static boolean readReceivedNullToken = false;

         @Override
         public void write(JsonWriter out, SafeType value) throws IOException {
             if (value == null) {
                 writeReceivedNull = true;
                 out.nullValue();
             } else {
                 out.value(value.data);
             }
         }

         @Override
         public SafeType read(JsonReader in) throws IOException {
             if (in.peek() == JsonToken.NULL) {
                 readReceivedNullToken = true;
                 in.nextNull();
                 return null;
             }
             return new SafeType(in.nextString());
         }
     }

     public static class UnsafeTypeAdapter extends TypeAdapter<UnsafeType> {
         static boolean writeReceivedNull = false;
         static boolean readReceivedNullToken = false;

         @Override
         public void write(JsonWriter out, UnsafeType value) throws IOException {
             if (value == null) {
                 writeReceivedNull = true;
                 out.nullValue();
             } else {
                 out.value(value.data);
             }
         }

         @Override
         public UnsafeType read(JsonReader in) throws IOException {
             if (in.peek() == JsonToken.NULL) {
                 readReceivedNullToken = true;
                 in.nextNull();
                 return null;
             }
             return new UnsafeType(in.nextString());
         }
     }

     private Gson gson;

     @Override
     protected void setUp() {
         gson = new Gson();
         SafeTypeAdapter.writeReceivedNull = false;
         SafeTypeAdapter.readReceivedNullToken = false;
         UnsafeTypeAdapter.writeReceivedNull = false;
         UnsafeTypeAdapter.readReceivedNullToken = false;
     }

     /**
      * When nullSafe=true (the default), serializing a null value must produce {@code "null"}
      * and must <strong>not</strong> delegate to {@link TypeAdapter#write(JsonWriter, Object)}.
      */
     public void testNullSafeTrueSerialization() {
         String json = gson.toJson(null, SafeType.class);
         assertEquals("null", json);
         assertFalse("Adapter.write was called for null when nullSafe=true",
                 SafeTypeAdapter.writeReceivedNull);
     }

     /**
      * When nullSafe=true, deserializing a JSON null token must return {@code null}
      * without calling {@link TypeAdapter#read(JsonReader)}.
      */
     public void testNullSafeTrueDeserialization() {
         SafeType value = gson.fromJson("null", SafeType.class);
         assertNull(value);
         assertFalse("Adapter.read was called for null token when nullSafe=true",
                 SafeTypeAdapter.readReceivedNullToken);
     }

     /**
      * When nullSafe=false, serializing a null value must delegate to the adapter's
      * {@link TypeAdapter#write(JsonWriter, Object)} method, passing the null value.
      */
     public void testNullSafeFalseSerialization() {
         String json = gson.toJson(null, UnsafeType.class);
         assertEquals("null", json);
         assertTrue("Adapter.write must be called for null when nullSafe=false",
                 UnsafeTypeAdapter.writeReceivedNull);
     }

     /**
      * When nullSafe=false, deserializing a JSON null token must delegate to the adapter's
      * {@link TypeAdapter#read(JsonReader)} method, allowing the adapter to see the null token.
      */
     public void testNullSafeFalseDeserialization() {
         UnsafeType value = gson.fromJson("null", UnsafeType.class);
         assertNull(value);
         assertTrue("Adapter.read must be called for null token when nullSafe=false",
                 UnsafeTypeAdapter.readReceivedNullToken);
     }

     /**
      * Sanity check: the factory returns {@code null} for types without the annotation,
      * so the default adapter handles null without exception.
      */
     public void testNoAnnotationHandlesNullSafely() {
         String json = gson.toJson(null, String.class);
         assertEquals("null", json);
         String value = gson.fromJson("null", String.class);
         assertNull(value);
     }

     /**
      * Non-null values must round-trip correctly regardless of the {@code nullSafe} setting.
      */
     public void testNonNullRoundTrip() {
         SafeType safe = new SafeType("hello");
         String json = gson.toJson(safe, SafeType.class);
         SafeType back = gson.fromJson(json, SafeType.class);
         assertEquals("hello", back.data);

         UnsafeType unsafe = new UnsafeType("world");
         json = gson.toJson(unsafe, UnsafeType.class);
         UnsafeType back2 = gson.fromJson(json, UnsafeType.class);
         assertEquals("world", back2.data);
     }

     /**
      * Mixing {@code nullSafe=true} and {@code nullSafe=false} across different types
      * must not leak behaviour between adapters.
      */
     public void testMixOfNullSafeSettingsDoesNotInterfere() {
         // Trigger unsafe path – adapter must be called with null
         gson.toJson(null, UnsafeType.class);
         assertTrue(UnsafeTypeAdapter.writeReceivedNull);

         // Reset safe adapter flags and verify the safe path still prevents delegation
         SafeTypeAdapter.writeReceivedNull = false;
         gson.toJson(null, SafeType.class);
         assertFalse("Adapter.write must not be called for nullSafe=true, even after using a
nullSafe=false type",
                 SafeTypeAdapter.writeReceivedNull);
     }
 }