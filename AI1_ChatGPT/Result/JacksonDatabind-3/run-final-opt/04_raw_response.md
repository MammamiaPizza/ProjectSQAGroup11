@Test
public void testCustomStringDeserializerHandlesMultipleBufferChunksAndNulls() throws java.lang.Exception
{
    com.fasterxml.jackson.databind.module.SimpleModule module =
            new com.fasterxml.jackson.databind.module.SimpleModule();
    module.addDeserializer(String.class, new com.fasterxml.jackson.databind.JsonDeserializer<String>() {
        @Override
        public String deserialize(com.fasterxml.jackson.core.JsonParser jp,
                com.fasterxml.jackson.databind.DeserializationContext ctxt)
                throws java.io.IOException {
            return jp.getText().toUpperCase(java.util.Locale.ENGLISH);
        }
    });

    com.fasterxml.jackson.databind.ObjectMapper mapper =
            new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.registerModule(module);

    StringBuilder json = new StringBuilder("[");
    for (int i = 0; i < 40; ++i) {
        if (i > 0) {
            json.append(',');
        }
        json.append('"').append("value").append(i).append('"');
    }
    json.append(",null]");

    String[] result = mapper.readValue(json.toString(), String[].class);

    assertEquals(41, result.length);
    assertEquals("VALUE0", result[0]);
    assertEquals("VALUE11", result[11]);
    assertEquals("VALUE12", result[12]);
    assertEquals("VALUE39", result[39]);
    assertNull(result[40]);
}