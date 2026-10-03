package org.apache.commons.math.distribution;

import org.apache.commons.math.exception.NotStrictlyPositiveException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class NormalDistributionImplExtremeValuesTest {

    @Test
    public void cumulativeProbabilityReturnsZeroForNegativeMaximumFiniteValue() throws Exception {
        NormalDistributionImpl distribution = new NormalDistributionImpl();

        assertEquals(0.0, distribution.cumulativeProbability(-Double.MAX_VALUE), 0.0);
    }

    @Test
    public void cumulativeProbabilityReturnsOneForPositiveMaximumFiniteValue() throws Exception {
        NormalDistributionImpl distribution = new NormalDistributionImpl();

        assertEquals(1.0, distribution.cumulativeProbability(Double.MAX_VALUE), 0.0);
    }

    @Test
    public void cumulativeProbabilityHandlesInfiniteArguments() throws Exception {
        NormalDistributionImpl distribution = new NormalDistributionImpl();

        assertEquals(0.0, distribution.cumulativeProbability(Double.NEGATIVE_INFINITY), 0.0);
        assertEquals(1.0, distribution.cumulativeProbability(Double.POSITIVE_INFINITY), 0.0);
    }

    @Test
    public void cumulativeProbabilityHandlesOverflowingDeviationFromLargeMean() throws Exception {
        NormalDistributionImpl positiveMean = new NormalDistributionImpl(Double.MAX_VALUE, 1.0);
        NormalDistributionImpl negativeMean = new NormalDistributionImpl(-Double.MAX_VALUE, 1.0);

        assertEquals(0.0, positiveMean.cumulativeProbability(-Double.MAX_VALUE), 0.0);
        assertEquals(1.0, negativeMean.cumulativeProbability(Double.MAX_VALUE), 0.0);
    }

    @Test
    public void cumulativeProbabilityAtMeanIsOneHalfForLargeFiniteMean() throws Exception {
        NormalDistributionImpl distribution = new NormalDistributionImpl(Double.MAX_VALUE, 1.0);

        assertEquals(0.5, distribution.cumulativeProbability(Double.MAX_VALUE), 0.0);
    }

    @Test
    public void cumulativeProbabilityHasExpectedOrdinaryValues() throws Exception {
        NormalDistributionImpl distribution = new NormalDistributionImpl();

        assertEquals(0.5, distribution.cumulativeProbability(0.0), 1.0e-15);
        assertEquals(0.8413447460685429, distribution.cumulativeProbability(1.0), 1.0e-12);
        assertEquals(0.15865525393145707, distribution.cumulativeProbability(-1.0), 1.0e-12);
    }

    @Test
    public void cumulativeProbabilityIsFiniteForExtremeFiniteValues() throws Exception {
        NormalDistributionImpl distribution = new NormalDistributionImpl();

        assertFalse(Double.isNaN(distribution.cumulativeProbability(-Double.MAX_VALUE)));
        assertFalse(Double.isNaN(distribution.cumulativeProbability(Double.MAX_VALUE)));
    }

    @Test(expected = NotStrictlyPositiveException.class)
    public void constructorRejectsZeroStandardDeviation() {
        new NormalDistributionImpl(0.0, 0.0);
    }

    @Test(expected = NotStrictlyPositiveException.class)
    public void constructorRejectsNegativeStandardDeviation() {
        new NormalDistributionImpl(0.0, -1.0);
    }
}