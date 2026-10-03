package org.apache.commons.math.dfp;

 import org.junit.Before;
 import org.junit.Test;
 import static org.junit.Assert.*;

 public class DfpTest {
     private DfpField field;

     @Before
     public void setUp() {
         field = new DfpField(25);
     }

     @Test
     public void testDivideUnderflowToZero() {
         Dfp tiny = field.getZero().newInstance("1e-30000");
         Dfp huge = field.getZero().newInstance("1e30000");
         Dfp result = tiny.divide(huge);
         assertFalse("Expected finite, not infinite", result.isInfinite());
         assertFalse(result.isNaN());
         assertEquals(Dfp.FINITE, result.classify());
         assertEquals(0.0, result.toDouble(), 0.0);
     }

     @Test
     public void testSubtractSelfZero() {
         Dfp a = field.getZero().newInstance("1234.5678");
         Dfp result = a.subtract(a);
         assertFalse(result.isInfinite());
         assertFalse(result.isNaN());
         assertEquals(Dfp.FINITE, result.classify());
         assertEquals(0.0, result.toDouble(), 0.0);
     }

     @Test
     public void testAddNegateZero() {
         Dfp a = field.getZero().newInstance("0.0001");
         Dfp negA = a.negate();
         Dfp sum = a.add(negA);
         assertFalse(sum.isInfinite());
         assertFalse(sum.isNaN());
         assertEquals(Dfp.FINITE, sum.classify());
         assertEquals(0.0, sum.toDouble(), 0.0);
     }

     @Test
     public void testSqrtTinyPositive() {
         Dfp small = field.getZero().newInstance("1e-20000");
         Dfp root = small.sqrt();
         assertFalse(root.isInfinite());
         assertFalse(root.isNaN());
         assertEquals(Dfp.FINITE, root.classify());
         assertTrue(root.toDouble() >= 0.0);
     }

     @Test
     public void testTruncTinyPositiveToZero() {
         Dfp small = field.getZero().newInstance("0.0000000001");
         Dfp truncated = small.trunc(DfpField.RoundingMode.DOWN);
         assertFalse(truncated.isInfinite());
         assertFalse(truncated.isNaN());
         assertEquals(0.0, truncated.toDouble(), 0.0);
     }

     @Test
     public void testRintTinyPositive() {
         Dfp small = field.getZero().newInstance("0.0000000001");
         Dfp rounded = small.rint();
         assertFalse(rounded.isInfinite());
         assertFalse(rounded.isNaN());
         assertEquals(0.0, rounded.toDouble(), 0.0);
     }

     @Test
     public void testMultiplyUnderflowToZero() {
         Dfp tiny1 = field.getZero().newInstance("1e-20000");
         Dfp tiny2 = field.getZero().newInstance("1e-20000");
         Dfp product = tiny1.multiply(tiny2);
         assertFalse(product.isInfinite());
         assertFalse(product.isNaN());
         assertEquals(Dfp.FINITE, product.classify());
         assertEquals(0.0, product.toDouble(), 0.0);
     }

     @Test
     public void testDivideZeroByNonZero() {
         Dfp zero = field.getZero();
         Dfp nonZero = field.getZero().newInstance("1.0");
         Dfp result = zero.divide(nonZero);
         assertFalse(result.isInfinite());
         assertFalse(result.isNaN());
         assertEquals(Dfp.FINITE, result.classify());
         assertEquals(0.0, result.toDouble(), 0.0);
     }

     @Test
     public void testCreateNearMinExpFinite() {
         Dfp nearMin = field.getZero().newInstance("1e-32500");
         assertFalse("Number near MIN_EXP should be fine", nearMin.isInfinite());
         assertFalse(nearMin.isNaN());
         assertEquals(Dfp.FINITE, nearMin.classify());
     }

     @Test
     public void testDivideWithSmallNumbers() {
         Dfp a = field.getZero().newInstance("1e-25000");
         Dfp b = field.getZero().newInstance("1e25000");
         Dfp res = a.divide(b);
         assertFalse(res.isInfinite());
         assertFalse(res.isNaN());
         assertEquals(Dfp.FINITE, res.classify());
         double d = res.toDouble();
         assertTrue("Result should be 0.0, not -Infinity", d > -0.001 && d < 0.001);
     }

     @Test
     public void testNegateInfinity() {
         Dfp inf = field.getZero().newInstance((byte)1, (byte) Dfp.INFINITE);
         Dfp negInf = inf.negate();
         assertTrue(negInf.isInfinite());
         assertFalse(negInf.isNaN());
         assertTrue(Double.isInfinite(negInf.toDouble()) && negInf.toDouble() < 0.0);
     }

     @Test
     public void testFloorTinyPositive() {
         Dfp small = field.getZero().newInstance("0.0000000001");
         Dfp floor = small.floor();
         assertFalse(floor.isInfinite());
         assertFalse(floor.isNaN());
         assertEquals(0.0, floor.toDouble(), 0.0);
     }
 }
