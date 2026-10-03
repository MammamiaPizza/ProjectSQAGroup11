package com.fasterxml.jackson.databind.exc;

 import java.io.IOException;
 import java.util.Map;

 import org.junit.Test;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.fail;

 import com.fasterxml.jackson.core.Version;
 import com.fasterxml.jackson.core.type.TypeReference;
 import com.fasterxml.jackson.databind.DeserializationContext;
 import com.fasterxml.jackson.databind.JsonMappingException;
 import com.fasterxml.jackson.databind.KeyDeserializer;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.module.SimpleModule;

 public class JsonMappingExceptionMessageTest {

     // Enum used for invalid key test
     static enum ABC { A, B, C }

     // Custom key class that deliberately triggers failure when deserialized
     static class BadKey {
         public BadKey(String s) {
             throw new IllegalArgumentException("BadKey always fails");
         }

         @Override
         public String toString() { return "BadKey"; }
     }

     // Helper to count occurrences of a substring
     private static int countOccurrences(String str, String sub) {
         if (str == null || sub == null || sub.isEmpty()) return 0;
         int count = 0;
         int idx = 0;
         while ((idx = str.indexOf(sub, idx)) != -1) {
             count++;
             idx += sub.length();
         }
         return count;
     }

     // ----------------------------------------------------------------------
     // Tests for the bug: duplicate "at [" path markers in error messages
     // ----------------------------------------------------------------------

     @Test
     public void testInvalidEnumMapKeySingleAtMarker() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         try {
             mapper.readValue("{\"value\":1}", new TypeReference<Map<ABC, Integer>>() {});
             fail("Should have thrown JsonMappingException");
         } catch (JsonMappingException e) {
             String msg = e.getMessage();
             assertNotNull(msg);
             int atCount = countOccurrences(msg, "at [");
             assertEquals("Expected exactly one \"at [\" marker in message: " + msg,
                     1, atCount);
         }
     }

     @Test
     public void testInvalidEnumMapKeyInsideObject() throws Exception {
         // Wraps the failing map inside a simple bean to add a path element
         ObjectMapper mapper = new ObjectMapper();
         try {
             mapper.readValue("{\"wrapper\": {\"value\": 123}}",
                     new TypeReference<Map<String, Map<ABC, Integer>>>() {});
             fail("Should have thrown JsonMappingException");
         } catch (JsonMappingException e) {
             String msg = e.getMessage();
             assertNotNull(msg);
             int atCount = countOccurrences(msg, "at [");
             assertEquals("Expected exactly one \"at [\" marker in message: " + msg,
                     1, atCount);
         }
     }

     @Test
     public void testInvalidEnumMapKeyTwoLevels() throws Exception {
         // Map-of-Map, inner key fails; path should be longer but still single "at ["
         ObjectMapper mapper = new ObjectMapper();
         try {
             mapper.readValue("{\"outer\": {\"notA\": \"shouldFail\"}}",
                     new TypeReference<Map<String, Map<ABC, String>>>() {});
             fail("Should have thrown JsonMappingException");
         } catch (JsonMappingException e) {
             String msg = e.getMessage();
             assertNotNull(msg);
             int atCount = countOccurrences(msg, "at [");
             assertEquals("Expected exactly one \"at [\" marker in message: " + msg,
                     1, atCount);
         }
     }

     @Test
     public void testCustomKeyDeserializerFailureSingleAt() throws Exception {
         // Uses a custom key deserializer that throws a mapping exception directly
         SimpleModule module = new SimpleModule("testModule", Version.unknownVersion());
         module.addKeyDeserializer(BadKey.class, new KeyDeserializer() {
             @Override
             public Object deserializeKey(String key, DeserializationContext ctxt)
                     throws IOException {
                 throw ctxt.mappingException("Custom key deserialization failure for: %s", key);
             }
         });

         ObjectMapper mapper = new ObjectMapper();
         mapper.registerModule(module);

         try {
             mapper.readValue("{\"aBadKey\": \"someValue\"}",
                     new TypeReference<Map<BadKey, String>>() {});
             fail("Should have thrown JsonMappingException");
         } catch (JsonMappingException e) {
             String msg = e.getMessage();
             assertNotNull(msg);
             int atCount = countOccurrences(msg, "at [");
             assertEquals("Expected exactly one \"at [\" marker in message: " + msg,
                     1, atCount);
         }
     }

     @Test
     public void testCustomKeyDeserializerFailureInsideObject() throws Exception {
         // Same custom key failure but inside a bean to add path depth
         SimpleModule module = new SimpleModule("testModule", Version.unknownVersion());
         module.addKeyDeserializer(BadKey.class, new KeyDeserializer() {
             @Override
             public Object deserializeKey(String key, DeserializationContext ctxt)
                     throws IOException {
                 throw ctxt.mappingException("Inner key deserialization failure for: %s", key);
             }
         });
         ObjectMapper mapper = new ObjectMapper();
         mapper.registerModule(module);

         try {
             mapper.readValue("{\"outer\": {\"bad\": \"value\"}}",
                     new TypeReference<Map<String, Map<BadKey, String>>>() {});
             fail("Should have thrown JsonMappingException");
         } catch (JsonMappingException e) {
             String msg = e.getMessage();
             assertNotNull(msg);
             int atCount = countOccurrences(msg, "at [");
             assertEquals("Expected exactly one \"at [\" marker in message: " + msg,
                     1, atCount);
         }
     }

     @Test
     public void testValidEnumMapKeyDoesNotProduceAtMarker() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         Map<ABC, Integer> result = mapper.readValue("{\"A\": 10, \"B\": 20}",
                 new TypeReference<Map<ABC, Integer>>() {});
         assertNotNull(result);
         assertEquals(2, result.size());
         // No exception -> no "at [" markers can be counted
     }

     @Test
     public void testMultipleInvalidKeysInMapStillSingleAt() throws Exception {
         // Even if multiple keys are invalid, the first failure triggers exception;
         // the path should only appear once.
         ObjectMapper mapper = new ObjectMapper();
         try {
             mapper.readValue("{\"firstBad\": 1, \"secondBad\": 2}",
                     new TypeReference<Map<ABC, Integer>>() {});
             fail("Should have thrown JsonMappingException");
         } catch (JsonMappingException e) {
             String msg = e.getMessage();
             assertNotNull(msg);
             int atCount = countOccurrences(msg, "at [");
             assertEquals("Expected exactly one \"at [\" marker in message: " + msg,
                     1, atCount);
         }
     }

     @Test
     public void testEnumKeyInvalidFormatExceptionMsgConsistency() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         try {
             mapper.readValue("{\"XYZ\": 0}", new TypeReference<Map<ABC, Integer>>() {});
             fail("Should have thrown JsonMappingException");
         } catch (JsonMappingException e) {
             String msg = e.getMessage();
             assertNotNull(msg);
             // The message should contain the type name and the invalid value
             assertTrue(msg.contains("ABC"));
             assertTrue(msg.contains("XYZ"));
             int atCount = countOccurrences(msg, "at [");
             assertEquals("Expected exactly one \"at [\" marker in message: " + msg,
                     1, atCount);
         }
     }

     @Test
     public void testKeyFailureWithIOExceptionWrapping() throws Exception {
         // A key deserializer that throws an IOException (non-JsonMappingException)
         // should still result in a mapped exception with exactly one "at [" marker.
         SimpleModule module = new SimpleModule("testModule", Version.unknownVersion());
         module.addKeyDeserializer(BadKey.class, new KeyDeserializer() {
             @Override
             public Object deserializeKey(String key, DeserializationContext ctxt)
                     throws IOException {
                 throw new IOException("Low-level I/O error");
             }
         });
         ObjectMapper mapper = new ObjectMapper();
         mapper.registerModule(module);

         try {
             mapper.readValue("{\"x\": 1}", new TypeReference<Map<BadKey, Integer>>() {});
             fail("Should have thrown JsonMappingException");
         } catch (JsonMappingException e) {
             String msg = e.getMessage();
             assertNotNull(msg);
             int atCount = countOccurrences(msg, "at [");
             assertEquals("Expected exactly one \"at [\" marker in message: " + msg,
                     1, atCount);
         }
     }
 }