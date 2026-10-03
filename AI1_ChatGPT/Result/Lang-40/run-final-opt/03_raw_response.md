package org.apache.commons.lang;

import java.util.Locale;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringUtilsContainsIgnoreCaseLang432Test {

    @Test
    public void testIContainsIWithEnglishDefaultLocale() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.ENGLISH);
            assertTrue(StringUtils.containsIgnoreCase("I", "i"));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    public void testIContainsIWithTurkishDefaultLocale() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(new Locale("tr", "TR"));
            assertTrue(StringUtils.containsIgnoreCase("I", "i"));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    public void testCaseInsensitiveMatchingWorksInReverseAndWithinText() {
        assertTrue(StringUtils.containsIgnoreCase("i", "I"));
        assertTrue(StringUtils.containsIgnoreCase("prefixIsuffix", "i"));
    }

    @Test
    public void testContainsIgnoreCaseHandlesAsciiAndAbsentText() {
        assertTrue(StringUtils.containsIgnoreCase("AbCdEf", "cDe"));
        assertFalse(StringUtils.containsIgnoreCase("AbCdEf", "xyz"));
    }

    @Test
    public void testContainsIgnoreCaseHandlesNullAndEmptyArguments() {
        assertFalse(StringUtils.containsIgnoreCase(null, "text"));
        assertFalse(StringUtils.containsIgnoreCase("text", null));
        assertFalse(StringUtils.containsIgnoreCase(null, null));
        assertTrue(StringUtils.containsIgnoreCase("text", ""));
        assertTrue(StringUtils.containsIgnoreCase("", ""));
    }
}