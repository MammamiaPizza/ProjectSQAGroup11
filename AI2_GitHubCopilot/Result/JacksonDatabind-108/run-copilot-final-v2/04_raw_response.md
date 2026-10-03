@Test
 public void testWithJsonFactoryReadTree() throws Exception {
     com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();
     com.fasterxml.jackson.databind.ObjectReader baseReader = mapper.reader();
     com.fasterxml.jackson.core.JsonFactory newFactory = new
com.fasterxml.jackson.core.JsonFactory();
     com.fasterxml.jackson.databind.ObjectReader factoryReader = baseReader.with(newFactory);
     com.fasterxml.jackson.databind.JsonNode result = factoryReader.readTree("{\"a\":1}");
     org.junit.Assert.assertNotNull("Should get non-null result from factory‑wired reader", result);
     org.junit.Assert.assertTrue("Result should be ObjectNode", result.isObject());
     org.junit.Assert.assertEquals(1, result.get("a").asInt());
 }

 @Test
 public void testDeserializationFeatureConstructor() throws Exception {
     com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();
     com.fasterxml.jackson.databind.ObjectReader reader = mapper.reader();
     com.fasterxml.jackson.databind.ObjectReader featureReader =
reader.with(com.fasterxml.jackson.databind.DeserializationFeature.INDENT_OUTPUT);
     com.fasterxml.jackson.databind.JsonNode node = featureReader.readTree("{\"b\":2}");
     org.junit.Assert.assertNotNull(node);
     org.junit.Assert.assertTrue(node.isObject());
 }

 @Test
 public void testReadTreeFromFile() throws Exception {
     com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();
     com.fasterxml.jackson.databind.ObjectReader reader = mapper.reader();
     java.io.File tempFile = java.io.File.createTempFile("jacksonTest", ".json");
     tempFile.deleteOnExit();
     java.io.FileWriter fw = new java.io.FileWriter(tempFile);
     fw.write("{\"key\":\"value\"}");
     fw.close();
     com.fasterxml.jackson.databind.JsonNode result = reader.readTree(tempFile);
     org.junit.Assert.assertNotNull("File read must not return null", result);
     org.junit.Assert.assertTrue("Result should be ObjectNode", result.isObject());
     org.junit.Assert.assertEquals("value", result.get("key").asText());
 }

 @Test
 public void testReadTreeFromReader() throws Exception {
     com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();
     com.fasterxml.jackson.databind.ObjectReader reader = mapper.reader();
     java.io.StringReader sr = new java.io.StringReader("{\"x\":123}");
     com.fasterxml.jackson.databind.JsonNode node = reader.readTree(sr);
     org.junit.Assert.assertNotNull(node);
     org.junit.Assert.assertTrue(node.isObject());
     org.junit.Assert.assertEquals(123, node.get("x").asInt());
 }