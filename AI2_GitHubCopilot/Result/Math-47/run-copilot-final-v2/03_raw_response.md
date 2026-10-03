package org.apache.commons.math.complex;

 import static org.junit.Assert.*;

 import org.junit.Test;
 import org.apache.commons.math.exception.NullArgumentException;

 /**
  * JUnit tests for Complex focused on divide and atan bugs (MATH-657).
  */
 public class ComplexTest {

     @Test(expected = NullArgumentException.class)
     public void testDivideByNull() {
         Complex.ONE.divide((Complex) null);
     }

     @Test
     public void testDivideByZero_BothZero() {
         Complex result = Complex.ZERO.divide(Complex.ZERO);
         assertTrue("0/0 must be NaN", result.isNaN());
     }

     @Test
     public void testDivideByZero_OneByZero() {
         Complex result = Complex.ONE.divide(Complex.ZERO);
         assertTrue("1/0 must be infinite", result.isInfinite());
         assertFalse("1/0 must not be NaN", result.isNaN());
     }

     @Test
     public void testDivideByZero_InfByZero() {
         Complex result = Complex.INF.divide(Complex.ZERO);
         assertTrue("Inf/0 must be infinite", result.isInfinite());
         assertFalse("Inf/0 must not be NaN", result.isNaN());
     }

     @Test
     public void testDivideByZero_NanByZero() {
         Complex result = Complex.Nan.divide(Complex.ZERO);
         assertTrue("NaN/0 must be NaN", result.isNaN());
     }

     @Test
     public void testDivideNormal() {
         Complex result = new Complex(1.0, 1.0).divide(new Complex(2.0, 0.0));
         assertEquals(0.5, result.getReal(), 1e-15);
         assertEquals(0.5, result.getImaginary(), 1e-15);
     }

     @Test
     public void testDivideInfByInf() {
         Complex result = Complex.INF.divide(Complex.INF);
         assertTrue("Inf/Inf must be NaN", result.isNaN());
     }

     @Test
     public void testDivideFiniteByInf() {
         Complex result = Complex.ONE.divide(Complex.INF);
         assertEquals(Complex.ZERO, result);
     }

     @Test
     public void testAtanI() {
         Complex result = Complex.I.atan();
         assertTrue("atan(I) must be infinite", result.isInfinite());
         assertFalse("atan(I) must not be NaN", result.isNaN());
     }

     @Test
     public void testAtanZero() {
         Complex result = Complex.ZERO.atan();
         assertEquals(0.0, result.getReal(), 0.0);
         assertEquals(0.0, result.getImaginary(), 0.0);
     }

     @Test
     public void testAtanNan() {
         Complex result = Complex.Nan.atan();
         assertTrue("atan(NaN) must be NaN", result.isNaN());
     }

     @Test
     public void testAtanReal() {
         Complex result = Complex.valuef(2.0).atan();
         assertEquals(Math.atan(2.0), result.getReal(), 1e-15);
         assertEquals(0.0, result.getImaginary(), 1e-15);
     }
 }