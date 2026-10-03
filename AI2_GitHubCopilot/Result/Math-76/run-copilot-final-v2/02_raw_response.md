package org.apache.commons.math.linear;

 import static org.junit.Assert.*;

 import org.apache.commons.math.exception.MathArithmeticException;
 import org.apache.commons.math.exception.InvalidMatrixException;
 import org.junit.Test;

 public class SingularValueDecompositionImplTest {

     private static final double EPS = 1.0e-10;

     private double[][] multiply(double[][] a, double[][] b) {
         int m = a.length;
         int n = a[0].length;
         int p = b[0].length;
         double[][] result = new double[m][p];
         for (int i = 0; i < m; i++) {
             for (int k = 0; k < n; k++) {
                 double aik = a[i][k];
                 for (int j = 0; j < p; j++) {
                     result[i][j] += aik * b[k][j];
                 }
             }
         }
         return result;
     }

     private double[][] subtract(double[][] a, double[][] b) {
         int m = a.length;
         int n = a[0].length;
         double[][] result = new double[m][n];
         for (int i = 0; i < m; i++) {
             for (int j = 0; j < n; j++) {
                 result[i][j] = a[i][j] - b[i][j];
             }
         }
         return result;
     }

     private double normF(double[][] a) {
         double sum = 0;
         for (double[] row : a) {
             for (double v : row) {
                 sum += v * v;
             }
         }
         return Math.sqrt(sum);
     }

     private double normMax(double[][] a) {
         double max = 0;
         for (double[] row : a) {
             for (double v : row) {
                 max = Math.max(max, Math.abs(v));
             }
         }
         return max;
     }

     private double[][] identity(int n) {
         double[][] I = new double[n][n];
         for (int i = 0; i < n; i++) {
             I[i][i] = 1.0;
         }
         return I;
     }

     private double[][] transpose(double[][] a) {
         int m = a.length;
         int n = a[0].length;
         double[][] t = new double[n][m];
         for (int i = 0; i < m; i++) {
             for (int j = 0; j < n; j++) {
                 t[j][i] = a[i][j];
             }
         }
         return t;
     }

     private RealMatrix toRealMatrix(double[][] data) {
         return new Array2DRowRealMatrix(data);
     }

     // MATH-320A: 2x2 matrix [[3,2],[2,3]] — SVD solves b=[5,5] via solution [1,1]
     @Test
     public void testMath320A() {
         double[][] data = {{3, 2}, {2, 3}};
         RealMatrix A = toRealMatrix(data);
         SingularValueDecompositionImpl svd = new SingularValueDecompositionImpl(A);

         double[] singularValues = svd.getSingularValues();
         assertEquals(2, singularValues.length);
         assertTrue(singularValues[0] > 0);
         assertTrue(singularValues[1] > 0);
         assertTrue("singular values must be sorted descending", singularValues[0] >=
singularValues[1]);

         double[] b = {5, 5};
         double[] x = svd.getSolver().solve(b);
         assertEquals(2, x.length);

         double[] expected = {1.0, 1.0};
         double diff0 = Math.abs(x[0] - expected[0]);
         double diff1 = Math.abs(x[1] - expected[1]);
         assertTrue("Solution x[0]=" + x[0] + " deviates from 1.0", diff0 < 1.0e-8);
         assertTrue("Solution x[1]=" + x[1] + " deviates from 1.0", diff1 < 1.0e-8);
     }

     // MATH-320B: singular 3x3 matrix with a zero row — solve should not crash and residual should
be near zero
     @Test
     public void testMath320B() {
         double[][] data = {{1, 2, 3}, {2, 4, 6}, {0, 0, 0}};
         RealMatrix A = toRealMatrix(data);
         SingularValueDecompositionImpl svd = new SingularValueDecompositionImpl(A);

         double[] singularValues = svd.getSingularValues();
         assertTrue(singularValues.length >= 1);

         // solve a consistent RHS: b is the sum of first two columns (i.e., in column space)
         double[] b = {3, 6, 0};
         double[] x = svd.getSolver().solve(b);

         // residual check: A * x should be close to b
         RealMatrix X = toRealMatrix(new double[][]{{x[0]}, {x[1]}, {x[2]}});
         RealMatrix residual = A.multiply(X).subtract(toRealMatrix(new double[][]{{b[0]}, {b[1]},
{b[2]}}));
         double resNorm = 0;
         for (int i = 0; i < residual.getRowDimension(); i++) {
             resNorm += Math.pow(residual.getEntry(i, 0), 2);
         }
         resNorm = Math.sqrt(resNorm);
         assertTrue("Residual norm " + resNorm + " should be small", resNorm < 1.0e-10);
     }

     // Square nonsingular 3x3 matrix: reconstruct A from U*S*V^T
     @Test
     public void testReconstructionNonsingular() {
         double[][] data = {{4, 1, 2}, {1, 5, 3}, {2, 3, 6}};
         RealMatrix A = toRealMatrix(data);
         SingularValueDecompositionImpl svd = new SingularValueDecompositionImpl(A);
         RealMatrix U = svd.getU();
         RealMatrix S = svd.getS();
         RealMatrix V = svd.getV();

         RealMatrix rebuilt = U.multiply(S).multiply(V.transpose());
         double error = A.subtract(rebuilt).getNorm();
         assertTrue("A ≈ U * S * V^T, error=" + error, error < EPS);
     }

     // Orthogonality: U^T * U ≈ I
     @Test
     public void testUOrthogonal() {
         double[][] data = {{3, 1, 0}, {1, 4, 2}, {0, 2, 3}};
         RealMatrix A = toRealMatrix(data);
         SingularValueDecompositionImpl svd = new SingularValueDecompositionImpl(A);
         RealMatrix UT = svd.getUT();
         RealMatrix U = svd.getU();
         RealMatrix utu = UT.multiply(U);
         int size = utu.getRowDimension();
         for (int i = 0; i < size; i++) {
             for (int j = 0; j < size; j++) {
                 double expected = (i == j) ? 1.0 : 0.0;
                 assertEquals("U^T*U[" + i + "][" + j + "]", expected, utu.getEntry(i, j), EPS);
             }
         }
     }

     // Orthogonality: V^T * V ≈ I
     @Test
     public void testVOrthogonal() {
         double[][] data = {{2, 3, 1}, {4, 1, 3}, {2, 5, 4}};
         RealMatrix A = toRealMatrix(data);
         SingularValueDecompositionImpl svd = new SingularValueDecompositionImpl(A);
         RealMatrix VT = svd.getVT();
         RealMatrix V = svd.getV();
         RealMatrix vtv = VT.multiply(V);
         int size = vtv.getRowDimension();
         for (int i = 0; i < size; i++) {
             for (int j = 0; j < size; j++) {
                 double expected = (i == j) ? 1.0 : 0.0;
                 assertEquals("V^T*V[" + i + "][" + j + "]", expected, vtv.getEntry(i, j), EPS);
             }
         }
     }

     // Singular values are nonnegative and descending
     @Test
     public void testSingularValuesNonnegativeDescending() {
         double[][] data = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}};
         RealMatrix A = toRealMatrix(data);
         SingularValueDecompositionImpl svd = new SingularValueDecompositionImpl(A);
         double[] sv = svd.getSingularValues();
         for (int i = 0; i < sv.length; i++) {
             assertTrue("singular value " + i + " must be nonnegative", sv[i] >= 0.0);
         }
         for (int i = 1; i < sv.length; i++) {
             assertTrue("singular values must be sorted descending", sv[i] <= sv[i - 1]);
         }
     }

     // Rank-deficient 3x2 tall: correct rank and solve consistency
     @Test
     public void testRankDeficientTall() {
         // column 2 is 2 * column 1 => rank 1
         double[][] data = {{1, 2}, {2, 4}, {3, 6}};
         RealMatrix A = toRealMatrix(data);
         SingularValueDecompositionImpl svd = new SingularValueDecompositionImpl(A);
         int rank = svd.getRank();
         assertTrue("Expected rank 1, got " + rank, rank == 1);

         double[] b = {3, 6, 9}; // consistent: 3 * col1
         double[] x = svd.getSolver().solve(b);
         RealMatrix X = toRealMatrix(new double[][]{{x[0]}, {x[1]}});
         RealMatrix residual = A.multiply(X).subtract(toRealMatrix(new double[][]{{b[0]}, {b[1]},
{b[2]}}));
         double resNorm = residual.getNorm();
         assertTrue("Residual norm " + resNorm + " should be small", resNorm < EPS);
     }

     // Wide rectangular 2x4 matrix
     @Test
     public void testWideRectangular() {
         double[][] data = {{1, 3, 5, 7}, {2, 4, 6, 8}};
         RealMatrix A = toRealMatrix(data);
         SingularValueDecompositionImpl svd = new SingularValueDecompositionImpl(A);
         RealMatrix U = svd.getU();
         RealMatrix S = svd.getS();
         RealMatrix V = svd.getV();

         RealMatrix rebuilt = U.multiply(S).multiply(V.transpose());
         double error = A.subtract(rebuilt).getNorm();
         assertTrue("A ≈ U * S * V^T, error=" + error, error < EPS);
     }

     // All-zero matrix
     @Test
     public void testZeroMatrix() {
         double[][] data = new double[3][3];
         RealMatrix A = toRealMatrix(data);
         SingularValueDecompositionImpl svd = new SingularValueDecompositionImpl(A);

         double[] sv = svd.getSingularValues();
         assertTrue(sv.length == 0);

         int rank = svd.getRank();
         assertEquals("Zero matrix rank must be 0", 0, rank);

         double[] b = {1, 2, 3};
         double[] x = svd.getSolver().solve(b);
         RealMatrix X = toRealMatrix(new double[][]{{x[0]}, {x[1]}, {x[2]}});
         RealMatrix residual = A.multiply(X).subtract(toRealMatrix(new double[][]{{b[0]}, {b[1]},
{b[2]}}));
         double resNorm = residual.getNorm();
         double bNorm = Math.sqrt(b[0]*b[0] + b[1]*b[1] + b[2]*b[2]);
         assertTrue("Residual zero for zero matrix, got " + resNorm + " bNorm=" + bNorm, resNorm <
EPS);
     }

     // Identity matrix: singular values are all 1, orthogonal matrices are identity
     @Test
     public void testIdentity() {
         int n = 4;
         double[][] data = identity(n);
         RealMatrix A = toRealMatrix(data);
         SingularValueDecompositionImpl svd = new SingularValueDecompositionImpl(A);

         double[] sv = svd.getSingularValues();
         assertEquals(n, sv.length);
         for (int i = 0; i < n; i++) {
             assertEquals("Identity sing. val. " + i, 1.0, sv[i], EPS);
         }

         RealMatrix U = svd.getU();
         RealMatrix V = svd.getV();
         for (int i = 0; i < n; i++) {
             assertEquals("U["+i+"]["+i+"]", 1.0, Math.abs(U.getEntry(i, i)), EPS);
             assertEquals("V["+i+"]["+i+"]", 1.0, Math.abs(V.getEntry(i, i)), EPS);
         }
     }

     // Solve with multiple RHS (2D)
     @Test
     public void testSolveMultipleRHS() {
         double[][] data = {{4, 1}, {1, 3}};
         RealMatrix A = toRealMatrix(data);
         SingularValueDecompositionImpl svd = new SingularValueDecompositionImpl(A);

         double[] b = {9, 1};
         RealVector xVector = svd.getSolver().solve(new ArrayRealVector(b));
         double[] bCheck = A.operate(xVector).toArray();
         assertArrayEquals("Vector solve residual", b, bCheck, EPS);

         RealMatrix B = toRealMatrix(new double[][]{{9, 1, 5}, {1, 4, 2}});
         RealMatrix X = svd.getSolver().solve(B);
         RealMatrix check = A.multiply(X);
         for (int i = 0; i < B.getRowDimension(); i++) {
             for (int j = 0; j < B.getColumnDimension(); j++) {
                 assertEquals("Matrix solve ["+i+"]["+j+"]", B.getEntry(i, j), check.getEntry(i, j),
EPS);
             }
         }
     }
 }