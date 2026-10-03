@org.junit.Test(expected = com.fasterxml.jackson.core.JsonParseException.class)
public void rejectsIdentifierSuffixAfterTrueLiteral() throws Exception {
    com.fasterxml.jackson.core.JsonParser parser =
            new com.fasterxml.jackson.core.JsonFactory().createParser("truex");
    parser.nextToken();
}

@org.junit.Test(expected = com.fasterxml.jackson.core.JsonParseException.class)
public void rejectsArrayClosedWithObjectEndMarker() throws Exception {
    com.fasterxml.jackson.core.JsonParser parser =
            new com.fasterxml.jackson.core.JsonFactory().createParser("[}");
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.START_ARRAY, parser.nextToken());
    parser.nextToken();
}

@org.junit.Test(expected = com.fasterxml.jackson.core.JsonParseException.class)
public void rejectsObjectClosedWithArrayEndMarker() throws Exception {
    com.fasterxml.jackson.core.JsonParser parser =
            new com.fasterxml.jackson.core.JsonFactory().createParser("{]");
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.START_OBJECT, parser.nextToken());
    parser.nextToken();
}

@org.junit.Test
public void closesOrRetainsReaderBasedInputAccordingToAutoCloseFeature() throws Exception {
    java.io.StringReader retainedReader = new java.io.StringReader("{}");
    com.fasterxml.jackson.core.JsonFactory noCloseFactory = new com.fasterxml.jackson.core.JsonFactory();
    noCloseFactory.disable(com.fasterxml.jackson.core.JsonParser.Feature.AUTO_CLOSE_SOURCE);
    com.fasterxml.jackson.core.JsonParser retainedParser = noCloseFactory.createParser(retainedReader);
    retainedParser.close();
    org.junit.Assert.assertEquals('{', retainedReader.read());

    java.io.StringReader closedReader = new java.io.StringReader("{}");
    com.fasterxml.jackson.core.JsonParser closedParser =
            new com.fasterxml.jackson.core.JsonFactory().createParser(closedReader);
    closedParser.close();
    try {
        closedReader.read();
        org.junit.Assert.fail("Reader should have been closed");
    } catch (java.io.IOException expected) {
    }
}