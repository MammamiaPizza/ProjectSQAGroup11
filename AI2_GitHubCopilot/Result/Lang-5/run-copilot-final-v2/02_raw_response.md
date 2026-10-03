package org.apache.commons.lang3;

 import org.junit.Test;
 import static org.junit.Assert.*;
 import java.util.Locale;

 public class LocaleUtilsTest {

     @Test
     public void testNull() {
         assertNull(LocaleUtils.toLocae(null));
     }

     @Test(expected = IllegalArgumentException.class)
     public void testEmptyString() {
         LocaleUtils.toLocale("");
     }

     @Test(expected = IllegalArgumentException.class)
     public void testShortString() {
         LocaleUtils.toLocale("x");
     }

     @Test
     public void testLowerCaseLanguageOnly() {
         assertEquals(new Locale("en"), LocaleUtils.toLocale("en"));
         assertEquals(new Locale("de"), LocaleUtils.toLocale("de"));
     }

     @Test(expected = IllegalArgumentException.class)
     public void testUpperCaseLanguageInvalid() {
         LocaleUtils.toLocale("GB");
     }

     @Test
     public void testLanguageCountry() {
         assertEquals(new Locale("en", "GB"), LocaleUtils.toLocale("en_GB"));
     }

     @Test
     public void testLanguageCountryVariant() {
         assertEquals(new Locale("en", "GB", "Win"), LocaleUtils.toLocale("en_GB_Win"));
     }

     @Test
     public void testLanguageEmptyCountryVariant() {
         assertEquals(new Locale("en", "", "Win"), LocaleUtils.toLocale("en__Win"));
     }

     @Test
     public void testCountryOnly() {
         assertEquals(new Locale("", "GB", ""), LocaleUtils.toLocale("_GB"));
     }

     @Test
     public void testCountryOnlyVariant() {
         assertEquals(new Locale("", "GB", "Win"), LocaleUtils.toLocale("_GB_Win"));
     }

     @Test
     public void testEmptyLanguageEmptyCountry() {
         assertEquals(new Locale("", "", ""), LocaleUtils.toLocale("__"));
     }

     @Test
     public void testMultiUnderscoreVariant() {
         assertEquals(new Locale("en", "GB", "Win_X"), LocaleUtils.toLocale("en_GB_Win_X"));
     }
 }