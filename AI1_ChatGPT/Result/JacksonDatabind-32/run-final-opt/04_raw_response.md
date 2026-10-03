@org.junit.Test
public void testUntypedScalarValuesDeserializeToNaturalJavaValues() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    org.junit.Assert.assertEquals("text", mapper.readValue("\"text\"", Object.class));
    org.junit.Assert.assertEquals(java.lang.Boolean.TRUE, mapper.readValue("true", Object.class));
    org.junit.Assert.assertEquals(java.lang.Boolean.FALSE, mapper.readValue("false", Object.class));
    org.junit.Assert.assertNull(mapper.readValue("null", Object.class));

    Object integer = mapper.readValue("37", Object.class);
    org.junit.Assert.assertTrue(integer instanceof java.lang.Integer);
    org.junit.Assert.assertEquals(37, ((java.lang.Integer) integer).intValue());

    Object decimal = mapper.readValue("1.25", Object.class);
    org.junit.Assert.assertTrue(decimal instanceof java.lang.Double);
    org.junit.Assert.assertEquals(1.25d, ((java.lang.Double) decimal).doubleValue(), 0.0d);
}