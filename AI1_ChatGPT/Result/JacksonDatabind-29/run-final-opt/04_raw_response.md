@Test
public void creatorBasedExternalTypeIdDeserializesBufferedValue() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    ExternalTypeCreatorHolder result = mapper.readValue(
            "{\"creatorKind\":\"creatorDog\",\"creatorValue\":{\"name\":\"Buddy\"}}",
            ExternalTypeCreatorHolder.class);

    org.junit.Assert.assertEquals(ExternalTypeCreatorDog.class, result.creatorValue.getClass());
    org.junit.Assert.assertEquals("Buddy", result.creatorValue.name);
}

@Test
public void missingExternalTypeIdUsesConfiguredDefaultImplementation() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    ExternalTypeDefaultHolder result = mapper.readValue(
            "{\"defaultValue\":{\"name\":\"Fallback\"}}",
            ExternalTypeDefaultHolder.class);

    org.junit.Assert.assertEquals(ExternalTypeDefaultDog.class, result.defaultValue.getClass());
    org.junit.Assert.assertEquals("Fallback", result.defaultValue.name);
}

@com.fasterxml.jackson.annotation.JsonTypeInfo(
        use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME,
        include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "creatorKind")
@com.fasterxml.jackson.annotation.JsonSubTypes({
        @com.fasterxml.jackson.annotation.JsonSubTypes.Type(
                value = ExternalTypeCreatorDog.class, name = "creatorDog")
})
public static interface ExternalTypeCreatorAnimal {
}

public static class ExternalTypeCreatorDog implements ExternalTypeCreatorAnimal {
    public String name;

    public ExternalTypeCreatorDog() {
    }
}

public static class ExternalTypeCreatorHolder {
    public final ExternalTypeCreatorAnimal creatorValue;

    @com.fasterxml.jackson.annotation.JsonCreator
    public ExternalTypeCreatorHolder(
            @com.fasterxml.jackson.annotation.JsonProperty("creatorValue")
            ExternalTypeCreatorAnimal creatorValue) {
        this.creatorValue = creatorValue;
    }
}

@com.fasterxml.jackson.annotation.JsonTypeInfo(
        use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME,
        include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "defaultKind",
        defaultImpl = ExternalTypeDefaultDog.class)
@com.fasterxml.jackson.annotation.JsonSubTypes({
        @com.fasterxml.jackson.annotation.JsonSubTypes.Type(
                value = ExternalTypeDefaultDog.class, name = "defaultDog")
})
public static interface ExternalTypeDefaultAnimal {
}

public static class ExternalTypeDefaultDog implements ExternalTypeDefaultAnimal {
    public String name;

    public ExternalTypeDefaultDog() {
    }
}

public static class ExternalTypeDefaultHolder {
    public ExternalTypeDefaultAnimal defaultValue;

    public ExternalTypeDefaultHolder() {
    }
}