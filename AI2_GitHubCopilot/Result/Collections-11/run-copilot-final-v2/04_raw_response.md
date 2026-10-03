@Test
 public void testFourKeyConstructor() {
     MultiKey mk = new MultiKey("a", "b", "c", "d");
     assertEquals(4, mk.size());
     assertEquals("a", mk.getKey(0));
     assertEquals("b", mk.getKey(1));
     assertEquals("c", mk.getKey(2));
     assertEquals("d", mk.getKey(3));
 }

 @Test(expected = IllegalArgumentException.class)
 public void testNullArrayConstructorThrowsException() {
     new MultiKey((Object[]) null, false);
 }

 @Test
 public void testEqualsWithSelf() {
     MultiKey mk = new MultiKey("a", "b");
     assertTrue(mk.equals(mk));
 }

 @Test
 public void testEqualsWithNonMultiKey() {
     MultiKey mk = new MultiKey("a", "b");
     assertFalse(mk.equals("not a MultiKey"));
 }