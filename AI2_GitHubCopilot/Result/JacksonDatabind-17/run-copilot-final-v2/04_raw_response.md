@Test
    public void testObjectMapperConstructorWithJsonFactory() throws Exception {
        com.fasterxml.jackson.core.JsonFactory jf = new com.fasterxml.jackson.core.JsonFactory();
        ObjectMapper mapper = new ObjectMapper(jf);
        String json = mapper.writeValueAsString(new int[]{1, 2, 3});
        assertNotNull(json);
        com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(json);
        assertTrue(node.isArray());
        assertEquals(3, node.size());
    }

 @Test
 public void testCopyObjectMapper() throws Exception {
     ObjectMapper mapper = new ObjectMapper();
     ObjectMapper copy = mapper.copy();
     assertNotSame(mapper, copy);
     String json = copy.writeValueAsString("test");
     assertEquals("\"test\"", json);
     mapper.configure(com.fasterxml.jackson.databind.MapperFeature.REQUIRE_SETTERS_FOR_GETTERS,
true);
     copy = mapper.copy();
     assertTrue(copy.isEnabled(com.fasterxml.jackson.databind.MapperFeature.REQUIRE_SETTERS_FOR_GETT
ERS));
 }

 @Test(expected = IllegalStateException.class)
 public void testCopyWithoutOverrideThrows() {
     ObjectMapper mapper = new ObjectMapper() {};
     mapper.copy();
 }

 @Test
 public void testDefaultTypingObjectOnlyDoesNotBreakJsonNode() throws Exception {
     ObjectMapper mapper = new ObjectMapper();
     mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT);
     com.fasterxml.jackson.databind.node.ArrayNode node = mapper.createArrayNode();
     node.add(42);
     String json = mapper.writeValueAsString(node);
     assertFalse(json.contains("\"@class\""));
     com.fasterxml.jackson.databind.JsonNode result = mapper.readTree(json);
     assertNotNull(result);
     assertTrue(result.isArray());
     assertEquals(1, result.size());
     assertEquals(42, result.get(0).intValue());
 }