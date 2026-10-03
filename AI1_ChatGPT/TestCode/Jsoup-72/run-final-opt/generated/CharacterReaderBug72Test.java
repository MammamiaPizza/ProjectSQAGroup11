package org.jsoup.parser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CharacterReaderBug72Test {

    @Test
    public void consumeToLeavesCurrentDelimiterUnread() {
        CharacterReader reader = new CharacterReader("&tail");

        assertFalse(reader.isEmpty());
        assertEquals('&', reader.current());
        assertEquals("", reader.consumeTo('&'));
        assertEquals(0, reader.pos());
        assertEquals('&', reader.current());

        reader.advance();
        assertEquals("tail", reader.consumeTo('&'));
        assertTrue(reader.isEmpty());
    }

    @Test
    public void consumeToConsumesTextBeforeDelimiterAndStopsAtDelimiter() {
        CharacterReader reader = new CharacterReader("abc&def");

        assertEquals("abc", reader.consumeTo('&'));
        assertEquals(3, reader.pos());
        assertEquals('&', reader.current());

        reader.advance();
        assertEquals("def", reader.consumeTo('&'));
        assertTrue(reader.isEmpty());
    }

    @Test
    public void consumeToAbsentDelimiterConsumesRemainingInput() {
        CharacterReader reader = new CharacterReader("comment text");

        assertEquals("comment text", reader.consumeTo('>'));
        assertEquals(12, reader.pos());
        assertTrue(reader.isEmpty());
    }

    @Test
    public void consumeToAbsentDelimiterAtEofReturnsEmptyWithoutThrowing() {
        CharacterReader reader = new CharacterReader("&");

        reader.advance();
        assertTrue(reader.isEmpty());
        assertEquals(1, reader.pos());

        assertEquals("", reader.consumeTo('&'));
        assertTrue(reader.isEmpty());
        assertEquals(1, reader.pos());
        assertEquals((char) -1, reader.current());
    }

    @Test
    public void consumeToOnEmptyInputReturnsEmptyAndRemainsAtEof() {
        CharacterReader reader = new CharacterReader("");

        assertTrue(reader.isEmpty());
        assertEquals("", reader.consumeTo('&'));
        assertEquals(0, reader.pos());
        assertEquals((char) -1, reader.current());
    }
}
