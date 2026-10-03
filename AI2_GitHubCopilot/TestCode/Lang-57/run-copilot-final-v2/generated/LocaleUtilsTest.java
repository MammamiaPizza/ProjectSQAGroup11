package org.apache.commons.lang;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;

public class LocaleUtilsTest {

 @Test
 public void testToLocaleNull() {
     assertNull(LocaleUtils.toLocale(null));
 }

 @Test
 public void testToLocaleValid2Letter() {
     Locale locale = LocaleUtils.toLocale("en");
     assertEquals("en", locale.getLanguage());
     assertEquals("", locale.getCountry());
     assertEquals("", locale.getVariant());
 }

 @Test
 public void testToLocaleValidWithCountry() {
     Locale locale = LocaleUtils.toLocale("en_US");
     assertEquals("en", locale.getLanguage());
     assertEquals("US", locale.getCountry());
     assertEquals("", locale.getVariant());
 }

 @Test
 public void testToLocaleValidWithVariant() {
     Locale locale = LocaleUtils.toLocale("en_US_WIN");
     assertEquals("en", locale.getLanguage());
     assertEquals("US", locale.getCountry());
     // variant behavior depends on JDK; just verify it is not empty
     assertTrue(locale.getVariant().length() > 0);
 }

 @Test
 public void testToLocaleInvalidFormats() {
     // empty string
     try {
         LocaleUtils.toLocale("");
         fail("Expected IllegalArgumentException for empty string");
     } catch (IllegalArgumentException expected) {
     }
     // length 1
     try {
         LocaleUtils.toLocale("a");
         fail("Expected IllegalArgumentException for length 1");
     } catch (IllegalArgumentException expected) {
     }
     // length 3 without underscore
     try {
         LocaleUtils.toLocale("abc");
         fail("Expected IllegalArgumentException for length 3");
     } catch (IllegalArgumentException expected) {
     }
     // language not lowercase
     try {
         LocaleUtils.toLocale("EN");
         fail("Expected IllegalArgumentException for uppercase language");
     } catch (IllegalArgumentException expected) {
     }
     // country not uppercase
     try {
         LocaleUtils.toLocale("en_us");
         fail("Expected IllegalArgumentException for lowercase country");
     } catch (IllegalArgumentException expected) {
     }
     // wrong separator
     try {
         LocaleUtils.toLocale("en-US");
         fail("Expected IllegalArgumentException for wrong separator");
     } catch (IllegalArgumentException expected) {
     }
     // invalid length 6
     try {
         LocaleUtils.toLocale("en_US_");
         fail("Expected IllegalArgumentException for length 6");
     } catch (IllegalArgumentException expected) {
     }
 }

 @Test
 public void testAvailableLocaleListAndSet() {
     List<Locale> list = LocaleUtils.availableLocaleList();
     assertNotNull(list);
     assertFalse("Available locale list is empty", list.isEmpty());
     // all elements must be non-null
     for (Locale locale : list) {
         assertNotNull("List contains null locale", locale);
     }
     Set<Locale> set = LocaleUtils.availableLocaleSet();
     assertNotNull(set);
     assertEquals(list.size(), set.size());
     assertTrue(set.containsAll(list));
 }

 @Test
 public void testIsAvailableLocale() {
     // Ensure the set is populated first to avoid masking the bug
     // if isAvailableLocale is called before availableLocaleSet() the
     // uninitialized field may cause NullPointerException.
     // This test intentionally calls availableLocaleSet() first to
     // check that isAvailableLocale works after proper initialization.
     LocaleUtils.availableLocaleSet();
     assertTrue(LocaleUtils.isAvailableLocale(Locale.US));
     assertFalse(LocaleUtils.isAvailableLocale(new Locale("zz", "ZZ")));
 }

 @Test
 public void testIsAvailableLocaleDirect() {
     // Exercise isAvailableLocale without prior initialization of the
     // static set – this should reveal the NullPointerException bug.
     try {
         boolean result = LocaleUtils.isAvailableLocale(Locale.US);
         // if we reach here the bug is fixed; still verify correctness
         assertTrue(result);
     } catch (NullPointerException e) {
         fail("isAvailableLocale threw NullPointerException (field not initialized)");
     }
 }

 @Test
 public void testLocaleLookupListBasic() {
     Locale locale = new Locale("fr", "CA", "xxx");
     List<Locale> list = LocaleUtils.localeLookupList(locale);
     assertNotNull(list);
     assertEquals(4, list.size());
     assertEquals(new Locale("fr", "CA", "xxx"), list.get(0));
     assertEquals(new Locale("fr", "CA"), list.get(1));
     assertEquals(new Locale("fr", ""), list.get(2));
     assertEquals(locale, list.get(3)); // defaultLocale same as input
 }

 @Test
 public void testLocaleLookupListWithDefault() {
     Locale locale = new Locale("fr", "CA", "xxx");
     Locale defaultLocale = new Locale("en");
     List<Locale> list = LocaleUtils.localeLookupList(locale, defaultLocale);
     assertNotNull(list);
     assertEquals(4, list.size());
     assertEquals(new Locale("fr", "CA", "xxx"), list.get(0));
     assertEquals(new Locale("fr", "CA"), list.get(1));
     assertEquals(new Locale("fr", ""), list.get(2));
     assertEquals(defaultLocale, list.get(3));
 }

 @Test
 public void testLocaleLookupListWithRedundantDefault() {
     Locale locale = new Locale("fr", "CA");
     Locale defaultLocale = new Locale("fr", "");
     List<Locale> list = LocaleUtils.localeLookupList(locale, defaultLocale);
     assertNotNull(list);
     // list should be [fr_CA, fr] because default is already present
     assertEquals(2, list.size());
     assertEquals(new Locale("fr", "CA"), list.get(0));
     assertEquals(new Locale("fr", ""), list.get(1));
 }

 @Test
 public void testLocaleLookupListNullLocale() {
     List<Locale> list = LocaleUtils.localeLookupList(null, Locale.US);
     assertNotNull(list);
     assertTrue(list.isEmpty());
 }

 @Test
 public void testLanguagesByCountry() {
     // null country should return empty list
     assertTrue(LocaleUtils.languagesByCountry(null).isEmpty());

     // valid country – at least "en" should be present for "US"
     List<Locale> langs = LocaleUtils.languagesByCountry("US");
     assertNotNull(langs);
     boolean foundEnglish = false;
     for (Locale l : langs) {
         assertEquals("US", l.getCountry());
         assertEquals(0, l.getVariant().length());
         if ("en".equals(l.getLanguage())) {
             foundEnglish = true;
         }
     }
     assertTrue("English not found for US", foundEnglish);

     // invalid country
     List<Locale> empty = LocaleUtils.languagesByCountry("ZZ");
     assertNotNull(empty);
     assertTrue(empty.isEmpty());
 }

 @Test
 public void testCountriesByLanguage() {
     // null language should return empty list
     assertTrue(LocaleUtils.countriesByLanguage(null).isEmpty());

     // valid language – at least "US" should be present for "en"
     List<Locale> countries = LocaleUtils.countriesByLanguage("en");
     assertNotNull(countries);
     boolean foundUS = false;
     for (Locale l : countries) {
         assertEquals("en", l.getLanguage());
         assertFalse(l.getCountry().length() == 0);
         assertEquals(0, l.getVariant().length());
         if ("US".equals(l.getCountry())) {
             foundUS = true;
         }
     }
     assertTrue("US not found for English", foundUS);

     // invalid language
     List<Locale> empty = LocaleUtils.countriesByLanguage("zz");
     assertNotNull(empty);
     assertTrue(empty.isEmpty());
 }

 @Test
 public void testConstructor() {
     new LocaleUtils(); // no exception
 }

}
