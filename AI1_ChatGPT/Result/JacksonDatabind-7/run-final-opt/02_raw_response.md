package com.fasterxml.jackson.databind.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class TokenBufferBug7Test
{
    @Test
    public void testDeserializeFromFieldNameAddsImpliedObjectBoundaries() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        JsonParser input = mapper.getFactory().createParser("{\"value\":3}");
        assertEquals(JsonToken.START_OBJECT, input.nextToken());
        assertEquals(JsonToken.FIELD_NAME, input.nextToken());

        TokenBuffer buffer = new TokenBuffer(input);
        buffer.deserialize(input, null);

        JsonParser parser = buffer.asParser();
        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
        assertEquals("value", parser.getCurrentName());
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals(3, parser.getIntValue());
        assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        assertNull(parser.nextToken());
    }

    @Test
    public void testDeserializeFromStartObjectPreservesCompleteStructure() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        JsonParser input = mapper.getFactory().createParser("{\"value\":3}");
        assertEquals(JsonToken.START_OBJECT, input.nextToken());

        TokenBuffer buffer = new TokenBuffer(input);
        buffer.deserialize(input, null);

        JsonParser parser = buffer.asParser();
        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
        assertEquals("value", parser.getCurrentName());
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals(3, parser.getIntValue());
        assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        assertNull(parser.nextToken());
    }

    @Test
    public void testAsParserStartsBeforeFirstBufferedToken() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        TokenBuffer buffer = new TokenBuffer(mapper, false);
        buffer.writeStartObject();
        buffer.writeFieldName("name");
        buffer.writeString("Jackson");
        buffer.writeEndObject();

        assertEquals(JsonToken.START_OBJECT, buffer.firstToken());

        JsonParser parser = buffer.asParser();
        assertNull(parser.getCurrentToken());
        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
        assertEquals("name", parser.getCurrentName());
        assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        assertEquals("Jackson", parser.getText());
        assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        assertNull(parser.nextToken());
    }

    @Test
    public void testAsParserWithSourceDoesNotAdvanceToFieldName() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        TokenBuffer buffer = new TokenBuffer(mapper, false);
        buffer.writeStartObject();
        buffer.writeFieldName("a");
        buffer.writeEndObject();

        JsonParser source = mapper.getFactory().createParser("{\"source\":true}");
        assertEquals(JsonToken.START_OBJECT, source.nextToken());

        JsonParser parser = buffer.asParser(source);
        assertSame(mapper, parser.getCodec());
        assertNull(parser.getCurrentToken());
        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
        assertEquals("a", parser.getCurrentName());
        assertEquals(JsonToken.END_OBJECT, parser.nextToken());

        assertEquals(JsonToken.START_OBJECT, source.getCurrentToken());
    }

    @Test
    public void testNestedObjectAndArrayTokensArePreserved() throws Exception
    {
        TokenBuffer buffer = new TokenBuffer((com.fasterxml.jackson.core.ObjectCodec) null, false);
        buffer.writeStartObject();
        buffer.writeFieldName("items");
        buffer.writeStartArray();
        buffer.writeStartObject();
        buffer.writeFieldName("id");
        buffer.writeNumber(1);
        buffer.writeEndObject();
        buffer.writeEndArray();
        buffer.writeEndObject();

        JsonParser parser = buffer.asParser();
        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
        assertEquals("items", parser.getCurrentName());
        assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
        assertEquals("id", parser.getCurrentName());
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals(1, parser.getIntValue());
        assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        assertNull(parser.nextToken());
    }

    @Test
    public void testEmptyBufferHasNoFirstOrNextToken() throws Exception
    {
        TokenBuffer buffer = new TokenBuffer((com.fasterxml.jackson.core.ObjectCodec) null, false);

        assertNull(buffer.firstToken());

        JsonParser parser = buffer.asParser();
        assertNull(parser.getCurrentToken());
        assertNull(parser.nextToken());
    }

    @Test
    public void testParserTraversesPastSegmentBoundary() throws Exception
    {
        TokenBuffer buffer = new TokenBuffer((com.fasterxml.jackson.core.ObjectCodec) null, false);
        buffer.writeStartArray();
        for (int i = 0; i < 17; ++i) {
            buffer.writeNumber(i);
        }
        buffer.writeEndArray();

        JsonParser parser = buffer.asParser();
        assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        for (int i = 0; i < 17; ++i) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(i, parser.getIntValue());
        }
        assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        assertNull(parser.nextToken());
    }
}