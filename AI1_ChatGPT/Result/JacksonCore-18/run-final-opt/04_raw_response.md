@org.junit.Test
public void testUtf8GeneratorWritesMaximumPositivePlainBigDecimalScale() throws Exception {
    assertBoundaryPlainBigDecimalWritten(new java.math.BigDecimal("1E+9999"));
}

@org.junit.Test
public void testUtf8GeneratorWritesMaximumNegativePlainBigDecimalScale() throws Exception {
    assertBoundaryPlainBigDecimalWritten(new java.math.BigDecimal("1E-9999"));
}

private void assertBoundaryPlainBigDecimalWritten(java.math.BigDecimal value) throws Exception {
    com.fasterxml.jackson.core.JsonFactory factory = new com.fasterxml.jackson.core.JsonFactory();

    java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
    com.fasterxml.jackson.core.JsonGenerator utf8Generator = factory.createGenerator(
            bytes, com.fasterxml.jackson.core.JsonEncoding.UTF8);
    utf8Generator.enable(com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
    utf8Generator.writeNumber(value);
    utf8Generator.close();
    org.junit.Assert.assertEquals(value.toPlainString(), new String(bytes.toByteArray(), "UTF-8"));

    java.io.StringWriter writer = new java.io.StringWriter();
    com.fasterxml.jackson.core.JsonGenerator writerGenerator = factory.createGenerator(writer);
    writerGenerator.enable(com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
    writerGenerator.writeNumber(value);
    writerGenerator.close();
    org.junit.Assert.assertEquals(value.toPlainString(), writer.toString());
}