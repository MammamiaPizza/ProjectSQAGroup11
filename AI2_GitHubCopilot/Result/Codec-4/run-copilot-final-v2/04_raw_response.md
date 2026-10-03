@Test
public void testConstructorWithNullLineSeparatorDisablesChunking() {
    Base64 b64 = new Base64(76, null);
    byte[] data = new byte[]{0, 1, 2};
    byte[] encoded = b64.encode(data);
    byte[] decoded = b64.decode(encoded);
    assertArrayEquals(data, decoded);
    assertEquals(4, encoded.length);
}

@Test(expected = IllegalArgumentException.class)
public void testConstructorWithLineSeparatorContainingBase64CharacterThrowsException() {
    new Base64(76, new byte[]{'A'});
}

@Test(expected = org.apache.commons.codec.EncoderException.class)
public void testEncodeObjectWithNonByteArrayThrowsEncoderException() throws Exception {
    Base64 b64 = new Base64();
    b64.encode("Not a byte array");
}

@Test(expected = org.apache.commons.codec.DecoderException.class)
public void testDecodeObjectWithNonByteArrayOrStringThrowsDecoderException() throws Exception {
    Base64 b64 = new Base64();
    b64.decode(Integer.valueOf(42));
}