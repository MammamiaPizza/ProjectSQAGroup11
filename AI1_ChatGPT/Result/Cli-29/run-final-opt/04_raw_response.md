public void testStripLeadingHyphens()
{
    assertNull(Util.stripLeadingHyphens(null));
    assertEquals("option", Util.stripLeadingHyphens("--option"));
    assertEquals("option", Util.stripLeadingHyphens("-option"));
    assertEquals("option", Util.stripLeadingHyphens("option"));
}