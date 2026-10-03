package org.apache.commons.math.complex;

 import org.junit.Test;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;

 public class ComplexTanTanhTest {

     @Test
     public void testTanhPositiveInfinity() {
         Complex z = new Complex(Double.POSITIVE_INFINITY, 0.0).tanh();
         assertEquals(1.0, z.getReal(), 0.0);
         assertEquals(0.0, z.getImaginary(), 0.0);
     }

     @Test
     public void testTanhNegativeInfinity() {
         Complex z = new Complex(Double.NEGATIVE_INFINITY, 0.0).tanh();
         assertEquals(-1.0, z.getReal(), 0.0);
         assertEquals(0.0, z.getImaginary(), 0.0);
     }

     @Test
     public void testTanPositiveInfinityImaginary() {
         Complex z = new Complex(0.0, Double.POSITIVE_INFINITY).tan();
         assertEquals(0.0, z.getReal(), 0.0);
         assertEquals(1.0, z.getImaginary(), 0.0);
     }

     @Test
     public void testTanNegativeInfinityImaginary() {
         Complex z = new Complex(0.0, Double.NEGATIVE_INFINITY).tan();
         assertEquals(0.0, z.getReal(), 0.0);
         assertEquals(-1.0, z.getImaginary(), 0.0);
     }

     @Test
     public void testTanLargeImaginaryApproachesI() {
         Complex z = new Complex(0.0, 1e16).tan();
         assertEquals(0.0, z.getReal(), 1e-8);
         assertEquals(1.0, z.getImaginary(), 1e-8);
     }

     @Test
     public void testTanhLargeRealApproachesOne() {
         Complex z = new Complex(1e16, 0.0).tanh();
         assertEquals(1.0, z.getReal(), 1e-8);
         assertEquals(0.0, z.getImaginary(), 1e-8);
     }

     @Test
     public void testTanNaNReal() {
         Complex z = new Complex(Double.NaN, 1.0).tan();
         assertTrue(z.isNaN());
     }

     @Test
     public void testTanNaNImaginary() {
         Complex z = new Complex(0.0, Double.NaN).tan();
         assertTrue(z.isNaN());
     }

     @Test
     public void testTanhNaN() {
         Complex z = new Complex(Double.NaN, Double.NaN).tanh();
         assertTrue(z.isNaN());
     }

     @Test
     public void testTanZero() {
         Complex z = Complex.ZERO.tan();
         assertEquals(0.0, z.getReal(), 0.0);
         assertEquals(0.0, z.getImaginary(), 0.0);
     }
 }
