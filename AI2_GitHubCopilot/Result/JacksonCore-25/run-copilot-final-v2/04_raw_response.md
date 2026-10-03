@Test(expected = com.fasterxml.jackson.core.JsonParseException.class)
    public void testMismatchedEndArrayBrace() throws Exception {
        com.fasterxml.jackson.core.JsonParser p = new
com.fasterxml.jackson.core.JsonFactory().createParser("[}");
        com.fasterxml.jackson.core.JsonToken t = p.nextToken();
        assertEquals(com.fasterxml.jackson.core.JsonToken.START_ARRAY, t);
        p.nextToken(); // should throw
        fail("Expected parse exception for mismatched end marker");
    }

 @Test(expected = com.fasterxml.jackson.core.JsonParseException.class)
 public void testInvalidKeywordTrueWithIdentifierPart() throws Exception {
     com.fasterxml.jackson.core.JsonFactory f = new com.fasterxml.jackson.core.JsonFactory();
     // disable unquoted names so "truel" is not treated as field name
     com.fasterxml.jackson.core.JsonParser p = f.createParser("truel");
     p.nextToken();
     fail("Expected parse exception for invalid token");
 }

 @Test
 public void testBase64DecodingWithLeadingWhitespace() throws Exception {
     com.fasterxml.jackson.core.JsonFactory f = new com.fasterxml.jackson.core.JsonFactory();
     com.fasterxml.jackson.core.JsonParser p = f.createParser("{\"d\":\" SGVsbG8=\"}");
     assertEquals(com.fasterxml.jackson.core.JsonToken.START_OBJECT, p.nextToken());
     assertEquals(com.fasterxml.jackson.core.JsonToken.FIELD_NAME, p.nextToken());
     assertEquals("d", p.getCurrentName());
     assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_STRING, p.nextToken());
     byte[] data = p.getBinaryValue(com.fasterxml.jackson.core.Base64Variant.MIME);
     assertEquals("Hello", new String(data, "UTF-8"));
     assertEquals(com.fasterxml.jackson.core.JsonToken.END_OBJECT, p.nextToken());
 }

 @Test
 public void testUnquotedNameWithCharacter257() throws Exception {
     com.fasterxml.jackson.core.JsonFactory f = new com.fasterxml.jackson.core.JsonFactory();
     f.enable(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
     com.fasterxml.jackson.core.JsonParser p = f.createParser("{\u0101:\"val\"}");
     assertEquals(com.fasterxml.jackson.core.JsonToken.START_OBJECT, p.nextToken());
     assertEquals(com.fasterxml.jackson.core.JsonToken.FIELD_NAME, p.nextToken());
     assertEquals("\u0101", p.getCurrentName());
     assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_STRING, p.nextToken());
     assertEquals("val", p.getText());
     assertEquals(com.fasterxml.jackson.core.JsonToken.END_OBJECT, p.nextToken());
 }