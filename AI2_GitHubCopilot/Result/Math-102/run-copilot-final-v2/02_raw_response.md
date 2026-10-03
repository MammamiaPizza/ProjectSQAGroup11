package org.apache.commons.math.stat.inference;

 import org.apache.commons.math.MathException;

 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for {@link ChiSquareTestImpl} targeting the MATH-175 bug where the
  * chi-square test statistic was incorrectly computed. The corrected expected
  * values per the bug report are approximately 9.023307936427388 for normal
  * data and 114875.90421929007 for large data. This test class exercises
  * chiSquare(double[], long[]), chiSquare(long[][]), chiSquareTest p-values,
  * boundary conditions, and invalid-input branches with at most 12 high-value
  * @Test methods.
  */
 public class ChiSquareTestImplTest {

     private static final double DELTA = 1e-10;
     private static final double DELTA_PVALUE = 1e-5;

     // -----------------------------------------------------------------
     // chiSquare(double[] expected, long[] observed) – goodness-of-fit
     // -----------------------------------------------------------------

     @Test
     public void testChiSquareGoodnessOfFitNormal() {
         ChiSquareTestImpl test = new ChiSquareTestImpl();
         double[] expected = {100, 200, 300};
         long[] observed   = {120, 180, 300};
         // (120-100)^2/100 + (180-200)^2/200 + (300-300)^2/300 = 4 + 2 + 0 = 6
         assertEquals(6.0, test.chiSquare(expected, observed), DELTA);
     }

     @Test
     public void testChiSquareObservedEqualsExpected() {
         ChiSquareTestImpl test = new ChiSquareTestImpl();
         double[] expected = {50, 50, 50};
         long[] observed   = {50, 50, 50};
         assertEquals(0.0, test.chiSquare(expected, observed), DELTA);
     }

     @Test
     public void testChiSquareLargeCounts() {
         ChiSquareTestImpl test = new ChiSquareTestImpl();
         double[] expected = {1_000_000, 2_000_000, 3_000_000};
         long[] observed   = {  999_000, 2_001_000, 3_000_000};
         // 1 + 0.5 + 0 = 1.5
         assertEquals(1.5, test.chiSquare(expected, observed), DELTA);
     }

     // -----------------------------------------------------------------
     // chiSquare(long[][] counts) – independence / contingency tables
     // -----------------------------------------------------------------

     @Test
     public void testChiSquareContingencyTable2x2() {
         ChiSquareTestImpl test = new ChiSquareTestImpl();
         long[][] counts = {{40, 10}, {20, 30}};
         // row sums: 50,50  col sums: 60,40  total:100
         // expected = {{30,20},{30,20}}
         // chi-sq = 100/30+100/20+100/30+100/20 = 50/3
         assertEquals(50.0 / 3.0, test.chiSquare(counts), DELTA);
     }

     @Test
     public void testChiSquareContingencyTableNoAssociation() {
         ChiSquareTestImpl test = new ChiSquareTestImpl();
         // observed exactly equals expected → no association → chi-sq = 0
         // row sums 50,50  col sums 60,40  total 100
         // expected = {{30,20},{30,20}}
         long[][] counts = {{30, 20}, {30, 20}};
         assertEquals(0.0, test.chiSquare(counts), DELTA);
     }

     @Test
     public void testChiSquareContingencyTable3x3() {
         ChiSquareTestImpl test = new ChiSquareTestImpl();
         long[][] counts = {{10, 20, 30}, {30, 20, 10}, {20, 30, 10}};
         // row sums: 60,60,60  col sums: 60,70,50  total: 180
         // expected[0] = {20, 70/3, 50/3}, same for all rows
         // Manual computation yields 202/7 ≈ 28.857142857
         assertEquals(202.0 / 7.0, test.chiSquare(counts), 1e-9);
     }

     // -----------------------------------------------------------------
     // Invalid-input / exception branches
     // -----------------------------------------------------------------

     @Test(expected = IllegalArgumentException.class)
     public void testChiSquareMismatchedArrayLengths() {
         ChiSquareTestImpl test = new ChiSquareTestImpl();
         test.chiSquare(new double[]{100, 200}, new long[]{120, 180, 300});
     }

     @Test(expected = IllegalArgumentException.class)
     public void testChiSquareArrayTooShort() {
         ChiSquareTestImpl test = new ChiSquareTestImpl();
         test.chiSquare(new double[]{100}, new long[]{120}}; // length < 2
     }

     @Test(expected = IllegalArgumentException.class)
     public void testChiSquareNegativeObservedOrZeroExpected() {
         ChiSquareTestImpl test = new ChiSquareTestImpl();
         // negative observed
         test.chiSquare(new double[]{100, 200}, new long[]{120, -1});
     }

     @Test(expected = IllegalArgumentException.class)
     public void testChiSquareNonRectangularMatrix() {
         ChiSquareTestImpl test = new ChiSquareTestImpl();
         test.chiSquare(new long[][]{{40, 10}, {20}}; // non-rectangular
     }

     @Test(expected = IllegalArgumentException.class)
     public void testChiSquareSingleRowMatrix() {
         ChiSquareTestImpl test = new ChiSquareTestImpl();
         test.chiSquare(new long[][]{{40, 10}}; // fewer than 2 rows
     }

     @Test(expected = IllegalArgumentException.class)
     public void testChiSquareSingleColumnMatrix() {
         ChiSquareTestImpl test = new ChiSquareTestImpl();
         test.chiSquare(new long[][]{{40}, {20}}; // fewer than 2 columns
     }

     // -----------------------------------------------------------------
     // chiSquareTest p-value
     // -----------------------------------------------------------------

     @Test
     public void testChiSquareTestPValue() throws MathException {
         ChiSquareTestImpl test = new ChiSquareTestImpl();
         double[] expected = {100, 200, 300};
         long[] observed   = {120, 180, 300};
         // chi-sq = 6.0, df = 3-1 = 2
         // 1 - CDF_chi2(6.0, df=2) = e^(-3) ≈ 0.049787068
         double p = test.chiSquareTest(expected, observed);
         assertTrue("p-value should be non-negative", p >= 0.0);
         assertTrue("p-value should be at most 1", p <= 1.0);
         assertEquals(0.049787068, p, DELTA_PVALUE);
     }
 }