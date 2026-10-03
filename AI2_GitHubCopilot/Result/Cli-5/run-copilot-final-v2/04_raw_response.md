public void testStripLeadingAndTrailingQuotes_null() {
    try {
        assertNull(Util.stripLeadingAndTrailingQuotes(null));
    } catch (NullPointerException e) {
        fail("stripLeadingAndTrailingQuotes(null) should not throw NullPointerException");
    }
}

public void testStripLeadingAndTrailingQuotes_empty() {
    assertEquals("", Util.stripLeadingAndTrailingQuotes(""));
}

public void testStripLeadingAndTrailingQuotes_noQuotes() {
    assertEquals("hello", Util.stripLeadingAndTrailingQuotes("hello"));
    assertEquals("a", Util.stripLeadingAndTrailingQuotes("a"));
}

public void testStripLeadingAndTrailingQuotes_withQuotes() {
    assertEquals("hello", Util.stripLeadingAndTrailingQuotes(""hello""));
    assertEquals("hello"", Util.stripLeadingAndTrailingQuotes("hello""));
    assertEquals(""hello", Util.stripLeadingAndTrailingQuotes(""hello"));
    assertEquals("", Util.stripLeadingAndTrailingQuotes("""));
    assertEquals(""hello"", Util.stripLeadingAndTrailingQuotes("""hello"""));
    assertEquals(""", Util.stripLeadingAndTrailingQuotes("""""));
}