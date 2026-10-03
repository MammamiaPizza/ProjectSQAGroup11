package com.fasterxml.jackson.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class TextBufferExpansionTest
{
    @Test
    public void expandCurrentSegmentGrowsAndRetainsCurrentContents() {
        TextBuffer buffer = new TextBuffer(null);
        char[] segment = buffer.getCurrentSegment();
        int originalLength = segment.length;

        segment[0] = 'a';
        segment[originalLength - 1] = 'z';
        buffer.setCurrentLength(originalLength);

        char[] expanded = buffer.expandCurrentSegment();

        assertNotSame(segment, expanded);
        assertTrue(expanded.length > originalLength);
        assertEquals('a', expanded[0]);
        assertEquals('z', expanded[originalLength - 1]);
        assertEquals(originalLength, buffer.getCurrentSegmentSize());
    }

    @Test
    public void expandCurrentSegmentAtMaximumSizeAddsCapacityForAnotherCharacter() {
        TextBuffer buffer = new TextBuffer(null);
        char[] segment = buffer.getCurrentSegment();
        segment = buffer.expandCurrentSegment(262144);

        Arrays.fill(segment, 'x');
        segment[0] = 'a';
        segment[segment.length - 1] = 'z';
        buffer.setCurrentLength(segment.length);

        char[] expanded = buffer.expandCurrentSegment();

        assertTrue("A full maximum-size segment must expand for additional content",
                expanded.length > 262144);
        assertEquals('a', expanded[0]);
        assertEquals('z', expanded[262143]);
        assertEquals(262144, buffer.getCurrentSegmentSize());
    }

    @Test
    public void expandCurrentSegmentWithMinimumSizeUsesRequestedCapacityAndRetainsData() {
        TextBuffer buffer = new TextBuffer(null);
        char[] segment = buffer.getCurrentSegment();
        int requestedSize = segment.length + 137;

        segment[0] = 'q';
        buffer.setCurrentLength(1);

        char[] expanded = buffer.expandCurrentSegment(requestedSize);

        assertTrue(expanded.length >= requestedSize);
        assertEquals('q', expanded[0]);
        assertEquals(1, buffer.getCurrentSegmentSize());
    }

    @Test
    public void expandCurrentSegmentWithAlreadySatisfiedMinimumReturnsCurrentSegment() {
        TextBuffer buffer = new TextBuffer(null);
        char[] segment = buffer.getCurrentSegment();

        char[] result = buffer.expandCurrentSegment(segment.length);

        assertSame(segment, result);
    }

    @Test
    public void appendAfterFullMaximumSegmentPreservesAllContent() {
        TextBuffer buffer = new TextBuffer(null);
        char[] segment = buffer.getCurrentSegment();
        segment = buffer.expandCurrentSegment(262144);

        Arrays.fill(segment, 'm');
        segment[0] = 'a';
        segment[segment.length - 1] = 'z';
        buffer.setCurrentLength(segment.length);

        buffer.append('!');

        assertEquals(262145, buffer.size());
        char[] contents = buffer.contentsAsArray();
        assertEquals(262145, contents.length);
        assertEquals('a', contents[0]);
        assertEquals('z', contents[262143]);
        assertEquals('!', contents[262144]);
    }

    @Test
    public void finishCurrentSegmentRetainsFilledSegmentAsBufferContents() {
        TextBuffer buffer = new TextBuffer(null);
        char[] segment = buffer.getCurrentSegment();
        Arrays.fill(segment, 'p');
        segment[0] = 'f';
        segment[segment.length - 1] = 'l';
        buffer.setCurrentLength(segment.length);

        char[] next = buffer.finishCurrentSegment();

        assertEquals(0, buffer.getCurrentSegmentSize());
        assertTrue(next.length >= segment.length);
        assertEquals(segment.length, buffer.size());
        char[] contents = buffer.contentsAsArray();
        assertEquals('f', contents[0]);
        assertEquals('l', contents[contents.length - 1]);
    }

@org.junit.Test
public void appendCharacterAfterSharedInputUnsharesAndRetainsContents() {
    TextBuffer buffer = new TextBuffer(new com.fasterxml.jackson.core.util.BufferRecycler());
    char[] input = new char[] { 'x', 'b', 'c', 'd', 'y' };

    buffer.resetWithShared(input, 1, 3);
    buffer.append('!');

    org.junit.Assert.assertEquals("bcd!", buffer.contentsAsString());
    org.junit.Assert.assertEquals(4, buffer.size());
}

@org.junit.Test
public void appendCharacterArrayAfterSharedInputUsesRequestedRange() {
    TextBuffer buffer = new TextBuffer(new com.fasterxml.jackson.core.util.BufferRecycler());
    char[] input = new char[] { 'a', 'b', 'c' };
    char[] suffix = new char[] { 'x', 'd', 'e', 'y' };

    buffer.resetWithShared(input, 0, input.length);
    buffer.append(suffix, 1, 2);

    org.junit.Assert.assertEquals("abcde", buffer.contentsAsString());
    org.junit.Assert.assertEquals(5, buffer.size());
}

@org.junit.Test
public void appendStringAfterSharedInputUsesRequestedRange() {
    TextBuffer buffer = new TextBuffer(new com.fasterxml.jackson.core.util.BufferRecycler());
    char[] input = new char[] { 'x', 'a', 'b', 'c', 'y' };

    buffer.resetWithShared(input, 1, 3);
    buffer.append("01234", 1, 3);

    org.junit.Assert.assertEquals("abc123", buffer.contentsAsString());
    org.junit.Assert.assertEquals(6, buffer.size());
}

@org.junit.Test
public void appendStringAcrossCurrentSegmentBoundaryRetainsAllCharacters() {
    TextBuffer buffer = new TextBuffer(new com.fasterxml.jackson.core.util.BufferRecycler());
    char[] segment = buffer.getCurrentSegment();
    StringBuilder builder = new StringBuilder(segment.length);
    for (int i = 0; i < segment.length; ++i) {
        builder.append('z');
    }
    String suffix = builder.toString();

    buffer.append("x", 0, 1);
    buffer.append(suffix, 0, suffix.length());

    org.junit.Assert.assertEquals("x" + suffix, buffer.contentsAsString());
    org.junit.Assert.assertEquals(suffix.length() + 1, buffer.size());
}
}
