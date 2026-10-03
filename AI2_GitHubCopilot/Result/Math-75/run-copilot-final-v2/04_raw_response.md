@Test(expected = IllegalArgumentException.class)
 public void testAddValueNonComparableObjectThrowsException() {
     Frequency f = new Frequency();
     f.addValue(new Object());
 }

 @Test(expected = IllegalArgumentException.class)
 public void testAddValueIncompatibleTypesThrowsException() {
     Frequency f = new Frequency();
     f.addValue("string");
     f.addValue(42);
 }

 @Test
 public void testFrequencyWithCustomComparator() {
     Frequency f = new Frequency(java.util.Collections.reverseOrder());
     f.addValue(1);
     f.addValue(2);
     f.addValue(2);
     assertEquals("getPct with custom comparator", 1.0 / 3.0, f.getPct(1), 1e-9);
     assertEquals("getPct(Object) with custom comparator", 2.0 / 3.0,
             f.getPct((Object) Integer.valueOf(2)), 1e-9);
 }

 @Test
 public void testClearResetsFrequency() {
     Frequency f = new Frequency();
     f.addValue("a");
     f.addValue("b");
     assertEquals(2, f.getSumFreq());
     f.clear();
     assertEquals(0, f.getSumFreq());
     assertTrue("getPct should return NaN after clear", Double.isNaN(f.getPct("a")));
 }