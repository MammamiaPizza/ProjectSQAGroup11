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
}