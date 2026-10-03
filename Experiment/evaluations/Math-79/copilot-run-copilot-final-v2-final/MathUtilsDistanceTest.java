package org.apache.commons.math.util;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.fail;

 import org.junit.Test;

 public class MathUtilsDistanceTest {

     private static final double TOLERANCE = 1e-15;

     @Test
     public void testDistanceDoubleNormal() {
         double[] p1 = {1.0, 2.0, 3.0};
         double[] p2 = {4.0, 5.0, 6.0};
         double expected = Math.sqrt(27.0);
         assertEquals(expected, MathUtils.distance(p1, p2), TOLERANCE);
     }

     @Test
     public void testDistanceDoubleSingleElement() {
         double[] p1 = {2.0};
         double[] p2 = {5.0};
         assertEquals(3.0, MathUtils.distance(p1, p2), TOLERANCE);
     }

     @Test
     public void testDistanceIntNormal() {
         int[] p1 = {1, 2};
         int[] p2 = {4, 6};
         assertEquals(5.0, MathUtils.distance(p1, p2), TOLERANCE);
     }

     @Test
     public void testDistance1DoubleNormal() {
         double[] p1 = {1.0, -2.0, 5.0};
         double[] p2 = {4.0, 3.0, -5.0};
         assertEquals(18.0, MathUtils.distance1(p1, p2), TOLERANCE);
     }

     @Test
     public void testDistance1IntNormal() {
         int[] p1 = {1, -2, 5};
         int[] p2 = {4, 3, -5};
         assertEquals(18, MathUtils.distance1(p1, p2));
     }

     @Test
     public void testDistanceInfDoubleNormal() {
         double[] p1 = {1.0, -5.0, 3.0};
         double[] p2 = {4.0, 1.0, -7.0};
         assertEquals(10.0, MathUtils.distanceInf(p1, p2), TOLERANCE);
     }

     @Test
     public void testDistanceInfIntNormal() {
         int[] p1 = {1, -5, 3};
         int[] p2 = {4, 1, -7};
         assertEquals(10, MathUtils.distanceInf(p1, p2));
     }

     @Test(expected = NullPointerException.class)
     public void testNullFirstArgumentDouble() {
         MathUtils.distance(null, new double[]{1.0, 2.0});
     }

     @Test(expected = NullPointerException.class)
     public void testNullSecondArgumentDouble() {
         MathUtils.distance(new double[]{1.0, 2.0}, null);
     }

     @Test
     public void testEmptyArraysDouble() {
         assertEquals(0.0, MathUtils.distance(new double[0], new double[0]), TOLERANCE);
     }

     @Test(expected = ArrayIndexOutOfBoundsException.class)
     public void testLengthMismatchDouble() {
         MathUtils.distance(new double[]{1.0, 2.0}, new double[]{3.0});
     }

     @Test
     public void testNullArgumentsInt() {
         try {
             MathUtils.distance((int[]) null, new int[]{1, 2});
             fail("Expected NullPointerException for null first argument");
         } catch (NullPointerException e) {
             // expected
         }
         try {
             MathUtils.distance(new int[]{1, 2}, (int[]) null);
             fail("Expected NullPointerException for null second argument");
         } catch (NullPointerException e) {
             // expected
         }
     }
 }
