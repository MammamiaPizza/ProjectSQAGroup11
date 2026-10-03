@Test
public void joinObjectArrayRangeWithStringSeparatorHandlesNullAtNonzeroStart() {
    assertEquals(":tail", StringUtils.join(new Object[] { "ignored", null, "tail", "ignored" }, ":", 1, 3));
}

@Test
public void abbreviateWithOffsetReturnsNullAndLeavesShortStringsUnchanged() {
    assertNull(StringUtils.abbreviate(null, 0, 4));
    assertEquals("short", StringUtils.abbreviate("short", 100, 5));
}

@Test(expected = IllegalArgumentException.class)
public void abbreviateWithOffsetRejectsWidthBelowFour() {
    StringUtils.abbreviate("abcdef", 0, 3);
}

@Test
public void abbreviateWithOffsetPastEndUsesTrailingCharacters() {
    assertEquals("...ijklmno", StringUtils.abbreviate("abcdefghijklmno", 20, 10));
}