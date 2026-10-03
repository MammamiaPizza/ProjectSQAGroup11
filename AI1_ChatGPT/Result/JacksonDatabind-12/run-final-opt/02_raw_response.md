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
}