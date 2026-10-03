@org.junit.Test
public void readerConstructorConsumesStringReaderAndReportsEof() {
    org.jsoup.parser.CharacterReader reader =
        new org.jsoup.parser.CharacterReader(new java.io.StringReader("ab"));

    org.junit.Assert.assertEquals(0, reader.pos());
    org.junit.Assert.assertEquals('a', reader.current());
    reader.advance();
    org.junit.Assert.assertEquals(1, reader.pos());
    org.junit.Assert.assertEquals('b', reader.current());
    reader.advance();

    org.junit.Assert.assertTrue(reader.isEmpty());
    org.junit.Assert.assertEquals((char) -1, reader.current());
}

@org.junit.Test
public void consumeToReusesMatchingTextAndHandlesHashCollisions() {
    org.jsoup.parser.CharacterReader reader =
        new org.jsoup.parser.CharacterReader("abc,abc,Aa,BB,");

    org.junit.Assert.assertEquals("abc", reader.consumeTo(','));
    reader.advance();
    org.junit.Assert.assertEquals("abc", reader.consumeTo(','));
    reader.advance();
    org.junit.Assert.assertEquals("Aa", reader.consumeTo(','));
    reader.advance();
    org.junit.Assert.assertEquals("BB", reader.consumeTo(','));
    org.junit.Assert.assertEquals(',', reader.current());
}

@org.junit.Test
public void consumeToAnyLeavesMatchedDelimiterAvailable() {
    org.jsoup.parser.CharacterReader reader =
        new org.jsoup.parser.CharacterReader("alpha|beta;");

    org.junit.Assert.assertEquals("alpha", reader.consumeToAny('|', ';'));
    org.junit.Assert.assertEquals('|', reader.current());
    reader.advance();
    org.junit.Assert.assertEquals("beta", reader.consumeToAny('|', ';'));
    org.junit.Assert.assertEquals(';', reader.current());
}