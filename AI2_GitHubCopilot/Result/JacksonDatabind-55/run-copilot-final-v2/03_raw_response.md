package com.fasterxml.jackson.databind.ser.std;

 import static org.junit.Assert.*;
 import org.junit.Test;

 import java.io.StringWriter;
 import java.util.*;

 import com.fasterxml.jackson.annotation.JsonProperty;
 import com.fasterxml.jackson.annotation.JsonValue;
 import com.fasterxml.jackson.core.JsonFactory;
 import com.fasterxml.jackson.core.JsonGenerator;
 import com.fasterxml.jackson.databind.*;
 import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;
 import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Dynamic;

 public class StdKeySerializersTest {

     enum AlephEnum {
         @JsonProperty("aleph") A,
         @JsonProperty("bet") B;
     }

     enum PlainEnum { ALPHA, BETA; }

     enum JsonValueEnum {
         X("x-val");
         private final String v;
         JsonValueEnum(String v) { this.v = v; }
         @JsonValue public String getV() { return v; }
     }

     enum EmptyPropEnum {
         @JsonProperty("") EMPTY,
         NORMAL;
     }

     static class Nested {
         enum NestedEnum {
             @JsonProperty("nested-val") NESTED;
         }
     }

     enum MixedEnum {
         @JsonProperty("annotated") ANNOTATED,
         PLAIN;
     }

     @Test
     public void testEnumKeyWithJsonProperty() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         Map<AlephEnum, String> map = new HashMap<>();
         map.put(AlephEnum.A, "b");
         String json = mapper.writeValueAsString(map);
         assertTrue("Key should use @JsonProperty value 'aleph': " + json,
                 json.contains("\"aleph\":"));
         assertFalse("Key should NOT use enum name 'A': " + json,
                 json.contains("\"A\":"));
     }

     @Test
     public void testEnumKeyWithoutAnnotation() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         Map<PlainEnum, Integer> map = new HashMap<>();
         map.put(PlainEnum.ALPHA, 1);
         String json = mapper.writeValueAsString(map);
         assertTrue("Unannotated enum key should use name(): " + json,
                 json.contains("\"ALPHA\":"));
     }

     @Test
     public void testMultipleEnumKeysWithJsonProperty() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         Map<AlephEnum, Integer> map = new LinkedHashMap<>();
         map.put(AlephEnum.A, 1);
         map.put(AlephEnum.B, 2);
         String json = mapper.writeValueAsString(map);
         assertTrue(json.contains("\"aleph\":"));
         assertTrue(json.contains("\"bet\":"));
         assertFalse(json.contains("\"B\":"));
     }

     @Test
     public void testMixedAnnotationEnumKeys() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         Map<MixedEnum, String> map = new LinkedHashMap<>();
         map.put(MixedEnum.ANNOTATED, "a");
         map.put(MixedEnum.PLAIN, "p");
         String json = mapper.writeValueAsString(map);
         assertTrue(json.contains("\"annotated\":"));
         assertTrue(json.contains("\"PLAIN\":"));
     }

     @Test
     public void testEmptyJsonPropertyEnumKey() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         Map<EmptyPropEnum, String> map = new HashMap<>();
         map.put(EmptyPropEnum.EMPTY, "e");
         String json = mapper.writeValueAsString(map);
         assertTrue("Empty @JsonProperty should produce empty string key: " + json,
                 json.contains("\"\":"));
     }

     @Test
     public void testNestedEnumKeyWithJsonProperty() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         Map<Nested.NestedEnum, String> map = new HashMap<>();
         map.put(Nested.NestedEnum.NESTED, "n");
         String json = mapper.writeValueAsString(map);
         assertTrue("Nested enum key should use @JsonProperty: " + json,
                 json.contains("\"nested-val\":"));
     }

     @Test
     public void testEnumMapWithJsonProperty() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         EnumMap<AlephEnum, String> emap = new EnumMap<>(AlephEnum.class);
         emap.put(AlephEnum.A, "v");
         String json = mapper.writeValueAsString(emap);
         assertTrue("EnumMap key should use @JsonProperty value: " + json,
                 json.contains("\"aleph\":"));
     }

     @Test
     public void testDefaultSerializerTypeEnum() throws Exception {
         Default serializer = new Default(4, AlephEnum.class);
         StringWriter sw = new StringWriter();
         ObjectMapper mapper = new ObjectMapper();
         SerializerProvider prov = mapper.getSerializerProvider();
         JsonGenerator gen = new JsonFactory().createGenerator(sw);
         gen.writeStartObject();
         serializer.serialize(AlephEnum.A, gen, prov);
         gen.writeString("x");
         gen.writeEndObject();
         gen.close();
         String result = sw.toString();
         assertTrue("Default TYPE_ENUM serialize should use @JsonProperty: " + result,
                 result.contains("\"aleph\":"));
         assertFalse("Default TYPE_ENUM should NOT use name(): " + result,
                 result.contains("\"A\":"));
     }

     @Test
     public void testFallbackSerializerForEnumClass() throws Exception {
         SerializationConfig cfg = new ObjectMapper().getSerializationConfig();
         JsonSerializer<Object> ser = StdKeySerializers.getFallbackKeySerializer(cfg,
AlephEnum.class);
         assertNotNull("Should return non-null for enum type", ser);
         assertTrue("Should be Default instance", ser instanceof Default);
         assertEquals("Should have TYPE_ENUM typeId", 4, ((Default) ser)._typeId);
     }

     @Test
     public void testStdKeySerializerSkipsEnum() throws Exception {
         SerializationConfig cfg = new ObjectMapper().getSerializationConfig();
         JsonSerializer<Object> ser = StdKeySerializers.getStdKeySerializer(cfg, AlephEnum.class,
false);
         assertNull("getStdKeySerializer should not handle enum types", ser);
     }

     @Test
     public void testFallbackForRawEnumClassReturnsDynamic() throws Exception {
         SerializationConfig cfg = new ObjectMapper().getSerializationConfig();
         JsonSerializer<Object> ser = StdKeySerializers.getFallbackKeySerializer(cfg, Enum.class);
         assertNotNull("Should return non-null for Enum.class", ser);
         assertTrue("Should be Dynamic instance for raw Enum.class", ser instanceof Dynamic);
     }

     @Test
     public void testEnumKeyWithJsonValue() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         Map<JsonValueEnum, String> map = new HashMap<>();
         map.put(JsonValueEnum.X, "test");
         String json = mapper.writeValueAsString(map);
         assertTrue("Key should use @JsonValue value 'x-val': " + json,
                 json.contains("\"x-val\":"));
     }
 }