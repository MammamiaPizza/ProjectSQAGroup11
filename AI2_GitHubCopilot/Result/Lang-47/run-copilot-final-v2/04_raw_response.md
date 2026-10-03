@Test
    public void testConstructorWithNegativeCapacity() {
        assertTrue(new StrBuilder(-5).isEmpty());
    }

 @Test
 public void testConstructorWithNullString() {
     assertTrue(new StrBuilder((String) null).isEmpty());
 }

 @Test
 public void testAppendChar() {
     StrBuilder sb = new StrBuilder();
     sb.append('A');
     assertEquals("A", sb.toString());
 }

 @Test(expected = NullPointerException.class)
 public void testAppendNullThrowsNullPointerWhenNullTextNull() {
     new StrBuilder().setNullText(null).appendNull();
 }