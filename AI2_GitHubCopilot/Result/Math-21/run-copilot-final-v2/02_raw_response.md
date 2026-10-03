import static org.junit.Assert.*;
 import org.junit.Test;
 import org.apache.commons.math3.linear.Array2DRowRealMatrix;
 import org.apache.commons.math3.linear.MatrixUtils;
 import org.apache.commons.math3.linear.NonPositiveDefiniteMatrixException;
 import org.apache.commons.math3.linear.RealMatrix;
 import org.apache.commons.math3.linear.RectangularCholeskyDecomposition;

 /**
  * Tests for {@link RectangularCholeskyDecomposition} focusing on rank detection,
  * correctness of the root matrix and handling of degenerate cases.
  */
 public class RectangularCholeskyDecompositionTest {

  private static final double EPS = 1e-12;
  private static final double SMALL = 1e-10;

  // helper: max absolute difference between expected and actual matrices
  private static double maxAbsDiff(RealMatrix expected, RealMatrix actual) {
  int rows = expected.getRowDimension();
  int cols = expected.getColumnDimension();
  assertEquals(rows, actual.getRowDimension());
  assertEquals(cols, actual.getColumnDimension());
  double max = 0d;
  for (int i = 0; i < rows; i++) {
  for (int j = 0; j < cols; j++) {
  double diff = FastMath.abs(expected.getEntry(i, j) - actual.getEntry(i, j));
  if (diff > max) {
  max = diff;
  }
  }
  }
  return max;
  }

  @Test
  public void testFullRankIdentity() {
  RealMatrix ident = MatrixUtils.createRealIdentityMatrix(3);
  RectangularCholeskyDecomposition d = new RectangularCholeskyDecomposition(ident, SMALL);
  assertEquals(3, d.getRank());
  RealMatrix root = d.getRootMatrix();
  RealMatrix recomposed = root.multiply(root.transpose());
  double error = maxAbsDiff(ident, recomposed);
  assertTrue("Reconstruction error: " + error, error < 1e-10);
  }

  @Test
  public void testFullRankRandomPD() {
  // build a random 4x4 matrix and create A = M * M^T to guarantee semi-definiteness
  double[][] data = {
  { 1.2, -0.4, 0.1, 0.3},
  { 0.5, 1.1, -0.2, 0.1},
  { -0.1, 0.2, 0.9, 0.0},
  { 0.3, 0.1, 0.0, 1.0}
  };
  RealMatrix m = new Array2DRowRealMatrix(data, false);
  RealMatrix a = m.multiply(m.transpose());
  RectangularCholeskyDecomposition d = new RectangularCholeskyDecomposition(a, SMALL);
  assertEquals(4, d.getRank());
  RealMatrix root = d.getRootMatrix();
  RealMatrix recomposed = root.multiply(root.transpose());
  assertTrue(maxAbsDiff(a, recomposed) < 1e-10);
  }

  @Test
  public void testRankDeficientZeroRows() {
  // A is 3x3 with last row/col zero -> rank 2
  double[][] data = {
  { 2.0, -1.0, 0.0},
  { -1.0, 2.0, 0.0},
  { 0.0, 0.0, 0.0}
  };
  RealMatrix a = new Array2DRowRealMatrix(data, false);
  RectangularCholeskyDecomposition d = new RectangularCholeskyDecomposition(a, SMALL);
  assertEquals(2, d.getRank());
  RealMatrix root = d.getRootMatrix();
  assertTrue(maxAbsDiff(a, root.multiply(root.transpose())) < 1e-10);
  }

  @Test
  public void testThresholdDrop() {
  // matrix with one small diagonal, small threshold above it
  double[][] data = {
  { 1e-6, 0, 0},
  { 0, 2.0, 0},
  { 0, 0, 3.0}
  };
  RealMatrix a = new Array2DRowRealMatrix(data, false);
  // small = 1e-4 > 1e-6, so first column dropped
  RectangularCholeskyDecomposition d = new RectangularCholeskyDecomposition(a, 1e-4);
  assertEquals(2, d.getRank());
  // reconstruction of dropped part is zero, remainder should match
  RealMatrix reconstructed = d.getRootMatrix().multiply(d.getRootMatrix().transpose());
  // dropped first element: original had 1e-6, reconstructed 0
  assertTrue(maxAbsDiff(a, reconstructed) <= 1e-4);
  }

  @Test
  public void testDuplicateRows() {
  // rank-1 matrix: outer product of [1,2,1]
  double[] v = {1, 2, 1};
  RealMatrix a = MatrixUtils.createRealMatrix(3,3);
  for (int i=0; i<3; i++) {
  for (int j=0; j<3; j++) {
  a.setEntry(i, j, v[i] * v[j]);
  }
  }
  RectangularCholeskyDecomposition d = new RectangularCholeskyDecomposition(a, SMALL);
  assertEquals(1, d.getRank());
  RealMatrix root = d.getRootMatrix();
  // check reconstruction is exact up to tolerance
  assertTrue(maxAbsDiff(a, root.multiply(root.transpose())) < 1e-10);
  }

  @Test(expected = NonPositiveDefiniteMatrixException.class)
  public void testZeroMatrixThrows() {
  RealMatrix zero = MatrixUtils.createRealMatrix(2,2);
  new RectangularCholeskyDecomposition(zero, SMALL); // should throw because r=0
  }

  @Test(expected = NonPositiveDefiniteMatrixException.class)
  public void testLargeSmallThreshold() {
  // threshold larger than all positive diagonal entries -> r=0 -> exception
  RealMatrix ident = MatrixUtils.createRealIdentityMatrix(3);
  // check identity entries are 1, small=10 > 1 => throw
  new RectangularCholeskyDecomposition(ident, 10.0);
  }

  @Test
  public void testSmallZeroFullRank() {
  RealMatrix ident = MatrixUtils.createRealIdentityMatrix(2);
  RectangularCholeskyDecomposition d = new RectangularCholeskyDecomposition(ident, 0.0);
  assertEquals(2, d.getRank());
  assertTrue(maxAbsDiff(ident, d.getRootMatrix().multiply(d.getRootMatrix().transpose())) < 1e-12);
  }

  @Test(expected = NonPositiveDefiniteMatrixException.class)
  public void testNonPositiveDefinite() {
  // matrix with a sufficiently negative diagonal (not the first)
  double[][] data = {
  { 5.0, 0, 0},
  { 0, 1.0, 0.1},
  { 0, 0.1, -3.0}
  };
  RealMatrix a = new Array2DRowRealMatrix(data, false);
  new RectangularCholeskyDecomposition(a, SMALL); // should throw
  }

  @Test
  public void test1x1Matrix() {
  // 1-by-1 matrix
  RealMatrix a = MatrixUtils.createRealMatrix(1,1);
  a.setEntry(0, 0, 9.0);
  RectangularCholeskyDecomposition d = new RectangularCholeskyDecomposition(a, SMALL);
  assertEquals(1, d.getRank());
  assertEquals(3.0, d.getRootMatrix().getEntry(0,0), 1e-12);
  }

  @Test
  public void testRankMatchesRootColumnCount() {
  double[][] data = { {4,1,0}, {1,2,0}, {0,0,0} };
  RealMatrix a = new Array2DRowRealMatrix(data, false);
  RectangularCholeskyDecomposition d = new RectangularCholeskyDecomposition(a, SMALL);
  assertEquals(d.getRank(), d.getRootMatrix().getColumnDimension());
  }

  // tries to trigger MATH-789 pattern: matrix with nearly zero diagonal but
  // small enough to be kept, checking reconstruction error
  @Test
  public void testNearZeroDiagKept() {
  double[] dvals = { 1e-8, 2.0, 3.0 };
  RealMatrix a = MatrixUtils.createRealMatrix(3,3);
  for (int i=0; i<3; i++) a.setEntry(i, i, dvals[i]);
  RectangularCholeskyDecomposition d = new RectangularCholeskyDecomposition(a, 1e-12);
  assertEquals(3, d.getRank());
  RealMatrix recon = d.getRootMatrix().multiply(d.getRootMatrix().transpose());
  assertTrue(maxAbsDiff(a, recon) < 1e-8); // expect good reconstruction
  }

 }