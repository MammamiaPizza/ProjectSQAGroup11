@org.junit.Test
public void abbreviateWithOffsetHandlesLeadingRecursiveAndTrailingAbbreviations() {
    org.junit.Assert.assertEquals("abcdefg...", StringUtils.abbreviate("abcdefghijklmno", 0, 10));
    org.junit.Assert.assertEquals("...fghi...", StringUtils.abbreviate("abcdefghijklmno", 5, 10));
    org.junit.Assert.assertEquals("...ijklmno", StringUtils.abbreviate("abcdefghijklmno", 20, 10));
}

@org.junit.Test(expected = IllegalArgumentException.class)
public void abbreviateWithOffsetRejectsWidthsBelowSevenAfterOffset() {
    StringUtils.abbreviate("abcdefghijklmno", 5, 6);
}

@org.junit.Test
public void abbreviateMiddlePreservesBothEndsAroundReplacement() {
    org.junit.Assert.assertEquals("ab...gh", StringUtils.abbreviateMiddle("abcdefgh", "...", 7));
}