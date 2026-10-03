@Test
    public void testUnwrapSingleValueArrayLocale() throws Exception {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();

mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,
true);
        Locale result = mapper.readValue("["en_US"]", Locale.class);
        assertEquals(new Locale("en", "US"), result);
    }

 @Test(expected = com.fasterxml.jackson.core.JsonProcessingException.class)
 public void testUnwrapSingleValueArrayMultipleValuesThrowsException() throws Exception {
     com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();

mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,
true);
     mapper.readValue("[\"en_US\", \"en_UK\"]", Locale.class);
 }

 @Test(expected = com.fasterxml.jackson.databind.JsonMappingException.class)
 public void testInvalidLocaleThrowsException() throws Exception {
     com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();
     mapper.readValue("\"xx-yy-zzz\"", Locale.class);
 }

 @Test
 public void testFileEmptyStringAsNullWithAcceptFeature() throws Exception {
     com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();
     mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_STRING_AS_N
ULL_OBJECT, true);
     java.io.File result = mapper.readValue("\"\"", java.io.File.class);
     assertNull(result);
 }