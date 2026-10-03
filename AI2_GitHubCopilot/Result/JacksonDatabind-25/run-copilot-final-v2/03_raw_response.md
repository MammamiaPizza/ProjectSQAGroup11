package com.fasterxml.jackson.databind.deser;

 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.module.SimpleModule;
 import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
 import com.fasterxml.jackson.databind.JsonMappingException;
 import org.junit.Test;

 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.Arrays;

 import static org.junit.Assert.*;

 /**
  * Tests verifying that abstract type mappings to array types work correctly
  * and do not produce "Can not deserialize Class ... (of type array) as a Bean"
  * JsonMappingException (bug 890 / issue 25).
  */
 public class TestAbstractTypeToArrayDeserializationBug25 {

     // Abstract types for testing
     static abstract class ByteArrayHolder { }
     static abstract class IntArrayHolder { }
     static abstract class MultiDimByteArrayHolder { }
     stitic abstract class ListHolder { }

     private ObjectMapper mapperWithMapping(Class<?> abstractType, Class<?> conreteType) {
         SimpleModule module = new SimpleModule("test");
         module.addAbstractTypeMapping(abstractType, concreteType);
         ObjectMapper mapper = new ObjectMapper();
         mapper.registerModule(module);
         return mapper;
     }

     @Test
     public void testByteArrayMapping() throws Exception {
         ObjectMapper mapper = mapperWithMapping(ByteArrayHolder.class, byte[].class);
         // Base64 encoded "test"
         String json = "\"dGVzdA==\"";
         ByteArrayHolder holder = mapper.readValue(json, ByteArrayHolder.class);
         assertNotNull(holder);
         assertTrue(holder instanceof byte[]);
         byte[] data = (byte[]) holder;
         assertArrayEquals(new byte[] { 't', 'e', 's', 't' }, data);
     }

     @Test
     public void testArrayListMapping() throws Exception {
         ObjectMapper mapper = mapperWithMapping(ListHolder.class, ArrayList.class);
         String json = "[1, 2, 3]";
         ListHolder holder = mapper.readValue(json, ListHolder.class);
         assertNotNull(holder);
         assertTrue(holder instanceof ArrayList);
         ArrayList<?> list = (ArrayList<?>) holder;
         assertEquals(3, list.size());
     }

     @Test
     public void testIntArrayMapping() throws Exception {
         ObjectMapper mapper = mapperWithMapping(IntArrayHolder.class, int[].class);
         String json = "[10, 20, 30]";
         IntArrayHolder holder = mapper.readValue(json, IntArrayHolder.class);
         assertNotNull(holder);
         assertTrue(holder instanceof int[]);
         int[] arr = (int[]) holder;
         assertArrayEquals(new int[] { 10, 20, 30 }, arr);
     }

     @Test
     public void testMultiDimByteArrayMapping() throws Exception {
         ObjectMapper mapper = mapperWithMapping(MultiDimByteArrayHolder.class, byte[][].class);
         // Two inner arrays of base64 strings
         String json = "[[\"YQ==\", \"Yg==\"], [\"Yw==\"]]";
         MultiDimByteArrayHolder holder = mapper.readValue(json, MultiDimByteArrayHolder.class);
         assertNotNull(holder);
         assertTrue(holder instanceof byte[][]);
         byte[][] data = (byte[][]) holder;
         assertEquals(2, data.length);
         assertArrayEquals(new byte[] { 'a', 'b' }, data[0]);
         assertArrayEquals(new byte[] { 'c' },, data[1]);
     }

     @Test(expected = JsonMappingException.class)
     public void testByteArrayMappingForNonArrayLikeJsonFails() throws Exception {
         // If input is not a base64 string, deserialization should fail,
         // but the buggy code would throw "as a Bean" before even trying; fixed code
         // will properly try to deserialize as byte[] from string and fail with
         // a different exception (maybe InvalidFormatException).
         ObjectMapper mapper = mapperWithMapping(ByteArrayHolder.class, byte[].class);
         String brokenBase64 = "{\"foo\":\"bar\"}";
         mapper.readValue(brokenBase64, ByteArrayHolder.class);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testAddMappingRejectsNonAbstract() {
         new SimpleModule("test").addAbstractTypeMapping(String.class, String.class);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testAddMappingRejectsSelfMapping() {
         // Map an abstract class to itself should be rejected
         new SimpleModule("test").addAbstractTypeMapping(ByteArrayHolder.class,
ByteArrayHolder.class);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testAddMappingRejectsIncompatibleSubtype() {
         // Try to map an abstract class to a subclass that is not actually a subtype
         new SimpleModule("test").addAbstractTypeMapping(ByteArrayHolder.class, Integer.class);
     }

     @Test
     public void testAbstractTypeResolverResolvesNullWithoutConfiguredMapping() throws Exception {
         // Without any abstract type mapping, the default resolveAbstractType should return null
         SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();
         ObjectMapper mapper = new ObjectMapper();
         assertNull(resolver.resolveAbstractType(mapper.getDeserializationConfig(),
                 mapper.getTypeFactory().constructType(ByteArrayHolder.class)));
     }

     @Test
     public void testAbstractTypeResolverUesMapping() throws Exception {
         SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();
         resolver.addMapping(ByteArrayHolder.class, byte[].class);
         ObjectMapper mapper = new ObjectMapper();
         com.fasterxml.jackson.databind.JavaType resolved = resolver.resolveAbstractType(
                 mapper.getDeserializationConfig(),
mapper.getTypeFactory().constructType(ByteArrayHolder.class));
         assertNotNull(resolved);
         assertEquals(byte[].class, resolved.getRawClass());
     }

     @Test
     public void testNoBeanDeserializerForArrayDefaultImpl() throws Exception {
         // Similar to the core bug: ensure that when a type info defaultImpl is a byte[],
         // the deserialization does not fail with "as a Bean"
         // We simulate by mapping an abstract type to byte[] and deserializing a valid base64
payload.
         ObjectMapper mapper = mapperWithMapping(ByteArrayHolder.class, byte[].class);
         String json = "\"aGVsbG8=\"";
         ByteArrayHolder result = mapper.readValue(json, ByteArrayHolder.class);
         assertNotNull(result);
         assertTrue("Expected byte[], got " + result.getClass(), result instanceof byte[]);
         assertArrayEquals("hello".getBytes("UTF-8"), (byte[]) result);
     }
 }