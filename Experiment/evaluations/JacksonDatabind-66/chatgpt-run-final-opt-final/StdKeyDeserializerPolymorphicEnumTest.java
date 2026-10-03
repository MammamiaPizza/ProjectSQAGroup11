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

@org.junit.Test
public void stdKeyDeserializerParsesScalarWrapperMapKeys() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    java.util.Map<java.lang.Boolean, java.lang.String> booleans = mapper.readValue(
            "{\"true\":\"yes\",\"false\":\"no\"}",
            new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<java.lang.Boolean, java.lang.String>>() { });
    org.junit.Assert.assertEquals("yes", booleans.get(java.lang.Boolean.TRUE));
    org.junit.Assert.assertEquals("no", booleans.get(java.lang.Boolean.FALSE));

    java.util.Map<java.lang.Byte, java.lang.String> bytes = mapper.readValue(
            "{\"-128\":\"minimum\",\"255\":\"unsigned\"}",
            new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<java.lang.Byte, java.lang.String>>() { });
    org.junit.Assert.assertEquals("minimum", bytes.get(java.lang.Byte.valueOf((byte) -128)));
    org.junit.Assert.assertEquals("unsigned", bytes.get(java.lang.Byte.valueOf((byte) 255)));

    java.util.Map<java.lang.Short, java.lang.String> shorts = mapper.readValue(
            "{\"-32768\":\"minimum\",\"32767\":\"maximum\"}",
            new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<java.lang.Short, java.lang.String>>() { });
    org.junit.Assert.assertEquals("minimum", shorts.get(java.lang.Short.valueOf((short) -32768)));
    org.junit.Assert.assertEquals("maximum", shorts.get(java.lang.Short.valueOf((short) 32767)));

    java.util.Map<java.lang.Character, java.lang.String> characters = mapper.readValue(
            "{\"Z\":\"letter\"}",
            new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<java.lang.Character, java.lang.String>>() { });
    org.junit.Assert.assertEquals("letter", characters.get(java.lang.Character.valueOf('Z')));

    java.util.Map<java.lang.Integer, java.lang.String> integers = mapper.readValue(
            "{\"-12\":\"integer\"}",
            new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<java.lang.Integer, java.lang.String>>() { });
    org.junit.Assert.assertEquals("integer", integers.get(java.lang.Integer.valueOf(-12)));

    java.util.Map<java.lang.Long, java.lang.String> longs = mapper.readValue(
            "{\"1234567890123\":\"long\"}",
            new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<java.lang.Long, java.lang.String>>() { });
    org.junit.Assert.assertEquals("long", longs.get(java.lang.Long.valueOf(1234567890123L)));

    java.util.Map<java.lang.Float, java.lang.String> floats = mapper.readValue(
            "{\"1.25\":\"float\"}",
            new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<java.lang.Float, java.lang.String>>() { });
    org.junit.Assert.assertEquals("float", floats.get(java.lang.Float.valueOf(1.25f)));

    java.util.Map<java.lang.Double, java.lang.String> doubles = mapper.readValue(
            "{\"2.5\":\"double\"}",
            new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<java.lang.Double, java.lang.String>>() { });
    org.junit.Assert.assertEquals("double", doubles.get(java.lang.Double.valueOf(2.5d)));
}

@org.junit.Test
public void stdKeyDeserializerRejectsInvalidBooleanAndByteMapKeys() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    try {
        mapper.readValue("{\"TRUE\":\"value\"}",
                new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<java.lang.Boolean, java.lang.String>>() { });
        org.junit.Assert.fail("Expected invalid boolean map key to fail");
    } catch (com.fasterxml.jackson.databind.exc.InvalidFormatException expected) {
        // expected
    }

    try {
        mapper.readValue("{\"256\":\"value\"}",
                new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<java.lang.Byte, java.lang.String>>() { });
        org.junit.Assert.fail("Expected overflowing byte map key to fail");
    } catch (com.fasterxml.jackson.databind.exc.InvalidFormatException expected) {
        // expected
    }
}

@org.junit.Test
public void stdKeyDeserializerParsesStandardJdkObjectMapKeys() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    java.util.Map<java.util.UUID, java.lang.String> uuids = mapper.readValue(
            "{\"123e4567-e89b-12d3-a456-426655440000\":\"uuid\"}",
            new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<java.util.UUID, java.lang.String>>() { });
    org.junit.Assert.assertEquals("uuid",
            uuids.get(java.util.UUID.fromString("123e4567-e89b-12d3-a456-426655440000")));

    java.util.Map<java.net.URI, java.lang.String> uris = mapper.readValue(
            "{\"https://example.com/path\":\"uri\"}",
            new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<java.net.URI, java.lang.String>>() { });
    org.junit.Assert.assertEquals("uri", uris.get(java.net.URI.create("https://example.com/path")));

    java.util.Map<java.util.Locale, java.lang.String> locales = mapper.readValue(
            "{\"en_US\":\"locale\"}",
            new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<java.util.Locale, java.lang.String>>() { });
    org.junit.Assert.assertEquals("locale", locales.get(java.util.Locale.US));

    java.util.Map<java.util.Currency, java.lang.String> currencies = mapper.readValue(
            "{\"USD\":\"currency\"}",
            new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<java.util.Currency, java.lang.String>>() { });
    org.junit.Assert.assertEquals("currency", currencies.get(java.util.Currency.getInstance("USD")));

    java.util.Map<java.lang.Class<?>, java.lang.String> classes = mapper.readValue(
            "{\"java.lang.String\":\"class\"}",
            new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<java.lang.Class<?>, java.lang.String>>() { });
    org.junit.Assert.assertEquals("class", classes.get(java.lang.String.class));
}

@org.junit.Test
public void stdKeyDeserializerParsesDateAndCalendarMapKeys() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    java.util.Map<java.util.Date, java.lang.String> dates = mapper.readValue(
            "{\"1970-01-01T00:00:00.000Z\":\"epoch\"}",
            new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<java.util.Date, java.lang.String>>() { });
    org.junit.Assert.assertEquals("epoch", dates.get(new java.util.Date(0L)));

    java.util.Map<java.util.Calendar, java.lang.String> calendars = mapper.readValue(
            "{\"1970-01-01T00:00:00.000Z\":\"epoch\"}",
            new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<java.util.Calendar, java.lang.String>>() { });
    org.junit.Assert.assertEquals(1, calendars.size());
    org.junit.Assert.assertEquals(0L, calendars.keySet().iterator().next().getTimeInMillis());
}
}
