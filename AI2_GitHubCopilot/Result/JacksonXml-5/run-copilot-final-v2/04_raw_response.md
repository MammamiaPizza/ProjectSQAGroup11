@Test
    public void testCopySerializeRootArray() throws Exception {
        XmlMapper mapper = xmlMapper.copy();
        String xml = mapper.writeValueAsString(java.util.Arrays.asList("a", "b"));
        assertNotNull(xml);
        assertTrue("Root array must contain <item>: " + xml, xml.contains("<item>"));
    }

 @Test
 public void testCopyConvertValue() {
     XmlMapper mapper = xmlMapper.copy();
     java.util.Map<?,?> result = mapper.convertValue(new Pojo282(3), java.util.Map.class);
     assertNotNull(result);
     assertEquals(3, result.get("a"));
 }

 @Test
 public void testCopyWithNamespaceRootName() throws Exception {
     XmlMapper mapper = xmlMapper.copy();
     String xml = mapper.writeValueAsString(new NsPojo282());
     assertNotNull(xml);
     assertTrue("Namespace must be set: " + xml,
             xml.contains("xmlns=\"]8;id=md-1pbnbbu;http://example.com/nshttp://example.com/ns]8;;]8;;\""));]8;;
 }

 @Test
 public void testSerializeExceptionNullMessage() {
     XmlMapper mapper = xmlMapper.copy();
     try {
         mapper.writeValueAsString(new BadBean());
         fail("Expected exception");
     } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
         assertNotNull(e.getMessage());
     }
 }

 @com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement(namespace="]8;id=md-1pbnbbu;http://example.com/nshttp://example.co]8;;
]8;id=md-1pbnbbu;http://example.com/nsm/ns]8;;]8;;")]8;;
 private static class NsPojo282 {
     public int getA() { return 5; }
 }

 private static class BadBean {
     public String getBad() { throw new NullPointerException(); }
 }