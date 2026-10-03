@Test
    public void testTooSmallBigDecimalPlain() throws Exception
    {
        com.fasterxml.jackson.core.JsonFactory f = new com.fasterxml.jackson.core.JsonFactory();
        java.io.StringWriter sw = new java.io.StringWriter();
        com.fasterxml.jackson.core.JsonGenerator g = f.createGenerator(sw);
        g.enable(com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
        g.writeStartArray();
        try {
            g.writeNumber(new java.math.BigDecimal("1E-10000"));
            org.junit.Assert.fail("Should have thrown for 1E-10000");
        } catch (com.fasterxml.jackson.core.JsonGenerationException e) {
        }
        g.writeEndArray();
        g.close();
    }

 @Test
 public void testTooSmallBigDecimalPlainWithNumbersAsStrings() throws Exception
 {
     com.fasterxml.jackson.core.JsonFactory f = new com.fasterxml.jackson.core.JsonFactory();
     java.io.StringWriter sw = new java.io.StringWriter();
     com.fasterxml.jackson.core.JsonGenerator g = f.createGenerator(sw);
     g.enable(com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
     g.enable(com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS);
     g.writeStartArray();
     try {
         g.writeNumber(new java.math.BigDecimal("1E-10000"));
         org.junit.Assert.fail("Should have thrown for 1E-10000 with numbers-as-strings");
     } catch (com.fasterxml.jackson.core.JsonGenerationException e) {
     }
     g.writeEndArray();
     g.close();
 }

 @Test
 public void testBigDecimalAsStringWithoutPlain() throws Exception
 {
     com.fasterxml.jackson.core.JsonFactory f = new com.fasterxml.jackson.core.JsonFactory();
     java.io.StringWriter sw = new java.io.StringWriter();
     com.fasterxml.jackson.core.JsonGenerator g = f.createGenerator(sw);
     g.enable(com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS);
     g.writeStartArray();
     g.writeNumber(new java.math.BigDecimal("1E+10000"));
     g.writeEndArray();
     g.close();
     org.junit.Assert.assertEquals("[\"1E+10000\"]", sw.toString());
 }

 @Test
 public void testWriteRawWithOffset() throws Exception
 {
     com.fasterxml.jackson.core.JsonFactory f = new com.fasterxml.jackson.core.JsonFactory();
     java.io.StringWriter sw = new java.io.StringWriter();
     com.fasterxml.jackson.core.JsonGenerator g = f.createGenerator(sw);
     g.writeRaw("abcdef", 2, 3);
     g.close();
     org.junit.Assert.assertEquals("cde", sw.toString());
 }