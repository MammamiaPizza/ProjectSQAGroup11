@Test
public void missingTypeIdUsesConfiguredDefaultImplementation() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    DefaultImplForTypeDeserializerBase value = mapper.readValue(
            "{\"value\":\"fallback\"}", DefaultedTypeDeserializerBase.class);

    org.junit.Assert.assertTrue(value instanceof DefaultImplForTypeDeserializerBase);
    org.junit.Assert.assertEquals("fallback", value.value);
}

@Test
public void unknownTypeIdUsesConfiguredDefaultImplementation() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    DefaultImplForTypeDeserializerBase value = mapper.readValue(
            "{\"type\":\"unknown-type\",\"value\":\"fallback\"}",
            DefaultedTypeDeserializerBase.class);

    org.junit.Assert.assertTrue(value instanceof DefaultImplForTypeDeserializerBase);
    org.junit.Assert.assertEquals("fallback", value.value);
}

@Test
public void unknownTypeIdCanBeIgnoredWhenInvalidSubtypeFailuresAreDisabled() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.disable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);

    NoDefaultTypeDeserializerBase value = mapper.readValue(
            "{\"type\":\"unknown-type\",\"value\":\"ignored\"}",
            NoDefaultTypeDeserializerBase.class);

    org.junit.Assert.assertNull(value);
}

@Test(expected = com.fasterxml.jackson.databind.JsonMappingException.class)
public void unknownTypeIdFailsWhenNoDefaultImplementationIsConfigured() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    mapper.readValue("{\"type\":\"unknown-type\",\"value\":\"failure\"}",
            NoDefaultTypeDeserializerBase.class);
}

@com.fasterxml.jackson.annotation.JsonTypeInfo(
        use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME,
        include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY,
        property = "type",
        defaultImpl = DefaultImplForTypeDeserializerBase.class)
public static abstract class DefaultedTypeDeserializerBase {
    public String value;
}

public static class DefaultImplForTypeDeserializerBase extends DefaultedTypeDeserializerBase {
}

@com.fasterxml.jackson.annotation.JsonTypeInfo(
        use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME,
        include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY,
        property = "type")
public static abstract class NoDefaultTypeDeserializerBase {
    public String value;
}