@org.junit.Test
public void readerWithReplacementJsonFactoryUsesItsParserFeatures() throws Exception {
    com.fasterxml.jackson.core.JsonFactory factory = new com.fasterxml.jackson.core.JsonFactory();
    factory.enable(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES);

    com.fasterxml.jackson.databind.ObjectReader reader =
            new com.fasterxml.jackson.databind.ObjectMapper().reader().with(factory);
    com.fasterxml.jackson.databind.JsonNode result = reader.readTree("{'answer':42}");

    org.junit.Assert.assertNotNull(result);
    org.junit.Assert.assertEquals(42, result.get("answer").asInt());
}