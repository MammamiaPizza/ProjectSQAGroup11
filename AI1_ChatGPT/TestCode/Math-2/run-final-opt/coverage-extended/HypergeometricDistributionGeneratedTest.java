package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HypergeometricDistributionGeneratedTest {

    private static final double EPS = 1e-12;

    @Test
    public void testValuesFarBelowNonzeroSupportAreHandledAsBoundaryValues() {
        HypergeometricDistribution distribution =
                new HypergeometricDistribution(10, 8, 5);

        assertEquals(0.0, distribution.probability(-50), 0.0);
        assertEquals(0.0, distribution.cumulativeProbability(-50), 0.0);
        assertEquals(1.0, distribution.upperCumulativeProbability(-50), 0.0);
    }

    @Test
    public void testSupportBoundsAndAdjacentCumulativeBoundaries() {
        HypergeometricDistribution distribution =
                new HypergeometricDistribution(10, 8, 5);

        assertEquals(3, distribution.getSupportLowerBound());
        assertEquals(5, distribution.getSupportUpperBound());

        assertEquals(0.0, distribution.cumulativeProbability(2), 0.0);
        assertEquals(1.0, distribution.upperCumulativeProbability(3), 0.0);
        assertEquals(1.0, distribution.cumulativeProbability(5), 0.0);
        assertEquals(0.0, distribution.upperCumulativeProbability(6), 0.0);
        assertEquals(0.0, distribution.probability(2), 0.0);
        assertEquals(0.0, distribution.probability(6), 0.0);
    }

    @Test
    public void testPointAndCumulativeProbabilitiesForKnownDistribution() {
        HypergeometricDistribution distribution =
                new HypergeometricDistribution(10, 8, 5);

        assertEquals(2.0 / 9.0, distribution.probability(3), EPS);
        assertEquals(5.0 / 9.0, distribution.probability(4), EPS);
        assertEquals(2.0 / 9.0, distribution.probability(5), EPS);

        assertEquals(2.0 / 9.0, distribution.cumulativeProbability(3), EPS);
        assertEquals(7.0 / 9.0, distribution.cumulativeProbability(4), EPS);
        assertEquals(2.0 / 9.0, distribution.upperCumulativeProbability(5), EPS);
        assertEquals(7.0 / 9.0, distribution.upperCumulativeProbability(4), EPS);
    }

    @Test
    public void testLowerAndUpperCumulativeProbabilitiesAreComplementary() {
        HypergeometricDistribution distribution =
                new HypergeometricDistribution(20, 12, 8);

        for (int x = distribution.getSupportLowerBound();
             x < distribution.getSupportUpperBound();
             x++) {
            assertEquals(1.0,
                    distribution.cumulativeProbability(x)
                            + distribution.upperCumulativeProbability(x + 1),
                    EPS);
        }
    }

    @Test
    public void testMeanVarianceAndConnectedSupport() {
        HypergeometricDistribution distribution =
                new HypergeometricDistribution(10, 8, 5);

        assertEquals(4.0, distribution.getNumericalMean(), 0.0);
        assertEquals(4.0 / 9.0, distribution.getNumericalVariance(), EPS);
        assertTrue(distribution.isSupportConnected());
    }

    @Test
    public void testZeroSampleHasSinglePointMassAtZero() {
        HypergeometricDistribution distribution =
                new HypergeometricDistribution(10, 7, 0);

        assertEquals(0, distribution.getSupportLowerBound());
        assertEquals(0, distribution.getSupportUpperBound());
        assertEquals(1.0, distribution.probability(0), 0.0);
        assertEquals(0.0, distribution.probability(1), 0.0);
        assertEquals(1.0, distribution.cumulativeProbability(0), 0.0);
        assertEquals(1.0, distribution.upperCumulativeProbability(0), 0.0);
        assertEquals(0.0, distribution.upperCumulativeProbability(1), 0.0);
    }

    @Test(expected = NotStrictlyPositiveException.class)
    public void testRejectsNonPositivePopulationSize() {
        new HypergeometricDistribution(0, 0, 0);
    }

    @Test(expected = NotPositiveException.class)
    public void testRejectsNegativeNumberOfSuccesses() {
        new HypergeometricDistribution(10, -1, 0);
    }

    @Test(expected = NotPositiveException.class)
    public void testRejectsNegativeSampleSize() {
        new HypergeometricDistribution(10, 5, -50);
    }

    @Test(expected = NumberIsTooLargeException.class)
    public void testRejectsMoreSuccessesThanPopulation() {
        new HypergeometricDistribution(10, 11, 1);
    }

    @Test(expected = NumberIsTooLargeException.class)
    public void testRejectsSampleLargerThanPopulation() {
        new HypergeometricDistribution(10, 5, 11);
    }

@Test
public void testNumericalVarianceIsReturnedConsistentlyAfterCalculation() {
    HypergeometricDistribution distribution = new HypergeometricDistribution(20, 7, 5);

    assertEquals(273.0 / 304.0, distribution.getNumericalVariance(), EPS);
    assertEquals(273.0 / 304.0, distribution.getNumericalVariance(), EPS);
}
}
