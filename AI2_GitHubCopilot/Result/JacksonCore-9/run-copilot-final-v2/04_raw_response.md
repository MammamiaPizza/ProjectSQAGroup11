@Test
    public void testNextFieldNameGetValueAsStringBytes() throws Exception {
        JsonFactory f = new JsonFactory();
        JsonParser p = f.createParser(new byte[] { '{', '"', 'a', '"', ':', '1', '}' });
        assertToken(JsonToken.START_OBJECT, p.nextToken());
        String fieldName = p.nextFieldName();
        assertEquals("a", fieldName);
        assertEquals("a", p.getValueAsString());
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        p.close();
    }

 @Test
 public void testNextFieldNameGetValueAsStringChars() throws Exception {
     JsonFactory f = new JsonFactory();
     JsonParser p = f.createParser(new java.io.StringReader("{\"a\":1}"));
     assertToken(JsonToken.START_OBJECT, p.nextToken());
     String fieldName = p.nextFieldName();
     assertEquals("a", fieldName);
     assertEquals("a", p.getValueAsString());
     assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
     p.close();
 }

 @Test
 public void testBinaryValueDecoding() throws Exception {
     JsonFactory f = new JsonFactory();
     JsonParser p = f.createParser("{\"data\":\"dGVzdA==\"}".getBytes("UTF-8"));
     assertToken(JsonToken.START_OBJECT, p.nextToken());
     assertToken(JsonToken.FIELD_NAME, p.nextToken());
     assertEquals("data", p.getValueAsString());
     assertToken(JsonToken.VALUE_STRING, p.nextToken());
     byte[] data = p.getBinaryValue(com.fasterxml.jackson.core.Base64Variants.getDefaultVariant());
     assertArrayEquals(new byte[] { 't', 'e', 's', 't' }, data);
     p.close();
 }

 @Test(expected = com.fasterxml.jackson.core.JsonParseException.class)
 public void testInvalidNumberThrowsException() throws Exception {
     JsonFactory f = new JsonFactory();
     JsonParser p = f.createParser("1.a".getBytes("UTF-8"));
     try {
         p.nextToken();
     } finally {
         p.close();
     }
 }