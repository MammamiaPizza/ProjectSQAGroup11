@Test
public void testVerifyOffsetsNegativeOffset() throws Exception {
    byte[] data = new byte[10];
    try {
        gen.writeBinary(data, -1, 5);
        fail("Expected IllegalArgumentException for negative offset");
    } catch (IllegalArgumentException e) {
        // expected
    }
}

@Test
public void testVerifyOffsetsLengthExceeds() throws Exception {
    byte[] data = new byte[10];
    try {
        gen.writeBinary(data, 5, 6);
        fail("Expected IllegalArgumentException for offset+length > data.length");
    } catch (IllegalArgumentException e) {
        // expected
    }
}

@Test
public void testWriteSimpleObjectIntegerAndLong() throws Exception {
    StringWriter sw = new StringWriter();
    com.fasterxml.jackson.core.JsonFactory f = new com.fasterxml.jackson.core.JsonFactory();
    com.fasterxml.jackson.core.JsonGenerator g = f.createGenerator(sw);
    g.writeStartArray();
    g.writeObject(Integer.valueOf(42));
    g.writeObject(Long.valueOf(123456789012345L));
    g.writeEndArray();
    g.close();
    assertEquals("[42,123456789012345]", sw.toString());
}

@Test
public void testWriteSimpleObjectShortByteAndBigInt() throws Exception {
    StringWriter sw = new StringWriter();
    com.fasterxml.jackson.core.JsonFactory f = new com.fasterxml.jackson.core.JsonFactory();
    com.fasterxml.jackson.core.JsonGenerator g = f.createGenerator(sw);
    g.writeStartArray();
    g.writeObject(Short.valueOf((short) 1));
    g.writeObject(Byte.valueOf((byte) 2));
    g.writeObject(new java.math.BigInteger("12345678901234567890"));
    g.writeObject(new java.math.BigDecimal("3.14159"));
    g.writeEndArray();
    g.close();
    assertEquals("[1,2,12345678901234567890,3.14159]", sw.toString());
}