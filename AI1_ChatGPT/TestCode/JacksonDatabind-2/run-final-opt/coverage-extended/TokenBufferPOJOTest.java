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

@org.junit.Test
public void parserConstructorRetainsSourceCodecForBufferedParser() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    com.fasterxml.jackson.core.JsonParser source = mapper.getFactory().createParser("{}");
    com.fasterxml.jackson.databind.util.TokenBuffer buffer =
            new com.fasterxml.jackson.databind.util.TokenBuffer(source);
    source.close();

    org.junit.Assert.assertSame(mapper, buffer.getCodec());

    buffer.writeStartObject();
    buffer.writeStringField("name", "buffered");
    buffer.writeEndObject();

    com.fasterxml.jackson.core.JsonParser parser = buffer.asParser();
    org.junit.Assert.assertSame(mapper, parser.getCodec());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.START_OBJECT, parser.nextToken());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.FIELD_NAME, parser.nextToken());
    org.junit.Assert.assertEquals("name", parser.getCurrentName());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_STRING, parser.nextToken());
    org.junit.Assert.assertEquals("buffered", parser.getText());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.END_OBJECT, parser.nextToken());
    org.junit.Assert.assertEquals(null, parser.nextToken());
    parser.close();
}

@org.junit.Test
public void appendPreservesTokensAcrossSegmentBoundary() throws Exception {
    com.fasterxml.jackson.databind.util.TokenBuffer source =
            new com.fasterxml.jackson.databind.util.TokenBuffer(null, false);
    for (int i = 0; i < 20; ++i) {
        source.writeNumber(i);
    }

    com.fasterxml.jackson.databind.util.TokenBuffer target =
            new com.fasterxml.jackson.databind.util.TokenBuffer(null, false);
    target.writeStartArray();
    org.junit.Assert.assertSame(target, target.append(source));
    target.writeEndArray();

    com.fasterxml.jackson.core.JsonParser parser = target.asParser();
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.START_ARRAY, parser.nextToken());
    for (int i = 0; i < 20; ++i) {
        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        org.junit.Assert.assertEquals(i, parser.getIntValue());
    }
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.END_ARRAY, parser.nextToken());
    org.junit.Assert.assertEquals(null, parser.nextToken());
    parser.close();
}

@org.junit.Test
public void nativeIdsAreIncludedInTokenBufferDescription() throws Exception {
    com.fasterxml.jackson.databind.util.TokenBuffer buffer =
            new com.fasterxml.jackson.databind.util.TokenBuffer(null, true);
    buffer.writeObjectId("object-17");
    buffer.writeTypeId("type-17");
    buffer.writeString("value");

    String description = buffer.toString();
    org.junit.Assert.assertTrue(description.contains("[objectId=object-17]"));
    org.junit.Assert.assertTrue(description.contains("[typeId=type-17]"));
}
}
