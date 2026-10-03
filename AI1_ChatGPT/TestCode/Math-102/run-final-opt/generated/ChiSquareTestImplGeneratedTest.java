package org.apache.commons.math.stat.inference;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChiSquareTestImplGeneratedTest {

    @Test
    public void chiSquareRescalesExpectedFrequenciesWhenTotalsDiffer() {
        ChiSquareTestImpl test = new ChiSquareTestImpl();

        double statistic = test.chiSquare(
                new double[] {1.0, 1.0},
                new long[] {30L, 10L});

        assertEquals(10.0, statistic, 1.0e-12);
    }

    @Test
    public void chiSquareUsesExpectedCountsDirectlyWhenTotalsMatch() {
        ChiSquareTestImpl test = new ChiSquareTestImpl();

        double statistic = test.chiSquare(
                new double[] {10.0, 20.0, 30.0},
                new long[] {12L, 18L, 30L});

        assertEquals(0.6, statistic, 1.0e-12);
    }

    @Test
    public void chiSquareRescalingHandlesZeroObservedCategory() {
        ChiSquareTestImpl test = new ChiSquareTestImpl();

        double statistic = test.chiSquare(
                new double[] {0.5, 0.5},
                new long[] {0L, 10L});

        assertEquals(10.0, statistic, 1.0e-12);
    }

    @Test
    public void chiSquareTestUsesRescaledStatisticForPValue() throws Exception {
        ChiSquareTestImpl test = new ChiSquareTestImpl();

        double pValue = test.chiSquareTest(
                new double[] {1.0, 1.0},
                new long[] {30L, 10L});

        assertEquals(0.00156540225800255, pValue, 1.0e-10);
    }

    @Test
    public void chiSquareTestWithAlphaReflectsRescaledPValue() throws Exception {
        ChiSquareTestImpl test = new ChiSquareTestImpl();
        double[] expected = {1.0, 1.0};
        long[] observed = {30L, 10L};

        assertTrue(test.chiSquareTest(expected, observed, 0.01));
        assertFalse(test.chiSquareTest(expected, observed, 0.001));
    }

    @Test(expected = IllegalArgumentException.class)
    public void chiSquareRejectsMismatchedArrayLengths() {
        new ChiSquareTestImpl().chiSquare(
                new double[] {1.0, 1.0},
                new long[] {1L, 2L, 3L});
    }

    @Test(expected = IllegalArgumentException.class)
    public void chiSquareRejectsNonPositiveExpectedFrequencies() {
        new ChiSquareTestImpl().chiSquare(
                new double[] {1.0, 0.0},
                new long[] {1L, 1L});
    }

    @Test(expected = IllegalArgumentException.class)
    public void chiSquareRejectsNegativeObservedFrequencies() {
        new ChiSquareTestImpl().chiSquare(
                new double[] {1.0, 1.0},
                new long[] {1L, -1L});
    }

    @Test(expected = IllegalArgumentException.class)
    public void chiSquareTestRejectsInvalidAlpha() throws Exception {
        new ChiSquareTestImpl().chiSquareTest(
                new double[] {1.0, 1.0},
                new long[] {1L, 1L},
                0.0);
    }
}
