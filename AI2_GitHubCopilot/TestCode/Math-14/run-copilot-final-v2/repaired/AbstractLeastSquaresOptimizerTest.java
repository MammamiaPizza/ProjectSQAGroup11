package org.apache.commons.math3.optim.nonlinear.vector.jacobian;

import org.apache.commons.math3.exception.NonSquareMatrixException;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.optim.Target;
import org.apache.commons.math3.optim.nonlinear.vector.Weight;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assume;
import org.junit.Test;

import static org.junit.Assert.*;

/**

 - Tests for the buggy version of AbstractLeastSquaresOptimizer and Weight
 - (Math-14b).  Uses a concrete subclass to exercise protected behavior.
  */
 public class AbstractLeastSquaresOptimizerTest {
  /** Always-converged checker so that optimize() sets weight without extensive iteration.
  */
  private static final ConvergenceChecker<PointVectorValuePair> CONVERGED =
  new ConvergenceChecker<PointVectorValuePair>() {
      @Override
      public boolean converged(int iteration, PointVectorValuePair previous,
                               PointVectorValuePair current) {
          return true;
      }
  };
  // -----------------------------------------------------------------------
  // Concrete optimizer for testing – simple linear model y = p0
  * x + p1
  // -----------------------------------------------------------------------
  private static class TestOptimizer extends AbstractLeastSquaresOptimizer {
  private final double[] x;
  TestOptimizer(double[] x) {
      super(CONVERGED);
      this.x = x;
  }
  @Override
  protected double[] computeObjectiveValue(double[] params) {
      double[] f = new double[x.length];
      for (int i = 0; i < x.length; i++) {
          f[i] = params[0]
  * x[i] + params[1];
      }
      return f;
  }
  @Override
  protected double[][] computeJacobian(double[] params) {
      double[][] j = new double[x.length][2];
      for (int i = 0; i < x.length; i++) {
          j[i][0] = x[i];
          j[i][1] = 1.0;
      }
      return j;
  }
  // Expose protected members for direct testing
  public double exposedCost(double[] residuals) {
      return computeCost(residuals);
  }
  public RealMatrix exposedWeightedJacobian(double[] params) {
      return computeWeightedJacobian(params);
  }
  public double[] exposedResiduals(double[] objectiveValue) {
      return computeResiduals(objectiveValue);
  }
  }
  // -----------------------------------------------------------------------
  // Helpers
  // -----------------------------------------------------------------------
  private static OptimizationData[] optData(double[] init, double[] target,
                                        RealMatrix weight,
                                        int maxEval, int maxIter) {
  return new OptimizationData[] {
      new InitialGuess(init),
      new Target(target),
      new Weight(weight),
      new MaxEval(maxEval),
      new MaxIter(maxIter)
  };
  }
  private static OptimizationData[] optData(double[] init, double[] target,
                                        Weight weight,
                                        int maxEval, int maxIter) {
  return new OptimizationData[] {
      new InitialGuess(init),
      new Target(target),
      weight,
      new MaxEval(maxEval),
      new MaxIter(maxIter)
  };
  }
  private static void assertMatrixEquals(RealMatrix expected, RealMatrix actual, double tol) {
  assertEquals(expected.getRowDimension(), actual.getRowDimension());
  assertEquals(expected.getColumnDimension(), actual.getColumnDimension());
  for (int i = 0; i < expected.getRowDimension(); i++) {
      for (int j = 0; j < expected.getColumnDimension(); j++) {
          assertEquals(expected.getEntry(i, j), actual.getEntry(i, j), tol);
      }
  }
  }
  // -----------------------------------------------------------------------
  //
  1.  Weight with all ones behaves like unweighted (cost = L2 norm)
  // -----------------------------------------------------------------------
  @Test
  public void testWeightAllOnesEqualsUnweighted() {
  double[] xarr = {1, 2, 3};
  TestOptimizer opt = new TestOptimizer(xarr);
  double[] init = {0, 0};
  double[] target = {2, 3, 5};
  Weight w = new Weight(new double[]{1, 1, 1});
  opt.optimize(optData(init, target, w, 100, 100));
  double[] residuals = opt.exposedResiduals(opt.computeObjectiveValue(init));
  double cost = opt.exposedCost(residuals);
  double expectedCost = FastMath.sqrt(4 + 9 + 25); // r = [2,3,5]
  assertEquals(expectedCost, cost, 1e-15);
  double rms = opt.getRMS();
  double chi2 = opt.getChiSquare();
  assertEquals(expectedCost
  * expectedCost, chi2, 1e-12);
  assertEquals(FastMath.sqrt(chi2 / 3), rms, 1e-12);
  }
  // -----------------------------------------------------------------------
  //
  2.  Weighted cost / RMS / chi² for a known set of weights
  // -----------------------------------------------------------------------
  @Test
  public void testWeightedCostManual() {
  double[] xarr = {1, 2, 3};
  TestOptimizer opt = new TestOptimizer(xarr);
  double[] init = {0, 0};
  // target – arbitrary
  double[] target = {2, 3, 5};
  Weight w = new Weight(new double[]{1, 2, 0.5});
  opt.optimize(optData(init, target, w, 100, 100));
  double[] residuals = opt.exposedResiduals(opt.computeObjectiveValue(init));
  // residuals = target - model(0,0) = [2,3,5]
  double cost = opt.exposedCost(residuals);
  // manual: sum w_i
  * r_i^2 = 14 + 29 + 0.525 = 4+18+12.5 = 34.5
  double manualSum = 14 + 29 + 0.525;
  double manualCost = FastMath.sqrt(manualSum);
  assertEquals(manualCost, cost, 1e-15);
  double chi2 = opt.getChiSquare();
  assertEquals(manualSum, chi2, 1e-12);
  double rms = opt.getRMS();
  double manualRMS = FastMath.sqrt(manualSum / 3);
  assertEquals(manualRMS, rms, 1e-12);
  }
  // -----------------------------------------------------------------------
  //
  3.  Weighted Jacobian equals sqrt(W) * J (diagonal case)
  // -----------------------------------------------------------------------
  @Test
  public void testWeightedJacobianDiagonal() {
  double[] xarr = {1, 2};
  TestOptimizer opt = new TestOptimizer(xarr);
  double[] target = {0, 0};
  double[] params = {0.5, 0.75};
  // weight = diagonal(4, 9)  -> sqrt = diag(2, 3)
  Weight w = new Weight(new double[]{4, 9});
  opt.optimize(optData(params, target, w, 100, 100));
  RealMatrix weightedJ = opt.exposedWeightedJacobian(params);
  assertEquals(2, weightedJ.getRowDimension());
  assertEquals(2, weightedJ.getColumnDimension());
  // J from model: row0 = [x[0]=1, 1]; row1 = [2, 1]
  // expected weighted: row0 = [21, 21] = [2,2]; row1 = [32, 31] = [6,3]
  double[][] expected = {{2, 2}, {6, 3}};
  RealMatrix expectedMat = MatrixUtils.createRealMatrix(expected);
  assertMatrixEquals(expectedMat, weightedJ, 1e-15);
  }
  // -----------------------------------------------------------------------
  //
  4.  Weight with a zero entry – should not throw, cost excludes that residual
  // -----------------------------------------------------------------------
  @Test
  public void testWeightZeroEntry() {
  double[] xarr = {1, 2, 3};
  TestOptimizer opt = new TestOptimizer(xarr);
  double[] init = {0, 0};
  double[] target = {2, 3, 5};
  // weight with a zero -> first residual should be ignored
  Weight w = new Weight(new double[]{0, 1, 1});
  opt.optimize(optData(init, target, w, 100, 100));
  double[] residuals = opt.exposedResiduals(opt.computeObjectiveValue(init));
  // residuals = [2,3,5]
  double cost = opt.exposedCost(residuals);
  // manual: 04 + 19 + 1*25 = 34
  double manualCost = FastMath.sqrt(34);
  assertEquals(manualCost, cost, 1e-15);
  }
  // -----------------------------------------------------------------------
  //
  5.  Weight with a full (non‑diagonal) matrix – cost = rᵀ W r
  // -----------------------------------------------------------------------
  @Test
  public void testWeightNonDiagonalMatrix() {
  // 2 observations, linear model
  double[] xarr = {1, 2};
  TestOptimizer opt = new TestOptimizer(xarr);
  double[] init = {0, 0};
  double[] target = {2, 3};
  // symmetric positive‑definite weight matrix
  double[][] data = {{2, 1}, {1, 2}};
  RealMatrix Wmat = MatrixUtils.createRealMatrix(data);
  Weight w = new Weight(Wmat);
  opt.optimize(optData(init, target, Wmat, 100, 100));
  double[] residuals = opt.exposedResiduals(opt.computeObjectiveValue(init));
  // residuals = [2, 3]
  double cost = opt.exposedCost(residuals);
  // rᵀWr = [2,3]
  * [[2,1],[1,2]] * [2,3]ᵀ = [2,3] * [7, 8]ᵀ = 14+24 = 38
  assertEquals(FastMath.sqrt(38), cost, 1e-15);
  }
  // -----------------------------------------------------------------------
  //
  6.  getWeightSquareRoot – sqrt * sqrt ≈ original weight (diagonal case)
  // -----------------------------------------------------------------------
  @Test
  public void testGetWeightSquareRootDiagonal() {
  double[] xarr = {1, 2};
  TestOptimizer opt = new TestOptimizer(xarr);
  double[] init = {0, 0};
  double[] target = {0, 0};
  Weight w = new Weight(new double[]{4, 9});
  opt.optimize(optData(init, target, w, 100, 100));
  RealMatrix sqrt = opt.getWeightSquareRoot();
  RealMatrix product = sqrt.multiply(sqrt);  // S
  * S ≈ original W
  // original W = diag(4, 9)
  double[][] expected = {{4, 0}, {0, 9}};
  RealMatrix expectedMat = MatrixUtils.createRealMatrix(expected);
  assertMatrixEquals(expectedMat, product, 1e-12);
  }
  // -----------------------------------------------------------------------
  //
  7.  computeResiduals = target - objectiveValue
  // -----------------------------------------------------------------------
  @Test
  public void testComputeResiduals() {
  double[] xarr = {1, 2};
  TestOptimizer opt = new TestOptimizer(xarr);
  double[] init = {1, 0};
  double[] target = {2, 3};
  Weight w = new Weight(new double[]{1, 1});
  opt.optimize(optData(init, target, w, 100, 100));
  // model(1,0) for x=1 -> 1*1+0=1, for x=2 -> 2
  double[] objective = opt.computeObjectiveValue(init);
  double[] res = opt.exposedResiduals(objective);
  assertArrayEquals(new double[]{target[0]-objective[0], target[1]-objective[1]}, res, 1e-15);
  }
  // -----------------------------------------------------------------------
  //
  8.  RMS and chi‑square consistency
  // -----------------------------------------------------------------------
  @Test
  public void testRMSandChiSquareConsistency() {
  double[] xarr = {1, 2, 3};
  TestOptimizer opt = new TestOptimizer(xarr);
  double[] init = {0, 0};
  double[] target = {2, 3, 5};
  Weight w = new Weight(new double[]{1, 2, 0.5});
  opt.optimize(optData(init, target, w, 100, 100));
  double[] residuals = opt.exposedResiduals(opt.computeObjectiveValue(init));
  double cost = opt.exposedCost(residuals);
  double chi2 = opt.getChiSquare();
  double rms = opt.getRMS();
  assertEquals(cost
  * cost, chi2, 1e-12);
  assertEquals(FastMath.sqrt(chi2 / target.length), rms, 1e-12);
  }
  // -----------------------------------------------------------------------
  //
  9.  Weight(RealMatrix) rejects non‑square input
  // -----------------------------------------------------------------------
  @Test(expected = NonSquareMatrixException.class)
  public void testWeightThrowsNonSquareMatrix() {
  new Weight(MatrixUtils.createRealMatrix(2, 5));
  }
  // -----------------------------------------------------------------------
  //
  10.  getWeight() returns a defensive copy
  // -----------------------------------------------------------------------
  @Test
  public void testWeightCopy() {
  double[][] origData = {{1, 2}, {3, 4}};
  RealMatrix orig = MatrixUtils.createRealMatrix(origData);
  Weight w = new Weight(orig);
  RealMatrix retrieved = w.getWeight();
  // assert contents equal
  assertEquals(orig.getEntry(0, 1), retrieved.getEntry(0, 1), 0);
  // modify original
  orig.setEntry(0, 1, 999);
  // retrieved should be unchanged
  assertEquals(2, retrieved.getEntry(0, 1), 0);
  }
  // -----------------------------------------------------------------------
  //
  11.  Weight(double[]) with zero length does not throw
  // -----------------------------------------------------------------------
  @Test
  public void testWeightZeroLength() {
  // length zero is allowed (empty diagonal)
  Weight w = new Weight(new double[0]);
  RealMatrix m = w.getWeight();
  assertEquals(0, m.getRowDimension());
  assertEquals(0, m.getColumnDimension());
  }
  // -----------------------------------------------------------------------
  //
  12.  Large Weight(double[]) causes OutOfMemoryError (the bug indicator)
  // -----------------------------------------------------------------------
  @Test(expected = OutOfMemoryError.class)
  public void testLargeWeightCausesOOM() {
  // This test is only meaningful when the maximum heap is small (< 256 MB).
  Assume.assumeTrue("Heap too large to trigger OOM",
      Runtime.getRuntime().maxMemory() < 256L
  * 1024 * 1024);
  // A 100 000 × 100 000 dense matrix consumes ~80 GB → guaranteed OOM.
  new Weight(new double[100000]);
  }

}
