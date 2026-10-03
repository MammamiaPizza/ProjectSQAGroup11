package org.apache.commons.math3.util;

 import org.apache.commons.math3.exception.ConvergenceException;
 import org.apache.commons.math3.exception.MaxCountExceededException;
 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for {@link ContinuedFraction} targeting the MATH-718 / MATH-785 bug
  * where the modified Lentz algorithm can produce NaN for convergent inputs,
  * especially when the evaluation point x = 0.5 leads to numerical instability.
  */
 public class ContinuedFractionTest {

     // ---- concrete subclasses for different coefficient patterns ----

     /** Golden-ratio continued fraction: a_n = 1, b_n = 1  →  φ ≈ 1.6180339887. */
     private static class GoldenRatio extends ContinuedFraction {
         @Override protected double getA(int n, double x) { return 1.0; }
         @Override protected double getB(int n, double x) { return 1.0; }
     }

     /**
      * Deliberately triggers NaN at x = 0.5 by returning zero for the first
      * two a-coefficients and the first b-coefficient, causing 0/0 in the
      * delta computation.  For other x values the fraction converges normally.
      */
     private static class HalfNanTrigger extends ContinuedFraction {
         @Override protected double getA(int n, double x) {
             if (x == 0.5) { if (n == 0) return 0.0; if (n == 1) return 0.0; }
             return 1.0;
         }
         @Override protected double getB(int n, double x) {
             if (x == 0.5 && n == 1) return 0.0;
             return 1.0;
         }
     }

     /** Slowly-converging fraction: a_0=1, a_n=1/n (n≥1), b_n=1. */
     private static class SlowConverging extends ContinuedFraction {
         @Override protected double getA(int n, double x) { return n == 0 ? 1.0 : 1.0 / n; }
         @Override protected double getB(int n, double x) { return 1.0; }
     }

     /** Coefficients large enough to overflow doubles, forcing the scaling path. */
     private static class OverflowTrigger extends ContinuedFraction {
         @Override protected double getA(int n, double x) { return 1e250; }
         @Override protected double getB(int n, double x) { return 1e250; }
     }

     /** Alternating coefficients that produce a true divergence to infinity. */
     private static class InfinityDivergent extends ContinuedFraction {
         @Override protected double getA(int n, double x) { return n == 0 ? 1.0 : 2.0 * n; }
         @Override protected double getB(int n, double x) { return 0.1; }  // very small denominator
     }

     /** First a-coefficient is zero; remaining coefficients define a convergent CF. */
     private static class ZeroFirstA extends ContinuedFraction {
         @Override protected double getA(int n, double x) { return n == 0 ? 0.0 : 1.0; }
         @Override protected double getB(int n, double x) { return 1.0; }
     }

     /** Coefficients depend on x; for x=0 a==0 so the fraction is simpler. */
     private static class XDependent extends ContinuedFraction {
         @Override protected double getA(int n, double x) { return n == 0 ? 1.0 : x * n; }
         @Override protected double getB(int n, double x) { return n == 0 ? 0.0 : (1.0 - x) * n; }
     }

     /** All coefficients zero — should diverge immediately to NaN. */
     private static class AllZero extends ContinuedFraction {
         @Override protected double getA(int n, double x) { return 0.0; }
         @Override protected double getB(int n, double x) { return 0.0; }
     }

     // ======================== tests ========================

     @Test
     public void testGoldenRatioConverges() {
         GoldenRatio cf = new GoldenRatio();
         double result = cf.evaluate(0.0);  // x is irrelevant for constant coefficients
         double phi = (1.0 + Math.sqrt(5.0)) / 2.0;
         assertEquals("Golden ratio continued fraction should converge to φ",
                 phi, result, 1e-8);
     }

     @Test
     public void testNanTriggerAtHalf() {
         // x = 0.5 causes intermediate 0/0 → NaN; the fix should prevent NaN
         // and produce a finite result instead of throwing ConvergenceException.
         double result = new HalfNanTrigger().evaluate(0.5);
         assertFalse("Result should be finite after NaN fix", Double.isNaN(result));
         assertFalse("Result should be finite after NaN fix", Double.isInfinite(result));
     }

     @Test
     public void testHalfNanTriggerConvergesAtOtherX() {
         // Same fraction converges normally when x ≠ 0.5
         HalfNanTrigger cf = new HalfNanTrigger();
         double result = cf.evaluate(0.3);
         assertFalse("Result should be finite for x=0.3", Double.isNaN(result));
         assertFalse("Result should be finite for x=0.3", Double.isInfinite(result));
     }

     @Test
     public void testAllZeroCoefficients() {
         // Every coefficient is zero; the fix prevents NaN and returns a finite result.
         double result = new AllZero().evaluate(1.0);
         assertFalse("All-zero fraction should produce finite result after fix",
                 Double.isNaN(result));
         assertFalse("All-zero fraction should produce finite result after fix",
                 Double.isInfinite(result));
     }

     @Test(expected = MaxCountExceededException.class)
     public void testMaxIterationsExceeded() {
         // Slow-converging fraction with only 5 iterations is insufficient
         new SlowConverging().evaluate(0.0, 1e-15, 5);
     }

     @Test
     public void testSlowConvergingWithEnoughIterations() {
         // Same fraction converges given enough iterations
         SlowConverging cf = new SlowConverging();
         double result = cf.evaluate(0.0, 1e-8, 100000);
         assertFalse("Should converge with enough iterations", Double.isNaN(result));
         assertFalse("Should converge with enough iterations", Double.isInfinite(result));
     }

     @Test
     public void testScalingHandlesLargeCoefficients() {
         // Coefficients that would overflow doubles without scaling
         OverflowTrigger cf = new OverflowTrigger();
         double result = cf.evaluate(0.0);
         assertFalse("Overflow-trigger fraction should produce finite result",
                 Double.isNaN(result) || Double.isInfinite(result));
     }

     @Test
     public void testInfinityDivergence() {
         // This continued fraction diverges to infinity; the fix handles this
         // without producing NaN. The result should be infinite or an exception.
         try {
             double result = new InfinityDivergent().evaluate(0.0);
             assertTrue("Infinity-divergent fraction should produce infinite result",
                     Double.isInfinite(result));
         } catch (ConvergenceException e) {
             // Divergence detected by the algorithm — acceptable outcome
         }
     }

     @Test
     public void testTightEpsilonYieldsBetterPrecision() {
         GoldenRatio cf = new GoldenRatio();
         double phi = (1.0 + Math.sqrt(5.0)) / 2.0;
         double loose = cf.evaluate(0.0, 1e-3, Integer.MAX_VALUE);
         double tight = cf.evaluate(0.0, 1e-12, Integer.MAX_VALUE);
         double looseErr = Math.abs(loose - phi);
         double tightErr = Math.abs(tight - phi);
         assertTrue("Tighter epsilon should give smaller error",
                 tightErr <= looseErr);
     }

     @Test
     public void testZeroFirstACoefficientConverges() {
         // hPrev is set to 'small' when getA(0)==0, but remaining
         // coefficients produce a well-defined continued fraction.
         ZeroFirstA cf = new ZeroFirstA();
         double result = cf.evaluate(0.0);
         assertFalse("Fraction with zero first a should converge",
                 Double.isNaN(result) || Double.isInfinite(result));
     }

     @Test
     public void testEvaluateOverloads() {
         GoldenRatio cf = new GoldenRatio();
         double r1 = cf.evaluate(0.0);
         double r2 = cf.evaluate(0.0, 1e-9);
         double r3 = cf.evaluate(0.0, 1000);
         double r4 = cf.evaluate(0.0, 1e-9, 1000);
         double phi = (1.0 + Math.sqrt(5.0)) / 2.0;
         assertEquals("Default evaluate()", phi, r1, 1e-8);
         assertEquals("evaluate(x,epsilon)", phi, r2, 1e-8);
         assertEquals("evaluate(x,maxIter)", phi, r3, 1e-8);
         assertEquals("evaluate(x,epsilon,maxIter)", phi, r4, 1e-8);
     }

     @Test
     public void testXDependentCoefficients() {
         // x = 0  ⇒  all a_n=0 (n≥1), b0=0 so the fraction evaluates to b0 = 0
         XDependent cf = new XDependent();
         double resultAt0 = cf.evaluate(0.0);
         assertEquals("x=0 gives b0 only", 0.0, resultAt0, 1e-9);

         // x = 0.3 ⇒ fraction converges to some finite value
         double resultAt03 = cf.evaluate(0.3);
         assertFalse("x=0.3 should converge", Double.isNaN(resultAt03) ||
                 Double.isInfinite(resultAt03));

         // x = 0.7 ⇒ also convergent
         double resultAt07 = cf.evaluate(0.7);
         assertFalse("x=0.7 should converge", Double.isNaN(resultAt07) ||
                 Double.isInfinite(resultAt07));
     }
 }