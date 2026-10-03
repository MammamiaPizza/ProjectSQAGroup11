package com.fasterxml.jackson.databind;

 import static org.junit.Assert.*;
 import org.junit.Test;

 import com.fasterxml.jackson.annotation.JsonTypeInfo;
 import com.fasterxml.jackson.databind.node.*;

 import java.io.IOException;

 public class ObjectMapperDefaultTypingBug17Test {

     private ObjectMapper mapperWithDefaultTyping(JsonTypeInfo.As as) {
         ObjectMapper m = new ObjectMapper();
         m.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, as);
         return m;
     }

     private ObjectMapper mapperWithPropertyTyping(String prop) {
         ObjectMapper m = new ObjectMapper();
         m.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.NON_FINAL, prop);
         return m;
     }

     // 1. Empty array
     @Test
     public void testEmptyArrayDefaultTyping() throws Exception {
         ObjectMapper mapper = mapperWithDefaultTyping(JsonTypeInfo.As.WRAPPER_ARRAY);
         JsonNode node = mapper.readTree("[]");
         assertNotNull(node);
         assertTrue(node.isArray());
         assertEquals(0, node.size());
     }

     // 2. Integer array
     @Test
     public void testIntArrayDefaultTyping() throws Exception {
         ObjectMapper mapper = mapperWithDefaultTyping(JsonTypeInfo.As.WRAPPER_ARRAY);
         JsonNode node = mapper.readTree("[1,2,3]");
         assertNotNull(node);
         assertTrue(node.isArray());
         assertEquals(3, node.size());
         assertEquals(1, node.get(0).intValue());
         assertEquals(2, node.get(1).intValue());
         assertEquals(3, node.get(2).intValue());
     }

     // 3. String array
     @Test
     public void testStringArrayDefaultTyping() throws Exception {
         ObjectMapper mapper = mapperWithDefaultTyping(JsonTypeInfo.As.WRAPPER_ARRAY);
         JsonNode node = mapper.readTree("[\"a\",\"b\"]");
         assertNotNull(node);
         assertTrue(node.isArray());
         assertEquals(2, node.size());
         assertEquals("a", node.get(0).textValue());
         assertEquals("b", node.get(1).textValue());
     }

     // 4. Mixed array with true and null
     @Test
     public void testMixedArrayDefaultTyping() throws Exception {
         ObjectMapper mapper = mapperWithDefaultTyping(JsonTypeInfo.As.WRAPPER_ARRAY);
         JsonNode node = mapper.readTree("[true, null]");
         assertNotNull(node);
         assertTrue(node.isArray());
         assertEquals(2, node.size());
         assertTrue(node.get(0).isBoolean());
         assertTrue(node.get(0).booleanValue());
         assertTrue(node.get(1).isNull());
     }

     // 5. Nested array
     @Test
     public void testNestedArrayDefaultTyping() throws Exception {
         ObjectMapper mapper = mapperWithDefaultTyping(JsonTypeInfo.As.WRAPPER_ARRAY);
         JsonNode node = mapper.readTree("[[1],[2]]");
         assertNotNull(node);
         assertTrue(node.isArray());
         assertEquals(2, node.size());
         assertTrue(node.get(0).isArray());
         assertEquals(1, node.get(0).get(0).intValue());
         assertTrue(node.get(1).isArray());
         assertEquals(2, node.get(1).get(0).intValue());
     }

     // 6. Object node with an integer field
     @Test
     public void testObjectNodeIntFieldDefaultTyping() throws Exception {
         ObjectMapper mapper = mapperWithDefaultTyping(JsonTypeInfo.As.WRAPPER_ARRAY);
         JsonNode node = mapper.readTree("{\"a\":1}");
         assertNotNull(node);
         assertTrue(node.isObject());
         assertEquals(1, node.get("a").intValue());
     }

     // 7. Object node with an array field
     @Test
     public void testObjectNodeArrayFieldDefaultTyping() throws Exception {
         ObjectMapper mapper = mapperWithDefaultTyping(JsonTypeInfo.As.WRAPPER_ARRAY);
         JsonNode node = mapper.readTree("{\"b\":[1,2]}");
         assertNotNull(node);
         assertTrue(node.isObject());
         JsonNode arr = node.get("b");
         assertTrue(arr.isArray());
         assertEquals(2, arr.size());
         assertEquals(1, arr.get(0).intValue());
     }

     // 8. Single primitive value (not inside an array)
     @Test
     public void testSingleIntValueDefaultTyping() throws Exception {
         ObjectMapper mapper = mapperWithDefaultTyping(JsonTypeInfo.As.WRAPPER_ARRAY);
         JsonNode node = mapper.readTree("42");
         assertNotNull(node);
         assertTrue(node.isInt());
         assertEquals(42, node.intValue());
     }

     // 9. Round-trip: serialize and deserialize ArrayNode with default typing
     @Test
     public void testRoundTripArrayNodeDefaultTyping() throws Exception {
         ObjectMapper mapper = mapperWithDefaultTyping(JsonTypeInfo.As.WRAPPER_ARRAY);
         ArrayNode original = mapper.createArrayNode();
         original.add(1);
         original.add(2);
         String json = mapper.writeValueAsString(original);
         JsonNode deserialized = mapper.readTree(json);
         assertEquals(original, deserialized);
     }

     // 10. With property-based default typing
     @Test
     public void testIntArrayDefaultTypingAsProperty() throws Exception {
         ObjectMapper mapper = mapperWithPropertyTyping("@class");
         JsonNode node = mapper.readTree("[1,2,3]");
         assertNotNull(node);
         assertTrue(node.isArray());
         assertEquals(3, node.size());
         assertEquals(1, node.get(0).intValue());
     }

     // 11. With property-based, object node
     @Test
     public void testObjectNodeDefaultTypingAsProperty() throws Exception {
         ObjectMapper mapper = mapperWithPropertyTyping("@class");
         JsonNode node = mapper.readTree("{\"a\":\"x\"}");
         assertNotNull(node);
         assertTrue(node.isObject());
         assertEquals("x", node.get("a").textValue());
     }

     // 12. Without default typing – sanity check
     @Test
     public void testIntArrayWithoutDefaultTyping() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         JsonNode node = mapper.readTree("[1,2,3]");
         assertNotNull(node);
         assertTrue(node.isArray());
         assertEquals(3, node.size());
         assertEquals(1, node.get(0).intValue());
     }
 }
