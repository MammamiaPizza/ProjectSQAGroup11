package org.apache.commons.math.optimization.general;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.DifferentiableMultivariateVectorialFunction;
import org.apache.commons.math.analysis.MultivariateMatrixFunction;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.SimpleVectorialValueChecker;
import org.apache.commons.math.optimization.VectorialConvergenceChecker;
import org.apache.commons.math.optimization.VectorialPointValuePair;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AbstractLeastSquaresOptimizerTest {

    @Test
    public void testWeightedChiSquareRmsCovarianceAndParameterError() throws Exception {
        OneStepOptimizer optimizer = new OneStepOptimizer();

        optimizer.optimize(twoEqualValuesFunction(),
                           new double[] { 1.0, 3.0 },
                           new double[] { 4.0, 1.0 },
                           new double[] { 2.0 });

        assertEquals(Math.sqrt(2.5), optimizer.getRMS(), 1.0e-14);
        assertEquals(5.0, optimizer.getChiSquare(), 1.0e-14);

        double[][] covariance = optimizer.getCovariances();
        assertEquals(1, covariance.length);
        assertEquals(1, covariance[0].length);
        assertEquals(0.2, covariance[0][0], 1.0e-14);

        assertArrayEquals(new double[] { 1.0 },
                          optimizer.guessParametersErrors(), 1.0e-14);
    }

    @Test
    public void testCovarianceUsesWeightedJacobianAndCountsJacobianEvaluation()
        throws Exception {
        OneStepOptimizer optimizer = new OneStepOptimizer();

        optimizer.optimize(twoEqualValuesFunction(),
                           new double[] { 1.0, 3.0 },
                           new double[] { 4.0, 1.0 },
                           new double[] { 2.0 });

        assertEquals(1, optimizer.getEvaluations());
        assertEquals(0, optimizer.getJacobianEvaluations());

        assertEquals(0.2, optimizer.getCovariances()[0][0], 1.0e-14);
        assertEquals(1, optimizer.getJacobianEvaluations());

        optimizer.optimize(twoEqualValuesFunction(),
                           new double[] { 1.0, 3.0 },
                           new double[] { 4.0, 1.0 },
                           new double[] { 2.0 });

        assertEquals(1, optimizer.getEvaluations());
        assertEquals(0, optimizer.getJacobianEvaluations());
    }

    @Test
    public void testGuessParametersErrorsRejectsNoDegreesOfFreedom() throws Exception {
        OneStepOptimizer optimizer = new OneStepOptimizer();

        optimizer.optimize(oneValueFunction(),
                           new double[] { 1.0 },
                           new double[] { 1.0 },
                           new double[] { 2.0 });

        try {
            optimizer.guessParametersErrors();
            fail("rows equal to columns must not provide parameter-error estimates");
        } catch (OptimizationException expected) {
            assertNotNull(expected);
        }
    }

    @Test(expected = OptimizationException.class)
    public void testOptimizeRejectsDifferentTargetAndWeightLengths() throws Exception {
        new OneStepOptimizer().optimize(twoEqualValuesFunction(),
                                        new double[] { 1.0 },
                                        new double[] { 1.0, 1.0 },
                                        new double[] { 0.0 });
    }

    @Test
    public void testConfigurationAndConvergenceCheckerAccessors() {
        OneStepOptimizer optimizer = new OneStepOptimizer();
        VectorialConvergenceChecker checker = new SimpleVectorialValueChecker();

        optimizer.setMaxIterations(17);
        optimizer.setMaxEvaluations(23);
        optimizer.setConvergenceChecker(checker);

        assertEquals(17, optimizer.getMaxIterations());
        assertEquals(23, optimizer.getMaxEvaluations());
        assertSame(checker, optimizer.getConvergenceChecker());
    }

    @Test
    public void testEvaluationLimitIsEnforcedDuringResidualUpdate() throws Exception {
        OneStepOptimizer optimizer = new OneStepOptimizer();
        optimizer.setMaxEvaluations(0);

        try {
            optimizer.optimize(twoEqualValuesFunction(),
                               new double[] { 1.0, 3.0 },
                               new double[] { 1.0, 1.0 },
                               new double[] { 2.0 });
            fail("an evaluation beyond the configured limit must fail");
        } catch (IllegalStateException expected) {
            assertTrue(expected.getCause() instanceof FunctionEvaluationException);
        }

        assertEquals(1, optimizer.getEvaluations());
    }

    @Test
    public void testIterationLimitIsEnforcedAndCounterIsUpdated() throws Exception {
        IteratingOptimizer optimizer = new IteratingOptimizer();
        optimizer.setMaxIterations(0);

        try {
            optimizer.optimize(twoEqualValuesFunction(),
                               new double[] { 1.0, 3.0 },
                               new double[] { 1.0, 1.0 },
                               new double[] { 2.0 });
            fail("an iteration beyond the configured limit must fail");
        } catch (IllegalStateException expected) {
            assertTrue(expected.getCause() instanceof OptimizationException);
        }

        assertEquals(1, optimizer.getIterations());
    }

    private static DifferentiableMultivariateVectorialFunction twoEqualValuesFunction() {
        return new DifferentiableMultivariateVectorialFunction() {
            public double[] value(double[] point) {
                return new double[] { point[0], point[0] };
            }

            public MultivariateMatrixFunction jacobian() {
                return new MultivariateMatrixFunction() {
                    public double[][] value(double[] point) {
                        return new double[][] { { 1.0 }, { 1.0 } };
                    }
                };
            }
        };
    }

    private static DifferentiableMultivariateVectorialFunction oneValueFunction() {
        return new DifferentiableMultivariateVectorialFunction() {
            public double[] value(double[] point) {
                return new double[] { point[0] };
            }

            public MultivariateMatrixFunction jacobian() {
                return new MultivariateMatrixFunction() {
                    public double[][] value(double[] point) {
                        return new double[][] { { 1.0 } };
                    }
                };
            }
        };
    }

    private static class OneStepOptimizer extends AbstractLeastSquaresOptimizer {
        protected VectorialPointValuePair doOptimize() {
            try {
                updateResidualsAndCost();
                return new VectorialPointValuePair(point, objective);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private static class IteratingOptimizer extends AbstractLeastSquaresOptimizer {
        protected VectorialPointValuePair doOptimize() {
            try {
                updateResidualsAndCost();
                incrementIterationsCounter();
                return new VectorialPointValuePair(point, objective);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }
}