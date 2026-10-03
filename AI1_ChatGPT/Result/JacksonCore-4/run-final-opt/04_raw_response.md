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