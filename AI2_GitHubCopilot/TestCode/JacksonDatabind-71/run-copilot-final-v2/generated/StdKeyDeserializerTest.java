package com.fasterxml.jackson.databind.deser.std;

 import static org.junit.Assert.*;

 import java.io.IOException;
 import java.util.Arrays;
 import java.util.Currency;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 import java.util.UUID;
 import java.util.concurrent.TimeUnit;

 import org.junit.Test;

 import com.fasterxml.jackson.core.type.TypeReference;
 import com.fasterxml.jackson.databind.DeserializationContext;
 import com.fasterxml.jackson.databind.JsonMappingException;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer;

 public class StdKeyDeserializerTest {

     @Test
     public void testForTypeCharSequenceNotNull() {
         StdKeyDeserializer deser = StdKeyDeserializer.forType(CharSequence.class);
         assertNotNull("forType(CharSequence.class) must not return null", deser);
     }

     @Test
     public void testForTypeStringNotNull() {
         assertNotNull(StdKeyDeserializer.forType(String.class));
     }

     @Test
     public void testForTypeObjectNotNull() {
         assertNotNull(StdKeyDeserializer.forType(Object.class));
     }

     @Test
     public void testForTypeAllSupportedTypesNotNull() {
         List<Class<?>> supported = Arrays.asList(
                 UUID.class, Integer.class, Long.class, java.util.Date.class,
                 java.util.Calendar.class, Boolean.class, Byte.class, Character.class,
                 Short.class, Float.class, Double.class, java.net.URI.class,
                 java.net.URL.class, Class.class, Locale.class, Currency.class
         );
         for (Class<?> cls : supported) {
             assertNotNull("forType(" + cls.getSimpleName() + ") must not return null",
                     StdKeyDeserializer.forType(cls));
         }
     }

     @Test
     public void testForTypeUnsupportedReturnsNull() {
         // Type that is not covered by forType should return null
         assertNull(StdKeyDeserializer.forType(TimeUnit.class));
     }

     @Test
     public void testDeserializeKeyCharSequenceReturnsKeyString() throws IOException {
         StdKeyDeserializer deser = StdKeyDeserializer.forType(CharSequence.class);
         assertNotNull(deser);
         // The CharSequence deserializer must return the raw key as String
         Object key = deser.deserializeKey("hello", null);
         assertEquals("hello", key);
     }

     @Test
     public void testMapDeserializationWithCharSequenceKeys() throws IOException {
         String json = "{\"key1\":\"value1\", \"key2\":\"value2\"}";
         ObjectMapper mapper = new ObjectMapper();

         Map<CharSequence, String> map = mapper.readValue(json,
                 new TypeReference<Map<CharSequence, String>>() {});
         assertNotNull(map);
         assertEquals(2, map.size());
         assertEquals("value1", map.get("key1"));
         assertEquals("value2", map.get("key2"));
     }

     @Test
     public void testMapDeserializationWithStringKeys() throws IOException {
         String json = "{\"a\":\"b\"}";
         ObjectMapper mapper = new ObjectMapper();

         Map<String, String> map = mapper.readValue(json,
                 new TypeReference<Map<String, String>>() {});
         assertEquals(1, map.size());
         assertEquals("b", map.get("a"));
     }

     @Test(expected = JsonMappingException.class)
     public void testMapDeserializationWithUnsupportedKeyTypeThrowsException() throws IOException {
         String json = "{\"x\":\"y\"}";
         ObjectMapper mapper = new ObjectMapper();

         // TimeUnit is not a supported key type; should trigger exception
         mapper.readValue(json, new TypeReference<Map<TimeUnit, String>>() {});
     }

     @Test
     public void testDeserializeKeyNullReturnsNull() throws IOException {
         StdKeyDeserializer deser = StdKeyDeserializer.forType(String.class);
         assertNotNull(deser);
         // null key must be handled gracefully
         Object result = deser.deserializeKey(null, null);
         assertNull(result);
     }

     @Test
     public void testGetKeyClassReturnsCharSequenceClass() {
         StdKeyDeserializer deser = StdKeyDeserializer.forType(CharSequence.class);
         assertNotNull(deser);
         assertEquals(CharSequence.class, deser.getKeyClass());
     }
 }
