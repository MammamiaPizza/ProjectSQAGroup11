@Test
    public void crossStreams_nonEmptyStream_transfersData() throws java.io.IOException {
        byte[] data = "hello world".getBytes("UTF-8");
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(data);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        DataUtil.crossStreams(in, out);
        org.junit.Assert.assertArrayEquals(data, out.toByteArray());
    }

 @Test
 public void getCharsetFromContentType_validCharset_returnsCharset() {
     String result = DataUtil.getCharsetFromContentType("text/html; charset=UTF-8");
     org.junit.Assert.assertEquals("UTF-8", result);
 }

 @Test
 public void getCharsetFromContentType_unsupportedCharset_returnsNull() {
     String result = DataUtil.getCharsetFromContentType("text/html; charset=unsupported");
     org.junit.Assert.assertNull(result);
 }

 @Test
 public void load_utf8BomStream_usesUtf8Charset() throws java.io.IOException {
     byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
     byte[] html = "<html><head></head><body>test</body></html>".getBytes("UTF-8");
     byte[] full = new byte[bom.length + html.length];
     System.arraycopy(bom, 0, full, 0, bom.length);
     System.arraycopy(html, 0, full, bom.length, html.length);
     java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(full);
     org.jsoup.nodes.Document doc = DataUtil.load(in, null, "http://example.com");
     org.junit.Assert.assertEquals("UTF-8", doc.charset().name());
 }