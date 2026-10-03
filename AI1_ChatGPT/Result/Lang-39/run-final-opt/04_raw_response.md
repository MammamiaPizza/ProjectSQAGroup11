@Test
public void abbreviateHandlesNullUnchangedAndPrefixAbbreviations() {
    final String text = "abcdefghijklmno";
    assertNull(StringUtils.abbreviate(null, 4));
    assertSame(text, StringUtils.abbreviate(text, text.length()));
    assertEquals("a...", StringUtils.abbreviate(text, 4));
    assertEquals("abcde...", StringUtils.abbreviate(text, 3, 8));
}

@Test
public void abbreviateUsesOffsetAndAdjustsOffsetsPastTheEnd() {
    final String text = "abcdefghijklmno";
    assertEquals("...fg...", StringUtils.abbreviate(text, 5, 8));
    assertEquals("...klmno", StringUtils.abbreviate(text, 50, 8));
}

@Test
public void abbreviateRejectsWidthsTooSmallForTheRequestedForm() {
    try {
        StringUtils.abbreviate("abcdefgh", 3);
        throw new java.lang.AssertionError("Expected IllegalArgumentException");
    } catch (java.lang.IllegalArgumentException expected) {
    }

    try {
        StringUtils.abbreviate("abcdefghijklmno", 5, 6);
        throw new java.lang.AssertionError("Expected IllegalArgumentException");
    } catch (java.lang.IllegalArgumentException expected) {
    }
}

@Test
public void capitalizeHandlesNullEmptyAndNonEmptyStrings() {
    assertNull(StringUtils.capitalize(null));
    assertSame("", StringUtils.capitalize(""));
    assertEquals("Cat", StringUtils.capitalize("cat"));
    assertEquals("CAT", StringUtils.capitalize("CAT"));
}