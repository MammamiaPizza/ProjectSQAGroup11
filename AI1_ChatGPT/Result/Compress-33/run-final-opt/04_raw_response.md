@Test(expected = IllegalArgumentException.class)
public void factoryRejectsNullInputStream() throws Exception {
    new CompressorStreamFactory().createCompressorInputStream(null);
}

@Test
public void factoryDetectsDeflateStreamWithBestSpeedZlibHeader() throws Exception {
    final byte[] original = "deflate header variation".getBytes("UTF-8");
    final java.io.ByteArrayOutputStream compressed = new java.io.ByteArrayOutputStream();
    final java.util.zip.DeflaterOutputStream output =
            new java.util.zip.DeflaterOutputStream(
                    compressed, new java.util.zip.Deflater(java.util.zip.Deflater.BEST_SPEED));
    output.write(original);
    output.close();

    final java.io.InputStream stream = new CompressorStreamFactory()
            .createCompressorInputStream(new java.io.ByteArrayInputStream(compressed.toByteArray()));

    assertArrayEquals(original, readAll(stream));
}

@Test
public void factoryConfiguredToDecompressUntilEofReadsConcatenatedGzipMembers() throws Exception {
    final byte[] first = gzipMember("first ".getBytes("UTF-8"));
    final byte[] second = gzipMember("second".getBytes("UTF-8"));
    final byte[] concatenated = new byte[first.length + second.length];
    System.arraycopy(first, 0, concatenated, 0, first.length);
    System.arraycopy(second, 0, concatenated, first.length, second.length);

    final java.io.InputStream stream = new CompressorStreamFactory(true)
            .createCompressorInputStream(new java.io.ByteArrayInputStream(concatenated));

    assertArrayEquals("first second".getBytes("UTF-8"), readAll(stream));
}

private byte[] gzipMember(final byte[] content) throws java.io.IOException {
    final java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
    final java.util.zip.GZIPOutputStream gzip = new java.util.zip.GZIPOutputStream(bytes);
    gzip.write(content);
    gzip.close();
    return bytes.toByteArray();
}