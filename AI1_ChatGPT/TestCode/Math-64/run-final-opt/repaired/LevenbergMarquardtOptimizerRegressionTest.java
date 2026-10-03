package org.apache.commons.math.optimization.general;

import static org.junit.Assert.assertEquals;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.DifferentiableMultivariateVectorialFunction;
import org.apache.commons.math.analysis.MultivariateMatrixFunction;
import org.apache.commons.math.optimization.VectorialPointValuePair;
import org.junit.Test;

public class LevenbergMarquardtOptimizerRegressionTest {

    @Test
    public void testJennrichSampsonConvergesToExpectedCost() throws Exception {
        DifferentiableMultivariateVectorialFunction function =
                new DifferentiableMultivariateVectorialFunction() {
            public double[] value(double[] point) throws FunctionEvaluationException {
                double[] value = new double[10];
                for (int i = 1; i <= 10; ++i) {
                    value[i - 1] = 2.0 + 2.0 * i
                            - Math.exp(i * point[0])
                            - Math.exp(i * point[1]);
                }
                return value;
            }

            public MultivariateMatrixFunction jacobian() {
                return new MultivariateMatrixFunction() {
                    public double[][] value(double[] point) throws FunctionEvaluationException {
                        double[][] value = new double[10][2];
                        for (int i = 1; i <= 10; ++i) {
                            value[i - 1][0] = -i * Math.exp(i * point[0]);
                            value[i - 1][1] = -i * Math.exp(i * point[1]);
                        }
                        return value;
                    }
                };
            }
        };

        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        optimizer.optimize(function, new double[10], unitWeights(10),
                           new double[] { 0.3, 0.4 });

        assertEquals(0.2578199266368004, optimizer.getCost(), 1.0e-8);
    }

    @Test
    public void testFreudensteinRothConvergesToExpectedLocalMinimum() throws Exception {
        DifferentiableMultivariateVectorialFunction function =
                new DifferentiableMultivariateVectorialFunction() {
            public double[] value(double[] point) throws FunctionEvaluationException {
                double x = point[0];
                double y = point[1];
                return new double[] {
                    -13.0 + x + ((5.0 - y) * y - 2.0) * y,
                    -29.0 + x + ((1.0 + y) * y - 14.0) * y
                };
            }

            public MultivariateMatrixFunction jacobian() {
                return new MultivariateMatrixFunction() {
                    public double[][] value(double[] point) throws FunctionEvaluationException {
                        double y = point[1];
                        return new double[][] {
                            { 1.0, 10.0 * y - 3.0 * y * y - 2.0 },
                            { 1.0, 3.0 * y * y + 2.0 * y - 14.0 }
                        };
                    }
                };
            }
        };

        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        VectorialPointValuePair result =
                optimizer.optimize(function, new double[] { 0.0, 0.0 },
                                   new double[] { 1.0, 1.0 },
                                   new double[] { 0.5, -2.0 });

        assertEquals(11.41300466147456, result.getPoint()[0], 1.0e-8);
    }

    @Test
    public void testExactLinearLeastSquaresProblemFindsZeroResidualSolution() throws Exception {
        DifferentiableMultivariateVectorialFunction function =
                new DifferentiableMultivariateVectorialFunction() {
            public double[] value(double[] point) throws FunctionEvaluationException {
                return new double[] {
                    point[0] + point[1] - 3.0,
                    2.0 * point[0] - point[1]
                };
            }

            public MultivariateMatrixFunction jacobian() {
                return new MultivariateMatrixFunction() {
                    public double[][] value(double[] point) throws FunctionEvaluationException {
                        return new double[][] {
                            { 1.0, 1.0 },
                            { 2.0, -1.0 }
                        };
                    }
                };
            }
        };

        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        VectorialPointValuePair result =
                optimizer.optimize(function, new double[] { 0.0, 0.0 },
                                   new double[] { 1.0, 1.0 },
                                   new double[] { 0.0, 0.0 });

        assertEquals(1.0, result.getPoint()[0], 1.0e-12);
        assertEquals(2.0, result.getPoint()[1], 1.0e-12);
        assertEquals(0.0, optimizer.getCost(), 1.0e-12);
    }

    private static double[] unitWeights(int size) {
        double[] weights = new double[size];
        for (int i = 0; i < size; ++i) {
            weights[i] = 1.0;
        }
        return weights;
    }
}
