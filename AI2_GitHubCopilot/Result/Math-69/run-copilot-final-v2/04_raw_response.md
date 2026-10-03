@Test
    public void testDefaultConstructor() {
        PearsonsCorrelation pc = new PearsonsCorrelation();
        assertNull(pc.getCorrelationMatrix());
    }

 @Test
 public void testCovarianceConstructor() {
     double[][] data = {{1, 2}, {2, 4}, {3, 6}, {4, 8}, {5, 10}};
     org.apache.commons.math.stat.correlation.Covariance cov =
         new org.apache.commons.math.stat.correlation.Covariance(data);
     PearsonsCorrelation pc = new PearsonsCorrelation(cov);
     org.apache.commons.math.linear.RealMatrix corr = pc.getCorrelationMatrix();
     assertEquals(2, corr.getRowDimension());
     assertEquals(2, corr.getColumnDimension());
     assertEquals(1.0, corr.getEntry(0, 1), 1e-10);
     assertEquals(1.0, corr.getEntry(1, 0), 1e-10);
 }

 @Test
 public void testCorrelationStandardErrors() {
     double[][] data = {{1, 2}, {2, 4}, {3, 6}, {4, 8}, {5, 10}};
     PearsonsCorrelation pc = new PearsonsCorrelation(data);
     org.apache.commons.math.linear.RealMatrix se = pc.getCorrelationStandardErrors();
     assertEquals(2, se.getRowDimension());
     assertEquals(2, se.getColumnDimension());
     assertEquals(0.0, se.getEntry(0, 1), 1e-15);
     assertEquals(0.0, se.getEntry(1, 0), 1e-15);
     assertEquals(0.0, se.getEntry(0, 0), 1e-15);
 }

 @Test(expected = IllegalArgumentException.class)
 public void testCorrelationWithMismatchedDimensions() {
     double[][] data = {{1, 2}, {2, 4}, {3, 6}};
     PearsonsCorrelation pc = new PearsonsCorrelation(data);
     pc.correlation(new double[]{1, 2, 3}, new double[]{1, 2});
 }