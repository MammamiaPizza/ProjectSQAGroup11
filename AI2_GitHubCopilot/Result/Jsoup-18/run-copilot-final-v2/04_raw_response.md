@Test
    public void consumeLetterSequence() {
        CharacterReader reader = new CharacterReader("abc123");
        assertEquals("abc", reader.consumeLetterSequence());
        assertEquals(3, reader.pos());
    }

 @Test
 public void consumeHexSequence() {
     CharacterReader reader = new CharacterReader("ab12cd");
     assertEquals("ab12cd", reader.consumeHexSequence());
     assertEquals(6, reader.pos());
 }

 @Test
 public void consumeDigitSequence() {
     CharacterReader reader = new CharacterReader("123abc");
     assertEquals("123", reader.consumeDigitSequence());
     assertEquals(3, reader.pos());
 }

 @Test
 public void consumeToCharFound() {
     CharacterReader reader = new CharacterReader("hello;world");
     assertEquals("hello", reader.consumeTo(';'));
     assertEquals(5, reader.pos());
 }