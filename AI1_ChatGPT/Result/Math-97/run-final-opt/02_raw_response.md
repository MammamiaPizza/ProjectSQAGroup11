package org.apache.commons.math.analysis;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BrentSolverEndpointTest {

    @Test
    public void testUpperEndpointWithSinPiIsAcceptedAsRoot() throws Exception {
        BrentSolver solver = new BrentSolver(new UnivariateRealFunction() {
            public double value(double x) {
                return Math.sin(x);
            }
        });

        assertEquals(Math.PI, solver.solve(3.0, Math.PI), 0.0);
    }

    @Test
    public void testExactLowerEndpointRootIsReturned() throws Exception {
        BrentSolver solver = new BrentSolver(new UnivariateRealFunction() {
            public double value(double x) {
                return x - 2.0;
            }
        });

        assertEquals(2.0, solver.solve(2.0, 4.0), 0.0);
    }

    @Test
    public void testExactUpperEndpointRootIsReturned() throws Exception {
        BrentSolver solver = new BrentSolver(new UnivariateRealFunction() {
            public double value(double x) {
                return x - 4.0;
            }
        });

        assertEquals(4.0, solver.solve(2.0, 4.0), 0.0);
    }

    @Test
    public void testInitialGuessVariantReturnsLowerEndpointRoot() throws Exception {
        BrentSolver solver = new BrentSolver(new UnivariateRealFunction() {
            public double value(double x) {
                return x - 2.0;
            }
        });

        assertEquals(2.0, solver.solve(2.0, 4.0, 3.0), 0.0);
    }

    @Test
    public void testInitialGuessVariantReturnsUpperEndpointRoot() throws Exception {
        BrentSolver solver = new BrentSolver(new UnivariateRealFunction() {
            public double value(double x) {
                return x - 4.0;
            }
        });

        assertEquals(4.0, solver.solve(2.0, 4.0, 3.0), 0.0);
    }

    @Test
    public void testInteriorRootInOppositeSignBracket() throws Exception {
        BrentSolver solver = new BrentSolver(new UnivariateRealFunction() {
            public double value(double x) {
                return x * x - 2.0;
            }
        });

        assertEquals(Math.sqrt(2.0), solver.solve(0.0, 2.0), 1.0e-5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSameSignEndpointsWithoutRootAreRejected() throws Exception {
        BrentSolver solver = new BrentSolver(new UnivariateRealFunction() {
            public double value(double x) {
                return x * x + 1.0;
            }
        });

        solver.solve(-1.0, 1.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitialGuessOutsideIntervalIsRejected() throws Exception {
        BrentSolver solver = new BrentSolver(new UnivariateRealFunction() {
            public double value(double x) {
                return x - 1.0;
            }
        });

        solver.solve(0.0, 2.0, 3.0);
    }
}