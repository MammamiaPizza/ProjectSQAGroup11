import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.optim.BaseOptimizer;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.optim.SimpleValueChecker;
import org.apache.commons.math3.optim.SimpleVectorValueChecker;
import org.apache.commons.math3.optim.Target;
import org.apache.commons.math3.optim.Weight;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.ObjectiveFunctionGradient;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.CMAESOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.PowellOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import org.apache.commons.math3.optim.nonlinear.vector.ModelFunction;
import org.apache.commons.math3.optim.nonlinear.vector.ModelFunctionJacobian;
import org.apache.commons.math3.optim.nonlinear.vector.jacobian.GaussNewtonOptimizer;
import org.apache.commons.math3.optim.nonlinear.vector.jacobian.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.random.Well19937c;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Math6IterationAccountingTest {

    @Test
    public void testBaseOptimizerResetsCountersAndRetainsConfiguredLimits() {
        CountingOptimizer optimizer = new CountingOptimizer();

        assertEquals("done", optimizer.optimize(new MaxEval(7), new MaxIter(5)));
        assertEquals(1, optimizer.getEvaluations());
        assertEquals(1, optimizer.getIterations());
        assertEquals(7, optimizer.getMaxEvaluations());
        assertEquals(5, optimizer.getMaxIterations());

        assertEquals("done", optimizer.optimize());
        assertEquals(1, optimizer.getEvaluations());
        assertEquals(1, optimizer.getIterations());
        assertEquals(7, optimizer.getMaxEvaluations());
        assertEquals(5, optimizer.getMaxIterations());
    }

    @Test
    public void testConjugateGradientFindsQuadraticMinimumAndCountsIterations() {
        NonLinearConjugateGradientOptimizer optimizer =
                new NonLinearConjugateGradientOptimizer(
                        NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE,
                        new SimpleValueChecker(1e-12, 1e-12));

        PointValuePair result = optimizer.optimize(
                new MaxEval(1000),
                new MaxIter(100),
                new ObjectiveFunction(new MultivariateFunction() {
                    public double value(double[] point) {
                        double d = point[0] - 3.0;
                        return d * d;
                    }
                }),
                new ObjectiveFunctionGradient(new MultivariateVectorFunction() {
                    public double[] value(double[] point) {
                        return new double[] { 2.0 * (point[0] - 3.0) };
                    }
                }),
                GoalType.MINIMIZE,
                new InitialGuess(new double[] { 10.0 }));

        assertEquals(3.0, result.getPoint()[0], 1e-8);
        assertEquals(0.0, result.getValue(), 1e-12);
        assertTrue("a conjugate-gradient step must be recorded", optimizer.getIterations() > 0);
    }

    @Test
    public void testCmaesRecordsGenerations() {
        CMAESOptimizer optimizer = new CMAESOptimizer(
                30, 0.0, true, 0, 0, new Well19937c(123456789L), false,
                new ConvergenceChecker<PointValuePair>() {
                    public boolean converged(int iteration,
                                             PointValuePair previous,
                                             PointValuePair current) {
                        return iteration >= 2;
                    }
                });

        PointValuePair result = optimizer.optimize(
                new MaxEval(5000),
                new MaxIter(30),
                new ObjectiveFunction(new MultivariateFunction() {
                    public double value(double[] point) {
                        return point[0] * point[0] + point[1] * point[1];
                    }
                }),
                GoalType.MINIMIZE,
                new InitialGuess(new double[] { 0.5, -0.5 }),
                new CMAESOptimizer.Sigma(new double[] { 0.2, 0.2 }),
                new CMAESOptimizer.PopulationSize(6));

        assertEquals(2, result.getPoint().length);
        assertFalse(Double.isNaN(result.getValue()));
        assertTrue(result.getValue() >= 0.0);
        assertTrue("each CMA-ES generation must update BaseOptimizer's counter",
                   optimizer.getIterations() > 0);
    }

    @Test
    public void testPowellFindsMinimumAndCountsIterations() {
        PowellOptimizer optimizer = new PowellOptimizer(1e-10, 1e-10);

        PointValuePair result = optimizer.optimize(
                new MaxEval(5000),
                new MaxIter(100),
                new ObjectiveFunction(new MultivariateFunction() {
                    public double value(double[] point) {
                        double x = point[0] - 2.0;
                        double y = point[1] + 1.0;
                        return x * x + y * y;
                    }
                }),
                GoalType.MINIMIZE,
                new InitialGuess(new double[] { 8.0, -6.0 }));

        assertEquals(2.0, result.getPoint()[0], 1e-6);
        assertEquals(-1.0, result.getPoint()[1], 1e-6);
        assertEquals(0.0, result.getValue(), 1e-10);
        assertTrue("Powell direction cycles must be counted", optimizer.getIterations() > 0);
    }

    @Test
    public void testSimplexFindsMinimumAndCountsIterations() {
        SimplexOptimizer optimizer = new SimplexOptimizer(1e-10, 1e-10);

        PointValuePair result = optimizer.optimize(
                new MaxEval(5000),
                new MaxIter(1000),
                new ObjectiveFunction(new MultivariateFunction() {
                    public double value(double[] point) {
                        double x = point[0] - 2.0;
                        double y = point[1] + 1.0;
                        return x * x + y * y;
                    }
                }),
                GoalType.MINIMIZE,
                new InitialGuess(new double[] { 5.0, 4.0 }),
                new NelderMeadSimplex(new double[] { 1.0, 1.0 }));

        assertEquals(2.0, result.getPoint()[0], 1e-5);
        assertEquals(-1.0, result.getPoint()[1], 1e-5);
        assertEquals(0.0, result.getValue(), 1e-9);
        assertTrue("simplex transformations must be counted", optimizer.getIterations() > 0);
    }

    @Test
    public void testGaussNewtonFindsLinearLeastSquaresSolutionAndCountsIterations() {
        GaussNewtonOptimizer optimizer = new GaussNewtonOptimizer(
                true, new SimpleVectorValueChecker(1e-12, 1e-12));

        PointVectorValuePair result = optimizer.optimize(
                new MaxEval(1000),
                new MaxIter(100),
                new Target(new double[] { 2.0, 4.0 }),
                new Weight(new double[] { 1.0, 1.0 }),
                new InitialGuess(new double[] { 0.0, 0.0 }),
                new ModelFunction(new MultivariateVectorFunction() {
                    public double[] value(double[] point) {
                        return new double[] {
                            point[0] + point[1],
                            point[0] - point[1]
                        };
                    }
                }),
                new ModelFunctionJacobian(new MultivariateMatrixFunction() {
                    public double[][] value(double[] point) {
                        return new double[][] {
                            { 1.0, 1.0 },
                            { 1.0, -1.0 }
                        };
                    }
                }));

        assertEquals(3.0, result.getPoint()[0], 1e-12);
        assertEquals(-1.0, result.getPoint()[1], 1e-12);
        assertTrue("Gauss-Newton iterations must update BaseOptimizer's counter",
                   optimizer.getIterations() > 0);
    }

    @Test
    public void testLevenbergMarquardtFindsLinearLeastSquaresSolutionAndCountsIterations() {
        LevenbergMarquardtOptimizer optimizer =
                new LevenbergMarquardtOptimizer(new SimpleVectorValueChecker(1e-12, 1e-12));

        PointVectorValuePair result = optimizer.optimize(
                new MaxEval(1000),
                new MaxIter(100),
                new Target(new double[] { 2.0, 4.0 }),
                new Weight(new double[] { 1.0, 1.0 }),
                new InitialGuess(new double[] { 0.0, 0.0 }),
                new ModelFunction(new MultivariateVectorFunction() {
                    public double[] value(double[] point) {
                        return new double[] {
                            point[0] + point[1],
                            point[0] - point[1]
                        };
                    }
                }),
                new ModelFunctionJacobian(new MultivariateMatrixFunction() {
                    public double[][] value(double[] point) {
                        return new double[][] {
                            { 1.0, 1.0 },
                            { 1.0, -1.0 }
                        };
                    }
                }));

        assertEquals(3.0, result.getPoint()[0], 1e-10);
        assertEquals(-1.0, result.getPoint()[1], 1e-10);
        assertTrue("Levenberg-Marquardt iterations must update BaseOptimizer's counter",
                   optimizer.getIterations() > 0);
    }

    private static class CountingOptimizer extends BaseOptimizer<String> {
        CountingOptimizer() {
            super(null);
        }

        @Override
        protected String doOptimize() {
            incrementEvaluationCount();
            incrementIterationCount();
            return "done";
        }
    }
}