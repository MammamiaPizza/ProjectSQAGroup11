@Test
public void consumeToStringStopsBeforeMatchedSequence() {
    CharacterReader reader = new CharacterReader("one--two");

    assertEquals("one", reader.consumeTo("--"));
    assertEquals('-', reader.current());
    assertEquals('-', reader.consume());
    assertEquals('-', reader.consume());
    assertEquals("two", reader.consumeToEnd());
}

@Test
public void consumeLetterSequenceStopsAtFirstNonLetter() {
    CharacterReader reader = new CharacterReader("AbZ1");

    assertEquals("AbZ", reader.consumeLetterSequence());
    assertEquals('1', reader.current());
}

@Test
public void consumeHexSequenceAcceptsBothCasesAndStopsAtNonHex() {
    CharacterReader reader = new CharacterReader("09aBcG");

    assertEquals("09aBc", reader.consumeHexSequence());
    assertEquals('G', reader.current());
}

@Test
public void consumeDigitSequenceStopsAtFirstNonDigit() {
    CharacterReader reader = new CharacterReader("123x");

    assertEquals("123", reader.consumeDigitSequence());
    assertEquals('x', reader.current());
}