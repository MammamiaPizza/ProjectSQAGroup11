@org.junit.Test
public void crossStreamsCopiesAllBytes() throws java.io.IOException {
    byte[] source = new byte[] {0, 1, 2, 3, 4};
    java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();

    DataUtil.crossStreams(new java.io.ByteArrayInputStream(source), output);

    org.junit.Assert.assertArrayEquals(source, output.toByteArray());
}

@org.junit.Test
public void getsCharsetFromContentTypeAndRejectsUnsupportedCharsets() {
    org.junit.Assert.assertEquals("ISO-8859-1",
        DataUtil.getCharsetFromContentType("text/html; charset=\"ISO-8859-1\""));
    org.junit.Assert.assertNull(DataUtil.getCharsetFromContentType("text/html; charset=not-a-real-charset"));
    org.junit.Assert.assertNull(DataUtil.getCharsetFromContentType("text/html"));
}

@org.junit.Test
public void loadsEmptyDocumentWhenInputStreamIsNull() throws java.io.IOException {
    org.jsoup.nodes.Document document = DataUtil.load((java.io.InputStream) null, null, "http://example.com/");

    org.junit.Assert.assertNotNull(document);
}

@org.junit.Test
public void readsInputStreamIntoByteBuffer() throws java.io.IOException {
    byte[] source = new byte[] {10, 20, 30};
    java.nio.ByteBuffer buffer = DataUtil.readToByteBuffer(new java.io.ByteArrayInputStream(source), 32);
    byte[] actual = new byte[buffer.remaining()];
    buffer.get(actual);

    org.junit.Assert.assertArrayEquals(source, actual);
}