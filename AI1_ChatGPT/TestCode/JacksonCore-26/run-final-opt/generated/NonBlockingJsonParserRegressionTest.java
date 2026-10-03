import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.async.ByteArrayFeeder;
import com.fasterxml.jackson.core.json.async.NonBlockingJsonParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class NonBlockingJsonParserRegressionTest
{
    private NonBlockingJsonParser newParser() throws IOException {
        return (NonBlockingJsonParser) new JsonFactory().createNonBlockingByteArrayParser();
    }

    private void assertToken(JsonToken expected, JsonToken actual) {
        assertEquals(expected, actual);
    }

    @Test
    public void testLocationOffsetsUseFedRangeRatherThanArrayIndexes() throws Exception {
        NonBlockingJsonParser parser = newParser();
        ByteArrayFeeder feeder = parser.getNonBlockingInputFeeder();
        byte[] input = "xx[  1 ]".getBytes("UTF-8");

        try {
            assertEquals(0L, parser.getCurrentLocation().getByteOffset());

            feeder.feedInput(input, 2, 5);
            assertToken(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(1L, parser.getCurrentLocation().getByteOffset());

            assertToken(JsonToken.NOT_AVAILABLE, parser.nextToken());
            assertEquals(3L, parser.getCurrentLocation().getByteOffset());
            assertTrue(feeder.needMoreInput());

            feeder.feedInput(input, 5, input.length);
            assertToken(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(1, parser.getIntValue());
            assertEquals(4L, parser.getCurrentLocation().getByteOffset());

            assertToken(JsonToken.END_ARRAY, parser.nextToken());
            assertEquals(6L, parser.getCurrentLocation().getByteOffset());

            assertTrue(feeder.needMoreInput());
            feeder.endOfInput();
            assertNull(parser.nextToken());
        } finally {
            parser.close();
        }
    }

    @Test
    public void testObjectTokensAcrossChunkBoundaries() throws Exception {
        NonBlockingJsonParser parser = newParser();
        ByteArrayFeeder feeder = parser.getNonBlockingInputFeeder();

        try {
            assertTrue(feeder.needMoreInput());

            feeder.feedInput("{".getBytes("UTF-8"), 0, 1);
            assertToken(JsonToken.START_OBJECT, parser.nextToken());
            assertTrue(feeder.needMoreInput());

            byte[] nameAndColon = "\"a\":".getBytes("UTF-8");
            feeder.feedInput(nameAndColon, 0, nameAndColon.length);
            assertToken(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals("a", parser.getCurrentName());
            assertToken(JsonToken.NOT_AVAILABLE, parser.nextToken());
            assertTrue(feeder.needMoreInput());

            byte[] valueAndEnd = "true}".getBytes("UTF-8");
            feeder.feedInput(valueAndEnd, 0, valueAndEnd.length);
            assertToken(JsonToken.VALUE_TRUE, parser.nextToken());
            assertToken(JsonToken.END_OBJECT, parser.nextToken());

            assertTrue(feeder.needMoreInput());
            feeder.endOfInput();
            assertNull(parser.nextToken());
        } finally {
            parser.close();
        }
    }

    @Test
    public void testUtf8CharacterCanBeSplitAcrossIndividualBytes() throws Exception {
        NonBlockingJsonParser parser = newParser();
        ByteArrayFeeder feeder = parser.getNonBlockingInputFeeder();
        byte[] document = "[\"\u20ac\"]".getBytes("UTF-8");

        try {
            feeder.feedInput(document, 0, 2);
            assertToken(JsonToken.START_ARRAY, parser.nextToken());
            assertToken(JsonToken.NOT_AVAILABLE, parser.nextToken());
            assertTrue(feeder.needMoreInput());

            feeder.feedInput(document, 2, 3);
            assertToken(JsonToken.NOT_AVAILABLE, parser.nextToken());
            assertTrue(feeder.needMoreInput());

            feeder.feedInput(document, 3, 4);
            assertToken(JsonToken.NOT_AVAILABLE, parser.nextToken());
            assertTrue(feeder.needMoreInput());

            feeder.feedInput(document, 4, 5);
            assertToken(JsonToken.NOT_AVAILABLE, parser.nextToken());
            assertTrue(feeder.needMoreInput());

            feeder.feedInput(document, 5, document.length);
            assertToken(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("\u20ac", parser.getText());
            assertToken(JsonToken.END_ARRAY, parser.nextToken());

            feeder.endOfInput();
            assertNull(parser.nextToken());
        } finally {
            parser.close();
        }
    }

    @Test(expected = IOException.class)
    public void testCannotReplaceInputBeforePreviousInputIsConsumed() throws Exception {
        NonBlockingJsonParser parser = newParser();
        try {
            ByteArrayFeeder feeder = parser.getNonBlockingInputFeeder();
            byte[] first = "[".getBytes("UTF-8");
            byte[] second = "]".getBytes("UTF-8");

            feeder.feedInput(first, 0, first.length);
            assertFalse(feeder.needMoreInput());
            feeder.feedInput(second, 0, second.length);
        } finally {
            parser.close();
        }
    }
}
