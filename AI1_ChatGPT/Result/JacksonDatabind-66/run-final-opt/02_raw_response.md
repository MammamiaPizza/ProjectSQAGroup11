package com.fasterxml.jackson.databind.module;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StdKeyDeserializerPolymorphicEnumTest
{
    interface SuperType {
        String key();
    }

    enum SuperTypeEnum implements SuperType {
        FOO("FOO"),
        BAR("BAR");

        private final String key;

        SuperTypeEnum(String key) {
            this.key = key;
        }

        @Override
        public String key() {
            return key;
        }

        @Override
        public String toString() {
            return "text-" + key.toLowerCase();
        }
    }

    public static class SuperTypeKeySerializer extends JsonSerializer<SuperType> {
        @Override
        public void serialize(SuperType value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            gen.writeFieldName(value.key());
        }
    }

    public static class PolymorphicMapHolder {
        @JsonSerialize(keyUsing = SuperTypeKeySerializer.class)
        @JsonDeserialize(keyAs = SuperTypeEnum.class)
        public Map<SuperType, String> values;
    }

    @Test
    public void customEnumKeySerializerRoundTripsPolymorphicMapKey() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        PolymorphicMapHolder input = new PolymorphicMapHolder();
        input.values = new LinkedHashMap<SuperType, String>();
        input.values.put(SuperTypeEnum.FOO, "value");

        String json = mapper.writeValueAsString(input);
        assertEquals("{\"values\":{\"FOO\":\"value\"}}", json);

        PolymorphicMapHolder result = mapper.readValue(json, PolymorphicMapHolder.class);
        assertEquals(1, result.values.size());
        assertEquals("value", result.values.get(SuperTypeEnum.FOO));
        assertTrue(result.values.keySet().iterator().next() instanceof SuperTypeEnum);
    }

    @Test
    public void polymorphicEnumMapKeyUsesEnumNameResolver() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        PolymorphicMapHolder result = mapper.readValue(
                "{\"values\":{\"FOO\":\"first\",\"BAR\":\"second\"}}",
                PolymorphicMapHolder.class);

        assertEquals(2, result.values.size());
        assertEquals("first", result.values.get(SuperTypeEnum.FOO));
        assertEquals("second", result.values.get(SuperTypeEnum.BAR));
    }

    @Test
    public void polymorphicEnumMapKeyCanUseToStringResolver() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);

        PolymorphicMapHolder result = mapper.readValue(
                "{\"values\":{\"text-foo\":\"value\"}}",
                PolymorphicMapHolder.class);

        assertEquals(1, result.values.size());
        assertEquals("value", result.values.get(SuperTypeEnum.FOO));
    }

    @Test
    public void invalidPolymorphicEnumMapKeyReportsInvalidFormat() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.readValue("{\"values\":{\"UNKNOWN\":\"value\"}}", PolymorphicMapHolder.class);
        } catch (Exception e) {
            assertTrue(e instanceof InvalidFormatException);
            return;
        }
        throw new AssertionError("Expected an InvalidFormatException for an unknown enum map key");
    }
}