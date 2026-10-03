@org.junit.Test
public void customKeyDeserializerIsUsedForMapKeys() throws java.lang.Exception {
    com.fasterxml.jackson.databind.module.SimpleModule module =
            new com.fasterxml.jackson.databind.module.SimpleModule();
    module.addKeyDeserializer(java.lang.Integer.class,
            new com.fasterxml.jackson.databind.KeyDeserializer() {
                @Override
                public java.lang.Object deserializeKey(java.lang.String key,
                        com.fasterxml.jackson.databind.DeserializationContext ctxt)
                        throws java.io.IOException {
                    return java.lang.Integer.valueOf(key.substring(1));
                }
            });

    com.fasterxml.jackson.databind.ObjectMapper mapper =
            new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.registerModule(module);

    java.util.Map<java.lang.Integer, java.lang.String> result = mapper.readValue(
            "{\"k1\":\"first\",\"k2\":\"second\"}",
            new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<java.lang.Integer, java.lang.String>>() { });

    org.junit.Assert.assertEquals("first", result.get(java.lang.Integer.valueOf(1)));
    org.junit.Assert.assertEquals("second", result.get(java.lang.Integer.valueOf(2)));
    org.junit.Assert.assertEquals(2, result.size());
}

@org.junit.Test
public void mapDeserializationRetainsNullAndNestedValues() throws java.lang.Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper =
            new com.fasterxml.jackson.databind.ObjectMapper();

    java.util.Map<java.lang.String, java.lang.Object> result = mapper.readValue(
            "{\"text\":\"value\",\"missing\":null,\"nested\":{\"number\":2},\"array\":[1,2]}",
            new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<java.lang.String, java.lang.Object>>() { });

    org.junit.Assert.assertEquals("value", result.get("text"));
    org.junit.Assert.assertTrue(result.containsKey("missing"));
    org.junit.Assert.assertNull(result.get("missing"));
    org.junit.Assert.assertTrue(result.get("nested") instanceof java.util.Map);
    org.junit.Assert.assertEquals(java.lang.Integer.valueOf(2),
            ((java.util.Map<?, ?>) result.get("nested")).get("number"));
    org.junit.Assert.assertTrue(result.get("array") instanceof java.util.List);
    org.junit.Assert.assertEquals(2, ((java.util.List<?>) result.get("array")).size());
}

@org.junit.Test
public void concreteTreeMapIsCreatedAndUsesNaturalKeyOrdering() throws java.lang.Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper =
            new com.fasterxml.jackson.databind.ObjectMapper();

    java.util.TreeMap<java.lang.String, java.lang.Integer> result = mapper.readValue(
            "{\"z\":1,\"a\":2}",
            new com.fasterxml.jackson.core.type.TypeReference<java.util.TreeMap<java.lang.String, java.lang.Integer>>() { });

    org.junit.Assert.assertEquals("a", result.firstKey());
    org.junit.Assert.assertEquals(java.lang.Integer.valueOf(1), result.get("z"));
    org.junit.Assert.assertEquals(java.lang.Integer.valueOf(2), result.get("a"));
}