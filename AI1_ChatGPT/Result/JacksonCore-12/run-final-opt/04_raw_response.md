@org.junit.Test
public void readerParserTracksNestedObjectFieldOffsets() throws Exception {
    com.fasterxml.jackson.core.JsonParser parser = new com.fasterxml.jackson.core.JsonFactory()
            .createParser(new java.io.StringReader("{\"outer\":{\"inner\":2}}"));
    try {
        org.junit.Assert.assertTrue(parser instanceof com.fasterxml.jackson.core.json.ReaderBasedJsonParser);
        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.START_OBJECT, parser.nextToken());
        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.FIELD_NAME, parser.nextToken());
        org.junit.Assert.assertEquals(1L, parser.getTokenLocation().getCharOffset());
        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.START_OBJECT, parser.nextToken());
        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.FIELD_NAME, parser.nextToken());
        org.junit.Assert.assertEquals("inner", parser.getCurrentName());
        org.junit.Assert.assertEquals(10L, parser.getTokenLocation().getCharOffset());
    } finally {
        parser.close();
    }
}

@org.junit.Test
public void utf8StreamParserTracksNestedObjectFieldOffsets() throws Exception {
    com.fasterxml.jackson.core.JsonParser parser = new com.fasterxml.jackson.core.JsonFactory()
            .createParser(new java.io.ByteArrayInputStream("{\"outer\":{\"inner\":2}}".getBytes("UTF-8")));
    try {
        org.junit.Assert.assertTrue(parser instanceof com.fasterxml.jackson.core.json.UTF8StreamJsonParser);
        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.START_OBJECT, parser.nextToken());
        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.FIELD_NAME, parser.nextToken());
        org.junit.Assert.assertEquals(1L, parser.getTokenLocation().getCharOffset());
        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.START_OBJECT, parser.nextToken());
        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.FIELD_NAME, parser.nextToken());
        org.junit.Assert.assertEquals("inner", parser.getCurrentName());
        org.junit.Assert.assertEquals(10L, parser.getTokenLocation().getCharOffset());
    } finally {
        parser.close();
    }
}

@org.junit.Test
public void utf8StreamParserRetainsFieldStartAfterWhitespace() throws Exception {
    com.fasterxml.jackson.core.JsonParser parser = new com.fasterxml.jackson.core.JsonFactory()
            .createParser(new java.io.ByteArrayInputStream("{\"a\":1,  \"b\":2}".getBytes("UTF-8")));
    try {
        org.junit.Assert.assertTrue(parser instanceof com.fasterxml.jackson.core.json.UTF8StreamJsonParser);
        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.START_OBJECT, parser.nextToken());
        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.FIELD_NAME, parser.nextToken());
        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.FIELD_NAME, parser.nextToken());
        org.junit.Assert.assertEquals("b", parser.getCurrentName());
        org.junit.Assert.assertEquals(9L, parser.getTokenLocation().getCharOffset());
    } finally {
        parser.close();
    }
}