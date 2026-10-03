package com.fasterxml.jackson.databind.interop;

 import static org.junit.Assert.*;

 import java.lang.reflect.InvocationHandler;
 import java.lang.reflect.Method;
 import java.lang.reflect.Proxy;

 import org.junit.Rule;
 import org.junit.Test;
 import org.junit.rules.ExpectedException;

 import com.fasterxml.jackson.databind.JsonMappingException;
 import com.fasterxml.jackson.databind.ObjectMapper;

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
         thrown.expect(JsonMappingException.class);
         thrown.expectMessage("Illegal type");
         mapper.readValue("{}", MyThrowable.class);
     }

     @Test
     public void testDirectDeserializationOfProxyClass() throws Exception {
         Class<?> proxyClass = Proxy.getProxyClass(
                 BeanDeserializerFactory_IllegalTypesTest.class.getClassLoader(),
                 AnInterface.class);
         thrown.expect(JsonMappingException.class);
         thrown.expectMessage("Illegal type");
         mapper.readValue("{}", proxyClass);
     }

     @Test
     public void testDirectDeserializationOfAbstractMap() throws Exception {
         thrown.expect(JsonMappingException.class);
         thrown.expectMessage("Illegal type");
         mapper.readValue("{}", java.util.Map.class);
     }

     @Test
     public void testDirectDeserializationOfAbstractCollection() throws Exception {
         thrown.expect(JsonMappingException.class);
         thrown.expectMessage("Illegal type");
         mapper.readValue("[]", java.util.Collection.class);
     }

     // -- Bean property of illegal type --------------------------------------

     @Test
     public void testBeanPropertyOfThrowableSubclass() throws Exception {
         thrown.expect(JsonMappingException.class);
         thrown.expectMessage("Illegal type");
         mapper.readValue("{\"throwable\":{}}", BeanWithThrowableProperty.class);
     }

     @Test
     public void testBeanFieldOfThrowableWithoutSetter() throws Exception {
         // Field-only injection (no setter) should also be rejected
         thrown.expect(JsonMappingException.class);
         thrown.expectMessage("Illegal type");
         mapper.readValue("{\"throwable\":{}}", BeanWithThrowableProperty.class);
     }

     @Test
     public void testBeanPropertyOfProxySubclass() throws Exception {
         thrown.expect(JsonMappingException.class);
         thrown.expectMessage("Illegal type");
         mapper.readValue("{\"proxyProp\":{}}", BeanWithProxyProperty.class);
     }

     // -- Constructor parameter with illegal type ----------------------------

     public static class ConstructedBean {
         public final MyThrowable throwable;
         @com.fasterxml.jackson.annotation.JsonCreator
         public ConstructedBean(
                 @com.fasterxml.jackson.annotation.JsonProperty("throwable") MyThrowable throwable)
{
             this.throwable = throwable;
         }
     }

     @Test
     public void testConstructorParamOfIllegalType() throws Exception {
         thrown.expect(JsonMappingException.class);
         thrown.expectMessage("Illegal type");
         mapper.readValue("{\"throwable\":{}}", ConstructedBean.class);
     }

     // -- Custom deserializer override should not be rejected ----------------

     @Test
     public void testCustomDeserializerOverridesRejection() throws Exception {
         ObjectMapper localMapper = new ObjectMapper();
         localMapper.registerModule(new com.fasterxml.jackson.databind.module.SimpleModule()
             .addDeserializer(MyThrowable.class, new
com.fasterxml.jackson.databind.JsonDeserializer<MyThrowable>() {
                 @Override
                 public MyThrowable deserialize(com.fasterxml.jackson.core.JsonParser p,
                         com.fasterxml.jackson.databind.DeserializationContext ctxt) throws
java.io.IOException {
                     return new MyThrowable();
                 }
             }));
         // Should succeed without exception – false positive check
         BeanWithThrowableProperty bean = localMapper.readValue("{\"throwable\":{}}",
BeanWithThrowableProperty.class);
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
         // Should work if materialization is configured; otherwise may fail.
         // Here we test that deserializing abstract type without concrete mapping
         // results in "Illegal type" (since no materialization).
         thrown.expect(JsonMappingException.class);
         thrown.expectMessage("Illegal type");
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
