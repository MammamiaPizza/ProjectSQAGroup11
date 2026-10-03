package org.apache.commons.math.distribution;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PoissonDistributionImplContractTest {

    @Test
    public void testPositiveMeanIsRetained() {
        PoissonDistributionImpl distribution = new PoissonDistributionImpl(3.5);

        assertEquals(3.5, distribution.getMean(), 0.0);
    }

    @Test
    public void testConfiguredConstructorRetainsPositiveMean() {
        PoissonDistributionImpl distribution = new PoissonDistributionImpl(12.0, 1.0e-9, 100);

        assertEquals(12.0, distribution.getMean(), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroMeanIsRejected() {
        new PoissonDistributionImpl(0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeMeanIsRejected() {
        new PoissonDistributionImpl(-1.0);
    }

    @Test
    public void testNaNMeanIsRetained() {
        PoissonDistributionImpl distribution = new PoissonDistributionImpl(Double.NaN);

        assertEquals(Double.NaN, distribution.getMean(), 0.0);
    }
}