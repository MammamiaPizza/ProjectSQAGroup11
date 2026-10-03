@org.junit.Test
public void jsonValueEnumFormatVisitorUsesJsonValueAccessorResults() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    EnumValueFormatVisitor visitor = new EnumValueFormatVisitor();

    mapper.acceptJsonFormatVisitor(JsonValueEnum.class, visitor);

    org.junit.Assert.assertEquals(
            new java.util.LinkedHashSet<String>(java.util.Arrays.asList("first-value", "second-value")),
            visitor.values);
}

@org.junit.Test
public void jsonValueEnumFormatVisitorWrapsAccessorFailures() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    try {
        mapper.acceptJsonFormatVisitor(FailingJsonValueEnum.class, new EnumValueFormatVisitor());
        org.junit.Assert.fail("Expected JsonMappingException");
    } catch (com.fasterxml.jackson.databind.JsonMappingException e) {
        org.junit.Assert.assertTrue(e.getMessage().contains("value()"));
        org.junit.Assert.assertTrue(e.getCause() instanceof IllegalStateException);
    }
}

@org.junit.Test
public void jsonValueEnumFormatVisitorPropagatesAccessorErrors() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    try {
        mapper.acceptJsonFormatVisitor(ErrorJsonValueEnum.class, new EnumValueFormatVisitor());
        org.junit.Assert.fail("Expected AssertionError");
    } catch (AssertionError e) {
        org.junit.Assert.assertEquals("json value error", e.getMessage());
    }
}

@org.junit.Test
public void naturalIntegerJsonValueRetainsBeanTypeWithDefaultTyping() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.enableDefaultTyping(com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL);

    String json = mapper.writeValueAsString(new CreatorBackedIntegerValue(37));
    CreatorBackedIntegerValue restored = mapper.readValue(json, CreatorBackedIntegerValue.class);

    org.junit.Assert.assertEquals(CreatorBackedIntegerValue.class, restored.getClass());
    org.junit.Assert.assertEquals(37, restored.value());
}

private static class EnumValueFormatVisitor
        extends com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper.Base {
    java.util.Set<String> values;

    @Override
    public com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor expectStringFormat(
            com.fasterxml.jackson.databind.JavaType type) {
        return new com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor.Base() {
            @Override
            public void enumTypes(java.util.Set<String> enums) {
                values = new java.util.LinkedHashSet<String>(enums);
            }
        };
    }
}

private enum JsonValueEnum {
    FIRST("first-value"),
    SECOND("second-value");

    private final String value;

    JsonValueEnum(String value) {
        this.value = value;
    }

    @com.fasterxml.jackson.annotation.JsonValue
    public String value() {
        return value;
    }
}

private enum FailingJsonValueEnum {
    VALUE;

    @com.fasterxml.jackson.annotation.JsonValue
    public String value() {
        throw new IllegalStateException("json value failure");
    }
}

private enum ErrorJsonValueEnum {
    VALUE;

    @com.fasterxml.jackson.annotation.JsonValue
    public String value() {
        throw new AssertionError("json value error");
    }
}

public static class CreatorBackedIntegerValue {
    private final int value;

    @com.fasterxml.jackson.annotation.JsonCreator
    public CreatorBackedIntegerValue(int value) {
        this.value = value;
    }

    @com.fasterxml.jackson.annotation.JsonValue
    public int value() {
        return value;
    }
}