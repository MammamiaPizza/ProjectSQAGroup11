@org.junit.Test
public void testCopiedMapperRetainsNonFinalDefaultTypingConfiguration() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper original = new com.fasterxml.jackson.databind.ObjectMapper();
    original.enableDefaultTyping(com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL);

    com.fasterxml.jackson.databind.ObjectMapper copy = original.copy();
    java.util.ArrayList<String> input = new java.util.ArrayList<String>();
    input.add("value");

    String json = copy.writeValueAsString(input);
    org.junit.Assert.assertTrue(json.contains(java.util.ArrayList.class.getName()));

    Object result = copy.readValue(json, Object.class);
    org.junit.Assert.assertTrue(result instanceof java.util.ArrayList);
    org.junit.Assert.assertEquals("value", ((java.util.ArrayList<?>) result).get(0));
}

@org.junit.Test
public void testNoTypeInfoBuilderSkipsHandlersForObjectType() {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder builder =
            com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder.noTypeInfoBuilder();
    com.fasterxml.jackson.databind.JavaType objectType =
            mapper.getTypeFactory().constructType(Object.class);

    org.junit.Assert.assertNull(builder.buildTypeSerializer(
            mapper.getSerializationConfig(), objectType, null));
    org.junit.Assert.assertNull(builder.buildTypeDeserializer(
            mapper.getDeserializationConfig(), objectType, null));
}

@org.junit.Test
public void testStdTypeResolverBuilderRetainsConfiguredProperties() {
    com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder builder =
            new com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder()
                    .init(com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS, null)
                    .inclusion(com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY)
                    .typeProperty("kind")
                    .defaultImpl(java.util.ArrayList.class)
                    .typeIdVisibility(true);

    org.junit.Assert.assertEquals("kind", builder.getTypeProperty());
    org.junit.Assert.assertSame(java.util.ArrayList.class, builder.getDefaultImpl());
    org.junit.Assert.assertTrue(builder.isTypeIdVisible());
}