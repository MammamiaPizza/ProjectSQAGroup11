@org.junit.Test
public void testEscapedCharactersAreDecodedInSingleInputBuffer() throws Exception {
    com.fasterxml.jackson.core.JsonParser parser = new com.fasterxml.jackson.core.JsonFactory()
            .createNonBlockingByteArrayParser();
    com.fasterxml.jackson.core.async.ByteArrayFeeder feeder =
            (com.fasterxml.jackson.core.async.ByteArrayFeeder) parser.getNonBlockingInputFeeder();

    byte[] input = "[\"\\b\\t\\n\\f\\r\\\"\\\\\\u0041\"]"
            .getBytes(java.nio.charset.StandardCharsets.UTF_8);
    feeder.feedInput(input, 0, input.length);

    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.START_ARRAY, parser.nextToken());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_STRING, parser.nextToken());
    org.junit.Assert.assertEquals("\b\t\n\f\r\"\\A", parser.getText());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.END_ARRAY, parser.nextToken());

    parser.endOfInput();
    org.junit.Assert.assertNull(parser.nextToken());
}

@org.junit.Test
public void testUnicodeEscapeCanBeSplitImmediatelyAfterBackslash() throws Exception {
    com.fasterxml.jackson.core.JsonParser parser = new com.fasterxml.jackson.core.JsonFactory()
            .createNonBlockingByteArrayParser();
    com.fasterxml.jackson.core.async.ByteArrayFeeder feeder =
            (com.fasterxml.jackson.core.async.ByteArrayFeeder) parser.getNonBlockingInputFeeder();

    byte[] first = "[\"\\"
            .getBytes(java.nio.charset.StandardCharsets.UTF_8);
    feeder.feedInput(first, 0, first.length);

    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.START_ARRAY, parser.nextToken());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.NOT_AVAILABLE, parser.nextToken());
    org.junit.Assert.assertTrue(feeder.needMoreInput());

    byte[] second = "u0041\"]"
            .getBytes(java.nio.charset.StandardCharsets.UTF_8);
    feeder.feedInput(second, 0, second.length);

    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_STRING, parser.nextToken());
    org.junit.Assert.assertEquals("A", parser.getText());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.END_ARRAY, parser.nextToken());

    parser.endOfInput();
    org.junit.Assert.assertNull(parser.nextToken());
}