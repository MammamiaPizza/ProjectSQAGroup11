package org.apache.commons.math.stat.regression;

import org.junit.Test;
import static org.junit.Assert.*;

public class SimpleRegressionTest {

 @Test
 public void testSSENonNegative() {
     SimpleRegression regression = new SimpleRegression();
     regression.addData(new double[][]{
         {1.0, 1.0},
         {1.000000000000001, 2.0},
         {0.999999999999999, 3.0}
     });
     double sse = regression.getSumSquaredErrors();
     assertTrue("SSE should be non-negative, but was " + sse, sse >= 0.0);
 }

 @Test
 public void testPerfectFitSSEZero() {
     SimpleRegression regression = new SimpleRegression();
     double[][] data = {{0, 1}, {1, 4}, {2, 7}, {3, 10}}; // y = 3x + 1
     regression.addData(data);
     assertEquals("Perfect fit should produce SSE ~ 0", 0.0, regression.getSumSquaredErrors(),
1e-12);
     assertEquals(3.0, regression.getSlope(), 1e-12);
     assertEquals(1.0, regression.getIntercept(), 1e-12);
 }

 @Test
 public void testSSEFromPredictions() {
     SimpleRegression regression = new SimpleRegression();
     double[][] data = {{1, 2.1}, {2, 3.9}, {3, 6.2}, {4, 8.1}, {5, 9.8}};
     regression.addData(data);
     double expectedSSE = 0.0;
     for (double[] point : data) {
         double residual = point[1] - regression.predict(point[0]);
         expectedSSE += residual * residual;
     }
     assertEquals("SSE should equal sum of squared residuals",
                  expectedSSE, regression.getSumSquaredErrors(), 1e-12);
 }

 @Test
 public void testTotalSumSquaresDecomposition() {
     SimpleRegression regression = new SimpleRegression();
     double[][] data = {{1, 2}, {2, 4}, {3, 6.5}, {4, 8.2}, {5, 10.1}};
     regression.addData(data);
     double tss = regression.getTotalSumSquares();
     double regss = regression.getRegressionSumSquares();
     double sse = regression.getSumSquaredErrors();
     assertEquals("TSS should equal RegSS + SSE",
                  tss, regss + sse, 1e-12);
 }

 @Test
 public void testClearResetsN() {
     SimpleRegression regression = new SimpleRegression();
     regression.addData(1, 1);
     regression.addData(2, 2);
     regression.clear();
     assertEquals(0, regression.getN());
 }

 @Test
 public void testSinglePointGetN() {
     SimpleRegression regression = new SimpleRegression();
     regression.addData(5, 7);
     assertEquals(1, regression.getN());
     assertTrue("Slope should be NaN with single point",
                Double.isNaN(regression.getSlope()));
 }

 @Test
 public void testTotalSumSquaresNaNWhenNLessThan2() {
     SimpleRegression regression = new SimpleRegression();
     assertTrue("TSS should be NaN with no data",
                Double.isNaN(regression.getTotalSumSquares()));
     regression.addData(5, 7);
     assertTrue("TSS should be NaN with one point",
                Double.isNaN(regression.getTotalSumSquares()));
     regression.addData(6, 9);
     assertFalse("TSS should be finite when n >= 2",
                 Double.isNaN(regression.getTotalSumSquares()));
 }

 @Test
 public void testMeanSquareErrorNaNWhenNLessThan3() {
     SimpleRegression regression = new SimpleRegression();
     regression.addData(1, 2);
     assertTrue("MSE should be NaN with n < 3",
                Double.isNaN(regression.getMeanSquareError()));
     regression.addData(3, 4);
     assertTrue("MSE should be NaN with n = 2",
                Double.isNaN(regression.getMeanSquareError()));
     regression.addData(5, 7);
     assertFalse("MSE should be finite when n >= 3",
                 Double.isNaN(regression.getMeanSquareError()));
 }

 @Test
 public void testAddDataArray() {
     SimpleRegression regression = new SimpleRegression();
     double[][] data = {{1.0, 1.1}, {2.0, 2.2}, {3.0, 3.3}};
     regression.addData(data);
     assertEquals(3, regression.getN());
 }

 @Test
 public void testRSignConsistentWithSlope() {
     SimpleRegression posReg = new SimpleRegression();
     posReg.addData(new double[][]{{1, 1}, {2, 3}, {3, 5}}); // positive slope
     assertTrue("R should be non-negative for positive slope",
                posReg.getR() >= 0.0);

     SimpleRegression negReg = new SimpleRegression();
     negReg.addData(new double[][]{{1, 5}, {2, 3}, {3, 1}}); // negative slope
     assertTrue("R should be non-positive for negative slope",
                negReg.getR() <= 0.0);
 }

 @Test
 public void testLargeValuesSSENonNegative() {
     SimpleRegression regression = new SimpleRegression();
     for (int i = 0; i < 100; i++) {
         regression.addData(i * 1e9, i * 1e9 + 1e8);
     }
     assertTrue("SSE should be non-negative for large values",
                regression.getSumSquaredErrors() >= 0.0);
 }

 @Test
 public void testStandardErrorsNonNegative() {
     SimpleRegression regression = new SimpleRegression();
     regression.addData(new double[][]{{1, 2}, {2, 4}, {3, 6.1}, {4, 8.3}});
     assertTrue("Intercept standard error should be non-negative",
                regression.getInterceptStdErr() >= 0.0);
     assertTrue("Slope standard error should be non-negative",
                regression.getSlopeStdErr() >= 0.0);
 }

}
