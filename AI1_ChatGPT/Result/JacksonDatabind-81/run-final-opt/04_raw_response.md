@org.junit.Test
public void serializesPrimitiveIntFieldAsNumericSupertype() throws Exception {
    class NumericBean {
        @com.fasterxml.jackson.databind.annotation.JsonSerialize(as = java.lang.Number.class)
        public int i = 13;
    }

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    org.junit.Assert.assertEquals("{\"i\":13}", mapper.writeValueAsString(new NumericBean()));
}