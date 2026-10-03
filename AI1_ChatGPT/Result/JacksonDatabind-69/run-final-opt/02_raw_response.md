package com.fasterxml.jackson.databind.creators;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CreatorCollectorBug69Test
{
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void explicitPropertyCreatorRetainsAllCreatorPropertiesWhenImplicitCreatorAlsoExists()
            throws Exception
    {
        ConstructorChoice value = mapper.readValue(
                "{\"intField\":37,\"stringField\":\"chosen\"}",
                ConstructorChoice.class);

        assertEquals("explicit", value.creator);
        assertEquals(37, value.intField);
        assertEquals("chosen", value.stringField);
    }

    @Test
    public void explicitPropertyCreatorHandlesPrimitiveBoundaryValueDespiteImplicitCreator()
            throws Exception
    {
        ConstructorChoice value = mapper.readValue(
                "{\"intField\":0,\"stringField\":\"zero\"}",
                ConstructorChoice.class);

        assertEquals("explicit", value.creator);
        assertEquals(0, value.intField);
        assertEquals("zero", value.stringField);
    }

    public static class ConstructorChoice {
        public final int intField;
        public final String stringField;
        public final String creator;

        @JsonCreator
        public ConstructorChoice(@JsonProperty("intField") int intField,
                                 @JsonProperty("stringField") String stringField) {
            this.intField = intField;
            this.stringField = stringField;
            this.creator = "explicit";
        }

        public ConstructorChoice(@JsonProperty("stringField") String stringField) {
            this.intField = -1;
            this.stringField = stringField;
            this.creator = "implicit";
        }
    }
}