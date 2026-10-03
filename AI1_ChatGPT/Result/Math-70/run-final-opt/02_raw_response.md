package org.apache.commons.math.analysis.solvers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.junit.Test;

public class BisectionSolverMath369Test {

    private UnivariateRealFunction linearFunction(final double root) {
        return new UnivariateRealFunction() {
            public double value(double x) {
                return x - root;
            }
        };
    }

    @Test
    public void solveWithFunctionArgumentUsesTheSuppliedFunction() throws Exception {
        final int[] evaluations = new int[1];
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) {
                evaluations[0]++;
                return x - 2.0;
            }
        };

        BisectionSolver solver = new BisectionSolver();
        double result = solver.solve(function, 0.0, 4.0);

        assertEquals(2.0, result, 1.0e-6);
        assertTrue(evaluations[0] > 0);
    }

    @Test
    public void solveWithFunctionArgumentAndInitialValueFindsRoot() throws Exception {
        BisectionSolver solver = new BisectionSolver();

        double result = solver.solve(linearFunction(0.25), 0.0, 1.0, 0.75);

        assertEquals(0.25, result, 1.0e-6);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void deprecatedFunctionConstructorSupportsSolveWithoutInitialValue() throws Exception {
        BisectionSolver solver = new BisectionSolver(linearFunction(3.0));

        double result = solver.solve(1.0, 5.0);

        assertEquals(3.0, result, 1.0e-6);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void deprecatedFunctionConstructorSupportsSolveWithInitialValue() throws Exception {
        BisectionSolver solver = new BisectionSolver(linearFunction(-1.0));

        double result = solver.solve(-2.0, 2.0, 0.0);

        assertEquals(-1.0, result, 1.0e-6);
    }

    @Test(expected = IllegalArgumentException.class)
    public void solveWithFunctionArgumentRejectsNonIncreasingInterval() throws Exception {
        BisectionSolver solver = new BisectionSolver();

        solver.solve(linearFunction(1.0), 1.0, 1.0);
    }
}