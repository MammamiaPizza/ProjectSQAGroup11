package org.apache.commons.math.linear;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SingularValueDecompositionImplMath320Test {

    private static final double EPS = 1.0e-10;

    @Test
    public void testTallRankDeficientArraySolveHasZeroResidual() {
        RealMatrix a = new Array2DRowRealMatrix(new double[][] {
            { 1.0, 2.0 },
            { 3.0, 6.0 },
            { 4.0, 8.0 }
        });
        double[] b = new double[] { 3.0, 9.0, 12.0 };

        double[] x = new SingularValueDecompositionImpl(a).getSolver().solve(b);

        assertArrayEquals(b, a.operate(x), EPS);
        assertEquals(0.6, x[0], EPS);
        assertEquals(1.2, x[1], EPS);
    }

    @Test
    public void testWideRankDeficientArraySolveHasZeroResidual() {
        RealMatrix a = new Array2DRowRealMatrix(new double[][] {
            { 1.0, 3.0, 5.0 },
            { 2.0, 6.0, 10.0 }
        });
        double[] b = new double[] { 4.0, 8.0 };

        double[] x = new SingularValueDecompositionImpl(a).getSolver().solve(b);

        assertArrayEquals(b, a.operate(x), EPS);
        assertArrayEquals(new double[] {
            4.0 / 35.0, 12.0 / 35.0, 20.0 / 35.0
        }, x, EPS);
    }

    @Test
    public void testTallRankDeficientVectorSolveHasZeroResidual() {
        RealMatrix a = new Array2DRowRealMatrix(new double[][] {
            { 1.0, 2.0 },
            { 3.0, 6.0 },
            { 4.0, 8.0 }
        });
        RealVector b = new ArrayRealVector(new double[] { 2.0, 6.0, 8.0 });

        RealVector x = new SingularValueDecompositionImpl(a).getSolver().solve(b);

        assertArrayEquals(b.getData(), a.operate(x).getData(), EPS);
    }

    @Test
    public void testWideRankDeficientMatrixSolveHasZeroResidualForEveryColumn() {
        RealMatrix a = new Array2DRowRealMatrix(new double[][] {
            { 1.0, 3.0, 5.0 },
            { 2.0, 6.0, 10.0 }
        });
        RealMatrix b = new Array2DRowRealMatrix(new double[][] {
            { 4.0, -2.0 },
            { 8.0, -4.0 }
        });

        RealMatrix x = new SingularValueDecompositionImpl(a).getSolver().solve(b);

        RealMatrix reconstructed = a.multiply(x);
        for (int row = 0; row < b.getRowDimension(); row++) {
            for (int column = 0; column < b.getColumnDimension(); column++) {
                assertEquals(b.getEntry(row, column),
                             reconstructed.getEntry(row, column), EPS);
            }
        }
    }

    @Test
    public void testInverseOfTallRankDeficientMatrixSatisfiesPenroseReconstruction() {
        RealMatrix a = new Array2DRowRealMatrix(new double[][] {
            { 1.0, 2.0 },
            { 3.0, 6.0 },
            { 4.0, 8.0 }
        });

        RealMatrix inverse = new SingularValueDecompositionImpl(a).getSolver().getInverse();
        RealMatrix reconstructed = a.multiply(inverse).multiply(a);

        for (int row = 0; row < a.getRowDimension(); row++) {
            for (int column = 0; column < a.getColumnDimension(); column++) {
                assertEquals(a.getEntry(row, column),
                             reconstructed.getEntry(row, column), EPS);
            }
        }
    }

    @Test
    public void testFullRankSolveAndInverseRemainConsistent() {
        RealMatrix a = new Array2DRowRealMatrix(new double[][] {
            { 2.0, 1.0 },
            { 1.0, 3.0 }
        });
        DecompositionSolver solver = new SingularValueDecompositionImpl(a).getSolver();

        assertTrue(solver.isNonSingular());

        double[] x = solver.solve(new double[] { 5.0, 7.0 });
        assertArrayEquals(new double[] { 1.6, 1.8 }, x, EPS);

        RealMatrix identity = a.multiply(solver.getInverse());
        assertEquals(1.0, identity.getEntry(0, 0), EPS);
        assertEquals(0.0, identity.getEntry(0, 1), EPS);
        assertEquals(0.0, identity.getEntry(1, 0), EPS);
        assertEquals(1.0, identity.getEntry(1, 1), EPS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSolveRejectsArrayWithWrongDimension() {
        RealMatrix a = new Array2DRowRealMatrix(new double[][] {
            { 1.0, 0.0 },
            { 0.0, 1.0 }
        });

        new SingularValueDecompositionImpl(a).getSolver().solve(new double[] { 1.0 });
    }

    @Test
    public void testRankDeficientSolverIsReportedSingular() {
        RealMatrix a = new Array2DRowRealMatrix(new double[][] {
            { 1.0, 2.0 },
            { 3.0, 6.0 },
            { 4.0, 8.0 }
        });

        assertFalse(new SingularValueDecompositionImpl(a).getSolver().isNonSingular());
    }
}