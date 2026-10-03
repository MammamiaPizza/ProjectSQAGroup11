package org.apache.commons.math3.complex;

 import static org.junit.Assert.*;

 import org.junit.Test;

 public class ComplexTest {

     // The reported bug: reciprocal of ZERO must be NaN, not Infinity.
     @Test
     public void testReciprocalZero() {
         Complex result = Complex.ZERO.reciprocal();
         assertTrue("Reciprocal of ZERO must be NaN", result.isNaN());
         assertTrue("Real part is NaN", Double.isNaN(result.getReal()));
         assertTrue("Imaginary part is NaN", Double.isNaN(result.getImaginary()));
     }

     @Test
     public void testReciprocalOfNaN() {
         Complex result = Complex.NaN.reciprocal();
         assertTrue("Reciprocal of NaN must be NaN", result.isNaN());
     }

     @Test
     public void testReciprocalOfInfinity() {
         Complex result = Complex.INF.reciprocal();
         assertEquals("Reciprocal of INF must be ZERO", Complex.ZERO, result);
     }

     @Test
     public void testReciprocalOfOne() {
         assertEquals("Reciprocal of ONE is ONE", Complex.ONE, Complex.ONE.reciprocal());
     }

     @Test
     public void testReciprocalOfI() {
         Complex result = Complex.I.reciprocal();
         assertEquals("Real part of 1/i", 0.0, result.getReal(), 1e-15);
         assertEquals("Imaginary part of 1/i", -1.0, result.getImaginary(), 1e-15);
     }

     @Test
     public void testReciprocalOfReal() {
         Complex z = new Complex(2.0, 0.0);
         Complex result = z.reciprocal();
         assertEquals("Real part of 1/2", 0.5, result.getReal(), 1e-15);
         assertEquals("Imaginary part of 1/2", 0.0, result.getImaginary(), 1e-15);
     }

     @Test
     public void testReciprocalOfComplex() {
         Complex z = new Complex(1.0, 1.0);
         Complex result = z.reciprocal();
         // expected: 0.5 - 0.5i
         assertEquals("Real part of 1/(1+i)", 0.5, result.getReal(), 1e-15);
         assertEquals("Imaginary part of 1/(1+i)", -0.5, result.getImaginary(), 1e-15);
     }

     @Test
     public void testReciprocalReciprocal() {
         Complex z = new Complex(2.0, 3.0);
         Complex r = z.reciprocal().reciprocal();
         assertEquals("Real part after double reciprocal", z.getReal(), r.getReal(), 1e-15);
         assertEquals("Imaginary part after double reciprocal", z.getImaginary(), r.getImaginary(),
1e-15);
     }

     @Test
     public void testReciprocalOfLargeReal() {
         Complex z = new Complex(Double.MAX_VALUE, 0.0);
         Complex result = z.reciprocal();
         assertFalse("Must not be NaN", result.isNaN());
         assertFalse("Must not be infinite", result.isInfinite());
         // expected 1/MAX_VALUE (approx)
         double expected = 1.0 / Double.MAX_VALUE;
         assertEquals("Reciprocal of large real", expected, result.getReal(), 1e-300);
         assertEquals("Imaginary part must be zero", 0.0, result.getImaginary(), 1e-15);
     }

     @Test
     public void testReciprocalOfSmallImaginary() {
         // Use a small value whose square does not underflow to zero
         double small = 1e-150;
         Complex z = new Complex(0.0, small);
         Complex result = z.reciprocal();
         assertFalse("Must not be NaN", result.isNaN());
         assertFalse("Must not be infinite", result.isInfinite());
         assertEquals("Real part must be zero", 0.0, result.getReal(), 1e-15);
         double expected = -1.0 / small;
         assertEquals("Imaginary part after small imaginary reciprocal", expected,
                 result.getImaginary(), 1e135);
     }

     @Test
     public void testDivideByZero() {
         Complex result = new Complex(3.0, 4.0).divide(Complex.ZERO);
         assertTrue("Division by (0,0) must yield NaN", result.isNaN());
     }

     @Test
     public void testDivideByZeroDouble() {
         Complex result = new Complex(2.0, 5.0).divide(0.0);
         assertTrue("Division by 0 double must yield NaN", result.isNaN());
     }

 }