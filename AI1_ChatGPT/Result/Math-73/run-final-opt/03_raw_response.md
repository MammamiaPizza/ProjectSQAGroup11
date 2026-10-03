package org.apache.commons.math.analysis.solvers;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BrentSolverBug73Test {

    @Test(expected = IllegalArgumentException.class)
    public void solveRejectsEndpointsThatDoNotBracketARoot() throws Exception {
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) throws FunctionEvaluationException {
                return x * x + 1.0;
            }
        };

        new BrentSolver(function).solve(-1.0, 1.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void solveWithInitialRejectsWhenAllThreeValuesHaveSameSign() throws Exception {
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) throws FunctionEvaluationException {
                return x * x + 1.0;
            }
        };

        new BrentSolver(function).solve(-1.0, 1.0, 0.0);
    }

    @Test
    public void solveReturnsAnEndpointWhenItIsARoot() throws Exception {
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) throws FunctionEvaluationException {
                return x - 2.0;
            }
        };

        double root = new BrentSolver(function).solve(2.0, 4.0);

        assertEquals(2.0, root, 0.0);
    }

    @Test
    public void solveWithInitialReturnsInitialPointWhenItIsARoot() throws Exception {
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) throws FunctionEvaluationException {
                return x - 3.0;
            }
        };

        double root = new BrentSolver(function).solve(2.0, 4.0, 3.0);

        assertEquals(3.0, root, 0.0);
    }

    @Test
    public void solveWithInitialAllowsEndpointsWithSameSignWhenInitialBracketsRoot()
        throws Exception {
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) throws FunctionEvaluationException {
                return x * x - 1.0;
            }
        };

        double root = new BrentSolver(function).solve(-2.0, 2.0, 0.0);

        assertTrue(Math.abs(function.value(root)) <= 1.0e-6);
    }
}