package com.fasterxml.jackson.core.json;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ReaderBasedJsonParserIssue510Test
{
    private JsonParser parserFor(String json) throws IOException {
        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        return factory.createParser(json);
    }

    @Test
    public void parsesUnquotedFieldNameContainingCharacter256() throws Exception {
        JsonParser parser = parserFor("{\u0100:true}");
        try {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals("\u0100", parser.getCurrentName());
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        } finally {
            parser.close();
        }
    }

    @Test
    public void parsesUnquotedFieldNameContainingCharacter255() throws Exception {
        JsonParser parser = parserFor("{\u00FF:1}");
        try {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals("\u00FF", parser.getCurrentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(1, parser.getIntValue());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        } finally {
            parser.close();
        }
    }

    @Test
    public void preservesNonLatin1CharactersWithinLongerUnquotedName() throws Exception {
        JsonParser parser = parserFor("{before\u0100after:\"value\"}");
        try {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals("before\u0100after", parser.getCurrentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("value", parser.getText());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        } finally {
            parser.close();
        }
    }

    @Test(expected = JsonParseException.class)
    public void rejectsUnquotedNonLatin1NameWhenFeatureIsDisabled() throws Exception {
        JsonParser parser = new JsonFactory().createParser("{\u0100:true}");
        try {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            parser.nextToken();
        } finally {
            parser.close();
        }
    }
}
