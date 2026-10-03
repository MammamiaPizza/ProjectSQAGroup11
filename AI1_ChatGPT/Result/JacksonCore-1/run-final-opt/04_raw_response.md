@Test
public void recognizesLongRangeBoundariesForPositiveAndNegativeValues() {
    assertTrue(NumberInput.inLongRange("9223372036854775807", false));
    assertFalse(NumberInput.inLongRange("9223372036854775808", false));
    assertTrue(NumberInput.inLongRange("9223372036854775808", true));
    assertFalse(NumberInput.inLongRange("9223372036854775809", true));
    assertTrue(NumberInput.inLongRange("123", false));
    assertFalse(NumberInput.inLongRange("12345678901234567890", false));
}

@Test
public void textBufferUnsharesBeforeAppendingToSharedContents() {
    TextBuffer buffer = new TextBuffer(null);
    buffer.resetWithShared("xx123yy".toCharArray(), 2, 3);

    buffer.ensureNotShared();
    buffer.append('4');

    assertEquals(4, buffer.size());
    assertEquals("1234", buffer.contentsAsString());
    assertEquals("1234", new String(buffer.contentsAsArray()));
}

@Test
public void textBufferBuildsContentsAcrossExpandedSegments() {
    char[] characters = new char[2000];
    java.util.Arrays.fill(characters, 'x');

    TextBuffer buffer = new TextBuffer(null);
    buffer.resetWithEmpty();
    buffer.append(characters, 0, characters.length);
    buffer.append('!');

    assertEquals(2001, buffer.size());
    assertEquals(new String(characters) + "!", buffer.contentsAsString());
    assertEquals(new String(characters) + "!", new String(buffer.contentsAsArray()));
}