package com.fasterxml.jackson.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class JsonPointerTest {

    @Test
    public void leadingZeroIndexesAreNotArrayIndexes() {
        JsonPointer pointer = JsonPointer.compile("/00");

        assertEquals("00", pointer.getMatchingProperty());
        assertEquals(-1, pointer.getMatchingIndex());
        assertFalse(pointer.mayMatchElement());
        assertTrue(pointer.mayMatchProperty());
        assertNull(pointer.matchElement(0));
        assertNotNull(pointer.matchProperty("00"));
    }

    @Test
    public void zeroIsAValidArrayIndex() {
        JsonPointer pointer = JsonPointer.compile("/0");

        assertEquals("0", pointer.getMatchingProperty());
        assertEquals(0, pointer.getMatchingIndex());
        assertTrue(pointer.mayMatchElement());
        assertNotNull(pointer.matchElement(0));
        assertNull(pointer.matchElement(1));
    }

    @Test
    public void normalNumericSegmentMatchesBothPropertyAndElement() {
        JsonPointer pointer = JsonPointer.compile("/12");

        assertEquals("12", pointer.getMatchingProperty());
        assertEquals(12, pointer.getMatchingIndex());
        assertTrue(pointer.mayMatchProperty());
        assertTrue(pointer.mayMatchElement());
        assertNotNull(pointer.matchProperty("12"));
        assertNotNull(pointer.matchElement(12));
        assertNull(pointer.matchElement(11));
    }

    @Test
    public void nonNumericAndNegativeSegmentsDoNotMatchElements() {
        JsonPointer text = JsonPointer.compile("/abc");
        JsonPointer negative = JsonPointer.compile("/-1");

        assertEquals(-1, text.getMatchingIndex());
        assertFalse(text.mayMatchElement());
        assertNotNull(text.matchProperty("abc"));

        assertEquals(-1, negative.getMatchingIndex());
        assertFalse(negative.mayMatchElement());
        assertNull(negative.matchElement(-1));
    }

    @Test
    public void indexRangeAndLengthBoundariesAreHandled() {
        assertEquals(Integer.MAX_VALUE,
                JsonPointer.compile("/2147483647").getMatchingIndex());
        assertEquals(-1,
                JsonPointer.compile("/2147483648").getMatchingIndex());
        assertEquals(-1,
                JsonPointer.compile("/12345678901").getMatchingIndex());
    }

    @Test
    public void tailTraversalRetainsRemainingPointerSegments() {
        JsonPointer pointer = JsonPointer.compile("/12/name/0");

        assertEquals("/12/name/0", pointer.toString());
        assertEquals(12, pointer.getMatchingIndex());

        JsonPointer second = pointer.tail();
        assertEquals("/name/0", second.toString());
        assertEquals("name", second.getMatchingProperty());

        JsonPointer third = second.matchProperty("name");
        assertNotNull(third);
        assertEquals("/0", third.toString());
        assertEquals(0, third.getMatchingIndex());

        JsonPointer empty = third.matchElement(0);
        assertNotNull(empty);
        assertTrue(empty.matches());
        assertEquals("", empty.toString());
    }

    @Test
    public void escapedPropertyNamesAreDecodedForMatching() {
        JsonPointer pointer = JsonPointer.compile("/a~1b~0c");

        assertEquals("/a~1b~0c", pointer.toString());
        assertEquals("a/b~c", pointer.getMatchingProperty());
        assertNotNull(pointer.matchProperty("a/b~c"));
        assertNull(pointer.matchProperty("a~1b~0c"));
    }

    @Test
    public void emptyPointerMatchesCurrentLocation() {
        JsonPointer pointer = JsonPointer.valueOf("");

        assertTrue(pointer.matches());
        assertEquals("", pointer.toString());
        assertEquals("", pointer.getMatchingProperty());
        assertEquals(-1, pointer.getMatchingIndex());
        assertFalse(pointer.mayMatchElement());
        assertNull(pointer.tail());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonEmptyPointerMustStartWithSlash() {
        JsonPointer.compile("property");
    }
}