@org.junit.Test
public void readerParserDecodesEmptyAndPaddedBase64Strings() throws Exception {
    com.fasterxml.jackson.core.JsonParser parser = new com.fasterxml.jackson.core.JsonFactory()
            .createParser(new java.io.StringReader("[\"\",\"TWE=\"]"));

    org.junit.Assert.assertSame(com.fasterxml.jackson.core.JsonToken.START_ARRAY, parser.nextToken());
    org.junit.Assert.assertSame(com.fasterxml.jackson.core.JsonToken.VALUE_STRING, parser.nextToken());
    org.junit.Assert.assertArrayEquals(new byte[0],
            parser.getBinaryValue(com.fasterxml.jackson.core.Base64Variants.MIME));
    org.junit.Assert.assertSame(com.fasterxml.jackson.core.JsonToken.VALUE_STRING, parser.nextToken());
    org.junit.Assert.assertArrayEquals(new byte[] { 77, 97 },
            parser.getBinaryValue(com.fasterxml.jackson.core.Base64Variants.MIME));
    org.junit.Assert.assertSame(com.fasterxml.jackson.core.JsonToken.END_ARRAY, parser.nextToken());
    org.junit.Assert.assertNull(parser.nextToken());
    parser.close();
}

@org.junit.Test
public void utf8ParserDecodesEmptyAndPaddedBase64Strings() throws Exception {
    byte[] input = "[\"\",\"TWE=\"]".getBytes("UTF-8");
    com.fasterxml.jackson.core.JsonParser parser = new com.fasterxml.jackson.core.JsonFactory()
            .createParser(new java.io.ByteArrayInputStream(input));

    org.junit.Assert.assertSame(com.fasterxml.jackson.core.JsonToken.START_ARRAY, parser.nextToken());
    org.junit.Assert.assertSame(com.fasterxml.jackson.core.JsonToken.VALUE_STRING, parser.nextToken());
    org.junit.Assert.assertArrayEquals(new byte[0],
            parser.getBinaryValue(com.fasterxml.jackson.core.Base64Variants.MIME));
    org.junit.Assert.assertSame(com.fasterxml.jackson.core.JsonToken.VALUE_STRING, parser.nextToken());
    org.junit.Assert.assertArrayEquals(new byte[] { 77, 97 },
            parser.getBinaryValue(com.fasterxml.jackson.core.Base64Variants.MIME));
    org.junit.Assert.assertSame(com.fasterxml.jackson.core.JsonToken.END_ARRAY, parser.nextToken());
    org.junit.Assert.assertNull(parser.nextToken());
    parser.close();
}