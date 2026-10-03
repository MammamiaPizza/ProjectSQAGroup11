@Test
    public void testAbbreviateWithOffset_offsetExceedsLengthAndAdjustsToPrefix() {
        assertEquals("abcd...", StringUtils.abbreviate("abcdefgh", 10, 7));
    }

 @Test
 public void testAbbreviateWithOffset_elseBranchReturnsSuffix() {
     assertEquals("...fghij", StringUtils.abbreviate("abcdefghij", 9, 8));
 }

 @Test(expected = IllegalArgumentException.class)
 public void testAbbreviateWithOffset_maxWidthLessThanSevenWithOffsetGreaterThanFour() {
     StringUtils.abbreviate("abcdefghij", 5, 6);
 }

 @Test
 public void testAbbreviateMiddle_basic() {
     assertEquals("a...f", StringUtils.abbreviateMiddle("abcdef", "...", 5));
 }