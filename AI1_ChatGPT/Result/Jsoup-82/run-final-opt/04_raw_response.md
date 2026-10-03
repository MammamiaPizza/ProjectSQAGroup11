@org.junit.Test
public void crossStreamsCopiesAllBytes() throws java.io.IOException {
    byte[] source = "copy this content".getBytes("UTF-8");
    java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();

    DataUtil.crossStreams(new java.io.ByteArrayInputStream(source), output);

    org.junit.Assert.assertArrayEquals(source, output.toByteArray());
}

@org.junit.Test
public void loadDetectsUtf8AndUtf16ByteOrderMarks() throws java.io.IOException {
    byte[] utf8Html = "<p>€</p>".getBytes("UTF-8");
    byte[] utf8WithBom = new byte[utf8Html.length + 3];
    utf8WithBom[0] = (byte) 0xEF;
    utf8WithBom[1] = (byte) 0xBB;
    utf8WithBom[2] = (byte) 0xBF;
    System.arraycopy(utf8Html, 0, utf8WithBom, 3, utf8Html.length);

    org.jsoup.nodes.Document utf8Document = DataUtil.load(
        new java.io.ByteArrayInputStream(utf8WithBom), null, "");
    org.junit.Assert.assertEquals("€", utf8Document.select("p").text());
    org.junit.Assert.assertEquals("UTF-8", utf8Document.outputSettings().charset().name());

    byte[] utf16Html = "<p>é</p>".getBytes("UTF-16");
    org.jsoup.nodes.Document utf16Document = DataUtil.load(
        new java.io.ByteArrayInputStream(utf16Html), null, "");
    org.junit.Assert.assertEquals("é", utf16Document.select("p").text());
    org.junit.Assert.assertEquals("UTF-16", utf16Document.outputSettings().charset().name());
}

@org.junit.Test
public void readToByteBufferHonorsMaximumSize() throws java.io.IOException {
    java.nio.ByteBuffer buffer = DataUtil.readToByteBuffer(
        new java.io.ByteArrayInputStream("abcdef".getBytes("UTF-8")), 4);

    org.junit.Assert.assertEquals("abcd", new String(buffer.array(), "UTF-8"));
}

@org.junit.Test
public void loadFileParsesContentUsingProvidedCharset() throws java.io.IOException {
    java.io.File file = java.io.File.createTempFile("jsoup-datautil-", ".html");
    try {
        java.io.FileOutputStream output = new java.io.FileOutputStream(file);
        try {
            output.write("<p>file content</p>".getBytes("UTF-8"));
        } finally {
            output.close();
        }

        org.jsoup.nodes.Document document = DataUtil.load(file, "UTF-8", "http://example.com/");
        org.junit.Assert.assertEquals("file content", document.select("p").text());
    } finally {
        file.delete();
    }
}