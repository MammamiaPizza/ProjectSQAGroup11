package com.fasterxml.jackson.databind.jsontype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BeanDeserializerExternalCreator928Test
{
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void creatorPropertyDeserializesWhenExternalTypeIdPrecedesValue() throws Exception
    {
        CreatorHolder result = mapper.readValue(
                "{\"animalType\":\"dog\",\"animal\":{\"name\":\"Rex\"}}",
                CreatorHolder.class);

        assertNotNull(result);
        assertNotNull(result.getAnimal());
        assertTrue(result.getAnimal() instanceof Dog);
        assertEquals("Rex", ((Dog) result.getAnimal()).name);
    }

    @Test
    public void creatorPropertyDeserializesWhenValuePrecedesExternalTypeId() throws Exception
    {
        CreatorHolder result = mapper.readValue(
                "{\"animal\":{\"name\":\"Fido\"},\"animalType\":\"dog\"}",
                CreatorHolder.class);

        assertNotNull(result);
        assertNotNull(result.getAnimal());
        assertTrue(result.getAnimal() instanceof Dog);
        assertEquals("Fido", ((Dog) result.getAnimal()).name);
    }

    @Test
    public void regularExternalTypeIdPropertyStillDeserializesInEitherOrder() throws Exception
    {
        SetterHolder typeFirst = mapper.readValue(
                "{\"animalType\":\"dog\",\"animal\":{\"name\":\"Max\"}}",
                SetterHolder.class);
        SetterHolder valueFirst = mapper.readValue(
                "{\"animal\":{\"name\":\"Bella\"},\"animalType\":\"dog\"}",
                SetterHolder.class);

        assertTrue(typeFirst.animal instanceof Dog);
        assertEquals("Max", ((Dog) typeFirst.animal).name);
        assertTrue(valueFirst.animal instanceof Dog);
        assertEquals("Bella", ((Dog) valueFirst.animal).name);
    }

    @Test(expected = JsonMappingException.class)
    public void unknownExternalTypeIdForCreatorPropertyIsReportedAsMappingProblem() throws Exception
    {
        mapper.readValue(
                "{\"animalType\":\"unknown\",\"animal\":{\"name\":\"Rex\"}}",
                CreatorHolder.class);
    }

    @JsonSubTypes({
        @JsonSubTypes.Type(value = Dog.class, name = "dog")
    })
    public static interface Animal
    {
    }

    @JsonTypeName("dog")
    public static class Dog implements Animal
    {
        public String name;

        public Dog()
        {
        }
    }

    public static class CreatorHolder
    {
        private final Animal animal;

        @JsonCreator
        public CreatorHolder(
                @JsonProperty("animal")
                @JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
                        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                        property = "animalType")
                Animal animal)
        {
            this.animal = animal;
        }

        public Animal getAnimal()
        {
            return animal;
        }
    }

    public static class SetterHolder
    {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "animalType")
        public Animal animal;

        public SetterHolder()
        {
        }
    }
}
