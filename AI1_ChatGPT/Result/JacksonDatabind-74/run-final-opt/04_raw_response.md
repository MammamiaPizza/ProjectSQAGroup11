@Test
public void defaultTypingDeserializesTypeIdBeforeMapProperties() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.enableDefaultTyping(com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL,
            com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY);

    Object result = mapper.readValue(
            "{\"@class\":\"java.util.LinkedHashMap\",\"item\":{\"@class\":\"java.util.LinkedHashMap\",\"answer\":7}}",
            Object.class);

    org.junit.Assert.assertTrue(result instanceof java.util.LinkedHashMap);
    java.util.Map<?, ?> values = (java.util.Map<?, ?>) result;
    org.junit.Assert.assertTrue(values.get("item") instanceof java.util.LinkedHashMap);
    org.junit.Assert.assertEquals(Integer.valueOf(7),
            ((java.util.Map<?, ?>) values.get("item")).get("answer"));
}

@Test
public void defaultTypingPreservesNaturalStringWithoutTypeId() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.enableDefaultTyping(com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL,
            com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY);

    Object result = mapper.readValue("\"plain text\"", Object.class);

    org.junit.Assert.assertEquals("plain text", result);
}

@Test
public void defaultTypingAcceptsArrayWrapperForPropertyInclusion() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.enableDefaultTyping(com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL,
            com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY);

    Object result = mapper.readValue("[\"java.util.ArrayList\",[1,2]]", Object.class);

    org.junit.Assert.assertTrue(result instanceof java.util.ArrayList);
    org.junit.Assert.assertEquals(java.util.Arrays.asList(Integer.valueOf(1), Integer.valueOf(2)), result);
}