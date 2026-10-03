@Test
public void consumeDataStopsBeforeAmpersandTagAndNullCharacters() {
    CharacterReader reader = new CharacterReader(new java.io.StringReader("a&b<c\u0000d"));

    assertEquals("a", reader.consumeData());
    assertEquals('&', reader.current());
    reader.advance();

    assertEquals("b", reader.consumeData());
    assertEquals('<', reader.current());
    reader.advance();

    assertEquals("c", reader.consumeData());
    assertEquals('\u0000', reader.current());
    reader.advance();

    assertEquals("d", reader.consumeData());
    assertTrue(reader.isEmpty());
}

@Test
public void consumeToAnyLeavesMatchedDelimiterUnread() {
    CharacterReader reader = new CharacterReader("one,two;three");

    assertEquals("one", reader.consumeToAny(',', ';'));
    assertEquals(',', reader.current());
    reader.advance();

    assertEquals("two", reader.consumeToAny(',', ';'));
    assertEquals(';', reader.current());
}

@Test
public void consumeReturnsEofAfterInputIsExhausted() {
    CharacterReader reader = new CharacterReader("x");

    assertEquals('x', reader.consume());
    assertEquals(CharacterReader.EOF, reader.consume());
    assertTrue(reader.isEmpty());
}

@Test(expected = java.io.UncheckedIOException.class)
public void readerConstructorWrapsIOException() {
    new CharacterReader(new java.io.Reader() {
        @Override
        public int read(char[] cbuf, int off, int len) throws java.io.IOException {
            throw new java.io.IOException("read failure");
        }

        @Override
        public void close() {
        }
    });
}