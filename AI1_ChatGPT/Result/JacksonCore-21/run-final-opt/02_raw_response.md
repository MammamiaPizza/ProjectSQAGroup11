package com.fasterxml.jackson.core.filter;

import java.io.StringWriter;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class FilteringParserDelegateBug330Test
{
    private static class ObValueFilter extends TokenFilter {
        private final int level;

        ObValueFilter() {
            this(0);
        }

        private ObValueFilter(int level) {
            this.level = level;
        }

        @Override
        public TokenFilter includeProperty(String name) {
            if (level == 0 && "ob".equals(name)) {
                return new ObValueFilter(1);
            }
            if (level == 1 && "value".equals(name)) {
                return TokenFilter.INCLUDE_ALL;
            }
            return null;
        }
    }

    private FilteringParserDelegate filteredParser(String json) throws Exception {
        JsonParser source = new JsonFactory().createParser(json);
        return new FilteringParserDelegate(source, new ObValueFilter(), true, false);
    }

    @Test
    public void singleNestedMatchWithPathIncludesBothObjectEnds() throws Exception {
        FilteringParserDelegate parser = filteredParser(
                "{\"ob\":{\"value\":3,\"ignored\":4},\"other\":5}");
        try {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals("ob", parser.getCurrentName());
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals("value", parser.getCurrentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(3, parser.getIntValue());

            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        } finally {
            parser.close();
        }
    }

    @Test
    public void serializedPathExposedSingleMatchHasFinalOuterClose() throws Exception {
        FilteringParserDelegate parser = filteredParser(
                "{\"ob\":{\"value\":3,\"ignored\":4},\"other\":5}");
        StringWriter output = new StringWriter();
        JsonGenerator generator = new JsonFactory().createGenerator(output);
        try {
            JsonToken token;
            while ((token = parser.nextToken()) != null) {
                generator.copyCurrentEvent(parser);
            }
            generator.close();

            assertEquals("{\"ob\":{\"value\":3}}", output.toString());
        } finally {
            parser.close();
            if (!generator.isClosed()) {
                generator.close();
            }
        }
    }

    @Test
    public void skipChildrenAtMatchedObjectStillLeavesEnclosingEndObject() throws Exception {
        FilteringParserDelegate parser = filteredParser(
                "{\"ob\":{\"value\":3},\"other\":5}");
        try {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());

            assertSame(parser, parser.skipChildren());
            assertEquals(JsonToken.END_OBJECT, parser.currentToken());

            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        } finally {
            parser.close();
        }
    }

    @Test
    public void currentTokenStateRemainsValidThroughFinalEndAndEof() throws Exception {
        FilteringParserDelegate parser = filteredParser("{\"ob\":{\"value\":3}}");
        try {
            JsonToken token;
            do {
                token = parser.nextToken();
            } while (token != JsonToken.END_OBJECT || !parser.getParsingContext().inRoot());

            assertEquals(JsonToken.END_OBJECT, parser.currentToken());
            assertTrue(parser.hasCurrentToken());
            assertTrue(parser.hasToken(JsonToken.END_OBJECT));

            assertNull(parser.nextToken());
            assertNull(parser.currentToken());
            assertFalse(parser.hasCurrentToken());
            assertNull(parser.nextToken());
        } finally {
            parser.close();
        }
    }

    @Test
    public void noMatchingPropertyProducesNoPathTokens() throws Exception {
        FilteringParserDelegate parser = filteredParser("{\"ob\":{\"other\":3},\"value\":4}");
        try {
            assertNull(parser.nextToken());
            assertFalse(parser.hasCurrentToken());
        } finally {
            parser.close();
        }
    }
}