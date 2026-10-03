@org.junit.Test
public void explicitBooleanCreatorIsUsedForBooleanInput() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    AdditionalBooleanCreator value = mapper.readValue("true", AdditionalBooleanCreator.class);

    org.junit.Assert.assertTrue(value.value);
}

@org.junit.Test
public void explicitDoubleCreatorIsUsedForFloatingPointInput() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    AdditionalDoubleCreator value = mapper.readValue("2.75", AdditionalDoubleCreator.class);

    org.junit.Assert.assertEquals(2.75d, value.value, 0.0d);
}

@org.junit.Test
public void explicitCollectionDelegatingCreatorReceivesArrayContents() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    AdditionalCollectionDelegatingCreator value = mapper.readValue("[\"first\",\"second\"]",
            AdditionalCollectionDelegatingCreator.class);

    org.junit.Assert.assertEquals(2, value.values.size());
    org.junit.Assert.assertEquals("first", value.values.get(0));
    org.junit.Assert.assertEquals("second", value.values.get(1));
}

public static class AdditionalBooleanCreator {
    public final boolean value;

    @com.fasterxml.jackson.annotation.JsonCreator
    public AdditionalBooleanCreator(boolean value) {
        this.value = value;
    }
}

public static class AdditionalDoubleCreator {
    public final double value;

    @com.fasterxml.jackson.annotation.JsonCreator
    public AdditionalDoubleCreator(double value) {
        this.value = value;
    }
}

public static class AdditionalCollectionDelegatingCreator {
    public final java.util.List<String> values;

    @com.fasterxml.jackson.annotation.JsonCreator
    public AdditionalCollectionDelegatingCreator(java.util.List<String> values) {
        this.values = values;
    }
}