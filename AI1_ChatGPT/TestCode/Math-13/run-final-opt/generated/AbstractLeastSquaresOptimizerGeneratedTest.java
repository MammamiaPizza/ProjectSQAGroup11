package org.apache.commons.math3.optimization.general;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableVectorFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.optimization.PointVectorValuePair;
import org.apache.commons.math3.optimization.fitting.PolynomialFitter;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AbstractLeastSquaresOptimizerGeneratedTest {

    private MultivariateDifferentiableVectorFunction twoParameterModel() {
        return new MultivariateDifferentiableVectorFunction() {
            public double[] value(double[] point) {
                return new double[] {
                    point[0] + 2.0 * point[1],
                    3.0 * point[0] - point[1],
                    point[0] + point[1]
                };
            }

            public DerivativeStructure[] value(DerivativeStructure[] point) {
                return new DerivativeStructure[] {
                    point[0].add(point[1].multiply(2.0)),
                    point[0].multiply(3.0).subtract(point[1]),
                    point[0].add(point[1])
                };
            }
        };
    }

    @Test
    public void testWeightedJacobianDrivesCovarianceAndEvaluationCount() {
        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();

        PointVectorValuePair result = optimizer.optimize(
                1000,
                twoParameterModel(),
                new double[] { 0.0, 7.0, 1.0 },
                new double[] { 4.0, 9.0, 16.0 },
                new double[] { 0.0, 0.0 });

        assertEquals(2.0, result.getPoint()[0], 1.0e-10);
        assertEquals(-1.0, result.getPoint()[1], 1.0e-10);
        assertTrue(optimizer.getJacobianEvaluations() > 0);

        RealMatrix root = optimizer.getWeightSquareRoot();
        assertEquals(2.0, root.getEntry(0, 0), 0.0);
        assertEquals(3.0, root.getEntry(1, 1), 0.0);
        assertEquals(4.0, root.getEntry(2, 2), 0.0);
        assertEquals(0.0, root.getEntry(0, 1), 0.0);

        int beforeCovariance = optimizer.getJacobianEvaluations();
        double[][] covariance = optimizer.getCovariances(1.0e-14);
        assertEquals(beforeCovariance + 1, optimizer.getJacobianEvaluations());

        assertEquals(41.0 / 4141.0, covariance[0][0], 1.0e-12);
        assertEquals(3.0 / 4141.0, covariance[0][1], 1.0e-12);
        assertEquals(3.0 / 4141.0, covariance[1][0], 1.0e-12);
        assertEquals(101.0 / 4141.0, covariance[1][1], 1.0e-12);
    }

    @Test
    public void testWeightedResidualCostChiSquareAndRms() {
        MultivariateDifferentiableVectorFunction constantModel =
                new MultivariateDifferentiableVectorFunction() {
                    public double[] value(double[] point) {
                        return new double[] { point[0], point[0] };
                    }

                    public DerivativeStructure[] value(DerivativeStructure[] point) {
                        return new DerivativeStructure[] { point[0], point[0] };
                    }
                };

        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        PointVectorValuePair result = optimizer.optimize(
                1000,
                constantModel,
                new double[] { 1.0, 3.0 },
                new double[] { 4.0, 9.0 },
                new double[] { 0.0 });

        assertEquals(31.0 / 13.0, result.getPoint()[0], 1.0e-10);
        assertEquals(12.0 / Math.sqrt(13.0), optimizer.getChiSquare() > 0.0
                ? Math.sqrt(optimizer.getChiSquare()) : 0.0, 1.0e-10);
        assertEquals(144.0 / 13.0, optimizer.getChiSquare(), 1.0e-10);
        assertEquals(Math.sqrt(72.0 / 13.0), optimizer.getRMS(), 1.0e-10);
    }

    @Test(expected = NumberIsTooSmallException.class)
    public void testGuessParameterErrorsRequiresDegreesOfFreedom() {
        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();

        optimizer.optimize(
                1000,
                new MultivariateDifferentiableVectorFunction() {
                    public double[] value(double[] point) {
                        return new double[] {
                            point[0] + point[1],
                            point[0] - point[1]
                        };
                    }

                    public DerivativeStructure[] value(DerivativeStructure[] point) {
                        return new DerivativeStructure[] {
                            point[0].add(point[1]),
                            point[0].subtract(point[1])
                        };
                    }
                },
                new double[] { 3.0, 1.0 },
                new double[] { 1.0, 1.0 },
                new double[] { 0.0, 0.0 });

        optimizer.guessParametersErrors();
    }

    @Test(expected = DimensionMismatchException.class)
    public void testDerivativeOutputDimensionMustMatchTargetDimension() {
        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();

        optimizer.optimize(
                100,
                new MultivariateDifferentiableVectorFunction() {
                    public double[] value(double[] point) {
                        return new double[] { point[0], point[0] };
                    }

                    public DerivativeStructure[] value(DerivativeStructure[] point) {
                        return new DerivativeStructure[] { point[0] };
                    }
                },
                new double[] { 1.0, 2.0 },
                new double[] { 1.0, 1.0 },
                new double[] { 0.0 });
    }

    @Test
    public void testLargePolynomialFitCompletesAndProducesFiniteCoefficients() {
        PolynomialFitter fitter =
                new PolynomialFitter(new LevenbergMarquardtOptimizer());

        for (int i = 0; i < 50000; i++) {
            double x = ((i % 1000) - 500) / 500.0;
            double y = 1.5 - 2.0 * x + 0.75 * x * x;
            fitter.addObservedPoint(x, y);
        }

        double[] coefficients = fitter.fit(new double[] { 0.0, 0.0, 0.0 });

        assertEquals(3, coefficients.length);
        assertFalse(Double.isNaN(coefficients[0]));
        assertFalse(Double.isNaN(coefficients[1]));
        assertFalse(Double.isNaN(coefficients[2]));
        assertEquals(1.5, coefficients[0], 1.0e-8);
        assertEquals(-2.0, coefficients[1], 1.0e-8);
        assertEquals(0.75, coefficients[2], 1.0e-8);
    }
}
