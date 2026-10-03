package com.fasterxml.jackson.databind.struct;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectIdNullDeserializationTest
{
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    public static class Identifiable {
        public String id;
        public String name;
    }

    private final ObjectMapper mapper = new ObjectMapper();

    @Test(expected = JsonMappingException.class)
    public void testNullObjectIdIsAccepted() throws Exception {
        Identifiable value = mapper.readValue(
                "{\"id\":null,\"name\":\"pending\"}", Identifiable.class);

        assertNull(value.id);
        assertEquals("pending", value.name);
    }

    @Test
    public void testNonNullObjectIdStillDeserializes() throws Exception {
        Identifiable value = mapper.readValue(
                "{\"id\":\"object-1\",\"name\":\"stored\"}", Identifiable.class);

        assertEquals("object-1", value.id);
        assertEquals("stored", value.name);
    }
}