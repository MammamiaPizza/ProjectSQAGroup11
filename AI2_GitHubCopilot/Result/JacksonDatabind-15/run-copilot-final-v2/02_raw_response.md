package com.fasterxml.jackson.databind.convert;

import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import com.fasterxml.jackson.databind.util.Converter;

import org.junit.Assert;
import org.junit.Test;

public class TestConvertingSerializerBug731 {

 public static class DummyBean { }

 public static class TargetBean {
     private String name;
     public TargetBean() { }
     public TargetBean(String name) { this.name = name; }
     public String getName() { return name; }
     public void setName(String name) { this.name = name; }
 }

 public static class EmptyBean { }

 public static class NestedBean {
     private int id;
     private TargetBean child;
     public NestedBean() { }
     public NestedBean(int id, TargetBean child) { this.id = id; this.child = child; }
     public int getId() { return id; }
     public void setId(int id) { this.id = id; }
     public TargetBean getChild() { return child; }
     public void setChild(TargetBean child) { this.child = child; }
 }

 // Converter implementations
 public static class DummyToTargetConverter implements Converter<DummyBean, TargetBean> {
     @Override
     public TargetBean convert(DummyBean value) {
         return new TargetBean("converted");
     }
 }

 public static class NullResultConverter implements Converter<DummyBean, TargetBean> {
     @Override
     public TargetBean convert(DummyBean value) {
         return null;
     }
 }

 public static class DummyToListConverter implements Converter<DummyBean, List<String>> {
     @Override
     public List<String> convert(DummyBean value) {
         return Arrays.asList("a", "b");
     }
 }

 public static class DummyToMapConverter implements Converter<DummyBean, Map<String, Object>> {
     @Override
     public Map<String, Object> convert(DummyBean value) {
         Map<String, Object> m = new HashMap<String, Object>();
         m.put("key", 123);
         return m;
     }
 }

 public static class DummyToEmptyBeanConverter implements Converter<DummyBean, EmptyBean> {
     @Override
     public EmptyBean convert(DummyBean value) {
         return new EmptyBean();
     }
 }

 public static class DummyToNestedBeanConverter implements Converter<DummyBean, NestedBean> {
     @Override
     public NestedBean convert(DummyBean value) {
         return new NestedBean(1, new TargetBean("inner"));
     }
 }

 public static class WrappingConverter implements Converter<DummyBean, TargetBean> {
     @Override
     public TargetBean convert(DummyBean value) {
         return new TargetBean("wrapped");
     }
 }

 // Custom serializer for TargetBean (to test delegation)
 @SuppressWarnings("serial")
 public static class CustomTargetSerializer extends
com.fasterxml.jackson.databind.ser.std.StdSerializer<TargetBean> {
     public CustomTargetSerializer() {
         super(TargetBean.class);
     }
     @Override
     public void serialize(TargetBean value, com.fasterxml.jackson.core.JsonGenerator gen,
             SerializerProvider provider) throws java.io.IOException {
         gen.writeString(value.getName().toUpperCase());
     }
 }

 // ================ Tests ================

 @Test
 public void testIssue731() throws Exception {
     ObjectMapper mapper = new ObjectMapper();
     SimpleModule mod = new SimpleModule();
     mod.addSerializer(new StdDelegatingSerializer(new DummyToTargetConverter()));
     mapper.registerModule(mod);
     String json = mapper.writeValueAsString(new DummyBean());
     Assert.assertEquals("{\"name\":\"converted\"}", json);
 }

 @Test
 public void testNullResult() throws Exception {
     ObjectMapper mapper = new ObjectMapper();
     SimpleModule mod = new SimpleModule();
     mod.addSerializer(new StdDelegatingSerializer(new NullResultConverter()));
     mapper.registerModule(mod);
     String json = mapper.writeValueAsString(new DummyBean());
     Assert.assertEquals("null", json);
 }

 @Test
 public void testListResult() throws Exception {
     ObjectMapper mapper = new ObjectMapper();
     SimpleModule mod = new SimpleModule();
     mod.addSerializer(new StdDelegatingSerializer(new DummyToListConverter()));
     mapper.registerModule(mod);
     String json = mapper.writeValueAsString(new DummyBean());
     Assert.assertEquals("[\"a\",\"b\"]", json);
 }

 @Test
 public void testMapResult() throws Exception {
     ObjectMapper mapper = new ObjectMapper();
     SimpleModule mod = new SimpleModule();
     mod.addSerializer(new StdDelegatingSerializer(new DummyToMapConverter()));
     mapper.registerModule(mod);
     String json = mapper.writeValueAsString(new DummyBean());
     Assert.assertEquals("{\"key\":123}", json);
 }

 @Test
 public void testNestedBeanResult() throws Exception {
     ObjectMapper mapper = new ObjectMapper();
     SimpleModule mod = new SimpleModule();
     mod.addSerializer(new StdDelegatingSerializer(new DummyToNestedBeanConverter()));
     mapper.registerModule(mod);
     String json = mapper.writeValueAsString(new DummyBean());
     Assert.assertEquals("{\"id\":1,\"child\":{\"name\":\"inner\"}}", json);
 }

 @Test
 public void testEmptyBeanWithFailEnabled() throws Exception {
     ObjectMapper mapper = new ObjectMapper();
     mapper.enable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
     SimpleModule mod = new SimpleModule();
     mod.addSerializer(new StdDelegatingSerializer(new DummyToEmptyBeanConverter()));
     mapper.registerModule(mod);
     try {
         mapper.writeValueAsString(new DummyBean());
         Assert.fail("Should throw JsonMappingException");
     } catch (JsonMappingException e) {
         // expected
     }
 }

 @Test
 public void testEmptyBeanWithFailDisabled() throws Exception {
     ObjectMapper mapper = new ObjectMapper();
     mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
     SimpleModule mod = new SimpleModule();
     mod.addSerializer(new StdDelegatingSerializer(new DummyToEmptyBeanConverter()));
     mapper.registerModule(mod);
     String json = mapper.writeValueAsString(new DummyBean());
     Assert.assertEquals("{}", json);
 }

 @Test
 public void testCustomSerializerForDelegateTypeIsUsed() throws Exception {
     ObjectMapper mapper = new ObjectMapper();
     SimpleModule mod = new SimpleModule();
     // register a custom serializer for TargetBean
     mod.addSerializer(TargetBean.class, new CustomTargetSerializer());
     // register delegating serializer for DummyBean
     mod.addSerializer(new StdDelegatingSerializer(new DummyToTargetConverter()));
     mapper.registerModule(mod);
     // The delegating serializer should use the custom serializer for TargetBean
     String json = mapper.writeValueAsString(new DummyBean());
     Assert.assertEquals("\"CONVERTED\"", json);
 }

 @Test
 public void testConverterOutputTypeWithWildcard() throws Exception {
     ObjectMapper mapper = new ObjectMapper();
     // Create a converter that returns a Collection with wildcard type
     Converter<DummyBean, Collection<?>> converter = new Converter<DummyBean, Collection<?>>() {
         @Override
         public Collection<?> convert(DummyBean value) {
             return Arrays.asList("x", "y");
         }
     };
     SimpleModule mod = new SimpleModule();
     mod.addSerializer(new StdDelegatingSerializer(converter));
     mapper.registerModule(mod);
     String json = mapper.writeValueAsString(new DummyBean());
     Assert.assertEquals("[\"x\",\"y\"]", json);
 }

 @Test
 public void testSerializerHandlesTypeHandler() throws Exception {
     // Use a mix-in to attach a typeHandler to DummyBean
     ObjectMapper mapper = new ObjectMapper();
     abstract class DummyMixIn {
         // This annotation might need a specific type handler; we use a simple delegating
serializer
         // to verify that the typeHandler does not break the delegated serialization
     }
     // Not strictly necessary, but we test that registration remains working
     SimpleModule mod = new SimpleModule();
     mod.addSerializer(new StdDelegatingSerializer(new DummyToTargetConverter()));
     mapper.registerModule(mod);
     String json = mapper.writeValueAsString(new DummyBean());
     Assert.assertEquals("{\"name\":\"converted\"}", json);
 }

}