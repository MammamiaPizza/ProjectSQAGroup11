@org.junit.Test
public void byteArrayDecodeSupportsPaddedAndUnpaddedData() {
    final Base64 codec = new Base64();

    org.junit.Assert.assertArrayEquals(new byte[] { 'M' },
            codec.decode(new byte[] { 'T', 'Q', '=', '=' }));
    org.junit.Assert.assertArrayEquals(new byte[] { 'M', 'a' },
            codec.decode(new byte[] { 'T', 'W', 'E' }));
}

@org.junit.Test
public void objectDecodeAcceptsStringsAndRejectsUnsupportedTypes() throws Exception {
    final Base64 codec = new Base64();

    org.junit.Assert.assertArrayEquals(new byte[] { 'M', 'a' },
            (byte[]) codec.decode((Object) "TWE="));

    try {
        codec.decode((Object) new Object());
        org.junit.Assert.fail("Expected DecoderException");
    } catch (org.apache.commons.codec.DecoderException expected) {
        // expected
    }
}

@org.junit.Test
public void nullLineSeparatorDisablesChunking() {
    final Base64 codec = new Base64(4, null);

    org.junit.Assert.assertArrayEquals(
            new byte[] { 'T', 'W', 'F', 'u', 'T', 'W', 'F', 'u' },
            codec.encode(new byte[] { 'M', 'a', 'n', 'M', 'a', 'n' }));
}

@org.junit.Test
public void urlSafeCodecUsesUrlSafeAlphabetWithoutPadding() {
    final Base64 codec = new Base64(true);
    final byte[] data = new byte[] { (byte) 0xfb, (byte) 0xff, (byte) 0xff };

    org.junit.Assert.assertTrue(codec.isUrlSafe());
    org.junit.Assert.assertArrayEquals(new byte[] { '-', '_', '_', '_' }, codec.encode(data));
    org.junit.Assert.assertArrayEquals(data, codec.decode(new byte[] { '-', '_', '_', '_' }));
}