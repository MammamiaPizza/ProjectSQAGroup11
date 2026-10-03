@org.junit.Test
public void testDecodeObjectWithByteArray() throws org.apache.commons.codec.DecoderException {
    Base64 base64 = new Base64();
    Object result = base64.decode((Object) new byte[0]);
    org.junit.Assert.assertTrue(result instanceof byte[]);
    org.junit.Assert.assertEquals(0, ((byte[]) result).length);
}

@org.junit.Test(expected = org.apache.commons.codec.DecoderException.class)
public void testDecodeObjectWithNonByteArrayThrowsDecoderException() throws
org.apache.commons.codec.DecoderException {
    new Base64().decode(new Object());
}

@org.junit.Test(expected = IllegalArgumentException.class)
public void testConstructorThrowsWhenLineSeparatorContainsBase64Character() {
    new Base64(76, new byte[] {'A'});
}

@org.junit.Test
public void testIsArrayByteBase64WithBase64AndNonBase64Inputs() {
    org.junit.Assert.assertTrue(Base64.isArrayByteBase64(new byte[] {'A', 'B'}));
    org.junit.Assert.assertFalse(Base64.isArrayByteBase64(new byte[] {'A', '!'}));
}