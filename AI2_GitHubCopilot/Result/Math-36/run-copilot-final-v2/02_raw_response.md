package org.apache.commons.math.fraction;

 import java.math.BigDecimal;
 import java.math.BigInteger;
 import java.math.RoundingMode;

 import org.junit.Test;
 import org.junit.Assert;

 /**
  * Tests for {@link BigFraction#floatValue()} and {@link BigFraction#doubleValue()}
  * targeting bug MATH-744 where large numerator/denominator cause NaN instead of
  * the correct finite value.
  */
 public class BigFractionTest {

     /**
      * A large base value (2^2000) whose doubleValue() overflows to Infinity.
      * Used with co-prime offsets so the fraction cannot be reduced to small components.
      */
     private static final BigInteger LARGE_BASE = BigInteger.ONE.shiftLeft(2000);

     /**
      * Compute the expected double value using BigDecimal division as a mathematical oracle.
      */
     private static double expectedDouble(BigInteger num, BigInteger den) {
         return new BigDecimal(num)
                 .divide(new BigDecimal(den), 100, RoundingMode.HALF_UP)
                 .doubleValue();
     }

     /**
      * Compute the expected float value using BigDecimal division as a mathematical oracle.
      */
     private static float expectedFloat(BigInteger num, BigInteger den) {
         return new BigDecimal(num)
                 .divide(new BigDecimal(den), 100, RoundingMode.HALF_UP)
                 .floatValue();
     }

     // ---------- Trigger cases from bug report ----------

     @Test
     public void testFloatValueForLargeNumeratorAndDenominator() {
         // Fraction ≈ 5.0 with both components too large for BigInteger.doubleValue()
         BigFraction f = new BigFraction(
                 LARGE_BASE.multiply(BigInteger.valueOf(5)).add(BigInteger.ONE),
                 LARGE_BASE);
         float expected = expectedFloat(f.getNumerator(), f.getDenominator());
         Assert.assertFalse("Float value must not be NaN for a finite large fraction",
                 Float.isNaN(f.floatValue()));
         Assert.assertFalse("Float value must not be infinite for a finite large fraction",
                 Float.isInfinite(f.floatValue()));
         Assert.assertEquals("Large fraction should approximate 5.0",
                 expected, f.floatValue(), 0.0f);
     }

     @Test
     public void testDoubleValueForLargeNumeratorAndDenominator() {
         BigFraction f = new BigFraction(
                 LARGE_BASE.multiply(BigInteger.valueOf(5)).add(BigInteger.ONE),
                 LARGE_BASE);
         double expected = expectedDouble(f.getNumerator(), f.getDenominator());
         Assert.assertFalse("Double value must not be NaN for a finite large fraction",
                 Double.isNaN(f.doubleValue()));
         Assert.assertFalse("Double value must not be infinite for a finite large fraction",
                 Double.isInfinite(f.doubleValue()));
         Assert.assertEquals("Large fraction should approximate 5.0",
                 expected, f.doubleValue(), 0.0);
     }

     // ---------- Large co-prime numbers giving approximately 1.0 ----------

     @Test
     public void testDoubleValueLargeFractionEqualsOne() {
         // (2^2000 + 1) / 2^2000 ≈ 1.0, co-prime so not reduced
         BigFraction f = new BigFraction(LARGE_BASE.add(BigInteger.ONE), LARGE_BASE);
         double expected = expectedDouble(f.getNumerator(), f.getDenominator());
         Assert.assertFalse("Must not be NaN", Double.isNaN(f.doubleValue()));
         Assert.assertEquals(expected, f.doubleValue(), 1e-15);
     }

     @Test
     public void testFloatValueLargeFractionEqualsOne() {
         BigFraction f = new BigFraction(LARGE_BASE.add(BigInteger.ONE), LARGE_BASE);
         float expected = expectedFloat(f.getNumerator(), f.getDenominator());
         Assert.assertFalse("Must not be NaN", Float.isNaN(f.floatValue()));
         Assert.assertEquals(expected, f.floatValue(), 1e-7f);
     }

     // ---------- Negative large fraction ----------

     @Test
     public void testDoubleValueLargeNegativeFraction() {
         // -(2^2000 + 1) / 2^2000 ≈ -1.0, both components overflow double
         BigFraction f = new BigFraction(
                 LARGE_BASE.negate().subtract(BigInteger.ONE),
                 LARGE_BASE);
         double expected = expectedDouble(f.getNumerator(), f.getDenominator());
         Assert.assertFalse("Must not be NaN", Double.isNaN(f.doubleValue()));
         Assert.assertEquals(expected, f.doubleValue(), 1e-15);
     }

     @Test
     public void testFloatValueLargeNegativeFraction() {
         BigFraction f = new BigFraction(
                 LARGE_BASE.negate().subtract(BigInteger.ONE),
                 LARGE_BASE);
         float expected = expectedFloat(f.getNumerator(), f.getDenominator());
         Assert.assertFalse("Must not be NaN", Float.isNaN(f.floatValue()));
         Assert.assertEquals(expected, f.floatValue(), 1e-7f);
     }

     // ---------- Basic values which should work regardless of bug ----------

     @Test
     public void testDoubleValueZero() {
         Assert.assertEquals(0.0, BigFraction.ZERO.doubleValue(), 0.0);
     }

     @Test
     public void testFloatValueZero() {
         Assert.assertEquals(0.0f, BigFraction.ZERO.floatValue(), 0.0f);
     }

     @Test
     public void testDoubleValueOne() {
         Assert.assertEquals(1.0, BigFraction.ONE.doubleValue(), 0.0);
     }

     @Test
     public void testFloatValueMinusOne() {
         Assert.assertEquals(-1.0f, BigFraction.MINUS_ONE.floatValue(), 0.0f);
     }

     // ---------- Very small fractions from large denominators ----------

     @Test
     public void testDoubleValueLargeVerySmallFraction() {
         // 1 / 2^2000 is an extremely small but finite positive double
         BigFraction f = new BigFraction(BigInteger.ONE, LARGE_BASE);
         double expected = expectedDouble(BigInteger.ONE, LARGE_BASE);
         Assert.assertFalse("Must not be NaN", Double.isNaN(f.doubleValue()));
         Assert.assertEquals(expected, f.doubleValue(), 0.0);
     }

     @Test
     public void testFloatValueLargeVerySmallFraction() {
         BigFraction f = new BigFraction(BigInteger.ONE, LARGE_BASE);
         float expected = expectedFloat(BigInteger.ONE, LARGE_BASE);
         Assert.assertFalse("Must not be NaN", Float.isNaN(f.floatValue()));
         Assert.assertEquals(expected, f.floatValue(), 0.0f);
     }
 }