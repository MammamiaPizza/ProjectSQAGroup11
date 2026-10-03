package org.apache.commons.math.complex;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;

 import org.junit.Test;

 /**
  * JUnit test for {@link Complex} focusing on equals and hashCode contract
  * compliance, especially regression of MATH-221 (negative-zero equality).
  */
 public class ComplexTest {

     @Test
     public void testEqualsSelf() {
         Complex c = new Complex(3.0, -4.5);
         assertTrue(c.equals(c));
     }

     @Test
     public void testEqualsNull() {
         assertFalse(Complex.ZERO.equals(null));
     }

     @Test
     public void testEqualsNonComplex() {
         assertFalse(Complex.ZERO.equals("not a complex"));
     }

     @Test
     public void testEqualsSameValues() {
         Complex a = new Complex(1.0, 2.0);
         Complex b = new Complex(1.0, 2.0);
         assertTrue(a.equals(b));
         assertTrue(b.equals(a));
     }

     @Test
     public void testHashCodeForEqualObjects() {
         Complex a = new Complex(-3.14, 1.618);
         Complex b = new Complex(-3.14, 1.618);
         assertEquals(a.hashCode(), b.hashCode());
     }

     @Test
     public void testEqualsNegativeZeroReal() {
         // MATH-221: -0.0 and 0.0 should be equal per Double contract
         Complex a = new Complex(-0.0, 1.5);
         Complex b = new Complex(0.0, 1.5);
         assertTrue(a.equals(b));
     }

     @Test
     public void testEqualsNegativeZeroImaginary() {
         Complex a = new Complex(2.5, -0.0);
         Complex b = new Complex(2.5, 0.0);
         assertTrue(a.equals(b));
     }

     @Test
     public void testEqualsNegativeZeroBothComponents() {
         Complex a = new Complex(-0.0, -0.0);
         assertTrue(a.equals(Complex.ZERO));
         assertTrue(Complex.ZERO.equals(a));
     }

     @Test
     public void testIsNaN() {
         // NaN flag should be true when any component is NaN
         assertTrue(Complex.NaN.isNaN());
         assertTrue(new Complex(Double.NaN, 1.0).isNaN());
         assertTrue(new Complex(1.0, Double.NaN).isNaN());
         assertFalse(Complex.ZERO.isNaN());
         assertFalse(Complex.ONE.isNaN());
         assertFalse(Complex.INF.isNaN());
     }

     @Test
     public void testIsInfinite() {
         assertTrue(Complex.INF.isInfinite());
         assertTrue(new Complex(Double.POSITIVE_INFINITY, 0.0).isInfinite());
         assertTrue(new Complex(0.0, Double.NEGATIVE_INFINITY).isInfinite());
         assertFalse(Complex.ZERO.isInfinite());
         assertFalse(Complex.ONE.isInfinite());
         assertFalse(Complex.NaN.isInfinite()); // NaN is not infinite
     }

     @Test
     public void testGetRealGetImaginary() {
         Complex c = new Complex(3.14, -2.72);
         assertEquals(3.14, c.getReal(), 0.0);
         assertEquals(-2.72, c.getImaginary(), 0.0);
     }

     @Test
     public void testHashCodeForNaN() {
         Complex nan1 = Complex.NaN;
         Complex nan2 = new Complex(Double.NaN, Double.NaN);
         assertTrue(nan1.isNaN() && nan2.isNaN());
         // All NaN Complex instances must have the same hash code
         assertEquals(nan1.hashCode(), nan2.hashCode());
     }
 }