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

}