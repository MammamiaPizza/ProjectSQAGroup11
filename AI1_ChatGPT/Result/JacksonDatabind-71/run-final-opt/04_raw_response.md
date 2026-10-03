@org.junit.Test
public void deserializesBooleanMapKeys() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    java.util.Map<java.lang.Boolean, java.lang.Integer> result = mapper.readValue(
            "{\"true\":1,\"false\":2}",
            new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<java.lang.Boolean, java.lang.Integer>>() { });

    org.junit.Assert.assertEquals(java.lang.Integer.valueOf(1), result.get(java.lang.Boolean.TRUE));
    org.junit.Assert.assertEquals(java.lang.Integer.valueOf(2), result.get(java.lang.Boolean.FALSE));
}

@org.junit.Test
public void rejectsInvalidBooleanMapKey() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    try {
        mapper.readValue("{\"yes\":1}",
                new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<java.lang.Boolean, java.lang.Integer>>() { });
        org.junit.Assert.fail("Expected invalid Boolean key to fail");
    } catch (com.fasterxml.jackson.databind.JsonMappingException e) {
        org.junit.Assert.assertTrue(e.getMessage().contains("value not 'true' or 'false'"));
    }
}

@org.junit.Test
public void parsesByteShortAndCharacterKeys() throws Exception {
    org.junit.Assert.assertEquals(java.lang.Byte.valueOf((byte) -128),
            com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer.forType(java.lang.Byte.class)
                    .deserializeKey("-128", null));
    org.junit.Assert.assertEquals(java.lang.Byte.valueOf((byte) -1),
            com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer.forType(java.lang.Byte.class)
                    .deserializeKey("255", null));
    org.junit.Assert.assertEquals(java.lang.Short.valueOf(java.lang.Short.MAX_VALUE),
            com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer.forType(java.lang.Short.class)
                    .deserializeKey("32767", null));
    org.junit.Assert.assertEquals(java.lang.Character.valueOf('x'),
            com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer.forType(java.lang.Character.class)
                    .deserializeKey("x", null));
}

@org.junit.Test
public void parsesIntegralAndFloatingPointKeys() throws Exception {
    org.junit.Assert.assertEquals(java.lang.Integer.valueOf(-12),
            com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer.forType(java.lang.Integer.class)
                    .deserializeKey("-12", null));
    org.junit.Assert.assertEquals(java.lang.Long.valueOf(1234567890123L),
            com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer.forType(java.lang.Long.class)
                    .deserializeKey("1234567890123", null));
    org.junit.Assert.assertEquals(java.lang.Float.valueOf(1.5f),
            com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer.forType(java.lang.Float.class)
                    .deserializeKey("1.5", null));
    org.junit.Assert.assertEquals(java.lang.Double.valueOf(-2.25d),
            com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer.forType(java.lang.Double.class)
                    .deserializeKey("-2.25", null));
}