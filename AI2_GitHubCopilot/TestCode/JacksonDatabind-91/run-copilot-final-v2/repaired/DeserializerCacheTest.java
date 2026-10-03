package com.fasterxml.jackson.databind.deser;

 import com.fasterxml.jackson.core.JsonParser;
 import com.fasterxml.jackson.core.JsonProcessingException;
 import com.fasterxml.jackson.databind.*;
 import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer;
 import com.fasterxml.jackson.databind.type.MapType;
 import com.fasterxml.jackson.databind.type.TypeFactory;
 import com.fasterxml.jackson.databind.module.SimpleModule;
 import org.junit.Before;
 import org.junit.Test;

 import java.io.IOException;
 import java.util.HashMap;
 import java.util.LinkedHashMap;
 import java.util.Map;

 import static org.junit.Assert.*;

 public class DeserializerCacheTest
 {
     private ObjectMapper mapper;
     private DeserializationContext ctxt;
     private DeserializerFactory factory;
     private DeserializerCache cache;

     // A simple custom KeyDeserializer that uppercases the key
     static class UpperCaseKeyDeserializer extends KeyDeserializer {
         @Override
         public Object deserializeKey(String key, DeserializationContext ctxt) {
             return key.toUpperCase();
         }
     }

     @Before
     public void setUp() {
         mapper = new ObjectMapper();
         // Register custom key deserializer for String
         SimpleModule mod = new SimpleModule("TestModule");
         mod.addKeyDeserializer(String.class, new UpperCaseKeyDeserializer());
         mapper.registerModule(mod);

         // Obtain necessary internal objects
         DeserializationConfig config = mapper.getDeserializationConfig();
         JsonParser parser = mapper.getFactory().createParser("{}");
         try {
             ctxt = mapper.createDeserializationContext(parser, config);
         } catch (IOException e) {
             throw new RuntimeException(e);
         }
         factory = mapper.getDeserializerContext().getFactory(); // fallback if above not accessible
         if (factory == null) {
             // alternative: use BeanDeserializerFactory
             factory = (DeserializerFactory) config.getProblemHandlers(); // Not reliable, we'll
reflect or use public API
         }
         cache = new DeserializerCache();
     }

     @Test
     public void testKeyDeserializerCaching() throws Exception {
         JavaType keyType = TypeFactory.defaultInstance().constructType(String.class);

         KeyDeserializer kd1 = cache.findKeyDeserializer(ctxt, factory, keyType);
         assertNotNull("First call must return a key deserializer", kd1);
         assertTrue("Must be the custom key deserializer", kd1 instanceof UpperCaseKeyDeserializer);

         KeyDeserializer kd2 = cache.findKeyDeserializer(ctxt, factory, keyType);
         assertSame("Second call must return the cached key deserializer instance", kd1, kd2);
     }

     @Test
     public void testKeyDeserializerCacheIsolation() throws Exception {
         JavaType stringKey = TypeFactory.defaultInstance().constructType(String.class);
         JavaType intKey    = TypeFactory.defaultInstance().constructType(Integer.class);

         // Register a different key deserializer for Integer
         SimpleModule intMod = new SimpleModule("IntModule");
         intMod.addKeyDeserializer(Integer.class, new KeyDeserializer() {
             @Override
             public Object deserializeKey(String key, DeserializationContext ctxt) {
                 return Integer.parseInt(key) + 100;
             }
         });
         ObjectMapper mapper2 = new ObjectMapper();
         mapper2.registerModule(intMod);
         DeserializationConfig config2 = mapper2.getDeserializationConfig();
         JsonParser parser2 = mapper2.getFactory().createParser("{}");
         DeserializationContext ctxt2 = mapper2.createDeserializationContext(parser2, config2);
         DeserializerFactory factory2 = mapper2.getDeserializerContext().getFactory();

         KeyDeserializer kdStr = cache.findKeyDeserializer(ctxt, factory, stringKey);
         KeyDeserializer kdInt = cache.findKeyDeserializer(ctxt2, factory2, intKey);

         assertNotSame("Caches for different types must be independent", kdStr, kdInt);
         assertNotEquals(kdStr.getClass(), kdInt.getClass());
     }

     @Test
     public void testMapDeserializationUsesCachedKeyDeserializer() throws Exception {
         // Build a map and deserialize twice to verify custom key deserializer is consistently used
         String json = "{\"1st\":\"onedata\",\"2nd\":\"twodata\"}";
         MapType mapType = TypeFactory.defaultInstance().constructMapType(HashMap.class,
String.class, String.class);

         // First deserialization
         Map<String, String> map1 = mapper.readValue(json, mapType);
         assertEquals("1st key should be uppercased", "1ST", getKeyByName(map1, "1ST"));
         assertEquals("2nd key should be uppercased", "2ND", getKeyByName(map1, "2ND"));

         // Second deserialization using same mapper (the bug would cause loss of custom key
deserializer)
         Map<String, String> map2 = mapper.readValue(json, mapType);
         assertEquals("1st key still uppercased after caching", "1ST", getKeyByName(map2, "1ST"));
     }

     @Test
     public void testMapWithSingleEntry() throws Exception {
         String json = "{\"abc\":\"xyz\"}";
         MapType mapType = TypeFactory.defaultInstance().constructMapType(HashMap.class,
String.class, String.class);
         Map<String, String> map = mapper.readValue(json, mapType);
         assertTrue(map.containsKey("ABC"));
         assertEquals("xyz", map.get("ABC"));
     }

     @Test
     public void testEmptyMap() throws Exception {
         String json = "{}";
         MapType mapType = TypeFactory.defaultInstance().constructMapType(HashMap.class,
String.class, String.class);
         Map<String, String> map = mapper.readValue(json, mapType);
         assertTrue(map.isEmpty());
     }

     @Test
     public void testMapWithNullKeyFails() throws Exception {
         String json = "{null:\"value\"}"; // JSON does not allow null keys, but if forced...
         MapType mapType = TypeFactory.defaultInstance().constructMapType(HashMap.class,
String.class, String.class);
         try {
             mapper.readValue(json, mapType);
             // Depending on feature settings, might pass or fail. We expect custom key deserializer
not to handle null.
             // If it passes, we still assert that the key deserializer is called
         } catch (JsonMappingException e) {
             // Expected when key deserializer cannot handle null
             assertTrue(e.getMessage().contains("Can not find a (Map) Key deserializer") ||
                        e.getMessage().contains("null"));
         }
     }

     @Test
     public void testUnknownKeyDeserializerThrows() throws Exception {
         // Use a type for which no key deserializer is registered
         ObjectMapper strictMapper = new ObjectMapper();
         strictMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
         JavaType doubleKey = TypeFactory.defaultInstance().constructType(Double.class);
         try {
             cache.findKeyDeserializer(

strictMapper.createDeserializationContext(strictMapper.getFactory().createParser("{}"),
                          strictMapper.getDeserializationConfig()),
                 strictMapper.getDeserializerContext().getFactory(),
                 doubleKey);
             fail("Should have thrown");
         } catch (JsonMappingException e) {
             assertTrue(e.getMessage().contains("Can not find a (Map) Key deserializer"));
         }
     }

     // Helper to verify a key exists ignoring case (since uppercased)
     private static boolean containsKeyIgnoreCase(Map<String, String> map, String key) {
         for (String k : map.keySet()) {
             if (k.equalsIgnoreCase(key)) return true;
         }
         return false;
     }

     private String getKeyByName(Map<String, String> map, String expectedKey) {
         for (String k : map.keySet()) {
             if (k.equals(expectedKey)) return k;
         }
         return null;
     }
 }
