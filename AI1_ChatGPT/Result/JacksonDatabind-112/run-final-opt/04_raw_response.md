@org.junit.Test
public void testCustomStringDeserializerIsUsedForCollectionContentsAndNulls() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    com.fasterxml.jackson.databind.module.SimpleModule module =
            new com.fasterxml.jackson.databind.module.SimpleModule();
    module.addDeserializer(String.class, new com.fasterxml.jackson.databind.JsonDeserializer<String>() {
        @Override
        public String deserialize(com.fasterxml.jackson.core.JsonParser p,
                com.fasterxml.jackson.databind.DeserializationContext ctxt) throws java.io.IOException {
            return "custom-" + p.getText();
        }

        @Override
        public String getNullValue(com.fasterxml.jackson.databind.DeserializationContext ctxt) {
            return "custom-null";
        }
    });
    mapper.registerModule(module);

    java.util.List<String> values = mapper.readValue("[\"first\",null,\"third\"]",
            new com.fasterxml.jackson.core.type.TypeReference<java.util.List<String>>() { });

    org.junit.Assert.assertEquals(
            java.util.Arrays.asList("custom-first", "custom-null", "custom-third"), values);
}

@org.junit.Test
public void testCustomStringDeserializerSupportsSingleValueCollection() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper()
            .enable(com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    com.fasterxml.jackson.databind.module.SimpleModule module =
            new com.fasterxml.jackson.databind.module.SimpleModule();
    module.addDeserializer(String.class, new com.fasterxml.jackson.databind.JsonDeserializer<String>() {
        @Override
        public String deserialize(com.fasterxml.jackson.core.JsonParser p,
                com.fasterxml.jackson.databind.DeserializationContext ctxt) throws java.io.IOException {
            return "custom-" + p.getText();
        }
    });
    mapper.registerModule(module);

    java.util.List<String> values = mapper.readValue("\"only\"",
            new com.fasterxml.jackson.core.type.TypeReference<java.util.List<String>>() { });

    org.junit.Assert.assertEquals(java.util.Arrays.asList("custom-only"), values);
}