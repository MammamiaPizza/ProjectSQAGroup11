@org.junit.Test
public void scalarAccessorsUseWrappedBooleanAndNumbersOrDefaults() {
    org.junit.Assert.assertTrue(new POJONode(Boolean.TRUE).asBoolean(false));
    org.junit.Assert.assertFalse(new POJONode(Boolean.FALSE).asBoolean(true));
    org.junit.Assert.assertTrue(new POJONode("true").asBoolean(true));

    POJONode number = new POJONode(Integer.valueOf(13));
    org.junit.Assert.assertEquals(13, number.asInt(-1));
    org.junit.Assert.assertEquals(13L, number.asLong(-1L));
    org.junit.Assert.assertEquals(13.0d, number.asDouble(-1.0d), 0.0d);

    POJONode text = new POJONode("not a number");
    org.junit.Assert.assertEquals(-1, text.asInt(-1));
    org.junit.Assert.assertEquals(-2L, text.asLong(-2L));
    org.junit.Assert.assertEquals(-3.0d, text.asDouble(-3.0d), 0.0d);
}

@org.junit.Test
public void reportsPojoNodeTypeAndEmbeddedObjectToken() {
    POJONode node = new POJONode("value");

    org.junit.Assert.assertEquals(com.fasterxml.jackson.databind.node.JsonNodeType.POJO,
            node.getNodeType());
    org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_EMBEDDED_OBJECT,
            node.asToken());
}

@org.junit.Test
public void equalityUsesWrappedPojoIncludingNullValues() {
    POJONode value = new POJONode("value");

    org.junit.Assert.assertTrue(value.equals(value));
    org.junit.Assert.assertTrue(value.equals(new POJONode("value")));
    org.junit.Assert.assertFalse(value.equals(new POJONode("other")));
    org.junit.Assert.assertFalse(value.equals(null));
    org.junit.Assert.assertFalse(value.equals("value"));

    org.junit.Assert.assertTrue(new POJONode(null).equals(new POJONode(null)));
    org.junit.Assert.assertFalse(new POJONode(null).equals(new POJONode("value")));
}

@org.junit.Test
public void nonBinaryPojoHasNoBinaryValue() throws java.io.IOException {
    org.junit.Assert.assertNull(new POJONode("not binary").binaryValue());
}