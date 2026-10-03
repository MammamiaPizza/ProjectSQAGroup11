package org.apache.commons.math.linear;

 import static org.junit.Assert.assertEquals;

 import org.junit.Test;

 /**
  * Tests for {@link OpenMapRealMatrix} covering MATH-679 (constructor overflow).
  */
 public class OpenMapRealMatrixTest {

     @Test
     public void testConstructorNormal() {
         OpenMapRealMatrix m = new OpenMapRealMatrix(3, 4);
         assertEquals(3, m.getRowDimension());
         assertEquals(4, m.getColumnDimension());
         assertEquals(0.0, m.getEntry(0, 0), 0.0);
     }

     @Test(expected = org.apache.commons.math.exception.NotStrictlyPositiveException.class)
     public void testConstructorZeroRow() {
         new OpenMapRealMatrix(0, 5);
     }

     @Test(expected = org.apache.commons.math.exception.NotStrictlyPositiveException.class)
     public void testConstructorZeroCol() {
         new OpenMapRealMatrix(5, 0);
     }

     @Test(expected = org.apache.commons.math.exception.NotStrictlyPositiveException.class)
     public void testConstructorNegativeRow() {
         new OpenMapRealMatrix(-1, 3);
     }

     @Test(expected = org.apache.commons.math.exception.NumberIsTooLargeException.class)
     public void testConstructorOverflow46341() {
         new OpenMapRealMatrix(46341, 46341);
     }

     @Test(expected = org.apache.commons.math.exception.NumberIsTooLargeException.class)
     public void testConstructorOverflowNegativeProduct() {
         // Product overflows and becomes negative.
         new OpenMapRealMatrix(65536, 32768);
     }

     @Test(expected = org.apache.commons.math.exception.NumberIsTooLargeException.class)
     public void testConstructorBoundaryMax() {
         new OpenMapRealMatrix(1, Integer.MAX_VALUE);
     }

     @Test(expected = org.apache.commons.math.exception.NumberIsTooLargeException.class)
     public void testCreateMatrixOverflow() {
         OpenMapRealMatrix base = new OpenMapRealMatrix(3, 4);
         base.createMatrix(46341, 46341);
     }

     @Test
     public void testCopy() {
         OpenMapRealMatrix original = new OpenMapRealMatrix(2, 3);
         original.setEntry(0, 1, 5.0);
         OpenMapRealMatrix copy = original.copy();
         assertEquals(5.0, copy.getEntry(0, 1), 0.0);
         copy.setEntry(0, 1, 10.0);
         assertEquals(5.0, original.getEntry(0, 1), 0.0);
     }

     @Test
     public void testMultiplyOpenMap() {
         OpenMapRealMatrix a = new OpenMapRealMatrix(2, 3);
         a.setEntry(0, 0, 1);
         a.setEntry(0, 2, 2);
         a.setEntry(1, 1, 3);
         OpenMapRealMatrix b = new OpenMapRealMatrix(3, 2);
         b.setEntry(0, 1), 4);
         b.setEntry(2, 0), 5);
         b.setEntry(1, 0), 6);
         RealMatrix c = a.multiply(b);
         assertEquals(2, c.getRowDimension());
         assertEquals(2, c.getColumnDimension());
         assertEquals(10.0, c.getEntry(0, 0), 1e-12));
         assertEquals(4.0, c.getEntry(0, 1), 1e-12));
         assertEquals(18.0, c.getEntry(1, 0), 1e-12));
         assertEquals(0.0, c.getEntry(1, 1), 1e-12));
     }

     @Test
     public void testMultiplyRealMatrix() {
         OpenMapRealMatrix a = new OpenMapRealMatrix(2, 2);
         a.setEntry(0, 0, 2);
         a.setEntry(1, 1, 3);
         RealMatrix b = new BlockRealMatrix(2, 2);
         b.setEntry(0, 0, 5);
         b.setEntry(1, 1), 7);
         RealMatrix c = a.multiply(b);
         assertEquals(10.0, c.getEntry(0, 0), 0.0));
         assertEquals(21.0, c.getEntry(1, 1), 0.0));
     }

     @Test
     public void testAddToEntry() {
         OpenMapRealMatrix m = new OpenMapRealMatrix(2, 2);
         m.addToEntry(0, 0, 3.0);
         assertEquals(3.0, m.getEntry(0, 0), 0.0);
         m.addToEntry(0, 0, -3.0);
         assertEquals(0.0, m.getEntry(0, 0), 0.0));
         m.addToEntry(0, 0, 2.0);
         m.addToEntry(0, 0, 3.0);
         assertEquals(5.0, m.getEntry(0, 0), 0.0);
     }

     @Test
     public void testMultiplyEntry() {
         OpenMapRealMatrix m = new OpenMapRealMatrix(2, 2);
         m.setEntry(0, 0, 4.0);
         m.multiplyEntry(0, 0, 2.0);
         assertEquals(8.0, m.getEntry(0, 0), 0.0);
         m.multiplyEntry(0, 0, 0.0);
         assertEquals(0.0, m.getEntry(0, 0), 0.0);
     }
 }