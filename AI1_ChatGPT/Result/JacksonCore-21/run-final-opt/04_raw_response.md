@org.junit.Test
public void matchedArrayWithPathIncludesArrayAndOuterObjectEnds() throws Exception {
    com.fasterxml.jackson.core.JsonParser source = new com.fasterxml.jackson.core.JsonFactory()
            .createParser("{\"a\":[1,2],\"ignored\":[3]}");
    com.fasterxml.jackson.core.filter.FilteringParserDelegate parser =
            new com.fasterxml.jackson.core.filter.FilteringParserDelegate(
                    source,
                    new com.fasterxml.jackson.core.filter.JsonPointerBasedFilter("/a"),
                    true,
                    false);

    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.START_OBJECT, parser.nextToken());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.FIELD_NAME, parser.nextToken());
    org.junit.Assert.assertEquals("a", parser.getCurrentName());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.START_ARRAY, parser.nextToken());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_INT, parser.nextToken());
    org.junit.Assert.assertEquals(1, parser.getIntValue());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_INT, parser.nextToken());
    org.junit.Assert.assertEquals(2, parser.getIntValue());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.END_ARRAY, parser.nextToken());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.END_OBJECT, parser.nextToken());
    org.junit.Assert.assertNull(parser.nextToken());
    parser.close();
}

@org.junit.Test
public void indexedArrayMatchWithPathSkipsOtherElementsAndClosesContexts() throws Exception {
    com.fasterxml.jackson.core.JsonParser source = new com.fasterxml.jackson.core.JsonFactory()
            .createParser("{\"a\":[1,2,3]}");
    com.fasterxml.jackson.core.filter.FilteringParserDelegate parser =
            new com.fasterxml.jackson.core.filter.FilteringParserDelegate(
                    source,
                    new com.fasterxml.jackson.core.filter.JsonPointerBasedFilter("/a/1"),
                    true,
                    false);

    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.START_OBJECT, parser.nextToken());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.FIELD_NAME, parser.nextToken());
    org.junit.Assert.assertEquals("a", parser.getCurrentName());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.START_ARRAY, parser.nextToken());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_INT, parser.nextToken());
    org.junit.Assert.assertEquals(2, parser.getIntValue());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.END_ARRAY, parser.nextToken());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.END_OBJECT, parser.nextToken());
    org.junit.Assert.assertNull(parser.nextToken());
    parser.close();
}