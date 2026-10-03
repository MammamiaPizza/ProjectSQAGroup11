@Test
 public void testBaseDeserializeFromEmptyStringReturnsNull() throws Exception {
     com.fasterxml.jackson.databind.deser.std.FromStringDeserializer<String> deser =
             new
com.fasterxml.jackson.databind.deser.std.FromStringDeserializer<String>(String.class) {
         @Override
         protected String _deserialize(String value,
com.fasterxml.jackson.databind.DeserializationContext ctxt) {
             return value;
         }
     };
     com.fasterxml.jackson.databind.module.SimpleModule module =
             new com.fasterxml.jackson.databind.module.SimpleModule();
     module.addDeserializer(String.class, deser);
     com.fasterxml.jackson.databind.ObjectMapper mapper =
             new com.fasterxml.jackson.databind.ObjectMapper();
     mapper.registerModule(module);
     String result = mapper.readValue("\"\"", String.class);
     org.junit.Assert.assertNull(result);
 }

 @Test
 public void testUnwrapSingleValueArraySuccess() throws Exception {
     com.fasterxml.jackson.databind.ObjectMapper mapper =
             new com.fasterxml.jackson.databind.ObjectMapper();
     mapper.configure(
             com.fasterxml.jackson.databind.DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,
true);
     java.util.Locale locale = mapper.readValue("[\"en\"]", java.util.Locale.class);
     org.junit.Assert.assertEquals(new java.util.Locale("en"), locale);
 }

 @Test(expected = com.fasterxml.jackson.databind.JsonMappingException.class)
 public void testUnwrapSingleValueArrayMultipleValuesThrows() throws Exception {
     com.fasterxml.jackson.databind.ObjectMapper mapper =
             new com.fasterxml.jackson.databind.ObjectMapper();
     mapper.configure(
             com.fasterxml.jackson.databind.DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,
true);
     mapper.readValue("[\"en\",\"US\"]", java.util.Locale.class);
 }

 @Test
 public void testFindDeserializerCoversAllStandardTypes() {
     java.lang.Class<?>[] types = {
             java.io.File.class,
             java.net.URL.class,
             java.net.URI.class,
             java.lang.Class.class,
             com.fasterxml.jackson.databind.JavaType.class,
             java.util.Currency.class,
             java.util.regex.Pattern.class,
             java.util.Locale.class,
             java.nio.charset.Charset.class,
             java.util.TimeZone.class,
             java.net.InetAddress.class,
             java.net.InetSocketAddress.class
     };
     for (java.lang.Class<?> type : types) {
         org.junit.Assert.assertNotNull(

com.fasterxml.jackson.databind.deser.std.FromStringDeserializer.findDeserializer(type)
         );
     }
 }