@Test
public void testConstructor() {
    new StringUtils();
}

@Test
public void testAbbreviate_Null() {
    assertNull(StringUtils.abbreviate(null, 0, 10));
}

@Test(expected = IllegalArgumentException.class)
public void testAbbreviate_MaxWidthLessThan4() {
    StringUtils.abbreviate("abc", 0, 3);
}

@Test
public void testAbbreviate_StringShorterThanMaxWidth() {
    assertEquals("abc", StringUtils.abbreviate("abc", 0, 5));
}