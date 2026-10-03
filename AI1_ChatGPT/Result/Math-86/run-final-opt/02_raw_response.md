package org.apache.commons.math.linear;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CholeskyDecompositionImplGeneratedTest {

    @Test(expected = NotPositiveDefiniteMatrixException.class)
    public void testRejectsMatrixWhoseSecondPivotBecomesNegative() {
        RealMatrix matrix = new RealMatrixImpl(new double[][] {
            { 1.0, 2.0 },
            { 2.0, 1.0 }
        });

        new CholeskyDecompositionImpl(matrix);
    }

    @Test(expected = NotPositiveDefiniteMatrixException.class)
    public void testRejectsMatrixWhoseSecondPivotBecomesZero() {
        RealMatrix matrix = new RealMatrixImpl(new double[][] {
            { 1.0, 1.0 },
            { 1.0, 1.0 }
        });

        new CholeskyDecompositionImpl(matrix);
    }

    @Test(expected = NotPositiveDefiniteMatrixException.class)
    public void testRejectsMatrixWithNonPositiveInitialDiagonal() {
        RealMatrix matrix = new RealMatrixImpl(new double[][] {
            { 2.0, 0.0 },
            { 0.0, -1.0 }
        });

        new CholeskyDecompositionImpl(matrix);
    }

    @Test(expected = NotSymmetricMatrixException.class)
    public void testRejectsNonSymmetricMatrix() {
        RealMatrix matrix = new RealMatrixImpl(new double[][] {
            { 2.0, 1.0 },
            { 0.0, 2.0 }
        });

        new CholeskyDecompositionImpl(matrix);
    }

    @Test(expected = NonSquareMatrixException.class)
    public void testRejectsNonSquareMatrix() {
        RealMatrix matrix = new RealMatrixImpl(new double[][] {
            { 1.0, 2.0, 3.0 },
            { 4.0, 5.0, 6.0 }
        });

        new CholeskyDecompositionImpl(matrix);
    }

    @Test
    public void testPositiveDefiniteMatrixProducesFactorDeterminantAndSolution() {
        RealMatrix matrix = new RealMatrixImpl(new double[][] {
            { 4.0, 2.0 },
            { 2.0, 3.0 }
        });

        CholeskyDecompositionImpl decomposition =
            new CholeskyDecompositionImpl(matrix);

        RealMatrix l = decomposition.getL();
        RealMatrix lt = decomposition.getLT();

        assertEquals(2.0, l.getEntry(0, 0), 1.0e-15);
        assertEquals(0.0, l.getEntry(0, 1), 1.0e-15);
        assertEquals(1.0, l.getEntry(1, 0), 1.0e-15);
        assertEquals(Math.sqrt(2.0), l.getEntry(1, 1), 1.0e-15);
        assertEquals(1.0, lt.getEntry(0, 1), 1.0e-15);
        assertEquals(8.0, decomposition.getDeterminant(), 1.0e-14);

        DecompositionSolver solver = decomposition.getSolver();
        assertTrue(solver.isNonSingular());
        assertArrayEquals(new double[] { 1.0, 2.0 },
                          solver.solve(new double[] { 8.0, 8.0 }),
                          1.0e-14);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSolverRejectsVectorWithWrongDimension() {
        CholeskyDecompositionImpl decomposition =
            new CholeskyDecompositionImpl(new RealMatrixImpl(new double[][] {
                { 4.0, 2.0 },
                { 2.0, 3.0 }
            }));

        decomposition.getSolver().solve(new double[] { 1.0 });
    }
}