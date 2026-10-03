package org.apache.commons.math.analysis.solvers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.junit.Test;

public class UnivariateRealSolverUtilsTest {

    @Test
    public void testBracketAcceptsExactZeroAtUpperEndpoint() throws Exception {
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) {
                return x - 2.0;
            }
        };

        double[] bracket = UnivariateRealSolverUtils.bracket(function, 1.0, 0.0, 2.0, 10);

        assertEquals(0.0, bracket[0], 0.0);
        assertEquals(2.0, bracket[1], 0.0);
        assertEquals(0.0, function.value(bracket[1]), 0.0);
    }

    @Test
    public void testBracketAcceptsExactZeroAtLowerEndpoint() throws Exception {
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) {
                return x;
            }
        };

        double[] bracket = UnivariateRealSolverUtils.bracket(function, 1.0, 0.0, 2.0, 10);

        assertEquals(0.0, bracket[0], 0.0);
        assertEquals(2.0, bracket[1], 0.0);
        assertEquals(0.0, function.value(bracket[0]), 0.0);
    }

    @Test
    public void testBracketFindsOrdinarySignChangingInterval() throws Exception {
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) {
                return x - 0.5;
            }
        };

        double[] bracket = UnivariateRealSolverUtils.bracket(function, 1.0, 0.0, 2.0, 10);

        assertEquals(0.0, bracket[0], 0.0);
        assertEquals(2.0, bracket[1], 0.0);
        assertTrue(function.value(bracket[0]) * function.value(bracket[1]) < 0.0);
    }

    @Test
    public void testSolveReturnsExactUpperEndpointRootAcrossLargeInterval() throws Exception {
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) {
                return x / Double.MAX_VALUE - 1.0;
            }
        };

        double root = UnivariateRealSolverUtils.solve(function, 0.0, Double.MAX_VALUE);

        assertEquals(Double.MAX_VALUE, root, 0.0);
        assertEquals(0.0, function.value(root), 0.0);
    }

    @Test
    public void testSolveReturnsExactLowerEndpointRoot() throws Exception {
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) {
                return x;
            }
        };

        double root = UnivariateRealSolverUtils.solve(function, 0.0, 2.0);

        assertEquals(0.0, root, 0.0);
    }

    @Test
    public void testSolveWithSpecifiedAccuracyFindsInteriorRoot() throws Exception {
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) {
                return x - 1.5;
            }
        };

        double root = UnivariateRealSolverUtils.solve(function, 0.0, 3.0, 1.0e-8);

        assertEquals(1.5, root, 1.0e-8);
        assertEquals(0.0, function.value(root), 1.0e-8);
    }

    @Test(expected = ConvergenceException.class)
    public void testBracketThrowsWhenNoRootCanBeBracketed() throws Exception {
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) {
                return 1.0;
            }
        };

        UnivariateRealSolverUtils.bracket(function, 1.0, 0.0, 2.0, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBracketRejectsNonPositiveMaximumIterations() throws Exception {
        UnivariateRealSolverUtils.bracket(new UnivariateRealFunction() {
            public double value(double x) {
                return x;
            }
        }, 1.0, 0.0, 2.0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSolveRejectsNullFunction() throws Exception {
        UnivariateRealSolverUtils.solve(null, 0.0, 1.0);
    }

    @Test
    public void testMidpointOfFiniteValues() {
        assertEquals(3.0, UnivariateRealSolverUtils.midpoint(2.0, 4.0), 0.0);
    }
}
