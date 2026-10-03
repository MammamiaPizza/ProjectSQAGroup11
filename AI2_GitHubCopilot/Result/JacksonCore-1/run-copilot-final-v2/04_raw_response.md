@Test
    public void testInLongRangeBoundaries() {
        assertTrue(NumberInput.inLongRange("9223372036854775807", false));
        assertFalse(NumberInput.inLongRange("9223372036854775808", false));
        assertTrue(NumberInput.inLongRange("9223372036854775808", true));
        assertFalse(NumberInput.inLongRange("9223372036854775809", true));
        assertTrue(NumberInput.inLongRange("1", false));
        assertTrue(NumberInput.inLongRange("1", true));
    }

 @Test
 public void testInLongRangeSameLengthLexicographic() {
     assertTrue(NumberInput.inLongRange("9223372036854775807", true));
     assertFalse(NumberInput.inLongRange("9223372036854775809", true));
     assertTrue(NumberInput.inLongRange("9223372036854775800", false));
     assertFalse(NumberInput.inLongRange("9223372036854775808", false));
 }

 @Test
 public void testTextBufferSizeAcrossModes() {
     TextBuffer tb = new TextBuffer(new com.fasterxml.jackson.core.util.BufferRecycler());
     tb.resetWithShared(new char[]{'a', 'b', 'c'}, 0, 3);
     assertEquals(3, tb.size());
     assertEquals(0, tb.getTextOffset());
     tb.resetWithString("abcd");
     assertEquals(4, tb.size());
     tb.resetWithEmpty();
     assertEquals(0, tb.size());
     tb.append('x');
     assertEquals(1, tb.size());
 }

 @Test
 public void testTextBufferResetWithCopyAndContents() {
     TextBuffer tb = new TextBuffer(new com.fasterxml.jackson.core.util.BufferRecycler());
     char[] data = {'1', '.', '5'};
     tb.resetWithCopy(data, 0, 3);
     assertEquals(3, tb.size());
     assertEquals("1.5", tb.contentsAsString());
     assertEquals(1.5, tb.contentsAsDouble(), 0.0);
     char[] out = tb.contentsAsArray();
     assertEquals(3, out.length);
     assertEquals('1', out[0]);
 }