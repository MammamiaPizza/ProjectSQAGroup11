package com.fasterxml.jackson.databind.jsontype.ext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExternalTypeIdWithEnum1328RegressionTest
{
    private final ObjectMapper MAPPER = new ObjectMapper();

    enum AnimalType {
        DOG
    }

    static abstract class Animal { }

    static class Dog extends Animal {
        public String name;

        public Dog() { }
    }

    static class AnimalAndType {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type")
        @JsonSubTypes({
                @JsonSubTypes.Type(value = Dog.class, name = "DOG")
        })
        public final Animal animal;

        public final AnimalType type;

        @JsonCreator
        public AnimalAndType(@JsonProperty("animal") Animal animal,
                @JsonProperty("type") AnimalType type) {
            this.animal = animal;
            this.type = type;
        }
    }

    @Test
    public void externalTypeIdAfterValueBindsEnumCreatorProperty() throws Exception {
        AnimalAndType result = MAPPER.readValue(
                "{\"animal\":{\"name\":\"Rex\"},\"type\":\"DOG\"}",
                AnimalAndType.class);

        assertTrue(result.animal instanceof Dog);
        assertEquals("Rex", ((Dog) result.animal).name);
        assertEquals(AnimalType.DOG, result.type);
    }

    @Test
    public void externalTypeIdBeforeValueBindsEnumCreatorProperty() throws Exception {
        AnimalAndType result = MAPPER.readValue(
                "{\"type\":\"DOG\",\"animal\":{\"name\":\"Rex\"}}",
                AnimalAndType.class);

        assertTrue(result.animal instanceof Dog);
        assertEquals("Rex", ((Dog) result.animal).name);
        assertEquals(AnimalType.DOG, result.type);
    }

    @Test
    public void nullExternalValueStillBindsAssociatedEnumType() throws Exception {
        AnimalAndType result = MAPPER.readValue(
                "{\"type\":\"DOG\",\"animal\":null}",
                AnimalAndType.class);

        assertNull(result.animal);
        assertEquals(AnimalType.DOG, result.type);
    }

    @Test
    public void absentExternalValueAndTypeAreAllowedTogether() throws Exception {
        AnimalAndType result = MAPPER.readValue("{}", AnimalAndType.class);

        assertNull(result.animal);
        assertNull(result.type);
    }

    @Test(expected = JsonMappingException.class)
    public void missingExternalValueFailsWhenFeatureIsEnabled() throws Exception {
        MAPPER.enable(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY)
                .readValue("{\"type\":\"DOG\"}", AnimalAndType.class);
    }

    @Test(expected = JsonMappingException.class)
    public void missingExternalTypeIdForObjectValueFails() throws Exception {
        MAPPER.readValue("{\"animal\":{\"name\":\"Rex\"}}", AnimalAndType.class);
    }
}
