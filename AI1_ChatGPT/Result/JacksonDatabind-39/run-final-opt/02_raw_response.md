import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.deser.std.NullifyingDeserializer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class NullifyingDeserializerTest {

    @Test
    public void deserializeSkipsRemainingObjectContentWhenStartedAtUnknownScalarField()
            throws IOException {
        JsonParser parser = new JsonFactory()
                .createParser("{\"location\":\"unknown-place\",\"application\":\"app\"}");
        try {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals("location", parser.getCurrentName());

            assertNull(NullifyingDeserializer.instance.deserialize(parser, null));

            assertNull(parser.nextToken());
        } finally {
            parser.close();
        }
    }

    @Test
    public void deserializeSkipsNestedUnknownLocationContentWhenStartedAtFieldName()
            throws IOException {
        JsonParser parser = new JsonFactory().createParser(
                "{\"location\":{\"coordinates\":[12,34],\"details\":{\"country\":\"US\"}},"
                        + "\"application\":\"app\",\"version\":1}");
        try {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals("location", parser.getCurrentName());

            assertNull(NullifyingDeserializer.instance.deserialize(parser, null));

            assertNull(parser.nextToken());
        } finally {
            parser.close();
        }
    }

    @Test
    public void deserializeSkipsObjectValueAndLeavesParserAtMatchingEndObject()
            throws IOException {
        JsonParser parser = new JsonFactory().createParser(
                "{\"location\":{\"name\":\"somewhere\",\"values\":[1,2,3]}}");
        try {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());

            assertNull(NullifyingDeserializer.instance.deserialize(parser, null));

            assertEquals(JsonToken.END_OBJECT, parser.getCurrentToken());
            assertNull(parser.nextToken());
        } finally {
            parser.close();
        }
    }

    @Test
    public void deserializeSkipsArrayValueAndLeavesParserAtMatchingEndArray()
            throws IOException {
        JsonParser parser = new JsonFactory().createParser(
                "[{\"location\":\"first\"},{\"location\":\"second\"}]");
        try {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());

            assertNull(NullifyingDeserializer.instance.deserialize(parser, null));

            assertEquals(JsonToken.END_ARRAY, parser.getCurrentToken());
            assertNull(parser.nextToken());
        } finally {
            parser.close();
        }
    }

    @Test
    public void deserializeReturnsNullForScalarWithoutAdvancingPastScalar()
            throws IOException {
        JsonParser parser = new JsonFactory().createParser("\"ignored\"");
        try {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());

            assertNull(NullifyingDeserializer.instance.deserialize(parser, null));

            assertEquals(JsonToken.VALUE_STRING, parser.getCurrentToken());
            assertNull(parser.nextToken());
        } finally {
            parser.close();
        }
    }
}