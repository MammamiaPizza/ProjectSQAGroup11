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
}
