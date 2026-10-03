package com.fasterxml.jackson.databind.ser;

 import static org.junit.Assert.*;
 import org.junit.Test;
 import com.fasterxml.jackson.annotation.JsonValue;
 import com.fasterxml.jackson.core.JsonGenerator;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.SerializerProvider;
 import com.fasterxml.jackson.databind.module.SimpleModule;
 import com.fasterxml.jackson.databind.JsonSerializer;
 import java.io.IOException;
 import java.util.*;

 /**
  * Tests for correct override resolution when {@literal @}JsonValue is present.
  * Demonstrates bug 22 (report #848): a custom serializer should win over {@literal @}JsonValue.
  */
 public class TestJsonValueOverride {

     // --- test beans --------------------------------------------------------

     public static class StringBean {
         @JsonValue
         public String getValue() { return "value"; }
     }

     public static class NullBean {
         @JsonValue
         public String getNullValue() { return null; }
     }

     public static class IntBean {
         @JsonValue
         public int getIntValue() { return 99; }
     }

     public static class SimpleBean {
         private String name = "simple";
         public String getName() { return name; }
     }

     public static class OuterBean {
         private StringBean inner = new StringBean();
         public StringBean getInner() { return inner; }
     }

     public static class ListBean {
         @JsonValue
         public List<String> getList() { return Arrays.asList("a", "b"); }
     }

     // --- custom serializers -----------------------------------------------

     public static class CustomStringBeanSerializer extends JsonSerializer<StringBean> {
         @Override
         public void serialize(StringBean value, JsonGenerator gen, SerializerProvider provider)
                 throws IOException {
             gen.writeNumber(42);
         }
     }

     public static class CustomSimpleBeanSerializer extends JsonSerializer<SimpleBean> {
         @Override
         public void serialize(SimpleBean value, JsonGenerator gen, SerializerProvider provider)
                 throws IOException {
             gen.writeNumber(100);
         }
     }

     public static class CustomListBeanSerializer extends JsonSerializer<ListBean> {
         @Override
         public void serialize(ListBean value, JsonGenerator gen, SerializerProvider provider)
                 throws IOException {
             gen.writeStartArray();
             gen.writeString("x");
             gen.writeEndArray();
         }
     }

     // --- tests -------------------------------------------------------------

     /**
      * The core regression test for bug 22 / report #848.
      * Custom serializer must override the {@literal @}JsonValue-annotated method.
      */
     @Test
     public void testJsonValueWithCustomOverride() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         SimpleModule mod = new SimpleModule();
         mod.addSerializer(StringBean.class, new CustomStringBeanSerializer());
         mapper.registerModule(mod);

         String result = mapper.writeValueAsString(new StringBean());
         assertEquals("42", result);
     }

     @Test
     public void testJsonValueWithoutCustomSerializer() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         String result = mapper.writeValueAsString(new StringBean());
         assertEquals("\"value\"", result);
     }

     @Test
     public void testJsonValueNullReturn() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         String result = mapper.writeValueAsString(new NullBean());
         assertEquals("null", result);
     }

     @Test
     public void testJsonValueNumberReturn() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         String result = mapper.writeValueAsString(new IntBean());
         assertEquals("99", result);
     }

     @Test
     public void testCustomSerializerWithoutJsonValue() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         SimpleModule mod = new SimpleModule();
         mod.addSerializer(SimpleBean.class, new CustomSimpleBeanSerializer());
         mapper.registerModule(mod);

         String result = mapper.writeValueAsString(new SimpleBean());
         assertEquals("100", result);
     }

     @Test
     public void testNormalSerializationWithoutJsonValue() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         String result = mapper.writeValueAsString(new SimpleBean());
         assertEquals("{\"name\":\"simple\"}", result);
     }

     @Test
     public void testNestedBeanWithJsonValueAndCustomOverride() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         SimpleModule mod = new SimpleModule();
         mod.addSerializer(StringBean.class, new CustomStringBeanSerializer());
         mapper.registerModule(mod);

         OuterBean outer = new OuterBean();
         String result = mapper.writeValueAsString(outer);
         assertEquals("{\"inner\":42}", result);
     }

     @Test
     public void testListJsonValueWithCustomOverride() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         SimpleModule mod = new SimpleModule();
         mod.addSerializer(ListBean.class, new CustomListBeanSerializer());
         mapper.registerModule(mod);

         String result = mapper.writeValueAsString(new ListBean());
         assertEquals("[\"x\"]", result);
     }

     @Test
     public void testListJsonValueWithoutCustomSerializer() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         String result = mapper.writeValueAsString(new ListBean());
         assertEquals("[\"a\",\"b\"]", result);
     }
 }
