@org.junit.Test
public void traversesArrayWithSkipChildrenAndCanBeClosed() throws Exception {
    com.fasterxml.jackson.databind.node.ArrayNode array =
            com.fasterxml.jackson.databind.node.JsonNodeFactory.instance.arrayNode()
                    .add(1)
                    .add("ignored");
    com.fasterxml.jackson.databind.node.TreeTraversingParser parser =
            new com.fasterxml.jackson.databind.node.TreeTraversingParser(array);

    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.START_ARRAY, parser.nextToken());
    org.junit.Assert.assertSame(parser, parser.skipChildren());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.END_ARRAY, parser.getCurrentToken());

    parser.close();
    org.junit.Assert.assertTrue(parser.isClosed());
    org.junit.Assert.assertNull(parser.getCurrentName());

    parser.close();
    org.junit.Assert.assertTrue(parser.isClosed());
}

@org.junit.Test
public void traversesObjectFieldsAndSupportsOverridingCurrentName() throws Exception {
    com.fasterxml.jackson.databind.node.ObjectNode object =
            com.fasterxml.jackson.databind.node.JsonNodeFactory.instance.objectNode()
                    .put("answer", 42);
    com.fasterxml.jackson.databind.node.TreeTraversingParser parser =
            new com.fasterxml.jackson.databind.node.TreeTraversingParser(object);

    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.START_OBJECT, parser.nextToken());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.FIELD_NAME, parser.nextToken());
    org.junit.Assert.assertEquals("answer", parser.getCurrentName());

    parser.overrideCurrentName("replacement");
    org.junit.Assert.assertEquals("replacement", parser.getCurrentName());

    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_INT, parser.nextToken());
    org.junit.Assert.assertEquals(42, parser.getIntValue());
    org.junit.Assert.assertEquals("replacement", parser.getCurrentName());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.END_OBJECT, parser.nextToken());
}

@org.junit.Test
public void numericAccessorsUseNumericNodesAndRejectTextNodes() throws Exception {
    com.fasterxml.jackson.databind.node.TreeTraversingParser numericParser =
            new com.fasterxml.jackson.databind.node.TreeTraversingParser(
                    com.fasterxml.jackson.databind.node.JsonNodeFactory.instance.numberNode(
                            new java.math.BigDecimal("12.50")));

    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_FLOAT, numericParser.nextToken());
    org.junit.Assert.assertEquals(new java.math.BigDecimal("12.50"), numericParser.getDecimalValue());
    org.junit.Assert.assertEquals(java.math.BigInteger.valueOf(12L), numericParser.getBigIntegerValue());
    org.junit.Assert.assertEquals(12.5d, numericParser.getDoubleValue(), 0.0d);

    com.fasterxml.jackson.databind.node.TreeTraversingParser textParser =
            new com.fasterxml.jackson.databind.node.TreeTraversingParser(
                    com.fasterxml.jackson.databind.node.JsonNodeFactory.instance.textNode("not a number"));
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_STRING, textParser.nextToken());
    try {
        textParser.getIntValue();
        org.junit.Assert.fail("Expected numeric access on a text node to fail");
    } catch (com.fasterxml.jackson.core.JsonParseException expected) {
    }
}

@org.junit.Test
public void returnsByteArrayStoredInPojoNode() throws Exception {
    byte[] input = new byte[] { 3, 4, 5 };
    com.fasterxml.jackson.databind.node.TreeTraversingParser parser =
            new com.fasterxml.jackson.databind.node.TreeTraversingParser(
                    new com.fasterxml.jackson.databind.node.POJONode(input));

    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
    org.junit.Assert.assertArrayEquals(
            input,
            parser.getBinaryValue(com.fasterxml.jackson.core.Base64Variants.MODIFIED_FOR_URL));
    org.junit.Assert.assertSame(input, parser.getEmbeddedObject());
}