package org.apache.commons.lang3;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StringUtilsLang786Test {

    @Test
    public void testEqualsSameStringInstance() {
        final String value = new String("same instance");
        assertTrue(StringUtils.equals(value, value));
    }

    @Test
    public void testEqualsStringValuesAndNulls() {
        assertTrue(StringUtils.equals("alpha", new String("alpha")));
        assertFalse(StringUtils.equals("alpha", "beta"));
        assertTrue(StringUtils.equals(null, null));
        assertFalse(StringUtils.equals(null, "alpha"));
        assertFalse(StringUtils.equals("alpha", null));
    }

    @Test
    public void testEqualsDistinctStringBuildersWithSameContent() {
        final CharSequence first = new StringBuilder("builder content");
        final CharSequence second = new StringBuilder("builder content");

        assertTrue(StringUtils.equals(first, second));
    }

    @Test
    public void testEqualsStringAndStringBuilderWithSameContentInBothOrders() {
        final String text = "mixed content";
        final StringBuilder builder = new StringBuilder(text);

        assertTrue(StringUtils.equals(text, builder));
        assertTrue(StringUtils.equals(builder, text));
    }

    @Test
    public void testEqualsEmptyCharSequencesAndDifferentLengths() {
        assertTrue(StringUtils.equals(new StringBuilder(), new StringBuilder()));
        assertFalse(StringUtils.equals(new StringBuilder(""), new StringBuilder("x")));
        assertFalse(StringUtils.equals(new StringBuilder("short"), new StringBuilder("shorter")));
    }

    @Test
    public void testEqualsCharSequencesDifferingNearEnd() {
        assertFalse(StringUtils.equals(
                new StringBuilder("identical-prefix-a"),
                new StringBuilder("identical-prefix-b")));
    }
}
