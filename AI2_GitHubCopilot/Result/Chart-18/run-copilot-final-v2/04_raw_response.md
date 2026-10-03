@Test
 public void testClear() {
     DefaultKeyedValues data = new DefaultKeyedValues();
     data.addValue("A", 1.0);
     data.addValue("B", 2.0);
     assertEquals(2, data.getItemCount());
     data.clear();
     assertEquals(0, data.getItemCount());
     assertEquals(-1, data.getIndex("A"));
     assertEquals(-1, data.getIndex("B"));
     assertTrue(data.getKeys().isEmpty());
 }

 @Test
 public void testClone() throws CloneNotSupportedException {
     DefaultKeyedValues original = new DefaultKeyedValues();
     original.addValue("K1", 10.0);
     original.addValue("K2", 20.0);
     DefaultKeyedValues cloned = (DefaultKeyedValues) original.clone();
     assertNotNull(cloned);
     assertNotSame(original, cloned);
     assertEquals(original.getItemCount(), cloned.getItemCount());
     assertEquals(original.getValue("K1"), cloned.getValue("K1"));
     original.removeValue("K1");
     assertEquals(2, cloned.getItemCount());
     assertEquals(10.0, cloned.getValue("K1").doubleValue(), 0.0);
 }

 @Test
 public void testEquals() {
     DefaultKeyedValues d1 = new DefaultKeyedValues();
     d1.addValue("X", 5.0);
     DefaultKeyedValues d2 = new DefaultKeyedValues();
     d2.addValue("X", 5.0);
     DefaultKeyedValues d3 = new DefaultKeyedValues();
     d3.addValue("Y", 5.0);
     DefaultKeyedValues d4 = new DefaultKeyedValues();
     d4.addValue("X", 5.0);
     d4.addValue("Z", 7.0);

     assertTrue(d1.equals(d1));
     assertTrue(d1.equals(d2));
     assertTrue(d2.equals(d1));
     assertFalse(d1.equals(d3));
     assertFalse(d1.equals("string"));
     assertFalse(d1.equals(d4));
 }

 @Test(expected = IndexOutOfBoundsException.class)
 public void testRemoveRowByKeyNonexistent() {
     org.jfree.data.DefaultKeyedValues2D table = new org.jfree.data.DefaultKeyedValues2D();
     table.removeRow("NoSuchRow");
 }