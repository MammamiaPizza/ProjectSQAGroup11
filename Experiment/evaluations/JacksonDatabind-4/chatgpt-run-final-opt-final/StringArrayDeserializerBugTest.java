package com.fasterxml.jackson.databind.deser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

public class StringArrayDeserializerBugTest
{
    @Test
    public void testNormalStringArrayDeserializationPreservesValuesAndNulls() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        String[] result = mapper.readValue("[\"first\",null,\"last\"]", String[].class);

        assertEquals(3, result.length);
        assertEquals("first", result[0]);
        assertNull(result[1]);
        assertEquals("last", result[2]);
    }

    @Test
    public void testExceptionForSecondArrayElementReportsIndexOne() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.readValue("[\"valid\",{}]", String[].class);
            fail("Expected JsonMappingException for object used as String array element");
        } catch (JsonMappingException e) {
            assertEquals(1, e.getPath().size());
            assertEquals(1, e.getPath().get(0).getIndex());
        }
    }

    @Test
    public void testExceptionForFirstArrayElementReportsIndexZero() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.readValue("[{}]", String[].class);
            fail("Expected JsonMappingException for object used as String array element");
        } catch (JsonMappingException e) {
            assertEquals(1, e.getPath().size());
            assertEquals(0, e.getPath().get(0).getIndex());
        }
    }

    @Test
    public void testSingleStringValueCanBeAcceptedAsArrayWhenFeatureEnabled() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        String[] result = mapper.readValue("\"single\"", String[].class);

        assertEquals(1, result.length);
        assertEquals("single", result[0]);
    }

    @Test
    public void testNonArrayStringIsRejectedWhenSingleValueFeatureDisabled() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.readValue("\"single\"", String[].class);
            fail("Expected non-array String input to be rejected");
        } catch (JsonProcessingException e) {
            assertEquals(JsonMappingException.class.isAssignableFrom(e.getClass()), true);
        }
    }

    @Test
    public void testEmptyStringCanDeserializeAsNullWhenFeatureEnabled() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        String[] result = mapper.readValue("\"\"", String[].class);

        assertNull(result);
    }

@org.junit.Test
public void testCustomStringDeserializerHandlesArrayLargerThanInitialBuffer() throws Exception {
    com.fasterxml.jackson.databind.module.SimpleModule module =
            new com.fasterxml.jackson.databind.module.SimpleModule();
    module.addDeserializer(String.class, new com.fasterxml.jackson.databind.JsonDeserializer<String>() {
        @Override
        public String deserialize(com.fasterxml.jackson.core.JsonParser jp,
                com.fasterxml.jackson.databind.DeserializationContext ctxt) throws java.io.IOException {
            return "custom-" + jp.getText();
        }
    });
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.registerModule(module);

    StringBuilder json = new StringBuilder("[");
    for (int i = 0; i < 13; ++i) {
        if (i > 0) {
            json.append(',');
        }
        json.append('"').append(i).append('"');
    }
    json.append(']');

    String[] result = mapper.readValue(json.toString(), String[].class);
    org.junit.Assert.assertEquals(13, result.length);
    org.junit.Assert.assertEquals("custom-0", result[0]);
    org.junit.Assert.assertEquals("custom-12", result[12]);
}

@org.junit.Test
public void testCustomStringDeserializerExceptionReportsFailingArrayIndex() throws Exception {
    com.fasterxml.jackson.databind.module.SimpleModule module =
            new com.fasterxml.jackson.databind.module.SimpleModule();
    module.addDeserializer(String.class, new com.fasterxml.jackson.databind.JsonDeserializer<String>() {
        @Override
        public String deserialize(com.fasterxml.jackson.core.JsonParser jp,
                com.fasterxml.jackson.databind.DeserializationContext ctxt) throws java.io.IOException {
            if ("bad".equals(jp.getText())) {
                throw new java.io.IOException("bad string");
            }
            return jp.getText();
        }
    });
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.registerModule(module);

    try {
        mapper.readValue("[\"good\",\"bad\"]", String[].class);
        org.junit.Assert.fail("Expected JsonMappingException from custom String deserializer");
    } catch (com.fasterxml.jackson.databind.JsonMappingException e) {
        org.junit.Assert.assertEquals(1, e.getPath().size());
        org.junit.Assert.assertEquals(1, e.getPath().get(0).getIndex());
    }
}

@org.junit.Test
public void testNullValueDeserializesAsNullStringArray() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    String[] result = mapper.readValue("null", String[].class);

    org.junit.Assert.assertNull(result);
}
}
