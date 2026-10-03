@org.junit.Test
public void builderBasedDeserializerIsCreatedForAnnotatedType() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    CacheBuilderValue value = mapper.readValue("{\"name\":\"builder\"}", CacheBuilderValue.class);

    org.junit.Assert.assertEquals("builder", value.name);
}

@org.junit.Test
public void converterBasedDeserializerUsesDeclaredInputType() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    CacheConvertedValue value = mapper.readValue("\"37\"", CacheConvertedValue.class);

    org.junit.Assert.assertEquals(37, value.number);
}

@org.junit.Test
public void collectionDeserializerIsCachedAndReused() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    java.util.List<java.lang.Integer> first = mapper.readValue("[1,2]",
            new com.fasterxml.jackson.core.type.TypeReference<java.util.List<java.lang.Integer>>() { });
    java.util.List<java.lang.Integer> second = mapper.readValue("[3]",
            new com.fasterxml.jackson.core.type.TypeReference<java.util.List<java.lang.Integer>>() { });

    org.junit.Assert.assertEquals(java.util.Arrays.asList(1, 2), first);
    org.junit.Assert.assertEquals(java.util.Collections.singletonList(3), second);
}

@org.junit.Test
public void abstractTypeWithoutDeserializerFailsWithMappingException() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    try {
        mapper.readValue("{}", CacheUnresolvedType.class);
        org.junit.Assert.fail("Expected abstract type deserialization to fail");
    } catch (com.fasterxml.jackson.databind.JsonMappingException expected) {
    }
}

@com.fasterxml.jackson.databind.annotation.JsonDeserialize(builder = CacheBuilderValue.Builder.class)
public static class CacheBuilderValue {
    public final java.lang.String name;

    private CacheBuilderValue(Builder builder) {
        name = builder.name;
    }

    @com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        public java.lang.String name;

        public Builder name(java.lang.String value) {
            name = value;
            return this;
        }

        public CacheBuilderValue build() {
            return new CacheBuilderValue(this);
        }
    }
}

@com.fasterxml.jackson.databind.annotation.JsonDeserialize(converter = CacheStringToValueConverter.class)
public static class CacheConvertedValue {
    public final int number;

    public CacheConvertedValue(int value) {
        number = value;
    }
}

public static class CacheStringToValueConverter
        extends com.fasterxml.jackson.databind.util.StdConverter<java.lang.String, CacheConvertedValue> {
    @Override
    public CacheConvertedValue convert(java.lang.String value) {
        return new CacheConvertedValue(java.lang.Integer.parseInt(value));
    }
}

private interface CacheUnresolvedType {
}