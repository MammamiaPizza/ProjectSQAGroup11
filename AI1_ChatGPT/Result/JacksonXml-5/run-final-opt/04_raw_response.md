@org.junit.Test
public void testNullValueUsesNullRootElement() throws Exception {
    String xml = new com.fasterxml.jackson.dataformat.xml.XmlMapper().writeValueAsString(null);

    org.junit.Assert.assertEquals("<null/>", xml);
}

@org.junit.Test
public void testRootArrayUsesItemElements() throws Exception {
    String xml = new com.fasterxml.jackson.dataformat.xml.XmlMapper()
            .writeValueAsString(new String[] { "a", "b" });

    org.junit.Assert.assertTrue(xml.contains("<item>a</item>"));
    org.junit.Assert.assertTrue(xml.contains("<item>b</item>"));
}

@org.junit.Test
public void testSerializeValueRejectsNonXmlGenerator() throws Exception {
    com.fasterxml.jackson.dataformat.xml.ser.XmlSerializerProvider provider =
            new com.fasterxml.jackson.dataformat.xml.ser.XmlSerializerProvider(null);
    java.io.StringWriter output = new java.io.StringWriter();
    com.fasterxml.jackson.core.JsonGenerator generator =
            new com.fasterxml.jackson.core.JsonFactory().createGenerator(output);
    try {
        provider.serializeValue(generator, "value");
        org.junit.Assert.fail("Expected JsonMappingException");
    } catch (com.fasterxml.jackson.databind.JsonMappingException e) {
        org.junit.Assert.assertTrue(e.getMessage().contains(
                "XmlMapper does not with generators of type other than ToXmlGenerator"));
    } finally {
        generator.close();
    }
}