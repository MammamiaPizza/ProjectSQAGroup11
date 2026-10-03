package org.apache.commons.math3.stat.inference;

 import org.apache.commons.math3.distribution.NormalDistribution;
 import org.apache.commons.math3.exception.NoDataException;
 import org.apache.commons.math3.exception.NullArgumentException;
 import org.junit.Assert;
 import org.junit.Test;

 /**
  * Tests for {@link MannWhitneyUTest} focusing on MATH-790 integer overflow.
  */
 public class MannWhitneyUTestTest {

     private final MannWhitneyUTest testStatistic = new MannWhitneyUTest();

     // ---- Helper: compute U correctly using long arithmetic ----

     /**
      * Computes Umax (max(U1, U2)) manually with long arithmetic to avoid int overflow.
      * @return [Umax, Umin]
      */
     private double[] computeUManual(double[] x, double[] y) {
         int n1 = x.length;
         int n2 = y.length;
         int total = n1 + n2;

         // Build combined array and index map
         double[] combined = new double[total];
         System.arraycopy(x, 0, combined, 0, n1);
         System.arraycopy(y, 0, combined, n1, n2);

         // Sort indices by value
         Integer[] idx = new Integer[total];
         for (int i =0; i < total; i++) idx[i] = i;
         java.util.Arrays.sort(idx, new java.util.Comparator<Integer>() {
             public int compare(Integer a, Integer b) {
                 return Double.compare(combined[a], combined[b]));
             }
         });

         // Assign average ranks for ties
         double[] ranks = new double[total];
         int i =0;
         while (i < total) {
             int j = i;
             while (j < total && combined[idx[j]] == combined[idx[i]]) j++;
             double avgRank = (i +1 + j) /2.0;
             for (int k = i; k < j; k++) ranks[idx[k]] = avgRank;
             i = j;
         }

         // Sum ranks for x samples
         double sumRankX =0.0;
         for (int k =0; k < n1; k++) sumRankX += ranks[k];

         // U1 = sumRankX - n1*(n1+1)/2   using long
         long term = (long) n1 * (n1 +1) / 2;
         double U1 = sumRankX - term;
         double U2 = (double) ((long) n1 * n2) - U1;

         return new double[]{Math.max(U1, U2), Math.min(U1, U2)};
     }

     /**
      * Compute expected asymptotic p-value using all-long arithmetic.
      */
     private double computePValueManual(double[] x, double[] y) {
         double[] uv = computeUManual(x, y);
         double Umin = uv[1];
         int n1 = x.length;
         int n2 = y.length;
         long n1n2 = (long) n1 * n2;
         double EU = n1n2 / 2.0;
         double VarU = n1n2 * (n1 + n2 + 1) / 12.0;
         double z = (Umin - EU) / Math.sqrt(VarU);
         return 2 * new NormalDistribution(0, 1).cumulativeProbability(z);
     }

     // ---- Fault-related: MATH-790 integer overflow ----

     @Test
     public void testBigDataSet() {
         // MATH-790: int overflow in (x.length * (x.length + 1)) / 2
         // n = 46341 triggers overflow because 46341*46342 > Integer.MAX_VALUE
         final int n1 = 46341;
         final int n2 = 46341;

         double[] x = new double[n1];
         double[] y = new double[n2];
         for (int i = 0; i < n1; i++) x[i] = i;
         for (int i = 0; i < n2; i++) y[i] = n1 + i + 1;

         // With all y > all x:
         // sumRankX = n1*(n1+1)/2, U1 = 0, U2 = n1*n2, Umax = n1*n2
         double actual = testStatistic.mannWhitneyU(x, y);
         long expected = (long) n1 * n2;

         Assert.assertEquals("mannWhitneyU with large equal samples (MATH-790))",
                 (double) expected, actual, 0.0);
     }

     @Test
     public void testBigDataSetPValue() {
         // MATH-790 also hits n1n2prod overflow in calculateAsymptoticPValue
         final int n1 = 46341;
         final int n2 = 46341;

         double[] x = new double[n1];
         double[] y = new double[n2];
         for (int i =0; i < n1; i++) x[i] = i;
         for (int i = 0; i < n2; i++) y[i] = n1 + i +1;

         double actual = testStatistic.mannWhitneyUTest(x, y);
         double expected = computePValueManual(x, y);

         Assert.assertEquals("Asymptotic p-value with large samples (MATH-790))",
                 expected, actual, 1e-10);
     }

     // ---- Normal small-sample behaviour ----

     @Test
     public void testSmallSamples() {
         double[] x = {1, 3, 5, 7, 9};
         double[] y = {2, 4, 6, 8, 10};

         double U = testStatistic.mannWhitneyU(x, y);

         // Ranks: x={1,3,5,7,9} -> {1,3,5,7,9}; sumRankX=25
         // U1 =25 -5*6/2 =10; U2=25-10=15; Umax=15
         Assert.assertEquals(15.0, U, 0.0);
     }

     @Test
     public void testUnequalSampleSizes() {
         double[] x = {2.0, 4.0, 6.0};
         double[] y = {1.0, 3.0, 5.0, 7.0, 8.0};

         double U = testStatistic.mannWhitneyU(x, y);

         // Combined sorted:1,2,3,4,5,6,7,8 -> ranks:1,2,3,4,5,6,7,8
         // x ranks: 2,4,6 -> sumRankX=12
         // U1 =12-3*4/2 =6; U2=3*5-6=9; Umax=9
         Assert.assertEquals(9.0, U, 0.0);
     }

     // ---- Boundary: ties ----

     @Test
     public void testAllTies() {
         double[] x = {5, 5, 5};
         double[] y = {5, 5, 5};

         double U = testStatistic.mannWhitneyU(x, y);

         // All 6 ranks = (1+2+3+4+5+6)/6 =3.5
         // sumRankX =3*3.5 =10.5
         // U1 =10.5 -3*4/2 =4.5; U2 =9-4.5 =4.5; Umax =4.5
         Assert.assertEquals(4.5, U, 0.0);
     }

     @Test
     public void testWithTies() {
         double[] x = {1.0, 2.0, 2.0, 3.0};
         double[] y = {2.0, 2.0, 4.0};

         // Use manual computation for expected
         double[] expected = computeUManual(x, y);
         double actual = testStatistic.mannWhitneyU(x, y);
         Assert.assertEquals("Umax with ties", expected[0], actual, 1e-10);
     }

     // ---- Boundary: one sample completely dominates ----

     @Test
     public void testAllXGreaterThanY() {
         double[] x = {10, 20, 30};
         double[] y = {1, 2, 3};

         double U = testStatistic.mannWhitneyU(x, y);

         // Combined sorted:1,2,3,10,20,30 -> ranks:1,2,3,4,5,6
         // x ranks:4,5,6 -> sumRankX=15
         // U1 =15-3*4/2 =9; U2=9-9=0; Umax=9
         Assert.assertEquals(9.0, U, 0.0);
     }

     @Test
     public void testAllYGreaterThanX() {
         double[] x = {1, 2, 3};
         double[] y = {10, 20, 30};

         double U = testStatistic.mannWhitneyU(x, y);

         // Combined: 1,2,3,10,20,30 -> ranks: 1,2,3,4,5,6
         // x ranks: 1,2,3 -> sumRankX=6
         // U1 = 6 - 3*4/2 = 0; U2 = 9 - 0 = 9; Umax = 9
         Assert.assertEquals(9.0, U, 0.0);
     }

     // ---- Boundary: separated samples p-value ----

     @Test
     public void testCompletelySeparatedSamples() {
         double[] x = {1, 2, 3, 4};
         double[] y = {5, 6, 7, 8};

         double p = testStatistic.mannWhitneyUTest(x, y);

         // All y > all x: n1=n2=4 => Umin=0
         // EU =8, VarU =16*9/12=12, z =(0-8)/sqrt(12) ≈ -2.3094
         // p =2*Φ(-2.3094) ≈0.0209
         double expected = computePValueManual(x, y);
         Assert.assertEquals("P-value for completely separated samples", expected, p, 1e-10);
         Assert.assertTrue(p <0.06);
     }

     @Test
     public void testIdenticalSamples() {
         double[] x = {1.0, 2.0, 3.0, 4.0};
         double[] y = {1.0, 2.0, 3.0, 4.0};

         double p = testStatistic.mannWhitneyUTest(x, y);

         // Identical samples: U1 = U2 = n1*n2/2 => Umax = Umin = 8
         // z = (8-8)/sqrt(VarU) =0 => p =2*Φ(0) =1.0
         double expected = computePValueManual(x, y);
         Assert.assertEquals("P-value for identical samples", expected, p, 1e-10);
         Assert.assertTrue("P-value near 1.0 for identical samples", p >0.99);
     }

     // ---- Invalid / exception branches ----

     @Test(expected = NullArgumentException.class)
     public void testNullX() {
         testStatistic.mannWhitneyU(null, new double[]{1, 2, 3});
     }

     @Test(expected = NullArgumentException.class)
     public void testNullY() {
         testStatistic.mannWhitneyU(new double[]{1, 2, 3}, null);
     }

     @Test(expected = NoDataException.class)
     public void testEmptyX() {
         testStatistic.mannWhitneyU(new double[0], new double[]{1, 2, 3});
     }

     @Test(expected = NoDataException.class)
     public void testEmptyY() {
         testStatistic.mannWhitneyU(new double[]{1, 2, 3}, new double[0]);
     }

     @Test(expected = NullArgumentException.class)
     public void testNullXForUTest() {
         testStatistic.mannWhitneyUTest(null, new double[]{1, 2, 3});
     }

     @Test(expected = NullArgumentException.class)
     public void testNullYForUTest() {
         testStatistic.mannWhitneyUTest(new double[]{1, 2, 3}, null);
     }

     // ---- Property: symmetry ----

     @Test
     public void testSymmetric() {
         double[] x = {2, 4, 6, 8};
         double[] y = {1, 3, 5, 7};

         double Uxy = testStatistic.mannWhitneyU(x, y);
         double Uyx = testStatistic.mannWhitneyU(y, x);

         Assert.assertEquals("U statistic should be symmetric", Uxy, Uyx, 0.0);
     }
 }```
