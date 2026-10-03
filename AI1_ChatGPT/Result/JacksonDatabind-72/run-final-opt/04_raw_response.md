@org.junit.Test
public void testIssue1501BuffersNonStaticInnerPropertyBeforeCreatorCompletes() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    Issue1501Outer result = mapper.readValue(
            "{\"a\":{\"value\":37},\"id\":12}", Issue1501Outer.class);

    org.junit.Assert.assertEquals(12, result.id);
    org.junit.Assert.assertNotNull(result.a);
    org.junit.Assert.assertEquals(37, result.a.value);
}

public static class Issue1501Outer {
    public final int id;
    public Issue1501Inner a;

    @com.fasterxml.jackson.annotation.JsonCreator
    public Issue1501Outer(@com.fasterxml.jackson.annotation.JsonProperty("id") int id) {
        this.id = id;
    }

    public class Issue1501Inner {
        public int value;
    }
}