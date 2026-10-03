package com.fasterxml.jackson.databind.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.JsonSerializer;

public class POJONodeGeneratedTest {

    private static final String CONTEXT_KEY = "pojo-node-custom-serializer-value";

    @Test
    public void customSerializerUsesWrappedPojoAndCurrentProviderContext() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Payload payload = new Payload("Hello!");
        ObjectNode root = mapper.createObjectNode();
        root.putPOJO("value", payload);

        String json = mapper.writer()
                .withAttribute(CONTEXT_KEY, payload)
                .writeValueAsString(root);

        assertEquals("{\"value\":{\"aStr\":\"The value is: Hello!\"}}", json);
    }

    @Test
    public void getPojoAndAsTextPreserveWrappedValue() {
        Payload payload = new Payload("text");
        POJONode node = new POJONode(payload);

        assertSame(payload, node.getPojo());
        assertEquals("Payload(text)", node.asText());
        assertEquals("Payload(text)", node.asText("default"));
    }

    @Test
    public void nullPojoUsesTextDefaultAndSerializesAsJsonNull() throws Exception {
        POJONode node = new POJONode(null);

        assertEquals("null", node.asText());
        assertEquals("default", node.asText("default"));
        assertEquals("null", new ObjectMapper().writeValueAsString(node));
    }

    @Test
    public void byteArrayPojoIsExposedAsBinaryValue() throws Exception {
        byte[] bytes = new byte[] { 1, 2, 3 };
        POJONode node = new POJONode(bytes);

        assertSame(bytes, node.binaryValue());
    }

    @JsonSerialize(using = PayloadSerializer.class)
    public static class Payload {
        final String value;

        Payload(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Payload(" + value + ")";
        }
    }

    public static class PayloadSerializer extends JsonSerializer<Payload> {
        @Override
        public void serialize(Payload value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            Object contextualValue = provider.getAttribute(CONTEXT_KEY);
            String text = (value != null && contextualValue == value) ? value.value : "NULL";

            gen.writeStartObject();
            gen.writeStringField("aStr", "The value is: " + text);
            gen.writeEndObject();
        }
    }
}