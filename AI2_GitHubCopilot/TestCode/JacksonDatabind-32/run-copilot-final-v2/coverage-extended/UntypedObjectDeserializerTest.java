package com.fasterxml.jackson.databind.deser.std;

 import static org.junit.Assert.*;

 import java.util.List;
 import java.util.Map;

 import org.junit.Before;
 import org.junit.Test;

 import com.fasterxml.jackson.databind.ObjectMapper;

 /**
  * Tests for {@link UntypedObjectDeserializer} focusing on the bug #989:
  * nested untyped deserialization caused END_OBJECT token to trigger a mapping exception.
  */
 public class UntypedObjectDeserializerTest {
     private ObjectMapper mapper;

     @Before
     public void setUp() {
         mapper = new ObjectMapper();
     }

     /**
      * Core bug: nested object (map containing a map) should deserialize without exception.
      */
     @Test
     public void testNestedObject() throws Exception {
         Object result = mapper.readValue("{\"a\":{\"b\":1}}", Object.class);
         assertTrue(result instanceof Map);
         Map<?, ?> outer = (Map<?, ?>) result;
         assertTrue(outer.get("a") instanceof Map);
     }

     /**
      * Nested array: map containing a list.
      */
     @Test
     public void testNestedArray() throws Exception {
         Object result = mapper.readValue("{\"a\":[1,2]}", Object.class);
         assertTrue(result instanceof Map);
         Map<?, ?> outer = (Map<?, ?>) result;
         assertTrue(outer.get("a") instanceof List);
     }

     /**
      * Deeply nested maps.
      */
     @Test
     public void testDeeplyNested() throws Exception {
         Object result = mapper.readValue("{\"a\":{\"b\":{\"c\":1}}}", Object.class);
         assertTrue(result instanceof Map);
         Map<?, ?> a = (Map<?, ?>) ((Map<?, ?>) result).get("a");
         assertTrue(a.get("b") instanceof Map);
     }

     /**
      * Nested empty object: map with empty map.
      */
     @Test
     public void testEmptyNestedObject() throws Exception {
         Object result = mapper.readValue("{\"a\":{}}", Object.class);
         assertTrue(result instanceof Map);
         Map<?, ?> outer = (Map<?, ?>) result;
         assertTrue(outer.get("a") instanceof Map);
         assertTrue(((Map<?, ?>) outer.get("a")).isEmpty());
     }

     /**
      * Top-level empty object.
      */
     @Test
     public void testEmptyTopLevelObject() throws Exception {
         Object result = mapper.readValue("{}", Object.class);
         assertTrue(result instanceof Map);
         assertTrue(((Map<?, ?>) result).isEmpty());
     }

     /**
      * Array of objects (list of maps).
      */
     @Test
     public void testArrayOfObjects() throws Exception {
         Object result = mapper.readValue("[{\"a\":1},{\"b\":2}]", Object.class);
         assertTrue(result instanceof List);
         List<?> list = (List<?>) result;
         assertTrue(list.get(0) instanceof Map);
         assertTrue(list.get(1) instanceof Map);
     }

     /**
      * Map with null value.
      */
     @Test
     public void testNullValue() throws Exception {
         Object result = mapper.readValue("{\"a\":null}", Object.class);
         assertTrue(result instanceof Map);
         Map<?, ?> map = (Map<?, ?>) result;
         assertNull(map.get("a"));
     }

     /**
      * Map with string value.
      */
     @Test
     public void testStringValue() throws Exception {
         Object result = mapper.readValue("{\"a\":\"hello\"}", Object.class);
         assertTrue(result instanceof Map);
         assertEquals("hello", ((Map<?, ?>) result).get("a"));
     }

     /**
      * Map with integral number value.
      */
     @Test
     public void testNumberValue() throws Exception {
         Object result = mapper.readValue("{\"a\":123}", Object.class);
         assertTrue(result instanceof Map);
         assertEquals(123, ((Map<?, ?>) result).get("a"));
     }

     /**
      * Map with boolean value.
      */
     @Test
     public void testBooleanValue() throws Exception {
         Object result = mapper.readValue("{\"a\":true}", Object.class);
         assertTrue(result instanceof Map);
         assertEquals(Boolean.TRUE, ((Map<?, ?>) result).get("a"));
     }

     /**
      * Mixed nested: map containing map with list and number.
      */
     @Test
     public void testMixedNested() throws Exception {
         Object result = mapper.readValue("{\"a\":{\"b\":1,\"c\":[2,3]}}", Object.class);
         assertTrue(result instanceof Map);
         Map<?, ?> a = (Map<?, ?>) ((Map<?, ?>) result).get("a");
         assertTrue(a.get("c") instanceof List);
     }

     /**
      * List of mixed scalar types.
      */
     @Test
     public void testListOfMixedTypes() throws Exception {
         Object result = mapper.readValue("[1, \"hello\", true, null]", Object.class);
         assertTrue(result instanceof List);
         List<?> list = (List<?>) result;
         assertEquals(1, ((Number) list.get(0)).intValue());
         assertEquals("hello", list.get(1));
         assertEquals(Boolean.TRUE, list.get(2));
         assertNull(list.get(3));
     }

@Test
    public void testCustomNumberDeserializer() throws Exception {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.module.SimpleModule module = new
com.fasterxml.jackson.databind.module.SimpleModule();
        module.addDeserializer(Number.class, new
com.fasterxml.jackson.databind.JsonDeserializer<Number>() {
            @Override
            public Number deserialize(com.fasterxml.jackson.core.JsonParser p,
com.fasterxml.jackson.databind.DeserializationContext ctxt) throws java.io.IOException {
                p.nextToken();
                return 42;
            }
        });
        mapper.registerModule(module);
        Object result = mapper.readValue("123", Object.class);
        org.junit.Assert.assertEquals(42, ((Number) result).intValue());
    }

 @Test
 public void testCustomStringDeserializer() throws Exception {
     com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();
     com.fasterxml.jackson.databind.module.SimpleModule module = new
com.fasterxml.jackson.databind.module.SimpleModule();
     module.addDeserializer(String.class, new
com.fasterxml.jackson.databind.JsonDeserializer<String>() {
         @Override
         public String deserialize(com.fasterxml.jackson.core.JsonParser p,
com.fasterxml.jackson.databind.DeserializationContext ctxt) throws java.io.IOException {
             p.nextToken();
             return "custom";
         }
     });
     mapper.registerModule(module);
     Object result = mapper.readValue("\"hello\"", Object.class);
     org.junit.Assert.assertEquals("custom", result);
 }

 @Test
 public void testCustomListDeserializer() throws Exception {
     com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();
     com.fasterxml.jackson.databind.module.SimpleModule module = new
com.fasterxml.jackson.databind.module.SimpleModule();
     module.addDeserializer(java.util.List.class, new
com.fasterxml.jackson.databind.JsonDeserializer<java.util.List<?>>() {
         @Override
         public java.util.List<?> deserialize(com.fasterxml.jackson.core.JsonParser p,
com.fasterxml.jackson.databind.DeserializationContext ctxt) throws java.io.IOException {
             p.skipChildren();
             return new java.util.ArrayList();
         }
     });
     mapper.registerModule(module);
     Object result = mapper.readValue("[1,2,3]", Object.class);
     org.junit.Assert.assertTrue(result instanceof java.util.List);
     org.junit.Assert.assertEquals(0, ((java.util.List<?>) result).size());
 }

 @Test
 public void testCustomMapDeserializer() throws Exception {
     com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();
     com.fasterxml.jackson.databind.module.SimpleModule module = new
com.fasterxml.jackson.databind.module.SimpleModule();
     module.addDeserializer(java.util.Map.class, new
com.fasterxml.jackson.databind.JsonDeserializer<java.util.Map<?,?>>() {
         @Override
         public java.util.Map<?,?> deserialize(com.fasterxml.jackson.core.JsonParser p,
com.fasterxml.jackson.databind.DeserializationContext ctxt) throws java.io.IOException {
             p.skipChildren();
             return new java.util.HashMap();
         }
     });
     mapper.registerModule(module);
     Object result = mapper.readValue("{\"a\":1}", Object.class);
     org.junit.Assert.assertTrue(result instanceof java.util.Map);
     org.junit.Assert.assertEquals(0, ((java.util.Map<?,?>) result).size());
 }
}
