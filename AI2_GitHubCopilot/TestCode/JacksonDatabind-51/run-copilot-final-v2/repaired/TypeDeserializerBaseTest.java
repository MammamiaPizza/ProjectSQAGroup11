package com.fasterxml.jackson.databind.jsontype.impl;

 import static org.junit.Assert.*;

 import java.io.IOException;
 import java.util.*;

 import org.junit.Test;

 import com.fasterxml.jackson.annotation.JsonTypeInfo;
 import com.fasterxml.jackson.annotation.JsonTypeIdResolver;
 import com.fasterxml.jackson.core.*;
 import com.fasterxml.jackson.databind.*;
 import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
 import com.fasterxml.jackson.databind.jsontype.*;
 import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
 import com.fasterxml.jackson.databind.jsontype.impl.TypeDeserializerBase;
 import com.fasterxml.jackson.databind.module.SimpleModule;
 import com.fasterxml.jackson.databind.type.TypeFactory;

 /**
  * Tests for {@link TypeDeserializerBase} targeting bug #1270: polymorphic
  * deserialization via custom {@link TypeIdResolver} for nested properties.
  */
 public class TypeDeserializerBaseTest {
     private final ObjectMapper mapper = new ObjectMapper();

     // -------------------------------------------------------------------
     //  Polymorphic hierarchy
     // -------------------------------------------------------------------
     @JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.PROPERTY,
             property = "type", visible = false)
     static abstract class PolyBase {
         public String common;
     }

     static class PolyA extends PolyBase {
         public String aVal;
     }

     static class PolyB extends PolyBase {
         public int bVal;
     }

     // Generic subtype – exercises constructSpecializedType / getRawClass() code path
     @SuppressWarnings("rawtypes")
     static class PolyGen<T> extends PolyBase {
         public T payload;

         public PolyGen() {
         }

         public PolyGen(T payload) {
             this.payload = payload;
         }
     }

     static class Container {
         public PolyBase options;

         public Container() {
         }

         public Container(PolyBase options) {
             this.options = options;
         }
     }

     // -------------------------------------------------------------------
     //  Custom TypeIdResolver that returns full generic type for PolyGen
     // -------------------------------------------------------------------
     public static class CustomTypeIdResolver extends TypeIdResolverBase {
         @Override
         public String idFromValue(Object value) {
             if (value instanceof PolyA) return "A";
             if (value instanceof PolyB) return "B";
             if (value instanceof PolyGen) return "GEN";
             throw new IllegalArgumentException("Unknown value type: " + value.getClass());
         }

         @Override
         public String idFromValueAndType(Object value, Class<?> suggestedType) {
             return idFromValue(value);
         }

         @Override
         public JavaType typeFromId(DatabindContext ctxt, String id) throws IOException {
             switch (id) {
                 case "A":
                     return ctxt.constructType(PolyA.class);
                 case "B":
                     return ctxt.constructType(PolyB.class);
                 case "GEN":
                     // return full generic type: PolyGen<String>
                     return ctxt.getTypeFactory().constructParametricType(PolyGen.class,
String.class);
                 default:
                     return null; // triggers unknown-type-id handling
             }
         }

         @Override
         public JsonTypeInfo.Id getMechanism() {
             return JsonTypeInfo.Id.CUSTOM;
         }
     }

     // Mix-in that attaches both @JsonTypeInfo and @JsonTypeIdResolver to PolyBase
     @JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.PROPERTY,
             property = "type", visible = false)
     @JsonTypeIdResolver(CustomTypeIdResolver.class)
     static abstract class PolyBaseMixIn {
     }

     @JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.PROPERTY,
             property = "type", visible = true)
     @JsonTypeIdResolver(CustomTypeIdResolver.class)
     static abstract class VisibleBaseMixIn {
     }

     // -------------------------------------------------------------------
     //  Helper to configure an ObjectMapper with our custom resolver
     // -------------------------------------------------------------------
     private ObjectMapper mapperWithResolver() {
         ObjectMapper m = new ObjectMapper();
         m.addMixIn(PolyBase.class, PolyBaseMixIn.class);
         return m;
     }

     private ObjectMapper mapperWithResolverAndVisible() {
         ObjectMapper m = new ObjectMapper();
         m.addMixIn(PolyBase.class, VisibleBaseMixIn.class);
         return m;
     }

     // ===================================================================
     //  Tests
     // ===================================================================

     /**
      * Core regression for bug #1270: a nested polymorphic property with
      * a custom resolver that returns a fully generified type must deserialize
      * correctly, preserving generic parameters.
      */
     @Test
     public void testPolymorhicViaCustomWithGenericSubtype() throws Exception {
         ObjectMapper m = mapperWithResolver();
         // Build a container containing a generic PolyGen<String>
         PolyGen<String> gen = new PolyGen<>("hello");
         Container orig = new Container(gen);
         String json = m.writeValueAsString(orig);
         // Expected JSON: {"options":{"type":"GEN","payload":"hello"}}
         Container result = m.readValue(json, Container.class);
         assertNotNull("Deserialized container must not be null", result);
         assertNotNull("Options property must be non-null", result.options);
         assertTrue("Should be PolyGen", result.options instanceof PolyGen);
         @SuppressWarnings("unchecked")
         PolyGen<String> genRes = (PolyGen<String>) result.options;
         assertEquals("Payload must survive round-trip", "hello", genRes.payload);
     }

     /**
      * Missing type id: falls back to default implementation (Void → null).
      */
     @Test
     public void testMissingTypeIdYeldsDefaultImpl() throws Exception {
         ObjectMapper m = mapperWithResolver();
         String json = "{\"options\":{}}";
         Container result = m.readValue(json, Container.class);
         assertNull("Without type-id, default impl (Void) should map to null", result.options);
     }

     /**
      * Unknown type id results in a JsonMappingException.
      */
     @Test(expected = JsonMappingException.class)
     public void testUnknownTypeIdThrows() throws Exception {
         ObjectMapper m = mapperWithResolver();
         String json = "{\"options\":{\"type\":\"NO_SUCH\"}}";
         m.readValue(json, Container.class);
     }

     /**
      * When _typeIdVisible is true the type-id property is also exposed
      * on the POJO and can be round-tripped.
      */
     @Test
     public void testTypeIdVisiblePropertyRoundTripp() throws Exception {
         ObjectMapper m = mapperWithResolverAndVisible();
         // Use a simple non-generic subtype to keep it clear
         PolyA a = new PolyA();
         a.common = "shared";
         a.aVal = "a-data";
         Container orig = new Container(a);
         String json = m.writeValueAsString(orig);
         Container result = m.readValue(json, Container.class);
         assertNotNull(result);
         assertTrue("Should be PolyA", result.options instanceof PolyA);
         PolyA deser = (PolyA) result.options;
         assertEquals("common field", "shared", deser.common);
         assertEquals("aVal field", "a-data", deser.aVal);
     }

     /**
      * Verifies that the concrete TypeIdResolver returned by getTypeIdResolver()
      * is the one supplied at construction.
      */
     @Test
     public void testGetTypeIdResolverReturnsSupplied() {
         CustomTypeIdResolver resolver = new CustomTypeIdResolver();
         TypeDeserializerBase td = new TestTypeDeserializer(
                 TypeFactory.defaultInstance().constructType(Object.class),
                 resolver, "type", false, null);
         assertSame(resolver, td.getTypeIdResolver());
     }

     /**
      * getPropertyName() returns the type-property name from constructor.
      */
     @Test
     public void testGetPropertyName() {
         TypeDeserializerBase td = new TestTypeDeserializer(
                 TypeFactory.defaultInstance().constructType(String.class),
                 new CustomTypeIdResolver(), "typename", false, null);
         assertEquals("typename", td.getPropertyName());
     }

     /**
      * BaseTypeName() returns the raw class name of the base type.
      */
     @Test
     public void testBaseTypeName() {
         TypeDeserializerBase td = new TestTypeDeserializer(
                 TypeFactory.defaultInstance().constructType(ArrayList.class),
                 new CustomTypeIdResolver(), "t", false, null);
         assertEquals("java.util.ArrayList", td.baseTypeName());
     }

     /**
      * Constructor replaces null typePropertyName with empty string to avoid NPE.
      */
     @Test
     public void testConstructorHandlesNullTypePropertyName() {
         TypeDeserializerBase td = new TestTypeDeserializer(
                 TypeFactory.defaultInstance().constructType(Object.class),
                 new CustomTypeIdResolver(), null, false, null);
         assertNotNull("getPropertyName() must not return null", td.getPropertyName());
         assertEquals("", td.getPropertyName());
     }

     /**
      * getDefaultImpl() returns the raw class of the passed default implementation.
      */
     @Test
     public void testGetDefaultImpl() {
         JavaType defaultType = TypeFactory.defaultInstance().constructType(HashMap.class);
         TypeDeserializerBase td = new TestTypeDeserializer(
                 TypeFactory.defaultInstance().constructType(Map.class),
                 new CustomTypeIdResolver(), "x", false, defaultType);
         assertEquals(HashMap.class, td.getDefaultImpl());
     }

     /**
      * toString() should not throw and contain base type name.
      */
     @Test
     public void testToString() {
         TypeDeserializerBase td = new TestTypeDeserializer(
                 TypeFactory.defaultInstance().constructType(String.class),
                 new CustomTypeIdResolver(), "typ", false, null);
         String str = td.toString();
         assertNotNull(str);
         assertTrue(str.contains("java.lang.String"));
     }

     // -------------------------------------------------------------------
     //  Minimal concrete subclass used for direct construction
     // -------------------------------------------------------------------
     static class TestTypeDeserializer extends TypeDeserializerBase {
         private TestTypeDeserializer(JavaType baseType, TypeIdResolver idRes,
                                      String typePropertyName, boolean typeIdVisible, JavaType
defaultImpl) {
             super(baseType, idRes, typePropertyName, typeIdVisible, defaultImpl);
         }

         @Override
         public TypeDeserializer forProperty(BeanProperty prop) {
             // not needed for these tests; return a copy that shares same data
             throw new UnsupportedOperationException("not implemented in test stub");
         }

         @Override
         public JsonTypeInfo.As getTypeInclusion() {
             return JsonTypeInfo.As.PROPERTY;
         }
     }
 }
