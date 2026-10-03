package org.apache.commons.lang3.math;

import static org.junit.Assert.*;
import org.junit.Test;

public class FractionTest {

 @Test
 public void testReduceMinValueOverTwo() {
     Fraction f = Fraction.getFraction(Integer.MIN_VALUE, 2);
     Fraction r = f.reduce();
     assertEquals(-1073741824, r.getNumerator());
     assertEquals(1, r.getDenominator());
 }

 @Test
 public void testGetReducedFractionMinValueOverTwo() {
     Fraction f = Fraction.getReducedFraction(Integer.MIN_VALUE, 2);
     assertEquals(-1073741824, f.getNumerator());
     assertEquals(1, f.getDenominator());
 }

 @Test
 public void testGetReducedFractionMinValueOverNegativeTwo() {
     try {
         Fraction.getReducedFraction(Integer.MIN_VALUE, -2);
         fail("Expected ArithmeticException for Integer.MIN_VALUE / -2");
     } catch (ArithmeticException e) {
         // expected
     }
 }

 @Test
 public void testGetReducedFractionMinValueOverFour() {
     Fraction f = Fraction.getReducedFraction(Integer.MIN_VALUE, 4);
     assertEquals(-536870912, f.getNumerator());
     assertEquals(1, f.getDenominator());
 }

 @Test
 public void testGetReducedFractionMaxValueOverMaxValue() {
     Fraction f = Fraction.getReducedFraction(Integer.MAX_VALUE, Integer.MAX_VALUE);
     assertEquals(1, f.getNumerator());
     assertEquals(1, f.getDenominator());
 }

 @Test
 public void testReduceNormal() {
     Fraction f = Fraction.getFraction(6, 8);
     Fraction r = f.reduce();
     assertEquals(3, r.getNumerator());
     assertEquals(4, r.getDenominator());
 }

 @Test
 public void testReduceZeroNumerator() {
     Fraction f = Fraction.getFraction(0, 5);
     Fraction r = f.reduce();
     assertEquals(0, r.getNumerator());
     assertEquals(1, r.getDenominator());
 }

 @Test
 public void testReduceDenominatorAlreadyPositive() {
     Fraction f = Fraction.getFraction(3, -6);
     Fraction r = f.reduce();
     assertEquals(-1, r.getNumerator());
     assertEquals(2, r.getDenominator());
 }

 @Test
 public void testReduceXOverOne() {
     Fraction f = Fraction.getFraction(42, 1);
     Fraction r = f.reduce();
     assertEquals(42, r.getNumerator());
     assertEquals(1, r.getDenominator());
 }

 @Test
 public void testGetReducedFractionLargeSafe() {
     Fraction f = Fraction.getReducedFraction(100, 200);
     assertEquals(1, f.getNumerator());
     assertEquals(2, f.getDenominator());
 }

 @Test
 public void testGetReducedFractionNegativeDenominatorSafe() {
     Fraction f = Fraction.getReducedFraction(6, -8);
     assertEquals(-3, f.getNumerator());
     assertEquals(4, f.getDenominator());
 }

 @Test
 public void testGetReducedFractionZeroDenominator() {
     try {
         Fraction.getReducedFraction(1, 0);
         fail("Expected ArithmeticException for denominator zero");
     } catch (ArithmeticException e) {
         // expected
     }
 }

}
