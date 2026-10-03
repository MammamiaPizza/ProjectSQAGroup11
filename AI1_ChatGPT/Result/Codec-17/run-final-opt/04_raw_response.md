@org.junit.Test
public void testEqualsHandlesIdentityNullStringsAndOtherCharSequences() {
    org.junit.Assert.assertTrue(StringUtils.equals(null, null));
    org.junit.Assert.assertFalse(StringUtils.equals(null, "value"));
    org.junit.Assert.assertFalse(StringUtils.equals("value", null));
    org.junit.Assert.assertTrue(StringUtils.equals("value", "value"));
    org.junit.Assert.assertFalse(StringUtils.equals("value", "other"));
    org.junit.Assert.assertTrue(StringUtils.equals(new StringBuilder("value"), new StringBuilder("value")));
    org.junit.Assert.assertFalse(StringUtils.equals(new StringBuilder("value"), new StringBuilder("other")));
}

@org.junit.Test
public void testGetBytesCharsetWrappersEncodeAndHandleNull() {
    org.junit.Assert.assertArrayEquals(new byte[] { 0x41, (byte) 0xE9 },
            StringUtils.getBytesIso8859_1("Aé"));
    org.junit.Assert.assertArrayEquals(new byte[] { 0x41 },
            StringUtils.getBytesUsAscii("A"));
    org.junit.Assert.assertArrayEquals(new byte[] { (byte) 0xC3, (byte) 0xA9 },
            StringUtils.getBytesUtf8("é"));
    org.junit.Assert.assertArrayEquals(new byte[] { (byte) 0xFE, (byte) 0xFF, 0x00, 0x41 },
            StringUtils.getBytesUtf16("A"));
    org.junit.Assert.assertArrayEquals(new byte[] { 0x00, 0x41 },
            StringUtils.getBytesUtf16Be("A"));
    org.junit.Assert.assertArrayEquals(new byte[] { 0x41, 0x00 },
            StringUtils.getBytesUtf16Le("A"));

    org.junit.Assert.assertNull(StringUtils.getBytesIso8859_1(null));
    org.junit.Assert.assertNull(StringUtils.getBytesUsAscii(null));
    org.junit.Assert.assertNull(StringUtils.getBytesUtf8(null));
    org.junit.Assert.assertNull(StringUtils.getBytesUtf16(null));
    org.junit.Assert.assertNull(StringUtils.getBytesUtf16Be(null));
    org.junit.Assert.assertNull(StringUtils.getBytesUtf16Le(null));
}

@org.junit.Test
public void testGetBytesUncheckedHandlesValidNullAndUnsupportedCharsets() {
    org.junit.Assert.assertArrayEquals(new byte[] { (byte) 0xC3, (byte) 0xA9 },
            StringUtils.getBytesUnchecked("é", "UTF-8"));
    org.junit.Assert.assertNull(StringUtils.getBytesUnchecked(null, "UTF-8"));

    try {
        StringUtils.getBytesUnchecked("value", "unsupported-charset-name");
        org.junit.Assert.fail("Expected IllegalStateException");
    } catch (final IllegalStateException expected) {
    }
}

@org.junit.Test
public void testGetByteBufferUtf8WrapsEncodedBytesAndHandlesNull() {
    final java.nio.ByteBuffer buffer = StringUtils.getByteBufferUtf8("é");
    org.junit.Assert.assertNotNull(buffer);
    org.junit.Assert.assertEquals(0, buffer.position());
    org.junit.Assert.assertArrayEquals(new byte[] { (byte) 0xC3, (byte) 0xA9 }, buffer.array());
    org.junit.Assert.assertNull(StringUtils.getByteBufferUtf8(null));
}