package com.fasterxml.jackson.databind.jsontype.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JavaType;

import org.junit.Test;

public class ClassNameIdResolverBug88Test
{
    public static abstract class Payload {
    }

    public static class PayloadImpl extends Payload {
        public String value;

        public PayloadImpl() { }

        public PayloadImpl(String value) {
            this.value = value;
        }
    }

    public static class Wrapper {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY)
        public Payload w;
    }

    @Test
    public void mechanismIsClassNameBased() {
        ObjectMapper mapper = new ObjectMapper();
        JavaType baseType = mapper.getTypeFactory().constructType(Payload.class);
        ClassNameIdResolver resolver = new ClassNameIdResolver(baseType, mapper.getTypeFactory());

        assertEquals(JsonTypeInfo.Id.CLASS, resolver.getMechanism());
    }

    @Test
    public void validSubtypeClassNameDeserializesIntoDeclaredBaseType() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = "{\"w\":[\"" + PayloadImpl.class.getName() + "\",{\"value\":\"ok\"}]}";

        Wrapper result = mapper.readValue(json, Wrapper.class);

        assertTrue(result.w instanceof PayloadImpl);
        assertEquals("ok", ((PayloadImpl) result.w).value);
    }

    @Test
    public void plainNonSubtypeClassNameIsRejectedDuringTypeResolution() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = "{\"w\":[\"java.util.HashMap\",{}]}";

        try {
            mapper.readValue(json, Wrapper.class);
            fail("Expected incompatible class name type id to be rejected");
        } catch (JsonMappingException e) {
            assertTrue(e.getMessage().contains("not subtype of"));
        }
    }

    @Test
    public void genericNonSubtypeClassNameIsRejectedDuringTypeResolution() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = "{\"w\":[\"java.util.HashMap<java.lang.String,java.lang.Object>\",{}]}";

        try {
            mapper.readValue(json, Wrapper.class);
            fail("Expected incompatible generic class name type id to be rejected");
        } catch (JsonMappingException e) {
            assertTrue(e.getMessage().contains("not subtype of"));
        }
    }

    @Test
    public void unknownClassNameUsesUnknownTypeIdHandling() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = "{\"w\":[\"no.such.type.MissingPayload\",{}]}";

        try {
            mapper.readValue(json, Wrapper.class);
            fail("Expected unknown type id to fail");
        } catch (JsonMappingException e) {
            assertTrue(e.getMessage().contains("no.such.type.MissingPayload"));
        }
    }

    @Test
    public void idFromValueUsesConcreteClassName() {
        ObjectMapper mapper = new ObjectMapper();
        JavaType baseType = mapper.getTypeFactory().constructType(Payload.class);
        ClassNameIdResolver resolver = new ClassNameIdResolver(baseType, mapper.getTypeFactory());

        assertEquals(PayloadImpl.class.getName(), resolver.idFromValue(new PayloadImpl("value")));
    }
}