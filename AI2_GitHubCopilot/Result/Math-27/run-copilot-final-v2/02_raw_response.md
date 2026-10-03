package org.apache.commons.math3.fraction;

 import org.junit.Test;
 import static org.junit.Assert.*;
 import org.apache.commons.math3.exception.FractionConversionException;
 import org.apache.commons.math3.util.ArithmeticUtils;

 /**
  * JUnit test that exposes the overflow / sign bug (MATH-835) in Fraction constructors
  * for large double values near 2.17e9.  Only public API is used; every test has meaningful
  * assertions targeting the documented failure and basic contract checks.
  */
 public class FractionBug27Test {

     private static void assertReduced(Fraction f) {
         if (f.getNumerator() == 0) {
             assertEquals("Zero fraction must have denominator 1", 1, f.getDenominator());
             return;
         }
         int g = ArithmeticUtils.gcd(Math.abs(f.getNumerator()), Math.abs(f.getDenominator()));
         assertEquals("Fraction not reduced: " + f, 1, g);
     }

     // ------------------------------------------------------------------------
     // 1. NaN throws FractionConversionException
     // ------------------------------------------------------------------------
     @Test(expected = FractionConversionException.class)
     public void testNaNThrowsException() {
         new Fraction(Double.NaN);
     }

     // ------------------------------------------------------------------------
     // 2. Positive infinity throws FractionConversionException
     // ------------------------------------------------------------------------
     @Test(expected = FractionConversionException.class)
     public void testPositiveInfinityThrowsException() {
         new Fraction(Double.POSITIVE_INFINITY);
     }

     // ------------------------------------------------------------------------
     // 3. Negative infinity throws FractionConversionException
     // ------------------------------------------------------------------------
     @Test(expected = FractionConversionException.class)
     public void testNegativeInfinityThrowsException() {
         new Fraction(Double.NEGATIVE_INFINITY);
     }

     // ------------------------------------------------------------------------
     // 4. Zero value → ZERO fraction (0/1, reduced, sign consistent)
     // ------------------------------------------------------------------------
     @Test
     public void testZeroValue() {
         Fraction f = new Fraction(0.0);
         assertEquals("Numerator", 0, f.getNumerator());
         assertEquals("Denominator", 1, f.getDenominator());
         assertEquals(0.0, f.doubleValue(), 0.0);
         assertEquals(Fraction.ZERO, f);
     }

     // ------------------------------------------------------------------------
     // 5. Small strictly representable double → exact reduced fraction
     // ------------------------------------------------------------------------
     @Test
     public void testSmallFractionExact() {
         Fraction f = new Fraction(0.25);
         assertEquals(1, f.getNumerator());
         assertEquals(4, f.getDenominator());
         assertEquals(0.25, f.doubleValue(), 0.0);
         assertReduced(f);
     }

     // ------------------------------------------------------------------------
     // 6. Large positive double (the exact trigger value from MATH-835)
     //    Buggy version produces a negative doubleValue; fixed version stays positive.
     // ------------------------------------------------------------------------
     @Test
     public void testLargePositiveDoubleSignAndRecovery() {
         double value = 2.1691754E9;
         Fraction f = new Fraction(value);
         assertTrue("Numerator must be positive", f.getNumerator() > 0);
         assertTrue("Denominator must be positive", f.getDenominator() > 0);
         assertTrue("doubleValue must be positive", f.doubleValue() > 0);
         // Default epsilon = 1e-5 → doubleValue within 1 % of the input
         assertEquals(value, f.doubleValue(), value * 0.01);
         assertReduced(f);
     }

     // ------------------------------------------------------------------------
     // 7. Large negative double → fraction sign consistent (negative numerator)
     // ------------------------------------------------------------------------
     @Test
     public void testLargeNegativeDoubleSign() {
         double value = -2.1691754E9;
         Fraction f = new Fraction(value);
         assertTrue("Numerator must be negative", f.getNumerator() < 0);
         assertTrue("Denominator must be positive", f.getDenominator() > 0);
         assertTrue("doubleValue must be negative", f.doubleValue() < 0);
         assertEquals(value, f.doubleValue(), Math.abs(value) * 0.01);
         assertReduced(f);
     }

     // ------------------------------------------------------------------------
     // 8. Double exactly representable as Integer.MAX_VALUE – 1
     // ------------------------------------------------------------------------
     @Test
     public void testValueJustBelowIntegerMaxValue() {
         double value = (double) (Integer.MAX_VALUE - 1);
         Fraction f = new Fraction(value);
         assertEquals(Integer.MAX_VALUE - 1, f.getNumerator());
         assertEquals(1, f.getDenominator());
         assertEquals(value, f.doubleValue(), 0.0);
     }

     // ------------------------------------------------------------------------
     // 9. Double value exactly Integer.MAX_VALUE + 1 (just above MAX_VALUE)
     //    Fixed version should either represent it or throw; buggy must not
     //    produce a negative value.
     // ------------------------------------------------------------------------
     @Test
     public void testValueJustAboveIntegerMaxValue() {
         double value = (double) ((long) Integer.MAX_VALUE + 1L);
         try {
             Fraction f = new Fraction(value);
             // If conversion succeeds, result must be positive.
             assertTrue("doubleValue must be positive", f.doubleValue() > 0);
             assertEquals(value, f.doubleValue(), 1.0);  // integer value should be exact
             assertReduced(f);
         } catch (FractionConversionException e) {
             // throwing is also acceptable for values that cannot be represented
             assertTrue("Exception message should mention the value",
                        e.getMessage().contains(Double.toString(value)));
         }
     }

     // ------------------------------------------------------------------------
     // 10. Fraction(double, int) with small maxDenominator – approximation
     // ------------------------------------------------------------------------
     @Test
     public void testMaxDenominatorApproximation() {
         Fraction f = new Fraction(0.333, 10);
         // best approximation within denominator ≤ 10
         assertTrue("Denominator ≤ 10", f.getDenominator() <= 10);
         assertEquals(1.0 / 3.0, f.doubleValue(), 0.05);
         assertReduced(f);
     }

     // ------------------------------------------------------------------------
     // 11. Fraction(double, double, int) with explicit epsilon and maxIterations
     // ------------------------------------------------------------------------
     @Test
     public void testEpsilonAndMaxIterations() {
         Fraction f = new Fraction(0.333, 0.01, 20);
         // Must be within epsilon
         assertEquals(0.333, f.doubleValue(), 0.01);
         assertReduced(f);
     }

     // ------------------------------------------------------------------------
     // 12. Reduction guarantee for a simple value
     // ------------------------------------------------------------------------
     @Test
     public void testReductionGuaranteed() {
         Fraction f = new Fraction(0.5);
         assertEquals("Reduced to 1/2", 1, f.getNumerator());
         assertEquals("Reduced to 1/2", 2, f.getDenominator());
     }
 }