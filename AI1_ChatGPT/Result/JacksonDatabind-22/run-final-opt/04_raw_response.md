@org.junit.Test
public void testJsonValueFieldWithCustomOverride() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    org.junit.Assert.assertEquals("42",
            mapper.writeValueAsString(new JsonValueFieldWithCustomOverride()));
}

@org.junit.Test
public void testArraySerializersForPrimitiveAndObjectArrays() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    org.junit.Assert.assertEquals("[1,2]", mapper.writeValueAsString(new int[] { 1, 2 }));
    org.junit.Assert.assertEquals("[\"a\",3]", mapper.writeValueAsString(new Object[] { "a", 3 }));
}

@org.junit.Test
public void testContentUsingSerializerForCollectionProperty() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    org.junit.Assert.assertEquals("{\"values\":[1,4]}",
            mapper.writeValueAsString(new ContentSerializerBean()));
}

public static class JsonValueFieldWithCustomOverride {
    @com.fasterxml.jackson.annotation.JsonValue
    @com.fasterxml.jackson.databind.annotation.JsonSerialize(using = JsonValueAsNumberSerializer.class)
    public final String value = "value";
}

public static class JsonValueAsNumberSerializer
        extends com.fasterxml.jackson.databind.JsonSerializer<String> {
    @Override
    public void serialize(String value, com.fasterxml.jackson.core.JsonGenerator gen,
            com.fasterxml.jackson.databind.SerializerProvider provider) throws java.io.IOException {
        gen.writeNumber(42);
    }
}

public static class ContentSerializerBean {
    @com.fasterxml.jackson.databind.annotation.JsonSerialize(contentUsing = StringLengthSerializer.class)
    public final java.util.List<String> values = java.util.Arrays.asList("a", "four");
}

public static class StringLengthSerializer
        extends com.fasterxml.jackson.databind.JsonSerializer<String> {
    @Override
    public void serialize(String value, com.fasterxml.jackson.core.JsonGenerator gen,
            com.fasterxml.jackson.databind.SerializerProvider provider) throws java.io.IOException {
        gen.writeNumber(value.length());
    }
}