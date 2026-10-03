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
}