@org.junit.Test
public void deserializesRootArrayAsGenericJsonNode() throws Exception {
    com.fasterxml.jackson.databind.JsonNode node =
            new com.fasterxml.jackson.databind.ObjectMapper().readTree("[true,3]");

    org.junit.Assert.assertTrue(node.isArray());
    org.junit.Assert.assertEquals(2, node.size());
    org.junit.Assert.assertTrue(node.get(0).asBoolean());
    org.junit.Assert.assertEquals(3, node.get(1).asInt());
}

@org.junit.Test
public void deserializesScalarAsGenericJsonNode() throws Exception {
    com.fasterxml.jackson.databind.JsonNode node =
            new com.fasterxml.jackson.databind.ObjectMapper().readTree("\"value\"");

    org.junit.Assert.assertTrue(node.isTextual());
    org.junit.Assert.assertEquals("value", node.asText());
}

@org.junit.Test
public void returnsNullNodeFromBothNullValueMethods() {
    com.fasterxml.jackson.databind.JsonDeserializer<? extends com.fasterxml.jackson.databind.JsonNode> deserializer =
            com.fasterxml.jackson.databind.deser.std.JsonNodeDeserializer.getDeserializer(
                    com.fasterxml.jackson.databind.JsonNode.class);

    org.junit.Assert.assertSame(
            com.fasterxml.jackson.databind.node.NullNode.getInstance(),
            deserializer.getNullValue());
    org.junit.Assert.assertSame(
            com.fasterxml.jackson.databind.node.NullNode.getInstance(),
            deserializer.getNullValue((com.fasterxml.jackson.databind.DeserializationContext) null));
}