package org.apache.commons.math3.fraction;

import org.junit.Test;
import static org.junit.Assert.*;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.FractionConversionException;

/**

 - JUnit tests for Fraction class focusing on overflow detection
 - and bug MATH-836: constructor reduction overflow.
  */
 public class FractionTest {
  @Test(expected = MathArithmeticException.class)
  public void testConstructorIntegerMinValueNegativeDenominator() {
  // num=Integer.MIN_VALUE, den=-1 should throw overflow
  new Fraction(Integer.MIN_VALUE, -1);
  }
  @Test(expected = MathArithmeticException.class)
  public void testConstructorDenominatorMinimumValue() {
  // den=Integer.MIN_VALUE (negative) triggers overflow check
  new Fraction(1, Integer.MIN_VALUE);
  }
  @Test
  public void testConstructorSignNormalization() {
  Fraction f = new Fraction(2, -4);
  assertEquals(-1, f.getNumerator());
  assertEquals(2, f.getDenominator());
  }
  @Test
  public void testConstructorReduction() {
  Fraction f = new Fraction(6, 8);
  assertEquals(3, f.getNumerator());
  assertEquals(4, f.getDenominator());
  }
  @Test(expected = MathArithmeticException.class)
  public void testMultiplyOverflowMinValueByNegativeOneFraction() {
  new Fraction(Integer.MIN_VALUE).multiply(new Fraction(-1));
  }
  @Test(expected = MathArithmeticException.class)
  public void testMultiplyOverflowMinValueByNegativeOneInt() {
  new Fraction(Integer.MIN_VALUE).multiply(-1);
  }
  @Test(expected = MathArithmeticException.class)
  public void testAddOverflowMaxValuePlusMaxValue() {
  new Fraction(Integer.MAX_VALUE).add(new Fraction(Integer.MAX_VALUE));
  }
  @Test(expected = MathArithmeticException.class)
  public void testSubtractOverflowMinValueMinusOne() {
  new Fraction(Integer.MIN_VALUE).subtract(new Fraction(1));
  }
  @Test(expected = MathArithmeticException.class)
  public void testNegateOverflowMinValue() {
  new Fraction(Integer.MIN_VALUE).negate();
  }
  @Test
  public void testNegateNoOverflow() {
  Fraction f = new Fraction(Integer.MIN_VALUE + 1).negate();
  assertEquals(-(Integer.MIN_VALUE + 1), f.getNumerator());
  assertEquals(1, f.getDenominator());
  }
  @Test
  public void testReciprocalOfZeroThrows() {
  try {
      new Fraction(0).reciprocal();
      fail("Should have thrown MathArithmeticException");
  } catch (MathArithmeticException e) {
      // expected
  }
  }
  @Test
  public void testEqualsAndHashCode() {
  Fraction a = new Fraction(2, 4);
  Fraction b = new Fraction(1, 2);
  assertEquals(a, b);
  assertEquals(a.hashCode(), b.hashCode());
  }

}
