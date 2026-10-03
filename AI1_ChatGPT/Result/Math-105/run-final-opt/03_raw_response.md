package org.apache.commons.math.stat.regression;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SimpleRegressionMATH105Test {

    @Test
    public void testSumSquaredErrorsIsNeverNegativeForNearlyPerfectFit() {
        SimpleRegression regression = new SimpleRegression();

        for (int i = 1; i < 10; i++) {
            regression.addData(i, i);
        }
        regression.addData(10.0d, 10.000000000001d);

        assertTrue("SSE must be non-negative", regression.getSumSquaredErrors() >= 0.0d);
    }

    @Test
    public void testExactLinearDataHasZeroError() {
        SimpleRegression regression = new SimpleRegression();

        regression.addData(0.0d, 1.0d);
        regression.addData(1.0d, 3.0d);
        regression.addData(2.0d, 5.0d);
        regression.addData(3.0d, 7.0d);

        assertEquals(4L, regression.getN());
        assertEquals(2.0d, regression.getSlope(), 0.0d);
        assertEquals(1.0d, regression.getIntercept(), 0.0d);
        assertEquals(0.0d, regression.getSumSquaredErrors(), 0.0d);
        assertEquals(7.0d, regression.predict(3.0d), 0.0d);
    }

    @Test
    public void testArrayAdditionComputesRegressionSummaryValues() {
        SimpleRegression regression = new SimpleRegression();

        regression.addData(new double[][] {
            {1.0d, 2.0d},
            {2.0d, 3.0d},
            {3.0d, 5.0d},
            {4.0d, 4.0d}
        });

        assertEquals(4L, regression.getN());
        assertEquals(0.8d, regression.getSlope(), 1.0e-15d);
        assertEquals(1.5d, regression.getIntercept(), 1.0e-15d);
        assertEquals(1.8d, regression.getSumSquaredErrors(), 1.0e-15d);
        assertEquals(5.0d, regression.getTotalSumSquares(), 1.0e-15d);
        assertEquals(3.2d, regression.getRegressionSumSquares(), 1.0e-15d);
        assertEquals(0.64d, regression.getRSquare(), 1.0e-15d);
    }

    @Test
    public void testInsufficientDataReturnsNaNForUnavailableStatistics() {
        SimpleRegression regression = new SimpleRegression();

        assertTrue(Double.isNaN(regression.getSlope()));
        assertTrue(Double.isNaN(regression.getTotalSumSquares()));
        assertTrue(Double.isNaN(regression.getMeanSquareError()));

        regression.addData(0.0d, 0.0d);
        assertTrue(Double.isNaN(regression.getSlope()));
        assertTrue(Double.isNaN(regression.getTotalSumSquares()));

        regression.addData(1.0d, 1.0d);
        assertEquals(1.0d, regression.getSlope(), 0.0d);
        assertTrue(Double.isNaN(regression.getMeanSquareError()));

        regression.addData(2.0d, 2.0d);
        assertEquals(0.0d, regression.getMeanSquareError(), 0.0d);
    }

    @Test
    public void testNegativeSlopeProducesNegativeCorrelation() {
        SimpleRegression regression = new SimpleRegression();

        regression.addData(1.0d, 4.0d);
        regression.addData(2.0d, 2.0d);
        regression.addData(3.0d, 0.0d);

        assertEquals(-2.0d, regression.getSlope(), 0.0d);
        assertEquals(-1.0d, regression.getR(), 0.0d);
        assertEquals(1.0d, regression.getRSquare(), 0.0d);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSlopeConfidenceIntervalRejectsInvalidAlpha() throws Exception {
        SimpleRegression regression = new SimpleRegression();
        regression.getSlopeConfidenceInterval(0.0d);
    }

    @Test
    public void testClearAllowsIndependentNewDataSet() {
        SimpleRegression regression = new SimpleRegression();

        regression.addData(1.0d, 10.0d);
        regression.addData(2.0d, 20.0d);
        regression.clear();

        assertEquals(0L, regression.getN());

        regression.addData(5.0d, 1.0d);
        regression.addData(6.0d, 3.0d);

        assertEquals(2L, regression.getN());
        assertEquals(2.0d, regression.getSlope(), 0.0d);
        assertEquals(-9.0d, regression.getIntercept(), 0.0d);
    }
}