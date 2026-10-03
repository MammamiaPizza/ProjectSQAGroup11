package com.fasterxml.jackson.databind.deser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class MapDeserializerBug735Test
{
    public static class MultiplyByOneHundredDeserializer extends JsonDeserializer<Integer>
    {
        @Override
        public Integer deserialize(JsonParser jp, com.fasterxml.jackson.databind.DeserializationContext ctxt)
            throws IOException
        {
            return Integer.valueOf(jp.getIntValue() * 100);
        }
    }

    public static class PlainIntegerDeserializer extends JsonDeserializer<Integer>
    {
        @Override
        public Integer deserialize(JsonParser jp, com.fasterxml.jackson.databind.DeserializationContext ctxt)
            throws IOException
        {
            return Integer.valueOf(jp.getIntValue());
        }
    }

    public static class MapUsingHundredDeserializer
    {
        @JsonDeserialize(contentUsing = MultiplyByOneHundredDeserializer.class)
        public Map<String, Integer> values;
    }

    public static class MapUsingPlainDeserializer
    {
        @JsonDeserialize(contentUsing = PlainIntegerDeserializer.class)
        public Map<String, Integer> values;
    }

    @Test
    public void customMapValueDeserializersMustNotLeakThroughDeserializerCache() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        MapUsingHundredDeserializer first = mapper.readValue(
                "{\"values\":{\"first\":1,\"second\":2}}",
                MapUsingHundredDeserializer.class);

        assertNotNull(first.values);
        assertEquals(Integer.valueOf(100), first.values.get("first"));
        assertEquals(Integer.valueOf(200), first.values.get("second"));

        MapUsingPlainDeserializer second = mapper.readValue(
                "{\"values\":{\"first\":1,\"second\":2}}",
                MapUsingPlainDeserializer.class);

        assertNotNull(second.values);
        assertEquals(Integer.valueOf(1), second.values.get("first"));
        assertEquals(Integer.valueOf(2), second.values.get("second"));
    }

@org.junit.Test
public void customKeyDeserializerIsUsedForMapKeys() throws java.lang.Exception {
    com.fasterxml.jackson.databind.module.SimpleModule module =
            new com.fasterxml.jackson.databind.module.SimpleModule();
    module.addKeyDeserializer(java.lang.Integer.class,
            new com.fasterxml.jackson.databind.KeyDeserializer() {
                @Override
                public java.lang.Object deserializeKey(java.lang.String key,
                        com.fasterxml.jackson.databind.DeserializationContext ctxt)
                        throws java.io.IOException {
                    return java.lang.Integer.valueOf(key.substring(1));
                }
            });

    com.fasterxml.jackson.databind.ObjectMapper mapper =
            new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.registerModule(module);

    java.util.Map<java.lang.Integer, java.lang.String> result = mapper.readValue(
            "{\"k1\":\"first\",\"k2\":\"second\"}",
            new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<java.lang.Integer, java.lang.String>>() { });

    org.junit.Assert.assertEquals("first", result.get(java.lang.Integer.valueOf(1)));
    org.junit.Assert.assertEquals("second", result.get(java.lang.Integer.valueOf(2)));
    org.junit.Assert.assertEquals(2, result.size());
}

@org.junit.Test
public void mapDeserializationRetainsNullAndNestedValues() throws java.lang.Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper =
            new com.fasterxml.jackson.databind.ObjectMapper();

    java.util.Map<java.lang.String, java.lang.Object> result = mapper.readValue(
            "{\"text\":\"value\",\"missing\":null,\"nested\":{\"number\":2},\"array\":[1,2]}",
            new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<java.lang.String, java.lang.Object>>() { });

    org.junit.Assert.assertEquals("value", result.get("text"));
    org.junit.Assert.assertTrue(result.containsKey("missing"));
    org.junit.Assert.assertNull(result.get("missing"));
    org.junit.Assert.assertTrue(result.get("nested") instanceof java.util.Map);
    org.junit.Assert.assertEquals(java.lang.Integer.valueOf(2),
            ((java.util.Map<?, ?>) result.get("nested")).get("number"));
    org.junit.Assert.assertTrue(result.get("array") instanceof java.util.List);
    org.junit.Assert.assertEquals(2, ((java.util.List<?>) result.get("array")).size());
}

@org.junit.Test
public void concreteTreeMapIsCreatedAndUsesNaturalKeyOrdering() throws java.lang.Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper =
            new com.fasterxml.jackson.databind.ObjectMapper();

    java.util.TreeMap<java.lang.String, java.lang.Integer> result = mapper.readValue(
            "{\"z\":1,\"a\":2}",
            new com.fasterxml.jackson.core.type.TypeReference<java.util.TreeMap<java.lang.String, java.lang.Integer>>() { });

    org.junit.Assert.assertEquals("a", result.firstKey());
    org.junit.Assert.assertEquals(java.lang.Integer.valueOf(1), result.get("z"));
    org.junit.Assert.assertEquals(java.lang.Integer.valueOf(2), result.get("a"));
}
}
