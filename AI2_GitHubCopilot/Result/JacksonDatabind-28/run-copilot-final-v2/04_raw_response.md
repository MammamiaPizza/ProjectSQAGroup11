@Test
    public void testGetDeserializerReturnsArrayDeserializer() throws Exception {
        JsonDeserializer<? extends JsonNode> deser =
JsonNodeDeserializer.getDeserializer(ArrayNode.class);
        assertNotNull("ArrayNode deserializer should not be null", deser);
        assertTrue("Should return an ArrayDeserializer",
                deser instanceof JsonNodeDeserializer.ArrayDeserializer);
    }

 @Test
 public void testGetDeserializerReturnsGenericInstanceForNonSpecializedType() throws Exception {
     JsonDeserializer<? extends JsonNode> deser =
JsonNodeDeserializer.getDeserializer(JsonNode.class);
     assertNotNull("Generic deserializer should not be null", deser);
     assertFalse("Should not be ObjectDeserializer",
             deser instanceof JsonNodeDeserializer.ObjectDeserializer);
     assertFalse("Should not be ArrayDeserializer",
             deser instanceof JsonNodeDeserializer.ArrayDeserializer);
 }

 @Test
 public void testGenericDeserializeEmptyArrayViaJsonNode() throws Exception {
     JsonNode node = mapper.readValue("[]", JsonNode.class);
     assertNotNull("Deserialized node should not be null", node);
     assertTrue("Result should be an array node", node.isArray());
     assertEquals("Empty array should have size zero", 0, node.size());
 }

 @Test
 public void testGenericDeserializeStringValueViaJsonNode() throws Exception {
     JsonNode node = mapper.readValue("\"test\"", JsonNode.class);
     assertNotNull("Deserialized node should not be null", node);
     assertTrue("Result should be a textual node", node.isTextual());
     assertEquals("Text value should match", "test", node.textValue());
 }