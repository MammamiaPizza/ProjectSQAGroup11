package com.fasterxml.jackson.databind.deser.std;

 import static org.junit.Assert.*;

 import java.io.IOException;
 import java.util.List;

 import org.junit.Test;

 import com.fasterxml.jackson.core.*;
 import com.fasterxml.jackson.databind.*;
 import com.fasterxml.jackson.databind.module.SimpleModule;

 public class StringArrayDeserializerTest {

     private int getArrayIndex(JsonMappingException e) {
         List<JsonMappingException.Reference> path = e.getPath();
         if (!path.isEmpty()) {
             return path.get(0).getIndex();
         }
         return -1;
     }

     @Test
     public void testArrayIndexForExceptions() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         String json = "[\"valid\", 123]";
         try {
             mapper.readValue(json, String[].class);
             fail("Expected JsonMappingException");
         } catch (JsonMappingException e) {
             assertEquals("Second element index must be 1 (0-based)", 1, getArrayIndex(e));
         }
     }

     @Test
     public void testEmptyArray() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         String[] result = mapper.readValue("[]", String[].class);
         assertNotNull(result);
         assertEquals(0, result.length);
     }

     @Test
     public void testSingleValidString() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         String[] result = mapper.readValue("[\"hello\"]", String[].class);
         assertArrayEquals(new String[]{"hello"}, result);
     }

     @Test
     public void testAllValidStrings() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         String[] result = mapper.readValue("[\"a\",\"b\",\"c\"]", String[].class);
         assertArrayEquals(new String[]{"a", "b", "c"}, result);
     }

     @Test
     public void testNullElements() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         String[] result = mapper.readValue("[\"a\",null,\"c\"]", String[].class);
         assertArrayEquals(new String[]{"a", null, "c"}, result);
     }

     @Test
     public void testFirstElementInvalidIndex() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         String json = "[123, \"valid\"]";
         try {
             mapper.readValue(json, String[].class);
             fail("Expected JsonMappingException");
         } catch (JsonMappingException e) {
             assertEquals("First element index must be 0", 0, getArrayIndex(e));
         }
     }

     @Test
     public void testNonArraySingleValueAccepted() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
         String[] result = mapper.readValue("\"hello\"", String[].class);
         assertArrayEquals(new String[]{"hello"}, result);
     }

     @Test(expected = JsonMappingException.class)
     public void testNonArraySingleValueNotAccepted() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         mapper.readValue("\"hello\"", String[].class);
     }

     @Test
     public void testEmptyStringAsNullArray() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
         String[] result = mapper.readValue("\"\"", String[].class);
         assertNull(result);
     }

     @Test
     public void testCustomElementDeserializerIndex() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         SimpleModule module = new SimpleModule("test", Version.unknownVersion());
         module.addDeserializer(String.class, new JsonDeserializer<String>() {
             @Override
             public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
{
                 if (p.getCurrentToken() == JsonToken.VALUE_STRING) return p.getText();
                 if (p.getCurrentToken() == JsonToken.VALUE_NULL) return getNullValue();
                 throw ctxt.mappingException(String.class, p.getCurrentToken());
             }

             @Override
             public String getNullValue() {
                 return null;
             }
         });
         mapper.registerModule(module);
         String json = "[\"valid\", 123]";
         try {
             mapper.readValue(json, String[].class);
             fail("Expected exception");
         } catch (JsonMappingException e) {
             assertEquals("Custom deserializer must report index 1", 1, getArrayIndex(e));
         }
     }

     @Test
     public void testNonScalarTokenIndex() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         String json = "[\"valid\", {\"key\":\"value\"}]";
         try {
             mapper.readValue(json, String[].class);
             fail("Expected exception");
         } catch (JsonMappingException e) {
             assertEquals("Non-scalar token at index 1 must be 1", 1, getArrayIndex(e));
         }
     }

     @Test
     public void testNormalLargeArray() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         String[] result = mapper.readValue("[\"a\",\"b\",\"c\",\"d\",\"e\"]", String[].class);
         assertEquals(5, result.length);
         assertEquals("c", result[2]);
     }
 }
