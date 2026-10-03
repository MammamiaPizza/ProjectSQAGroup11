package com.fasterxml.jackson.core.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class FilteringParserDelegateBug22Test
{
    private static class PropertyFilter extends TokenFilter {
        private final String property;

        PropertyFilter(String property) {
            this.property = property;
        }

        @Override
        public TokenFilter includeRootValue(int index) {
            return this;
        }

        @Override
        public TokenFilter filterStartObject() {
            return this;
        }

        @Override
        public TokenFilter filterStartArray() {
            return this;
        }

        @Override
        public TokenFilter includeProperty(String name) {
            return property.equals(name) ? TokenFilter.INCLUDE_ALL : null;
        }
    }

    private static class DescendingPropertyFilter extends TokenFilter {
        private final String property;

        DescendingPropertyFilter(String property) {
            this.property = property;
        }

        @Override
        public TokenFilter includeRootValue(int index) {
            return this;
        }

        @Override
        public TokenFilter filterStartObject() {
            return this;
        }

        @Override
        public TokenFilter filterStartArray() {
            return this;
        }

        @Override
        public TokenFilter includeProperty(String name) {
            return property.equals(name) ? TokenFilter.INCLUDE_ALL : this;
        }
    }

    private static class IndexFilter extends TokenFilter {
        private final int[] indexes;

        IndexFilter(int... indexes) {
            this.indexes = indexes;
        }

        @Override
        public TokenFilter includeRootValue(int index) {
            return this;
        }

        @Override
        public TokenFilter filterStartArray() {
            return this;
        }

        @Override
        public TokenFilter includeElement(int index) {
            for (int allowed : indexes) {
                if (allowed == index) {
                    return TokenFilter.INCLUDE_ALL;
                }
            }
            return null;
        }
    }

    private FilteringParserDelegate parser(String json, TokenFilter filter,
            boolean includePath, boolean allowMultipleMatches) throws IOException {
        JsonParser source = new JsonFactory().createParser(json);
        return new FilteringParserDelegate(source, filter, includePath, allowMultipleMatches);
    }

    private List<JsonToken> readAll(FilteringParserDelegate parser) throws IOException {
        List<JsonToken> tokens = new ArrayList<JsonToken>();
        JsonToken token;
        while ((token = parser.nextToken()) != null) {
            tokens.add(token);
        }
        return tokens;
    }

    @Test
    public void singlePropertyMatchWithPathExposesCompleteObjectPath() throws Exception {
        FilteringParserDelegate parser = parser("{\"skip\":0,\"target\":1}",
                new PropertyFilter("target"), true, false);

        assertEquals(Arrays.asList(
                JsonToken.START_OBJECT,
                JsonToken.FIELD_NAME,
                JsonToken.VALUE_NUMBER_INT,
                JsonToken.END_OBJECT), readAll(parser));
        assertEquals(1, parser.getMatchCount());
        assertNull(parser.currentToken());
    }

    @Test
    public void multiplePropertyMatchesWithPathAreAllExposedWhenAllowed() throws Exception {
        FilteringParserDelegate parser = parser("{\"target\":1,\"skip\":0,\"target\":2}",
                new PropertyFilter("target"), true, true);

        assertEquals(Arrays.asList(
                JsonToken.START_OBJECT,
                JsonToken.FIELD_NAME,
                JsonToken.VALUE_NUMBER_INT,
                JsonToken.FIELD_NAME,
                JsonToken.VALUE_NUMBER_INT,
                JsonToken.END_OBJECT), readAll(parser));
        assertEquals(2, parser.getMatchCount());
    }

    @Test
    public void multiplePropertyMatchesWithPathStopAfterFirstWhenNotAllowed() throws Exception {
        FilteringParserDelegate parser = parser("{\"target\":1,\"target\":2}",
                new PropertyFilter("target"), true, false);

        List<JsonToken> tokens = readAll(parser);
        int valueCount = 0;
        for (JsonToken token : tokens) {
            if (token == JsonToken.VALUE_NUMBER_INT) {
                ++valueCount;
            }
        }

        assertEquals(1, valueCount);
        assertEquals(1, parser.getMatchCount());
    }

    @Test
    public void singlePropertyMatchWithoutPathReturnsOnlyMatchedValue() throws Exception {
        FilteringParserDelegate parser = parser("{\"skip\":0,\"target\":13}",
                new PropertyFilter("target"), false, false);

        assertEquals(Arrays.asList(JsonToken.VALUE_NUMBER_INT), readAll(parser));
        assertEquals(1, parser.getMatchCount());
        assertFalse(parser.hasCurrentToken());
    }

    @Test
    public void multiplePropertyMatchesWithoutPathAreReturnedWhenAllowed() throws Exception {
        FilteringParserDelegate parser = parser("{\"target\":1,\"skip\":0,\"target\":2}",
                new PropertyFilter("target"), false, true);

        assertEquals(Arrays.asList(
                JsonToken.VALUE_NUMBER_INT,
                JsonToken.VALUE_NUMBER_INT), readAll(parser));
        assertEquals(2, parser.getMatchCount());
    }

    @Test
    public void multiplePropertyMatchesWithoutPathStopAfterFirstWhenNotAllowed() throws Exception {
        FilteringParserDelegate parser = parser("{\"target\":1,\"target\":2}",
                new PropertyFilter("target"), false, false);

        assertEquals(Arrays.asList(JsonToken.VALUE_NUMBER_INT), readAll(parser));
        assertEquals(1, parser.getMatchCount());
    }

    @Test
    public void nestedPropertyMatchWithPathIncludesAllAncestorStructure() throws Exception {
        FilteringParserDelegate parser = parser("{\"outer\":{\"target\":7,\"skip\":8}}",
                new DescendingPropertyFilter("target"), true, false);

        assertEquals(Arrays.asList(
                JsonToken.START_OBJECT,
                JsonToken.FIELD_NAME,
                JsonToken.START_OBJECT,
                JsonToken.FIELD_NAME,
                JsonToken.VALUE_NUMBER_INT,
                JsonToken.END_OBJECT,
                JsonToken.END_OBJECT), readAll(parser));
        assertEquals(1, parser.getMatchCount());
    }

    @Test
    public void selectedArrayIndexWithPathIncludesArrayBoundaries() throws Exception {
        FilteringParserDelegate parser = parser("[10,11,12]",
                new IndexFilter(1), true, false);

        assertEquals(Arrays.asList(
                JsonToken.START_ARRAY,
                JsonToken.VALUE_NUMBER_INT,
                JsonToken.END_ARRAY), readAll(parser));
        assertEquals(1, parser.getMatchCount());
    }

    @Test
    public void multipleSelectedArrayIndexesWithPathAreReturnedWhenAllowed() throws Exception {
        FilteringParserDelegate parser = parser("[10,11,12,13]",
                new IndexFilter(1, 3), true, true);

        assertEquals(Arrays.asList(
                JsonToken.START_ARRAY,
                JsonToken.VALUE_NUMBER_INT,
                JsonToken.VALUE_NUMBER_INT,
                JsonToken.END_ARRAY), readAll(parser));
        assertEquals(2, parser.getMatchCount());
    }

    @Test
    public void noMatchingPropertyProducesNoTokensAndNoMatches() throws Exception {
        FilteringParserDelegate parser = parser("{\"a\":1,\"b\":2}",
                new PropertyFilter("target"), true, true);

        assertEquals(0, readAll(parser).size());
        assertEquals(0, parser.getMatchCount());
        assertNull(parser.currentToken());
        assertFalse(parser.hasCurrentToken());
    }
}