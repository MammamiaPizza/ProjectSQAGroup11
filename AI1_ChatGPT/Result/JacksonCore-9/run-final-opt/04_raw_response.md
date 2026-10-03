@org.junit.Test
public void readerBasedParserReturnsStringFromDefaultValueAccessorBeforeTextIsRead() throws Exception {
    com.fasterxml.jackson.core.JsonParser parser = new com.fasterxml.jackson.core.JsonFactory()
            .createParser(new java.io.StringReader("\"a\""));
    try {
        org.junit.Assert.assertTrue(parser instanceof com.fasterxml.jackson.core.json.ReaderBasedJsonParser);
        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_STRING, parser.nextToken());
        org.junit.Assert.assertEquals("a", parser.getValueAsString("default"));
    } finally {
        parser.close();
    }
}

@org.junit.Test
public void utf8StreamParserReturnsStringFromDefaultValueAccessorBeforeTextIsRead() throws Exception {
    com.fasterxml.jackson.core.JsonParser parser = new com.fasterxml.jackson.core.JsonFactory()
            .createParser(new java.io.ByteArrayInputStream("\"a\"".getBytes("UTF-8")));
    try {
        org.junit.Assert.assertTrue(parser instanceof com.fasterxml.jackson.core.json.UTF8StreamJsonParser);
        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_STRING, parser.nextToken());
        org.junit.Assert.assertEquals("a", parser.getValueAsString("default"));
    } finally {
        parser.close();
    }
}