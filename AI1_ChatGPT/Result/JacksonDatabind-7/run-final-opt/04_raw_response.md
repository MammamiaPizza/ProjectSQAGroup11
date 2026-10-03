@Test
public void testAppendPreservesAllTokensFromBothBuffers() throws Exception {
    TokenBuffer first = new TokenBuffer((com.fasterxml.jackson.core.ObjectCodec) null, false);
    first.writeStartObject();
    first.writeFieldName("a");
    first.writeNumber(1);
    first.writeEndObject();

    TokenBuffer second = new TokenBuffer((com.fasterxml.jackson.core.ObjectCodec) null, false);
    second.writeStartArray();
    second.writeString("b");
    second.writeEndArray();

    assertSame(first, first.append(second));

    JsonParser parser = first.asParser();
    assertEquals(JsonToken.START_OBJECT, parser.nextToken());
    assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
    assertEquals("a", parser.getCurrentName());
    assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
    assertEquals(1, parser.getIntValue());
    assertEquals(JsonToken.END_OBJECT, parser.nextToken());
    assertEquals(JsonToken.START_ARRAY, parser.nextToken());
    assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
    assertEquals("b", parser.getText());
    assertEquals(JsonToken.END_ARRAY, parser.nextToken());
    assertNull(parser.nextToken());
}

@Test
public void testNativeTypeAndObjectIdsAreAvailableFromParser() throws Exception {
    TokenBuffer buffer = new TokenBuffer((com.fasterxml.jackson.core.ObjectCodec) null, true);
    buffer.writeTypeId("type-id");
    buffer.writeObjectId("object-id");
    buffer.writeString("value");

    JsonParser parser = buffer.asParser();
    assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
    assertEquals("value", parser.getText());
    assertEquals("type-id", parser.getTypeId());
    assertEquals("object-id", parser.getObjectId());
    assertNull(parser.nextToken());
}

@Test
public void testDeprecatedConstructorAndFeatureMaskConfiguration() {
    TokenBuffer buffer = new TokenBuffer((com.fasterxml.jackson.core.ObjectCodec) null);
    int originalMask = buffer.getFeatureMask();

    buffer.disable(com.fasterxml.jackson.core.JsonGenerator.Feature.AUTO_CLOSE_TARGET);
    assertFalse(buffer.isEnabled(com.fasterxml.jackson.core.JsonGenerator.Feature.AUTO_CLOSE_TARGET));

    buffer.enable(com.fasterxml.jackson.core.JsonGenerator.Feature.AUTO_CLOSE_TARGET);
    assertTrue(buffer.isEnabled(com.fasterxml.jackson.core.JsonGenerator.Feature.AUTO_CLOSE_TARGET));

    assertSame(buffer, buffer.setFeatureMask(originalMask));
    assertEquals(originalMask, buffer.getFeatureMask());
}