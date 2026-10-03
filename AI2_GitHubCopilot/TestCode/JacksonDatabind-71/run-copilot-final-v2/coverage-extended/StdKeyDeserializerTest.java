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

@Test
public void testParseBooleanKeyTrueFalseAndInvalid() throws Exception {
    DeserializationContext ctxt = mockCtxt();
    StdKeyDeserializer deser = StdKeyDeserializer.forType(Boolean.class);
    assertEquals(Boolean.TRUE, deser.deserializeKey("true", ctxt));
    assertEquals(Boolean.FALSE, deser.deserializeKey("false", ctxt));
    try {
        deser.deserializeKey("tru", ctxt);
        fail("Should throw exception on invalid boolean key");
    } catch (com.fasterxml.jackson.databind.JsonMappingException expected) { }
}

@Test
public void testParseByteKeyWithinRangeAndOverflow() throws Exception {
    DeserializationContext ctxt = mockCtxt();
    StdKeyDeserializer deser = StdKeyDeserializer.forType(Byte.class);
    assertEquals(Byte.valueOf((byte) 100), deser.deserializeKey("100", ctxt));
    try {
        deser.deserializeKey("256", ctxt);
        fail("Should throw exception on byte overflow");
    } catch (com.fasterxml.jackson.databind.JsonMappingException expected) { }
}

@Test
public void testParseShortKeyWithinRangeAndOverflow() throws Exception {
    DeserializationContext ctxt = mockCtxt();
    StdKeyDeserializer deser = StdKeyDeserializer.forType(Short.class);
    assertEquals(Short.valueOf((short) 30000), deser.deserializeKey("30000", ctxt));
    try {
        deser.deserializeKey("32768", ctxt);
        fail("Should throw exception on short overflow");
    } catch (com.fasterxml.jackson.databind.JsonMappingException expected) { }
}

@Test
public void testParseLocaleKey() throws Exception {
    DeserializationContext ctxt = mockCtxt();
    StdKeyDeserializer deser = StdKeyDeserializer.forType(java.util.Locale.class);
    Object result = deser.deserializeKey("en_US", ctxt);
    assertNotNull(result);
    assertTrue(result instanceof java.util.Locale);
    java.util.Locale loc = (java.util.Locale) result;
    assertEquals("en", loc.getLanguage());
}

private DeserializationContext mockCtxt() {
    DeserializationContext ctxt =
org.powermock.api.mockito.PowerMockito.mock(DeserializationContext.class);
    com.fasterxml.jackson.databind.JsonMappingException ex = new
com.fasterxml.jackson.databind.JsonMappingException("mock");
    org.powermock.api.mockito.PowerMockito.when(ctxt.weirdKeyException(
            org.mockito.Mockito.any(Class.class),
            org.mockito.Mockito.any(String.class),
            org.mockito.Mockito.any(String.class))).thenReturn(ex);
    return ctxt;
}
}
