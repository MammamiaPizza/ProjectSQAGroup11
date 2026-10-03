package com.fasterxml.jackson.databind.type;

 import java.util.*;

 import com.fasterxml.jackson.databind.JavaType;
 import com.fasterxml.jackson.databind.ObjectMapper;

 import org.junit.Test;
 import static org.junit.Assert.*;

 public class TypeFactoryPropertiesTest
 {
     private final TypeFactory tf = TypeFactory.defaultInstance();

     public static class MyProperties extends Properties { }

     @Test
     public void testPropertiesKeyAndValueType() {
         JavaType type = tf.constructFromCanonical("java.util.Properties");
         assertTrue(type.isMapLikeType());
         assertEquals(String.class, type.getKeyType().getRawClass());
         assertEquals(String.class, type.getContentType().getRawClass());
     }

     @Test
     public void testFindTypeParametersPropertiesClass() {
         JavaType[] params = tf.findTypeParameters(Properties.class, Map.class);
         assertNotNull(params);
         assertEquals(2, params.length);
         assertEquals(String.class, params[0].getRawClass());
         assertEquals(String.class, params[1].getRawClass());
     }

     @Test
     public void testFindTypeParametersPropertiesFromType() {
         JavaType type = tf.constructFromCanonical("java.util.Properties");
         JavaType[] params = tf.findTypeParameters(type, Map.class);
         assertNotNull(params);
         assertEquals(2, params.length);
         assertEquals(String.class, params[0].getRawClass());
         assertEquals(String.class, params[1].getRawClass());
     }

     @Test
     public void testConstructSpecializedTypePropertiesFromHashtable() {
         JavaType base = tf.constructFromCanonical("java.util.Hashtable");
         JavaType specialized = tf.constructSpecializedType(base, Properties.class);
         assertTrue(specialized.isMapLikeType());
         assertEquals(String.class, specialized.getKeyType().getRawClass());
         assertEquals(String.class, specialized.getContentType().getRawClass());
     }

     @Test
     public void testConstructSpecializedTypePropertiesFromObject() {
         JavaType base = tf.constructFromCanonical("java.lang.Object");
         JavaType specialized = tf.constructSpecializedType(base, Properties.class);
         assertTrue(specialized.isMapLikeType());
         assertEquals(String.class, specialized.getKeyType().getRawClass());
         assertEquals(String.class, specialized.getContentType().getRawClass());
     }

     @Test
     public void testConstructSpecializedTypeSameClass() {
         JavaType base = tf.constructFromCanonical("java.util.Properties");
         JavaType specialized = tf.constructSpecializedType(base, Properties.class);
         assertSame(base, specialized);
     }

     @Test
     public void testConvertMapToProperties() throws Exception {
         Map<String, Object> map = new HashMap<String, Object>();
         map.put("x", 129);
         map.put("y", 123);
         ObjectMapper mapper = new ObjectMapper();
         Properties props = mapper.convertValue(map, Properties.class);
         assertNotNull(props);
         assertEquals("129", props.getProperty("x"));
         assertEquals("123", props.getProperty("y"));
     }

     @Test
     public void testReadPropertiesFromJson() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         Properties props = mapper.readValue("{\"x\":123}", Properties.class);
         assertEquals("123", props.getProperty("x"));
     }

     @Test
     public void testPropertiesCacheConsistency() {
         JavaType type1 = tf.constructFromCanonical("java.util.Properties");
         JavaType type2 = tf.constructFromCanonical("java.util.Properties");
         assertSame(type1, type2);
         assertEquals(String.class, type1.getKeyType().getRawClass());
         assertEquals(String.class, type1.getContentType().getRawClass());
     }

     @Test
     public void testPropertiesSubclass() {
         JavaType type = tf.constructFromCanonical("com.fasterxml.jackson.databind.type.TypeFactoryP
ropertiesTest$MyProperties");
         assertTrue(type.isMapLikeType());
         assertEquals(String.class, type.getKeyType().getRawClass());
         assertEquals(String.class, type.getContentType().getRawClass());
     }

     @Test
     public void testPropertiesNotSimpleType() {
         JavaType type = tf.constructFromCanonical("java.util.Properties");
         assertFalse(type instanceof SimpleType);
     }

 }
