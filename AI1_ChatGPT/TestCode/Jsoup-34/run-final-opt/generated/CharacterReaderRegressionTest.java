package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CharacterReaderRegressionTest {

    @Test
    public void nextIndexOfSequenceReturnsMinusOneForPartialMatchAtEnd() {
        CharacterReader reader = new CharacterReader("abcdefgX");

        assertEquals(-1, reader.nextIndexOf("Xy"));
        assertEquals(0, reader.pos());
    }

    @Test
    public void nextIndexOfSequenceReturnsMinusOneForUnmatchedSequenceNearEnd() {
        CharacterReader reader = new CharacterReader("blah blah");

        assertEquals(-1, reader.nextIndexOf("blahblah"));
        assertEquals(0, reader.pos());
    }

    @Test
    public void consumeToAnyConsumesEntireInputWhenNoTargetExists() {
        CharacterReader reader = new CharacterReader("abcdefgh");

        assertEquals("abcdefgh", reader.consumeToAny('x', 'y', 'z'));
        assertTrue(reader.isEmpty());
        assertEquals(8, reader.pos());
    }

    @Test
    public void consumeToAnyAtEndReturnsEmptyWithoutMovingPastEnd() {
        CharacterReader reader = new CharacterReader("abc");
        reader.consumeToEnd();

        assertEquals("", reader.consumeToAny('x', 'y'));
        assertTrue(reader.isEmpty());
        assertEquals(3, reader.pos());
    }

    @Test
    public void consumeToAnyStopsBeforeMatchingCharacter() {
        CharacterReader reader = new CharacterReader("abc:def");

        assertEquals("abc", reader.consumeToAny(':', ';'));
        assertEquals(':', reader.current());
        assertEquals(3, reader.pos());
    }

    @Test
    public void unclosedCdataAtEndOfInputIsParsedAsText() {
        Document document = Jsoup.parse("<![CDATA[unfinished cdata");

        assertNotNull(document.body());
        assertEquals("unfinished cdata", document.body().text());
        assertFalse(document.body().text().isEmpty());
    }
}
