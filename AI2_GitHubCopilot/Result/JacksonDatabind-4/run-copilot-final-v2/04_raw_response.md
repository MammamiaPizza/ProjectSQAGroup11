@Test
public void testLargeArrayForBufferExpansion() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();
    StringBuilder sb = new StringBuilder("[");
    int size = 30;
    for (int i = 0; i < size; i++) {
        if (i > 0) sb.append(",");
        sb.append(""e").append(i).append(""");
    }
    sb.append("]");
    String[] result = mapper.readValue(sb.toString(), String[].class);
    org.junit.Assert.assertNotNull(result);
    org.junit.Assert.assertEquals(size, result.length);
    for (int i = 0; i < size; i++) {
        org.junit.Assert.assertEquals("e" + i, result[i]);
    }
}

@Test
public void testEmptyStringAsNullWithoutSingleValueAsArray() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();

mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
false);
    mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NU
LL_OBJECT, true);
    String[] result = mapper.readValue("""", String[].class);
    org.junit.Assert.assertNull(result);
}

@Test
public void testSingleNullValueAcceptedAsArray() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();

mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
true);
    String[] result = mapper.readValue("null", String[].class);
    org.junit.Assert.assertNotNull(result);
    org.junit.Assert.assertEquals(1, result.length);
    org.junit.Assert.assertNull(result[0]);
}

@Test
public void testCustomElementDeserializerContextual() throws Exception {
    com.fasterxml.jackson.databind.module.SimpleModule mod = new
com.fasterxml.jackson.databind.module.SimpleModule();
    mod.addDeserializer(String.class, new com.fasterxml.jackson.databind.JsonDeserializer<String>()
{
        @Override
        public String deserialize(com.fasterxml.jackson.core.JsonParser p,
com.fasterxml.jackson.databind.DeserializationContext ctxt) throws java.io.IOException {
            return "X" + p.getText();
        }
    });
    com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();
    mapper.registerModule(mod);
    String[] result = mapper.readValue("["a", "b"]", String[].class);
    org.junit.Assert.assertNotNull(result);
    org.junit.Assert.assertEquals(2, result.length);
    org.junit.Assert.assertEquals("Xa", result[0]);
    org.junit.Assert.assertEquals("Xb", result[1]);
}