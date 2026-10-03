@org.junit.Test
public void testWriteObjectWithIntegralNumberTypes() throws Exception {
    java.io.StringWriter output = new java.io.StringWriter();
    com.fasterxml.jackson.core.JsonGenerator generator =
            new com.fasterxml.jackson.core.JsonFactory().createGenerator(output);

    generator.writeStartArray();
    generator.writeObject(java.lang.Integer.valueOf(-7));
    generator.writeObject(java.lang.Long.valueOf(9000000000L));
    generator.writeObject(java.lang.Short.valueOf((short) 3));
    generator.writeObject(java.lang.Byte.valueOf((byte) 4));
    generator.writeObject(new java.math.BigInteger("12345678901234567890"));
    generator.writeEndArray();
    generator.close();

    org.junit.Assert.assertEquals("[-7,9000000000,3,4,12345678901234567890]",
            output.toString());
}

@org.junit.Test
public void testWriteObjectWithSimpleScalarValues() throws Exception {
    java.io.StringWriter output = new java.io.StringWriter();
    com.fasterxml.jackson.core.JsonGenerator generator =
            new com.fasterxml.jackson.core.JsonFactory().createGenerator(output);

    generator.writeStartArray();
    generator.writeObject("text");
    generator.writeObject(java.lang.Character.valueOf('x'));
    generator.writeObject(java.lang.Boolean.TRUE);
    generator.writeObject(java.lang.Double.valueOf(2.5));
    generator.writeObject(java.lang.Float.valueOf(1.25f));
    generator.writeObject(new java.math.BigDecimal("12.75"));
    generator.writeObject((java.lang.Object) null);
    generator.writeEndArray();
    generator.close();

    org.junit.Assert.assertEquals("[\"text\",\"x\",true,2.5,1.25,12.75,null]",
            output.toString());
}