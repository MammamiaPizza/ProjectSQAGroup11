@Test public void constructorWithFailingReaderThrows() {
        boolean thrown = false;
        try {
            CharacterReader reader = new CharacterReader(new java.io.Reader() {
                @Override public int read(char[] cbuf, int off, int len) throws java.io.IOException
{
                    throw new java.io.IOException("boom");
                }
                @Override public void close() throws java.io.IOException {
                }
            });
            reader.consumeData();
        } catch (RuntimeException expected) {
            thrown = true;
        }
        assertTrue(thrown);
    }

 @Test public void consumeDataStopsAtSpecialCharacters() {
     CharacterReader reader = new CharacterReader("abc<def");
     assertEquals("abc", reader.consumeData());
     assertEquals('<', reader.current());

     CharacterReader amp = new CharacterReader("abc&def");
     assertEquals("abc", amp.consumeData());
     assertEquals('&', amp.current());

     CharacterReader nul = new CharacterReader("abc\u0000def");
     assertEquals("abc", nul.consumeData());
     assertEquals('\u0000', nul.current());
 }

 @Test public void consumeDataReturnsEmptyWhenStartingAtSpecialCharacter() {
     CharacterReader reader = new CharacterReader("&rest");
     assertEquals("", reader.consumeData());
     assertEquals('&', reader.current());
 }

 @Test public void consumeDigitSequenceStopsAtNonDigitAndReturnsEmptyWithoutDigits() {
     CharacterReader reader = new CharacterReader("12345z");
     assertEquals("12345", reader.consumeDigitSequence());
     assertEquals('z', reader.current());

     CharacterReader noDigits = new CharacterReader("abc");
     assertEquals("", noDigits.consumeDigitSequence());
     assertEquals('a', noDigits.current());
 }