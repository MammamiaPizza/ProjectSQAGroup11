@org.junit.Test
public void testJsonPropertyNamespaceKeepsLocalJsonFieldName() throws Exception {
    class NamespacedPropertyBean {
        @com.fasterxml.jackson.annotation.JsonProperty(value = "renamed", namespace = "urn:test")
        public String original = "value";
    }

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    org.junit.Assert.assertEquals("{\"renamed\":\"value\"}",
            mapper.writeValueAsString(new NamespacedPropertyBean()));
}

@org.junit.Test
public void testRootNameNamespaceKeepsLocalJsonRootName() throws Exception {
    @com.fasterxml.jackson.annotation.JsonRootName(value = "root", namespace = "urn:test")
    class NamespacedRootBean {
        public int number = 1;
    }

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.enable(com.fasterxml.jackson.databind.SerializationFeature.WRAP_ROOT_VALUE);
    org.junit.Assert.assertEquals("{\"root\":{\"number\":1}}",
            mapper.writeValueAsString(new NamespacedRootBean()));
}

@org.junit.Test
public void testJsonIgnorePropertiesExcludesNamedPropertyDuringSerialization() throws Exception {
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({ "hidden" })
    class IgnoredPropertyBean {
        public String visible = "shown";
        public String hidden = "secret";
    }

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    org.junit.Assert.assertEquals("{\"visible\":\"shown\"}",
            mapper.writeValueAsString(new IgnoredPropertyBean()));
}