package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DistributionSupportBoundaryContractTest {

    @Test
    public void fDistributionLowerSupportBoundIsExclusive() {
        FDistribution distribution = new FDistribution(5.0, 10.0);

        assertFalse(distribution.isSupportLowerBoundInclusive());
    }

    @Test
    public void fDistributionLowerSupportInclusivityDoesNotDependOnDegreesOfFreedom() {
        assertFalse(new FDistribution(1.0, 1.0).isSupportLowerBoundInclusive());
        assertFalse(new FDistribution(20.0, 3.0).isSupportLowerBoundInclusive());
    }

    @Test
    public void fDistributionHasZeroCdfAtAndBelowExclusiveLowerBound() {
        FDistribution distribution = new FDistribution(4.0, 6.0);

        assertEquals(0.0, distribution.cumulativeProbability(0.0), 0.0);
        assertEquals(0.0, distribution.cumulativeProbability(-1.0), 0.0);
    }

    @Test(expected = NotStrictlyPositiveException.class)
    public void fDistributionRejectsNonPositiveNumeratorDegreesOfFreedom() {
        new FDistribution(0.0, 2.0);
    }

    @Test
    public void uniformDistributionUpperSupportBoundIsInclusive() {
        UniformRealDistribution distribution = new UniformRealDistribution(-2.5, 7.5);

        assertTrue(distribution.isSupportUpperBoundInclusive());
    }

    @Test
    public void uniformDistributionUpperSupportInclusivityDoesNotDependOnBounds() {
        assertTrue(new UniformRealDistribution(0.0, 1.0).isSupportUpperBoundInclusive());
        assertTrue(new UniformRealDistribution(-100.0, -99.0).isSupportUpperBoundInclusive());
    }

    @Test
    public void uniformDistributionIncludesUpperBoundInDensityAndCdf() {
        UniformRealDistribution distribution = new UniformRealDistribution(2.0, 6.0);

        assertEquals(0.25, distribution.density(6.0), 0.0);
        assertEquals(1.0, distribution.cumulativeProbability(6.0), 0.0);
    }

    @Test(expected = NumberIsTooLargeException.class)
    public void uniformDistributionRejectsEqualBounds() {
        new UniformRealDistribution(3.0, 3.0);
    }
}
