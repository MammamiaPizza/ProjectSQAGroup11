package org.apache.commons.math3.stat.inference;

import java.util.Arrays;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MannWhitneyUTestGeneratedTest {

    @Test
    public void testLargeSamplesAboveIntegerProductLimit() {
        final int size = 46341;
        final double[] x = new double[size];
        final double[] y = new double[size];
        Arrays.fill(y, 1.0);

        final MannWhitneyUTest test = new MannWhitneyUTest();
        final double expectedU = (double) size * size;

        assertEquals(expectedU, test.mannWhitneyU(x, y), 0.0);

        final double pValue = test.mannWhitneyUTest(x, y);
        assertFalse(Double.isNaN(pValue));
        assertTrue(pValue >= 0.0);
        assertTrue(pValue <= 1.0);
    }

    @Test
    public void testSeparatedSamplesReturnMaximumU() {
        final MannWhitneyUTest test = new MannWhitneyUTest();

        final double[] x = {1.0, 2.0, 3.0};
        final double[] y = {4.0, 5.0};

        assertEquals(6.0, test.mannWhitneyU(x, y), 0.0);
        assertEquals(6.0, test.mannWhitneyU(y, x), 0.0);
    }

    @Test
    public void testTiedValuesUseAverageRanks() {
        final MannWhitneyUTest test = new MannWhitneyUTest();

        final double[] x = {1.0, 2.0};
        final double[] y = {2.0, 3.0};

        assertEquals(3.5, test.mannWhitneyU(x, y), 0.0);
    }

    @Test
    public void testAsymptoticPValueMatchesNormalApproximation() {
        final MannWhitneyUTest test = new MannWhitneyUTest();

        final double[] x = {1.0, 3.0, 5.0};
        final double[] y = {2.0, 4.0, 6.0};

        final double uMin = 3.0;
        final double expected = 2.0 * new NormalDistribution(0.0, 1.0)
                .cumulativeProbability((uMin - 4.5) / Math.sqrt(5.25));

        assertEquals(expected, test.mannWhitneyUTest(x, y), 1.0e-12);
    }

    @Test(expected = NullArgumentException.class)
    public void testMannWhitneyURejectsNullSample() {
        new MannWhitneyUTest().mannWhitneyU(null, new double[] {1.0});
    }

    @Test(expected = NoDataException.class)
    public void testMannWhitneyUTestRejectsEmptySample() {
        new MannWhitneyUTest().mannWhitneyUTest(new double[0], new double[] {1.0});
    }

    @Test
    public void testDefaultFixedNaNStrategyLeavesNaNInStatistic() {
        final MannWhitneyUTest test = new MannWhitneyUTest();

        assertTrue(Double.isNaN(test.mannWhitneyU(
                new double[] {Double.NaN, 1.0},
                new double[] {2.0, 3.0})));
    }
}