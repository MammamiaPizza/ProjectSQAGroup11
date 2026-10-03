@Test
public void testContainsIgnoreCaseDoesNotTreatSharpSAsTwoCharacters() {
    assertFalse(StringUtils.containsIgnoreCase("\u00df", "SS"));
    assertFalse(StringUtils.containsIgnoreCase("SS", "\u00df"));
}

@Test
public void testAbbreviateWithOffsetSelectsExpectedSections() {
    assertEquals("abcdefg...", StringUtils.abbreviate("abcdefghijklmno", 2, 10));
    assertEquals("...ijklmno", StringUtils.abbreviate("abcdefghijklmno", 100, 10));
    assertEquals("...klmn...", StringUtils.abbreviate("abcdefghijklmnopqrstuvwxyz", 10, 10));
}

@Test
public void testAbbreviateWithOffsetRejectsTooNarrowMiddleAbbreviation() {
    boolean thrown = false;
    try {
        StringUtils.abbreviate("abcdefghijk", 5, 6);
    } catch (IllegalArgumentException expected) {
        thrown = true;
    }
    assertTrue(thrown);
}