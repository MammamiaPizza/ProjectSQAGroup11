@org.junit.Test
 public void testBeanWithJsonValueAndModuleCustomSerializer() throws Exception {
     com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();
     com.fasterxml.jackson.databind.module.SimpleModule mod = new
com.fasterxml.jackson.databind.module.SimpleModule("test",
             com.fasterxml.jackson.core.Version.unknownVersion());
     mod.addSerializer(BeanWithJsonValueAndModule.class,
             new com.fasterxml.jackson.databind.JsonSerializer<BeanWithJsonValueAndModule>() {
                 @Override
                 public void serialize(BeanWithJsonValueAndModule value,
                         com.fasterxml.jackson.core.JsonGenerator gen,
                         com.fasterxml.jackson.databind.SerializerProvider serializers)
                         throws java.io.IOException {
                     gen.writeNumber(999);
                 }
             });
     mapper.registerModule(mod);
     String result = mapper.writeValueAsString(new BeanWithJsonValueAndModule());
     org.junit.Assert.assertEquals("999", result);
 }

 static class BeanWithJsonValueAndModule {
     @com.fasterxml.jackson.annotation.JsonValue
     public String toString() {
         return "original";
     }
 }

 @org.junit.Test
 public void testArrayPropertyWithCustomContentSerializer() throws Exception {
     com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();
     String result = mapper.writeValueAsString(new BeanWithArrayAndContentSerializer());
     org.junit.Assert.assertEquals("[\"custom-x\",\"custom-x\"]", result);
 }

 static class BeanWithArrayAndContentSerializer {
     @com.fasterxml.jackson.databind.annotation.JsonSerialize(contentUsing =
StringContentSerializer.class)
     public ValueItem[] items = new ValueItem[] { new ValueItem("x"), new ValueItem("x") };
 }

 static class ValueItem {
     public String value;

     public ValueItem(String v) {
         value = v;
     }

     @com.fasterxml.jackson.annotation.JsonValue
     public String getValue() {
         return value;
     }
 }

 static class StringContentSerializer extends
com.fasterxml.jackson.databind.JsonSerializer<ValueItem> {
     @Override
     public void serialize(ValueItem v, com.fasterxml.jackson.core.JsonGenerator gen,
             com.fasterxml.jackson.databind.SerializerProvider serializers) throws
java.io.IOException {
         gen.writeString("custom-" + v.value);
     }
 }

 @org.junit.Test
 public void testArraySerializerModifier() throws Exception {
     com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();
     com.fasterxml.jackson.databind.module.SimpleModule mod = new
com.fasterxml.jackson.databind.module.SimpleModule(
             "test", com.fasterxml.jackson.core.Version.unknownVersion()) {
         @Override
         public void setupModule(com.fasterxml.jackson.databind.Module.SetupContext context) {
             context.addBeanSerializerModifier(new
com.fasterxml.jackson.databind.ser.BeanSerializerModifier() {
                 @Override
                 public com.fasterxml.jackson.databind.JsonSerializer<?> modifyArraySerializer(
                         com.fasterxml.jackson.databind.SerializationConfig config,
                         com.fasterxml.jackson.databind.JavaType valueType,
                         com.fasterxml.jackson.databind.introspect.BeanDescription beanDesc,
                         com.fasterxml.jackson.databind.JsonSerializer<?> serializer) {
                     @SuppressWarnings("unchecked")
                     final com.fasterxml.jackson.databind.JsonSerializer<Object> delegate =
(com.fasterxml.jackson.databind.JsonSerializer<Object>) serializer;
                     return new com.fasterxml.jackson.databind.JsonSerializer<Object>() {
                         @Override
                         public void serialize(Object value,
com.fasterxml.jackson.core.JsonGenerator gen,
                                 com.fasterxml.jackson.databind.SerializerProvider provider) throws
java.io.IOException {
                             gen.writeStartArray();
                             gen.writeString("modified");
                             delegate.serialize(value, gen);
                             gen.writeEndArray();
                         }
                     };
                 }
             });
         }
     };
     mapper.registerModule(mod);
     String result = mapper.writeValueAsString(new ArrayBean());
     org.junit.Assert.assertTrue(result.contains("modified"));
 }

 static class ArrayBean {
     public String[] items = new String[] { "a", "b" };
 }