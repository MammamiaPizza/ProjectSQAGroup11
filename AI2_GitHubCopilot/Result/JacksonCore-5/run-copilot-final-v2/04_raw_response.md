@Test
public void testValueOf() {
    JsonPointer p = JsonPointer.valueOf("/hello");
    assertEquals("/hello", p.toString());
    assertEquals("hello", p.getMatchingProperty());
    assertEquals(JsonPointer.compile("/hello"), p);
}

@Test
public void testEmptyMatchProperty() {
    assertNull(JsonPointer.EMPTY.matchProperty("anything"));
}

@Test
public void testEscapedTildeWithNonZeroOne() {
    JsonPointer p = JsonPointer.compile("/~2abc");
    assertEquals("~2abc", p.getMatchingProperty());
    assertTrue(p.mayMatchProperty());
    assertFalse(p.mayMatchElement());
}

@Test
public void testLargeIndexNotIndex() {
    JsonPointer p = JsonPointer.compile("/12345678901");
    assertEquals(-1, p.getMatchingIndex());
    assertEquals("12345678901", p.getMatchingProperty());
    assertTrue(p.mayMatchProperty());
    assertFalse(p.mayMatchElement());
}