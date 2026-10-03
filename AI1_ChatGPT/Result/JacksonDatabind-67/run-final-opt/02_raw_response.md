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
}