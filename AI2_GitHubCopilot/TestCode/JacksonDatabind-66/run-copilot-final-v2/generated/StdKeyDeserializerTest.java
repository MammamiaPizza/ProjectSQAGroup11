package com.fasterxml.jackson.databind.deser.std;

 import static org.junit.Assert.*;

 import java.util.Map;

 import org.junit.Test;

 import com.fasterxml.jackson.annotation.JsonSubTypes;
 import com.fasterxml.jackson.annotation.JsonTypeInfo;
 import com.fasterxml.jackson.annotation.JsonValue;
 import com.fasterxml.jackson.databind.JavaType;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.exc.InvalidFormatException;

 public class StdKeyDeserializerTest {

     // ---- plain enum -------------------------------------------------------

     enum SimpleEnum {
         A, B, C
     }

     @Test
     public void testSimpleEnumDeserialization() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         JavaType mapType = mapper.getTypeFactory()
                 .constructMapType(Map.class, SimpleEnum.class, String.class);
         Map<SimpleEnum, String> result = mapper.readValue("{\"A\":\"first\", \"B\":\"second\"}",
mapType);
         assertEquals("first", result.get(SimpleEnum.A));
         assertEquals("second", result.get(SimpleEnum.B));
         assertEquals(2, result.size());
     }

     // ---- polymorphic enum: without explicit type-id in key ------------------

     @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property =
"type")
     @JsonSubTypes(@JsonSubTypes.Type(value = SubEnum.class, name = "sub"))
     interface SuperTypeEnum {}

     enum SubEnum implements SuperTypeEnum {
         FOO, BAR
     }

     /**
      * Key without type-id should still resolve when a unique constant name exists across subtypes.
      */
     @Test
     public void testPolymorphicEnumDeserializationWithoutTypeId() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         JavaType mapType = mapper.getTypeFactory()
                 .constructMapType(Map.class, SuperTypeEnum.class, String.class);
         Map<SuperTypeEnum, String> result = mapper.readValue("{\"FOO\":\"val1\"}", mapType);
         assertEquals("val1", result.get(SubEnum.FOO));
         assertEquals(1, result.size());
     }

     // ---- polymorphic enum: key contains type-id (WRAPPER_ARRAY) -------------

     @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_ARRAY)
     @JsonSubTypes(@JsonSubTypes.Type(value = ArraySubEnum.class, name = "arrsub"))
     interface ArraySuperEnum {}

     enum ArraySubEnum implements ArraySuperEnum {
         FOO, BAR
     }

     /**
      * Key format: {@code ["arrsub","FOO"]}.
      */
     @Test
     public void testPolymorphicEnumDeserializationWithTypeId() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         JavaType mapType = mapper.getTypeFactory()
                 .constructMapType(Map.class, ArraySuperEnum.class, String.class);
         Map<ArraySuperEnum, String> result =
mapper.readValue("{\"[\"arrsub\",\"FOO\"]\":\"val2\"}", mapType);
         assertEquals("val2", result.get(ArraySubEnum.FOO));
         assertEquals(1, result.size());
     }

     @Test
     public void testPolymorphicEnumDeserializationUnknownKey() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         JavaType mapType = mapper.getTypeFactory()
                 .constructMapType(Map.class, SuperTypeEnum.class, String.class);
         try {
             mapper.readValue("{\"UNKNOWN\":\"val\"}", mapType);
             fail("Expected InvalidFormatException");
         } catch (InvalidFormatException e) {
             // expected
         }
     }

     // ---- ambiguous constant name across multiple subtypes --------------------

     @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property =
"type")
     @JsonSubTypes({
             @JsonSubTypes.Type(value = Sub1.class, name = "s1"),
             @JsonSubTypes.Type(value = Sub2.class, name = "s2")
     })
     interface AmbiguousSuper {}

     enum Sub1 implements AmbiguousSuper { SAME, ONE }
     enum Sub2 implements AmbiguousSuper { SAME, TWO }

     @Test
     public void testAmbiguousConstantNameShouldFail() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         JavaType mapType = mapper.getTypeFactory()
                 .constructMapType(Map.class, AmbiguousSuper.class, String.class);
         try {
             mapper.readValue("{\"SAME\":\"val\"}", mapType);
             fail("Expected InvalidFormatException or ambiguous resolution error");
         } catch (InvalidFormatException e) {
             // expected – cannot determine which subtype
         }
     }

     // ---- @JsonValue custom representation -----------------------------------

     enum ValueEnum {
         ONE(1), TWO(2);
         @JsonValue
         public final int code;
         ValueEnum(int code) { this.code = code; }
     }

     @Test
     public void testEnumKeyWithJsonValue() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         JavaType mapType = mapper.getTypeFactory()
                 .constructMapType(Map.class, ValueEnum.class, String.class);
         Map<ValueEnum, String> result = mapper.readValue("{\"1\":\"one\"}", mapType);
         assertEquals("one", result.get(ValueEnum.ONE));
         assertEquals(1, result.size());
     }

     // ---- null / empty key ---------------------------------------------------

     @Test
     public void testNullKey() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         JavaType mapType = mapper.getTypeFactory()
                 .constructMapType(Map.class, SimpleEnum.class, String.class);
         // According to StdKeyDeserializer.deserializeKey: null key returns null.
         // We cannot directly pass a null key through JSON; test via direct call.
         StdKeyDeserializer deser = (StdKeyDeserializer)
StdKeyDeserializer.forType(SimpleEnum.class);
         Object result = deser.deserializeKey(null, null);
         assertNull(result);
     }

     /**
      * Empty string is not a valid enum constant – expects InvalidFormatException.
      */
     @Test
     public void testEmptyKey() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         JavaType mapType = mapper.getTypeFactory()
                 .constructMapType(Map.class, SimpleEnum.class, String.class);
         try {
             mapper.readValue("{\"\":\"val\"}", mapType);
             fail("Expected InvalidFormatException");
         } catch (InvalidFormatException e) {
             // expected
         }
     }

     // ---- forType returns a usable deserializer ---------------------------------

     @Test
     public void testForTypeReturnsDeserializerForEnum() {
         StdKeyDeserializer deser = StdKeyDeserializer.forType(SimpleEnum.class);
         assertNotNull(deser);
         // internal kind for enum is -1 (EnumKD)
         assertTrue(deser instanceof StdKeyDeserializer.EnumKD);
     }
 }
