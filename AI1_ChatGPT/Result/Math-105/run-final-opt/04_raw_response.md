@Test
public void testStandardErrorsConfidenceIntervalSignificanceAndPositiveCorrelation()
        throws Exception {
    org.apache.commons.math.stat.regression.SimpleRegression regression =
            new org.apache.commons.math.stat.regression.SimpleRegression();
    regression.addData(0.0d, 1.0d);
    regression.addData(1.0d, 2.0d);
    regression.addData(2.0d, 2.0d);

    org.junit.Assert.assertEquals(Math.sqrt(5.0d) / 6.0d,
            regression.getInterceptStdErr(), 1.0e-12d);
    org.junit.Assert.assertEquals(Math.sqrt(1.0d / 12.0d),
            regression.getSlopeStdErr(), 1.0e-12d);
    org.junit.Assert.assertEquals(Math.sqrt(0.75d), regression.getR(), 1.0e-12d);
    org.junit.Assert.assertEquals(regression.getSlopeConfidenceInterval(0.05d),
            regression.getSlopeConfidenceInterval(), 0.0d);
    org.junit.Assert.assertEquals(1.0d / 3.0d, regression.getSignificance(), 1.0e-12d);
}

@Test
public void testSlopeIsNaNWhenAllXValuesAreIdentical() {
    org.apache.commons.math.stat.regression.SimpleRegression regression =
            new org.apache.commons.math.stat.regression.SimpleRegression();
    regression.addData(4.0d, 1.0d);
    regression.addData(4.0d, 2.0d);
    regression.addData(4.0d, 3.0d);

    org.junit.Assert.assertTrue(Double.isNaN(regression.getSlope()));
}

@Test
public void testSlopeConfidenceIntervalRejectsBothAlphaBoundaries() throws Exception {
    org.apache.commons.math.stat.regression.SimpleRegression regression =
            new org.apache.commons.math.stat.regression.SimpleRegression();

    try {
        regression.getSlopeConfidenceInterval(0.0d);
        org.junit.Assert.fail("alpha of zero must be rejected");
    } catch (IllegalArgumentException expected) {
    }

    try {
        regression.getSlopeConfidenceInterval(1.0d);
        org.junit.Assert.fail("alpha of one must be rejected");
    } catch (IllegalArgumentException expected) {
    }
}