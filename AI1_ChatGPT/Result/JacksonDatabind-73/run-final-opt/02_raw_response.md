package com.fasterxml.jackson.databind.deser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

public class ReadOnlyAndWriteOnlyPropertiesRegressionTest
{
    private final ObjectMapper mapper = new ObjectMapper();

    public static class ReadXWriteY {
        @JsonProperty(access = Access.READ_ONLY)
        public int x = 1;

        @JsonProperty(access = Access.WRITE_ONLY)
        public int y;
    }

    public static class Pojo935 {
        public String firstName;
        public String lastName;

        @JsonProperty(access = Access.READ_ONLY)
        public String getFullName() {
            return firstName + " " + lastName;
        }
    }

    @Test
    public void readOnlyInputIsIgnoredAndWriteOnlyInputIsBound() throws Exception {
        ReadXWriteY value = mapper.readValue("{\"x\":99,\"y\":7}", ReadXWriteY.class);

        assertEquals(1, value.x);
        assertEquals(7, value.y);
    }

    @Test
    public void readOnlyComputedNameIsIgnoredWhileRegularNamesAreBound() throws Exception {
        Pojo935 value = mapper.readValue(
                "{\"fullName\":\"Incorrect Name\",\"firstName\":\"Jane\",\"lastName\":\"Doe\"}",
                Pojo935.class);

        assertEquals("Jane", value.firstName);
        assertEquals("Doe", value.lastName);
        assertEquals("Jane Doe", value.getFullName());
    }

    @Test
    public void serializationUsesReadOnlyPropertyAndOmitsWriteOnlyProperty() throws Exception {
        ReadXWriteY value = new ReadXWriteY();
        value.x = 3;
        value.y = 8;

        JsonNode json = mapper.readTree(mapper.writeValueAsString(value));

        assertTrue(json.has("x"));
        assertEquals(3, json.get("x").asInt());
        assertFalse(json.has("y"));
    }

    @Test
    public void onlyDeclaredReadOnlyNamesBecomeIgnorable() throws Exception {
        try {
            mapper.readValue("{\"x\":4,\"unknown\":5,\"y\":6}", ReadXWriteY.class);
            fail("An unrelated unknown property must still be rejected");
        } catch (UnrecognizedPropertyException e) {
            assertEquals("unknown", e.getPropertyName());
        }
    }
}