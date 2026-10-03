@Test
public void serializedMapperRetainsGetterPropertyWriter() throws Exception {
    class GetterBean {
        public String getName() {
            return "getter";
        }
    }

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    assertEquals("{\"name\":\"getter\"}", mapper.writeValueAsString(new GetterBean()));

    com.fasterxml.jackson.databind.ObjectMapper restored = roundTripMapperForBeanPropertyWriterTest(mapper);

    assertEquals("{\"name\":\"getter\"}", restored.writeValueAsString(new GetterBean()));
}

private com.fasterxml.jackson.databind.ObjectMapper roundTripMapperForBeanPropertyWriterTest(
        com.fasterxml.jackson.databind.ObjectMapper mapper) throws Exception {
    java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
    java.io.ObjectOutputStream output = new java.io.ObjectOutputStream(bytes);
    output.writeObject(mapper);
    output.close();

    java.io.ObjectInputStream input = new java.io.ObjectInputStream(
            new java.io.ByteArrayInputStream(bytes.toByteArray()));
    try {
        return (com.fasterxml.jackson.databind.ObjectMapper) input.readObject();
    } finally {
        input.close();
    }
}