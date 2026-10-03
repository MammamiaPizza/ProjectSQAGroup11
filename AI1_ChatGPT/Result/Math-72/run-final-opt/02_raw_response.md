package org.apache.commons.math.analysis.solvers;

import static org.junit.Assert.assertEquals;

import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.junit.Test;

public class BrentSolverEndpointContractTest {

    @Test
    public void deprecatedSolveWithInitialReturnsMinCoordinateWhenMinIsNumericallyZero() throws Exception {
        UnivariateRealFunction sine = new UnivariateRealFunction() {
            public double value(double x) {
                return Math.sin(x);
            }
        };

        BrentSolver solver = new BrentSolver(sine);

        assertEquals(Math.PI, solver.solve(Math.PI, 2.0 * Math.PI, 1.5 * Math.PI), 0.0);
    }

    @Test
    public void solveWithInitialReturnsExactNonzeroMinEndpointRatherThanFunctionValue() throws Exception {
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) {
                return x - 2.0;
            }
        };

        BrentSolver solver = new BrentSolver();

        assertEquals(2.0, solver.solve(function, 2.0, 5.0, 3.0), 0.0);
    }

    @Test
    public void solveWithInitialReturnsExactNonzeroMaxEndpointRatherThanFunctionValue() throws Exception {
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) {
                return x - 5.0;
            }
        };

        BrentSolver solver = new BrentSolver();

        assertEquals(5.0, solver.solve(function, 2.0, 5.0, 3.0), 0.0);
    }

    @Test
    public void solveWithoutInitialSelectsMinWhenBothEndpointsAreRoots() throws Exception {
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) {
                return (x - 2.0) * (x - 5.0);
            }
        };

        BrentSolver solver = new BrentSolver();

        assertEquals(2.0, solver.solve(function, 2.0, 5.0), 0.0);
    }

    @Test
    public void solveFindsBracketedInteriorRoot() throws Exception {
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) {
                return x - 2.0;
            }
        };

        BrentSolver solver = new BrentSolver();

        assertEquals(2.0, solver.solve(function, 0.0, 5.0), 1.0e-5);
    }

    @Test
    public void solveWithInitialReturnsInitialWhenItIsARoot() throws Exception {
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) {
                return x - 3.0;
            }
        };

        BrentSolver solver = new BrentSolver();

        assertEquals(3.0, solver.solve(function, 0.0, 5.0, 3.0), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void solveRejectsIntervalThatDoesNotBracketARoot() throws Exception {
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) {
                return x * x + 1.0;
            }
        };

        new BrentSolver().solve(function, -2.0, 2.0);
    }
}