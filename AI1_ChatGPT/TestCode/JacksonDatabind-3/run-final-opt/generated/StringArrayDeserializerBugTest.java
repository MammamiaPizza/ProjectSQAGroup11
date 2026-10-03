package com.fasterxml.jackson.databind.deser;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class StringArrayDeserializerBugTest
{
    @Test
    public void testStringArrayWithMultipleValues() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        String[] result = mapper.readValue("[\"first\",\"second\",\"third\"]", String[].class);

        assertArrayEquals(new String[] { "first", "second", "third" }, result);
    }

    @Test
    public void testStringArrayWithNullElement() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        String[] result = mapper.readValue("[\"first\",null,\"third\"]", String[].class);

        assertEquals(3, result.length);
        assertEquals("first", result[0]);
        assertNull(result[1]);
        assertEquals("third", result[2]);
    }

    @Test
    public void testEmptyStringArray() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        String[] result = mapper.readValue("[]", String[].class);

        assertEquals(0, result.length);
    }

    @Test
    public void testStringArrayCompletesMultipleBufferChunks() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < 40; ++i) {
            if (i > 0) {
                json.append(',');
            }
            json.append('"').append("value").append(i).append('"');
        }
        json.append(']');

        String[] result = mapper.readValue(json.toString(), String[].class);

        assertEquals(40, result.length);
        assertEquals("value0", result[0]);
        assertEquals("value19", result[19]);
        assertEquals("value39", result[39]);
    }

    @Test
    public void testSingleStringAcceptedAsArrayWhenFeatureEnabled() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        String[] result = mapper.readValue("\"single\"", String[].class);

        assertArrayEquals(new String[] { "single" }, result);
    }

    @Test
    public void testSingleStringRejectedAsArrayByDefault() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.readValue("\"single\"", String[].class);
            fail("Expected a mapping exception for a non-array String value");
        } catch (JsonMappingException e) {
            assertEquals(JsonMappingException.class, e.getClass());
        }
    }

    @Test
    public void testEmptyStringCanDeserializeAsNullArrayWhenEnabled() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        String[] result = mapper.readValue("\"\"", String[].class);

        assertNull(result);
    }

    @Test
    public void testContextualContentDeserializerIsUsedForStringArray() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        ConvertedHolder result = mapper.readValue("{\"values\":[\"a\",\"b\"]}", ConvertedHolder.class);

        assertArrayEquals(new String[] { "converted-a", "converted-b" }, result.values);
    }

    public static class ConvertedHolder
    {
        @JsonDeserialize(contentUsing = PrefixingDeserializer.class)
        public String[] values;
    }

    public static class PrefixingDeserializer extends JsonDeserializer<String>
    {
        @Override
        public String deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException
        {
            return "converted-" + jp.getText();
        }
    }
}
