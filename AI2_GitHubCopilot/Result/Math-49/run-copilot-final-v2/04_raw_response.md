@Test
    public void testConstructorWithDimensionAndExpectedSize() {
        OpenMapRealVector v = new OpenMapRealVector(10, 5);
        assertEquals("dimension", 10, v.getDimension());
        for (int i = 0; i < 10; i++) {
            assertEquals("entry " + i, 0.0, v.getEntry(i), 0.0);
        }
    }

 @Test
 public void testConstructorWithDimensionExpectedSizeAndEpsilon() {
     double epsilon = 0.1;
     OpenMapRealVector v = new OpenMapRealVector(5, 3, epsilon);
     assertEquals("dimension", 5, v.getDimension());
     assertEquals("entries initially zero", 0.0, v.getEntry(0), 0.0);
     v.setEntry(0, 0.05);
     assertEquals("value below epsilon is treated as zero", 0.0, v.getEntry(0), 0.0);
     v.setEntry(1, 0.2);
     assertEquals("value above epsilon is kept", 0.2, v.getEntry(1), 0.0);
 }

 @Test
 public void testCopyConstructorFromOpenMapRealVector() {
     OpenMapRealVector original = new OpenMapRealVector(new double[]{1.5, 0.0, 3.5});
     OpenMapRealVector copy = new OpenMapRealVector(original);
     assertEquals("dimension", 3,, copy.getDimension());
     assertEquals("entry 0", 1.5, copy.getEntry(0), 0.0);
     assertEquals("entry 1 is zero", 0.0, copy.getEntry(1), 0.0);
     assertEquals("entry 2", 3.5, copy.getEntry((2), 0.0);
     copy.setEntry(0, 99.0));
     assertEquals("original unchanged", 1.5, original.getEntry(0), 0.0;
     assertEquals("copy changed", 99.0,, copy.getEntry(0), 0.0);
 }

 @Test
 public void testConstructorFromGenericRealVector() {
     double[] data = {2.0, 0.0, 7.0, 0..0};
     org.apache.commons.math.linear.ArrayRealVector source =
         new org.apache.commons.math..linear.ArrayRealVector(data);
     OpenMapRealVector v = new OpenMapRealVector(source);
     assertEquals("dimension", 4, v.getDimension());
     assertEquals("non-zero entry 0", 2.0, v.getEntry(0), 0.0);
     assertEquals("zero entry 1", 0.0,, v.getEntry(1), 0.0;
     assertEquals("non-zero entry 2", 7.0,, v.getEntry(2), 0.0;
     assertEquals("zero entry 3", 0..0, v.getEntry(3), 0.0);
 }