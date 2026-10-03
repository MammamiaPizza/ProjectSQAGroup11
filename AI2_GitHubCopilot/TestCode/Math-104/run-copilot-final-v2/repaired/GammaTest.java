package org.apache.commons.math.special;

 import org.apache.commons.math.MathException;
 import org.apache.commons.math.MaxIterationsExceededException;
 import org.junit.Test;

 import static org.junit.Assert.*;

 /**
  * Tests for the Gamma class. Covers regularizedGammaP and regularizedGammaQ.
  */
 public class GammaTest {

     // ---- invalid / NaN args ----

     @Test
     public void testRegularizedGammaPNaNAndNonPositive() throws MathException {
         assertTrue(Double.isNaN(Gamma.regularizedGammaP(Double.NaN, 1.0)));
         assertTrue(Double.isNaN(Gamma.regularizedGammaP(1.0, Double.NaN)));
         assertTrue(Double.isNaN(Gamma.regularizedGammaP(0.0, 1.0)));
         assertTrue(Double.isNaN(Gamma.regularizedGammaP(-1.0, 1.0)));
         assertTrue(Double.isNaN(Gamma.regularizedGammaP(1.0, -0.1)));
     }

     @Test
     public void testRegularizedGammaQNaNAndNonPositive() throws MathException {
         assertTrue(Double.isNaN(Gamma.regularizedGammaQ(Double.NaN, 1.0)));
         assertTrue(Double.isNaN(Gamma.regularizedGammaQ(1.0, Double.NaN)));
         assertTrue(Double.isNaN(Gamma.regularizedGammaQ(0.0, 1.0)));
         assertTrue(Double.isNaN(Gamma.regularizedGammaQ(-2.0, 5.0)));
         assertTrue(Double.isNaN(Gamma.regularizedGammaQ(3.0, -0.5)));
     }

     // ---- x = 0 boundaries ----

     @Test
     public void testRegularizedGammaPZeroX() throws MathException {
         assertEquals(0.0, Gamma.regularizedGammaP(1.0, 0.0), 0.0);
         assertEquals(0.0, Gamma.regularizedGammaP(0.5, 0.0), 0.0);
         assertEquals(0.0, Gamma.regularizedGammaP(100.0, 0.0), 0.0);
     }

     @Test
     public void testRegularizedGammaQZeroX() throws MathException {
         assertEquals(1.0, Gamma.regularizedGammaQ(1.0, 0.0), 0.0);
         assertEquals(1.0, Gamma.regularizedGammaQ(0.1, 0.0), 0.0);
         assertEquals(1.0, Gamma.regularizedGammaQ(50.0, 0.0), 0.0);
     }

     // ---- known reference value (the failing trigger) ----

     @Test
     public void testRegularizedGammaPositivePositive() throws MathException {
         // Reference value from bug report: P(1,2) = 0.632120558828558
         double expected = 0.632120558828558;
         double result = Gamma.regularizedGammaP(1.0, 2.0);
         assertEquals(expected, result, 1e-10);
     }

     // ---- identity P + Q = 1 ----

     @Test
     public void testRegularizedGammaIdentity() throws MathException {
         double[][] params = {
             {0.5, 0.5},
             {1.0, 2.0},
             {2.0, 1.0},
             {3.0, 4.0},
             {5.0, 10.0},
             {10.0, 5.0},
         };
         for (double[] pair : params) {
             double a = pair[0];
             double x = pair[1];
             double p = Gamma.regularizedGammaP(a, x);
             double q = Gamma.regularizedGammaQ(a, x);
             assertEquals("P+Q != 1 for a=" + a + ", x=" + x,
                          1.0, p + q, 1e-12);
         }
     }

     // ---- large x behaviour ----

     @Test
     public void testRegularizedGammaPLargeX() throws MathException {
         // For fixed a, P(a, x) -> 1 as x -> +Infinity
         double p = Gamma.regularizedGammaP(2.0, 1000.0);
         assertTrue("P should be near 1 for a=2, x=1000: " + p, p > 0.999999);
     }

     @Test
     public void testRegularizedGammaQLargeX() throws MathException {
         // For fixed a, Q(a, x) -> 0 as x -> +Infinity
         double q = Gamma.regularizedGammaQ(3.0, 500.0);
         assertTrue("Q should be near 0 for a=3, x=500: " + q, q < 1e-6);
     }

     // ---- small x behaviour ----

     @Test
     public void testRegularizedGammaPSmallX() throws MathException {
         // For fixed a > 0, P(a, x) -> 0 as x -> 0+
         double p = Gamma.regularizedGammaP(2.0, 1e-10);
         assertTrue("P should be near 0 for a=2, x=1e-10: " + p, p < 1e-9);
     }

     @Test
     public void testRegularizedGammaQSmallX() throws MathException {
         // For fixed a > 0, Q(a, x) -> 1 as x -> 0+
         double q = Gamma.regularizedGammaQ(0.5, 1e-8);
         assertTrue("Q should be near 1 for a=0.5, x=1e-8: " + q, q > 0.99999);
     }

     // ---- iteration limit exceeded ----

     @Test(expected = MaxIterationsExceededException.class)
     public void testRegularizedGammaPMaxIterationsExceeded() throws MathException {
         // Too few iterations with extremely tight epsilon should trigger exception
         Gamma.regularizedGammaP(2.0, 10.0, 1e-20, 2);
     }

     @Test(expected = MaxIterationsExceededException.class)
     public void testRegularizedGammaQMaxIterationsExceeded() throws MathException {
         Gamma.regularizedGammaQ(2.0, 10.0, 1e-20, 2);
     }

 }
