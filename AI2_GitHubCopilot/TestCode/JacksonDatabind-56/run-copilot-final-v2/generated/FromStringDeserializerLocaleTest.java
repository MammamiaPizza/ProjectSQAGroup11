package com.fasterxml.jackson.databind.deser;

import java.util.Locale;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;

public class FromStringDeserializerLocaleTest {

 private static final ObjectMapper MAPPER = new ObjectMapper();

 private Locale readLocale(String json) throws Exception {
     return MAPPER.readValue(json, Locale.class);
 }

 @Test
 public void testLocaleWithUnderscoreSeparator() throws Exception {
     assertEquals(new Locale("en", "US"), readLocale("\"en_US\""));
 }

 @Test
 public void testLocaleWithHyphenSeparator() throws Exception {
     Locale loc = readLocale("\"en-US\"");
     assertEquals(new Locale("en", "US"), loc);
     assertEquals("en", loc.getLanguage());
     assertEquals("US", loc.getCountry());
     assertEquals("en_US", loc.toString());
 }

 @Test
 public void testLocaleLanguageOnly() throws Exception {
     assertEquals(new Locale("en"), readLocale("\"en\""));
 }

 @Test
 public void testLocaleWithCountryAndUnderscore() throws Exception {
     assertEquals(new Locale("zh", "CN"), readLocale("\"zh_CN\""));
 }

 @Test
 public void testLocaleWithLowercaseCountryHyphen() throws Exception {
     assertEquals(new Locale("en", "US"), readLocale("\"en-us\""));
 }

 @Test
 public void testLocaleWithScriptAndUnderscores() throws Exception {
     Locale expected = Locale.forLanguageTag("sr-Latn-RS");
     assertEquals(expected, readLocale("\"sr_Latn_RS\""));
 }

 @Test
 public void testLocaleWithScriptAndHyphens() throws Exception {
     assertEquals(Locale.forLanguageTag("sr-Latn-RS"), readLocale("\"sr-Latn-RS\""));
 }

 @Test
 public void testLocaleEmptyStringBecomesRoot() throws Exception {
     assertEquals(Locale.ROOT, readLocale("\"\""));
 }

}
