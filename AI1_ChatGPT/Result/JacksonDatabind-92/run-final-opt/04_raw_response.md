@org.junit.Test
public void rejectsJdbcRowSetImplWhenUsedAsDefaultTypedValue() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.enableDefaultTyping();

    boolean rejected = false;
    try {
        mapper.readValue("{\"@class\":\"com.sun.rowset.JdbcRowSetImpl\"}", Object.class);
    } catch (Exception e) {
        rejected = true;
    }

    org.junit.Assert.assertTrue("Dangerous JDK JdbcRowSetImpl type must not deserialize successfully", rejected);
}