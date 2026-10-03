package com.fasterxml.jackson.databind.ser.std;

 import static org.junit.Assert.*;

 import java.io.Serializable;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.Map;

 import org.junit.Test;

 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.core.JsonProcessingException;

 public class StdKeySerializerTest {

     private final ObjectMapper mapper = new ObjectMapper();

     @Test
     public void testSerializeStringClassKey() throws Exception {
         Map<Class<?>, Integer> map = new HashMap<>();
         map.put(String.class, 2);
         String json = mapper.writeValueAsString(map);
         assertTrue("Key should not contain 'class ' prefix",
                 json.contains("\"java.lang.String\""));
         assertFalse("Key must not contain 'class ' prefix",
                 json.contains("\"class "));
     }

     @Test
     public void testSerializePrimitiveClassKey() throws Exception {
         Map<Class<?>, Integer> map = new HashMap<>();
         map.put(int.class, 1);
         String json = mapper.writeValueAsString(map);
         assertTrue("Primitive key should be 'int'",
                 json.contains("\"int\""));
     }

     @Test
     public void testSerializeInterfaceClassKey() throws Exception {
         Map<Class<?>, Integer> map = new HashMap<>();
         map.put(Serializable.class, 3);
         String json = mapper.writeValueAsString(map);
         assertTrue("Interface key should have full name without 'class '",
                 json.contains("\"java.io.Serializable\""));
         assertFalse("Interface key must not contain 'class '",
                 json.contains("\"class "));
     }

     @Test
     public void testSerializeArrayClassKey() throws Exception {
         Map<Class<?>, Integer> map = new HashMap<>();
         map.put(String[].class, 4);
         String json = mapper.writeValueAsString(map);
         assertTrue("Array key should have internal name without 'class '",
                 json.contains("\"[Ljava.lang.String;\""));
         assertFalse("Array key must not contain 'class '",
                 json.contains("\"class "));
     }

     @Test
     public void testSerializeNonClassKeyUnchanged() throws Exception {
         Map<String, Integer> map = new HashMap<>();
         map.put("foo", 1);
         String json = mapper.writeValueAsString(map);
         assertTrue("Regular String key should appear as-is",
                 json.contains("\"foo\""));
         // Ensure no spurious replacements affected regular keys
         assertEquals("{\"foo\":1}", json);
     }

     @Test
     public void testSerializeDateKey() throws Exception {
         Map<Date, Integer> map = new HashMap<>();
         Date d = new Date(0);
         map.put(d, 1);
         String json = mapper.writeValueAsString(map);
         // Date key serialization uses special path; no exception is expected
         assertNotNull(json);
         assertFalse("Date key should not produce empty JSON", json.isEmpty());
     }

     @Test
     public void testSerializeMixedKeys() throws Exception {
         Map<Object, Integer> map = new HashMap<>();
         map.put(String.class, 10);
         map.put("regular", 20);
         String json = mapper.writeValueAsString(map);
         assertTrue(json.contains("\"java.lang.String\""));
         assertTrue(json.contains("\"regular\""));
         assertFalse(json.contains("\"class "));
     }

     @Test
     public void testSerializeInnerClassKey() throws Exception {
         Map<Class<?>, Integer> map = new HashMap<>();
         map.put(Inner.class, 7);
         String json = mapper.writeValueAsString(map);
         // Inner class name uses '$' as separator

assertTrue(json.contains("\"com.fasterxml.jackson.databind.ser.std.StdKeySerializerTest$Inner\""));
         assertFalse(json.contains("\"class "));
     }

     @Test
     public void testDuplicateClassKeysProduceValidJson() throws Exception {
         Map<Class<?>, Integer> map = new HashMap<>();
         map.put(String.class, 1);
         map.put(String.class, 2); // overwrite
         String json = mapper.writeValueAsString(map);
         // JSON objects cannot have duplicate keys; last value wins
         assertTrue(json.contains("\"java.lang.String\":2"));
     }

     @Test
     public void testClassKeyBehaviorMatchesTriggerExpectation() throws Exception {
         Map<Class<?>, Integer> map = new HashMap<>();
         map.put(String.class, 2);
         String json = mapper.writeValueAsString(map);
         assertEquals("{\"java.lang.String\":2}", json);
     }

     // A nested inner class for testing
     static class Inner { }
 }