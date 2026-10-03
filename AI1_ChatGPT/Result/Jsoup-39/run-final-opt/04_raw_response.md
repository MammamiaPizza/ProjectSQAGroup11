@Test
public void detectsDeclaredMetaCharsetsWhenNoCharsetIsSupplied() throws Exception {
    String charsetMeta = "<html><head><meta charset=\"ISO-8859-1\"></head><body>caf\u00e9</body></html>";
    assertEquals("caf\u00e9", DataUtil.load(
        new java.io.ByteArrayInputStream(charsetMeta.getBytes("ISO-8859-1")), null, "").body().text());

    String httpEquivMeta = "<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\"></head><body>caf\u00e9</body></html>";
    assertEquals("caf\u00e9", DataUtil.load(
        new java.io.ByteArrayInputStream(httpEquivMeta.getBytes("ISO-8859-1")), null, "").body().text());
}

@Test
public void extractsSupportedCharsetsFromContentTypes() {
    assertEquals("UTF-8", DataUtil.getCharsetFromContentType("text/html; charset=UTF-8"));
    assertEquals("ISO-8859-1", DataUtil.getCharsetFromContentType("text/html; charset='ISO-8859-1'"));
    assertNull(DataUtil.getCharsetFromContentType("text/html"));
    assertNull(DataUtil.getCharsetFromContentType("text/html; charset="));
    assertNull(DataUtil.getCharsetFromContentType("text/html; charset=not/a-charset"));
}

@Test
public void loadsHtmlFromFile() throws Exception {
    java.io.File file = java.io.File.createTempFile("jsoup", ".html");
    try {
        java.io.FileOutputStream output = new java.io.FileOutputStream(file);
        try {
            output.write("<html><body>from file</body></html>".getBytes("UTF-8"));
        } finally {
            output.close();
        }

        assertEquals("from file", DataUtil.load(file, null, "http://example.com/").body().text());
    } finally {
        file.delete();
    }
}

@Test
public void readToByteBufferHonorsMaximumSize() throws Exception {
    java.nio.ByteBuffer byteData = DataUtil.readToByteBuffer(
        new java.io.ByteArrayInputStream("abcdef".getBytes("UTF-8")), 3);

    assertEquals(3, byteData.remaining());
    assertEquals((int) 'a', (int) byteData.get());
    assertEquals((int) 'b', (int) byteData.get());
    assertEquals((int) 'c', (int) byteData.get());
}