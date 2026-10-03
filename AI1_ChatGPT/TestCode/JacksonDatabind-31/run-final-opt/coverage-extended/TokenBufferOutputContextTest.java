package com.fasterxml.jackson.databind.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.core.json.JsonWriteContext;

import org.junit.Test;

public class TokenBufferOutputContextTest
{
    @Test
    public void testCurrentNameTracksLatestStringFieldName() throws Exception {
        TokenBuffer buffer = new TokenBuffer((ObjectCodec) null);

        buffer.writeStartObject();
        assertTrue(buffer.getOutputContext().inObject());
        assertNull(buffer.getOutputContext().getCurrentName());

        buffer.writeFieldName("a");
        assertEquals("a", buffer.getOutputContext().getCurrentName());

        buffer.writeString("value");
        assertEquals("a", buffer.getOutputContext().getCurrentName());

        buffer.writeFieldName("b");
        assertEquals("b", buffer.getOutputContext().getCurrentName());

        buffer.writeEndObject();
        assertTrue(buffer.getOutputContext().inRoot());
        assertNull(buffer.getOutputContext().getCurrentName());
    }

    @Test
    public void testSerializableStringFieldNameUpdatesOutputContext() throws Exception {
        TokenBuffer buffer = new TokenBuffer((ObjectCodec) null);

        buffer.writeStartObject();
        buffer.writeFieldName(new SerializedString("first"));
        assertEquals("first", buffer.getOutputContext().getCurrentName());

        buffer.writeNumber(1);
        buffer.writeFieldName(new SerializedString("second"));
        assertEquals("second", buffer.getOutputContext().getCurrentName());

        buffer.writeBoolean(true);
        assertEquals("second", buffer.getOutputContext().getCurrentName());
    }

    @Test
    public void testNestedObjectAndArrayContextsRetainParentFieldNames() throws Exception {
        TokenBuffer buffer = new TokenBuffer((ObjectCodec) null);

        buffer.writeStartObject();
        JsonWriteContext outerObject = buffer.getOutputContext();

        buffer.writeFieldName("nested");
        assertEquals("nested", outerObject.getCurrentName());

        buffer.writeStartObject();
        JsonWriteContext innerObject = buffer.getOutputContext();
        assertTrue(innerObject.inObject());
        assertSame(outerObject, innerObject.getParent());
        assertNull(innerObject.getCurrentName());

        buffer.writeFieldName("values");
        assertEquals("values", innerObject.getCurrentName());

        buffer.writeStartArray();
        JsonWriteContext arrayContext = buffer.getOutputContext();
        assertTrue(arrayContext.inArray());
        assertSame(innerObject, arrayContext.getParent());
        assertNull(arrayContext.getCurrentName());
        assertEquals("values", innerObject.getCurrentName());

        buffer.writeNumber(3);
        buffer.writeEndArray();
        assertSame(innerObject, buffer.getOutputContext());
        assertEquals("values", buffer.getOutputContext().getCurrentName());

        buffer.writeEndObject();
        assertSame(outerObject, buffer.getOutputContext());
        assertEquals("nested", buffer.getOutputContext().getCurrentName());

        buffer.writeEndObject();
        assertTrue(buffer.getOutputContext().inRoot());
        assertNull(buffer.getOutputContext().getCurrentName());
    }

@Test
public void testParserConstructorRetainsCodecForBufferedParser() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    com.fasterxml.jackson.core.JsonParser source = mapper.getFactory().createParser("null");
    TokenBuffer buffer = new TokenBuffer(source);

    buffer.writeString("buffered");

    com.fasterxml.jackson.core.JsonParser buffered = buffer.asParser();
    assertSame(mapper, buffered.getCodec());
    assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_STRING, buffered.nextToken());
    assertEquals("buffered", buffered.getText());
    assertNull(buffered.nextToken());

    buffered.close();
    source.close();
}

@Test
public void testSegmentRolloverPreservesBufferedTokens() throws Exception {
    TokenBuffer buffer = new TokenBuffer(null, false);
    buffer.writeStartArray();
    for (int i = 0; i < 20; ++i) {
        buffer.writeString(String.valueOf(i));
    }
    buffer.writeEndArray();

    com.fasterxml.jackson.core.JsonParser parser = buffer.asParser();
    assertEquals(com.fasterxml.jackson.core.JsonToken.START_ARRAY, parser.nextToken());
    for (int i = 0; i < 20; ++i) {
        assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_STRING, parser.nextToken());
        assertEquals(String.valueOf(i), parser.getText());
    }
    assertEquals(com.fasterxml.jackson.core.JsonToken.END_ARRAY, parser.nextToken());
    assertNull(parser.nextToken());
    parser.close();
}

@Test
public void testNativeIdsAreIncludedInBufferDescription() throws Exception {
    TokenBuffer buffer = new TokenBuffer(null, true);
    buffer.writeTypeId("type-1");
    buffer.writeObjectId("object-1");
    buffer.writeString("value");

    String description = buffer.toString();
    assertTrue(description.contains("[objectId=object-1]"));
    assertTrue(description.contains("[typeId=type-1]"));
}
}
