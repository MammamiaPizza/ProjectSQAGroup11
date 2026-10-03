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

@org.junit.Test
public void testAcosWithOrderZero() {
    final org.apache.commons.math3.analysis.differentiation.DSCompiler compiler =
            org.apache.commons.math3.analysis.differentiation.DSCompiler.getCompiler(1, 0);
    final double[] operand = new double[] { 0.5 };
    final double[] result = new double[1];

    compiler.acos(operand, 0, result, 0);

    org.junit.Assert.assertEquals(java.lang.Math.PI / 3.0, result[0], 1.0e-15);
}

@org.junit.Test
public void testAcosCompositionDerivativesThroughFourthOrder() {
    final org.apache.commons.math3.analysis.differentiation.DSCompiler compiler =
            org.apache.commons.math3.analysis.differentiation.DSCompiler.getCompiler(1, 4);
    final double[] operand = new double[] { 9.0, 0.5, 1.0, 0.0, 0.0, 0.0 };
    final double[] result = new double[7];

    compiler.acos(operand, 1, result, 2);

    final double x = 0.5;
    final double q = 1.0 - x * x;
    final double sqrtQ = java.lang.Math.sqrt(q);
    org.junit.Assert.assertEquals(java.lang.Math.PI / 3.0, result[2], 1.0e-15);
    org.junit.Assert.assertEquals(-1.0 / sqrtQ, result[3], 1.0e-15);
    org.junit.Assert.assertEquals(-x / (q * sqrtQ), result[4], 1.0e-15);
    org.junit.Assert.assertEquals(-(1.0 + 2.0 * x * x) / (q * q * sqrtQ), result[5], 1.0e-15);
    org.junit.Assert.assertEquals(-x * (9.0 + 6.0 * x * x) / (q * q * q * sqrtQ),
                                  result[6], 1.0e-14);
}

@org.junit.Test
public void testAtan2OnPositiveXAxisHasExpectedDerivatives() {
    final org.apache.commons.math3.analysis.differentiation.DerivativeStructure y =
            new org.apache.commons.math3.analysis.differentiation.DerivativeStructure(2, 1, 0, 0.0);
    final org.apache.commons.math3.analysis.differentiation.DerivativeStructure x =
            new org.apache.commons.math3.analysis.differentiation.DerivativeStructure(2, 1, 1, 2.0);

    final org.apache.commons.math3.analysis.differentiation.DerivativeStructure angle =
            org.apache.commons.math3.analysis.differentiation.DerivativeStructure.atan2(y, x);

    org.junit.Assert.assertEquals(0.0, angle.getValue(), 0.0);
    org.junit.Assert.assertEquals(0.5, angle.getPartialDerivative(1, 0), 1.0e-15);
    org.junit.Assert.assertEquals(0.0, angle.getPartialDerivative(0, 1), 1.0e-15);
}
}
