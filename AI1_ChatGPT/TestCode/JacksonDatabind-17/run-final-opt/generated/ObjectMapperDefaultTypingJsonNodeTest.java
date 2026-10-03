package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ObjectMapperDefaultTypingJsonNodeTest
{
    @Test
    public void readTreeWithDefaultTypingAcceptsNumericArray() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();

        JsonNode root = mapper.readTree("[1,2,3]");

        assertTrue(root.isArray());
        assertEquals(3, root.size());
        assertTrue(root.get(0).isInt());
        assertEquals(1, root.get(0).intValue());
        assertEquals(3, root.get(2).intValue());
    }

    @Test
    public void readValueAsJsonNodeWithDefaultTypingAcceptsNumericArray() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();

        JsonNode root = mapper.readValue("[42]", JsonNode.class);

        assertTrue(root.isArray());
        assertEquals(1, root.size());
        assertTrue(root.get(0).isInt());
        assertEquals(42, root.get(0).intValue());
    }

    @Test
    public void jsonNodePropertyAcceptsNumericArrayWithDefaultTyping() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();

        NodeHolder holder = mapper.readValue("{\"value\":[7,8]}", NodeHolder.class);

        assertTrue(holder.getValue().isArray());
        assertEquals(2, holder.getValue().size());
        assertEquals(7, holder.getValue().get(0).intValue());
        assertEquals(8, holder.getValue().get(1).intValue());
    }

    @Test
    public void readTreeWithDefaultTypingAcceptsEmptyArray() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();

        JsonNode root = mapper.readTree("[]");

        assertTrue(root.isArray());
        assertEquals(0, root.size());
    }

    @Test(expected = JsonProcessingException.class)
    public void malformedTreeInputStillReportsParseFailureWithDefaultTyping() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();

        mapper.readTree("[1,");
    }

    public static class NodeHolder
    {
        private JsonNode value;

        public JsonNode getValue()
        {
            return value;
        }

        public void setValue(JsonNode value)
        {
            this.value = value;
        }
    }
}
