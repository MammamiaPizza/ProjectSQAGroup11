import java.io.ByteArrayInputStream;
import java.io.StringReader;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.json.ReaderBasedJsonParser;
import com.fasterxml.jackson.core.json.UTF8StreamJsonParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParserObjectLocationRegressionTest
{
    private final JsonFactory factory = new JsonFactory();

    @Test
    public void readerParserTracksTokenAndCurrentOffsetsAcrossObjectFields() throws Exception
    {
        JsonParser parser = factory.createParser(new StringReader("{\"a\":1,\"b\":2}"));
        try {
            assertTrue(parser instanceof ReaderBasedJsonParser);

            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(0L, parser.getTokenLocation().getCharOffset());

            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals("a", parser.getCurrentName());
            assertEquals(1L, parser.getTokenLocation().getCharOffset());
            assertEquals(6L, parser.getCurrentLocation().getCharOffset());

            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(5L, parser.getTokenLocation().getCharOffset());

            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals("b", parser.getCurrentName());
            assertEquals(7L, parser.getTokenLocation().getCharOffset());
            assertEquals(12L, parser.getCurrentLocation().getCharOffset());

            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(11L, parser.getTokenLocation().getCharOffset());

            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertEquals(12L, parser.getTokenLocation().getCharOffset());
            assertEquals(13L, parser.getCurrentLocation().getCharOffset());
        } finally {
            parser.close();
        }
    }

    @Test
    public void utf8StreamParserTracksTokenAndCurrentOffsetsAcrossObjectFields() throws Exception
    {
        byte[] document = "{\"a\":1,\"b\":2}".getBytes("UTF-8");
        JsonParser parser = factory.createParser(new ByteArrayInputStream(document));
        try {
            assertTrue(parser instanceof UTF8StreamJsonParser);

            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(0L, parser.getTokenLocation().getByteOffset());

            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals("a", parser.getCurrentName());
            assertEquals(1L, parser.getTokenLocation().getByteOffset());
            assertEquals(6L, parser.getCurrentLocation().getByteOffset());

            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(5L, parser.getTokenLocation().getByteOffset());

            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals("b", parser.getCurrentName());
            assertEquals(7L, parser.getTokenLocation().getByteOffset());
            assertEquals(12L, parser.getCurrentLocation().getByteOffset());

            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(11L, parser.getTokenLocation().getByteOffset());

            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertEquals(12L, parser.getTokenLocation().getByteOffset());
            assertEquals(13L, parser.getCurrentLocation().getByteOffset());
        } finally {
            parser.close();
        }
    }

    @Test(expected = JsonParseException.class)
    public void readerParserRejectsObjectFieldWithoutColon() throws Exception
    {
        JsonParser parser = factory.createParser(new StringReader("{\"a\" 1}"));
        try {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            parser.nextToken();
        } finally {
            parser.close();
        }
    }

    @Test
    public void tokenLocationRetainsFieldStartWhenWhitespacePrecedesLaterField() throws Exception
    {
        JsonParser parser = factory.createParser(new StringReader("{\"a\":1, \"b\":2}"));
        try {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());

            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            JsonLocation tokenLocation = parser.getTokenLocation();
            assertEquals(8L, tokenLocation.getCharOffset());
            assertEquals(13L, parser.getCurrentLocation().getCharOffset());
        } finally {
            parser.close();
        }
    }
}
