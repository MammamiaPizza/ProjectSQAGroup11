@org.junit.Test
public void objectDecodeDelegatesForStringAndByteArrayInputs() throws Exception {
    org.apache.commons.codec.binary.Base64 codec = new org.apache.commons.codec.binary.Base64();

    org.junit.Assert.assertArrayEquals(new byte[] { 'a' }, (byte[]) codec.decode((Object) "YQ=="));
    org.junit.Assert.assertArrayEquals(new byte[] { 'a' },
            (byte[]) codec.decode((Object) new byte[] { 'Y', 'Q', '=', '=' }));
}

@org.junit.Test
public void lineLengthConstructorUsesDefaultChunkSeparator() {
    org.apache.commons.codec.binary.Base64 codec = new org.apache.commons.codec.binary.Base64(4);

    org.junit.Assert.assertArrayEquals(
            new byte[] { 'Y', 'W', 'J', 'j', '\r', '\n' },
            codec.encode(new byte[] { 'a', 'b', 'c' }));
}

@org.junit.Test
public void nullLineSeparatorDisablesChunking() {
    org.apache.commons.codec.binary.Base64 codec = new org.apache.commons.codec.binary.Base64(4, null);

    org.junit.Assert.assertArrayEquals(
            new byte[] { 'Y', 'W', 'J', 'j' },
            codec.encode(new byte[] { 'a', 'b', 'c' }));
}

@org.junit.Test(expected = IllegalArgumentException.class)
public void constructorRejectsBase64CharactersInLineSeparator() {
    new org.apache.commons.codec.binary.Base64(4, new byte[] { 'A' });
}