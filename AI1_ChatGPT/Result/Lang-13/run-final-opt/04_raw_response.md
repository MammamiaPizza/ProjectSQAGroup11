@Test(expected = IllegalArgumentException.class)
public void serializeRejectsNullOutputStream() {
    org.apache.commons.lang3.SerializationUtils.serialize("value", null);
}

@Test
public void serializeWrapsOutputStreamIOException() {
    try {
        org.apache.commons.lang3.SerializationUtils.serialize("value", new java.io.OutputStream() {
            @Override
            public void write(final int b) throws java.io.IOException {
                throw new java.io.IOException("write failure");
            }
        });
        org.junit.Assert.fail("Expected SerializationException");
    } catch (org.apache.commons.lang3.SerializationException ex) {
        org.junit.Assert.assertTrue(ex.getCause() instanceof java.io.IOException);
    }
}

@Test
public void deserializeWrapsMalformedStreamIOException() {
    try {
        org.apache.commons.lang3.SerializationUtils.deserialize(new byte[] { 1 });
        org.junit.Assert.fail("Expected SerializationException");
    } catch (org.apache.commons.lang3.SerializationException ex) {
        org.junit.Assert.assertTrue(ex.getCause() instanceof java.io.IOException);
    }
}

@Test
public void deserializeIgnoresInputStreamCloseIOException() {
    final byte[] data = org.apache.commons.lang3.SerializationUtils.serialize("value");

    final Object deserialized = org.apache.commons.lang3.SerializationUtils.deserialize(
            new java.io.ByteArrayInputStream(data) {
                @Override
                public void close() throws java.io.IOException {
                    throw new java.io.IOException("close failure");
                }
            });

    org.junit.Assert.assertEquals("value", deserialized);
}