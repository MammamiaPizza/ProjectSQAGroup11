@Test(expected = IllegalArgumentException.class)
 public void testSerializeNullOutputStream() {
     SerializationUtils.serialize("test", (java.io.OutputStream) null);
 }

 @Test(expected = IllegalArgumentException.class)
 public void testDeserializeNullInputStream() {
     SerializationUtils.deserialize((java.io.InputStream) null);
 }

 @Test(expected = org.apache.commons.lang3.SerializationException.class)
 public void testDeserializeCorruptStream() {
     SerializationUtils.deserialize(new java.io.ByteArrayInputStream(new byte[]{0, 0, 0, 0}));
 }

 @Test
 public void testConstructor() {
     new SerializationUtils();
 }