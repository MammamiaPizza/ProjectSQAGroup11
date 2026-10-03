package com.fasterxml.jackson.databind.jsontype;

 import static org.junit.Assert.*;

 import org.junit.Test;

 import com.fasterxml.jackson.annotation.JsonSubTypes;
 import com.fasterxml.jackson.annotation.JsonTypeInfo;
 import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
 import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.JsonMappingException;

 public class AsPropertyTypeDeserializerTest {

     @JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type",
                   defaultImpl = DefaultImpl.class)
     @JsonSubTypes(@JsonSubTypes.Type(value = ConcreteImpl.class, name = "concrete"))
     public static class Base {
         public int a = 0;
     }

     @JsonTypeInfo(use = Id.NONE)
     public static class DefaultImpl extends Base {
         public DefaultImpl() { a = -1; }
     }

     public static class ConcreteImpl extends Base {
         public ConcreteImpl() { }
     }

     @JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type",
                   defaultImpl = Void.class)
     public static class WithVoidDefault extends Base { }

     private final ObjectMapper mapper = new ObjectMapper();

     @Test
     public void testEmptyStringWithDefaultImpl() throws Exception {
         Base result = mapper.readValue("\"\"", Base.class);
         assertNotNull(result);
         assertTrue(result instanceof DefaultImpl);
         assertEquals(-1, result.a);
     }

     @Test
     public void testEptyStringWithVoidDefault() throws Exception {
         WithVoidDefault result = mapper.readValue("\"\"", WithVoidDefault.class);
         assertNull(result);
     }

     @Test
     public void testMissingTypePropetyWithDefaultImpl() throws Exception {
         Base result = mapper.readValue("{\"a\":5}", Base.class);
         assertNotNull(result);
         assertTrue(result instanceof DefaultImpl);
         assertEquals(5, result.a);
     }

     @Test
     public void testNormalValidTypeId() throws Exception {
         Base result = mapper.readValue("{\"type\":\"concrete\",\"a\":7}", Base.class);
         assertNotNull(result);
         assertTrue(result instanceof ConcreteImpl);
         assertEquals(7, result.a);
     }

     @Test
     public void testWhitespaceTokenWithDefaultImpl() throws Exception {
         Base result = mapper.readValue("\"   \"", Base.class);
         assertNotNull(result);
         assertTrue(result instanceof DefaultImpl);
         assertEquals(-1, result.a);
     }

     @Test
     public void testNullTokenWithDefaultImpl() throws Exception {
         Base result = mapper.readValue("null", Base.class);
         assertNull(result);
     }

     @Test
     public void testEmptyStringInsideArray() throws Exception {
         Base[] result = mapper.readValue("[\"\"]", Base[].class);
         assertNotNull(result);
         assertEquals(1, result.length);
         assertTrue(result[0] instanceof DefaultImpl);
         assertEquals(-1, result[0].a);
     }

     @Test
     public void testEmptyTypeIdValueInObject() throws Exception {
         Base result = mapper.readValue("{\"type\":\"\",\"a\":3}", Base.class);
         assertNotNull(result);
         assertTrue(result instanceof DefaultImpl);
         assertEquals(3, result.a);
     }

     @Test
     public void testTypePropertyAfterManyFields() throws Exception {
         String json = "{\"x\":1,\"y\":2,\"z\":3,\"type\":\"concrete\",\"a\":99}";
         Base result = mapper.readValue(json, Base.class);
         assertNotNull(result);
         assertTrue(result instanceof ConcreteImpl);
         assertEquals(99, result.a);
     }

     @Test
     public void testDeserializeTypedFromAnyWithObject() throws Exception {
         Base result = mapper.readValue("\"\"", Base.class);
         assertNotNull(result);
         assertTrue(result instanceof DefaultImpl);
     }

     @Test
     public void testDeserializeTypedFromAnyWithArray() throws Exception {
         Base[] result = mapper.readValue("[\"\",\"\"]", Base[].class);
         assertNotNull(result);
         assertEquals(2, result.length);
         assertTrue(result[0] instanceof DefaultImpl);
         assertTrue(result[1] instanceof DefaultImpl);
     }
 }