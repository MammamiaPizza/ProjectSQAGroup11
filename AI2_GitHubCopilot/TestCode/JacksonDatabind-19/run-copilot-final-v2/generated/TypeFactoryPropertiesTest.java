package com.fasterxml.jackson.databind.type;

import java.util.*;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import static org.junit.Assert.*;

public class TypeFactoryPropertiesTest
{
    private final TypeFactory tf = TypeFactory.defaultInstance();

 @Test
 public void testPropertiesKeyAndValueType() {
     JavaType type = tf.constructType(Properties.class);
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
     JavaType type = tf.constructType(Properties.class);
     JavaType[] params = tf.findTypeParameters(type, Map.class);
     assertNotNull(params);
     assertEquals(2, params.length);
     assertEquals(String.class, params[0].getRawClass());
     assertEquals(String.class, params[1].getRawClass());
 }

 @Test
 public void testConstructSpecializedTypePropertiesFromHashtable() {
     JavaType base = tf.constructType(Hashtable.class);
     JavaType specialized = tf.constructSpecializedType(base, Properties.class);
     assertTrue(specialized.isMapLikeType());
     assertEquals(String.class, specialized.getKeyType().getRawClass());
     assertEquals(String.class, specialized.getContentType().getRawClass());
 }

 @Test
 public void testConstructSpecializedTypePropertiesFromObject() {
     JavaType base = tf.constructType(Object.class);
     JavaType specialized = tf.constructSpecializedType(base, Properties.class);
     assertTrue(specialized.isMapLikeType());
     assertEquals(String.class, specialized.getKeyType().getRawClass());
     assertEquals(String.class, specialized.getContentType().getRawClass());
 }

 @Test
 public void testConstructSpecializedTypeSameClass() {
     JavaType base = tf.constructType(Properties.class);
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
     JavaType type1 = tf.constructType(Properties.class);
     JavaType type2 = tf.constructType(Properties.class);
     assertSame(type1, type2);
     assertEquals(String.class, type1.getKeyType().getRawClass());
     assertEquals(String.class, type1.getContentType().getRawClass());
 }

 @Test
 public void testPropertiesSubclass() {
     class MyProperties extends Properties { }
     JavaType type = tf.constructType(MyProperties.class);
     assertTrue(type.isMapLikeType());
     assertEquals(String.class, type.getKeyType().getRawClass());
     assertEquals(String.class, type.getContentType().getRawClass());
 }

 @Test
 public void testPropertiesNotSimpleType() {
     JavaType type = tf.constructType(Properties.class);
     assertFalse(type instanceof SimpleType);
 }

}
