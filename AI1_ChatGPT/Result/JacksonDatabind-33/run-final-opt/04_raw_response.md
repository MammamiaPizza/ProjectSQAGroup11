@org.junit.Test
public void unwrappedGetterIsSerializedUsingContainedProperties() throws Exception {
    class Inner {
        public String value = "value";
    }
    class Outer {
        private final Inner nested = new Inner();

        @com.fasterxml.jackson.annotation.JsonUnwrapped
        public Inner getNested() {
            return nested;
        }
    }

    String json = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(new Outer());

    org.junit.Assert.assertEquals("{\"value\":\"value\"}", json);
}

@org.junit.Test
public void jsonAppendAttributeUsesConfiguredAttributeNameAndRequiredMetadata() throws Exception {
    @com.fasterxml.jackson.databind.annotation.JsonAppend(attrs = {
            @com.fasterxml.jackson.databind.annotation.JsonAppend.Attr(value = "revision", required = true)
    })
    class Bean {
        public String name = "bean";
    }

    String json = new com.fasterxml.jackson.databind.ObjectMapper()
            .writer()
            .withAttribute("revision", "1")
            .writeValueAsString(new Bean());

    org.junit.Assert.assertEquals("{\"name\":\"bean\",\"revision\":\"1\"}", json);
}