@Test
public void testDefaultConstructorDecode() throws Exception {
    // "dGVzdA==" is base64 for "test"
    byte[] encoded = "dGVzdA==".getBytes("US-ASCII");
    java.io.InputStream in = new java.io.ByteArrayInputStream(encoded);
    Base64InputStream stream = new Base64InputStream(in);
    byte[] buf = new byte[100];
    int len = stream.read(buf);
    String result = new String(buf, 0, len, "US-ASCII");
    assertEquals("test", result);
}

@Test
public void testReadHighByteConversion() throws Exception {
    // "gA==" is base64 for byte 0x80 (128)
    byte[] encoded = "gA==".getBytes("US-ASCII");
    java.io.InputStream in = new java.io.ByteArrayInputStream(encoded);
    Base64InputStream stream = new Base64InputStream(in, false);
    int c = stream.read();
    assertTrue("Expected a non-EOF high byte", c >= 0);
    assertEquals(128, c);
    assertEquals(-1, stream.read());
}

@Test
public void testReadAfterEOFByteArray() throws Exception {
    // "SGVsbG8=" is base64 for "Hello"
    byte[] encoded = "SGVsbG8=".getBytes("US-ASCII");
    java.io.InputStream in = new java.io.ByteArrayInputStream(encoded);
    Base64InputStream stream = new Base64InputStream(in, false);
    byte[] buf = new byte[100];
    int total = stream.read(buf);
    assertEquals(5, total);
    // read past EOF
    int after = stream.read(buf);
    assertEquals(-1, after);
}

@Test
public void testReadNonEqualBufferLen() throws Exception {
    // "SGVsbG8=" decodes to "Hello" (5 bytes)
    byte[] encoded = "SGVsbG8=".getBytes("US-ASCII");
    java.io.InputStream in = new java.io.ByteArrayInputStream(encoded);
    Base64InputStream stream = new Base64InputStream(in, false);
    byte[] buf = new byte[10];   // length 10
    int len1 = stream.read(buf, 0, 3);  // len=3 < buf.length
    assertEquals(3, len1);
    assertEquals('H', buf[0]);
    assertEquals('e', buf[1]);
    assertEquals('l', buf[2]);
    int len2 = stream.read(buf, 0, 10);
    assertEquals(2, len2);
    assertEquals('l', buf[0]);
    assertEquals('o', buf[1]);
    assertEquals(-1, stream.read());
}