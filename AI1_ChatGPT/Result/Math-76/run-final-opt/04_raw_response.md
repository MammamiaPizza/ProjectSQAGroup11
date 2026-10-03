@org.junit.Test
public void testCovarianceKeepsOnlySingularValuesAboveCutoff() {
    final org.apache.commons.math.linear.SingularValueDecompositionImpl decomposition =
        new org.apache.commons.math.linear.SingularValueDecompositionImpl(
            new org.apache.commons.math.linear.Array2DRowRealMatrix(new double[][] {
                { 3.0, 0.0 },
                { 0.0, 2.0 }
            }));

    final org.apache.commons.math.linear.RealMatrix covariance =
        decomposition.getCovariance(2.5);

    org.junit.Assert.assertEquals(1.0 / 9.0, covariance.getEntry(0, 0), 1.0e-12);
    org.junit.Assert.assertEquals(0.0, covariance.getEntry(0, 1), 1.0e-12);
    org.junit.Assert.assertEquals(0.0, covariance.getEntry(1, 0), 1.0e-12);
    org.junit.Assert.assertEquals(0.0, covariance.getEntry(1, 1), 1.0e-12);
}

@org.junit.Test(expected = IllegalArgumentException.class)
public void testCovarianceRejectsCutoffAboveLargestSingularValue() {
    final org.apache.commons.math.linear.SingularValueDecompositionImpl decomposition =
        new org.apache.commons.math.linear.SingularValueDecompositionImpl(
            new org.apache.commons.math.linear.Array2DRowRealMatrix(new double[][] {
                { 3.0, 0.0 },
                { 0.0, 2.0 }
            }));

    decomposition.getCovariance(4.0);
}

@org.junit.Test
public void testWideMatrixFactorsReconstructOriginalMatrix() {
    final double[][] expected = {
        { 3.0, 0.0, 0.0 },
        { 0.0, 2.0, 0.0 }
    };
    final org.apache.commons.math.linear.SingularValueDecompositionImpl decomposition =
        new org.apache.commons.math.linear.SingularValueDecompositionImpl(
            new org.apache.commons.math.linear.Array2DRowRealMatrix(expected));

    final org.apache.commons.math.linear.RealMatrix u = decomposition.getU();
    final org.apache.commons.math.linear.RealMatrix s = decomposition.getS();
    final org.apache.commons.math.linear.RealMatrix v = decomposition.getV();
    final org.apache.commons.math.linear.RealMatrix vt = decomposition.getVT();

    org.junit.Assert.assertEquals(3, v.getData().length);
    org.junit.Assert.assertEquals(2, v.getData()[0].length);

    for (int row = 0; row < 2; ++row) {
        for (int column = 0; column < 3; ++column) {
            double reconstructed = 0.0;
            for (int index = 0; index < 2; ++index) {
                reconstructed += u.getEntry(row, index) * s.getEntry(index, index) *
                    vt.getEntry(index, column);
            }
            org.junit.Assert.assertEquals(expected[row][column], reconstructed, 1.0e-12);
        }
    }
}

@org.junit.Test
public void testRankDeficientDecompositionReportsSingularValuesAndCondition() {
    final org.apache.commons.math.linear.SingularValueDecompositionImpl decomposition =
        new org.apache.commons.math.linear.SingularValueDecompositionImpl(
            new org.apache.commons.math.linear.Array2DRowRealMatrix(new double[][] {
                { 4.0, 0.0 },
                { 0.0, 0.0 }
            }));

    org.junit.Assert.assertArrayEquals(new double[] { 4.0, 0.0 },
        decomposition.getSingularValues(), 1.0e-12);
    org.junit.Assert.assertEquals(4.0, decomposition.getNorm(), 1.0e-12);
    org.junit.Assert.assertEquals(1, decomposition.getRank());
    org.junit.Assert.assertEquals(Double.POSITIVE_INFINITY,
        decomposition.getConditionNumber(), 0.0);
}