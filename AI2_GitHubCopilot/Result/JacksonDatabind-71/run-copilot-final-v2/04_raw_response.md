@Test
public void testParseBooleanKeyTrueFalseAndInvalid() throws Exception {
    DeserializationContext ctxt = mockCtxt();
    StdKeyDeserializer deser = StdKeyDeserializer.forType(Boolean.class);
    assertEquals(Boolean.TRUE, deser.deserializeKey("true", ctxt));
    assertEquals(Boolean.FALSE, deser.deserializeKey("false", ctxt));
    try {
        deser.deserializeKey("tru", ctxt);
        fail("Should throw exception on invalid boolean key");
    } catch (com.fasterxml.jackson.databind.JsonMappingException expected) { }
}

@Test
public void testParseByteKeyWithinRangeAndOverflow() throws Exception {
    DeserializationContext ctxt = mockCtxt();
    StdKeyDeserializer deser = StdKeyDeserializer.forType(Byte.class);
    assertEquals(Byte.valueOf((byte) 100), deser.deserializeKey("100", ctxt));
    try {
        deser.deserializeKey("256", ctxt);
        fail("Should throw exception on byte overflow");
    } catch (com.fasterxml.jackson.databind.JsonMappingException expected) { }
}

@Test
public void testParseShortKeyWithinRangeAndOverflow() throws Exception {
    DeserializationContext ctxt = mockCtxt();
    StdKeyDeserializer deser = StdKeyDeserializer.forType(Short.class);
    assertEquals(Short.valueOf((short) 30000), deser.deserializeKey("30000", ctxt));
    try {
        deser.deserializeKey("32768", ctxt);
        fail("Should throw exception on short overflow");
    } catch (com.fasterxml.jackson.databind.JsonMappingException expected) { }
}

@Test
public void testParseLocaleKey() throws Exception {
    DeserializationContext ctxt = mockCtxt();
    StdKeyDeserializer deser = StdKeyDeserializer.forType(java.util.Locale.class);
    Object result = deser.deserializeKey("en_US", ctxt);
    assertNotNull(result);
    assertTrue(result instanceof java.util.Locale);
    java.util.Locale loc = (java.util.Locale) result;
    assertEquals("en", loc.getLanguage());
}

private DeserializationContext mockCtxt() {
    DeserializationContext ctxt =
org.powermock.api.mockito.PowerMockito.mock(DeserializationContext.class);
    com.fasterxml.jackson.databind.JsonMappingException ex = new
com.fasterxml.jackson.databind.JsonMappingException("mock");
    org.powermock.api.mockito.PowerMockito.when(ctxt.weirdKeyException(
            org.mockito.Mockito.any(Class.class),
            org.mockito.Mockito.any(String.class),
            org.mockito.Mockito.any(String.class))).thenReturn(ex);
    return ctxt;
}