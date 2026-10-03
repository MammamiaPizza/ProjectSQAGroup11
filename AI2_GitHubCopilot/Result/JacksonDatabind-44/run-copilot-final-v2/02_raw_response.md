package com.fasterxml.jackson.databind.type;

 import static org.junit.Assert.*;

 import java.util.*;

 import org.junit.Test;

 import com.fasterxml.jackson.annotation.*;
 import com.fasterxml.jackson.databind.*;
 import com.fasterxml.jackson.databind.introspect.*;

 /**
  * Tests for {@link SimpleType} that target the bug described in
  * <a href="]8;id=md-iapdfm;https://github.com/FasterXML/jackson-databind/issues/1125https://github.com/FasterXML/jackson-databind/issues/1125]8;;]8;;">#1125</a>:]8;;
  * {@code _narrow} may cause properties of subtypes to be invisible during
  * default-typing deserialization, leading to
  * {@link UnrecognizedPropertyException}.
  */
 public class TestSimpleTypeBug1125 {

     // ----------------------------------------------------------------
     // Helper classes to exercise polymorphism / property visibility
     // ----------------------------------------------------------------

     @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property =
"type")
     @JsonSubTypes({@JsonSubTypes.Type(value = Sub1125.class, name = "sub")})
     static class Base1125 {
         public int a;
         public int def;
     }

     @JsonTypeName("sub")
     static class Sub1125 extends Base1125 {
         public int b;
     }

     // For direct _narrow / constructUnsafe checks
     static class PlainBase {
         public String baseField;
     }

     static class PlainSub extends PlainBase {
         public String subField;
     }

     /**
      * Exposes protected {@link SimpleType#_narrow(Class)} for testing.
      */
     static class ExposedSimpleType extends SimpleType {
         private static final long serialVersionUID = 1L;

         ExposedSimpleType(Class<?> cls) {
             super(cls);
         }

         @Override
         public JavaType _narrow(Class<?> subclass) {
             return super._narrow(subclass);
         }
     }

     // ----------------------------------------------------------------
     // 1–3: Illegal arguments – containers / arrays via construct(Class)
     // ----------------------------------------------------------------

     @Test(expected = IllegalArgumentException.class)
     public void testConstructMapThrows() {
         SimpleType.construct(HashMap.class);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testConstructCollectionThrows() {
         SimpleType.construct(ArrayList.class);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testConstructArrayThrows() {
         SimpleType.construct(int[].class);
     }

     // ----------------------------------------------------------------
     // 4–5: _narrow preserves raw class and super-class linkage
     // ----------------------------------------------------------------

     @Test
     public void testNarrowPreservesRawClass() {
         ExposedSimpleType base = new ExposedSimpleType(PlainBase.class);
         JavaType narrowed = base._narrow(PlainSub.class);
         assertEquals("_narrow must change raw class to subclass",
                 PlainSub.class, narrowed.getRawClass());
     }

     @Test
     public void testNarrowSetsSuperClassChain() {
         ExposedSimpleType base = new ExposedSimpleType(PlainBase.class);
         JavaType narrowed = base._narrow(PlainSub.class);
         // The narrowed type's super-class must be the original declared type.
         assertSame("super-class must be the original type",
                 base, narrowed.getSuperClass());
     }

     // ----------------------------------------------------------------
     // 6: constructUnsafe – property introspection still works
     // ----------------------------------------------------------------

     @Test
     public void testConstructUnsafeProperties() {
         SimpleType unsafe = SimpleType.constructUnsafe(PlainSub.class);
         ObjectMapper mapper = new ObjectMapper();
         // Introspect using the mapper's configuration
         BeanDescription beanDesc = mapper.getSerializationConfig().introspect(unsafe);
         List<BeanPropertyDefinition> props = beanDesc.findProperties();

         // We expect at least "baseField" and "subField"
         boolean foundBase = false;
         boolean foundSub = false;
         for (BeanPropertyDefinition p : props) {
             if ("baseField".equals(p.getName())) foundBase = true;
             if ("subField".equals(p.getName())) foundSub = true;
         }
         assertTrue("baseField from superclass must be visible", foundBase);
         assertTrue("subField from subclass must be visible", foundSub);
     }

     // ----------------------------------------------------------------
     // 7: withTypeHandler preserves the handler
     // ----------------------------------------------------------------

     @Test
     public void testWithTypeHandler() {
         Object handler = new Object(); // dummy type handler
         SimpleType original = SimpleType.constructUnsafe(PlainSub.class);
         SimpleType withHandler = original.withTypeHandler(handler);
         assertNotSame("withTypeHandler should return a new instance", original, withHandler);
         assertSame("type handler must be set", handler, withHandler.getTypeHandler());
     }

     // ----------------------------------------------------------------
     // 8–9: Default-typing deserialization – the trigger for bug #1125
     // ----------------------------------------------------------------

     @Test
     public void testDefaultTypingWithSubtypeProperty() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         // Enable default typing (similar to DefaultTyping enum)
         mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
         String json = "{\"type\":\"sub\",\"a\":1,\"def\":2,\"b\":3}";

         Base1125 result = mapper.readValue(json, Base1125.class);
         assertTrue("result must be subtype", result instanceof Sub1125);
         assertEquals(3, ((Sub1125) result).b);
     }

     @Test
     public void testDefaultTypingSubclassRoundtrip() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

         Sub1125 sub = new Sub1125();
         sub.a = 10;
         sub.def = 20;
         sub.b = 30;

         String json = mapper.writeValueAsString(sub);
         // Deserialize back as declared base type – should keep b
         Base1125 result = mapper.readValue(json, Base1125.class);
         assertTrue(result instanceof Sub1125);
         assertEquals(30, ((Sub1125) result).b);
     }

     // ----------------------------------------------------------------
     // 10: sanity – construct on a regular class works
     // ----------------------------------------------------------------

     @Test
     public void testConstructValidClass() {
         SimpleType t = SimpleType.construct(PlainBase.class);
         assertEquals(PlainBase.class, t.getRawClass());
         assertNotNull(t);
     }
 }```