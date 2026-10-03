@Test
public void consumeLetterSequenceStopsAtFirstNonLetter() {
    CharacterReader reader = new CharacterReader("AbZ9");

    assertEquals("AbZ", reader.consumeLetterSequence());
    assertEquals(Character.valueOf('9'), Character.valueOf(reader.current()));
    assertEquals(3, reader.pos());
}

@Test
public void consumeHexAndDigitSequencesStopAtInvalidCharacters() {
    CharacterReader hexReader = new CharacterReader("aF09g");
    assertEquals("aF09", hexReader.consumeHexSequence());
    assertEquals(Character.valueOf('g'), Character.valueOf(hexReader.current()));

    CharacterReader digitReader = new CharacterReader("123x");
    assertEquals("123", digitReader.consumeDigitSequence());
    assertEquals(Character.valueOf('x'), Character.valueOf(digitReader.current()));
}

@Test
public void consumeLetterSequenceReturnsLongStringsWithoutCaching() {
    CharacterReader reader = new CharacterReader("abcdefghijklmn!");

    assertEquals("abcdefghijklmn", reader.consumeLetterSequence());
    assertEquals(Character.valueOf('!'), Character.valueOf(reader.current()));
}

@Test
public void consumeLetterSequenceHandlesCachedHashCollisions() {
    CharacterReader reader = new CharacterReader("Aa!BB");

    assertEquals("Aa", reader.consumeLetterSequence());
    assertEquals("!", reader.consumeAsString());
    assertEquals("BB", reader.consumeLetterSequence());
}