@Test
    public void testConsumeLetterSequence() {
        CharacterReader reader = new CharacterReader("abc123");
        String result = reader.consumeLetterSequence();
        assertEquals("abc", result);
        assertEquals('1', reader.current());
    }

 @Test
 public void testConsumeLetterThenDigitSequence() {
     CharacterReader reader = new CharacterReader("abc123");
     String result = reader.consumeLetterThenDigitSequence();
     assertEquals("abc123", result);
     assertEquals(6, reader.pos());
 }

 @Test
 public void testConsumeHexSequence() {
     CharacterReader reader = new CharacterReader("aBc12g");
     String result = reader.consumeHexSequence();
     assertEquals("aBc12", result);
     assertEquals('g', reader.current());
 }

 @Test
 public void testConsumeDigitSequence() {
     CharacterReader reader = new CharacterReader("123abc");
     String result = reader.consumeDigitSequence();
     assertEquals("123", result);
     assertEquals('a', reader.current());
 }