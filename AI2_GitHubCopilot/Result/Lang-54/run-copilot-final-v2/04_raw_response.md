@Test
public void testAvailableLocaleSetCachingAndUnmodifiable() {
    java.util.Set set = org.apache.commons.lang.LocaleUtils.availableLocaleSet();
    org.junit.Assert.assertNotNull(set);
    org.junit.Assert.assertFalse(set.isEmpty());
    try {
        set.add(new java.util.Locale("zz"));
        org.junit.Assert.fail("Expected UnsupportedOperationException");
    } catch (UnsupportedOperationException expected) {
    }
    java.util.Set set2 = org.apache.commons.lang.LocaleUtils.availableLocaleSet();
    org.junit.Assert.assertTrue(set == set2);
}

@Test
public void testIsAvailableLocale() {
    org.junit.Assert.assertTrue(org.apache.commons.lang.LocaleUtils.isAvailableLocale(java.util.Loca
le.US));
    org.junit.Assert.assertFalse(org.apache.commons.lang.LocaleUtils.isAvailableLocale(new
java.util.Locale("xx", "XX")));
}

@Test
public void testCountriesByLanguage() {
    java.util.List countries = org.apache.commons.lang.LocaleUtils.countriesByLanguage("en");
    org.junit.Assert.assertNotNull(countries);
    org.junit.Assert.assertFalse(countries.isEmpty());
    for (Object o : countries) {
        java.util.Locale loc = (java.util.Locale) o;
        org.junit.Assert.assertEquals("en", loc.getLanguage());
        org.junit.Assert.assertFalse(loc.getCountry().isEmpty());
        org.junit.Assert.assertEquals("", loc.getVariant());
    }
    java.util.List empty = org.apache.commons.lang.LocaleUtils.countriesByLanguage(null);
    org.junit.Assert.assertNotNull(empty);
    org.junit.Assert.assertTrue(empty.isEmpty());
    java.util.List noResult = org.apache.commons.lang.LocaleUtils.countriesByLanguage("zz");
    org.junit.Assert.assertNotNull(noResult);
    org.junit.Assert.assertTrue(noResult.isEmpty());
}

@Test
public void testLanguagesByCountry() {
    java.util.List languages = org.apache.commons.lang.LocaleUtils.languagesByCountry("US");
    org.junit.Assert.assertNotNull(languages);
    org.junit.Assert.assertFalse(languages.isEmpty());
    for (Object o : languages) {
        java.util.Locale loc = (java.util.Locale) o;
        org.junit.Assert.assertEquals("US", loc.getCountry());
        org.junit.Assert.assertFalse(loc.getLanguage().isEmpty());
    }
    java.util.List empty = org.apache.commons.lang.LocaleUtils.languagesByCountry(null);
    org.junit.Assert.assertNotNull(empty);
    org.junit.Assert.assertTrue(empty.isEmpty());
    java.util.List noResult = org.apache.commons.lang.LocaleUtils.languagesByCountry("ZZ");
    org.junit.Assert.assertNotNull(noResult);
    org.junit.Assert.assertTrue(noResult.isEmpty());
}