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
}
