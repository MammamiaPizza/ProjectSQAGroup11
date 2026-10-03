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
}