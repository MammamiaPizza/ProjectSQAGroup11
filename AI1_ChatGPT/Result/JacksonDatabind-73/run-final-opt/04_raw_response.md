@org.junit.Test
public void readOnlyGetterPreventsMatchingSetterFromBindingInput() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    AccessorReadOnlyBean value = mapper.readValue("{\"value\":9}", AccessorReadOnlyBean.class);

    org.junit.Assert.assertEquals(3, value.getValue());
}

public static class AccessorReadOnlyBean {
    private int value = 3;

    @com.fasterxml.jackson.annotation.JsonProperty(access = com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY)
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}