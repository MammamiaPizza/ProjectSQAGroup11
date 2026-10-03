@org.junit.Test
public void testToLocaleParsesLanguageOnlyLocale() {
    org.junit.Assert.assertEquals(new java.util.Locale("en"), LocaleUtils.toLocale("en"));
}

@org.junit.Test
public void testLocaleLookupListIncludesFallbacksAndAvoidsDuplicateDefault() {
    java.util.Locale locale = new java.util.Locale("en", "US", "POSIX");
    java.util.List<java.util.Locale> lookup = LocaleUtils.localeLookupList(locale, new java.util.Locale("fr", "CA"));

    org.junit.Assert.assertEquals(4, lookup.size());
    org.junit.Assert.assertEquals(locale, lookup.get(0));
    org.junit.Assert.assertEquals(new java.util.Locale("en", "US"), lookup.get(1));
    org.junit.Assert.assertEquals(java.util.Locale.ENGLISH, lookup.get(2));
    org.junit.Assert.assertEquals(new java.util.Locale("fr", "CA"), lookup.get(3));

    java.util.List<java.util.Locale> withoutDuplicateDefault =
            LocaleUtils.localeLookupList(locale, java.util.Locale.ENGLISH);
    org.junit.Assert.assertEquals(3, withoutDuplicateDefault.size());
}

@org.junit.Test
public void testAvailableLocaleCollectionsAndAvailabilityAgree() {
    java.util.List<java.util.Locale> locales = LocaleUtils.availableLocaleList();
    java.util.Set<java.util.Locale> localeSet = LocaleUtils.availableLocaleSet();

    org.junit.Assert.assertFalse(locales.isEmpty());
    java.util.Locale locale = locales.get(0);
    org.junit.Assert.assertTrue(localeSet.contains(locale));
    org.junit.Assert.assertTrue(LocaleUtils.isAvailableLocale(locale));
    org.junit.Assert.assertFalse(LocaleUtils.isAvailableLocale(null));

    try {
        locales.add(java.util.Locale.US);
        org.junit.Assert.fail("Available locale list should be unmodifiable");
    } catch (java.lang.UnsupportedOperationException expected) {
    }
}

@org.junit.Test
public void testLanguagesByCountryAndCountriesByLanguageIncludeUsLocale() {
    java.util.List<java.util.Locale> languages = LocaleUtils.languagesByCountry("US");
    java.util.List<java.util.Locale> countries = LocaleUtils.countriesByLanguage("en");

    org.junit.Assert.assertTrue(languages.contains(java.util.Locale.US));
    org.junit.Assert.assertTrue(countries.contains(java.util.Locale.US));
    org.junit.Assert.assertTrue(LocaleUtils.languagesByCountry(null).isEmpty());
    org.junit.Assert.assertTrue(LocaleUtils.countriesByLanguage(null).isEmpty());
}