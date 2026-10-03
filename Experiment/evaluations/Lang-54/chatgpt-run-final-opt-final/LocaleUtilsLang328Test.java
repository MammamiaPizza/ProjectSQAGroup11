package org.apache.commons.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Test;

public class LocaleUtilsLang328Test {

    @Test
    public void testToLocaleAcceptsLanguageOnly() {
        Locale locale = LocaleUtils.toLocale("fr");

        assertEquals("fr", locale.getLanguage());
        assertEquals("", locale.getCountry());
        assertEquals("", locale.getVariant());
    }

    @Test
    public void testToLocaleAcceptsLanguageAndCountry() {
        Locale locale = LocaleUtils.toLocale("en_GB");

        assertEquals("en", locale.getLanguage());
        assertEquals("GB", locale.getCountry());
        assertEquals("", locale.getVariant());
    }

    @Test
    public void testToLocaleAcceptsLanguageCountryAndVariant() {
        Locale locale = LocaleUtils.toLocale("en_US_POSIX");

        assertEquals("en", locale.getLanguage());
        assertEquals("US", locale.getCountry());
        assertEquals("POSIX", locale.getVariant());
    }

    @Test
    public void testToLocaleAcceptsEmptyCountryWithVariant() {
        Locale locale = LocaleUtils.toLocale("fr__POSIX");

        assertEquals("fr", locale.getLanguage());
        assertEquals("", locale.getCountry());
        assertEquals("POSIX", locale.getVariant());
    }

    @Test
    public void testToLocaleReturnsNullForNullInput() {
        assertNull(LocaleUtils.toLocale(null));
    }

    @Test
    public void testToLocaleRejectsInvalidFormats() {
        assertInvalidLocale("");
        assertInvalidLocale("e");
        assertInvalidLocale("EN");
        assertInvalidLocale("en-us");
        assertInvalidLocale("en_us");
        assertInvalidLocale("en_U");
        assertInvalidLocale("en_US_");
    }

    private void assertInvalidLocale(String value) {
        try {
            LocaleUtils.toLocale(value);
            fail("Expected IllegalArgumentException for: " + value);
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

@org.junit.Test
public void testAvailableLocaleSetMatchesAvailableLocaleListAndIsUnmodifiable() {
    java.util.List locales = org.apache.commons.lang.LocaleUtils.availableLocaleList();
    java.util.Set expected = new java.util.HashSet(locales);
    java.util.Set actual = org.apache.commons.lang.LocaleUtils.availableLocaleSet();

    org.junit.Assert.assertEquals(expected, actual);
    org.junit.Assert.assertTrue(actual.containsAll(locales));

    try {
        actual.clear();
        org.junit.Assert.fail("Expected available locale set to be unmodifiable");
    } catch (UnsupportedOperationException expectedException) {
    }
}

@org.junit.Test
public void testLanguagesByCountryAndCountriesByLanguageContainMatchingAvailableLocale() {
    java.util.Locale sample = null;
    java.util.List available = org.apache.commons.lang.LocaleUtils.availableLocaleList();
    for (int i = 0; i < available.size(); i++) {
        java.util.Locale locale = (java.util.Locale) available.get(i);
        if (locale.getCountry().length() != 0 && locale.getVariant().length() == 0) {
            sample = locale;
            break;
        }
    }

    org.junit.Assert.assertNotNull(sample);

    java.util.List languages = org.apache.commons.lang.LocaleUtils.languagesByCountry(sample.getCountry());
    org.junit.Assert.assertTrue(languages.contains(sample));
    for (int i = 0; i < languages.size(); i++) {
        java.util.Locale locale = (java.util.Locale) languages.get(i);
        org.junit.Assert.assertEquals(sample.getCountry(), locale.getCountry());
        org.junit.Assert.assertEquals("", locale.getVariant());
    }

    java.util.List countries = org.apache.commons.lang.LocaleUtils.countriesByLanguage(sample.getLanguage());
    org.junit.Assert.assertTrue(countries.contains(sample));
    for (int i = 0; i < countries.size(); i++) {
        java.util.Locale locale = (java.util.Locale) countries.get(i);
        org.junit.Assert.assertEquals(sample.getLanguage(), locale.getLanguage());
        org.junit.Assert.assertTrue(locale.getCountry().length() != 0);
        org.junit.Assert.assertEquals("", locale.getVariant());
    }
}

@org.junit.Test
public void testLanguageAndCountryLookupsHandleNullCodes() {
    org.junit.Assert.assertTrue(org.apache.commons.lang.LocaleUtils.languagesByCountry(null).isEmpty());
    org.junit.Assert.assertTrue(org.apache.commons.lang.LocaleUtils.countriesByLanguage(null).isEmpty());
}
}
