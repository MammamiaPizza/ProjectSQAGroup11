package com.fasterxml.jackson.databind.interop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class IllegalTypesCheckTest
{
    public static class SimpleBean {
        public int value;

        public SimpleBean() { }
    }

    @Test
    public void testIssue1599() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.readValue("{}", Runtime.class);
            fail("Deserializing Runtime must be rejected as an illegal type");
        } catch (Exception e) {
            assertTrue("Expected illegal-type rejection, got: " + e.getMessage(),
                    e.getMessage() != null && e.getMessage().contains("Illegal type"));
        }
    }

    @Test
    public void testRegularBeanDeserializationStillWorks() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        SimpleBean result = mapper.readValue("{\"value\":13}", SimpleBean.class);

        assertEquals(13, result.value);
    }
}