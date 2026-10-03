package com.fasterxml.jackson.databind.jsontype;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AsPropertyTypeDeserializerBug74Test
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = PropertyImpl.class, name = "impl")
    })
    public static interface AsProperty {
    }

    public static class PropertyImpl implements AsProperty {
        public int value;

        public PropertyImpl() { }
    }

    @Test
    public void emptyStringIsNullWhenEmptyStringsAreAcceptedAsNullObjects() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

        AsProperty result = mapper.readValue("\"\"", AsProperty.class);

        assertNull(result);
    }

    @Test
    public void typeIdAfterRegularPropertiesStillDeserializesBufferedProperties() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        AsProperty result = mapper.readValue("{\"value\":7,\"type\":\"impl\"}", AsProperty.class);

        assertTrue(result instanceof PropertyImpl);
        assertEquals(7, ((PropertyImpl) result).value);
    }

    @Test
    public void objectWithoutTypePropertyReportsMissingTypeId() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.readValue("{\"value\":7}", AsProperty.class);
            fail("Expected a missing type id failure");
        } catch (JsonMappingException e) {
            assertTrue(e.getMessage().contains("missing property 'type'"));
        }
    }

    @Test
    public void emptyStringWithoutNullObjectFeatureStillReportsMissingTypeId() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.readValue("\"\"", AsProperty.class);
            fail("Expected a missing type id failure");
        } catch (JsonMappingException e) {
            assertTrue(e.getMessage().contains("missing property 'type'"));
        }
    }

@Test
public void defaultTypingDeserializesTypeIdBeforeMapProperties() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.enableDefaultTyping(com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL,
            com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY);

    Object result = mapper.readValue(
            "{\"@class\":\"java.util.LinkedHashMap\",\"item\":{\"@class\":\"java.util.LinkedHashMap\",\"answer\":7}}",
            Object.class);

    org.junit.Assert.assertTrue(result instanceof java.util.LinkedHashMap);
    java.util.Map<?, ?> values = (java.util.Map<?, ?>) result;
    org.junit.Assert.assertTrue(values.get("item") instanceof java.util.LinkedHashMap);
    org.junit.Assert.assertEquals(Integer.valueOf(7),
            ((java.util.Map<?, ?>) values.get("item")).get("answer"));
}

@Test
public void defaultTypingPreservesNaturalStringWithoutTypeId() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.enableDefaultTyping(com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL,
            com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY);

    Object result = mapper.readValue("\"plain text\"", Object.class);

    org.junit.Assert.assertEquals("plain text", result);
}

@Test
public void defaultTypingAcceptsArrayWrapperForPropertyInclusion() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.enableDefaultTyping(com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL,
            com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY);

    Object result = mapper.readValue("[\"java.util.ArrayList\",[1,2]]", Object.class);

    org.junit.Assert.assertTrue(result instanceof java.util.ArrayList);
    org.junit.Assert.assertEquals(java.util.Arrays.asList(Integer.valueOf(1), Integer.valueOf(2)), result);
}
}
