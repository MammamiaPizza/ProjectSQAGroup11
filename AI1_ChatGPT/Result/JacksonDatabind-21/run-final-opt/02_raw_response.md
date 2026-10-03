package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class JacksonAnnotationIntrospectorEnumJsonPropertyTest
{
    enum RenamedEnum {
        @JsonProperty("b")
        B,

        @JsonProperty("a")
        A
    }

    enum PlainEnum {
        FIRST,
        SECOND
    }

    @Test
    public void testDeserializesEachJsonPropertyRenamedEnumValue() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        RenamedEnum[] result = mapper.readValue("[\"b\",\"a\"]", RenamedEnum[].class);

        assertArrayEquals(new RenamedEnum[] { RenamedEnum.B, RenamedEnum.A }, result);
    }

    @Test
    public void testSerializesJsonPropertyRenamedEnumValues() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(new RenamedEnum[] {
                RenamedEnum.B, RenamedEnum.A
        });

        assertEquals("[\"b\",\"a\"]", json);
    }

    @Test
    public void testOriginalEnumConstantNameIsNotUsedWhenJsonPropertyRenamesIt() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.readValue("\"B\"", RenamedEnum.class);
            fail("Original enum constant name should not be accepted after @JsonProperty renaming");
        } catch (JsonMappingException expected) {
            assertEquals(true, expected.getMessage().contains("B"));
        }
    }

    @Test
    public void testUnannotatedEnumStillUsesItsDeclaredName() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        assertEquals("\"FIRST\"", mapper.writeValueAsString(PlainEnum.FIRST));
        assertEquals(PlainEnum.SECOND, mapper.readValue("\"SECOND\"", PlainEnum.class));
    }
}