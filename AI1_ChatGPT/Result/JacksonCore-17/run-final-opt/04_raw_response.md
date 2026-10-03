@Test
public void escapeNonAsciiFeatureEscapesUtf8StringContent() throws Exception {
    com.fasterxml.jackson.core.JsonFactory factory = new com.fasterxml.jackson.core.JsonFactory();
    factory.enable(com.fasterxml.jackson.core.JsonGenerator.Feature.ESCAPE_NON_ASCII);
    java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
    com.fasterxml.jackson.core.JsonGenerator generator = factory.createGenerator(out);

    generator.writeString("\u00E9");
    generator.close();

    org.junit.Assert.assertArrayEquals("\\\"\\\\u00E9\\\"".getBytes("UTF-8"), out.toByteArray());
}

@Test
public void writeRawValuePreservesSurrogatePairSplitAtConcatBufferBoundary() throws Exception {
    StringBuilder raw = new StringBuilder();
    raw.append('"');
    for (int i = 0; i < 3998; ++i) {
        raw.append('a');
    }
    raw.append('\uD83D');
    raw.append('\uDE00');
    raw.append('"');

    java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
    com.fasterxml.jackson.core.JsonGenerator generator =
            new com.fasterxml.jackson.core.JsonFactory().createGenerator(out);
    generator.writeStartArray();
    generator.writeRawValue(raw.toString());
    generator.writeEndArray();
    generator.close();

    org.junit.Assert.assertArrayEquals(
            ("[" + raw.toString() + "]").getBytes("UTF-8"), out.toByteArray());
}