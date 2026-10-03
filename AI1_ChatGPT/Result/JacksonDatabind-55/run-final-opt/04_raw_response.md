@org.junit.Test
public void testEnumKeyWithJsonPropertyInRegularMap() throws Exception {
    java.util.Map<JsonPropertyMapKey, String> values =
            new java.util.LinkedHashMap<JsonPropertyMapKey, String>();
    values.put(JsonPropertyMapKey.ENTRY, "value");

    org.junit.Assert.assertEquals("{\"json-key\":\"value\"}",
            new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(values));
}

@org.junit.Test
public void testStandardKeySerializerSelection() {
    com.fasterxml.jackson.databind.SerializationConfig config =
            new com.fasterxml.jackson.databind.ObjectMapper().getSerializationConfig();

    org.junit.Assert.assertNull(
            com.fasterxml.jackson.databind.ser.std.StdKeySerializers.getStdKeySerializer(
                    config, null, false));
    org.junit.Assert.assertSame(
            com.fasterxml.jackson.databind.ser.std.StdKeySerializers.getDefault(),
            com.fasterxml.jackson.databind.ser.std.StdKeySerializers.getStdKeySerializer(
                    config, java.lang.Object.class, true));
    org.junit.Assert.assertTrue(
            com.fasterxml.jackson.databind.ser.std.StdKeySerializers.getStdKeySerializer(
                    config, java.lang.String.class, false)
                    instanceof com.fasterxml.jackson.databind.ser.std.StdKeySerializers.StringKeySerializer);
    org.junit.Assert.assertSame(
            com.fasterxml.jackson.databind.ser.std.StdKeySerializers.getDefault(),
            com.fasterxml.jackson.databind.ser.std.StdKeySerializers.getStdKeySerializer(
                    config, int.class, false));
    org.junit.Assert.assertTrue(
            com.fasterxml.jackson.databind.ser.std.StdKeySerializers.getStdKeySerializer(
                    config, java.lang.Class.class, false)
                    instanceof com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default);
    org.junit.Assert.assertTrue(
            com.fasterxml.jackson.databind.ser.std.StdKeySerializers.getStdKeySerializer(
                    config, java.util.Date.class, false)
                    instanceof com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default);
    org.junit.Assert.assertTrue(
            com.fasterxml.jackson.databind.ser.std.StdKeySerializers.getStdKeySerializer(
                    config, java.util.Calendar.class, false)
                    instanceof com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default);
    org.junit.Assert.assertTrue(
            com.fasterxml.jackson.databind.ser.std.StdKeySerializers.getStdKeySerializer(
                    config, java.util.UUID.class, false)
                    instanceof com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default);
    org.junit.Assert.assertNull(
            com.fasterxml.jackson.databind.ser.std.StdKeySerializers.getStdKeySerializer(
                    config, java.lang.StringBuilder.class, false));
}

@org.junit.Test
public void testFallbackKeySerializerSelectionForEnumTypes() {
    com.fasterxml.jackson.databind.SerializationConfig config =
            new com.fasterxml.jackson.databind.ObjectMapper().getSerializationConfig();

    org.junit.Assert.assertTrue(
            com.fasterxml.jackson.databind.ser.std.StdKeySerializers.getFallbackKeySerializer(
                    config, java.lang.Enum.class)
                    instanceof com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Dynamic);
    org.junit.Assert.assertTrue(
            com.fasterxml.jackson.databind.ser.std.StdKeySerializers.getFallbackKeySerializer(
                    config, JsonPropertyMapKey.class)
                    instanceof com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default);
}

private enum JsonPropertyMapKey {
    @com.fasterxml.jackson.annotation.JsonProperty("json-key")
    ENTRY
}