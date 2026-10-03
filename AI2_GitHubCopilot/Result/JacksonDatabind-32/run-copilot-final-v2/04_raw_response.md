@Test
    public void testCustomNumberDeserializer() throws Exception {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.module.SimpleModule module = new
com.fasterxml.jackson.databind.module.SimpleModule();
        module.addDeserializer(Number.class, new
com.fasterxml.jackson.databind.JsonDeserializer<Number>() {
            @Override
            public Number deserialize(com.fasterxml.jackson.core.JsonParser p,
com.fasterxml.jackson.databind.DeserializationContext ctxt) throws java.io.IOException {
                p.nextToken();
                return 42;
            }
        });
        mapper.registerModule(module);
        Object result = mapper.readValue("123", Object.class);
        org.junit.Assert.assertEquals(42, ((Number) result).intValue());
    }

 @Test
 public void testCustomStringDeserializer() throws Exception {
     com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();
     com.fasterxml.jackson.databind.module.SimpleModule module = new
com.fasterxml.jackson.databind.module.SimpleModule();
     module.addDeserializer(String.class, new
com.fasterxml.jackson.databind.JsonDeserializer<String>() {
         @Override
         public String deserialize(com.fasterxml.jackson.core.JsonParser p,
com.fasterxml.jackson.databind.DeserializationContext ctxt) throws java.io.IOException {
             p.nextToken();
             return "custom";
         }
     });
     mapper.registerModule(module);
     Object result = mapper.readValue("\"hello\"", Object.class);
     org.junit.Assert.assertEquals("custom", result);
 }

 @Test
 public void testCustomListDeserializer() throws Exception {
     com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();
     com.fasterxml.jackson.databind.module.SimpleModule module = new
com.fasterxml.jackson.databind.module.SimpleModule();
     module.addDeserializer(java.util.List.class, new
com.fasterxml.jackson.databind.JsonDeserializer<java.util.List<?>>() {
         @Override
         public java.util.List<?> deserialize(com.fasterxml.jackson.core.JsonParser p,
com.fasterxml.jackson.databind.DeserializationContext ctxt) throws java.io.IOException {
             p.skipChildren();
             return new java.util.ArrayList();
         }
     });
     mapper.registerModule(module);
     Object result = mapper.readValue("[1,2,3]", Object.class);
     org.junit.Assert.assertTrue(result instanceof java.util.List);
     org.junit.Assert.assertEquals(0, ((java.util.List<?>) result).size());
 }

 @Test
 public void testCustomMapDeserializer() throws Exception {
     com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();
     com.fasterxml.jackson.databind.module.SimpleModule module = new
com.fasterxml.jackson.databind.module.SimpleModule();
     module.addDeserializer(java.util.Map.class, new
com.fasterxml.jackson.databind.JsonDeserializer<java.util.Map<?,?>>() {
         @Override
         public java.util.Map<?,?> deserialize(com.fasterxml.jackson.core.JsonParser p,
com.fasterxml.jackson.databind.DeserializationContext ctxt) throws java.io.IOException {
             p.skipChildren();
             return new java.util.HashMap();
         }
     });
     mapper.registerModule(module);
     Object result = mapper.readValue("{\"a\":1}", Object.class);
     org.junit.Assert.assertTrue(result instanceof java.util.Map);
     org.junit.Assert.assertEquals(0, ((java.util.Map<?,?>) result).size());
 }