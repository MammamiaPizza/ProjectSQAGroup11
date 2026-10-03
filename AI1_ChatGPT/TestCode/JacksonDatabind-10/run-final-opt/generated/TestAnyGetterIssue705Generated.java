package com.fasterxml.jackson.databind.ser;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestAnyGetterIssue705Generated
{
    private final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testIssue705AnyGetterUsesKeyAndContentSerializers() throws Exception
    {
        assertEquals("{\"stuff\":\"key/value\"}",
                MAPPER.writeValueAsString(new Issue705Bean()));
    }

    @Test
    public void testOrdinaryAnyGetterEntriesAreWrittenAsBeanFields() throws Exception
    {
        Map<String, String> values = new LinkedHashMap<String, String>();
        values.put("first", "one");
        values.put("second", "two");

        assertEquals("{\"first\":\"one\",\"second\":\"two\"}",
                MAPPER.writeValueAsString(new OrdinaryAnyGetterBean(values)));
    }

    @Test
    public void testNullAnyGetterValueProducesNoFields() throws Exception
    {
        assertEquals("{}", MAPPER.writeValueAsString(new NullAnyGetterBean()));
    }

    @Test
    public void testNonMapAnyGetterValueFailsWithMappingException() throws Exception
    {
        try {
            MAPPER.writeValueAsString(new InvalidAnyGetterBean());
            fail("Expected a JsonMappingException for a non-Map any-getter value");
        } catch (JsonMappingException e) {
            assertTrue(e.getMessage().contains("any-getter"));
        }
    }

    static class Issue705Bean
    {
        private final Map<String, String> values = new LinkedHashMap<String, String>();

        Issue705Bean() {
            values.put("key", "value");
        }

        @JsonAnyGetter
        @JsonSerialize(keyUsing = Issue705KeySerializer.class,
                contentUsing = Issue705ValueSerializer.class)
        public Map<String, String> getValues() {
            return values;
        }
    }

    public static class Issue705KeySerializer extends JsonSerializer<Object>
    {
        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            gen.writeFieldName("stuff");
        }
    }

    public static class Issue705ValueSerializer extends JsonSerializer<Object>
    {
        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            gen.writeString("key/value");
        }
    }

    static class OrdinaryAnyGetterBean
    {
        private final Map<String, String> values;

        OrdinaryAnyGetterBean(Map<String, String> values) {
            this.values = values;
        }

        @JsonAnyGetter
        public Map<String, String> getValues() {
            return values;
        }
    }

    static class NullAnyGetterBean
    {
        @JsonAnyGetter
        public Map<String, String> getValues() {
            return null;
        }
    }

    static class InvalidAnyGetterBean
    {
        @JsonAnyGetter
        public Object getValues() {
            return "not a map";
        }
    }
}
