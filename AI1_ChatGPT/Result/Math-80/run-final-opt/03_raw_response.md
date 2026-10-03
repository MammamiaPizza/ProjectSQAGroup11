package org.apache.commons.math.linear;

import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EigenDecompositionImplGeneratedTest {

    @Test
    public void testDenseSymmetricMatrixHasExpectedEigenvaluesAndDecompositionIdentity()
        throws Exception {
        final double[][] data = {
            { 7.5, -2.5, -5.0,  1.0 },
            {-2.5,  7.5,  1.0, -5.0 },
            {-5.0,  1.0,  7.5, -2.5 },
            { 1.0, -5.0, -2.5,  7.5 }
        };

        final RealMatrix a = MatrixUtils.createRealMatrix(data);
        final EigenDecompositionImpl decomposition =
            new EigenDecompositionImpl(a, 0.0);

        final double[] actual = decomposition.getRealEigenvalues();
        Arrays.sort(actual);
        assertEquals(1.0, actual[0], 1.0e-9);
        assertEquals(4.0, actual[1], 1.0e-9);
        assertEquals(9.0, actual[2], 1.0e-9);
        assertEquals(16.0, actual[3], 1.0e-9);

        final RealMatrix v = decomposition.getV();
        final RealMatrix d = decomposition.getD();
        assertMatrixProductIdentity(a, v, d, 1.0e-8);
        assertOrthogonal(v, 1.0e-8);

        for (int i = 0; i < d.getRowDimension(); ++i) {
            assertEquals(decomposition.getRealEigenvalue(i), d.getEntry(i, i), 1.0e-10);
            for (int j = 0; j < d.getColumnDimension(); ++j) {
                if (i != j) {
                    assertEquals(0.0, d.getEntry(i, j), 0.0);
                }
            }
        }

        assertEquals(576.0, decomposition.getDeterminant(), 1.0e-7);
    }

    @Test
    public void testFiveByFiveShiftedTridiagonalSpectrumAndDeterminant()
        throws Exception {
        final int n = 5;
        final double diagonal = 24000.0;
        final double offDiagonal = 1000.0;
        final double[] main = new double[n];
        final double[] secondary = new double[n - 1];
        Arrays.fill(main, diagonal);
        Arrays.fill(secondary, offDiagonal);

        final EigenDecompositionImpl decomposition =
            new EigenDecompositionImpl(main, secondary, 0.0);

        final double[] actual = decomposition.getRealEigenvalues();
        Arrays.sort(actual);

        double expectedDeterminant = 1.0;
        for (int i = 0; i < n; ++i) {
            final double expected =
                diagonal + 2.0 * offDiagonal * Math.cos((n - i) * Math.PI / (n + 1.0));
            assertEquals(expected, actual[i], 1.0e-8 * Math.abs(expected));
            expectedDeterminant *= expected;
        }
        assertEquals(expectedDeterminant, decomposition.getDeterminant(),
                     1.0e-8 * Math.abs(expectedDeterminant));

        final RealMatrix v = decomposition.getV();
        final RealMatrix d = decomposition.getD();
        final RealMatrix a = tridiagonalMatrix(main, secondary);
        assertMatrixProductIdentity(a, v, d, 2.0e-7);
        assertOrthogonal(v, 2.0e-10);
    }

    @Test
    public void testTwoByTwoTridiagonalBlockUsesBothRoots()
        throws Exception {
        final EigenDecompositionImpl decomposition =
            new EigenDecompositionImpl(new double[] { 2.0, 2.0 },
                                       new double[] { 1.0 }, 0.0);

        final double[] values = decomposition.getRealEigenvalues();
        Arrays.sort(values);
        assertEquals(1.0, values[0], 1.0e-14);
        assertEquals(3.0, values[1], 1.0e-14);
        assertEquals(3.0, decomposition.getDeterminant(), 1.0e-14);

        final RealMatrix a = tridiagonalMatrix(new double[] { 2.0, 2.0 },
                                               new double[] { 1.0 });
        assertMatrixProductIdentity(a, decomposition.getV(), decomposition.getD(), 1.0e-12);
    }

    @Test
    public void testSingleElementTridiagonalDecomposition()
        throws Exception {
        final EigenDecompositionImpl decomposition =
            new EigenDecompositionImpl(new double[] { -42.5 }, new double[0], 0.0);

        assertEquals(-42.5, decomposition.getRealEigenvalue(0), 0.0);
        assertEquals(-42.5, decomposition.getD().getEntry(0, 0), 0.0);
        assertEquals(1.0, decomposition.getV().getEntry(0, 0), 0.0);
        assertEquals(1.0, decomposition.getVT().getEntry(0, 0), 0.0);
        assertEquals(-42.5, decomposition.getDeterminant(), 0.0);
    }

    @Test(expected = InvalidMatrixException.class)
    public void testAsymmetricMatrixIsRejected() throws Exception {
        new EigenDecompositionImpl(
            MatrixUtils.createRealMatrix(new double[][] {
                { 1.0, 2.0 },
                { 3.0, 4.0 }
            }),
            0.0);
    }

    @Test
    public void testSolverSolvesSymmetricSystemAndReportsNonSingular()
        throws Exception {
        final RealMatrix a = MatrixUtils.createRealMatrix(new double[][] {
            { 4.0, 1.0, 0.0 },
            { 1.0, 3.0, 1.0 },
            { 0.0, 1.0, 2.0 }
        });
        final EigenDecompositionImpl decomposition =
            new EigenDecompositionImpl(a, 0.0);

        final DecompositionSolver solver = decomposition.getSolver();
        assertTrue(solver.isNonSingular());

        final double[] solution = solver.solve(new double[] { 6.0, 10.0, 8.0 });
        assertEquals(1.0, solution[0], 1.0e-10);
        assertEquals(2.0, solution[1], 1.0e-10);
        assertEquals(3.0, solution[2], 1.0e-10);

        final EigenDecompositionImpl singular =
            new EigenDecompositionImpl(new double[] { 0.0, 2.0 },
                                       new double[] { 0.0 }, 0.0);
        assertFalse(singular.getSolver().isNonSingular());
    }

    private static RealMatrix tridiagonalMatrix(final double[] main, final double[] secondary) {
        final double[][] data = new double[main.length][main.length];
        for (int i = 0; i < main.length; ++i) {
            data[i][i] = main[i];
            if (i < secondary.length) {
                data[i][i + 1] = secondary[i];
                data[i + 1][i] = secondary[i];
            }
        }
        return MatrixUtils.createRealMatrix(data);
    }

    private static void assertMatrixProductIdentity(final RealMatrix a,
                                                    final RealMatrix v,
                                                    final RealMatrix d,
                                                    final double tolerance) {
        final int n = a.getRowDimension();
        for (int row = 0; row < n; ++row) {
            for (int column = 0; column < n; ++column) {
                double left = 0.0;
                double right = 0.0;
                for (int k = 0; k < n; ++k) {
                    left += a.getEntry(row, k) * v.getEntry(k, column);
                    right += v.getEntry(row, k) * d.getEntry(k, column);
                }
                assertEquals(left, right, tolerance * Math.max(1.0, Math.abs(left)));
            }
        }
    }

    private static void assertOrthogonal(final RealMatrix v, final double tolerance) {
        final int n = v.getRowDimension();
        for (int row = 0; row < n; ++row) {
            for (int column = 0; column < n; ++column) {
                double value = 0.0;
                for (int k = 0; k < n; ++k) {
                    value += v.getEntry(row, k) * v.getEntry(column, k);
                }
                assertEquals(row == column ? 1.0 : 0.0, value, tolerance);
            }
        }
    }
}