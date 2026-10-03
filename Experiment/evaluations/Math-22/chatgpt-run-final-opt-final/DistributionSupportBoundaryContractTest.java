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

@org.junit.Test(expected = org.apache.commons.math3.exception.NotStrictlyPositiveException.class)
public void fDistributionRejectsNonPositiveDenominatorDegreesOfFreedom() {
    new org.apache.commons.math3.distribution.FDistribution(1.0, 0.0);
}

@org.junit.Test
public void fDistributionComputesPositiveDensityCdfAndVariance() {
    org.apache.commons.math3.distribution.FDistribution distribution =
            new org.apache.commons.math3.distribution.FDistribution(2.0, 4.0);

    org.junit.Assert.assertEquals(8.0 / 27.0, distribution.density(1.0), 1e-14);
    org.junit.Assert.assertEquals(5.0 / 9.0, distribution.cumulativeProbability(1.0), 1e-14);

    org.apache.commons.math3.distribution.FDistribution finiteVariance =
            new org.apache.commons.math3.distribution.FDistribution(6.0, 8.0);
    org.junit.Assert.assertEquals(4.0 / 3.0, finiteVariance.getNumericalMean(), 1e-14);
    org.junit.Assert.assertEquals(16.0 / 9.0, finiteVariance.getNumericalVariance(), 1e-14);
    org.junit.Assert.assertTrue(Double.isNaN(distribution.getNumericalVariance()));
}

@org.junit.Test
public void uniformDistributionLowerSupportBoundIsInclusive() {
    org.apache.commons.math3.distribution.UniformRealDistribution distribution =
            new org.apache.commons.math3.distribution.UniformRealDistribution(-2.0, 6.0);

    org.junit.Assert.assertTrue(distribution.isSupportLowerBoundInclusive());
    org.junit.Assert.assertEquals(-2.0, distribution.getSupportLowerBound(), 0.0);
    org.junit.Assert.assertEquals(0.125, distribution.density(-2.0), 0.0);
    org.junit.Assert.assertEquals(0.0, distribution.density(-2.1), 0.0);
    org.junit.Assert.assertEquals(0.0, distribution.cumulativeProbability(-2.0), 0.0);
    org.junit.Assert.assertEquals(0.5, distribution.cumulativeProbability(2.0), 0.0);
}

@org.junit.Test
public void uniformDistributionReportsMomentsAndConnectedSupport() {
    org.apache.commons.math3.distribution.UniformRealDistribution distribution =
            new org.apache.commons.math3.distribution.UniformRealDistribution(-2.0, 6.0);

    org.junit.Assert.assertEquals(6.0, distribution.getSupportUpperBound(), 0.0);
    org.junit.Assert.assertEquals(2.0, distribution.getNumericalMean(), 0.0);
    org.junit.Assert.assertEquals(16.0 / 3.0, distribution.getNumericalVariance(), 0.0);
    org.junit.Assert.assertTrue(distribution.isSupportConnected());
}
}
