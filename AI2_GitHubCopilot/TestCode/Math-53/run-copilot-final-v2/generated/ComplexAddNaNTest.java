package org.apache.commons.math.complex;

 import org.junit.Test;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;

 /**
  * JUnit test class that covers IEEE 754 NaN propagation rules for
  * {@link Complex#add(Complex)} and the NaN equality contract described
  * in the public specifications / Javadoc.
  *
  * <p>The tests are ordered from most fault‑related (NaN add / equals) to
  * normal, boundary, invalid and exception branches.</p>
  */
 public class ComplexAddNaNTest {

     // ---- NaN equality (equals contract) ---------------------------------

     @Test
     public void testNaNEqualsNaN() {
         assertTrue("Complex.NaN must equal itself",
                 Complex.NaN.equals(Complex.NaN));
     }

     @Test
     public void testNaNNotEqualsFinite() {
         assertFalse("NaN must not equal a finite complex",
                 Complex.NaN.equals(new Complex(1.0, 2.0)));
         assertFalse("Finite complex must not equal NaN",
                 new Complex(1.0, 2.0).equals(Complex.NaN));
     }

     // ---- NaN propagation through add ------------------------------------

     @Test
     public void testAddNaNZeroPlusNaN() {
         Complex z = Complex.ZERO.add(Complex.NaN);
         assertTrue("Result must be NaN when adding NaN to zero",
                 z.isNaN());
         assertEquals("Zero + NaN must equal Complex.NaN",
                 Complex.NaN, z);
     }

     @Test
     public void testAddNaNFinitePlusNaN() {
         Complex z = new Complex(1.0, 2.0).add(Complex.NaN);
         assertTrue("Result must be NaN when adding NaN to a finite complex",
                 z.isNaN());
         assertEquals("Finite + NaN must equal Complex.NaN",
                 Complex.NaN, z);
     }

     @Test
     public void testAddNaNNaNPlusNaN() {
         Complex z = Complex.NaN.add(Complex.NaN);
         assertTrue("Result must be NaN when adding NaN to NaN",
                 z.isNaN());
         assertEquals("NaN + NaN must equal Complex.NaN",
                 Complex.NaN, z);
     }

     @Test
     public void testAddNaNWithPositiveInfinity() {
         Complex z = Complex.INF.add(Complex.NaN);
         assertTrue("Result must be NaN when infinity is added to NaN",
                 z.isNaN());
         assertEquals("Complex.INF + NaN must equal Complex.NaN",
                 Complex.NaN, z);
     }

     // ---- Normal addition (sanity) ---------------------------------------

     @Test
     public void testAddNormal() {
         Complex a = new Complex(2.0, 3.0);
         Complex b = new Complex(4.0, 5.0);
         Complex sum = a.add(b);
         assertEquals("Real part incorrect (2 + 4)", 6.0, sum.getReal(), 0.0);
         assertEquals("Imaginary part incorrect (3 + 5)", 8.0, sum.getImaginary(), 0.0);
     }

     @Test
     public void testAddWithZero() {
         Complex a = new Complex(7.0, -2.0);
         Complex sum = a.add(Complex.ZERO);
         assertEquals("Adding Zero must not change value",
                 a, sum);
     }

     // ---- Null argument                                               ----

     @Test(expected = org.apache.commons.math.exception.NullArgumentException.class)
     public void testAddNullThrowsException() {
         Complex.ZERO.add(null);
     }

     // ---- Related operation: subtract propagates NaN ---------------------

     @Test
     public void testSubtractNaNReturnsNaN() {
         Complex z = Complex.ONE.subtract(Complex.NaN);
         assertTrue("Subtracting NaN must yield NaN", z.isNaN());
         assertEquals("1 - NaN must equal Complex.NaN",
                 Complex.NaN, z);
     }

     // ---- Divide propagates NaN (boundary) --------------------------------

     @Test
     public void testDivideNaNReturnsNaN() {
         Complex z = Complex.ONE.divide(Complex.NaN);
         assertTrue("Dividing by NaN must yield NaN", z.isNaN());
         assertEquals("1 / NaN must equal Complex.NaN",
                 Complex.NaN, z);
     }

     /**
      * Dividing a finite complex by zero must return NaN.
      */
     @Test
     public void testDivideByZeroReturnsNaN() {
         Complex z = new Complex(3.0, 4.0).divide(Complex.ZERO);
         assertTrue("Division by zero must produce NaN", z.isNaN());
     }
 }
