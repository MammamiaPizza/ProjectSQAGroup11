@org.junit.Test
public void parserConstructorRetainsSourceCodecForBufferedParser() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    com.fasterxml.jackson.core.JsonParser source = mapper.getFactory().createParser("{}");
    com.fasterxml.jackson.databind.util.TokenBuffer buffer =
            new com.fasterxml.jackson.databind.util.TokenBuffer(source);
    source.close();

    org.junit.Assert.assertSame(mapper, buffer.getCodec());

    buffer.writeStartObject();
    buffer.writeStringField("name", "buffered");
    buffer.writeEndObject();

    com.fasterxml.jackson.core.JsonParser parser = buffer.asParser();
    org.junit.Assert.assertSame(mapper, parser.getCodec());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.START_OBJECT, parser.nextToken());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.FIELD_NAME, parser.nextToken());
    org.junit.Assert.assertEquals("name", parser.getCurrentName());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_STRING, parser.nextToken());
    org.junit.Assert.assertEquals("buffered", parser.getText());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.END_OBJECT, parser.nextToken());
    org.junit.Assert.assertEquals(null, parser.nextToken());
    parser.close();
}

@org.junit.Test
public void appendPreservesTokensAcrossSegmentBoundary() throws Exception {
    com.fasterxml.jackson.databind.util.TokenBuffer source =
            new com.fasterxml.jackson.databind.util.TokenBuffer(null, false);
    for (int i = 0; i < 20; ++i) {
        source.writeNumber(i);
    }

    com.fasterxml.jackson.databind.util.TokenBuffer target =
            new com.fasterxml.jackson.databind.util.TokenBuffer(null, false);
    target.writeStartArray();
    org.junit.Assert.assertSame(target, target.append(source));
    target.writeEndArray();

    com.fasterxml.jackson.core.JsonParser parser = target.asParser();
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.START_ARRAY, parser.nextToken());
    for (int i = 0; i < 20; ++i) {
        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        org.junit.Assert.assertEquals(i, parser.getIntValue());
    }
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.END_ARRAY, parser.nextToken());
    org.junit.Assert.assertEquals(null, parser.nextToken());
    parser.close();
}

@org.junit.Test
public void nativeIdsAreIncludedInTokenBufferDescription() throws Exception {
    com.fasterxml.jackson.databind.util.TokenBuffer buffer =
            new com.fasterxml.jackson.databind.util.TokenBuffer(null, true);
    buffer.writeObjectId("object-17");
    buffer.writeTypeId("type-17");
    buffer.writeString("value");

    String description = buffer.toString();
    org.junit.Assert.assertTrue(description.contains("[objectId=object-17]"));
    org.junit.Assert.assertTrue(description.contains("[typeId=type-17]"));
}