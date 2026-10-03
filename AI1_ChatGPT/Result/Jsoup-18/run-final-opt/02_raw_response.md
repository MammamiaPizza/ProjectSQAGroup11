package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CharacterReaderRegressionTest {

    @Test
    public void treatsCarriageReturnAsLineFeedWhenScanning() {
        CharacterReader reader = new CharacterReader("one\r two");

        assertEquals("one", reader.consumeTo('\n'));
        assertEquals('\n', reader.consume());
        assertEquals(" two", reader.consumeToEnd());
    }

    @Test
    public void treatsCarriageReturnLineFeedAsSingleLineFeed() {
        CharacterReader reader = new CharacterReader("one\r\n two");

        assertEquals("one", reader.consumeTo('\n'));
        assertEquals('\n', reader.consume());
        assertEquals(" two", reader.consumeToEnd());
    }

    @Test
    public void consumeToEndIncludesFinalCharacter() {
        CharacterReader reader = new CharacterReader("one two three");

        assertEquals("one two three", reader.consumeToEnd());
        assertTrue(reader.isEmpty());
        assertEquals(CharacterReader.EOF, reader.current());
    }

    @Test
    public void consumeToEndOnEmptyInputReturnsEmptyString() {
        CharacterReader reader = new CharacterReader("");

        assertEquals("", reader.consumeToEnd());
        assertTrue(reader.isEmpty());
    }

    @Test
    public void consumeToMissingDelimiterConsumesAllRemainingCharacters() {
        CharacterReader reader = new CharacterReader("abc");

        assertEquals("abc", reader.consumeTo('x'));
        assertTrue(reader.isEmpty());
    }

    @Test
    public void parserTreatsNewlineBetweenAttributesAsWhitespace() {
        Element link = Jsoup.parse("<a href=\"one\"\n id=\"two\"></a>").select("a").first();

        assertEquals("<a href=\"one\" id=\"two\"></a>", link.outerHtml());
        assertEquals("one", link.attr("href"));
        assertEquals("two", link.attr("id"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullInput() {
        new CharacterReader(null);
    }
}