package org.apache.commons.math3.fraction;

 import java.math.BigInteger;
 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for Fraction(double,int) and BigFraction(double,int) digit-limit constructors.
  * Regression tests for MATH-996: simple representable values must not cause Overflow.
  */
 public class DigitLimitConstructorTest {

     // ---------- Fraction normal cases ----------

     @Test
     public void testFractionDigitLimitNormal() {
         // Exactly representable rationals
         Fraction f1 = new Fraction(0.5, 1000);
         assertEquals(0, f1.compareTo(Fraction.ONE_HALF));
         assertEquals(1, f1.getNumerator());
         assertEquals(2, f1.getDenominator());

         Fraction f2 = new Fraction(0.25, 10);
         assertEquals(0, f2.compareTo(Fraction.ONE_QUARTER));
         assertEquals(1, f2.getNumerator());
         assertEquals(4, f2.getDenominator());

         Fraction f3 = new Fraction(0.2, 10);
         assertEquals(1, f3.getNumerator());
         assertEquals(5, f3.getDenominator());

         Fraction f4 = new Fraction(0.0, 100);
         assertEquals(0, f4.compareTo(Fraction.ZERO));
         assertEquals(0, f4.getNumerator());
         assertEquals(1, f4.getDenominator());

         Fraction f5 = new Fraction(1.0, 5);
         assertEquals(0, f5.compareTo(Fraction.ONE));
         assertEquals(1, f5.getNumerator());
         assertEquals(1, f5.getDenominator());
     }

     // ---------- BigFraction normal cases ----------

     @Test
     public void testBigFractionDigitLimitNormal() {
         BigFraction f1 = new BigFraction(0.5, 1000);
         assertEquals(0, f1.compareTo(BigFraction.ONE_HALF));
         assertEquals(BigInteger.ONE, f1.getNumerator());
         assertEquals(BigInteger.valueOf(2), f1.getDenominator());

         BigFraction f2 = new BigFraction(0.25, 10);
         assertEquals(0, f2.compareTo(BigFraction.ONE_QUARTER));
         assertEquals(BigInteger.ONE, f2.getNumerator());
         assertEquals(BigInteger.valueOf(4), f2.getDenominator());

         BigFraction f3 = new BigFraction(0.2, 10);
         assertEquals(BigInteger.ONE, f3.getNumerator());
         assertEquals(BigInteger.valueOf(5), f3.getDenominator());

         BigFraction f4 = new BigFraction(0.0, 100);
         assertEquals(0, f4.compareTo(BigFraction.ZERO));
         assertEquals(BigInteger.ZERO, f4.getNumerator());
         assertEquals(BigInteger.ONE, f4.getDenominator());

         BigFraction f5 = new BigFraction(1.0, 5);
         assertEquals(0, f5.compareTo(BigFraction.ONE));
     }

     // ---------- MATH-996 regression: no Overflow for simple values ----------

     @Test
     public void testFractionNoOverflowForSimpleValues() {
         // These must never throw FractionConversionException for reasonable maxDenominator
         new Fraction(0.5, 100);
         new Fraction(0.5, 1000);
         new Fraction(0.5, Integer.MAX_VALUE / 2);
         new Fraction(0.25, 1000);
         new Fraction(0.2, 100);
         new Fraction(0.125, 1000);
         new Fraction(0.1, 100);
     }

     @Test
     public void testBigFractionNoOverflowForSimpleValues() {
         new BigFraction(0.5, 100);
         new BigFraction(0.5, 1000);
         new BigFraction(0.5, Integer.MAX_VALUE / 2);
         new BigFraction(0.25, 1000);
         new BigFraction(0.2, 100);
         new BigFraction(0.125, 1000);
         new BigFraction(0.1, 100);
     }

     // ---------- Rounding / approximation ----------

     @Test
     public void testFractionDigitLimitRounding() {
         // 0.333... with maxDen=10 should give 1/3
         Fraction f1 = new Fraction(1.0 / 3.0, 10);
         assertEquals(0, f1.compareTo(Fraction.ONE_THIRD));

         // 0.666... with maxDen=10 -> 2/3
         Fraction f2 = new Fraction(2.0 / 3.0, 10);
         assertEquals(0, f2.compareTo(Fraction.TWO_THIRDS));

         // An irrational-like value approximated within bound
         Fraction f3 = new Fraction(Math.PI, 10);
         assertTrue("denominator must not exceed maxDen",
                    f3.getDenominator() <= 10);
     }

     @Test
     public void testBigFractionDigitLimitRounding() {
         BigFraction f1 = new BigFraction(1.0 / 3.0, 10);
         assertEquals(0, f1.compareTo(BigFraction.ONE_THIRD));

         BigFraction f2 = new BigFraction(2.0 / 3.0, 10);
         assertEquals(0, f2.compareTo(BigFraction.TWO_THIRDS));

         BigFraction f3 = new BigFraction(Math.PI, 10);
         assertTrue("denominator must not exceed maxDen",
                    f3.getDenominator().compareTo(BigInteger.TEN) <= 0);
     }

     // ---------- Boundary: maxDenominator = 1 ----------

     @Test
     public void testFractionMaxDenominatorOne() {
         // 0.5 cannot be represented with maxDen=1; constructor should throw or
         // return a rounded integer. Accept either behaviour.
         try {
             Fraction f1 = new Fraction(0.5, 1);
             assertEquals(1, f1.getDenominator());
             assertTrue("should round to nearest integer",
                        f1.getNumerator() == 0 || f1.getNumerator() == 1);
         } catch (FractionConversionException e) {
             // acceptable: constructor may reject unrepresentable values
         }

         // 0.0 with maxDen=1 -> 0/1
         Fraction f2 = new Fraction(0.0, 1);
         assertEquals(0, f2.compareTo(Fraction.ZERO));

         // 1.0 with maxDen=1 -> 1/1
         Fraction f3 = new Fraction(1.0, 1);
         assertEquals(0, f3.compareTo(Fraction.ONE));
     }

     @Test
     public void testBigFractionMaxDenominatorOne() {
         // 0.5 cannot be represented with maxDen=1; constructor should throw or
         // return a rounded integer. Accept either behaviour.
         try {
             BigFraction f1 = new BigFraction(0.5, 1);
             assertEquals(BigInteger.ONE, f1.getDenominator());
             assertTrue("should round to nearest integer",
                        BigInteger.ZERO.equals(f1.getNumerator())
                        || BigInteger.ONE.equals(f1.getNumerator()));
         } catch (FractionConversionException e) {
             // acceptable: constructor may reject unrepresentable values
         }

         BigFraction f2 = new BigFraction(0.0, 1);
         assertEquals(0, f2.compareTo(BigFraction.ZERO));

         BigFraction f3 = new BigFraction(1.0, 1);
         assertEquals(0, f3.compareTo(BigFraction.ONE));
     }

     // ---------- Negative values ----------

     @Test
     public void testFractionNegativeValues() {
         Fraction f1 = new Fraction(-0.5, 1000);
         assertEquals(-1, f1.getNumerator());
         assertEquals(2, f1.getDenominator());

         Fraction f2 = new Fraction(-1.0 / 3.0, 10);
         assertEquals(-1, f2.getNumerator());
         assertEquals(3, f2.getDenominator());

         Fraction f3 = new Fraction(-1.0, 5);
         assertEquals(0, f3.compareTo(Fraction.MINUS_ONE));
     }

     @Test
     public void testBigFractionNegativeValues() {
         BigFraction f1 = new BigFraction(-0.5, 1000);
         assertEquals(BigInteger.valueOf(-1), f1.getNumerator());
         assertEquals(BigInteger.valueOf(2), f1.getDenominator());

         BigFraction f2 = new BigFraction(-1.0 / 3.0, 10);
         assertEquals(BigInteger.valueOf(-1), f2.getNumerator());
         assertEquals(BigInteger.valueOf(3), f2.getDenominator());

         BigFraction f3 = new BigFraction(-1.0, 5);
         assertEquals(0, f3.compareTo(BigFraction.MINUS_ONE));
     }

     // ---------- Exception when denominator cannot meet maxDen ----------

     @Test
     public void testFractionMaxDenominatorExceeded() {
         // A value whose best approximation may require a larger denominator than maxDen=2.
         // The constructor should return a valid fraction or throw.
         try {
             Fraction f = new Fraction(0.123456789, 2);
             assertTrue("denominator must not exceed maxDen", f.getDenominator() <= 2);
         } catch (FractionConversionException e) {
             // acceptable if the implementation rejects this input
         }
     }

     @Test
     public void testBigFractionMaxDenominatorExceeded() {
         try {
             BigFraction f = new BigFraction(0.123456789, 2);
             assertTrue("denominator must not exceed maxDen",
                        f.getDenominator().compareTo(BigInteger.valueOf(2)) <= 0);
         } catch (FractionConversionException e) {
             // acceptable if the implementation rejects this input
         }
     }
 }