package org.apache.commons.math.stat.correlation;

 import org.apache.commons.math.MathException;
 import org.apache.commons.math.distribution.TDistribution;
 import org.apache.commons.math.distribution.TDistributionImpl;
 import org.apache.commons.math.linear.BlockRealMatrix;
 import org.apache.commons.math.linear.RealMatrix;
 import org.junit.Test;

 import static org.junit.Assert.*;

 /**
  * Tests for {@link PearsonsCorrelation#getCorrelationPValues()}
  * targeting the bug where p-values near zero could be returned as 0 instead of 1,
  * or where extremely small p-values are truncated to exactly 0.
  */
 public class PearsonsCorrelationPValuesTest {

     // ---------------------------------------------------------------
     // r = 0  →  two-sided p-value must be 1.0 for any valid n
     // ---------------------------------------------------------------

     @Test
     public void testPValueForZeroCorrelation_n10() throws MathException {
         double[][] cov = {{1, 0}, {0, 1}};
         int n = 10;
         PearsonsCorrelation pc = new PearsonsCorrelation(new BlockRealMatrix(cov), n);
         RealMatrix p = pc.getCorrelationPValues();

         // diagonal: correlation=1 → p=0
         assertEquals(0.0, p.getEntry(0, 0), 1e-15);
         assertEquals(0.0, p.getEntry(1, 1), 1e-15);
         // off-diagonal: r=0 → p=1.0
         assertEquals(1.0, p.getEntry(0, 1), 1e-15);
         assertEquals(1.0, p.getEntry(1, 0), 1e-15);
     }

     @Test
     public void testPValueForZeroCorrelation_n20() throws MathException {
         double[][] cov = {{1, 0}, {0, 1}};
         int n = 20;
         PearsonsCorrelation pc = new PearsonsCorrelation(new BlockRealMatrix(cov), n);
         RealMatrix p = pc.getCorrelationPValues();

         assertEquals(1.0, p.getEntry(0, 1), 1e-15);
         assertEquals(1.0, p.getEntry(1, 0), 1e-15);
     }

     @Test
     public void testPValueForZeroCorrelation_n100() throws MathException {
         double[][] cov = {{1, 0}, {0, 1}};
         int n = 100;
         PearsonsCorrelation pc = new PearsonsCorrelation(new BlockRealMatrix(cov), n);
         RealMatrix p = pc.getCorrelationPValues();

         assertEquals(1.0, p.getEntry(0, 1), 1e-15);
     }

     @Test
     public void testPValueForZeroCorrelation_n3_df1() throws MathException {
         double[][] cov = {{1, 0}, {0, 1}};
         int n = 3; // df = 1
         PearsonsCorrelation pc = new PearsonsCorrelation(new BlockRealMatrix(cov), n);
         RealMatrix p = pc.getCorrelationPValues();

         assertEquals("r=0 must give p=1.0 even with df=1",
                 1.0, p.getEntry(0, 1), 1e-12);
     }

     // ---------------------------------------------------------------
     // Moderate correlation → p-value computed via t-distribution
     // ---------------------------------------------------------------

     @Test
     public void testPValueFor_r05_n10() throws MathException {
         double r = 0.5;
         double[][] cov = {{1, r}, {r, 1}};
         int n = 10;
         PearsonsCorrelation pc = new PearsonsCorrelation(new BlockRealMatrix(cov), n);
         RealMatrix p = pc.getCorrelationPValues();

         double t = Math.abs(r * Math.sqrt((n - 2) / (1 - r * r)));
         TDistribution tDist = new TDistributionImpl(n - 2);
         double expected = 2 * (1 - tDist.cumulativeProbability(t));

         assertEquals(expected, p.getEntry(0, 1), 1e-12);
         assertEquals(expected, p.getEntry(1, 0), 1e-12);
         // diagonal
         assertEquals(0.0, p.getEntry(0, 0), 1e-15);
     }

     // ---------------------------------------------------------------
     // Very high |r| → p-value must be positive (not exactly 0)
     // ---------------------------------------------------------------

     @Test
     public void testPValueNearZeroFor_r0999_n20() throws MathException {
         double r = 0.999;
         double[][] cov = {{1, r}, {r, 1}};
         int n = 20;
         PearsonsCorrelation pc = new PearsonsCorrelation(new BlockRealMatrix(cov), n);
         RealMatrix p = pc.getCorrelationPValues();

         double pVal = p.getEntry(0, 1);
         assertTrue("P-value for high correlation must be > 0", pVal > 0.0);
         assertTrue("P-value must be < 1e-3 for r=0.999", pVal < 1e-3);
     }

     @Test
     public void testPValueFor_r099_n100() throws MathException {
         double r = 0.99;
         double[][] cov = {{1, r}, {r, 1}};
         int n = 100;
         PearsonsCorrelation pc = new PearsonsCorrelation(new BlockRealMatrix(cov), n);
         RealMatrix p = pc.getCorrelationPValues();

         double pVal = p.getEntry(0, 1);
         assertTrue(pVal > 0);
         assertTrue(pVal < 1e-30); // extremely small but positive
     }

     // ---------------------------------------------------------------
     // r = ±1  →  p-value is 0 (t = ∞)
     // ---------------------------------------------------------------

     @Test
     public void testPValueForExactPositiveOne() throws MathException {
         double[][] cov = {{1, 1}, {1, 1}};
         int n = 5;
         PearsonsCorrelation pc = new PearsonsCorrelation(new BlockRealMatrix(cov), n);
         RealMatrix p = pc.getCorrelationPValues();

         assertEquals("p for r=1 should be 0", 0.0, p.getEntry(0, 1), 0.0);
     }

     @Test
     public void testPValueForExactNegativeOne() throws MathException {
         double[][] cov = {{1, -1}, {-1, 1}};
         int n = 5;
         PearsonsCorrelation pc = new PearsonsCorrelation(new BlockRealMatrix(cov), n);
         RealMatrix p = pc.getCorrelationPValues();

         assertEquals(0.0, p.getEntry(0, 1), 0.0);
     }

     // ---------------------------------------------------------------
     // Insufficient data (less than 2 rows or columns)
     // ---------------------------------------------------------------

     @Test(expected = IllegalArgumentException.class)
     public void testInsufficientData_TooFewRows() throws MathException {
         double[][] data = {{1.0, 2.0}}; // 1 row, 2 cols
         PearsonsCorrelation pc = new PearsonsCorrelation(data);
         // should throw before we ever call getCorrelationPValues
         pc.getCorrelationPValues();
     }

     @Test(expected = IllegalArgumentException.class)
     public void testInsufficientData_TooFewCols() throws MathException {
         double[][] data = {{1.0}, {2.0}}; // 2 rows, 1 col
         PearsonsCorrelation pc = new PearsonsCorrelation(data);
         pc.getCorrelationPValues();
     }

     // ---------------------------------------------------------------
     // Zero-variance column → correlation undefined → NaN p-values
     // ---------------------------------------------------------------

     @Test
     public void testPValueForZeroVarianceColumn() throws MathException {
         // column 1 is constant
         double[][] data = {
             {1.0, 5.0},
             {2.0, 5.0},
             {3.0, 5.0}
         };
         PearsonsCorrelation pc = new PearsonsCorrelation(new BlockRealMatrix(data));
         RealMatrix p = pc.getCorrelationPValues();

         // off-diagonal entries involve the zero-variance column → NaN
         assertTrue(Double.isNaN(p.getEntry(0, 1)));
         assertTrue(Double.isNaN(p.getEntry(1, 0)));
         // diagonal remains 0 (by construction)
         assertEquals(0.0, p.getEntry(0, 0), 0.0);
         assertEquals(0.0, p.getEntry(1, 1), 0.0);
     }

     // ---------------------------------------------------------------
     // Diagonal entries are always 0
     // ---------------------------------------------------------------

     @Test
     public void testDiagonalPValuesAreZero() throws MathException {
         double[][] cov = {{2.0, 0.8}, {0.8, 3.0}};
         PearsonsCorrelation pc = new PearsonsCorrelation(new BlockRealMatrix(cov), 15);
         RealMatrix p = pc.getCorrelationPValues();

         for (int i = 0; i < p.getRowDimension(); i++) {
             assertEquals("diagonal p-value must be 0", 0.0, p.getEntry(i, i), 0.0);
         }
     }

     // ---------------------------------------------------------------
     // Symmetry
     // ---------------------------------------------------------------

     @Test
     public void testPValuesMatrixIsSymmetric() throws MathException {
         double[][] cov = {{1.0, 0.4, 0.2}, {0.4, 1.0, 0.5}, {0.2, 0.5, 1.0}};
         PearsonsCorrelation pc = new PearsonsCorrelation(new BlockRealMatrix(cov), 30);
         RealMatrix p = pc.getCorrelationPValues();

         int dim = p.getRowDimension();
         for (int i = 0; i < dim; i++) {
             for (int j = 0; j < dim; j++) {
                 assertEquals(p.getEntry(i, j), p.getEntry(j, i), 1e-15);
             }
         }
     }
 }
