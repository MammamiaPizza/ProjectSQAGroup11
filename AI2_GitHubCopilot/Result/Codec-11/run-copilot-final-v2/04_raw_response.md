@Test
    public void testEncodeStringNullWithCharset() throws java.io.UnsupportedEncodingException {
        org.junit.Assert.assertNull(new QuotedPrintableCodec().encode(null, "UTF-8"));
    }

 @Test
 public void testGetDefaultCharset() {
     org.junit.Assert.assertEquals("UTF-8", new QuotedPrintableCodec("UTF-8").getDefaultCharset());
 }

 @Test
 public void testDecodeEmptyByteArray() throws Exception {
     org.junit.Assert.assertArrayEquals(new byte[0], new QuotedPrintableCodec().decode(new
byte[0]));
 }

 @Test
 public void testEncodeEmptyByteArray() {
     org.junit.Assert.assertArrayEquals(new byte[0], new QuotedPrintableCodec().encode(new
byte[0]));
 }