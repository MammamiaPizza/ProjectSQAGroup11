@Test
public void testIndexOfAnyButFindsCharacterAfterMatchingSupplementaryPair() {
    final String pair = "\uD83D\uDE00";
    assertEquals(2, StringUtils.indexOfAnyBut(pair + "x", pair));
    assertEquals(2, StringUtils.indexOfAnyBut(pair + "x", pair.toCharArray()));
}

@Test
public void testIndexOfAnyButRejectsMalformedSourceSupplementaryPair() {
    final String pair = "\uD83D\uDE00";
    final String malformed = "\uD83Dx";
    assertEquals(0, StringUtils.indexOfAnyBut(malformed, pair));
    assertEquals(0, StringUtils.indexOfAnyBut(malformed, pair.toCharArray()));
}

@Test
public void testAbbreviateMiddleBoundaryCases() {
    assertEquals(null, StringUtils.abbreviateMiddle(null, ".", 4));
    assertEquals("abc", StringUtils.abbreviateMiddle("abc", ".", 3));
    assertEquals("ab.f", StringUtils.abbreviateMiddle("abcdef", ".", 4));
}

@Test
public void testDifferenceWhenStringsShareAndDoNotSharePrefix() {
    assertEquals("xyz", StringUtils.difference("abcde", "abxyz"));
    assertEquals("xyz", StringUtils.difference("abcde", "xyz"));
}