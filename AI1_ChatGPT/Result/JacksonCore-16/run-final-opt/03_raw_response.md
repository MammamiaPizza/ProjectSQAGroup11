package com.fasterxml.jackson.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.junit.Test;

public class JsonParserSequenceDefectsTest
{
    private final JsonFactory factory = new JsonFactory();

    @Test
    public void initializedParserIsNotDuplicatedWhenSequenceIsFlattened() throws Exception
    {
        JsonParser initialized = factory.createParser("1");
        assertEquals(JsonToken.VALUE_NUMBER_INT, initialized.nextToken());

        JsonParser second = factory.createParser("2");
        JsonParser third = factory.createParser("3");

        JsonParserSequence inner = JsonParserSequence.createFlattened(initialized, second);
        JsonParserSequence flattened = JsonParserSequence.createFlattened(inner, third);

        assertEquals(3, flattened.containedParsersCount());

        assertEquals(JsonToken.VALUE_NUMBER_INT, flattened.nextToken());
        assertEquals(2, flattened.getIntValue());
        assertEquals(JsonToken.VALUE_NUMBER_INT, flattened.nextToken());
        assertEquals(3, flattened.getIntValue());
        assertNull(flattened.nextToken());
    }

    @Test
    public void flatteningUninitializedSequenceRetainsAllRemainingParsers() throws Exception
    {
        JsonParser first = factory.createParser("1");
        JsonParser second = factory.createParser("2");
        JsonParser third = factory.createParser("3");

        JsonParserSequence inner = JsonParserSequence.createFlattened(first, second);
        JsonParserSequence flattened = JsonParserSequence.createFlattened(inner, third);

        assertEquals(3, flattened.containedParsersCount());

        assertEquals(JsonToken.VALUE_NUMBER_INT, flattened.nextToken());
        assertEquals(1, flattened.getIntValue());
        assertEquals(JsonToken.VALUE_NUMBER_INT, flattened.nextToken());
        assertEquals(2, flattened.getIntValue());
        assertEquals(JsonToken.VALUE_NUMBER_INT, flattened.nextToken());
        assertEquals(3, flattened.getIntValue());
        assertNull(flattened.nextToken());
    }

    @Test
    public void nextTokenSkipsEmptyParserAtBoundary() throws Exception
    {
        JsonParser empty = factory.createParser("");
        JsonParser value = factory.createParser("42");

        JsonParserSequence sequence = JsonParserSequence.createFlattened(empty, value);

        assertEquals(JsonToken.VALUE_NUMBER_INT, sequence.nextToken());
        assertEquals(42, sequence.getIntValue());
        assertNull(sequence.nextToken());
    }

    @Test
    public void nextTokenTraversesObjectAndArrayParserBoundaries() throws Exception
    {
        JsonParser object = factory.createParser("{\"a\":1}");
        JsonParser array = factory.createParser("[true]");

        JsonParserSequence sequence = JsonParserSequence.createFlattened(object, array);

        assertEquals(JsonToken.START_OBJECT, sequence.nextToken());
        assertEquals(JsonToken.FIELD_NAME, sequence.nextToken());
        assertEquals("a", sequence.getCurrentName());
        assertEquals(JsonToken.VALUE_NUMBER_INT, sequence.nextToken());
        assertEquals(1, sequence.getIntValue());
        assertEquals(JsonToken.END_OBJECT, sequence.nextToken());
        assertEquals(JsonToken.START_ARRAY, sequence.nextToken());
        assertEquals(JsonToken.VALUE_TRUE, sequence.nextToken());
        assertEquals(JsonToken.END_ARRAY, sequence.nextToken());
        assertNull(sequence.nextToken());
    }

    @Test
    public void closeClosesEveryUnderlyingParser() throws Exception
    {
        JsonParser first = factory.createParser("1");
        JsonParser second = factory.createParser("2");
        JsonParser third = factory.createParser("3");

        JsonParserSequence sequence = JsonParserSequence.createFlattened(
                JsonParserSequence.createFlattened(first, second), third);

        assertFalse(first.isClosed());
        assertFalse(second.isClosed());
        assertFalse(third.isClosed());

        sequence.close();

        assertTrue(first.isClosed());
        assertTrue(second.isClosed());
        assertTrue(third.isClosed());
    }
}