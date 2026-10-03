package org.apache.commons.math.linear;

import java.math.BigDecimal;
import org.junit.Test;
import static org.junit.Assert.*;

public class Math209Test {
    @Test(expected = IllegalArgumentException.class)
    public void testMultiplyBigMatrixImplIncompatibleDimensions() {
        BigMatrixImpl a = new BigMatrixImpl(new double[][]{{1,2,3},{4,5,6}});
        BigMatrixImpl b = new BigMatrixImpl(new double[][]{{1,2},{3,4},{5,6},{7,8}});
        a.multiply(b);
    }

 @Test(expected = IllegalArgumentException.class)
 public void testMultiplyRealMatrixImplIncompatibleDimensions() {
     RealMatrixImpl a = new RealMatrixImpl(new double[][]{{1,2,3},{4,5,6}});
     RealMatrixImpl b = new RealMatrixImpl(new double[][]{{1,2},{3,4},{5,6},{7,8}});
     a.multiply(b);
 }

 @Test
 public void testMultiplyBigMatrixImplValid() {
     BigMatrixImpl a = new BigMatrixImpl(new double[][]{{1,2},{3,4},{5,6}});
     BigMatrixImpl b = new BigMatrixImpl(new double[][]{{7,8,9},{10,11,12}});
     BigMatrix result = a.multiply(b);
     double[][] expected = {{27,30,33},{61,68,75},{95,106,117}};
     assertNotNull(result);
     assertEquals(3, result.getRowDimension());
     assertEquals(3, result.getColumnDimension());
     for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 3; j++) {
             assertEquals(new BigDecimal(expected[i][j]), result.getEntry(i, j));
         }
     }
 }

 @Test
 public void testMultiplyRealMatrixImplValid() {
     RealMatrixImpl a = new RealMatrixImpl(new double[][]{{1,2},{3,4},{5,6}});
     RealMatrixImpl b = new RealMatrixImpl(new double[][]{{7,8,9},{10,11,12}});
     RealMatrix result = a.multiply(b);
     double[][] expected = {{27,30,33},{61,68,75},{95,106,117}};
     assertNotNull(result);
     assertEquals(3, result.getRowDimension());
     assertEquals(3, result.getColumnDimension());
     for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 3; j++) {
             assertEquals(expected[i][j], result.getEntry(i, j), 0.0);
         }
     }
 }

 @Test
 public void testMultiplyBigMatrixImpl_1x1() {
     BigMatrixImpl a = new BigMatrixImpl(new double[][]{{5}});
     BigMatrixImpl b = new BigMatrixImpl(new double[][]{{7}});
     BigMatrix result = a.multiply(b);
     assertEquals(BigDecimal.valueOf(35), result.getEntry(0,0));
 }

 @Test
 public void testMultiplyRealMatrixImpl_1x1() {
     RealMatrixImpl a = new RealMatrixImpl(new double[][]{{5}});
     RealMatrixImpl b = new RealMatrixImpl(new double[][]{{7}});
     RealMatrix result = a.multiply(b);
     assertEquals(35.0, result.getEntry(0,0), 0.0);
 }

 @Test(expected = IllegalArgumentException.class)
 public void testMultiplyBigMatrixImpl_3x2_by_3x2_incompatible() {
     BigMatrixImpl a = new BigMatrixImpl(new double[][]{{1,2},{3,4},{5,6}});
     BigMatrixImpl b = new BigMatrixImpl(new double[][]{{7,8},{9,10},{11,12}});
     a.multiply(b);
 }

 @Test(expected = IllegalArgumentException.class)
 public void testMultiplyRealMatrixImpl_3x2_by_3x2_incompatible() {
     RealMatrixImpl a = new RealMatrixImpl(new double[][]{{1,2},{3,4},{5,6}});
     RealMatrixImpl b = new RealMatrixImpl(new double[][]{{7,8},{9,10},{11,12}});
     a.multiply(b);
 }

 @Test(expected = IllegalArgumentException.class)
 public void testMultiplyBigMatrixInterfaceIncompatible() {
     BigMatrix a = new BigMatrixImpl(new double[][]{{1,2,3},{4,5,6}});
     BigMatrix b = new BigMatrixImpl(new double[][]{{1,2},{3,4},{5,6},{7,8}});
     a.multiply(b);
 }

 @Test(expected = IllegalArgumentException.class)
 public void testMultiplyRealMatrixInterfaceIncompatible() {
     RealMatrix a = new RealMatrixImpl(new double[][]{{1,2,3},{4,5,6}});
     RealMatrix b = new RealMatrixImpl(new double[][]{{1,2},{3,4},{5,6},{7,8}});
     a.multiply(b);
 }

}
