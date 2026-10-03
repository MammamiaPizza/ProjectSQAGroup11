@Test
public void testAppendCharExpansion() {
    TextBuffer tb = new TextBuffer(new com.fasterxml.jackson.core.util.BufferRecycler());
    tb.resetWithEmpty();
    int count = 2001;
    char c = 'a';
    for (int i = 0; i < count; i++) {
        tb.append(c);
    }
    assertEquals(count, tb.size());
    char[] expected = new char[count];
    java.util.Arrays.fill(expected, 'a');
    assertEquals(new String(expected), tb.contentsAsString());
}

@Test
public void testAppendStringExpansion() {
    TextBuffer tb = new TextBuffer(new com.fasterxml.jackson.core.util.BufferRecycler());
    tb.resetWithEmpty();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 2000; i++) {
        sb.append('x');
    }
    String longStr = sb.toString();
    tb.append(longStr, 0, longStr.length());
    assertEquals(2000, tb.size());
    assertEquals(longStr, tb.contentsAsString());
}

@Test
public void testAppendStringWithSharedBuffer() {
    char[] shared = "hello".toCharArray();
    TextBuffer tb = new TextBuffer(new com.fasterxml.jackson.core.util.BufferRecycler());
    tb.resetWithShared(shared, 0, 5);
    tb.append(" world", 0, 6);
    assertEquals("hello world", tb.contentsAsString());
}

@Test
public void testAppendCharArrayExpansion() {
    TextBuffer tb = new TextBuffer(new com.fasterxml.jackson.core.util.BufferRecycler());
    tb.resetWithEmpty();
    char[] bigArr = new char[2000];
    java.util.Arrays.fill(bigArr, 'y');
    tb.append(bigArr, 0, bigArr.length);
    assertEquals(2000, tb.size());
    assertEquals(new String(bigArr), tb.contentsAsString());
}