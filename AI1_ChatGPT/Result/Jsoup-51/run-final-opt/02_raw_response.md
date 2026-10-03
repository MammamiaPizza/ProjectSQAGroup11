package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CharacterReaderNonAsciiTest {

    @Test
    public void parsesTextInsideNonAsciiTag() {
        Document document = Jsoup.parse("<中文>Yes</中文>");
        Element element = document.body().child(0);

        assertEquals("中文", element.tagName());
        assertEquals("Yes", element.text());
    }

    @Test
    public void consumeToAnyRetainsNonAsciiBeforeAsciiDelimiter() {
        CharacterReader reader = new CharacterReader("π;rest");

        assertEquals("π", reader.consumeToAny(';'));
        assertEquals(Character.valueOf(';'), Character.valueOf(reader.current()));
        assertEquals(1, reader.pos());
    }

    @Test
    public void consumeToAnyStopsAtNonAsciiDelimiter() {
        CharacterReader reader = new CharacterReader("abΩtail");

        assertEquals("ab", reader.consumeToAny('Ω'));
        assertEquals(Character.valueOf('Ω'), Character.valueOf(reader.current()));
        assertEquals(2, reader.pos());
    }

    @Test
    public void consumeToAnyHandlesNonAsciiAtStartAndEnd() {
        CharacterReader startsWithNonAscii = new CharacterReader("Ω;");
        assertEquals("Ω", startsWithNonAscii.consumeToAny(';'));
        assertEquals(Character.valueOf(';'), Character.valueOf(startsWithNonAscii.current()));

        CharacterReader endsWithNonAscii = new CharacterReader("textΩ");
        assertEquals("textΩ", endsWithNonAscii.consumeToAny(';'));
        assertTrue(endsWithNonAscii.isEmpty());
    }

    @Test
    public void consumeToAnyReturnsEmptyWhenDelimiterIsFirstCharacter() {
        CharacterReader reader = new CharacterReader("Ωtext");

        assertEquals("", reader.consumeToAny('Ω'));
        assertEquals(Character.valueOf('Ω'), Character.valueOf(reader.current()));
        assertEquals(0, reader.pos());
    }

    @Test
    public void consumeToAnySortedFindsNonAsciiDelimiter() {
        CharacterReader reader = new CharacterReader("beforeΩafter");
        char[] delimiters = new char[] {'!', 'Ω'};

        assertEquals("before", reader.consumeToAnySorted(delimiters));
        assertEquals(Character.valueOf('Ω'), Character.valueOf(reader.current()));
        assertEquals(6, reader.pos());
    }

    @Test
    public void consumeTagNameIncludesNonAsciiCharacters() {
        CharacterReader reader = new CharacterReader("中文>");

        assertEquals("中文", reader.consumeTagName());
        assertEquals(Character.valueOf('>'), Character.valueOf(reader.current()));
    }

    @Test
    public void rangeEqualsComparesNonAsciiCachedTextCorrectly() {
        CharacterReader reader = new CharacterReader("éΩx");

        assertTrue(reader.rangeEquals(0, 2, "éΩ"));
        assertFalse(reader.rangeEquals(0, 2, "éΨ"));
        assertFalse(reader.rangeEquals(0, 1, "éΩ"));
    }
}