package com.fasterxml.jackson.databind.deser.std;

 import static org.junit.Assert.*;
 import org.junit.Test;

 import java.io.IOException;

 import com.fasterxml.jackson.core.JsonParser;
 import com.fasterxml.jackson.core.JsonToken;
 import com.fasterxml.jackson.databind.*;
 import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

 /**
  * Tests for {@link StringArrayDeserializer}, focusing on the NullPointerException bug
  * when encountering a null element without a custom element deserializer.
  */
 public class StringArrayDeserializerBug3Test {

     private final ObjectMapper mapper = new ObjectMapper();

     @Test
     public void testNormalStringArray() throws Exception {
         String[] result = mapper.readValue("[\"a\",\"b\"]", String[].class);
         assertArrayEquals(new String[] {"a", "b"}, result);
     }

     @Test
     public void testEmptyArray() throws Exception {
         String[] result = mapper.readValue("[]", String[].class);
         assertEquals(0, result.length);
     }

     @Test
     public void testSingleElement() throws Exception {
         String[] result = mapper.readValue("[\"only\"]", String[].class);
         assertArrayEquals(new String[] {"only"}, result);
     }

     /**
      * Triggers the bug: default-path deserialization with a null element
      * calls <code>_elementDeserializer.getNullValue()</code> while
      * <code>_elementDeserializer</code> is null.
      */
     @Test
     public void testArrayWithNullElement() throws Exception {
         String[] result = mapper.readValue("[\"a\",null,\"b\"]", String[].class);
         assertArrayEquals(new String[] {"a", null, "b"}, result);
     }

     @Test
     public void testManyElements() throws Exception {
         final int count = 20;
         StringBuilder sb = new StringBuilder("[");
         String[] expected = new String[count];
         for (int i = 0; i < count; i++) {
             if (i > 0) sb.append(",");
             String val = "s" + i;
             sb.append('"').append(val).append('"');
             expected[i] = val;
         }
         sb.append("]");
         String[] result = mapper.readValue(sb.toString(), String[].class);
         assertArrayEquals(expected, result);
     }

     @Test(expected = JsonMappingException.class)
     public void testNonArrayScalarThrowsException() throws Exception {
         mapper.readValue("\"hello\"", String[].class);
     }

     @Test
     public void testNonArrayScalarWithAcceptSingleValueAsArray() throws Exception {
         ObjectMapper m = new ObjectMapper();
         m.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
         String[] result = m.readValue("\"hello\"", String[].class);
         assertArrayEquals(new String[] {"hello"}, result);
     }

     @Test(expected = NullPointerException.class)
     public void testNullTokenWithAcceptSingleValueAsArray() throws Exception {
         ObjectMapper m = new ObjectMapper();
         m.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
         m.readValue("null", String[].class);
     }

     @Test
     public void testEmptyStringAsNullArray() throws Exception {
         ObjectMapper m = new ObjectMapper();
         m.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
         assertNull(m.readValue("\"\"", String[].class));
     }

     @Test
     public void testArrayWithOnlyNulls() throws Exception {
         String[] result = mapper.readValue("[null,null]", String[].class);
         assertArrayEquals(new String[] {null, null}, result);
     }

     /**
      * Exercise the else-branch calling {@code _parseString} by providing a
      * non-string token (a number).
      */
     @Test
     public void testArrayWithNonStringToken() throws Exception {
         String[] result = mapper.readValue("[\"abc\",123]", String[].class);
         assertArrayEquals(new String[] {"abc", "123"}, result);
     }

     /**
      * Verify the custom contextual path does not NPE on null elements
      * (the bug only affects the default inline path).
      */
     @Test
     public void testCustomContextualDeserializerPath() throws Exception {
         ObjectMapper m = new ObjectMapper();
         Container c = m.readValue("{\"items\":[\"a\",null,\"b\"]}", Container.class);
         assertArrayEquals(new String[] {"custom_a", null, "custom_b"}, c.items);
     }

     // ----------------------------------------------------------------------
     // Helper POJO with a custom content deserializer to force use of
     // _deserializeCustom and contextual element deserializer.
     // ----------------------------------------------------------------------

     public static class Container {
         @JsonDeserialize(contentUsing = CustomStringDeserializer.class)
         public String[] items;
     }

     public static class CustomStringDeserializer extends JsonDeserializer<String> {
         @Override
         public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
             // called only for non-null tokens in _deserializeCustom
             return "custom_" + p.getText();
         }
     }
 }