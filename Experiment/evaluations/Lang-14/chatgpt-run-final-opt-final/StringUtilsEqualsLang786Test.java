package org.apache.commons.lang3;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StringUtilsEqualsLang786Test {

    @Test
    public void equalsReturnsTrueForSameStringInstance() {
        String value = new String("commons-lang");

        assertTrue(StringUtils.equals(value, value));
    }

    @Test
    public void equalsHandlesNullInputs() {
        assertTrue(StringUtils.equals(null, null));
        assertFalse(StringUtils.equals(null, "value"));
        assertFalse(StringUtils.equals("value", null));
    }

    @Test
    public void equalsComparesStringValues() {
        assertTrue(StringUtils.equals("same value", new String("same value")));
        assertFalse(StringUtils.equals("first", "second"));
    }

    @Test
    public void equalsComparesStringBuilderContents() {
        assertTrue(StringUtils.equals(new StringBuilder("builder text"), new StringBuilder("builder text")));
        assertTrue(StringUtils.equals(new StringBuilder(), new StringBuilder()));
        assertFalse(StringUtils.equals(new StringBuilder("builder text"), new StringBuilder("builder texts")));
    }

    @Test
    public void equalsComparesMixedStringAndStringBuilderContents() {
        assertTrue(StringUtils.equals("mixed content", new StringBuilder("mixed content")));
        assertTrue(StringUtils.equals(new StringBuilder("mixed content"), "mixed content"));
        assertFalse(StringUtils.equals("mixed content", new StringBuilder("mixed contenX")));
    }

@org.junit.Test
public void equalsComparesContentsOfGeneralCharSequences() {
    final java.lang.CharSequence first = identityCharSequence("custom contents");
    final java.lang.CharSequence second = identityCharSequence("custom contents");
    final java.lang.CharSequence different = identityCharSequence("different contents");

    org.junit.Assert.assertTrue(StringUtils.equals(first, second));
    org.junit.Assert.assertFalse(StringUtils.equals(first, different));
}

@org.junit.Test
public void abbreviateHandlesBasicWidthsAndNullInput() {
    org.junit.Assert.assertNull(StringUtils.abbreviate(null, 4));
    org.junit.Assert.assertEquals("abc", StringUtils.abbreviate("abc", 4));
    org.junit.Assert.assertEquals("a...", StringUtils.abbreviate("abcdef", 4));
}

@org.junit.Test
public void abbreviateRejectsWidthsBelowMinimum() {
    try {
        StringUtils.abbreviate("abcdef", 3);
        org.junit.Assert.fail("Expected IllegalArgumentException for an abbreviation width below four");
    } catch (final java.lang.IllegalArgumentException expected) {
        // expected
    }
}

@org.junit.Test
public void abbreviateWithOffsetAndMiddleHandlesBoundaryResults() {
    org.junit.Assert.assertEquals("...fg...", StringUtils.abbreviate("abcdefghijklmnopqrst", 5, 8));
    org.junit.Assert.assertEquals("...fghij", StringUtils.abbreviate("abcdefghij", 5, 8));
    org.junit.Assert.assertEquals("a...f", StringUtils.abbreviateMiddle("abcdef", "...", 5));
    org.junit.Assert.assertEquals("abcdef", StringUtils.abbreviateMiddle("abcdef", "...", 4));
}

private static java.lang.CharSequence identityCharSequence(final java.lang.String value) {
    return new java.lang.CharSequence() {
        @Override
        public int length() {
            return value.length();
        }

        @Override
        public char charAt(final int index) {
            return value.charAt(index);
        }

        @Override
        public java.lang.CharSequence subSequence(final int start, final int end) {
            return value.subSequence(start, end);
        }

        @Override
        public java.lang.String toString() {
            return value;
        }
    };
}
}
