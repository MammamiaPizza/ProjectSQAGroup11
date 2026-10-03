@org.junit.Test
public void mutableExternalTypePropertyDeserializesInEitherFieldOrder() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    MutableExternalHolder valueFirst = mapper.readValue(
            "{\"animal\":{\"name\":\"Rex\"},\"kind\":\"dog\"}",
            MutableExternalHolder.class);
    MutableExternalHolder typeFirst = mapper.readValue(
            "{\"kind\":\"dog\",\"animal\":{\"name\":\"Rex\"}}",
            MutableExternalHolder.class);

    org.junit.Assert.assertTrue(valueFirst.animal instanceof MutableExternalDog);
    org.junit.Assert.assertEquals("Rex", valueFirst.animal.name);
    org.junit.Assert.assertTrue(typeFirst.animal instanceof MutableExternalDog);
    org.junit.Assert.assertEquals("Rex", typeFirst.animal.name);
}

@org.junit.Test
public void missingExternalTypeIdUsesConfiguredDefaultImplementation() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    DefaultExternalHolder result = mapper.readValue(
            "{\"animal\":{\"name\":\"Rex\"}}",
            DefaultExternalHolder.class);

    org.junit.Assert.assertTrue(result.animal instanceof DefaultExternalDog);
    org.junit.Assert.assertEquals("Rex", result.animal.name);
}

public static class MutableExternalHolder {
    @com.fasterxml.jackson.annotation.JsonTypeInfo(
            use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME,
            include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "kind")
    @com.fasterxml.jackson.annotation.JsonSubTypes({
            @com.fasterxml.jackson.annotation.JsonSubTypes.Type(
                    value = MutableExternalDog.class, name = "dog")
    })
    public MutableExternalAnimal animal;
}

public static abstract class MutableExternalAnimal {
    public String name;
}

public static class MutableExternalDog extends MutableExternalAnimal {
}

public static class DefaultExternalHolder {
    @com.fasterxml.jackson.annotation.JsonTypeInfo(
            use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME,
            include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "kind",
            defaultImpl = DefaultExternalDog.class)
    @com.fasterxml.jackson.annotation.JsonSubTypes({
            @com.fasterxml.jackson.annotation.JsonSubTypes.Type(
                    value = DefaultExternalDog.class, name = "dog")
    })
    public DefaultExternalAnimal animal;
}

public static abstract class DefaultExternalAnimal {
    public String name;
}

public static class DefaultExternalDog extends DefaultExternalAnimal {
}