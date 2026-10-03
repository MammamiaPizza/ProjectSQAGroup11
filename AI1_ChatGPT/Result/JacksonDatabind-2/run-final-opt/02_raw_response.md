package com.fasterxml.jackson.databind.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TokenBufferPOJOTest
{
    @Test
    public void writeObjectWithCodecExposesPojoAsStructuredObjectTokens() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        TokenBuffer buffer = new TokenBuffer(mapper, false);

        buffer.writeObject(new NameBean("Ada"));

        assertEquals(JsonToken.START_OBJECT, buffer.firstToken());

        JsonParser parser = buffer.asParser();
        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
        assertEquals("name", parser.getCurrentName());
        assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        assertEquals("Ada", parser.getText());
        assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        assertEquals(null, parser.nextToken());
    }

    @Test
    public void convertValueOfPojoProducesObjectNodeRatherThanPojoNode() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode node = mapper.convertValue(new NameBean("Ada"), JsonNode.class);

        assertTrue("POJO conversion must expose an object structure", node.isObject());
        assertNotNull(node.get("name"));
        assertEquals("Ada", node.get("name").asText());
    }

    @Test
    public void convertValueRetainsNestedPojoStructureAndScalarProperties() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        Envelope value = new Envelope(new NameBean("Ada"), 3);

        JsonNode node = mapper.convertValue(value, JsonNode.class);

        assertTrue(node.isObject());
        assertEquals(3, node.get("count").asInt());
        assertNotNull(node.get("child"));
        assertTrue("nested POJO must be represented as an object", node.get("child").isObject());
        assertEquals("Ada", node.get("child").get("name").asText());
    }

    @Test
    public void manuallyBufferedObjectHasExpectedParserSequence() throws Exception
    {
        TokenBuffer buffer = new TokenBuffer(new ObjectMapper(), false);
        buffer.writeStartObject();
        buffer.writeFieldName("enabled");
        buffer.writeBoolean(true);
        buffer.writeEndObject();

        assertEquals(JsonToken.START_OBJECT, buffer.firstToken());

        JsonParser parser = buffer.asParser();
        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
        assertEquals("enabled", parser.getCurrentName());
        assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
        assertTrue(parser.getBooleanValue());
        assertEquals(JsonToken.END_OBJECT, parser.nextToken());
    }

    @Test
    public void writeNullObjectProducesNullToken() throws Exception
    {
        TokenBuffer buffer = new TokenBuffer(new ObjectMapper(), false);

        buffer.writeObject(null);

        assertEquals(JsonToken.VALUE_NULL, buffer.firstToken());
        JsonParser parser = buffer.asParser();
        assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void streamingBinaryWriteIsExplicitlyUnsupported() throws Exception
    {
        TokenBuffer buffer = new TokenBuffer(new ObjectMapper(), false);

        buffer.writeBinary(null, new ByteArrayInputStream(new byte[] { 1 }), 1);
    }

    public static class NameBean
    {
        private final String name;

        public NameBean(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class Envelope
    {
        private final NameBean child;
        private final int count;

        public Envelope(NameBean child, int count) {
            this.child = child;
            this.count = count;
        }

        public NameBean getChild() {
            return child;
        }

        public int getCount() {
            return count;
        }
    }
}