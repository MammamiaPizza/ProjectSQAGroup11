package org.apache.commons.lang3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Locale;

import org.junit.Test;

public class LocaleUtilsLang865Test {

    @Test
    public void testToLocaleAcceptsCountryOnlyLocaleWithLeadingUnderscore() {
        assertEquals(new Locale("", "GB"), LocaleUtils.toLocale("_GB"));
    }

    @Test
    public void testToLocaleParsesLanguageCountryAndVariant() {
        assertEquals(new Locale("en", "GB", "POSIX"), LocaleUtils.toLocale("en_GB_POSIX"));
    }

    @Test
    public void testToLocaleParsesLanguageAndCountry() {
        assertEquals(new Locale("en", "GB"), LocaleUtils.toLocale("en_GB"));
    }

    @Test
    public void testToLocaleReturnsNullForNullInput() {
        assertNull(LocaleUtils.toLocale(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToLocaleRejectsTooShortCountryOnlyLocale() {
        LocaleUtils.toLocale("_G");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToLocaleRejectsLowercaseCountryCode() {
        LocaleUtils.toLocale("en_gb");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToLocaleRejectsEmptyInput() {
        LocaleUtils.toLocale("");
    }

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
}
