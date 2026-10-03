package org.apache.commons.math3.linear;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class RectangularCholeskyDecompositionGeneratedTest {

    @Test
    public void testPivotSelectionRetainsLargeRemainingDiagonalComponent() {
        RealMatrix matrix = MatrixUtils.createRealMatrix(new double[][] {
            { 0.01, 0.0, 0.0 },
            { 0.0, 100.0, 0.0 },
            { 0.0, 0.0, 50.0 }
        });

        RectangularCholeskyDecomposition decomposition =
                new RectangularCholeskyDecomposition(matrix, 0.1);

        assertEquals(2, decomposition.getRank());
        RealMatrix reconstructed = reconstruction(decomposition);
        assertEquals(0.0, reconstructed.getEntry(0, 0), 1.0e-12);
        assertEquals(100.0, reconstructed.getEntry(1, 1), 1.0e-12);
        assertEquals(50.0, reconstructed.getEntry(2, 2), 1.0e-12);
        assertEquals(0.0, reconstructed.getEntry(1, 2), 1.0e-12);
    }

    @Test
    public void testRankDeficientMatrixHasOnlyIndependentRootColumns() {
        RealMatrix matrix = MatrixUtils.createRealMatrix(new double[][] {
            { 4.0, 0.0, 0.0 },
            { 0.0, 0.0, 0.0 },
            { 0.0, 0.0, 0.0 }
        });

        RectangularCholeskyDecomposition decomposition =
                new RectangularCholeskyDecomposition(matrix, 1.0e-12);

        assertEquals(1, decomposition.getRank());
        assertEquals(1, decomposition.getRootMatrix().getColumnDimension());
        assertMatrixEquals(matrix, reconstruction(decomposition), 1.0e-12);
    }

    @Test
    public void testFullRankPositiveDefiniteMatrixReconstructsInput() {
        RealMatrix matrix = MatrixUtils.createRealMatrix(new double[][] {
            { 4.0, 2.0, -4.0 },
            { 2.0, 10.0, -0.5 },
            { -4.0, -0.5, 5.25 }
        });

        RectangularCholeskyDecomposition decomposition =
                new RectangularCholeskyDecomposition(matrix, 1.0e-12);

        assertEquals(3, decomposition.getRank());
        assertEquals(3, decomposition.getRootMatrix().getColumnDimension());
        assertMatrixEquals(matrix, reconstruction(decomposition), 1.0e-10);
    }

    @Test
    public void testDiagonalExactlyAtThresholdIsRetained() {
        RealMatrix matrix = MatrixUtils.createRealMatrix(new double[][] {
            { 4.0, 0.0 },
            { 0.0, 0.1 }
        });

        RectangularCholeskyDecomposition decomposition =
                new RectangularCholeskyDecomposition(matrix, 0.1);

        assertEquals(2, decomposition.getRank());
        assertMatrixEquals(matrix, reconstruction(decomposition), 1.0e-12);
    }

    @Test
    public void testNegativeDiagonalIsRejected() {
        RealMatrix matrix = MatrixUtils.createRealMatrix(new double[][] {
            { -1.0 }
        });

        try {
            new RectangularCholeskyDecomposition(matrix, 1.0e-12);
            fail("A matrix with a negative diagonal entry must not be decomposed");
        } catch (NonPositiveDefiniteMatrixException expected) {
            assertEquals(0, expected.getRow());
        }
    }

    private RealMatrix reconstruction(RectangularCholeskyDecomposition decomposition) {
        RealMatrix root = decomposition.getRootMatrix();
        return root.multiply(root.transpose());
    }

    private void assertMatrixEquals(RealMatrix expected, RealMatrix actual, double tolerance) {
        assertEquals(expected.getRowDimension(), actual.getRowDimension());
        assertEquals(expected.getColumnDimension(), actual.getColumnDimension());
        for (int row = 0; row < expected.getRowDimension(); row++) {
            for (int column = 0; column < expected.getColumnDimension(); column++) {
                assertEquals(expected.getEntry(row, column),
                             actual.getEntry(row, column),
                             tolerance);
            }
        }
    }
}
