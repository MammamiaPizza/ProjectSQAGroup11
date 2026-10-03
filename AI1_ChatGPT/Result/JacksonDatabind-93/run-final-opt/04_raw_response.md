@Test
public void rejectsSpringApplicationContextSubtype() throws Exception {
    final Class<?> type = org.springframework.context.support.FileSystemXmlApplicationContext.class;
    try {
        new com.fasterxml.jackson.databind.ObjectMapper()
                .enableDefaultTyping()
                .readValue("[\"" + type.getName() + "\",{}]", Object.class);
        org.junit.Assert.fail("Spring application context types must be rejected");
    } catch (com.fasterxml.jackson.databind.JsonMappingException e) {
        org.junit.Assert.assertNotNull(e.getMessage());
        org.junit.Assert.assertTrue(e.getMessage().contains(type.getName()));
    }
}