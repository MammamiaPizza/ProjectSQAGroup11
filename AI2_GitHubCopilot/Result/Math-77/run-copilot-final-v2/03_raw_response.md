package org.apache.commons.math.linear;

 import org.junit.Test;
 import static org.junit.Assert.*;

 public class Math77BugTest {

     private static final double DELTA = 1e-12;

     // ----------- ArrayRealVector addition -----------

     @Test
     public void testArrayAddition() {
         ArrayRealVector v1 = new ArrayRealVector(new double[]{1, 2, 3});
         ArrayRealVector v2 = new ArrayRealVector(new double[]{4, 5, 6});

         // add(double[])
         RealVector sumDouble = v1.add(new double[]{4, 5, 6});
         assertArrayEquals("add(double[]) failed", new double[]{5, 7, 9}, sumDouble.toArray(),
DELTA);
         // original unchanged
         assertArrayEquals("original modified", new double[]{1, 2, 3}, v1.toArray(), DELTA);

         // add(RealVector) – v2 is a RealVector
         RealVector sumRV = v1.add((RealVector) v2);
         assertArrayEquals("add(RealVector) failed", new double[]{5, 7, 9}, sumRV.toArray(), DELTA);

         // add(ArrayRealVector)
         ArrayRealVector sumArr = v1.add(v2);
         assertArrayEquals("add(ArrayRealVector) failed", new double[]{5, 7, 9}, sumArr.toArray(),
DELTA);

         // identity
         ArrayRealVector zero = new ArrayRealVector(3, 0.0);
         RealVector sumZero = v1.add((RealVector) zero);
         assertArrayEquals("add zero failed", v1.toArray(), sumZero.toArray(), DELTA);
     }

     // ----------- ArrayRealVector subtraction -----------

     @Test
     public void testArraySubtraction() {
         ArrayRealVector v1 = new ArrayRealVector(new double[]{10, 20, 30});
         ArrayRealVector v2 = new ArrayRealVector(new double[]{1, 2, 3});

         // subtract(double[])
         RealVector subDouble = v1.subtract(new double[]{1, 2, 3});
         assertArrayEquals("subtract(double[]) failed", new double[]{9, 18, 27},
subDouble.toArray(), DELTA);

         // subtract(RealVector)
         RealVector subRV = v1.subtract((RealVector) v2);
         assertArrayEquals("subtract(RealVector) failed", new double[]{9, 18, 27}, subRV.toArray(),
DELTA);

         // subtract(ArrayRealVector)
         ArrayRealVector subArr = v1.subtract(v2);
         assertArrayEquals("subtract(ArrayRealVector) failed", new double[]{9, 18, 27},
subArr.toArray(), DELTA);

         // subtraction with negative values
         ArrayRealVector neg = new ArrayRealVector(new double[]{-1, -2, -3});
         RealVector subNeg = v1.subtract((RealVector) neg);
         assertArrayEquals("subtract negative failed", new double[]{11, 22, 33}, subNeg.toArray(),
DELTA);
     }

     // ----------- OpenMapRealVector addition -----------

     @Test
     public void testOpenMapAddOpenMap() {
         OpenMapRealVector s1 = new OpenMapRealVector(new double[]{1, 0, 3});
         OpenMapRealVector s2 = new OpenMapRealVector(new double[]{4, 5, 6});

         // add(OpenMapRealVector)
         OpenMapRealVector sum = s1.add(s2);
         assertArrayEquals("sparse add sparse failed", new double[]{5, 5, 9}, sum.toArray(), DELTA);

         // sparse with zero entries only
         OpenMapRealVector s3 = new OpenMapRealVector(new double[]{0, 0, 0});
         OpenMapRealVector s4 = new OpenMapRealVector(new double[]{7, 8, 9});
         OpenMapRealVector sum2 = s3.add(s4);
         assertArrayEquals("sparse zero add failed", new double[]{7, 8, 9}, sum2.toArray(), DELTA);
     }

     @Test
     public void testOpenMapAddRealVector() {
         OpenMapRealVector s1 = new OpenMapRealVector(new double[]{1, 0, 3});
         // dense RealVector
         ArrayRealVector d = new ArrayRealVector(new double[]{4, 5, 6});

         RealVector sum = s1.add(d);
         // should include the zero entry from s1 and add 5 to yield 5
         assertArrayEquals("sparse add dense failed", new double[]{5, 5, 9}, sum.toArray(), DELTA);

         // reverse: dense + sparse via ArrayRealVector.add(RealVector)
         RealVector sum2 = d.add(s1);
         assertArrayEquals("dense add sparse failed", new double[]{5, 5, 9}, sum2.toArray(), DELTA);
     }

     // ----------- OpenMapRealVector subtraction -----------

     @Test
     public void testOpenMapSubtractOpenMap() {
         OpenMapRealVector s1 = new OpenMapRealVector(new double[]{10, 0, 20});
         OpenMapRealVector s2 = new OpenMapRealVector(new double[]{1, 2, 3});

         OpenMapRealVector diff = s1.subtract(s2);
         assertArrayEquals("sparse subtract sparse failed", new double[]{9, -2, 17}, diff.toArray(),
DELTA);

         // subtract that involves zero entries on both sides
         OpenMapRealVector s3 = new OpenMapRealVector(new double[]{0, 0, 5});
         OpenMapRealVector s4 = new OpenMapRealVector(new double[]{0, 3, 2});
         OpenMapRealVector diff2 = s3.subtract(s4);
         assertArrayEquals("sparse subtract zero entries failed", new double[]{0, -3, 3},
diff2.toArray(), DELTA);
     }

     @Test
     public void testOpenMapSubtractRealVector() {
         OpenMapRealVector s1 = new OpenMapRealVector(new double[]{10, 0, 20});
         ArrayRealVector d = new ArrayRealVector(new double[]{1, 2, 3});

         RealVector diff = s1.subtract(d);
         assertArrayEquals("sparse subtract dense failed", new double[]{9, -2, 17}, diff.toArray(),
DELTA);

         // reverse
         RealVector diff2 = d.subtract(s1);
         assertArrayEquals("dense subtract sparse failed", new double[]{-9, 2, -17},
diff2.toArray(), DELTA);
     }

     // ----------- dot products -----------

     @Test
     public void testDotProduct() {
         // dense dot dense
         ArrayRealVector d1 = new ArrayRealVector(new double[]{1, 2, 3});
         ArrayRealVector d2 = new ArrayRealVector(new double[]{4, 5, 6});
         double dotDense = d1.dotProduct(d2);
         assertEquals("dense dot product", 1*4 + 2*5 + 3*6, dotDense, DELTA);

         // sparse dot sparse with zero entry
         OpenMapRealVector s1 = new OpenMapRealVector(new double[]{1, 0, 3});
         OpenMapRealVector s2 = new OpenMapRealVector(new double[]{4, 5, 6});
         double dotSparse = s1.dotProduct(s2);
         assertEquals("sparse dot sparse", 1*4 + 0*5 + 3*6, dotSparse, DELTA);

         // sparse dot dense – important because sparse may miss zero entry
         double dotMixed = s1.dotProduct(d1);   // 1*1 + 0*2 + 3*3 = 10
         assertEquals("sparse dot dense", 1*1 + 0*2 + 3*3, dotMixed, DELTA);

         // dense dot sparse
         double dotMixed2 = d1.dotProduct(s1);
         assertEquals("dense dot sparse", 1*1 + 0*2 + 3*3, dotMixed2, DELTA);
     }

     // ----------- vector combining constructors -----------

     @Test
     public void testArrayConstructorCombination() {
         ArrayRealVector v1 = new ArrayRealVector(new double[]{1, 2, 3});
         ArrayRealVector v2 = new ArrayRealVector(new double[]{4, 5});

         // ArrayRealVector(ArrayRealVector, ArrayRealVector)
         ArrayRealVector combined = new ArrayRealVector(v1, v2);
         assertArrayEquals("concat (arr+arr) failed", new double[]{1, 2, 3, 4, 5},
combined.toArray(), DELTA);

         // ArrayRealVector(ArrayRealVector, double[])
         ArrayRealVector c2 = new ArrayRealVector(v1, new double[]{6, 7});
         assertArrayEquals("concat (arr+double[]) failed", new double[]{1, 2, 3, 6, 7},
c2.toArray(), DELTA);

         // ArrayRealVector(double[], ArrayRealVector)
         ArrayRealVector c3 = new ArrayRealVector(new double[]{8, 9}, v1);
         assertArrayEquals("concat (double[]+arr) failed", new double[]{8, 9, 1, 2, 3},
c3.toArray(), DELTA);
     }

     // ----------- error cases -----------

     @Test
     public void testDimensionMismatchException() {
         ArrayRealVector v = new ArrayRealVector(new double[]{1, 2, 3});
         ArrayRealVector wrong = new ArrayRealVector(new double[]{4, 5});

         try {
             v.add((RealVector) wrong);
             fail("Should have thrown IllegalArgumentException for dimension mismatch in
add(RealVector)");
         } catch (IllegalArgumentException expected) { /* expected */ }

         try {
             v.subtract((RealVector) wrong);
             fail("Should have thrown IllegalArgumentException for dimension mismatch in
subtract(RealVector)");
         } catch (IllegalArgumentException expected) { /* expected */ }

         OpenMapRealVector s = new OpenMapRealVector(new double[]{1, 2});
         try {
             s.add(v);
             fail("Should have thrown IllegalArgumentException for sparse add with wrong
dimension");
         } catch (IllegalArgumentException expected) { /* expected */ }

         try {
             s.dotProduct(v);
             fail("Should have thrown IllegalArgumentException for dot product with wrong
dimension");
         } catch (IllegalArgumentException expected) { /* expected */ }
     }

     @Test
     public void testNullInputException() {
         ArrayRealVector v = new ArrayRealVector(new double[]{1, 2, 3});

         try {
             v.add((RealVector) null);
             fail("Should have thrown NullPointerException for add(null)");
         } catch (NullPointerException expected) { /* expected */ }

         try {
             v.subtract((RealVector) null);
             fail("Should have thrown NullPointerException for subtract(null)");
         } catch (NullPointerException expected) { /* expected */ }

         try {
             v.subtract((double[]) null);
             fail("Should have thrown NullPointerException for subtract(null double[])");
         } catch (NullPointerException expected) { /* expected */ }
     }
 }