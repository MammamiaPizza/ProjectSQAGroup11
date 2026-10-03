@Test
public void testCovarianceConstructorBuildsCorrelationMatrix() {
    org.apache.commons.math.stat.correlation.Covariance covariance =
            new org.apache.commons.math.stat.correlation.Covariance(
                    new double[][] {
                        {1.0, 2.0},
                        {2.0, 4.0},
                        {3.0, 6.0}
                    });

    PearsonsCorrelation correlation = new PearsonsCorrelation(covariance);

    assertEquals(1.0, correlation.getCorrelationMatrix().getEntry(0, 0), 0.0);
    assertEquals(1.0, correlation.getCorrelationMatrix().getEntry(1, 1), 0.0);
    assertEquals(1.0, correlation.getCorrelationMatrix().getEntry(0, 1), 0.0);
    assertEquals(1.0, correlation.getCorrelationMatrix().getEntry(1, 0), 0.0);
}

@Test
public void testCovarianceConstructorRejectsCovarianceWithoutMatrix() {
    try {
        new PearsonsCorrelation(new org.apache.commons.math.stat.correlation.Covariance());
        fail("A covariance instance without a covariance matrix must be rejected");
    } catch (IllegalArgumentException expected) {
        assertTrue(expected.getMessage().length() > 0);
    }
}

@Test
public void testComputeCorrelationMatrixFromArray() {
    PearsonsCorrelation correlation = new PearsonsCorrelation();

    org.apache.commons.math.linear.RealMatrix matrix =
            correlation.computeCorrelationMatrix(new double[][] {
                {1.0, 6.0},
                {2.0, 4.0},
                {3.0, 2.0}
            });

    assertEquals(1.0, matrix.getEntry(0, 0), 0.0);
    assertEquals(1.0, matrix.getEntry(1, 1), 0.0);
    assertEquals(-1.0, matrix.getEntry(0, 1), 0.0);
    assertEquals(-1.0, matrix.getEntry(1, 0), 0.0);
}