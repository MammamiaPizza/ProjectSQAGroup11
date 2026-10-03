package com.fasterxml.jackson.databind.ser;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import static org.junit.Assert.assertEquals;

public class BasicSerializerFactoryJsonValueOverrideTest
{
    @Test
    public void testJsonValueUsesDefaultAccessorSerialization() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        assertEquals("\"value\"", mapper.writeValueAsString(new PlainJsonValue()));
    }

    @Test
    public void testJsonValueWithCustomOverride() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        assertEquals("42", mapper.writeValueAsString(new JsonValueWithCustomOverride()));
    }

    public static class PlainJsonValue
    {
        @JsonValue
        public String value() {
            return "value";
        }
    }

    public static class JsonValueWithCustomOverride
    {
        @JsonValue
        @JsonSerialize(using = NumericValueSerializer.class)
        public String value() {
            return "value";
        }
    }

    public static class NumericValueSerializer extends JsonSerializer<Object>
    {
        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
                throws IOException
        {
            gen.writeNumber(42);
        }
    }
}
