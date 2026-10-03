package com.fasterxml.jackson.databind.deser;

 import static org.junit.Assert.*;

 import java.lang.reflect.*;
 import java.util.*;

 import org.junit.Test;

 import com.fasterxml.jackson.databind.*;
 import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
 import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;

 public class BeanDeserializerFactoryTest {

     public static class SimpleBean {
         public String name;
         public int value;
     }

     public static class MyException extends Exception {
         private String message;
         public MyException() { }
         public MyException(String msg) { super(msg); }
         public void setMessage(String msg) { this.message = msg; }
     }

     private final ObjectMapper mapper = new ObjectMapper();

     @Test
     public void testValidPojoDeserialization() throws Exception {
         String json = "{\"name\":\"test\",\"value\":42}";
         SimpleBean bean = mapper.readValue(json, SimpleBean.class);
         assertNotNull(bean);
         assertEquals("test", bean.name);
         assertEquals(42, bean.value);
     }

     @Test
     public void testProxyClassDeserializationFails() throws Throwable {
         Class<?> proxyClass = Proxy.newProxyInstance(
                 BeanDeserializerFactoryTest.class.getClassLoader(),
                 new Class<?>[]{Runnable.class},
                 new InvocationHandler() {
                     public Object invoke(Object proxy, Method method, Object[] args) throws
Throwable {
                         return null;
                     }
                 }).getClass();
         try {
             mapper.readValue("{}", proxyClass);
             fail("Should have thrown an exception for Proxy class");
         } catch (IllegalArgumentException e) {
             assertTrue(e.getMessage().contains("Can not deserialize Proxy class"));
         } catch (Exception e) {
             Throwable cause = e.getCause();
             if (cause instancef IllegalArgumentException) {
                 assertTrue(cause.getMessage().contains("Can not deserialize Proxy class"));
             } else {
                 fail("Unexpected exception type: " + e);
             }
         }
     }

     @Test
     public void testIlegalClassNamesDeserializationFails() throws Exception {
         Field field =
BeanDeserializerFactory.class.getDeclaredField("DEFAULT_NO_DESER_CLASS_NAMES");
         field.setAccessible(true);
         @SuppressWarnings("unchecked")
         Set<String> illegalNames = (Set<String>) field.get(null);
         assertFalse("Ilegal class names set must not be empty", illegalNames.isEmpty());

         for (String className : illegalNames) {
             Class<?> clazz;
             try {
                 clazz = Class.forName(className);
             } catch (ClassNotFoundException e) {
                 // skip classes not available at runtime
                 continue;
             }
             try {
                 mapper.readValue("{}", clazz);
                 fail("Should have thrown an exception for illegal class: " + className);
             } catch (JsonMappingException e) {
                 // expected
             } catch (Exception e) {
                 // Some classes may cause other runtime errors before deserialization is attempted,
                 // still acceptable as long as deserialization does not succeed.
                 // We simply assert that the read did not succeed.
             }
         }
     }

     @Test
     public void testThrowableSubclassDeserializationFails() {
         try {
             mapper.readValue("{\"message\":\"error\"}", MyException.class);
             fail("Should have thrown JsonMappingException for Throwable subclass");
         } catch (JsonMappingException e) {
             // expected
         } catch (Exception e) {
             fail("Wrong exception type: " + e);
         }
     }

     @Test
     public void testThrowableDeserializationFails() {
         try {
             mapper.readValue("{}", Throwable.class);
             fail("Should have thrown JsonMappingException for Throwable");
         } catch (JsonMappingException e) {
             // expected
         } catch (Exception e) {
             fail("Wrong exception type: " + e);
         }
     }

     @Test
     public void testFactoryWithConfigStillRejectsProxy() throws Exception {
         BeanDeserializerFactory factory = new BeanDeserializerFactory(new
DeserializerFactoryConfig());
         BeanDeserializerFactory newFactory = (BeanDeserializerFactory) factory.withConfig(
                 new DeserializerFactoryConfig());
         Method m = BeanDeserializerFactory.class.getDeclaredMethod("isPotentialBeanType",
Class.class);
         m.setAccessible(true);
         Class<?> proxyClass = Proxy.newProxyInstance(
                 getClass().getClassLoader(),
                 new Class<?>[]{Runnable.class},
                 new InvocationHandler() {
                     public Object invoke(Object proxy, Method method, Object[] args) throws
Throwable {
                         return null;
                     }
                 }).getClass();
         try {
             m.invoke(newFactory, proxyClass);
             fail("Expected IllegalArgumentException");
         } catch (InvocationTargetException e) {
             assertTrue(e.getCause() instanceof IllegalArgumentException);
             assertTrue(e.getCause().getMessage().contains("Can not deserialize Proxy class"));
         }
     }

     @Test
     public void testFactoryWithConfigStillRejectsIllegalNames() throws Exception {
         Field defaultField =
BeanDeserializerFactory.class.getDeclaredField("DEFAULT_NO_DESER_CLASS_NAMES");
         defaultField.setAccessible(true);
         @SuppressWarnings("unchecked")
         Set<String> defaultNames = (Set<String>) defaultField.get(null);

         BeanDeserializerFactory factory = new BeanDeserializerFactory(new
DeserializerFactoryConfig());
         BeanDeserializerFactory newFactory = (BeanDeserializerFactory) factory.withConfig(
                 new DeserializerFactoryConfig());
         Field cfgField = BeanDeserializerFactory.class.getDeclaredField("_cfgIllegalClassNames");
         cfgField.setAccessible(true);
         @SuppressWarnings("unchecked")
         Set<String> cfgNames = (Set<String>) cfgField.get(newFactory);

         assertNotNull("_cfgIllegalClassNames must not be null", cfgNames);
         for (String name : defaultNames) {
             assertTrue("_cfgIllegalClassNames should contain: " + name, cfgNames.contains(name));
         }
     }

     @Test
     public void testIsPotentialBeanTypeWithNormalBean() throws Exception {
         BeanDeserializerFactory factory = new BeanDeserializerFactory(new
DeserializerFactoryConfig());
         Method m = BeanDeserializerFactory.class.getDeclaredMethod("isPotentialBeanType",
Class.class);
         m.setAccessible(true);
         Boolean result = (Boolean) m.invoke(factory, SimpleBean.class);
         assertTrue("Normal bean should be a potential bean type", result);
     }

     @Test
     public void testIsPotentialBeanTypeWithProxyThrowsException() throws Exception {
         BeanDeserializerFactory factory = new BeanDeserializerFactory(new
DeserializerFactoryConfig());
         Method m = BeanDeserializerFactory.class.getDeclaredMethod("isPotentialBeanType",
Class.class);
         m.setAccessible(true);
         Class<?> proxyClass = Proxy.newProxyInstance(
                 getClass().getClassLoader(),
                 new Class<?>[]{Runnable.class},
                 new InvocationHandler() {
                     public Object invoke(Object proxy, Method method, Object[] args) throws
Throwable {
                         return null;
                     }
                 }).getClass();
         try {
             m.invoke(factory, proxyClass);
             fail("Should have thrown IllegalArgumentException");
         } catch (InvocationTargetException e) {
             assertTrue(e.getCause() instanceof IllegalArgumentException);
             assertTrue(e.getCause().getMessage().contains("Can not deserialize Proxy class"));
         }
     }

     @Test
     public void testNonThrowableBeanSucceeds() throws Exception {
         SimpleBean bean = mapper.readValue("{\"name\":\"ok\"}", SimpleBean.class);
         assertNotNull(bean);
         assertEquals("ok", bean.name);
     }

     @Test
     public void testMapDeserializationNotAffected() throws Exception {
         String json = "{\"key\":\"value\"}";
         Map<?, ?> map = mapper.readValue(json, Map.class);
         assertNotNull(map);
         assertEquals("value", map.get("key"));
     }

     @Test
     public void testCollectionDeserializationNotAffected() throws Exception {
         String json = "[\"a\",\"b\"]";
         List<?> list = mapper.readValue(json, List.class);
         assertNotNull(list);
         assertEquals(2, list.size());
     }
 }
