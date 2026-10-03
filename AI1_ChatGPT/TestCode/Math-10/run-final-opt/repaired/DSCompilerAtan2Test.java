package org.apache.commons.math3.analysis.differentiation;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.junit.Assert;
import org.junit.Test;

public class DSCompilerAtan2Test {

    @Test
    public void testAtan2OfPositiveZerosIsZero() {
        DerivativeStructure y = new DerivativeStructure(1, 1, 0, 0.0);
        DerivativeStructure x = new DerivativeStructure(1, 1, 0, 0.0);

        double value = DerivativeStructure.atan2(y, x).getValue();

        Assert.assertEquals(0.0, value, 0.0);
        Assert.assertEquals(Double.doubleToLongBits(0.0),
                            Double.doubleToLongBits(value));
    }

    @Test
    public void testAtan2SignedZeroQuadrants() {
        DerivativeStructure positiveZero = new DerivativeStructure(1, 1, 0, 0.0);
        DerivativeStructure negativeZero = new DerivativeStructure(1, 1, 0, -0.0);

        Assert.assertEquals(Math.PI, DerivativeStructure.atan2(positiveZero, negativeZero).getValue(), 0.0);
        Assert.assertEquals(-Math.PI, DerivativeStructure.atan2(negativeZero, negativeZero).getValue(), 0.0);

        double negativeZeroResult = DerivativeStructure.atan2(negativeZero, positiveZero).getValue();
        Assert.assertEquals(Double.doubleToLongBits(-0.0),
                            Double.doubleToLongBits(negativeZeroResult));
    }

    @Test
    public void testAtan2InfiniteArgumentsUsesCorrectQuadrants() {
        DerivativeStructure positiveInfinity =
            new DerivativeStructure(1, 1, 0, Double.POSITIVE_INFINITY);
        DerivativeStructure negativeInfinity =
            new DerivativeStructure(1, 1, 0, Double.NEGATIVE_INFINITY);

        Assert.assertEquals(Math.PI / 4.0,
                            DerivativeStructure.atan2(positiveInfinity, positiveInfinity).getValue(), 0.0);
        Assert.assertEquals(3.0 * Math.PI / 4.0,
                            DerivativeStructure.atan2(positiveInfinity, negativeInfinity).getValue(), 0.0);
        Assert.assertEquals(-Math.PI / 4.0,
                            DerivativeStructure.atan2(negativeInfinity, positiveInfinity).getValue(), 0.0);
        Assert.assertEquals(-3.0 * Math.PI / 4.0,
                            DerivativeStructure.atan2(negativeInfinity, negativeInfinity).getValue(), 0.0);
    }

    @Test
    public void testAtan2ComputesFiniteValueAndFirstDerivatives() {
        DerivativeStructure y = new DerivativeStructure(2, 1, 0, 1.0);
        DerivativeStructure x = new DerivativeStructure(2, 1, 1, 1.0);

        DerivativeStructure angle = DerivativeStructure.atan2(y, x);

        Assert.assertEquals(Math.PI / 4.0, angle.getValue(), 1.0e-15);
        Assert.assertEquals(0.5, angle.getPartialDerivative(1, 0), 1.0e-15);
        Assert.assertEquals(-0.5, angle.getPartialDerivative(0, 1), 1.0e-15);
    }

    @Test
    public void testAtan2OnPositiveYAxisHasExpectedDerivatives() {
        DerivativeStructure y = new DerivativeStructure(2, 1, 0, 1.0);
        DerivativeStructure x = new DerivativeStructure(2, 1, 1, 0.0);

        DerivativeStructure angle = DerivativeStructure.atan2(y, x);

        Assert.assertEquals(Math.PI / 2.0, angle.getValue(), 1.0e-15);
        Assert.assertEquals(0.0, angle.getPartialDerivative(1, 0), 1.0e-15);
        Assert.assertEquals(-1.0, angle.getPartialDerivative(0, 1), 1.0e-15);
    }

    @Test(expected = DimensionMismatchException.class)
    public void testAtan2RejectsIncompatibleDerivativeStructures() {
        DerivativeStructure y = new DerivativeStructure(1, 1, 0, 1.0);
        DerivativeStructure x = new DerivativeStructure(2, 1, 0, 1.0);

        DerivativeStructure.atan2(y, x);
    }
}
