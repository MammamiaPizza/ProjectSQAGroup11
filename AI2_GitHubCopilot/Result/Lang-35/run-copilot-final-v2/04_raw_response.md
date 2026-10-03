@Test
    public void testAddNullByteArray_returnsByteArray() {
        byte[] result = ArrayUtils.add((byte[]) null, (byte) 42);
        assertSame(byte[].class, result.getClass());
        assertArrayEquals(new byte[]{42}, result);
    }

 @Test
 public void testAddNullCharArray_returnsCharArray() {
     char[] result = ArrayUtils.add((char[]) null, 'x');
     assertSame(char[].class, result.getClass());
     assertArrayEquals(new char[]{'x'}, result);
 }

 @Test
 public void testAddNullByteArrayAtIndexZero_returnsByteArray() {
     byte[] result = ArrayUtils.add((byte[]) null, 0, (byte) 42);
     assertSame(byte[].class, result.getClass());
     assertArrayEquals(new byte[]{42}, result);
 }

 @Test(expected = IndexOutOfBoundsException.class)
 public void testAddNullByteArrayAtIndexNonZero_throwsException() {
     ArrayUtils.add((byte[]) null, 1, (byte) 42);
 }