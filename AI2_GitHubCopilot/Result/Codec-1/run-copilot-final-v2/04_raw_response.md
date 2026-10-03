@Test
    public void testCaverphoneNullAndEmpty() {
        Caverphone c = new Caverphone();
        assertEquals("Caverphone(null) should return '1111111111'", "1111111111",
c.caverphone(null));
        assertEquals("Caverphone('') should return '1111111111'", "1111111111", c.caverphone(""));
    }

 @Test(expected = org.apache.commons.codec.EncoderException.class)
 public void testCaverphoneEncodeNonString() throws org.apache.commons.codec.EncoderException {
     new Caverphone().encode(new Object());
 }

 @Test(expected = org.apache.commons.codec.EncoderException.class)
 public void testMetaphoneEncodeNonString() throws org.apache.commons.codec.EncoderException {
     new Metaphone().encode(new Object());
 }

 @Test
 public void testSoundexUtilsDifference() throws org.apache.commons.codec.EncoderException {
     assertEquals("Difference of 'hello' and 'hallo' using Metaphone must be 4",
             4, SoundexUtils.difference(new Metaphone(), "hello", "hallo"));
 }