package com.fasterxml.jackson.core.filter;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class FilteringParserDelegateBug15Test
{
    private static class NumberAtLeastFilter extends TokenFilter {
        private final int minimum;

        NumberAtLeastFilter(int minimum) {
            this.minimum = minimum;
        }

        @Override
        public boolean includeValue(JsonParser parser) throws IOException {
            return parser.getIntValue() >= minimum;
        }
    }

    private static class PropertyFilter extends TokenFilter {
        private final String includedName;

        PropertyFilter(String includedName) {
            this.includedName = includedName;
        }

        @Override
        public TokenFilter includeProperty(String name) {
            return includedName.equals(name) ? TokenFilter.INCLUDE_ALL : null;
        }
    }

    private FilteringParserDelegate filtered(String json, TokenFilter filter,
            boolean includePath, boolean allowMultipleMatches) throws IOException {
        JsonParser parser = new JsonFactory().createParser(json);
        return new FilteringParserDelegate(parser, filter, includePath, allowMultipleMatches);
    }

    @Test
    public void disallowMultipleMatchesStopsAfterFirstMatchingScalar() throws Exception {
        FilteringParserDelegate parser = filtered("[1,2,3,4]", new NumberAtLeastFilter(3),
                false, false);

        assertNull(parser.getCurrentToken());
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals(3, parser.getIntValue());
        assertEquals(1, parser.getMatchCount());

        assertNull(parser.nextToken());
        assertFalse(parser.hasCurrentToken());
        assertEquals(1, parser.getMatchCount());
    }

    @Test
    public void allowingMultipleMatchesExposesAllMatchingScalars() throws Exception {
        FilteringParserDelegate parser = filtered("[1,2,3,4]", new NumberAtLeastFilter(3),
                false, true);

        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals(3, parser.getIntValue());
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals(4, parser.getIntValue());
        assertNull(parser.nextToken());
        assertEquals(2, parser.getMatchCount());
    }

    @Test
    public void nonMatchingInputProducesNoTokensAndNoMatches() throws Exception {
        FilteringParserDelegate parser = filtered("[1,2]", new NumberAtLeastFilter(3),
                false, false);

        assertNull(parser.nextToken());
        assertFalse(parser.hasCurrentToken());
        assertEquals(0, parser.getMatchCount());
    }

    @Test
    public void includePathBuffersObjectPathForMatchedProperty() throws Exception {
        FilteringParserDelegate parser = filtered(
                "{\"ignored\":1,\"target\":3}", new PropertyFilter("target"), true, false);

        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
        assertEquals("target", parser.getCurrentName());
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals(3, parser.getIntValue());
        assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        assertNull(parser.nextToken());
        assertEquals(1, parser.getMatchCount());
    }

    @Test
    public void nextValueAdvancesAcrossFilteredScalars() throws Exception {
        FilteringParserDelegate parser = filtered("[1,3,4]", new NumberAtLeastFilter(3),
                false, true);

        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextValue());
        assertEquals(3, parser.getIntValue());
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextValue());
        assertEquals(4, parser.getIntValue());
        assertNull(parser.nextValue());
    }

    @Test
    public void clearingCurrentFilteredTokenPreservesLastClearedToken() throws Exception {
        FilteringParserDelegate parser = filtered("[3,4]", new NumberAtLeastFilter(3),
                false, true);

        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertTrue(parser.hasCurrentToken());

        parser.clearCurrentToken();

        assertNull(parser.getCurrentToken());
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.getLastClearedToken());
        assertFalse(parser.hasCurrentToken());

        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals(4, parser.getIntValue());
        assertEquals(2, parser.getMatchCount());
    }
}