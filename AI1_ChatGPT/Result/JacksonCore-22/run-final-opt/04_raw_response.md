@org.junit.Test
public void selectedArrayPropertyWithPathExposesCompleteArrayAndSkipsUnselectedArray() throws Exception {
    com.fasterxml.jackson.core.JsonParser source = new com.fasterxml.jackson.core.JsonFactory()
            .createParser("{\"keep\":[1,2],\"drop\":[3]}");
    com.fasterxml.jackson.core.filter.FilteringParserDelegate parser =
            new com.fasterxml.jackson.core.filter.FilteringParserDelegate(source,
                    new com.fasterxml.jackson.core.filter.TokenFilter() {
                        @Override
                        public com.fasterxml.jackson.core.filter.TokenFilter includeProperty(String name) {
                            return "keep".equals(name)
                                    ? com.fasterxml.jackson.core.filter.TokenFilter.INCLUDE_ALL : null;
                        }
                    }, true, true);

    java.util.List<com.fasterxml.jackson.core.JsonToken> tokens =
            new java.util.ArrayList<com.fasterxml.jackson.core.JsonToken>();
    com.fasterxml.jackson.core.JsonToken token;
    while ((token = parser.nextToken()) != null) {
        tokens.add(token);
    }

    org.junit.Assert.assertEquals(java.util.Arrays.asList(
            com.fasterxml.jackson.core.JsonToken.START_OBJECT,
            com.fasterxml.jackson.core.JsonToken.FIELD_NAME,
            com.fasterxml.jackson.core.JsonToken.START_ARRAY,
            com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_INT,
            com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_INT,
            com.fasterxml.jackson.core.JsonToken.END_ARRAY,
            com.fasterxml.jackson.core.JsonToken.END_OBJECT), tokens);
    org.junit.Assert.assertEquals(1, parser.getMatchCount());
}

@org.junit.Test
public void nextValueReturnsSelectedScalarWhenFieldNamesAreNotIncluded() throws Exception {
    com.fasterxml.jackson.core.JsonParser source = new com.fasterxml.jackson.core.JsonFactory()
            .createParser("{\"drop\":0,\"keep\":3}");
    com.fasterxml.jackson.core.filter.FilteringParserDelegate parser =
            new com.fasterxml.jackson.core.filter.FilteringParserDelegate(source,
                    new com.fasterxml.jackson.core.filter.TokenFilter() {
                        @Override
                        public com.fasterxml.jackson.core.filter.TokenFilter includeProperty(String name) {
                            return "keep".equals(name)
                                    ? com.fasterxml.jackson.core.filter.TokenFilter.INCLUDE_ALL : null;
                        }
                    }, false, true);

    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_INT, parser.nextValue());
    org.junit.Assert.assertEquals(3, parser.getIntValue());
    org.junit.Assert.assertNull(parser.nextValue());
    org.junit.Assert.assertEquals(1, parser.getMatchCount());
}

@org.junit.Test
public void includeAllFilterRetainsNestedStructure() throws Exception {
    com.fasterxml.jackson.core.JsonParser source = new com.fasterxml.jackson.core.JsonFactory()
            .createParser("{\"a\":[1]}");
    com.fasterxml.jackson.core.filter.FilteringParserDelegate parser =
            new com.fasterxml.jackson.core.filter.FilteringParserDelegate(source,
                    com.fasterxml.jackson.core.filter.TokenFilter.INCLUDE_ALL, false, true);

    java.util.List<com.fasterxml.jackson.core.JsonToken> tokens =
            new java.util.ArrayList<com.fasterxml.jackson.core.JsonToken>();
    com.fasterxml.jackson.core.JsonToken token;
    while ((token = parser.nextToken()) != null) {
        tokens.add(token);
    }

    org.junit.Assert.assertEquals(java.util.Arrays.asList(
            com.fasterxml.jackson.core.JsonToken.START_OBJECT,
            com.fasterxml.jackson.core.JsonToken.FIELD_NAME,
            com.fasterxml.jackson.core.JsonToken.START_ARRAY,
            com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_INT,
            com.fasterxml.jackson.core.JsonToken.END_ARRAY,
            com.fasterxml.jackson.core.JsonToken.END_OBJECT), tokens);
}

@org.junit.Test
public void clearCurrentTokenOnIncludedContentAllowsReadingFollowingToken() throws Exception {
    com.fasterxml.jackson.core.JsonParser source = new com.fasterxml.jackson.core.JsonFactory()
            .createParser("{\"a\":[1]}");
    com.fasterxml.jackson.core.filter.FilteringParserDelegate parser =
            new com.fasterxml.jackson.core.filter.FilteringParserDelegate(source,
                    com.fasterxml.jackson.core.filter.TokenFilter.INCLUDE_ALL, false, true);

    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.START_OBJECT, parser.nextToken());
    parser.clearCurrentToken();

    org.junit.Assert.assertFalse(parser.hasCurrentToken());
    org.junit.Assert.assertNull(parser.currentToken());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.START_OBJECT, parser.getLastClearedToken());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.FIELD_NAME, parser.nextToken());
    org.junit.Assert.assertEquals("a", parser.getCurrentName());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.START_ARRAY, parser.nextToken());
}