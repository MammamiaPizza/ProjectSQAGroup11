@Test
public void base64ObjectDecodeAcceptsByteArraysAndRejectsOtherObjects() throws Exception {
    assertArrayEquals(new byte[] { 'M' },
            (byte[]) new Base64().decode((Object) new byte[] { 'T', 'Q', '=', '=' }));

    try {
        new Base64().decode((Object) "TQ==");
        org.junit.Assert.fail("Expected DecoderException for a non-byte-array input");
    } catch (org.apache.commons.codec.DecoderException expected) {
        // expected
    }
}

@Test
public void decodeBase64HandlesCompleteAndPartialQuantaAfterDiscardingNonBase64Bytes() {
    assertArrayEquals(new byte[] { 'M', 'a', 'n' },
            Base64.decodeBase64(new byte[] { 'T', 'W', 'F', 'u', '!', '\n' }));
    assertArrayEquals(new byte[] { 'M', 'a' },
            Base64.decodeBase64(new byte[] { 'T', 'W', 'E', '=' }));
    assertArrayEquals(new byte[] { 'M' },
            Base64.decodeBase64(new byte[] { 'T', 'Q', '=', '=' }));
}

@Test
public void urlSafeEncodingUsesUrlAlphabetWithoutPaddingAndDecodesBack() {
    byte[] source = new byte[] { (byte) 0xfb, (byte) 0xff };

    assertArrayEquals(new byte[] { '-', '_', '8' }, Base64.encodeBase64URLSafe(source));
    assertArrayEquals(source, Base64.decodeBase64(Base64.encodeBase64URLSafe(source)));
}

@Test
public void integerEncodingRoundTripsPositiveValuesWithHighBitSet() {
    java.math.BigInteger value = new java.math.BigInteger("123456789abcdef", 16);

    assertEquals(value, Base64.decodeInteger(Base64.encodeInteger(value)));
}