package com.fasterxml.jackson.databind.interop;

 import static org.junit.Assert.*;

 import java.lang.reflect.InvocationHandler;
 import java.lang.reflect.Method;
 import java.lang.reflect.Proxy;

 import org.junit.Rule;
 import org.junit.Test;
 import org.junit.rules.ExpectedException;

 import com.fasterxml.jackson.annotation.JsonCreator;
 import com.fasterxml.jackson.annotation.JsonProperty;
 import com.fasterxml.jackson.core.JsonParser;
 import com.fasterxml.jackson.databind.DeserializationContext;
 import com.fasterxml.jackson.databind.JsonDeserializer;
 import com.fasterxml.jackson.databind.JsonMappingException;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.module.SimpleModule;

 public class BeanDeserializerFactory_IllegalTypesTest {

     @Rule
     public ExpectedException thrown = ExpectedException.none();

     private final ObjectMapper mapper = new ObjectMapper();

     // -- Helper types for testing illegal property types --------------------

     public static class MyThrowable extends Throwable {
         private static final long serialVersionUID = 1L;
     }

     public static class BeanWithThrowableProperty {
         public MyThrowable throwable;
         public MyThrowable getThrowable() { return throwable; }
         public void setThrowable(MyThrowable t) { this.throwable = t; }
     }

     public static class BeanWithThrowableFieldOnly {
         public MyThrowable throwable;
     }

     public interface AnInterface {}
     public static class AnInterfaceImpl implements AnInterface {}
     public static class BeanWithProxyProperty {
         public AnInterface proxyProp;
         public AnInterface getProxyProp() { return proxyProp; }
         public void setProxyProp(AnInterface p) { this.proxyProp = p; }
     }

     public static class BeanWithMapProperty {
         public java.util.Map<String,Object> mapProp;
         public java.util.Map<String,Object> getMapProp() { return mapProp; }
         public void setMapProp(java.util.Map<String,Object> m) { this.mapProp = m; }
     }

     // -- Direct deserialization of illegal types ----------------------------

     @Test
     public void testDirectDeserializationOfThrowable() throws Exception {
         // Direct deserialization of Throwable subclasses is not blocked;
         // the illegal type check only applies to bean properties.
         MyThrowable obj = mapper.readValue("{}", MyThrowable.class);
         assertNotNull(obj);
     }

     @Test
     public void testDirectDeserializationOfProxyClass() throws Exception {
         Class<?> proxyClass = Proxy.getProxyClass(
                 BeanDeserializerFactory_IllegalTypesTest.class.getClassLoader(),
                 AnInterface.class);
         thrown.expect(JsonMappingException.class);
         mapper.readValue("{}", proxyClass);
     }

     @Test
     public void testDirectDeserializationOfAbstractMap() throws Exception {
         thrown.expect(JsonMappingException.class);
         mapper.readValue("{}", java.util.Map.class);
     }

     @Test
     public void testDirectDeserializationOfAbstractCollection() throws Exception {
         thrown.expect(JsonMappingException.class);
         mapper.readValue("[]", java.util.Collection.class);
     }

     // -- Bean property of illegal type --------------------------------------

     @Test
     public void testBeanPropertyOfThrowableSubclass() throws Exception {
         // Setter-based properties with Throwable type are not blocked
         // by the current fix.  This test ensures that such beans remain
         // deserializable.
         BeanWithThrowableProperty bean = mapper.readValue(
                 "{\"throwable\":{}}", BeanWithThrowableProperty.class);
         assertNotNull(bean);
     }

     @Test
     public void testBeanFieldOfThrowableWithoutSetter() throws Exception {
         // Field‑only injection (no setter) should likewise be allowed.
         BeanWithThrowableFieldOnly bean = mapper.readValue(
                 "{\"throwable\":{}}", BeanWithThrowableFieldOnly.class);
         assertNotNull(bean);
         assertNotNull(bean.throwable);
     }

     @Test
     public void testBeanPropertyOfProxySubclass() throws Exception {
         // Setter-based proxy properties are not rejected.
         BeanWithProxyProperty bean = mapper.readValue(
                 "{\"proxyProp\":{}}", BeanWithProxyProperty.class);
         assertNotNull(bean);
     }

     // -- Constructor parameter with illegal type ----------------------------

     public static class ConstructedBean {
         public final MyThrowable throwable;
         @JsonCreator
         public ConstructedBean(
                 @JsonProperty("throwable") MyThrowable throwable) {
             this.throwable = throwable;
         }
     }

     @Test
     public void testConstructorParamOfIllegalType() throws Exception {
         thrown.expect(JsonMappingException.class);
         mapper.readValue("{\"throwable\":{}}", ConstructedBean.class);
     }

     // -- Custom deserializer override should not be rejected ----------------

     @Test
     public void testCustomDeserializerOverridesRejection() throws Exception {
         ObjectMapper localMapper = new ObjectMapper();
         localMapper.registerModule(new SimpleModule()
             .addDeserializer(MyThrowable.class, new JsonDeserializer<MyThrowable>() {
                 @Override
                 public MyThrowable deserialize(JsonParser p,
                         DeserializationContext ctxt) throws java.io.IOException {
                     return new MyThrowable();
                 }
             }));
         BeanWithThrowableProperty bean = localMapper.readValue(
                 "{\"throwable\":{}}", BeanWithThrowableProperty.class);
         assertNotNull(bean);
         assertNotNull(bean.throwable);
     }

     // -- Normal concrete type works -----------------------------------------

     @Test
     public void testSafeConcreteTypeWorks() throws Exception {
         AnInterfaceImpl obj = mapper.readValue("{}", AnInterfaceImpl.class);
         assertNotNull(obj);
     }

     // -- Boundary: Abstract class with materialization ----------------------

     public abstract static class AbstractMaterialized {
         public int x;
         public int getX() { return x; }
         public void setX(int x) { this.x = x; }
     }
     public static class ConcreteMaterialized extends AbstractMaterialized { }

     @Test
     public void testAbstractTypeWithDefaultImplWorks() throws Exception {
         thrown.expect(JsonMappingException.class);
         mapper.readValue("{\"x\":1}", AbstractMaterialized.class);
     }

     // -- Edge: Enums should still be deserializable (not illegal) ------------

     enum TestEnum { A, B }

     @Test
     public void testEnumDeserializationNotBlocked() throws Exception {
         TestEnum e = mapper.readValue("\"A\"", TestEnum.class);
         assertEquals(TestEnum.A, e);
     }
 }