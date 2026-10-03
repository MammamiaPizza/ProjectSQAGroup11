package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JsonNodeDeserializerBug941Test
{
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void deserializesEmptyObjectNodeWhenParserIsAtEndObject() throws Exception
    {
        JsonParser parser = mapper.getFactory().createParser("{}");
        try {
            assertEquals(com.fasterxml.jackson.core.JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(com.fasterxml.jackson.core.JsonToken.END_OBJECT, parser.nextToken());

            ObjectNode node = mapper.readValue(parser, ObjectNode.class);

            assertNotNull(node);
            assertTrue(node.isObject());
            assertEquals(0, node.size());
        } finally {
            parser.close();
        }
    }

    @Test
    public void deserializesEmptyGenericJsonNodeWhenParserIsAtEndObject() throws Exception
    {
        JsonParser parser = mapper.getFactory().createParser("{}");
        try {
            parser.nextToken();
            parser.nextToken();

            JsonNode node = mapper.readValue(parser, JsonNode.class);

            assertTrue(node.isObject());
            assertEquals(0, node.size());
        } finally {
            parser.close();
        }
    }

    @Test
    public void deserializesObjectNodeWhenParserIsAtFirstFieldName() throws Exception
    {
        JsonParser parser = mapper.getFactory().createParser("{\"answer\":42}");
        try {
            parser.nextToken();
            assertEquals(com.fasterxml.jackson.core.JsonToken.FIELD_NAME, parser.nextToken());

            ObjectNode node = mapper.readValue(parser, ObjectNode.class);

            assertEquals(42, node.get("answer").asInt());
            assertEquals(1, node.size());
        } finally {
            parser.close();
        }
    }

    @Test
    public void deserializesNestedObjectsAndArraysAsGenericJsonNode() throws Exception
    {
        JsonNode node = mapper.readValue(
                "{\"empty\":{},\"items\":[{\"id\":1},true,null],\"name\":\"value\"}",
                JsonNode.class);

        assertTrue(node.isObject());
        assertTrue(node.get("empty").isObject());
        assertEquals(0, node.get("empty").size());
        assertTrue(node.get("items").isArray());
        assertEquals(1, node.get("items").get(0).get("id").asInt());
        assertTrue(node.get("items").get(1).asBoolean());
        assertTrue(node.get("items").get(2).isNull());
        assertEquals("value", node.get("name").asText());
    }

    @Test
    public void deserializesRegularObjectNode() throws Exception
    {
        ObjectNode node = mapper.readValue("{\"name\":\"Jackson\",\"active\":false}", ObjectNode.class);

        assertEquals("Jackson", node.get("name").asText());
        assertFalse(node.get("active").asBoolean());
        assertEquals(2, node.size());
    }

    @Test
    public void deserializesRegularArrayNode() throws Exception
    {
        ArrayNode node = mapper.readValue("[1,{\"a\":\"b\"},[]]", ArrayNode.class);

        assertEquals(3, node.size());
        assertEquals(1, node.get(0).asInt());
        assertEquals("b", node.get(1).get("a").asText());
        assertTrue(node.get(2).isArray());
        assertEquals(0, node.get(2).size());
    }

    @Test
    public void rejectsScalarWhenObjectNodeIsRequested() throws Exception
    {
        try {
            mapper.readValue("\"not an object\"", ObjectNode.class);
            fail("Expected JsonMappingException for scalar input as ObjectNode");
        } catch (JsonMappingException expected) {
            assertNotNull(expected.getMessage());
        }
    }

    @Test
    public void rejectsObjectWhenArrayNodeIsRequested() throws Exception
    {
        try {
            mapper.readValue("{\"not\":\"an array\"}", ArrayNode.class);
            fail("Expected JsonMappingException for object input as ArrayNode");
        } catch (JsonMappingException expected) {
            assertNotNull(expected.getMessage());
        }
    }
}
