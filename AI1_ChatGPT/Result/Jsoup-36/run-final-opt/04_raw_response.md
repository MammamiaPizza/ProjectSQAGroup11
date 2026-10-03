@Test
public void loadsHtmlFromInputStreamWithProvidedCharset() throws java.io.IOException {
    String html = "<html><head><title>Stream title</title></head><body>café</body></html>";
    org.jsoup.nodes.Document document = DataUtil.load(
        new java.io.ByteArrayInputStream(html.getBytes("UTF-8")),
        "UTF-8",
        "http://example.com/");

    org.junit.Assert.assertEquals("Stream title", document.title());
    org.junit.Assert.assertEquals("café", document.body().text());
}

@Test
public void loadsHtmlFromFileAndClosesItsInputStream() throws java.io.IOException {
    java.io.File file = java.io.File.createTempFile("jsoup-datautil-", ".html");
    try {
        java.io.FileOutputStream output = new java.io.FileOutputStream(file);
        try {
            output.write("<html><head><title>File title</title></head><body>File body</body></html>".getBytes("UTF-8"));
        } finally {
            output.close();
        }

        org.jsoup.nodes.Document document = DataUtil.load(file, "UTF-8", "http://example.com/");
        org.junit.Assert.assertEquals("File title", document.title());
        org.junit.Assert.assertEquals("File body", document.body().text());
    } finally {
        file.delete();
    }
}

@Test
public void readsStreamsRespectingAndIgnoringMaximumSize() throws java.io.IOException {
    byte[] bytes = "abcde".getBytes("UTF-8");

    java.nio.ByteBuffer capped = DataUtil.readToByteBuffer(new java.io.ByteArrayInputStream(bytes), 3);
    org.junit.Assert.assertEquals(3, capped.remaining());
    org.junit.Assert.assertEquals("abc", new java.lang.String(capped.array(), "UTF-8"));

    java.nio.ByteBuffer unlimited = DataUtil.readToByteBuffer(new java.io.ByteArrayInputStream(bytes), 0);
    org.junit.Assert.assertEquals(5, unlimited.remaining());
    org.junit.Assert.assertEquals("abcde", new java.lang.String(unlimited.array(), "UTF-8"));
}