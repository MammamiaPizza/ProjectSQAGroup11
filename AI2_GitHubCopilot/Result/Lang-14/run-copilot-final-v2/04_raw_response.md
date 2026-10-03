@Test(expected = IllegalArgumentException.class)
public void testAbbreviate_MaxWidthLessThanFour_ThrowsException() {
    StringUtils.abbreviate("abc", 3);
}

@Test
public void testAbbreviate_ShorterThanMaxWidth_ReturnsOriginal() {
    assertTrue(StringUtils.equals("abc", StringUtils.abbreviate("abc", 5)));
}

@Test
public void testAbbreviateWithOffset_OffsetExceedsStringLength_ReturnsAbbreviation() {
    assertTrue(StringUtils.equals("...hij", StringUtils.abbreviate("abcdefghij", 12, 7)));
}

@Test
public void testAbbreviateMiddle_LengthTooSmall_ReturnsOriginal() {
    assertTrue(StringUtils.equals("abc", StringUtils.abbreviateMiddle("abc", "...", 4)));
}