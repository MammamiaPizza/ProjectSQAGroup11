@Test
public void testParserConstructorRetainsCodecForBufferedParser() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    com.fasterxml.jackson.core.JsonParser source = mapper.getFactory().createParser("null");
    TokenBuffer buffer = new TokenBuffer(source);

    buffer.writeString("buffered");

    com.fasterxml.jackson.core.JsonParser buffered = buffer.asParser();
    assertSame(mapper, buffered.getCodec());
    assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_STRING, buffered.nextToken());
    assertEquals("buffered", buffered.getText());
    assertNull(buffered.nextToken());

    buffered.close();
    source.close();
}

@Test
public void testSegmentRolloverPreservesBufferedTokens() throws Exception {
    TokenBuffer buffer = new TokenBuffer(null, false);
    buffer.writeStartArray();
    for (int i = 0; i < 20; ++i) {
        buffer.writeString(String.valueOf(i));
    }
    buffer.writeEndArray();

    com.fasterxml.jackson.core.JsonParser parser = buffer.asParser();
    assertEquals(com.fasterxml.jackson.core.JsonToken.START_ARRAY, parser.nextToken());
    for (int i = 0; i < 20; ++i) {
        assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_STRING, parser.nextToken());
        assertEquals(String.valueOf(i), parser.getText());
    }
    assertEquals(com.fasterxml.jackson.core.JsonToken.END_ARRAY, parser.nextToken());
    assertNull(parser.nextToken());
    parser.close();
}

@Test
public void testNativeIdsAreIncludedInBufferDescription() throws Exception {
    TokenBuffer buffer = new TokenBuffer(null, true);
    buffer.writeTypeId("type-1");
    buffer.writeObjectId("object-1");
    buffer.writeString("value");

    String description = buffer.toString();
    assertTrue(description.contains("[objectId=object-1]"));
    assertTrue(description.contains("[typeId=type-1]"));
}