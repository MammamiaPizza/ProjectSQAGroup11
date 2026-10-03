package com.fasterxml.jackson.databind.jsontype.ext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExternalTypeId999GeneratedTest
{
    private final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testCreatorReceivesExternalTypeIdWhenValuePrecedesTypeId() throws Exception
    {
        Envelope result = MAPPER.readValue(
                "{\"payload\":{\"text\":\"value\"},\"type\":\"foo\"}",
                Envelope.class);

        assertEquals("foo", result.type);
        assertNotNull(result.payload);
        assertTrue(result.payload instanceof FooPayload);
        assertEquals("value", ((FooPayload) result.payload).text);
    }

    @Test
    public void testCreatorReceivesExternalTypeIdWhenTypeIdPrecedesValue() throws Exception
    {
        Envelope result = MAPPER.readValue(
                "{\"type\":\"foo\",\"payload\":{\"text\":\"value\"}}",
                Envelope.class);

        assertEquals("foo", result.type);
        assertNotNull(result.payload);
        assertTrue(result.payload instanceof FooPayload);
        assertEquals("value", ((FooPayload) result.payload).text);
    }

    @Test(expected = JsonMappingException.class)
    public void testMissingExternalTypeIdIsRejected() throws Exception
    {
        MAPPER.readValue("{\"payload\":{\"text\":\"value\"}}", Envelope.class);
    }

    static class Envelope {
        final String type;
        final Payload payload;

        @JsonCreator
        Envelope(@JsonProperty("type") String type,
                @JsonProperty("payload") Payload payload) {
            this.type = type;
            this.payload = payload;
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = FooPayload.class, name = "foo")
    })
    static abstract class Payload {
    }

    static class FooPayload extends Payload {
        public String text;
    }
}
