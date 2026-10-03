package org.apache.commons.math.linear;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EigenDecompositionImplMath308Test {

    private static final double EPS = 1.0e-9;

    @Test
    public void testOneRowBlockProducesUsableDecomposition() {
        EigenDecompositionImpl decomposition =
            new EigenDecompositionImpl(new double[] { 7.5 }, new double[0], 0.0);

        assertEquals(7.5, decomposition.getRealEigenvalue(0), 0.0);
        assertEquals(7.5, decomposition.getDeterminant(), 0.0);
        assertEquals(0.0, decomposition.getImagEigenvalue(0), 0.0);
        assertEigenDecomposition(MatrixUtils.createRealMatrix(new double[][] { { 7.5 } }),
                                 decomposition);
    }

    @Test
    public void testTwoRowBlockProducesCorrectInvariantValues() {
        RealMatrix matrix = MatrixUtils.createRealMatrix(new double[][] {
            { 2.0, 1.0 },
            { 1.0, 2.0 }
        });

        EigenDecompositionImpl decomposition =
            new EigenDecompositionImpl(new double[] { 2.0, 2.0 }, new double[] { 1.0 }, 0.0);

        double[] eigenvalues = decomposition.getRealEigenvalues();
        assertEquals(4.0, eigenvalues[0] + eigenvalues[1], EPS);
        assertEquals(3.0, eigenvalues[0] * eigenvalues[1], EPS);
        assertEquals(3.0, decomposition.getDeterminant(), EPS);
        assertEigenDecomposition(matrix, decomposition);
    }

    @Test
    public void testThreeRowBlockProducesOrthogonalEigenvectors() {
        RealMatrix matrix = tridiagonal(
            new double[] { 4.0, 5.0, 6.0 },
            new double[] { 0.5, 0.75 });

        EigenDecompositionImpl decomposition =
            new EigenDecompositionImpl(new double[] { 4.0, 5.0, 6.0 },
                                       new double[] { 0.5, 0.75 }, 0.0);

        assertEquals(15.0, sum(decomposition.getRealEigenvalues()), EPS);
        assertEigenDecomposition(matrix, decomposition);
    }

    @Test
    public void testGeneralTridiagonalBlockDecomposesWithoutIndexFailure() {
        double[] main = { 1.0, 2.0, 4.0, 7.0, 11.0 };
        double[] secondary = { 0.25, 0.5, 0.75, 0.125 };
        RealMatrix matrix = tridiagonal(main, secondary);

        EigenDecompositionImpl decomposition =
            new EigenDecompositionImpl(main, secondary, 0.0);

        assertEquals(25.0, sum(decomposition.getRealEigenvalues()), EPS);
        assertEigenDecomposition(matrix, decomposition);
    }

    @Test
    public void testSplitAndDeflationBoundariesWithMixedBlockSizes() {
        double[] main = { 1.0, 3.0, 5.0, 8.0, 10.0, 13.0, 17.0, 19.0 };
        double[] secondary = { 0.0, 0.5, 0.0, 0.75, 0.25, 0.0, 0.9 };
        RealMatrix matrix = tridiagonal(main, secondary);

        EigenDecompositionImpl decomposition =
            new EigenDecompositionImpl(main, secondary, 0.0);

        assertEquals(76.0, sum(decomposition.getRealEigenvalues()), EPS);
        assertEigenDecomposition(matrix, decomposition);
    }

    @Test
    public void testSymmetricMatrixTransformationAndSolver() {
        RealMatrix matrix = MatrixUtils.createRealMatrix(new double[][] {
            { 6.0, 2.0, 1.0, 0.0 },
            { 2.0, 5.0, 2.0, 1.0 },
            { 1.0, 2.0, 4.0, 1.0 },
            { 0.0, 1.0, 1.0, 3.0 }
        });

        EigenDecompositionImpl decomposition = new EigenDecompositionImpl(matrix, 0.0);
        assertEigenDecomposition(matrix, decomposition);

        double[] rightHandSide = { 1.0, -2.0, 3.0, 4.0 };
        double[] solution = decomposition.getSolver().solve(rightHandSide);
        for (int row = 0; row < rightHandSide.length; row++) {
            double value = 0.0;
            for (int column = 0; column < rightHandSide.length; column++) {
                value += matrix.getEntry(row, column) * solution[column];
            }
            assertEquals(rightHandSide[row], value, EPS);
        }
    }

    @Test(expected = InvalidMatrixException.class)
    public void testAsymmetricMatricesAreRejected() {
        RealMatrix asymmetric = MatrixUtils.createRealMatrix(new double[][] {
            { 1.0, 2.0 },
            { 3.0, 4.0 }
        });

        new EigenDecompositionImpl(asymmetric, 0.0);
    }

    private static RealMatrix tridiagonal(double[] main, double[] secondary) {
        double[][] data = new double[main.length][main.length];
        for (int i = 0; i < main.length; i++) {
            data[i][i] = main[i];
            if (i < secondary.length) {
                data[i][i + 1] = secondary[i];
                data[i + 1][i] = secondary[i];
            }
        }
        return MatrixUtils.createRealMatrix(data);
    }

    private static double sum(double[] values) {
        double sum = 0.0;
        for (double value : values) {
            sum += value;
        }
        return sum;
    }

    private static void assertEigenDecomposition(RealMatrix matrix,
                                                  EigenDecompositionImpl decomposition) {
        RealMatrix v = decomposition.getV();
        RealMatrix d = decomposition.getD();
        RealMatrix vt = decomposition.getVT();

        assertEquals(matrix.getRowDimension(), v.getRowDimension());
        assertEquals(matrix.getColumnDimension(), v.getColumnDimension());

        RealMatrix left = matrix.multiply(v);
        RealMatrix right = v.multiply(d);
        assertMatrixEquals(left, right, EPS);

        RealMatrix identity = v.transpose().multiply(v);
        for (int row = 0; row < identity.getRowDimension(); row++) {
            for (int column = 0; column < identity.getColumnDimension(); column++) {
                assertEquals(row == column ? 1.0 : 0.0, identity.getEntry(row, column), EPS);
                assertEquals(v.transpose().getEntry(row, column), vt.getEntry(row, column), EPS);
            }
        }

        for (double imaginary : decomposition.getImagEigenvalues()) {
            assertEquals(0.0, imaginary, 0.0);
        }
    }

    private static void assertMatrixEquals(RealMatrix expected, RealMatrix actual, double tolerance) {
        assertEquals(expected.getRowDimension(), actual.getRowDimension());
        assertEquals(expected.getColumnDimension(), actual.getColumnDimension());
        for (int row = 0; row < expected.getRowDimension(); row++) {
            for (int column = 0; column < expected.getColumnDimension(); column++) {
                assertTrue("matrix entries differ at (" + row + "," + column + ")",
                           Math.abs(expected.getEntry(row, column) - actual.getEntry(row, column))
                           <= tolerance);
            }
        }
    }
}