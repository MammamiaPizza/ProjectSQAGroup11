@org.junit.Test
public void deserializesGeneratedObjectIdWithoutPhysicalIdProperty() throws Exception {
    GeneratedObjectIdPair pair = new com.fasterxml.jackson.databind.ObjectMapper().readValue(
            "{\"first\":{\"@id\":1,\"name\":\"primary\"},\"second\":1}",
            GeneratedObjectIdPair.class);

    org.junit.Assert.assertEquals("primary", pair.first.name);
    org.junit.Assert.assertSame(pair.first, pair.second);
}

@com.fasterxml.jackson.annotation.JsonIdentityInfo(
        generator = com.fasterxml.jackson.annotation.ObjectIdGenerators.IntSequenceGenerator.class,
        property = "@id")
public static class GeneratedObjectIdBean {
    public String name;
}

public static class GeneratedObjectIdPair {
    public GeneratedObjectIdBean first;
    public GeneratedObjectIdBean second;
}