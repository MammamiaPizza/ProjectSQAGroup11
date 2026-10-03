package com.fasterxml.jackson.databind.ser;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;

public class StdKeySerializersJsonPropertyTest
{
    enum JsonPropertyKey {
        @JsonProperty("aleph")
        A
    }

    @Test
    public void testEnumMapKeyUsesJsonPropertyValue() throws Exception
    {
        Map<JsonPropertyKey, String> values = new LinkedHashMap<JsonPropertyKey, String>();
        values.put(JsonPropertyKey.A, "b");

        assertEquals("{\"aleph\":\"b\"}", new ObjectMapper().writeValueAsString(values));
    }
}