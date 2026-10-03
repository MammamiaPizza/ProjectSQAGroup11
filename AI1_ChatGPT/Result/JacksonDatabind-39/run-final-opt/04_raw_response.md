@Test
public void deserializeWithTypeDelegatesStructuredTokensToTypedAnyDeserializer() throws Exception {
    Object expected = new Object();
    com.fasterxml.jackson.databind.jsontype.TypeDeserializer typeDeserializer =
            typeDeserializerReturning(expected);

    com.fasterxml.jackson.core.JsonParser parser =
            new com.fasterxml.jackson.core.JsonFactory().createParser("{}");
    assertEquals(JsonToken.START_OBJECT, parser.nextToken());
    assertEquals(expected, NullifyingDeserializer.instance.deserializeWithType(parser, null, typeDeserializer));
    parser.close();

    parser = new com.fasterxml.jackson.core.JsonFactory().createParser("[]");
    assertEquals(JsonToken.START_ARRAY, parser.nextToken());
    assertEquals(expected, NullifyingDeserializer.instance.deserializeWithType(parser, null, typeDeserializer));
    parser.close();

    parser = new com.fasterxml.jackson.core.JsonFactory().createParser("{\"location\":null}");
    assertEquals(JsonToken.START_OBJECT, parser.nextToken());
    assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
    assertEquals(expected, NullifyingDeserializer.instance.deserializeWithType(parser, null, typeDeserializer));
    parser.close();
}

@Test
public void deserializeWithTypeReturnsNullForScalarWithoutUsingTypeDeserializer() throws Exception {
    com.fasterxml.jackson.core.JsonParser parser =
            new com.fasterxml.jackson.core.JsonFactory().createParser("\"ignored\"");
    assertEquals(JsonToken.VALUE_STRING, parser.nextToken());

    assertNull(NullifyingDeserializer.instance.deserializeWithType(parser, null, null));
    assertEquals(JsonToken.VALUE_STRING, parser.getCurrentToken());
    parser.close();
}

private com.fasterxml.jackson.databind.jsontype.TypeDeserializer typeDeserializerReturning(final Object value) {
    return new com.fasterxml.jackson.databind.jsontype.TypeDeserializer() {
        @Override
        public com.fasterxml.jackson.databind.jsontype.TypeDeserializer forProperty(
                com.fasterxml.jackson.databind.BeanProperty property) {
            return this;
        }

        @Override
        public com.fasterxml.jackson.annotation.JsonTypeInfo.As getTypeInclusion() {
            return null;
        }

        @Override
        public String getPropertyName() {
            return null;
        }

        @Override
        public com.fasterxml.jackson.databind.jsontype.TypeIdResolver getTypeIdResolver() {
            return null;
        }

        @Override
        public Class<?> getDefaultImpl() {
            return null;
        }

        @Override
        public Object deserializeTypedFromObject(com.fasterxml.jackson.core.JsonParser parser,
                com.fasterxml.jackson.databind.DeserializationContext context) {
            throw new AssertionError("deserializeTypedFromAny should be used");
        }

        @Override
        public Object deserializeTypedFromArray(com.fasterxml.jackson.core.JsonParser parser,
                com.fasterxml.jackson.databind.DeserializationContext context) {
            throw new AssertionError("deserializeTypedFromAny should be used");
        }

        @Override
        public Object deserializeTypedFromScalar(com.fasterxml.jackson.core.JsonParser parser,
                com.fasterxml.jackson.databind.DeserializationContext context) {
            throw new AssertionError("deserializeTypedFromAny should be used");
        }

        @Override
        public Object deserializeTypedFromAny(com.fasterxml.jackson.core.JsonParser parser,
                com.fasterxml.jackson.databind.DeserializationContext context) {
            return value;
        }
    };
}