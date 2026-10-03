package com.fasterxml.jackson.databind.deser.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CreatorCollectorStringBuilderTest
{
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void deserializesStringIntoStringBuilder() throws Exception {
        StringBuilder value = mapper.readValue("\"Jackson databind\"", StringBuilder.class);

        assertNotNull(value);
        assertEquals("Jackson databind", value.toString());
    }

    @Test
    public void deserializesEmptyStringIntoStringBuilder() throws Exception {
        StringBuilder value = mapper.readValue("\"\"", StringBuilder.class);

        assertNotNull(value);
        assertEquals("", value.toString());
    }
}
