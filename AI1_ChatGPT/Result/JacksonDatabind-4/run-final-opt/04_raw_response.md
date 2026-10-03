@org.junit.Test
public void testCustomStringDeserializerHandlesArrayLargerThanInitialBuffer() throws Exception {
    com.fasterxml.jackson.databind.module.SimpleModule module =
            new com.fasterxml.jackson.databind.module.SimpleModule();
    module.addDeserializer(String.class, new com.fasterxml.jackson.databind.JsonDeserializer<String>() {
        @Override
        public String deserialize(com.fasterxml.jackson.core.JsonParser jp,
                com.fasterxml.jackson.databind.DeserializationContext ctxt) throws java.io.IOException {
            return "custom-" + jp.getText();
        }
    });
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.registerModule(module);

    StringBuilder json = new StringBuilder("[");
    for (int i = 0; i < 13; ++i) {
        if (i > 0) {
            json.append(',');
        }
        json.append('"').append(i).append('"');
    }
    json.append(']');

    String[] result = mapper.readValue(json.toString(), String[].class);
    org.junit.Assert.assertEquals(13, result.length);
    org.junit.Assert.assertEquals("custom-0", result[0]);
    org.junit.Assert.assertEquals("custom-12", result[12]);
}

@org.junit.Test
public void testCustomStringDeserializerExceptionReportsFailingArrayIndex() throws Exception {
    com.fasterxml.jackson.databind.module.SimpleModule module =
            new com.fasterxml.jackson.databind.module.SimpleModule();
    module.addDeserializer(String.class, new com.fasterxml.jackson.databind.JsonDeserializer<String>() {
        @Override
        public String deserialize(com.fasterxml.jackson.core.JsonParser jp,
                com.fasterxml.jackson.databind.DeserializationContext ctxt) throws java.io.IOException {
            if ("bad".equals(jp.getText())) {
                throw new java.io.IOException("bad string");
            }
            return jp.getText();
        }
    });
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.registerModule(module);

    try {
        mapper.readValue("[\"good\",\"bad\"]", String[].class);
        org.junit.Assert.fail("Expected JsonMappingException from custom String deserializer");
    } catch (com.fasterxml.jackson.databind.JsonMappingException e) {
        org.junit.Assert.assertEquals(1, e.getPath().size());
        org.junit.Assert.assertEquals(1, e.getPath().get(0).getIndex());
    }
}

@org.junit.Test
public void testNullValueDeserializesAsNullStringArray() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    String[] result = mapper.readValue("null", String[].class);

    org.junit.Assert.assertNull(result);
}