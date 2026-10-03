@org.junit.Test
public void readTreeWithDefaultTypingAcceptsMixedNodeArray() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.enableDefaultTyping();

    com.fasterxml.jackson.databind.JsonNode root =
            mapper.readTree("[true,null,{\"number\":2},\"text\"]");

    org.junit.Assert.assertTrue(root.isArray());
    org.junit.Assert.assertTrue(root.get(0).isBoolean());
    org.junit.Assert.assertTrue(root.get(1).isNull());
    org.junit.Assert.assertTrue(root.get(2).isObject());
    org.junit.Assert.assertEquals(2, root.get(2).get("number").intValue());
    org.junit.Assert.assertEquals("text", root.get(3).textValue());
}

@org.junit.Test
public void objectMapperConstructorAssignsCodecToFactoryWithoutCodec() {
    com.fasterxml.jackson.core.JsonFactory factory = new com.fasterxml.jackson.core.JsonFactory();
    org.junit.Assert.assertNull(factory.getCodec());

    com.fasterxml.jackson.databind.ObjectMapper mapper =
            new com.fasterxml.jackson.databind.ObjectMapper(factory);

    org.junit.Assert.assertSame(mapper, factory.getCodec());
}

@org.junit.Test
public void objectMapperConstructorPreservesExistingFactoryCodec() {
    com.fasterxml.jackson.core.JsonFactory factory = new com.fasterxml.jackson.core.JsonFactory();
    com.fasterxml.jackson.databind.ObjectMapper existingCodec =
            new com.fasterxml.jackson.databind.ObjectMapper();
    factory.setCodec(existingCodec);

    new com.fasterxml.jackson.databind.ObjectMapper(factory);

    org.junit.Assert.assertSame(existingCodec, factory.getCodec());
}

@org.junit.Test
public void copiedMapperRetainsDefaultTypingForJsonNodeArrays() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper original =
            new com.fasterxml.jackson.databind.ObjectMapper();
    original.enableDefaultTyping();

    com.fasterxml.jackson.databind.ObjectMapper copy = original.copy();
    com.fasterxml.jackson.databind.JsonNode root = copy.readTree("[9]");

    org.junit.Assert.assertNotSame(original, copy);
    org.junit.Assert.assertTrue(root.isArray());
    org.junit.Assert.assertEquals(9, root.get(0).intValue());
}