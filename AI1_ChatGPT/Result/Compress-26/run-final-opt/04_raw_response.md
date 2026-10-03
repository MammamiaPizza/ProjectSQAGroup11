@org.junit.Test
public void copyCopiesAllBytesUsingBothOverloads() throws java.io.IOException {
    final byte[] source = new byte[] { 1, 2, 3, 4, 5 };

    final java.io.ByteArrayOutputStream defaultOutput = new java.io.ByteArrayOutputStream();
    org.junit.Assert.assertEquals(5L,
            org.apache.commons.compress.utils.IOUtils.copy(
                    new java.io.ByteArrayInputStream(source), defaultOutput));
    org.junit.Assert.assertEquals(5, defaultOutput.toByteArray().length);
    org.junit.Assert.assertEquals(1, defaultOutput.toByteArray()[0]);
    org.junit.Assert.assertEquals(5, defaultOutput.toByteArray()[4]);

    final java.io.ByteArrayOutputStream smallBufferOutput = new java.io.ByteArrayOutputStream();
    org.junit.Assert.assertEquals(5L,
            org.apache.commons.compress.utils.IOUtils.copy(
                    new java.io.ByteArrayInputStream(source), smallBufferOutput, 2));
    org.junit.Assert.assertEquals(5, smallBufferOutput.toByteArray().length);
    org.junit.Assert.assertEquals(3, smallBufferOutput.toByteArray()[2]);
}

@org.junit.Test
public void readFullyReturnsBytesReadForCompleteAndTruncatedInput() throws java.io.IOException {
    final byte[] complete = new byte[2];
    org.junit.Assert.assertEquals(2,
            org.apache.commons.compress.utils.IOUtils.readFully(
                    new java.io.ByteArrayInputStream(new byte[] { 3, 4 }), complete));
    org.junit.Assert.assertEquals(3, complete[0]);
    org.junit.Assert.assertEquals(4, complete[1]);

    final byte[] partial = new byte[] { 9, 9, 9, 9, 9 };
    org.junit.Assert.assertEquals(2,
            org.apache.commons.compress.utils.IOUtils.readFully(
                    new java.io.ByteArrayInputStream(new byte[] { 1, 2 }), partial, 1, 3));
    org.junit.Assert.assertEquals(9, partial[0]);
    org.junit.Assert.assertEquals(1, partial[1]);
    org.junit.Assert.assertEquals(2, partial[2]);
    org.junit.Assert.assertEquals(9, partial[3]);
}

@org.junit.Test
public void toByteArrayReturnsAllInputBytes() throws java.io.IOException {
    final byte[] result = org.apache.commons.compress.utils.IOUtils.toByteArray(
            new java.io.ByteArrayInputStream(new byte[] { 7, 8, 9 }));

    org.junit.Assert.assertEquals(3, result.length);
    org.junit.Assert.assertEquals(7, result[0]);
    org.junit.Assert.assertEquals(8, result[1]);
    org.junit.Assert.assertEquals(9, result[2]);
}

@org.junit.Test
public void closeQuietlyClosesAndSuppressesIOException() {
    final boolean[] closed = new boolean[] { false };

    org.apache.commons.compress.utils.IOUtils.closeQuietly(new java.io.Closeable() {
        public void close() throws java.io.IOException {
            closed[0] = true;
            throw new java.io.IOException("expected");
        }
    });
    org.junit.Assert.assertTrue(closed[0]);

    org.apache.commons.compress.utils.IOUtils.closeQuietly(null);
}