package com.fasterxml.jackson.databind.deser.std;

 import static org.junit.Assert.*;

 import com.fasterxml.jackson.core.*;
 import com.fasterxml.jackson.databind.*;
 import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
 import com.fasterxml.jackson.databind.module.SimpleModule;
 import java.io.IOException;
 import java.util.*;

 import org.junit.Before;
 import org.junit.Test;

 /**
  * Tests for MapDeserializer that expose the caching issue described in bug #735.
  * The bug is that {@link MapDeserializer#isCachable()} does not consider the presence of a
  * custom value deserializer, causing the deserializer to be cached and reused later without the
  * custom deserializer when an annotation-based custom deserializer should apply.
  */
 public class MapDeserializerTest {

     private ObjectMapper mapper;

     @Before
     public void setUp() {
         mapper = new ObjectMapper();
     }

     // ---------- custom deserializers ----------

     public static class FixedIntDeserializer extends JsonDeserializer<Integer> {
         @Override
         public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
             // Consume the value but always return 1
             p.skipChildren();
             return 1;
         }
     }

     public static class NullSafeFixedIntDeserializer extends JsonDeserializer<Integer> {
         @Override
         public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
  p.skipChildren();
             return1;
  }
         @Override
         public Integer getNullValue() {
             return -1;
  }
     }

     // ---------- POJO for annotation tests ----------

     public static class AnnotatedMapWrapper {
         @JsonDeserialize(contentUsing = FixedIntDeserializer.class)
         public Map<String, Integer> values;
     }

     public static class AnnotatedMapWrapperWithNull {
         @JsonDeserialize(contentUsing = NullSafeFixedIntDeserializer.class)
         public Map<String, Integer> values;
     }

     // ---------- tests ----------

     /**
      * Direct use: register a module with a custom Integer deserializer.
      * Expect map value to be the custom deserialized value (1), not the raw JSON number.
      */
     @Test
     public void testModuleRegisteredCustomValueDeserializer() throws Exception {
  SimpleModule module = new SimpleModule("test", Version.unknownVersion());
         module.addDeserializer(Integer.class, new FixedIntDeserializer());
  mapper.registerModule(module);

         Map<String, Integer> result = mapper.readValue("{\"a\": 100}",
                 mapper.getTypeFactory().constructMapType(Map.class, String.class, Integer.class));
         assertEquals(Integer.valueOf(1), result.get("a"));
     }

     /**
      * Annotated field with custom content deserializer should be used even when the
      * plain Map<String, Integer> has been cached before (the core bug: isCachable ignores value
deser).
      */
     @Test
     public void testAnnotatedCustomValueDeserializerAfterCacheWarm() throws Exception {
         // 1. Warm the cache with a plain Map that has no custom deserializer
         Map<String, Integer> dummy = mapper.readValue("{\"x\": 999}",
                 mapper.getTypeFactory().constructMapType(Map.class, String.class, Integer.class));
         assertNotNull(dummy);
         assertEquals(Integer.valueOf(999), dummy.get("x"));

         // 2. Deserialize a wrapper that has @JsonDeserialize(contentUsing=...)
         AnnotatedMapWrapper wrapper = mapper.readValue("{\"values\": {\"key\": 100}}",
                 AnnotatedMapWrapper.class);
         assertNotNull("wrapper values must not be null", wrapper.values);
         assertEquals("custom deserializer must produce 1", Integer.valueOf(1),
wrapper.values.get("key"));
     }

     /**
      * Make sure the custom deserializer is also used when no other map has been cached yet.
      */
     @Test
     public void testAnnotatedCustomValueDeserializerWithoutWarmup() throws Exception {
         AnnotatedMapWrapper wrapper = mapper.readValue("{\"values\": {\"key\": 100}}",
                 AnnotatedMapWrapper.class);
         assertNotNull(wrapper.values);
         assertEquals(Integer.valueOf(1), wrapper.values.get("key"));
     }

     /**
      * Null map value -> custom deserializer getNullValue() is invoked.
      */
     @Test
     public void testNullValueUsesGetNullValueOfCustomDeserializer() throws Exception {
         SimpleModule module = new SimpleModule("test", Version.unknownVersion());
         module.addDeserializer(Integer.class, new NullSafeFixedIntDeserializer());
  mapper.registerModule(module);

         Map<String, Integer> result = mapper.readValue("{\"a\": null}",
                 mapper.getTypeFactory().constructMapType(Map.class, String.class, Integer.class));
         assertEquals(Integer.valueOf(-1), result.get("a"));
     }

     @Test
     public void testNullValueWithAnnotatedCustomDeserializer() throws Exception {
         // Warm cache
         mapper.readValue("{\"a\": 1}",
                 mapper.getTypeFactory().constructMapType(Map.class, String.class, Integer.class));

         AnnotatedMapWrapperWithNull wrapper = mapper.readValue(
                 "{\"values\": {\"key\": null}}", AnnotatedMapWrapperWithNull.class);
         assertNotNull(wrapper.values);
         assertEquals(Integer.valueOf(-1), wrapper.values.get("key"));
     }

     @Test
     public void testEmptyMap() throws Exception {
         Map<String, Integer> result = mapper.readValue("{}",
                 mapper.getTypeFactory().constructMapType(Map.class, String.class, Integer.class));
         assertNotNull(result);
         assertTrue("map should be empty", result.isEmpty());
     }

     /**
      * Several entries with custom deserializer all produce 1.
      */
     @Test
     public void testMultipleEntriesWithCustomDeserializer() throws Exception {
         SimpleModule module = new SimpleModule("test", Version.unknownVersion());
         module.addDeserializer(Integer.class, new FixedIntDeserializer());
  mapper.registerModule(module);

         Map<String, Integer> result = mapper.readValue("{\"a\": 10, \"b\": 20, \"c\": 30}",
                 mapper.getTypeFactory().constructMapType(Map.class, String.class, Integer.class));
         for (Integer val : result.values()) {
             assertEquals(Integer.valueOf(1), val);
         }
     }

     /**
      * In the faulty version, even after a clean instance, two fields with different custom
deserializers
      * might interfere if caching ignores the content deserializer.
      * This test demonstrates that a plain map deserializer does not leak into annotated usage.
      */
     @Test
     public void testRepeatedUseKeepsCustomDeserializer() throws Exception {
         // First, deserialize with plain map (no annotation)
         mapper.readValue("{\"x\": 111}",
                 mapper.getTypeFactory().constructMapType(Map.class, String.class, Integer.class));

         // Second, deserialize with annotation
         AnnotatedMapWrapper w1 = mapper.readValue("{\"values\": {\"k1\": 100}}",
                 AnnotatedMapWrapper.class);
         assertEquals(Integer.valueOf(1), w1.values.get("k1"));

         // Third, deserialize another annotated instance
         AnnotatedMapWrapper w2 = mapper.readValue("{\"values\": {\"k2\": 200}}",
                 AnnotatedMapWrapper.class);
         assertEquals(Integer.valueOf(1), w2.values.get("k2"));
     }
 }
