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