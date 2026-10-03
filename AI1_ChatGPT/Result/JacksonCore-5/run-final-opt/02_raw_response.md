package com.fasterxml.jackson.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class JsonPointerBug5Test
{
    @Test
    public void scientificLookingSegmentIsPropertyNotIndex() {
        JsonPointer pointer = JsonPointer.compile("/1e0");

        assertEquals("/1e0", pointer.toString());
        assertEquals("1e0", pointer.getMatchingProperty());
        assertEquals(-1, pointer.getMatchingIndex());
        assertTrue(pointer.mayMatchProperty());
        assertFalse(pointer.mayMatchElement());
    }

    @Test
    public void valueOfAlsoAcceptsScientificLookingPropertySegment() {
        JsonPointer pointer = JsonPointer.valueOf("/1e0");

        assertNotNull(pointer);
        assertSame(pointer.tail(), pointer.matchProperty("1e0"));
        assertNull(pointer.matchElement(1));
    }

    @Test
    public void ordinaryNumericSegmentMatchesElement() {
        JsonPointer pointer = JsonPointer.compile("/0");

        assertEquals("0", pointer.getMatchingProperty());
        assertEquals(0, pointer.getMatchingIndex());
        assertTrue(pointer.mayMatchElement());
        assertSame(pointer.tail(), pointer.matchElement(0));
        assertNull(pointer.matchElement(1));
    }

    @Test
    public void leadingZeroDigitsRemainParseableAsAnIndex() {
        JsonPointer pointer = JsonPointer.compile("/01");

        assertEquals(1, pointer.getMatchingIndex());
        assertTrue(pointer.mayMatchElement());
        assertSame(pointer.tail(), pointer.matchElement(1));
    }

    @Test
    public void negativeLookingSegmentIsOnlyAProperty() {
        JsonPointer pointer = JsonPointer.compile("/-1");

        assertEquals("-1", pointer.getMatchingProperty());
        assertEquals(-1, pointer.getMatchingIndex());
        assertFalse(pointer.mayMatchElement());
        assertSame(pointer.tail(), pointer.matchProperty("-1"));
        assertNull(pointer.matchElement(0));
    }

    @Test
    public void multiSegmentPointerMatchesAndExposesTail() {
        JsonPointer pointer = JsonPointer.compile("/12/name");

        assertEquals(12, pointer.getMatchingIndex());
        JsonPointer tail = pointer.matchElement(12);
        assertNotNull(tail);
        assertEquals("/name", tail.toString());
        assertEquals("name", tail.getMatchingProperty());
        assertTrue(tail.matches() == false);
        assertTrue(tail.tail().matches());
    }

    @Test
    public void escapedPropertyNameIsDecodedForMatching() {
        JsonPointer pointer = JsonPointer.compile("/a~1b");

        assertEquals("a/b", pointer.getMatchingProperty());
        assertSame(pointer.tail(), pointer.matchProperty("a/b"));
        assertNull(pointer.matchProperty("a~1b"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void pointerMustStartWithSlash() {
        JsonPointer.compile("not/a/pointer");
    }

    @Test
    public void emptyAndNullInputRepresentEmptyPointer() {
        JsonPointer empty = JsonPointer.compile("");
        JsonPointer nullInput = JsonPointer.compile(null);

        assertTrue(empty.matches());
        assertEquals("", empty.toString());
        assertEquals(empty, nullInput);
        assertFalse(empty.mayMatchElement());
    }
}