@Test
public void dateKeyUsesProviderDefaultDateKeySerialization() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
    java.util.Map<java.util.Date, Integer> values =
            new java.util.LinkedHashMap<java.util.Date, Integer>();
    values.put(new java.util.Date(0L), Integer.valueOf(2));

    assertEquals("{\"0\":2}", mapper.writeValueAsString(values));
}

@Test
public void nonStandardKeyUsesToStringAsFieldName() throws Exception {
    java.util.Map<KeyWithStableToString, Integer> values =
            new java.util.LinkedHashMap<KeyWithStableToString, Integer>();
    values.put(new KeyWithStableToString(), Integer.valueOf(3));

    assertEquals("{\"fallback-key\":3}",
            new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(values));
}

@Test
public void schemaDescribesStringKeys() throws Exception {
    com.fasterxml.jackson.databind.JsonNode schema =
            new com.fasterxml.jackson.databind.ser.std.StdKeySerializer().getSchema(null, null);

    assertEquals("string", schema.get("type").asText());
}

@Test
public void formatVisitorIsAskedForStringFormat() throws Exception {
    final com.fasterxml.jackson.databind.JavaType expectedType =
            new com.fasterxml.jackson.databind.ObjectMapper().getTypeFactory()
                    .constructType(String.class);
    final com.fasterxml.jackson.databind.JavaType[] observedType =
            new com.fasterxml.jackson.databind.JavaType[1];
    com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper visitor =
            new com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper.Base() {
                @Override
                public com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor
                        expectStringFormat(com.fasterxml.jackson.databind.JavaType type) {
                    observedType[0] = type;
                    return null;
                }
            };

    new com.fasterxml.jackson.databind.ser.std.StdKeySerializer()
            .acceptJsonFormatVisitor(visitor, expectedType);

    assertEquals(expectedType, observedType[0]);
}

private static final class KeyWithStableToString {
    @Override
    public String toString() {
        return "fallback-key";
    }
}