package org.apache.commons.lang;

import java.util.Locale;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringUtilsContainsIgnoreCaseLang432Test {

    @Test
    public void testSharpSContainsSSWithEnglishDefaultLocale() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.ENGLISH);
            assertTrue(StringUtils.containsIgnoreCase("\u00df", "SS"));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    public void testSharpSContainsSSWithTurkishDefaultLocale() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(new Locale("tr", "TR"));
            assertTrue(StringUtils.containsIgnoreCase("\u00df", "SS"));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    public void testSharpSCaseInsensitiveMatchingWorksInReverseAndWithinText() {
        assertTrue(StringUtils.containsIgnoreCase("SS", "\u00df"));
        assertTrue(StringUtils.containsIgnoreCase("prefix\u00dfsuffix", "SS"));
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
