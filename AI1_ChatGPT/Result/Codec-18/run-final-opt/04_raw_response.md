@Test
public void charsetConvenienceMethodsEncodeAndDecodeExpectedValues() {
    final String value = "café";
    final byte[] iso88591 = new byte[] { 99, 97, 102, (byte) 0xe9 };
    final byte[] utf8 = new byte[] { 99, 97, 102, (byte) 0xc3, (byte) 0xa9 };
    final byte[] utf16 = new byte[] {
        (byte) 0xfe, (byte) 0xff, 0, 99, 0, 97, 0, 102, 0, (byte) 0xe9
    };
    final byte[] utf16be = new byte[] { 0, 99, 0, 97, 0, 102, 0, (byte) 0xe9 };
    final byte[] utf16le = new byte[] { 99, 0, 97, 0, 102, 0, (byte) 0xe9, 0 };

    org.junit.Assert.assertArrayEquals(iso88591, StringUtils.getBytesIso8859_1(value));
    org.junit.Assert.assertArrayEquals(new byte[] { 99, 111, 100, 101, 99 },
            StringUtils.getBytesUsAscii("codec"));
    org.junit.Assert.assertArrayEquals(utf16, StringUtils.getBytesUtf16(value));
    org.junit.Assert.assertArrayEquals(utf16be, StringUtils.getBytesUtf16Be(value));
    org.junit.Assert.assertArrayEquals(utf16le, StringUtils.getBytesUtf16Le(value));
    org.junit.Assert.assertArrayEquals(utf8, StringUtils.getBytesUtf8(value));

    org.junit.Assert.assertEquals(value, StringUtils.newStringIso8859_1(iso88591));
    org.junit.Assert.assertEquals("codec",
            StringUtils.newStringUsAscii(new byte[] { 99, 111, 100, 101, 99 }));
    org.junit.Assert.assertEquals(value, StringUtils.newStringUtf16(utf16));
    org.junit.Assert.assertEquals(value, StringUtils.newStringUtf16Be(utf16be));
    org.junit.Assert.assertEquals(value, StringUtils.newStringUtf16Le(utf16le));
    org.junit.Assert.assertEquals(value, StringUtils.newStringUtf8(utf8));

    org.junit.Assert.assertNull(StringUtils.getBytesUtf8(null));
    org.junit.Assert.assertNull(StringUtils.newStringUtf8(null));
}

@Test
public void getByteBufferUtf8ReturnsWrappedUtf8BytesAndHandlesNull() {
    final java.nio.ByteBuffer buffer = StringUtils.getByteBufferUtf8("café");

    org.junit.Assert.assertArrayEquals(
            new byte[] { 99, 97, 102, (byte) 0xc3, (byte) 0xa9 }, buffer.array());
    org.junit.Assert.assertEquals(0, buffer.position());
    org.junit.Assert.assertEquals(5, buffer.remaining());
    org.junit.Assert.assertNull(StringUtils.getByteBufferUtf8(null));
}

@Test
public void uncheckedByteConversionSupportsValidCharsetsAndWrapsUnsupportedCharsets() {
    org.junit.Assert.assertArrayEquals(
            new byte[] { 99, 97, 102, (byte) 0xc3, (byte) 0xa9 },
            StringUtils.getBytesUnchecked("café", "UTF-8"));
    org.junit.Assert.assertNull(StringUtils.getBytesUnchecked(null, "UTF-8"));

    final String charsetName = "definitely-not-a-supported-charset";
    try {
        StringUtils.getBytesUnchecked("codec", charsetName);
        org.junit.Assert.fail("Expected an IllegalStateException for an unsupported charset");
    } catch (final IllegalStateException expected) {
        org.junit.Assert.assertTrue(expected.getMessage().contains(charsetName));
    }
}

@Test
public void namedStringConversionHandlesNullValidAndUnsupportedCharsets() {
    final byte[] utf8 = new byte[] { 99, 97, 102, (byte) 0xc3, (byte) 0xa9 };

    org.junit.Assert.assertEquals("café", StringUtils.newString(utf8, "UTF-8"));
    org.junit.Assert.assertNull(StringUtils.newString(null, "UTF-8"));

    final String charsetName = "definitely-not-a-supported-charset";
    try {
        StringUtils.newString(utf8, charsetName);
        org.junit.Assert.fail("Expected an IllegalStateException for an unsupported charset");
    } catch (final IllegalStateException expected) {
        org.junit.Assert.assertTrue(expected.getMessage().contains(charsetName));
    }
}