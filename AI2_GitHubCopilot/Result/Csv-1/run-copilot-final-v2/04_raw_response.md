@Test
public void testReadZeroLength() throws IOException {
    ExtendedBufferedReader br = new ExtendedBufferedReader(new StringReader("abc"));
    char[] buf = new char[5];
    int result = br.read(buf, 0, 0);
    assertEquals("read with zero length should return 0", 0, result);
    assertEquals("line number should not change", 0, br.getLineNumber());
}

@Test
public void testReadEOF() throws IOException {
    ExtendedBufferedReader br = new ExtendedBufferedReader(new StringReader(""));
    char[] buf = new char[5];
    int result = br.read(buf, 0, 5);
    assertEquals("reading empty stream returns -1", -1, result);
    assertEquals("line number remains 0", 0, br.getLineNumber());
}

@Test
public void testReadCharArrayLFNotPrecededByCR() throws IOException {
    ExtendedBufferedReader br = new ExtendedBufferedReader(new StringReader("a\n"));
    char[] buf = new char[5];
    int len = br.read(buf, 0, 5);
    assertEquals(2, len);
    assertEquals("LF not preceded by CR increments line", 1, br.getLineNumber());
}

@Test
public void testCRLFAcrossReads() throws IOException {
    ExtendedBufferedReader br = new ExtendedBufferedReader(new StringReader("z\r\nw"));
    char[] buf1 = new char[2];
    int len1 = br.read(buf1, 0, 2); // reads "z\r"
    assertEquals(2, len1);
    assertEquals("after reading z\r, line counted", 1, br.getLineNumber());
    char[] buf2 = new char[2];
    int len2 = br.read(buf2, 0, 2); // reads "\nw"
    assertEquals(2, len2);
    assertEquals("LF preceded by CR (via lastChar) should not double-count", 1, br.getLineNumber());
}