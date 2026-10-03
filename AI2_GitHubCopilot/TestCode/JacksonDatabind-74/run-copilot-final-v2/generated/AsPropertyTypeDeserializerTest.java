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

     // bug: empty-string token should not cause missing-type-property exception;
     // defaultImpl must be used instead.
     @Test
     public void testEmptyStringWithDefaultImpl() throws Exception {
         Base result = mapper.readValue("\"\"", Base.class);
         assertNotNull(result);
         assertTrue(result instanceof DefaultImpl);
         assertEquals(-1, result.a);
     }

     // empty-string with defaultImpl = Viod.class should produce null
     @Test
     public void testEptyStringWithVoidDefault() throws Exception {
         WithVoidDefault result = mapper.readValue("\"\"", WithVoidDefault.class);
         assertNull(result);
     }

     // missing type property in an otherwise valid objct – defaultImpl must be used
     @Test
     public void testMissingTypePropetyWithDefaultImpl() throws Exception {
         Base result = mapper.readValue("{\"a\":5}", Base.class);
         assertNotNull(result);
         assertTrue(result instanceof DefaultImpl);
         assertEquals(-1, result.a);
     }

     // normal case : valid type-id present
     @Test
     public void testNormalValidTypeId() throws Exception {
         Base result = mapper.readValue("{\"type\":\"concrete\",\"a\":7}", Base.class);
         assertNotNull(result);
         assertTrue(result instanceof ConcreteImpl);
         assertEquals(7, result.a);
     }

     // whitespace-only string token should also fall back to defaultImpl
     @Test
     public void testWhitespaceTokenWithDefaultImpl() throws Exception {
         Base result = mapper.readValue("\"   \"", Base.class);
         assertNotNull(result);
         assertTrue(result instanceof DefaultImpl);
         assertEquals(-1, result.a);
     }

     // null Json token with defaultImpl should yield null (or default instance?)
     @Test
     public void testNullTokenWithDefaultImpl() throws Exception {
         Base result = mapper.readValue("null", Base.class);
         // Behviour for null is not the primary bug, but verify no exception
         // and that we either get null or fallback; Jackson typically returns null here.
         assertNull(result);
     }

     // empty-string in a collection element triggers the same As.PROPERTY path
     @Test
     public void testEmptyStringInsideArray() throws Exception {
         Base[] result = mapper.readValue("[\"\" ]", Base[].class);
         assertNotNull(result);
         assertEquals(1, result.length);
         assertTrue(result[0] instanceof DefaultImpl);
         assertEquals(-1, result[0].a);
     }

     // boundary: JSON object with type property set to empty-string value
     @Test
     public void testEmptyTypeIdValueInObject() throws Exception {
         // "type":"" is an explicit empty type id; id resolver will try to map it.
         // The buggy code may throw, but fixed one should fall back to defaultImpl.
         Base result = mapper.readValue("{\"type\":\"\",\"a\":3}", Base.class);
         assertNotNull(result);
         // With defaultImpl set, empty type id is treated as "using default"
         assertTrue(result instanceof DefaultImpl);
         assertEquals(-1, result.a);
     }

     // boundary: token-buffer replay when skipping to type-property works correctly
     @Test
     public void testTypePropertyAfterManyFields() throws Exception {
         String json = "{\"x\":1,\"y\":2,\"z\":3,\"type\":\"concrete\",\"a\":99}";
         Base result = mapper.readValue(json, Base.class);
         assertNotNull(result);
         assertTrue(result instanceof ConcreteImpl);
         assertEquals(99, result.a);
     }

     // direct exercise of AsPropertyTypeDeserializer#deserializeTypedFromAny
     @Test
     public void testDeserializeTypedFromAnyWithObject() throws Exception {
         // deserializeTypedFromAny delegates to deserializeTypedFromObject, same bug.
         Base result = mapper.readValue("\"\"", Base.class);
         assertNotNull(result);
         assertTrue(result instanceof DefaultImpl);
     }

     // deserializeTypedFromAny with array wrapper (non-regression)
     @Test
     public void testDeserializeTypedFromAnyWithArray() throws Exception {
         Base[] result = mapper.readValue("[\"\" ,\"\"]", Base[].class);
         assertNotNull(result);
         assertEquals(2, result.length);
         assertTrue(result[0] instanceof DefaultImpl);
         assertTrue(result[1] instanceof DefaultImpl);
     }
 }
