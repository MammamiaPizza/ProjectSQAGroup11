@org.junit.Test
public void deserializesAnySetterWhileIgnoringExplicitlyIgnoredProperty() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    AnySetterBean bean = mapper.readValue(
            "{\"known\":3,\"extra\":\"value\",\"ignored\":true}", AnySetterBean.class);

    org.junit.Assert.assertEquals(3, bean.known);
    org.junit.Assert.assertEquals("value", bean.other.get("extra"));
    org.junit.Assert.assertFalse(bean.other.containsKey("ignored"));
}

@org.junit.Test
public void deserializesCollectionThroughGetterAsSetter() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    GetterCollectionBean bean = mapper.readValue("{\"values\":[2,5]}", GetterCollectionBean.class);

    org.junit.Assert.assertEquals(java.util.Arrays.asList(2, 5), bean.getValues());
}

@org.junit.Test
public void deserializesCreatorPropertyAlongsideRegularProperty() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    CreatorBean bean = mapper.readValue("{\"id\":17,\"name\":\"creator\"}", CreatorBean.class);

    org.junit.Assert.assertEquals(17, bean.id);
    org.junit.Assert.assertEquals("creator", bean.name);
}

@com.fasterxml.jackson.annotation.JsonIgnoreProperties({ "ignored" })
public static class AnySetterBean {
    public int known;
    public final java.util.Map<String, Object> other = new java.util.LinkedHashMap<String, Object>();

    @com.fasterxml.jackson.annotation.JsonAnySetter
    public void add(String name, Object value) {
        other.put(name, value);
    }
}

public static class GetterCollectionBean {
    private final java.util.List<Integer> values = new java.util.ArrayList<Integer>();

    public java.util.List<Integer> getValues() {
        return values;
    }
}

public static class CreatorBean {
    public final int id;
    public String name;

    @com.fasterxml.jackson.annotation.JsonCreator
    public CreatorBean(@com.fasterxml.jackson.annotation.JsonProperty("id") int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}