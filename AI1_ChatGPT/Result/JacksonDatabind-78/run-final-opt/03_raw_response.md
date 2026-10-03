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
            Throwable t = e;
            boolean found = false;
            while (t != null) {
                String message = t.getMessage();
                if (message != null && message.contains("Illegal type")) {
                    found = true;
                    break;
                }
                t = t.getCause();
            }
            assertTrue("Expected illegal-type rejection, got: " + e.getMessage(), found);
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