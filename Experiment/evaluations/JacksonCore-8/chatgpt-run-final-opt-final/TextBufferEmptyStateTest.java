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

@org.junit.Test
public void appendCharExpandsWhenCurrentSegmentIsFull() {
    TextBuffer buffer = new TextBuffer(null);
    char[] segment = buffer.emptyAndGetCurrentSegment();
    char[] contents = new char[segment.length];
    java.util.Arrays.fill(contents, 'a');

    buffer.append(contents, 0, contents.length);
    buffer.append('b');

    org.junit.Assert.assertEquals(contents.length + 1, buffer.size());
    org.junit.Assert.assertEquals(new String(contents) + "b", buffer.contentsAsString());
}

@org.junit.Test
public void appendStringAfterSharedInputUnsharesWithoutChangingInput() {
    TextBuffer buffer = new TextBuffer(null);
    char[] shared = new char[] { 'a', 'b', 'c' };

    buffer.resetWithShared(shared, 1, 1);
    buffer.append("YZ", 0, 2);

    org.junit.Assert.assertEquals("bYZ", buffer.contentsAsString());
    org.junit.Assert.assertArrayEquals(new char[] { 'a', 'b', 'c' }, shared);
}

@org.junit.Test
public void appendStringSpanningCurrentSegmentPreservesAllCharacters() {
    TextBuffer buffer = new TextBuffer(null);
    char[] segment = buffer.emptyAndGetCurrentSegment();
    char[] prefixChars = new char[Math.max(0, segment.length - 1)];
    char[] suffixChars = new char[segment.length + 2];
    java.util.Arrays.fill(prefixChars, 'p');
    java.util.Arrays.fill(suffixChars, 's');
    String prefix = new String(prefixChars);
    String suffix = new String(suffixChars);

    buffer.append(prefix, 0, prefix.length());
    buffer.append(suffix, 0, suffix.length());

    org.junit.Assert.assertEquals(prefix.length() + suffix.length(), buffer.size());
    org.junit.Assert.assertEquals(prefix + suffix, buffer.contentsAsString());
}

@org.junit.Test
public void appendCharactersAfterSharedInputUnsharesWithoutChangingInput() {
    TextBuffer buffer = new TextBuffer(null);
    char[] shared = new char[] { '0', '1', '2', '3' };
    char[] additional = new char[] { 'x', 'y', 'z' };

    buffer.resetWithShared(shared, 1, 2);
    buffer.append(additional, 1, 2);

    org.junit.Assert.assertEquals("12yz", buffer.contentsAsString());
    org.junit.Assert.assertArrayEquals(new char[] { '0', '1', '2', '3' }, shared);
}
}
