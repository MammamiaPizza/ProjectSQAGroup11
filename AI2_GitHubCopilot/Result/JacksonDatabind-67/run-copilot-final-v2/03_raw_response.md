package com.fasterxml.jackson.databind.deser;

  import com.fasterxml.jackson.databind.*;
  import com.fasterxml.jackson.databind.deser.*;
  import com.fasterxml.jackson.databind.exc.InvalidFormatException;
  import com.fasterxml.jackson.databind.introspect.*;
  import com.fasterxml.jackson.databind.module.*;
  import com.fasterxml.jackson.databind.type.*;

  import org.junit.Test;
  import static org.junit.Assert.*;

  import java.io.IOException;
  import java.util.*;

  /**
   * Tests related to custom key deserialization for enum map keys,
   * especially in conjunction with BeanDeserializerModifier (issue #1445).
   */
  public class TestBasicDeserializerFactoryEnumKeyCustom {

      // Enum with names as described in the bug report
      public enum KeyEnum {
          rootDirectory, replacements, licenseString;
      }

      // Case-insensitive KeyDeserializer for the test enum
      public static class CaseInsensitiveEnumKeyDeserializer extends KeyDeserializer {
          private final Class<? extends Enum<?>> enumClass;

          public CaseInsensitiveEnumKeyDeserializer(Class<? extends Enum<?>> enumClass) {
              this.enumClass = enumClass;
          }

          @Override
          public Object deserializeKey(String key, DeserializationContext ctxt)
                  throws IOException {
              for (Enum<?> e : enumClass.getEnumConstants()) {
                  if (e.name().equalsIgnoreCase(key)) {
                      return e;
                  }
              }
              throw ctxt.weirdKeyException(enumClass, key, "not a valid representation");
          }
      }

      // Implementation of KeyDeserializers that returns our custom deserializer for KeyEnum
      public static class EnumKeyDeserializers implements
 com.fasterxml.jackson.databind.deser.KeyDeserializers {
          @Override
          public KeyDeserializer findKeyDeserializer(JavaType type, DeserializationConfig config,
                                                     BeanDescription beanDesc) {
              if (type.isEnumType() && type.getRawClass() == KeyEnum.class) {
                  @SuppressWarnings("unchecked")
                  Class<? extends Enum<?>> ec = (Class<? extends Enum<?>>) type.getRawClass();
                  return new CaseInsensitiveEnumKeyDeserializer(ec);
              }
              return null;
          }
      }

      // BeanDeserializerModifier that replaces the key deserializer for KeyEnum
      public static class EnumKeyDeserializerModifier extends BeanDeserializerModifier {
          @Override
          public KeyDeserializer modifyKeyDeserializer(DeserializationConfig config,
                       JavaType valueType, KeyDeserializer defaultDeserializer) {
              if (valueType.isEnumType() && valueType.getRawClass() == KeyEnum.class) {
                  @SuppressWarnings("unchecked")
                  Class<? extends Enum<?>> ec = (Class<? extends Enum<?>>) valueType.getRawClass();
                  return new CaseInsensitiveEnumKeyDeserializer(ec);
              }
              return defaultDeserializer;
          }
      }

      // Helper: mapper with KeyDeserializers registration
      private ObjectMapper mapperWithKeyDeserializers() {
          ObjectMapper mapper = new ObjectMapper();
          SimpleModule module = new SimpleModule("enumKeyModule");
          module.addKeyDeserializers(new EnumKeyDeserializers());
          mapper.registerModule(module);
          return mapper;
      }

      // Helper: mapper with BeanDeserializerModifier registration
      private ObjectMapper mapperWithModifier() {
          ObjectMapper mapper = new ObjectMapper();
          SimpleModule module = new SimpleModule("enumKeyModifierModule");
          module.setDeserializerModifier(new EnumKeyDeserializerModifier());
          mapper.registerModule(module);
          return mapper;
      }

      // --------------------------------------------------------------
      // Tests using default behaviour (no custom deserializer)
      // --------------------------------------------------------------

      @Test
      public void testDefaultEnumKeyExactMatch() throws Exception {
          ObjectMapper mapper = new ObjectMapper();
          String json = "{\"replacements\":\"v\"}";
          Map<KeyEnum, String> map = mapper.readValue(json,
              new TypeReference<Map<KeyEnum, String>>() {});
          assertNotNull(map);
          assertTrue(map.containsKey(KeyEnum.replacements));
      }

      @Test(expected = InvalidFormatException.class)
      public void testDefaultEnumKeyCaseInsensitiveFails() throws Exception {
          ObjectMapper mapper = new ObjectMapper();
          String json = "{\"REPlaceMENTS\":\"v\"}"; // case-variant
          mapper.readValue(json, new TypeReference<Map<KeyEnum, String>>() {});
      }

      // --------------------------------------------------------------
      // Tests with custom KeyDeserializer registered via KeyDeserializers
      // --------------------------------------------------------------

      @Test
      public void testCustomViaKeyDeserializersExactMatch() throws Exception {
          ObjectMapper mapper = mapperWithKeyDeserializers();
          String json = "{\"replacements\":\"v\"}";
          Map<KeyEnum, String> map = mapper.readValue(json,
              new TypeReference<Map<KeyEnum, String>>() {});
          assertNotNull(map);
          assertEquals(KeyEnum.replacements, map.keySet().iterator().next());
      }

      @Test
      public void testCustomViaKeyDeserializersCaseInsensitive() throws Exception {
          ObjectMapper mapper = mapperWithKeyDeserializers();
          String json = "{\"REPlaceMENTS\":\"v\"}";
          Map<KeyEnum, String> map = mapper.readValue(json,
              new TypeReference<Map<KeyEnum, String>>() {});
          assertNotNull(map);
          assertEquals(KeyEnum.replacements, map.keySet().iterator().next());
      }

      // --------------------------------------------------------------
      // Tests with custom deserializer via BeanDeserializerModifier
      // (this is the exact scenario of the reported bug)
      // --------------------------------------------------------------

      @Test
      public void testCustomViaModifierExactMatch() throws Exception {
          ObjectMapper mapper = mapperWithModifier();
          String json = "{\"replacements\":\"v\"}";
          Map<KeyEnum, String> map = mapper.readValue(json,
              new TypeReference<Map<KeyEnum, String>>() {});
          assertTrue(map.containsKey(KeyEnum.replacements));
      }

      @Test
      public void testCustomViaModifierCaseInsensitive() throws Exception {
          ObjectMapper mapper = mapperWithModifier();
          String json = "{\"REPlaceMENTS\":\"v\"}";
          Map<KeyEnum, String> map = mapper.readValue(json,
              new TypeReference<Map<KeyEnum, String>>() {});
          assertEquals(KeyEnum.replacements, map.keySet().iterator().next());
      }

      @Test(expected = JsonMappingException.class)
      public void testCustomViaModifierUnknownKeyThrows() throws Exception {
          ObjectMapper mapper = mapperWithModifier();
          String json = "{\"unknownKey\":\"v\"}";
          mapper.readValue(json, new TypeReference<Map<KeyEnum, String>>() {});
      }
  }