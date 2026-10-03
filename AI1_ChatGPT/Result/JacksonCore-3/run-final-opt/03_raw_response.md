package com.fasterxml.jackson.core.json;

import java.io.ByteArrayInputStream;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.sym.BytesToNameCanonicalizer;
import com.fasterxml.jackson.core.util.BufferRecycler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UTF8StreamJsonParserInputOffsetTest
{
    private UTF8StreamJsonParser parserFor(byte[] buffer, int start) {
        IOContext context = new IOContext(new BufferRecycler(), "offset-test", false);
        return new UTF8StreamJsonParser(
                context,
                JsonParser.Feature.collectDefaults(),
                new ByteArrayInputStream(new byte[0]),
                null,
                BytesToNameCanonicalizer.createRoot(),
                buffer,
                start,
                buffer.length,
                false);
    }

    @Test
    public void testFirstTokenLocationExcludesInitialBufferOffset() throws Exception {
        byte[] input = "xxx[1]".getBytes("UTF-8");
        UTF8StreamJsonParser parser = parserFor(input, 3);

        assertEquals(JsonToken.START_ARRAY, parser.nextToken());

        JsonLocation location = parser.getTokenLocation();
        assertEquals(0L, location.getByteOffset());
    }

    @Test
    public void testTokenAndCurrentLocationsRemainRelativeAfterOffsetBuffer() throws Exception {
        byte[] input = "prefix[1,2]".getBytes("UTF-8");
        UTF8StreamJsonParser parser = parserFor(input, 6);

        assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        assertEquals(0L, parser.getTokenLocation().getByteOffset());
        assertEquals(1L, parser.getCurrentLocation().getByteOffset());

        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals(1L, parser.getTokenLocation().getByteOffset());
        assertEquals(2L, parser.getCurrentLocation().getByteOffset());

        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals(3L, parser.getTokenLocation().getByteOffset());
        assertEquals(4L, parser.getCurrentLocation().getByteOffset());

        assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        assertEquals(4L, parser.getTokenLocation().getByteOffset());
        assertEquals(5L, parser.getCurrentLocation().getByteOffset());

        assertNull(parser.nextToken());
    }

    @Test
    public void testWhitespaceBeforeTokenUsesSourceRelativeLocation() throws Exception {
        byte[] input = "skip \n true".getBytes("UTF-8");
        UTF8StreamJsonParser parser = parserFor(input, 4);

        assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());

        assertEquals(3L, parser.getTokenLocation().getByteOffset());
        assertEquals(7L, parser.getCurrentLocation().getByteOffset());
        assertNull(parser.nextToken());
    }
}