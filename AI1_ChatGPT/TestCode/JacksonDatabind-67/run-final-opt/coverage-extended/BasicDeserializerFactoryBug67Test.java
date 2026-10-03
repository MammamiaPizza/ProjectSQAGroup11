import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;

public class BasicDeserializerFactoryBug67Test
{
    enum KeyEnum {
        rootDirectory,
        replacements,
        licenseString
    }

    private static final TypeReference<Map<KeyEnum, Integer>> ENUM_MAP =
            new TypeReference<Map<KeyEnum, Integer>>() { };

    @Test
    public void customKeyDeserializerModifierIsAppliedToEnumMapKeys() throws Exception {
        ObjectMapper mapper = mapperWithCaseInsensitiveEnumKeys();

        Map<KeyEnum, Integer> result = mapper.readValue(
                "{\"REPlaceMENTS\":1,\"rootDirectory\":2,\"licenseString\":3}",
                ENUM_MAP);

        assertEquals(3, result.size());
        assertEquals(Integer.valueOf(1), result.get(KeyEnum.replacements));
        assertEquals(Integer.valueOf(2), result.get(KeyEnum.rootDirectory));
        assertEquals(Integer.valueOf(3), result.get(KeyEnum.licenseString));
    }

    @Test(expected = InvalidFormatException.class)
    public void enumMapKeyWithoutModifierStillRejectsMixedCaseName() throws Exception {
        new ObjectMapper().readValue("{\"REPlaceMENTS\":1}", ENUM_MAP);
    }

    @Test
    public void modifierProvidedDeserializerHandlesDeclaredEnumNamesToo() throws Exception {
        ObjectMapper mapper = mapperWithCaseInsensitiveEnumKeys();

        Map<KeyEnum, Integer> result = mapper.readValue(
                "{\"replacements\":7,\"rootDirectory\":8}", ENUM_MAP);

        assertTrue(result.containsKey(KeyEnum.replacements));
        assertTrue(result.containsKey(KeyEnum.rootDirectory));
        assertEquals(Integer.valueOf(7), result.get(KeyEnum.replacements));
        assertEquals(Integer.valueOf(8), result.get(KeyEnum.rootDirectory));
    }

    private ObjectMapper mapperWithCaseInsensitiveEnumKeys() {
        SimpleModule module = new SimpleModule();
        module.setDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public KeyDeserializer modifyKeyDeserializer(DeserializationConfig config,
                    JavaType type, KeyDeserializer deserializer) {
                if (type.getRawClass() == KeyEnum.class) {
                    return new KeyDeserializer() {
                        @Override
                        public Object deserializeKey(String key, DeserializationContext ctxt)
                                throws IOException {
                            for (KeyEnum value : KeyEnum.values()) {
                                if (value.name().equalsIgnoreCase(key)) {
                                    return value;
                                }
                            }
                            return deserializer.deserializeKey(key, ctxt);
                        }
                    };
                }
                return deserializer;
            }
        });
        return new ObjectMapper().registerModule(module);
    }

@org.junit.Test
public void customKeyDeserializerModifierIsAppliedToRegularEnumMapKeys() throws Exception {
    com.fasterxml.jackson.databind.module.SimpleModule module =
            new com.fasterxml.jackson.databind.module.SimpleModule();
    module.setDeserializerModifier(new com.fasterxml.jackson.databind.deser.BeanDeserializerModifier() {
        @Override
        public com.fasterxml.jackson.databind.KeyDeserializer modifyKeyDeserializer(
                com.fasterxml.jackson.databind.DeserializationConfig config,
                com.fasterxml.jackson.databind.JavaType type,
                com.fasterxml.jackson.databind.KeyDeserializer deserializer) {
            if (type.getRawClass() == KeyEnum.class) {
                return new com.fasterxml.jackson.databind.KeyDeserializer() {
                    @Override
                    public Object deserializeKey(String key,
                            com.fasterxml.jackson.databind.DeserializationContext ctxt)
                            throws java.io.IOException {
                        return KeyEnum.valueOf(key.toLowerCase(java.util.Locale.ROOT));
                    }
                };
            }
            return deserializer;
        }
    });

    java.util.Map<KeyEnum, Integer> result = new com.fasterxml.jackson.databind.ObjectMapper()
            .registerModule(module)
            .readValue("{\"REPlaceMENTS\":9}",
                    new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<KeyEnum, Integer>>() { });

    org.junit.Assert.assertEquals(Integer.valueOf(9), result.get(KeyEnum.replacements));
}
}
