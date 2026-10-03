package com.fasterxml.jackson.databind.ser.std;

 import static org.junit.Assert.*;

 import java.util.Date;

 import org.junit.Before;
 import org.junit.Test;

 import com.fasterxml.jackson.annotation.JsonSubTypes;
 import com.fasterxml.jackson.annotation.JsonTypeInfo;
 import com.fasterxml.jackson.annotation.JsonValue;
 import com.fasterxml.jackson.databind.MapperFeature;
 import com.fasterxml.jackson.databind.ObjectMapper;

 public class TestJsonValueSerializerBug1385 {

     private ObjectMapper mapper;

     @Before
     public void setUp() {
         mapper = new ObjectMapper();
     }

     /**
      * Bug 1385: A bean with @JsonValue returning byte[]. Default typing must write
      * the bean type as type id, not {@code byte[]} ({@code [B}).
      */
     @Test
     public void testByteArrayJsonValueWithDefaultTyping() throws Exception {
         mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
         BeanWithByteArrayJsonValue bean = new BeanWithByteArrayJsonValue(new byte[]{1, 2, 3});
         String json = mapper.writeValueAsString(bean);

         assertTrue("Type id should be the bean class",
                 json.contains("\"" + BeanWithByteArrayJsonValue.class.getName() + "\""));
         assertFalse("Type id must not be 'byte[]' / [B",
                 json.contains("\"[B\""));
     }

     /**
      * Bug 1385: External type id for a property referencing a bean with @JsonValue
      * must use the bean's logical type name, not the delegate value type.
      */
     @Test
     public void testExternalTypeIdWithJsonValueDate() throws Exception {
         Wrapper wrapper = new Wrapper(new Thingy(12345L));
         String json = mapper.writeValueAsString(wrapper);

         assertTrue("Type id should be 'thingy'", json.contains("\"type\":\"thingy\""));
         assertFalse("Type id must not be 'date'", json.contains("\"type\":\"date\""));
     }

     /**
      * Without any type info, only the delegate value (not the bean) is serialized.
      */
     @Test
     public void testSerializeWithoutTypeInfo() throws Exception {
         mapper.disable(MapperFeature.USE_STATIC_TYPING);
         BeanWithByteArrayJsonValue bean = new BeanWithByteArrayJsonValue(new byte[]{1, 2, 3});
         String json = mapper.writeValueAsString(bean);

         assertFalse("No bean class when type info is disabled",
                 json.contains(BeanWithByteArrayJsonValue.class.getName()));
     }

     /**
      * Null delegate value serializes as JSON null.
      */
     @Test
     public void testNullJsonValue() throws Exception {
         BeanWithNullJsonValue bean = new BeanWithNullJsonValue();
         String json = mapper.writeValueAsString(bean);
         assertEquals("null", json);
     }

     /**
      * Enum with @JsonValue; serialization should still work as before.
      */
     @Test
     public void testEnumWithJsonValue() throws Exception {
         String json = mapper.writeValueAsString(MyEnum.FOO);
         assertEquals("\"fooValue\"", json);
     }

     /**
      * Bean with @JsonValue returning String under default typing – type id
      * must still be the bean class, not String.
      */
     @Test
     public void testStringJsonValueWithDefaultTyping() throws Exception {
         mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
         BeanWithStringJsonValue bean = new BeanWithStringJsonValue("hello");
         String json = mapper.writeValueAsString(bean);

         assertTrue("Type id must be bean class",
json.contains(BeanWithStringJsonValue.class.getName()));
     }

     /**
      * Bean with @JsonValue returning a natural type (Integer) and forced type
      * info via {@code USE_STATIC_TYPING} – still uses bean class for type id.
      */
     @Test
     public void testIntegerJsonValueWithStaticTyping() throws Exception {
         mapper.enable(MapperFeature.USE_STATIC_TYPING);
         mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
         BeanWithIntegerJsonValue bean = new BeanWithIntegerJsonValue(42);
         String json = mapper.writeValueAsString(bean);

         assertTrue("Type id must be bean class",
json.contains(BeanWithIntegerJsonValue.class.getName()));
     }

     /**
      * Bean with @JsonValue returning a non-standard type (custom POJO) – type id
      * is still the bean class.
      */
     @Test
     public void testCustomTypeJsonValueWithDefaultTyping() throws Exception {
         mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
         BeanWithCustomJsonValue bean = new BeanWithCustomJsonValue(new CustomValue(7));
         String json = mapper.writeValueAsString(bean);

         assertTrue("Type id must be bean class",
json.contains(BeanWithCustomJsonValue.class.getName()));
         assertFalse("Type id must not be CustomValue", json.contains(CustomValue.class.getName()));
     }

     // --- Test fixtures -----------------------------------------------------

     static class BeanWithByteArrayJsonValue {
         private final byte[] data;
         BeanWithByteArrayJsonValue(byte[] data) { this.data = data; }
         @JsonValue
         public byte[] getValue() { return data; }
     }

     static class BeanWithNullJsonValue {
         @JsonValue
         public String getValue() { return null; }
     }

     enum MyEnum {
         FOO("fooValue");
         private final String value;
         MyEnum(String v) { this.value = v; }
         @JsonValue
         public String getValue() { return value; }
     }

     static class BeanWithStringJsonValue {
         private final String s;
         BeanWithStringJsonValue(String s) { this.s = s; }
         @JsonValue
         public String getValue() { return s; }
     }

     static class BeanWithIntegerJsonValue {
         private final int i;
         BeanWithIntegerJsonValue(int i) { this.i = i; }
         @JsonValue
         public int getValue() { return i; }
     }

     static class CustomValue {
         public int val;
         CustomValue(int val) { this.val = val; }
     }

     static class BeanWithCustomJsonValue {
         private final CustomValue v;
         BeanWithCustomJsonValue(CustomValue v) { this.v = v; }
         @JsonValue
         public CustomValue getValue() { return v; }
     }

     // External type id fixtures
     static class Wrapper {
         @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
property = "type")
         @JsonSubTypes(@JsonSubTypes.Type(value = Thingy.class, name = "thingy"))
         public Object value;

         Wrapper(Thingy v) { this.value = v; }
     }

     static class Thingy {
         private final long timestamp;
         Thingy(long timestamp) { this.timestamp = timestamp; }
         @JsonValue
         public Date getDate() { return new Date(timestamp); }
     }
 }
