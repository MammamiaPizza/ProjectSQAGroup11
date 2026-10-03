package org.apache.commons.codec.binary;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StringUtilsEqualsContractTest {

    @Test
    public void equalsReturnsTrueForSameStringReferenceAndEqualStrings() {
        final String value = new String("codec");

        assertTrue(StringUtils.equals(value, value));
        assertTrue(StringUtils.equals("codec", new String("codec")));
    }

    @Test
    public void equalsReturnsTrueForEqualStringAndStringBuilderInBothOrders() {
        final StringBuilder builder = new StringBuilder("codec");

        assertTrue(StringUtils.equals("codec", builder));
        assertTrue(StringUtils.equals(builder, "codec"));
    }

    @Test
    public void equalsReturnsTrueForEqualNonStringCharSequences() {
        assertTrue(StringUtils.equals(new StringBuilder("codec"), new StringBuffer("codec")));
    }

    @Test
    public void equalsReturnsFalseForSameLengthDifferentContent() {
        assertFalse(StringUtils.equals(new StringBuilder("codec"), new StringBuffer("codex")));
    }

    @Test
    public void equalsReturnsFalseForDifferentLengthsWithoutThrowing() {
        assertFalse(StringUtils.equals("codec", new StringBuilder("code")));
        assertFalse(StringUtils.equals(new StringBuilder("code"), "codec"));
    }

    @Test
    public void equalsHandlesEmptyAndNullCharSequences() {
        assertTrue(StringUtils.equals("", new StringBuilder()));
        assertTrue(StringUtils.equals(null, null));
        assertFalse(StringUtils.equals(null, ""));
        assertFalse(StringUtils.equals("", null));
    }
}
