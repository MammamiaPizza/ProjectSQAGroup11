@Test
public void consumeReturnsCharactersAndEofAfterInput() {
    CharacterReader reader = new CharacterReader("a");

    org.junit.Assert.assertEquals('a', reader.consume());
    org.junit.Assert.assertEquals(CharacterReader.EOF, reader.consume());
    org.junit.Assert.assertEquals(2, reader.pos());
}

@Test
public void consumeAsStringReturnsCurrentCharacterAndAdvances() {
    CharacterReader reader = new CharacterReader("ab");

    org.junit.Assert.assertEquals("a", reader.consumeAsString());
    org.junit.Assert.assertEquals('b', reader.current());
    org.junit.Assert.assertEquals(1, reader.pos());
}

@Test
public void consumeLetterSequenceStopsAtFirstNonLetter() {
    CharacterReader reader = new CharacterReader("aZ19");

    org.junit.Assert.assertEquals("aZ", reader.consumeLetterSequence());
    org.junit.Assert.assertEquals('1', reader.current());
    org.junit.Assert.assertEquals(2, reader.pos());
}

@Test
public void consumeHexAndDigitSequencesStopAtNonMatchingCharacters() {
    CharacterReader hexReader = new CharacterReader("aF09G");
    org.junit.Assert.assertEquals("aF09", hexReader.consumeHexSequence());
    org.junit.Assert.assertEquals('G', hexReader.current());

    CharacterReader digitReader = new CharacterReader("123x");
    org.junit.Assert.assertEquals("123", digitReader.consumeDigitSequence());
    org.junit.Assert.assertEquals('x', digitReader.current());
}