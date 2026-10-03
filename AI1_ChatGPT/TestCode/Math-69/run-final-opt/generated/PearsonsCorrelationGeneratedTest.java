package org.apache.commons.math.stat.correlation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.math.MathException;
import org.apache.commons.math.linear.BlockRealMatrix;
import org.apache.commons.math.linear.RealMatrix;
import org.junit.Test;

public class PearsonsCorrelationGeneratedTest {

    @Test
    public void testCorrelationComputesPerfectPositiveAndNegativeRelationships() {
        PearsonsCorrelation correlation = new PearsonsCorrelation();

        assertEquals(1.0,
                correlation.correlation(new double[] { 1, 2, 3, 4 },
                                        new double[] { 2, 4, 6, 8 }),
                1.0e-15);
        assertEquals(-1.0,
                correlation.correlation(new double[] { 1, 2, 3, 4 },
                                        new double[] { 8, 6, 4, 2 }),
                1.0e-15);
    }

    @Test
    public void testCorrelationRejectsMismatchedAndInsufficientArrays() {
        PearsonsCorrelation correlation = new PearsonsCorrelation();

        try {
            correlation.correlation(new double[] { 1, 2 }, new double[] { 1 });
            fail("Mismatched array lengths must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().length() > 0);
        }

        try {
            correlation.correlation(new double[] { 1 }, new double[] { 1 });
            fail("A single observation must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().length() > 0);
        }
    }

    @Test
    public void testDataConstructorComputesExpectedCorrelationMatrix() {
        double[][] data = {
            { 1, 2, 5 },
            { 2, 4, 4 },
            { 3, 6, 3 },
            { 4, 8, 2 }
        };

        PearsonsCorrelation correlation = new PearsonsCorrelation(data);
        RealMatrix matrix = correlation.getCorrelationMatrix();

        assertEquals(3, matrix.getRowDimension());
        assertEquals(3, matrix.getColumnDimension());
        assertEquals(1.0, matrix.getEntry(0, 0), 0.0);
        assertEquals(1.0, matrix.getEntry(1, 1), 0.0);
        assertEquals(1.0, matrix.getEntry(2, 2), 0.0);
        assertEquals(1.0, matrix.getEntry(0, 1), 1.0e-15);
        assertEquals(-1.0, matrix.getEntry(0, 2), 1.0e-15);
        assertEquals(-1.0, matrix.getEntry(1, 2), 1.0e-15);
    }

    @Test
    public void testVerySmallPValueFromDataIsPositiveRatherThanLostToCancellation()
        throws MathException {
        double[][] data = new double[20][2];
        for (int i = 0; i < data.length; i++) {
            data[i][0] = i;
            data[i][1] = i + ((i & 1) == 0 ? 0.1 : -0.1);
        }

        PearsonsCorrelation correlation = new PearsonsCorrelation(data);
        RealMatrix pValues = correlation.getCorrelationPValues();

        assertTrue(correlation.getCorrelationMatrix().getEntry(0, 1) > 0.999);
        assertTrue("A finite, extremely significant correlation has a positive p-value",
                pValues.getEntry(0, 1) > 0.0);
        assertTrue(pValues.getEntry(0, 1) < 1.0e-20);
        assertEquals(pValues.getEntry(0, 1), pValues.getEntry(1, 0), 0.0);
    }

    @Test
    public void testCovarianceMatrixConstructorScalesAndPreservesSmallPValue()
        throws MathException {
        RealMatrix covariance = new BlockRealMatrix(new double[][] {
            { 4.0, 5.994 },
            { 5.994, 9.0 }
        });

        PearsonsCorrelation correlation = new PearsonsCorrelation(covariance, 20);
        RealMatrix correlations = correlation.getCorrelationMatrix();
        RealMatrix pValues = correlation.getCorrelationPValues();

        assertEquals(1.0, correlations.getEntry(0, 0), 0.0);
        assertEquals(1.0, correlations.getEntry(1, 1), 0.0);
        assertEquals(0.999, correlations.getEntry(0, 1), 1.0e-15);
        assertEquals(0.999, correlations.getEntry(1, 0), 1.0e-15);
        assertTrue("The p-value for a correlation near one must not underflow by subtraction",
                pValues.getEntry(0, 1) > 0.0);
        assertTrue(pValues.getEntry(0, 1) < 1.0e-12);
    }

    @Test
    public void testPValuesAndStandardErrorsForZeroCorrelation() throws MathException {
        RealMatrix covariance = new BlockRealMatrix(new double[][] {
            { 4.0, 0.0 },
            { 0.0, 9.0 }
        });

        PearsonsCorrelation correlation = new PearsonsCorrelation(covariance, 10);
        RealMatrix pValues = correlation.getCorrelationPValues();
        RealMatrix standardErrors = correlation.getCorrelationStandardErrors();

        assertEquals(0.0, pValues.getEntry(0, 0), 0.0);
        assertEquals(0.0, pValues.getEntry(1, 1), 0.0);
        assertEquals(1.0, pValues.getEntry(0, 1), 1.0e-15);
        assertEquals(1.0, pValues.getEntry(1, 0), 1.0e-15);
        assertEquals(0.0, standardErrors.getEntry(0, 0), 0.0);
        assertEquals(Math.sqrt(1.0 / 8.0), standardErrors.getEntry(0, 1), 1.0e-15);
        assertFalse(Double.isNaN(standardErrors.getEntry(0, 1)));
    }

    @Test
    public void testTwoByTwoDataIsAcceptedAsMinimumSufficientMatrix() {
        PearsonsCorrelation correlation = new PearsonsCorrelation(new double[][] {
            { 1.0, 3.0 },
            { 2.0, 4.0 }
        });

        assertEquals(2, correlation.getCorrelationMatrix().getRowDimension());
        assertEquals(1.0, correlation.getCorrelationMatrix().getEntry(0, 1), 1.0e-15);
    }

    @Test
    public void testDataConstructorRejectsMatricesWithoutTwoRowsAndColumns() {
        try {
            new PearsonsCorrelation(new double[][] {
                { 1.0, 2.0 }
            });
            fail("A matrix with one row must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().length() > 0);
        }

        try {
            new PearsonsCorrelation(new double[][] {
                { 1.0 },
                { 2.0 }
            });
            fail("A matrix with one column must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().length() > 0);
        }
    }
}
