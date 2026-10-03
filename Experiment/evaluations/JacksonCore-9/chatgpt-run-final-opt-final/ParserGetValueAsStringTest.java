package com.fasterxml.jackson.core.json;

import java.io.ByteArrayInputStream;
import java.io.StringReader;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParserGetValueAsStringTest
{
    @Test
    public void readerBasedParserReturnsStringValueAsText() throws Exception
    {
        JsonParser parser = new JsonFactory().createParser(new StringReader("\"a\""));
        try {
            assertTrue(parser instanceof ReaderBasedJsonParser);
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("a", parser.getValueAsString());
            assertEquals("a", parser.getValueAsString("default"));
        } finally {
            parser.close();
        }
    }

    @Test
    public void utf8StreamParserReturnsStringValueAsText() throws Exception
    {
        JsonParser parser = new JsonFactory().createParser(
                new ByteArrayInputStream(new byte[] { '"', 'a', '"' }));
        try {
            assertTrue(parser instanceof UTF8StreamJsonParser);
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("a", parser.getValueAsString());
            assertEquals("a", parser.getValueAsString("default"));
        } finally {
            parser.close();
        }
    }

@org.junit.Test
public void readerBasedParserReturnsStringFromDefaultValueAccessorBeforeTextIsRead() throws Exception {
    com.fasterxml.jackson.core.JsonParser parser = new com.fasterxml.jackson.core.JsonFactory()
            .createParser(new java.io.StringReader("\"a\""));
    try {
        org.junit.Assert.assertTrue(parser instanceof com.fasterxml.jackson.core.json.ReaderBasedJsonParser);
        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_STRING, parser.nextToken());
        org.junit.Assert.assertEquals("a", parser.getValueAsString("default"));
    } finally {
        parser.close();
    }
}

@org.junit.Test
public void utf8StreamParserReturnsStringFromDefaultValueAccessorBeforeTextIsRead() throws Exception {
    com.fasterxml.jackson.core.JsonParser parser = new com.fasterxml.jackson.core.JsonFactory()
            .createParser(new java.io.ByteArrayInputStream("\"a\"".getBytes("UTF-8")));
    try {
        org.junit.Assert.assertTrue(parser instanceof com.fasterxml.jackson.core.json.UTF8StreamJsonParser);
        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_STRING, parser.nextToken());
        org.junit.Assert.assertEquals("a", parser.getValueAsString("default"));
    } finally {
        parser.close();
    }
}
}
