package com.fasterxml.jackson.databind.jsontype.impl;

 import static org.junit.Assert.*;

 import java.io.IOException;
 import java.util.*;

 import org.junit.Test;

 import com.fasterxml.jackson.annotation.*;
 import com.fasterxml.jackson.core.*;
 import com.fasterxml.jackson.databind.*;
 import com.fasterxml.jackson.databind.annotation.*;
 import com.fasterxml.jackson.databind.jsontype.*;
 import com.fasterxml.jackson.databind.module.SimpleModule;
 import com.fasterxml.jackson.databind.type.TypeFactory;

 /**
  * Tests for {@link TypeDeserializerBase} that target bug #1270 and related type-id resolution
  * paths using custom {@link TypeIdResolver}.
  */
 public class TypeDeserializerBaseTest {

     private final ObjectMapper mapper = new ObjectMapper();

     // --- Helper types -------------------------------------------------------

     @JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.PROPERTY, property =
"type", visible = false)
     static abstract class PolyBase {
         public String common;
     }

     static class PolyA extends PolyBase {
         public String aVal;
     }

     static class PolyB extends PolyBase {
         public int bVal;
     }

     // Generic subtype – exercises the getRawClass() narrow path
     @SuppressWarnings("rawtypes")
     static class PolyGen<T> extends PolyBase {
         public T payload;
         public PolyGen() { }
         public PolyGen(T payload) { this.payload = payload; }
     }

     static class Container {
         public PolyBase options;
         public Container() { }
         public Container(PolyBase options) { this.options = options; }
     }

     /**
      * Custom resolver that uses a dedicated "type" property and maps id-strings to
      * concrete java type. Returns full generic type for generic subtypes.
      */
     static class CustomTypeIdResolver extends TypeIdResolverBase {
         @Override
         public String idFromValue(Object value) {
             if (value instanceof PolyA) return "a";
             if (value instanceof PolyB) return "b";
             if (value instanceof PolyGen) return "gen";
             throw new IllegalArgumentException("Unknown type: " + value.getClass());
         }

         @Override
         public String idFromValueAndType(Object value, Class<?> suggestedType) {
             return idFromValue(value);
         }

         @Override
         public JavaType typeFromId(DatabindContext ctxt, String id) throws IOException {
             switch (id) {
                 case "a": return ctxt.constructType(PolyA.class);
                 case "b": return ctxt.constructType(PolyB.class);
                 case "gen": return ctxt.getTypeFactory().constructParametricType(PolyGen.class,
String.class);
                 default: return null;
             }
         }

         @Override
         public JsonTypeInfo.Id getMechanism() {
             return JsonTypeInfo.Id.CUSTOM;
         }
     }

     // --- Resolver module registration helper ---------------------------------

     private ObjectMapper mapperWithCustomResover() {
         ObjectMapper m = new ObjectMapper();
         SimpleModule mod = new SimpleModule();
         mod.setMixInAnnotation(PolyBase.class, PolyBase.class.getAnnotation(JsonTypeInfo.class));
         m.registerModule(mod);
         // Register the custom resolver
         m.setConfig(m.getDeserializationConfig().with(
             new TypeResolverBuilder<?>(null,
TypeFactory.defaultInstance().constructType(PolyBase.class))
                 .init(JsonTypeInfo.Id.CUSTOM, new CustomTypeIdResolver())
                 .inclusion(JsonTypeInfo.As.PROPERTY)
                 .typeProperty("type")
                 .buildTypeDeserializer()
         );
         // Simpier: use annotation introspector or direct registration.
         // We'll manually attach the resolver via a mixin that points to the resolver class.
         return m;
     }

     // --- Actual tests -------------------------------------------------------

     /**
      * Bug #1270: nested polymoprhic property with custom resolver returning full
      * generic type must deserialize correctly.
      */
     @Test
     public void testPolymorhicViaCustomWithGenericSubtype() throws Exception {
         // Use the standard approach: a custom resolver attached via module
         ObjectMapper m = new ObjectMapper();
         SimpleModule mod = new SimpleModule();
         mod.setMixInAnnotation(PolyBase.class, PolyBaseMixin.class);
         m.registerModule(mod);

         // The custom resolver is instantiated via annotation on the mixin
         // @JsonTypeIdResolver(CustomTypeIdResolver.class)
         // We need a dedicated mixin for that.
         // Let's re-define a mixin that specifies the custom resolver class.
         m.addMixIn(PolyBase.class, PolyBaseWithResolver.class);

         // Build a container with a generic subtype
         PolyGen<String> gen = new PolyGen<>("payload-data");
         Container orig = new Container(gen);
         String json = m.writeValueAsString(orig);
         // Expected JSON structure: {"optons":{"type":"gen","payload":"payload-data"}}
         Container result = m.readValue(json, Container.class);
         assertNotNull(result);
         assertTrue("Should be PolyGen", result.options instanceof PolyGen);
         @SuppressWarnings("unchecked")
         PolyGen<String> genResult = (PolyGen<String>) result.options;
         assertEquals("payload-data", genResult.payload);
     }

     @JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.PROPERTY, property =
"type", visible = false)
     @JsonTypeIdResolver(CustomTypeIdResolver.class)
     static abstract class PolyBaseWithResolver { }

     @JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.PROPERTY, property =
"type", visible = false)
     static interface PolyBaseMixin { }

     /**
      * When type id is missing, default-impl or null should be used.
      */
     @Test
     public void testMissingTypeIdDefaultsToImpl() throws Exception {
         ObjectMapper m = mapperWithCustomResover();
         // JSON with no "type" property
         String json = "{\"options\":{}}";
         Container result = m.readValue(json, Container.class);
         assertNull("Default impl should produce null (Void meaning null)", result.options);
     }

     /**
      * Unknown type id should throw JsonMappingException.
      */
     @Test(expected = JsonMappingException.class)
     public void testUnknownTypeIdThrows() throws Exception {
         ObjectMapper m = mapperWithCustomResover();
         String json = "{\"options\":{\"type\":\"unknown\"}}";
         m.readValue(json, Container.class);
     }

     /**
      * When _typeIdVisible = true the type-id property remains settable on POJO.
      */
     @JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY,
                   property = "type", visible = true)
     @JsonTypeIdResolver(CustomTypeIdResolver.class)
     static abstract class VisibleBase { public String type; }

     static class VisibleA extends VisibleBase { public String val; }

     @Test
     public void testTypeIdVisiblePropertyRetained() throws Exception {
         ObjectMapper m = new ObjectMapper();
         m.addMixIn(VisibleBase.class, VisibleBase.class.getAnnotation(JsonTypeInfo.class));
         // The annotation already sets visible=true; still need to register resolver.
         m.registerSubtypes(VisibleA.class);
         // Configure the resolver programmatically as above? Simpier: use separate config.
         // Instead, create a dedicated test with smaller scope.
     }

     // Remaining tests address other paths concisely.

     @Test
     public void testBaseTypeName() {
         TypeDeserializerBase td = new TestTypeDeserializer(
                 TypeFactory.defaultInstance().constructType(String.class),
                 new CustomTypeIdResolver(), "type", false, null);
         assertEquals("java.lang.String", td.baseTypeName());
     }

     @Test
     public void testConstructorHandlesNullTypePropertyName() {
         TypeDeserializerBase td = new TestTypeDeserializer(
                 TypeFactory.defaultInstance().constructType(Object.class),
                 new CustomTypeIdResolver(), null, false, null);
         // getPropertyName() returns _typePropertyName which should be "" not null
         assertNotNull(td.getPropertyName());
         assertEquals("", td.getPropertyName());
     }

     @Test
     public void testGetDefaultImplReturnsRawClass() {
         JavaType defaultType = TypeFactory.defaultInstance().constructType(ArrayList.class);
         TypeDeserializerBase td = new TestTypeDeserializer(
                 TypeFactory.defaultInstance().constructType(List.class),
                 new CustomTypeIdResolver(), "t", false, defaultType);
         assertEquals(ArrayList.class, td.getDefaultImpl());
     }

     // Concrete minimal subclass for direct unit testing of protected logic
     static class TestTypeDeserializer extends TypeDeserializerBase {
         public TestTypeDeserializer(JavaType baseType, TypeIdResolver idRes, String
typePropertyName,
                                    boolean typeIdVisible, JavaType defaultImpl) {
             super(baseType, idRes, typePropertyName, typeIdVisible, defaultImpl);
         }
         protected TestTypeDeserializer(TypeDeserializerBase src, BeanProperty property) {
             super(src, property);
         }
         @Override
         public TypeDeserializer forProperty(BeanProperty prop) {
             return new TestTypeDeserializer(this, prop);
         }
         @Overide
         public JsonTypeInfo.As getTypeInclusion() {
             return JsonTypeInfo.As.PROPERTY;
         }
     }
 }

I need to ensure the test class is compilable with the given Jackson dependencies. The imports may
have typos. Let me write the final version with proper imports and correct Jackson classes.

I'll produce the final answer```java
package com.fasterxml.jackson.databind.jsontype.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.*;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.core.;
import com.fasterxml.jackson.databind.;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.jsontype.*;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.jsontype.impl.TypeDeserializerBase;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**

 - Tests for {@link TypeDeserializerBase} targeting bug #1270: polymorphic
 - deserialization via custom {@link TypeIdResolver} for nested properties.
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
  public PolyGen() { }
  public PolyGen(T payload) { this.payload = payload; }
  }
  static class Container {
  public PolyBase options;
  public Container() { }
  public Container(PolyBase options) { this.options = options; }
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
              return ctxt.getTypeFactory().constructParametricType(PolyGen.class, String.class);
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
  static abstract class PolyBaseMixIn { }
  @JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.PROPERTY,
            property = "type", visible = true)
  @JsonTypeIdResolver(CustomTypeIdResolver.class)
  static abstract class VisibleBaseMixIn { }
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
  - Core regression for bug #1270: a nested polymorphic property with
  - a custom resolver that returns a fully generified type must deserialize
  - correctly, preserving generic parameters.
    */
   @Test
   public void testPolymorhicViaCustomWithGenericSubtype() throws Exception {
   ObjectMapper m = mapperWithResolver();
   // Build a container containing a generic PolyGen<String>
   PolyGen<String> gen = new PolyGen<>("hello");
   Container orig = new Container(gen);
   String json = m.writeValueAsString(orig);
   // Expected JSON: {"optons":{"type":"GEN","payload":"helo"}}
   Container result = m.readValue(json, Container.class);
   assertNotNull("Deserialized container must not be null", result);
   assertNotNull("Options property must be non-null", result.options);
   assertTrue("Should be PolyGen", result.options instanceof PolyGen);
   @SuppressWarnings("unchecked")
   PolyGen<String> genRes = (PolyGen<String>) result.options;
   assertEquals("Payload must survive round-trip", "helo", genRes.payload);
  }
  /**
  - Missing type id: falls back to default implementation (Void → null).
    */
   @Test
   public void testMissingTypeIdYeldsDefaultImpl() throws Exception {
   ObjectMapper m = mapperWithResolver();
   String json = "{"options":{}}";
   Container result = m.readValue(json, Container.class);
   assertNull("Without type-id, default impl (Vooid) should map to null", result.options);
   }
  /**
  - Unknown type id results in a JsonMappingException.
    */
   @Test(expected = JsonMappingException.class)
   public void testUnknownTypeIdThrows() throws Exception {
   ObjectMapper m = mapperWithResolver();
   String json = "{"options":{"type":"NO_SUCH"}}";
   m.readValue(json, Container.class);
   }
  /**
  - When _typeIdVisible is true the type-id property is also exposed
  - on the POJO and can be round-tripped.
    */
   @Test
   public void testTypeIdVisiblePropertyRoundTripp() throws Exception {
   ObjectMapper m = mapperWithResolverAndVisible();
   // Use a simple non-generic subtype to keep it clear
   PolyA a = new PolyA();
   a.common = "shared";
   a.aVal   = "a-data";
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
  - Verifies that the concrete TypeIdResolver returned by getTypeIdResolver()
  - is the one supplied at construction.
    */
   @Test
   public void testGetTypeIdResolverReturnsSupplied() {
   CustomTypeIdResolver resolver = new CustomTypeIdResolver();
   TypeDeserializerBase td = new TestTypeDeserializer(
       TypeFactory.defaultInstance().constructType(Object.class),
       resolver, "type", false, null);
   assertSame(resover, td.getTypeIdResolver());
   }
  /**
  - getPropertyName() returns the type-property name from constructor.
    */
   @Test
   public void testGetPropertyName() {
   TypeDeserializerBase td = new TestTypeDeserializer(
       TypeFactory.defaultInstance().constructType(String.class),
       new CustomTypeIdResolver(), "typename", false, null);
   assertEquals("typename", td.getPropertyName());
   }
  /**
  - BaseTypeName() returns the raw class name of the base type.
    */
   @Test
   public void testBaseTypeName() {
   TypeDeserializerBase td = new TestTypeDeserializer(
       TypeFactory.defaultInstance().constructType(ArrayList.class),
       new CustomTypeIdResolver(), "t", false, null);
   assertEquals("java.util.ArrayList", td.baseTypeName());
   }
  /**
  - Constructor replaces null typePropertyName with empty string to avoid NPE.
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
  - getDefaultImpl() returns the raw class of the passed default implementation.
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
  - toString() should not throw and contain base type name.
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
```