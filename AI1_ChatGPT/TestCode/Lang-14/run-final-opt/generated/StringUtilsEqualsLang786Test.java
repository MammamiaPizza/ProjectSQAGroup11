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
}
