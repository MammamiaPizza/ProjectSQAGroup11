@Test
    public void testTildeEscapeUnknownChar() {
        JsonPointer ptr = JsonPointer.compile("/ab~2cd");
        assertEquals("ab~2cd", ptr.getMatchingProperty());
        assertEquals(-1, ptr.getMatchingIndex());
        assertTrue(ptr.mayMatchProperty());
        assertFalse(ptr.mayMatchElement());
    }

 @Test
 public void testPointerEndingWithTilde() {
     JsonPointer ptr = JsonPointer.compile("/abc~");
     assertEquals("abc~", ptr.getMatchingProperty());
     assertEquals(-1, ptr.getMatchingIndex());
     assertTrue(ptr.mayMatchProperty());
     assertFalse(ptr.mayMatchElement());
 }

 @Test
 public void testMatchPropertyNonMatching() {
     JsonPointer ptr = JsonPointer.compile("/foo");
     assertNull(ptr.matchProperty("bar"));
     assertNotNull(ptr.matchProperty("foo"));
 }

 @Test
 public void testMatchElementNonMatching() {
     JsonPointer ptr = JsonPointer.compile("/1");
     assertEquals(1, ptr.getMatchingIndex());
     assertTrue(ptr.mayMatchElement());
     assertNull(ptr.matchElement(2));
     assertNotNull(ptr.matchElement(1));
 }