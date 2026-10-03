@Test
public void encodesDataAcrossSmallBulkReads() throws java.io.IOException {
    org.apache.commons.codec.binary.Base64InputStream stream =
            new org.apache.commons.codec.binary.Base64InputStream(
                    new java.io.ByteArrayInputStream(new byte[] { 'a', 'b', 'c' }), true);

    byte[] first = new byte[2];
    byte[] second = new byte[2];

    org.junit.Assert.assertEquals(2, stream.read(first));
    org.junit.Assert.assertArrayEquals(new byte[] { 'Y', 'W' }, first);
    org.junit.Assert.assertEquals(2, stream.read(second));
    org.junit.Assert.assertArrayEquals(new byte[] { 'J', 'j' }, second);
    org.junit.Assert.assertEquals(-1, stream.read(new byte[1]));
}

@Test
public void defaultInputStreamConstructorDecodesAndDoesNotSupportMarking() throws java.io.IOException {
    org.apache.commons.codec.binary.Base64InputStream stream =
            new org.apache.commons.codec.binary.Base64InputStream(
                    new java.io.ByteArrayInputStream(new byte[] { 'Y', 'Q', '=', '=' }));

    org.junit.Assert.assertFalse(stream.markSupported());
    org.junit.Assert.assertEquals((int) 'a', stream.read());
    org.junit.Assert.assertEquals(-1, stream.read());
}

@Test
public void nullLineSeparatorDisablesChunking() {
    org.apache.commons.codec.binary.Base64 codec =
            new org.apache.commons.codec.binary.Base64(4, null);

    org.junit.Assert.assertArrayEquals(
            new byte[] { 'Y', 'W', 'J', 'j', 'Z', 'G', 'V', 'm' },
            codec.encode(new byte[] { 'a', 'b', 'c', 'd', 'e', 'f' }));
}

@Test(expected = IllegalArgumentException.class)
public void rejectsLineSeparatorContainingBase64Characters() {
    new org.apache.commons.codec.binary.Base64(4, new byte[] { 'A' });
}