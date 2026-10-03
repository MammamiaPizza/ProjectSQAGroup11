package org.apache.commons.math.optimization.general;

import static org.junit.Assert.assertEquals;

import org.apache.commons.math.analysis.DifferentiableMultivariateVectorialFunction;
import org.apache.commons.math.analysis.MultivariateMatrixFunction;
import org.junit.Test;

public class LevenbergMarquardtOptimizerMATH362Test {

    @Test
    public void testJennrichSampsonConvergesToEstablishedRms() throws Exception {
        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();

        optimizer.optimize(jennrichSampsonFunction(),
                           new double[10],
                           unitWeights(10),
                           new double[] { 0.3, 0.4 });

        assertEquals(0.2578330049, optimizer.getRMS(), 1.0e-9);
    }

    @Test
    public void testFreudensteinRothConvergesToEstablishedRms() throws Exception {
        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();

        optimizer.optimize(freudensteinRothFunction(),
                           new double[] { 0.0, 0.0 },
                           unitWeights(2),
                           new double[] { 0.5, -2.0 });

        assertEquals(11.4121122022341, optimizer.getRMS(), 1.0e-9);
    }

    private static double[] unitWeights(int size) {
        double[] weights = new double[size];
        for (int i = 0; i < size; ++i) {
            weights[i] = 1.0;
        }
        return weights;
    }

    private static DifferentiableMultivariateVectorialFunction jennrichSampsonFunction() {
        return new DifferentiableMultivariateVectorialFunction() {
            public double[] value(double[] point) {
                double[] values = new double[10];
                for (int i = 0; i < values.length; ++i) {
                    values[i] = 2.0 + 2.0 * i
                              - Math.exp(i * point[0])
                              - Math.exp(i * point[1]);
                }
                return values;
            }

            public MultivariateMatrixFunction jacobian() {
                return new MultivariateMatrixFunction() {
                    public double[][] value(double[] point) {
                        double[][] jacobian = new double[10][2];
                        for (int i = 0; i < jacobian.length; ++i) {
                            jacobian[i][0] = -i * Math.exp(i * point[0]);
                            jacobian[i][1] = -i * Math.exp(i * point[1]);
                        }
                        return jacobian;
                    }
                };
            }
        };
    }

    private static DifferentiableMultivariateVectorialFunction freudensteinRothFunction() {
        return new DifferentiableMultivariateVectorialFunction() {
            public double[] value(double[] point) {
                double x = point[0];
                double y = point[1];
                return new double[] {
                    -13.0 + x + ((5.0 - y) * y - 2.0) * y,
                    -29.0 + x + ((1.0 + y) * y - 14.0) * y
                };
            }

            public MultivariateMatrixFunction jacobian() {
                return new MultivariateMatrixFunction() {
                    public double[][] value(double[] point) {
                        double y = point[1];
                        return new double[][] {
                            { 1.0, 10.0 * y - 3.0 * y * y - 2.0 },
                            { 1.0, 3.0 * y * y + 2.0 * y - 14.0 }
                        };
                    }
                };
            }
        };
    }
}