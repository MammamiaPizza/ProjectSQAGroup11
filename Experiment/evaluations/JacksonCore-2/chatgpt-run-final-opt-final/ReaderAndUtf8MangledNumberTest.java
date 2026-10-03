package com.fasterxml.jackson.core.json;

import java.io.ByteArrayInputStream;
import java.io.StringReader;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ReaderAndUtf8MangledNumberTest
{
    private final JsonFactory factory = new JsonFactory();

    @Test
    public void readerParserRejectsDecimalPointWithoutFractionDigits() throws Exception {
        assertReaderRejects("12.");
    }

    @Test
    public void utf8ParserRejectsDecimalPointWithoutFractionDigits() throws Exception {
        assertUtf8Rejects("12.");
    }

    @Test
    public void readerParserRejectsIncompleteExponents() throws Exception {
        assertReaderRejects("12e");
        assertReaderRejects("12e+");
        assertReaderRejects("12e-");
    }

    @Test
    public void utf8ParserRejectsIncompleteExponents() throws Exception {
        assertUtf8Rejects("12e");
        assertUtf8Rejects("12e+");
        assertUtf8Rejects("12e-");
    }

    @Test
    public void readerParserRejectsLoneMinusSign() throws Exception {
        assertReaderRejects("-");
    }

    @Test
    public void utf8ParserRejectsLoneMinusSign() throws Exception {
        assertUtf8Rejects("-");
    }

    @Test
    public void readerParserParsesZeroAndExponentNumberInArray() throws Exception {
        JsonParser parser = factory.createParser(new StringReader("[0,-12.5e+2]"));
        try {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals("0", parser.getText());
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals("-12.5e+2", parser.getText());
            assertEquals(-1250.0d, parser.getDoubleValue(), 0.0d);
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertEquals(null, parser.nextToken());
        } finally {
            parser.close();
        }
    }

    @Test
    public void utf8ParserParsesZeroAndExponentNumberInArray() throws Exception {
        JsonParser parser = factory.createParser(
                new ByteArrayInputStream("[0,-12.5e+2]".getBytes("UTF-8")));
        try {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals("0", parser.getText());
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals("-12.5e+2", parser.getText());
            assertEquals(-1250.0d, parser.getDoubleValue(), 0.0d);
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertEquals(null, parser.nextToken());
        } finally {
            parser.close();
        }
    }

    private void assertReaderRejects(String input) throws Exception {
        JsonParser parser = factory.createParser(new StringReader(input));
        try {
            try {
                JsonToken token = parser.nextToken();
                fail("Expected malformed number \"" + input
                        + "\" to fail, but got token " + token);
            } catch (JsonParseException e) {
                assertNotNull(e.getMessage());
            }
        } finally {
            parser.close();
        }
    }

    private void assertUtf8Rejects(String input) throws Exception {
        JsonParser parser = factory.createParser(
                new ByteArrayInputStream(input.getBytes("UTF-8")));
        try {
            try {
                JsonToken token = parser.nextToken();
                fail("Expected malformed UTF-8 number \"" + input
                        + "\" to fail, but got token " + token);
            } catch (JsonParseException e) {
                assertNotNull(e.getMessage());
            }
        } finally {
            parser.close();
        }
    }

@org.junit.Test
public void readerParserRejectsLeadingZeroNumbers() throws Exception {
    assertReaderRejects("00");
    assertReaderRejects("-00");
    assertReaderRejects("00.0");
    assertReaderRejects("00e1");
}

@org.junit.Test
public void utf8ParserRejectsLeadingZeroNumbers() throws Exception {
    assertUtf8Rejects("00");
    assertUtf8Rejects("-00");
    assertUtf8Rejects("00.0");
    assertUtf8Rejects("00e1");
}
}
