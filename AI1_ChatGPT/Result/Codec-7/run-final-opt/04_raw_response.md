@org.junit.Test
public void nullLineSeparatorDisablesChunking() {
    final Base64 base64 = new Base64(4, null);

    org.junit.Assert.assertEquals("TWFuTWFu",
            base64.encodeToString(new byte[] { 'M', 'a', 'n', 'M', 'a', 'n' }));
}

@org.junit.Test(expected = IllegalArgumentException.class)
public void constructorRejectsBase64CharactersInLineSeparator() {
    org.junit.Assert.assertTrue(Base64.isArrayByteBase64(new byte[] { 'A' }));
    org.junit.Assert.assertFalse(Base64.isArrayByteBase64(new byte[] { '!' }));

    new Base64(4, new byte[] { 'A' });
}

@org.junit.Test
public void decodeObjectAcceptsByteArraysAndStrings() throws Exception {
    final Base64 base64 = new Base64();
    final byte[] expected = new byte[] { 'f' };

    org.junit.Assert.assertArrayEquals(expected,
            (byte[]) base64.decode((Object) new byte[] { 'Z', 'g', '=', '=' }));
    org.junit.Assert.assertArrayEquals(expected,
            (byte[]) base64.decode((Object) "Zg=="));
}

@org.junit.Test(expected = org.apache.commons.codec.DecoderException.class)
public void decodeObjectRejectsUnsupportedTypes() throws Exception {
    new Base64().decode((Object) new Object());
}