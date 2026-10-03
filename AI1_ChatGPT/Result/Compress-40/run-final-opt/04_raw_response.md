@org.junit.Test
public void bigEndianReadsRemainingCachedBitsWithoutRefilling() throws java.io.IOException {
    final org.apache.commons.compress.utils.BitInputStream input =
            new org.apache.commons.compress.utils.BitInputStream(
                    new java.io.ByteArrayInputStream(new byte[] { (byte) 0xB2 }),
                    java.nio.ByteOrder.BIG_ENDIAN);

    org.junit.Assert.assertEquals(5L, input.readBits(3));
    org.junit.Assert.assertEquals(2L, input.readBits(2));
}

@org.junit.Test
public void closeClosesUnderlyingStream() throws java.io.IOException {
    final boolean[] closed = new boolean[] { false };
    final org.apache.commons.compress.utils.BitInputStream input =
            new org.apache.commons.compress.utils.BitInputStream(new java.io.InputStream() {
                @Override
                public int read() {
                    return -1;
                }

                @Override
                public void close() {
                    closed[0] = true;
                }
            }, java.nio.ByteOrder.LITTLE_ENDIAN);

    input.close();

    org.junit.Assert.assertTrue(closed[0]);
}