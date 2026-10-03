package com.fasterxml.jackson.databind.deser;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class UntypedObjectDeserializerBug989Test
{
    @Test
    public void testEndObjectTokenRepresentsEmptyUntypedObject() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        JsonParser parser = mapper.getFactory().createParser("{}");
        try {
            assertEquals(com.fasterxml.jackson.core.JsonToken.START_OBJECT, parser.nextToken());

            Object value = mapper.readValue(parser, Object.class);

            assertTrue(value instanceof Map);
            assertTrue(((Map<?, ?>) value).isEmpty());
        } finally {
            parser.close();
        }
    }

    @Test
    public void testNestedUntypedObjectsIncludingEmptyObject() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        Object value = mapper.readValue("{\"outer\":{\"inner\":{}}}", Object.class);

        assertTrue(value instanceof Map);
        Map<?, ?> outer = (Map<?, ?>) value;
        assertTrue(outer.get("outer") instanceof Map);

        Map<?, ?> nested = (Map<?, ?>) outer.get("outer");
        assertTrue(nested.containsKey("inner"));
        assertTrue(nested.get("inner") instanceof Map);
        assertTrue(((Map<?, ?>) nested.get("inner")).isEmpty());
    }

    @Test
    public void testNestedUntypedValuesInMapContainExpectedGraph() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        Map<?, ?> root = mapper.readValue(
                "{\"object\":{\"empty\":{},\"name\":\"value\"},\"nullValue\":null}",
                Map.class);

        assertTrue(root.get("object") instanceof Map);
        Map<?, ?> object = (Map<?, ?>) root.get("object");
        assertTrue(object.get("empty") instanceof Map);
        assertTrue(((Map<?, ?>) object.get("empty")).isEmpty());
        assertEquals("value", object.get("name"));
        assertTrue(root.containsKey("nullValue"));
        assertNull(root.get("nullValue"));
    }

    @Test
    public void testNestedArraysAndObjectsDeserializeAsUntypedValues() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        Object value = mapper.readValue(
                "{\"items\":[{}, {\"text\":\"x\",\"number\":3}, [], true, null]}",
                Object.class);

        Map<?, ?> root = (Map<?, ?>) value;
        assertTrue(root.get("items") instanceof List);
        List<?> items = (List<?>) root.get("items");

        assertEquals(5, items.size());
        assertTrue(items.get(0) instanceof Map);
        assertTrue(((Map<?, ?>) items.get(0)).isEmpty());
        assertTrue(items.get(1) instanceof Map);
        assertEquals("x", ((Map<?, ?>) items.get(1)).get("text"));
        assertEquals(Integer.valueOf(3), ((Map<?, ?>) items.get(1)).get("number"));
        assertTrue(items.get(2) instanceof List);
        assertTrue(((List<?>) items.get(2)).isEmpty());
        assertEquals(Boolean.TRUE, items.get(3));
        assertNull(items.get(4));
    }

    @Test
    public void testJavaArrayFeatureHandlesNestedUntypedObjects() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY);

        Object value = mapper.readValue("[{}, {\"nested\":[]}, \"text\"]", Object.class);

        assertTrue(value instanceof Object[]);
        Object[] values = (Object[]) value;
        assertEquals(3, values.length);
        assertTrue(values[0] instanceof Map);
        assertTrue(((Map<?, ?>) values[0]).isEmpty());
        assertTrue(values[1] instanceof Map);

        Object nested = ((Map<?, ?>) values[1]).get("nested");
        assertTrue(nested instanceof Object[]);
        assertEquals(0, ((Object[]) nested).length);
        assertEquals("text", values[2]);
    }

    @Test
    public void testEmptyArrayDeserializesAsUntypedValue() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        JsonParser parser = mapper.getFactory().createParser("[]");
        try {
            assertEquals(com.fasterxml.jackson.core.JsonToken.START_ARRAY, parser.nextToken());

            Object value = mapper.readValue(parser, Object.class);

            assertTrue(value instanceof List);
            assertTrue(((List<?>) value).isEmpty());
        } finally {
            parser.close();
        }
    }

@org.junit.Test
public void testUntypedScalarValuesDeserializeToNaturalJavaValues() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    org.junit.Assert.assertEquals("text", mapper.readValue("\"text\"", Object.class));
    org.junit.Assert.assertEquals(java.lang.Boolean.TRUE, mapper.readValue("true", Object.class));
    org.junit.Assert.assertEquals(java.lang.Boolean.FALSE, mapper.readValue("false", Object.class));
    org.junit.Assert.assertNull(mapper.readValue("null", Object.class));

    Object integer = mapper.readValue("37", Object.class);
    org.junit.Assert.assertTrue(integer instanceof java.lang.Integer);
    org.junit.Assert.assertEquals(37, ((java.lang.Integer) integer).intValue());

    Object decimal = mapper.readValue("1.25", Object.class);
    org.junit.Assert.assertTrue(decimal instanceof java.lang.Double);
    org.junit.Assert.assertEquals(1.25d, ((java.lang.Double) decimal).doubleValue(), 0.0d);
}
}
