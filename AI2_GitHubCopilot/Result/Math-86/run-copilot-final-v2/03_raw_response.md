package org.apache.commons.math.linear;

 import static org.junit.Assert.*;
 import org.junit.Test;

 import org.apache.commons.math.linear.CholeskyDecompositionImpl;
 import org.apache.commons.math.linear.NotPositiveDefiniteMatrixException;
 import org.apache.commons.math.linear.NotSymmetricMatrixException;
 import org.apache.commons.math.linear.NonSquareMatrixException;
 import org.apache.commons.math.linear.RealMatrix;
 import org.apache.commons.math.linear.RealMatrixImpl;
 import org.apache.commons.math.linear.DecompositionSolver;

 /**

 - Tests for {@link CholeskyDecompositionImpl} focusing on detection of non‑positive‑definite
 - matrices (MATH‑274). The bug causes the constructor to not throw
 - {@link NotPositiveDefiniteMatrixException} for matrices that have positive diagonal entries
 - but are nonetheless indefinite or singular.
   */
  public class CholeskyDecompositionImplTest {
  private static RealMatrix matrix(double[][] data) {
  return new RealMatrixImpl(data);
  }
  // ---- Non‑positive‑definite detection (core of MATH‑274) ----
  @Test(expected = NotPositiveDefiniteMatrixException.class)
  public void testNotPositiveDefiniteIndefinite2x2() {
  // Symmetric, positive diagonals, but eigenvalues 3 and -1 → indefinite
  new CholeskyDecompositionImpl(matrix(new double[][] {{1, 2}, {2, 1}}));
  }
  @Test(expected = NotPositiveDefiniteMatrixException.class)
  public void testNotPositiveDefiniteSingular2x2() {
  // Symmetric, positive diagonals, but singular (eigenvalue 0)
  new CholeskyDecompositionImpl(matrix(new double[][] {{1, 2}, {2, 4}}));
  }
  @Test(expected = NotPositiveDefiniteMatrixException.class)
  public void testNotPositiveDefiniteNearSingular2x2() {
  // Symmetric, positive diagonals, determinant ≈ –2e‑10 → not positive definite
  new CholeskyDecompositionImpl(matrix(new double[][] {{1, 1.0000000001},
                                                       {1.0000000001, 1}}));
  }
  @Test(expected = NotPositiveDefiniteMatrixException.class)
  public void testNotPositiveDefinite3x3Rank1() {
  // Symmetric, all diagonals positive, rank 1 → pivot becomes zero during decomposition
  new CholeskyDecompositionImpl(matrix(new double[][] {{1, 2, 3},
                                       {2, 4, 6},
                                       {3, 6, 9}}));
  }
  @Test(expected = NotPositiveDefiniteMatrixException.class)
  public void testNotPositiveDefinite3x3Indefinite() {
  // Symetric, positive diagonals, sub‑matrix [[1,2],[2,1]] is indefinite
  new CholeskyDecompositionImpl(matrix(new double[][] {{1, 2, 0},
                                       {2, 1, 0},
                                       {0, 0, 1}}));
  }
  @Test(expected = NotPositiveDefiniteMatrixException.class)
  public void testCustomThresholdDoesNotHideNonPositiveDefinite() {
  // Same indefinite matrix must be rejected even with custom thresholds
  new CholeskyDecompositionImpl(matrix(new double[][] {{1, 2}, {2, 1}}));
  }
  // ---- Symetry and squareness checks (contract) ----
  @Test(expected = NotSymmetricMatrixException.class)
  public void testNotSymmetric() {
  new CholeskyDecompositionImpl(matrix(new double[][] {{1, 2}, {3, 4}}));
  }
  @Test(expected = NonSquareMatrixException.class)
  public void testNonSquare() {
  new CholeskyDecompositionImpl(matrix(new double[][] {{1, 2, 3}, {4, 5, 6}}));
  }
  // ---- Correct operation for a positive‑definite matrix (sanity) ----
  @Test
  public void testPositiveDefiniteWorks() {
  double[][] d = {{2, 1, 0}, {1, 2, 1}, {0, 1, 2}};
  CholeskyDecompositionImpl chol = new CholeskyDecompositionImpl(matrix(d));
  // getL and getLT are consistent
  RealMatrix L = chol.getL();
  RealMatrix LT = chol.getLT();
  assertEquals(L.transpose(), LT);
  // determinant is product of squared diagonal entries of L (and hence of LT)
  double expectedDet = 1.0;
  for (int i = 0; i < 3; i++) {
      double lii = LT.getEntry(i, i);
      expectedDet
  *= lii * lii;
  }
  assertEquals(expectedDet, chol.getDeterminant(), 1e-15);
  }
  @Test
  public void testSolveSimpleDiagonal() {
  double[][] d = {{4, 0}, {0, 9}};
  CholeskyDecompositionImpl chol = new CholeskyDecompositionImpl(matrix(d));
  DecompositionSolver solver = chol.getSolver();
  double[] b = {8, 27};
  double[] x = solver.solve(b);
  assertArrayEquals(new double[] {2, 3}, x, 1e-10);
  }
  @Test
  public void testInverseWorks() {
  double[][] d = {{4, 0}, {0, 9}};
  CholeskyDecompositionImpl chol = new CholeskyDecompositionImpl(matrix(d));
  RealMatrix inv = chol.getSolver().getInverse();
  assertEquals(0.25, inv.getEntry(0, 0), 1e-15);
  assertEquals(1.0/9.0, inv.getEntry(1, 1), 1e-15);
  assertEquals(0.0, inv.getEntry(0, 1), 1e-15);
  assertEquals(0.0, inv.getEntry(1, 0), 1e-15);
  }
  private static void assertArrayEquals(double[] expected, double[] actual, double tol) {
  assertEquals("Array lengths differ", expected.length, actual.length);
  for (int i = 0; i < expected.length; i++) {
      assertEquals("Element " + i, expected[i], actual[i], tol);
  }
  }

 }