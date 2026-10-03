@Test
public void testGetBytesUtf8_NullInput() {
    assertNull(StringUtils.getBytesUtf8(null));
}

@Test
public void testGetBytesUnchecked_NullInput() {
    assertNull(StringUtils.getBytesUnchecked(null, "UTF-8"));
}

@Test
public void testGetByteBufferUtf8_NullInput() {
    assertNull(StringUtils.getByteBufferUtf8(null));
}

@Test
public void testNewString_NullBytesInput() {
    assertNull(StringUtils.newString(null, "UTF-8"));
}