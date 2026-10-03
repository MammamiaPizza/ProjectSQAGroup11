package org.apache.commons.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;

public class LocaleUtilsGeneratedTest {

    @Test
    public void testPublicConstructorCreatesInstance() {
        assertNotNull(new LocaleUtils());
    }

    @Test
    public void testToLocaleParsesNullAndAllSupportedFormats() {
        assertNull(LocaleUtils.toLocale(null));

        assertEquals(new Locale("en", ""), LocaleUtils.toLocale("en"));
        assertEquals(new Locale("en", "GB"), LocaleUtils.toLocale("en_GB"));
        assertEquals(new Locale("fr", "CA", "POSIX"), LocaleUtils.toLocale("fr_CA_POSIX"));
    }

    @Test
    public void testToLocaleRejectsInvalidFormats() {
        assertInvalidLocale("e");
        assertInvalidLocale("eng");
        assertInvalidLocale("EN");
        assertInvalidLocale("en_us");
        assertInvalidLocale("en-US");
        assertInvalidLocale("en_U");
        assertInvalidLocale("en_GBx");
        assertInvalidLocale("en_GB-x");
    }

    @Test
    public void testLocaleLookupListWithoutExplicitDefaultUsesFallbacks() {
        Locale locale = new Locale("fr", "CA", "POSIX");

        List expected = Arrays.asList(new Locale[] {
            locale,
            new Locale("fr", "CA"),
            new Locale("fr", "")
        });

        List actual = LocaleUtils.localeLookupList(locale);
        assertEquals(expected, actual);

        try {
            actual.add(Locale.US);
            fail("Lookup list must be unmodifiable");
        } catch (UnsupportedOperationException expectedException) {
            // expected
        }
    }

    @Test
    public void testLocaleLookupListUsesExplicitDefaultOnlyOnceAndHandlesNullLocale() {
        Locale locale = new Locale("fr", "CA", "POSIX");
        Locale defaultLocale = Locale.ENGLISH;

        assertEquals(Arrays.asList(new Locale[] {
            locale,
            new Locale("fr", "CA"),
            new Locale("fr", ""),
            defaultLocale
        }), LocaleUtils.localeLookupList(locale, defaultLocale));

        assertEquals(Collections.singletonList(Locale.US),
                LocaleUtils.localeLookupList(Locale.US, Locale.US));
        assertEquals(Collections.singletonList(Locale.US),
                LocaleUtils.localeLookupList(null, Locale.US));
    }

    @Test
    public void testAvailableLocaleListAndSetMatchJdkLocalesAndAreUnmodifiable() {
        List expectedList = Arrays.asList(Locale.getAvailableLocales());
        Set expectedSet = new HashSet(expectedList);

        List actualList = LocaleUtils.availableLocaleList();
        Set actualSet = LocaleUtils.availableLocaleSet();

        assertEquals(expectedList, actualList);
        assertEquals(expectedSet, actualSet);

        try {
            actualList.add(Locale.US);
            fail("Available locale list must be unmodifiable");
        } catch (UnsupportedOperationException expectedException) {
            // expected
        }

        try {
            actualSet.add(Locale.US);
            fail("Available locale set must be unmodifiable");
        } catch (UnsupportedOperationException expectedException) {
            // expected
        }
    }

    @Test
    public void testIsAvailableLocaleMatchesJdkAvailableLocaleSet() {
        Locale[] installed = Locale.getAvailableLocales();
        assertTrue(installed.length > 0);

        Set expected = new HashSet(Arrays.asList(installed));
        Locale knownAvailable = installed[0];
        Locale unavailable = new Locale("zz", "ZZ", "LocaleUtilsGeneratedUnavailable");

        assertTrue(expected.contains(knownAvailable));
        assertFalse(expected.contains(unavailable));
        assertEquals(expected.contains(knownAvailable), LocaleUtils.isAvailableLocale(knownAvailable));
        assertEquals(expected.contains(unavailable), LocaleUtils.isAvailableLocale(unavailable));
    }

    @Test
    public void testLanguagesByCountryFiltersVariantsAndHandlesNullAndUnknownCodes() {
        Locale representative = findLocaleWithCountry();
        assertNotNull(representative);

        String country = representative.getCountry();
        List expected = expectedLanguagesByCountry(country);

        assertFalse(expected.isEmpty());
        assertEquals(expected, LocaleUtils.languagesByCountry(country));
        assertEquals(Collections.EMPTY_LIST, LocaleUtils.languagesByCountry(null));
        assertEquals(Collections.EMPTY_LIST, LocaleUtils.languagesByCountry("ZZ"));
    }

    @Test
    public void testCountriesByLanguageFiltersLanguageOnlyAndVariants() {
        Locale representative = findLocaleWithLanguageAndCountry();
        assertNotNull(representative);

        String language = representative.getLanguage();
        List expected = expectedCountriesByLanguage(language);

        assertFalse(expected.isEmpty());
        assertEquals(expected, LocaleUtils.countriesByLanguage(language));
        assertEquals(Collections.EMPTY_LIST, LocaleUtils.countriesByLanguage(null));
        assertEquals(Collections.EMPTY_LIST, LocaleUtils.countriesByLanguage("zz"));
    }

    private static void assertInvalidLocale(String value) {
        try {
            LocaleUtils.toLocale(value);
            fail("Expected IllegalArgumentException for: " + value);
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    private static Locale findLocaleWithCountry() {
        Locale[] locales = Locale.getAvailableLocales();
        for (int i = 0; i < locales.length; i++) {
            Locale locale = locales[i];
            if (locale.getCountry().length() > 0 && locale.getVariant().length() == 0) {
                return locale;
            }
        }
        return null;
    }

    private static Locale findLocaleWithLanguageAndCountry() {
        Locale[] locales = Locale.getAvailableLocales();
        for (int i = 0; i < locales.length; i++) {
            Locale locale = locales[i];
            if (locale.getLanguage().length() > 0
                    && locale.getCountry().length() > 0
                    && locale.getVariant().length() == 0) {
                return locale;
            }
        }
        return null;
    }

    private static List expectedLanguagesByCountry(String country) {
        List expected = new ArrayList();
        Locale[] locales = Locale.getAvailableLocales();
        for (int i = 0; i < locales.length; i++) {
            Locale locale = locales[i];
            if (country.equals(locale.getCountry()) && locale.getVariant().length() == 0) {
                expected.add(locale);
            }
        }
        return expected;
    }

    private static List expectedCountriesByLanguage(String language) {
        List expected = new ArrayList();
        Locale[] locales = Locale.getAvailableLocales();
        for (int i = 0; i < locales.length; i++) {
            Locale locale = locales[i];
            if (language.equals(locale.getLanguage())
                    && locale.getCountry().length() > 0
                    && locale.getVariant().length() == 0) {
                expected.add(locale);
            }
        }
        return expected;
    }
}