@org.junit.Test
public void honorsIgnoredPropertiesAndIgnoreUnknownAnnotation() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    IgnoredPropertiesBean value = mapper.readValue(
            "{\"visible\":3,\"hidden\":9,\"unknown\":12}", IgnoredPropertiesBean.class);

    org.junit.Assert.assertEquals(3, value.getVisible());
    org.junit.Assert.assertEquals(0, value.getHidden());
}

@org.junit.Test
public void usesAnySetterForUnrecognizedProperties() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    AnySetterBean value = mapper.readValue("{\"known\":\"ok\",\"extra\":7}", AnySetterBean.class);

    org.junit.Assert.assertEquals("ok", value.known);
    org.junit.Assert.assertEquals(java.lang.Integer.valueOf(7), value.other.get("extra"));
}

@org.junit.Test
public void populatesCollectionExposedOnlyByGetter() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    SetterlessCollectionBean value = mapper.readValue("{\"values\":[\"a\",\"b\"]}",
            SetterlessCollectionBean.class);

    org.junit.Assert.assertEquals(java.util.Arrays.asList("a", "b"), value.getValues());
}

@com.fasterxml.jackson.annotation.JsonIgnoreProperties(value = { "hidden" }, ignoreUnknown = true)
public static class IgnoredPropertiesBean {
    private int visible;
    private int hidden;

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public int getHidden() {
        return hidden;
    }

    public void setHidden(int hidden) {
        this.hidden = hidden;
    }
}

public static class AnySetterBean {
    public String known;
    public final java.util.Map<String, Object> other =
            new java.util.LinkedHashMap<String, Object>();

    @com.fasterxml.jackson.annotation.JsonAnySetter
    public void add(String name, Object value) {
        other.put(name, value);
    }
}

public static class SetterlessCollectionBean {
    private final java.util.List<String> values = new java.util.ArrayList<String>();

    public java.util.List<String> getValues() {
        return values;
    }
}