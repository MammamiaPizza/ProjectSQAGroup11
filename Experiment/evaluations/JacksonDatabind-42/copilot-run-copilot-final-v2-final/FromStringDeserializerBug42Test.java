package com.fasterxml.jackson.databind.deser.std;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FromStringDeserializerBug42Test {

 private ObjectMapper mapper;

 @Before
 public void setUp() {
     mapper = new ObjectMapper();
 }

 @Test
 public void testEmptyStringDeserializesToLocaleRoot() throws Exception {
     Locale result = mapper.readValue("\"\"", Locale.class);
     assertNotNull("Empty string should deserialize to a non-null Locale", result);
     assertEquals("", result.getLanguage());
     assertEquals("", result.getCountry());
     assertEquals("", result.getVariant());
 }

 @Test
 public void testWhitespaceStringDeserializesToLocaleRoot() throws Exception {
     Locale result = mapper.readValue("\" \"", Locale.class);
     assertNotNull("Whitespace-only string should deserialize to a non-null Locale", result);
     assertEquals("", result.getLanguage());
     assertEquals("", result.getCountry());
 }

 @Test
 public void testLocaleEn() throws Exception {
     Locale result = mapper.readValue("\"en\"", Locale.class);
     assertEquals(new Locale("en"), result);
 }

 @Test
 public void testLocaleEnUS() throws Exception {
     Locale result = mapper.readValue("\"en_US\"", Locale.class);
     assertEquals(new Locale("en", "US"), result);
 }

 @Test
 public void testLocaleFr() throws Exception {
     Locale result = mapper.readValue("\"fr\"", Locale.class);
     assertEquals(new Locale("fr"), result);
 }

 @Test
 public void testLocaleJaJP() throws Exception {
     Locale result = mapper.readValue("\"ja_JP\"", Locale.class);
     assertEquals(new Locale("ja", "JP"), result);
 }

 @Test
 public void testLocaleWithVariant() throws Exception {
     Locale result = mapper.readValue("\"th_TH_TH\"", Locale.class);
     assertEquals(new Locale("th", "TH", "TH"), result);
 }

 @Test
 public void testInvalidLocaleStringDoesNotThrow() throws Exception {
     Locale result = mapper.readValue("\"xx_YY\"", Locale.class);
     assertNotNull(result);
     assertEquals("xx", result.getLanguage());
     assertEquals("YY", result.getCountry());
 }

 @Test
 public void testTrimmedLocale() throws Exception {
     Locale result = mapper.readValue("\" en \"", Locale.class);
     assertEquals(new Locale("en"), result);
 }

 @Test
 public void testURIDeserializeEmptyString() throws Exception {
     URI result = mapper.readValue("\"\"", URI.class);
     assertNotNull(result);
     assertEquals(URI.create(""), result);
 }

 @Test
 public void testNullJsonToken() throws Exception {
     Locale result = mapper.readValue("null", Locale.class);
     assertNull(result);
 }

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
}
