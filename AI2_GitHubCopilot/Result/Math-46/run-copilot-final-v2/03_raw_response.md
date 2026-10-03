package org.apache.commons.math.complex;

 import static org.apache.commons.math.complex.Complex.*;
 import static org.junit.Assert.*;
 import org.junit.Test;

 public class ComplexTestBug46 {

     private static final double DELTA = 1e-15;

     @Test
     public void testDivideNonZeroByZeroYieldsInfinity() {
         Complex num = new Complex(1, 2);
         Complex result = num.divide(ZERO);
         assertFalse("Should not be NaN", result.isNaN());
         assertTrue("Should be infinite", result.isInfinite());
     }

     @Test
     public void testDivideZeroByZeroYieldsNaN() {
         Complex result = ZERO.divide(ZERO);
         assertTrue("0/0 should be NaN", result.isNaN());
     }

     @Test
     public void testDivideNaNByZeroYieldsNaN() {
         Complex result = NaN.divide(ZERO);
         assertTrue("NaN/0 should be NaN", result.isNaN());
     }

     @Test
     public void testDivideInfinityByZero() {
         Complex result = INF.divide(ZERO);
         assertTrue("INF/0 must be infinite or NaN", result.isInfinite() || result.isNaN());
     }

     @Test
     public void testDivideZeroByNonZeroYieldsZero() {
         Complex divisor = new Complex(3, 4);
         Complex result = ZERO.divide(divisor);
         assertEquals("0/(3+4i) should be 0", ZERO, result);
     }

     @Test
     public void testDivideCorrectRatio() {
         // (6+2i) / (2+2i) = 2 - i
         Complex expected = new Complex(2, -1);
         Complex result = new Complex(6, 2).divide(new Complex(2, 2));
         assertEquals("(6+2i)/(2+2i) should be 2-i", expected, result);
     }

     @Test
     public void testAtanIisNotNaN() {
         Complex result = I.atan();
         assertFalse("atan(I) must not be NaN", result.isNaN());
     }

     @Test
     public void testAtanNegativeIisNotNaN() {
         Complex result = I.negate().atan();
         assertFalse("atan(-I) must not be NaN", result.isNaN());
     }

     @Test
     public void testAtanZeroReturnsZero() {
         Complex result = ZERO.atan();
         assertEquals("atan(0) should be 0", ZERO, result);
     }

     @Test
     public void testAtanOneReturnsPiOver4() {
         // atan(1+0i) = π/4 + 0i
         Complex result = ONE.atan();
         assertEquals("real part of atan(1)", Math.PI / 4, result.getReal(), DELTA);
         assertEquals("imag part of atan(1)", 0.0, result.getImaginary(), DELTA);
     }

     @Test
     public void testAtanNaNReturnsNaN() {
         Complex result = NaN.atan();
         assertTrue("atan(NaN) must be NaN", result.isNaN());
     }

     @Test
     public void testAtanInfinityDoesNotThrow() {
         // Just verify that atan(INF) computes without exception; result may be infinite or NaN.
         Complex result = INF.atan();
         assertNotNull(result);
     }
 }