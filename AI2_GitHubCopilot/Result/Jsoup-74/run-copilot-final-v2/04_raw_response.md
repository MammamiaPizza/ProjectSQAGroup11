@Test
public void testJoinCollection() {
    assertEquals("a-b-c", StringUtil.join(java.util.Arrays.asList("a", "b", "c"), "-"));
}

@Test
public void testJoinIteratorEmpty() {
    java.util.Iterator<String> it = java.util.Collections.<String>emptyIterator();
    assertEquals("", StringUtil.join(it, ","));
}

@Test
public void testIsBlank() {
    assertTrue(StringUtil.isBlank(null));
    assertTrue(StringUtil.isBlank(""));
    assertTrue(StringUtil.isBlank("   "));
    assertTrue(StringUtil.isBlank("\t\n"));
    assertFalse(StringUtil.isBlank("hello"));
    assertFalse(StringUtil.isBlank(" hello "));
}

@Test
public void testIsNumeric() {
    assertFalse(StringUtil.isNumeric(null));
    assertFalse(StringUtil.isNumeric(""));
    assertTrue(StringUtil.isNumeric("123"));
    assertFalse(StringUtil.isNumeric("12a"));
    assertFalse(StringUtil.isNumeric("12.3"));
}