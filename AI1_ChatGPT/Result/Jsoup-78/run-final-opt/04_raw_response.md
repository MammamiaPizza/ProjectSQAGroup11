@org.junit.Test
public void loadsStreamThatBecomesEmptyBeforeParserRead() throws java.io.IOException {
    java.io.InputStream input = new java.io.InputStream() {
        private boolean firstRead = true;

        @Override
        public int read(byte[] buffer, int offset, int length) {
            if (length == 0)
                return 0;
            if (firstRead) {
                firstRead = false;
                return -1;
            }
            return 0;
        }

        @Override
        public int read() {
            return -1;
        }
    };

    org.jsoup.nodes.Document document = DataUtil.load(input, null, "http://example.com/empty");
    org.junit.Assert.assertNotNull(document);
    org.junit.Assert.assertEquals("http://example.com/empty", document.location());
}

@org.junit.Test
public void loadsUtf8BomAndSkipsItBeforeParsing() throws java.io.IOException {
    byte[] html = "\uFEFF<html><head><title>Bom Title</title></head><body>Body</body></html>"
        .getBytes(java.nio.charset.StandardCharsets.UTF_8);

    org.jsoup.nodes.Document document = DataUtil.load(
        new java.io.ByteArrayInputStream(html), null, "http://example.com/bom");

    org.junit.Assert.assertEquals("Bom Title", document.title());
    org.junit.Assert.assertEquals("Body", document.body().text());
}

@org.junit.Test
public void extractsOnlySupportedCharsetsFromContentTypes() {
    org.junit.Assert.assertEquals("UTF-8",
        DataUtil.getCharsetFromContentType("text/html; charset= utf-8"));
    org.junit.Assert.assertNull(
        DataUtil.getCharsetFromContentType("text/html; charset=not-a-real-charset"));
    org.junit.Assert.assertNull(DataUtil.getCharsetFromContentType("text/html"));
}

@org.junit.Test
public void crossStreamsCopiesAllInputBytes() throws java.io.IOException {
    java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();

    DataUtil.crossStreams(
        new java.io.ByteArrayInputStream("stream data".getBytes(java.nio.charset.StandardCharsets.UTF_8)),
        output);

    org.junit.Assert.assertArrayEquals(
        "stream data".getBytes(java.nio.charset.StandardCharsets.UTF_8),
        output.toByteArray());
}