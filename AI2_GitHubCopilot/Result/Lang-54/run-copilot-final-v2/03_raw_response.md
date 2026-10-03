package org.apache.commons.lang;

import java.util.Locale;
import org.junit.Test;
import static org.junit.Assert.*;

/**

 - Tests for LocaleUtils.toLocale(String) targeting bug LANG-328.
 - "fr__POSIX" (empty country with variant) must be accepted.
  */
 public class LocaleUtilsTest {
  @Test
  public void testToLocaleTwoLetterLanguage() {
  Locale loc = LocaleUtils.toLocale("en");
  assertNotNull(loc);
  assertEquals("en", loc.getLanguage());
  assertEquals("", loc.getCountry());
  assertEquals("", loc.getVariant());
  }
  @Test
  public void testToLocaleLanguageCountry() {
  Locale loc = LocaleUtils.toLocale("fr_FR");
  assertNotNull(loc);
  assertEquals("fr", loc.getLanguage());
  assertEquals("FR", loc.getCountry());
  assertEquals("", loc.getVariant());
  }
  @Test
  public void testToLocaleLanguageCountryVariant() {
  Locale loc = LocaleUtils.toLocale("en_US_WIN");
  assertNotNull(loc);
  assertEquals("en", loc.getLanguage());
  assertEquals("US", loc.getCountry());
  assertEquals("WIN", loc.getVariant());
  }
  @Test
  public void testToLocaleEmptyCountryWithVariant() {
  // LANG-328: "fr__POSIX" has two underscores (empty country) and a variant
  Locale loc = LocaleUtils.toLocale("fr__POSIX");
  assertNotNull(loc);
  assertEquals("fr", loc.getLanguage());
  assertEquals("", loc.getCountry());
  assertEquals("POSIX", loc.getVariant());
  }
  @Test(expected = IllegalArgumentException.class)
  public void testToLocaleLanguageUnderscoreNoCountry() {
  LocaleUtils.toLocale("fr_");
  }
  @Test
  public void testToLocaleLanguageUnderscoreUnderscoreVariant() {
  // "de__LINUX" – another empty country with variant
  Locale loc = LocaleUtils.toLocale("de__LINUX");
  assertNotNull(loc);
  assertEquals("de", loc.getLanguage());
  assertEquals("", loc.getCountry());
  assertEquals("LINUX", loc.getVariant());
  }
  @Test
  public void testToLocaleNullReturnsNull() {
  assertNull(LocaleUtils.toLocale(null));
  }
  @Test(expected = IllegalArgumentException.class)
  public void testToLocaleEmptyString() {
  LocaleUtils.toLocale("");
  }
  @Test(expected = IllegalArgumentException.class)
  public void testToLocaleUnderscoreOnly() {
  LocaleUtils.toLocale("_");
  }
  @Test(expected = IllegalArgumentException.class)
  public void testToLocaleUnderscoreCountry() {
  LocaleUtils.toLocale("_FR");
  }
  @Test(expected = IllegalArgumentException.class)
  public void testToLocaleInvalidLanguageUpperase() {
  LocaleUtils.toLocale("EN");
  }
  @Test(expected = IllegalArgumentException.class)
  public void testToLocaleLowercaseCountry() {
  LocaleUtils.toLocale("en_us");
  }

}