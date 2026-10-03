package com.fasterxml.jackson.databind.interop;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.SubTypeValidator;
import org.junit.Test;

public class SubTypeValidatorC3P0Test
{
    private static final String C3P0_TYPE =
            "com.mchange.v2.c3p0.jacksontest.ComboPooledDataSource";

    @Test
    public void defaultTypingRejectsC3P0TypeBeforeStringInstantiation() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();

        try {
            mapper.readValue("[\"" + C3P0_TYPE + "\",\"/tmp/foobar.txt\"]", Object.class);
            fail("Expected C3P0 subtype to be rejected as illegal");
        } catch (JsonMappingException e) {
            assertTrue("Expected security rejection, got: " + e.getMessage(),
                    e.getMessage().contains("Illegal type"));
        }
    }

    @Test
    public void validatorRejectsC3P0RawJavaType() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        Class<?> c3p0Type = Class.forName(C3P0_TYPE);
        JavaType type = mapper.constructType(c3p0Type);
        DeserializationContext context = mapper.getDeserializationContext();

        try {
            SubTypeValidator.instance().validateSubType(context, type);
            fail("Expected C3P0 raw type to be rejected as illegal");
        } catch (JsonMappingException e) {
            assertTrue("Expected security rejection, got: " + e.getMessage(),
                    e.getMessage().contains("Illegal type"));
        }
    }
}