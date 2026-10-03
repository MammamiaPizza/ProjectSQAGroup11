package com.fasterxml.jackson.core.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class TextBufferEmptyStateTest
{
    @Test
    public void resetWithEmptyProvidesUsableEmptyAccessors()
    {
        TextBuffer buffer = new TextBuffer(null);

        buffer.resetWithEmpty();

        assertEquals(0, buffer.size());
        assertEquals("", buffer.contentsAsString());
        assertArrayEquals(new char[0], buffer.contentsAsArray());
        assertNotNull(buffer.getTextBuffer());
    }

    @Test
    public void resetWithEmptyAfterStringClearsAndKeepsBufferAccessible()
    {
        TextBuffer buffer = new TextBuffer(null);
        buffer.resetWithString("non-empty");

        buffer.resetWithEmpty();

        assertEquals(0, buffer.size());
        assertEquals("", buffer.contentsAsString());
        assertArrayEquals(new char[0], buffer.contentsAsArray());
        assertNotNull(buffer.getTextBuffer());
    }

    @Test
    public void resetWithEmptyAfterSharedInputClearsAndKeepsBufferAccessible()
    {
        TextBuffer buffer = new TextBuffer(null);
        buffer.resetWithShared(new char[] { 'x', 'a', 'b', 'c', 'y' }, 1, 3);

        buffer.resetWithEmpty();

        assertEquals(0, buffer.size());
        assertEquals("", buffer.contentsAsString());
        assertArrayEquals(new char[0], buffer.contentsAsArray());
        assertNotNull(buffer.getTextBuffer());
    }

    @Test
    public void resetWithEmptyAfterCopiedInputClearsAndKeepsBufferAccessible()
    {
        TextBuffer buffer = new TextBuffer(null);
        buffer.resetWithCopy(new char[] { 'a', 'b', 'c' }, 0, 3);

        buffer.resetWithEmpty();

        assertEquals(0, buffer.size());
        assertEquals("", buffer.contentsAsString());
        assertArrayEquals(new char[0], buffer.contentsAsArray());
        assertNotNull(buffer.getTextBuffer());
    }

    @Test
    public void resetWithEmptyAfterReleaseBuffersKeepsBufferAccessible()
    {
        TextBuffer buffer = new TextBuffer(null);
        buffer.append('x');
        buffer.releaseBuffers();

        buffer.resetWithEmpty();

        assertEquals(0, buffer.size());
        assertEquals("", buffer.contentsAsString());
        assertArrayEquals(new char[0], buffer.contentsAsArray());
        assertNotNull(buffer.getTextBuffer());
    }

    @Test
    public void appendAfterEmptyResetBuildsUsableText()
    {
        TextBuffer buffer = new TextBuffer(null);
        buffer.resetWithEmpty();
        buffer.getCurrentSegment();

        buffer.append('a');
        buffer.append(new char[] { 'b', 'c' }, 0, 2);

        assertEquals(3, buffer.size());
        assertEquals("abc", buffer.contentsAsString());
        assertArrayEquals(new char[] { 'a', 'b', 'c' }, buffer.contentsAsArray());
        assertNotNull(buffer.getTextBuffer());
    }
}