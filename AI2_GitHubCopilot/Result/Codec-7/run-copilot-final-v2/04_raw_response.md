@Test
 public void testDecodeObjectWithByteArray() {
     byte[] input = "SGVsbG8=".getBytes();
     byte[] expected = "Hello".getBytes();
     assertArrayEquals(expected, (byte[]) new Base64().decode((Object) input));
 }

 @Test
 public void testDecodeObjectWithString() {
     byte[] expected = "Hello".getBytes();
     assertArrayEquals(expected, (byte[]) new Base64().decode((Object) "SGVsbG8="));
 }

 @Test(expected = org.apache.commons.codec.DecoderException.class)
 public void testDecodeObjectWithInvalidType() {
     new Base64().decode(new Object());
 }

 @Test(expected = org.apache.commons.codec.EncoderException.class)
 public void testEncodeObjectWithInvalidType() {
     new Base64().encode(new Object());
 }