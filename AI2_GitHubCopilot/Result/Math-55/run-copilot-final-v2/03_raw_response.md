package org.apache.commons.math.geometry;

 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for {@link Vector3D#crossProduct(Vector3D, Vector3D)} focusing on
  * MATH-554: catastrophic cancellation for near-parallel vectors.
  */
 public class Vector3DTest {

     /** Tolerance for floating-point comparisons. */
     private static final double EPS = 1e-14;

     // -----------------------------------------------------------------------
     // Test: catastrophic cancellation for near-parallel vectors (MATH-554)
     // -----------------------------------------------------------------------

     /**
      * Near-parallel unit-magnitude vectors separated by a tiny angle. The naive
      * cross-product formula suffers from cancellation; the buggy version returns
      * a zero (or near-zero) vector. The expected cross-product magnitude is
      * approximately sin(angle).
      */
     @Test
     public void testCrossProductCancellation() {
         // Two unit vectors separated by ~1e-8 radians (not exactly parallel).
         // sin(1e-8) ≈ 1e-8, so the cross-product norm should be ≈ 1e-8, not 0.
         double eps = 1e-8;
         Vector3D v1 = new Vector3D(1.0, 0.0, 0.0);
         Vector3D v2 = new Vector3D(Math.cos(eps), Math.sin(eps), 0.0);

         Vector3D cross = Vector3D.crossProduct(v1, v2);

         // The magnitude should be approximately sin(eps) ≈ eps.
         double expectedMag = Math.abs(Math.sin(eps));
         double actualMag = cross.getNorm();
         assertTrue("Cross product of near-parallel unit vectors collapsed to zero; "
             + "expected ~" + expectedMag + " but was " + actualMag,
             actualMag > expectedMag * 0.5);
     }

     // -----------------------------------------------------------------------
     // Test: orthogonality
     // -----------------------------------------------------------------------

     @Test
     public void testCrossProductOrthogonal() {
         Vector3D v1 = new Vector3D(2.0, -1.0, 4.0);
         Vector3D v2 = new Vector3D(-3.0, 5.0, 1.0);

         Vector3D cross = Vector3D.crossProduct(v1, v2);

         // cross must be orthogonal to both v1 and v2.
         double dotWithV1 = Vector3D.dotProduct(cross, v1);
         double dotWithV2 = Vector3D.dotProduct(cross, v2);
         assertEquals("cross(v1,v2) not orthogonal to v1", 0.0, dotWithV1, EPS);
         assertEquals("cross(v1,v2) not orthogonal to v2", 0.0, dotWithV2, EPS);
     }

     // -----------------------------------------------------------------------
     // Test: anti-commutativity
     // -----------------------------------------------------------------------

     @Test
     public void testCrossProductAntiCommutative() {
         Vector3D v1 = new Vector3D(1.0, 2.0, -1.0);
         Vector3D v2 = new Vector3D(3.0, -4.0, 2.0);

         Vector3D cross12 = Vector3D.crossProduct(v1, v2);
         Vector3D cross21 = Vector3D.crossProduct(v2, v1);

         // cross(v1,v2) = -cross(v2,v1)
         assertEquals("cross(v1,v2) != -cross(v2,v1) x-component",
             cross12.getX(), -cross21.getX(), EPS);
         assertEquals("cross(v1,v2) != -cross(v2,v1) y-component",
             cross12.getY(), -cross21.getY(), EPS);
         assertEquals("cross(v1,v2) != -cross(v2,v1) z-component",
             cross12.getZ(), -cross21.getZ(), EPS);
     }

     // -----------------------------------------------------------------------
     // Test: cross product of a vector with itself is ZERO
     // -----------------------------------------------------------------------

     @Test
     public void testCrossProductSelf() {
         Vector3D v = new Vector3D(7.0, -3.0, 2.0);
         Vector3D cross = Vector3D.crossProduct(v, v);
         assertEquals(Vector3D.ZERO, cross);
     }

     // -----------------------------------------------------------------------
     // Test: cross product of parallel vectors is ZERO
     // -----------------------------------------------------------------------

     @Test
     public void testCrossProductParallel() {
         Vector3D v1 = new Vector3D(2.0, -6.0, 4.0);
         Vector3D v2 = new Vector3D(-3.0, 9.0, -6.0); // v2 = -1.5 * v1

         Vector3D cross = Vector3D.crossProduct(v1, v2);

         // Parallel vectors → zero cross product.
         assertEquals("Parallel vectors should give ZERO cross product",
             Vector3D.ZERO, cross);
     }

     // -----------------------------------------------------------------------
     // Test: cross product with ZERO vector
     // -----------------------------------------------------------------------

     @Test
     public void testCrossProductWithZero() {
         Vector3D v = new Vector3D(3.0, -2.0, 5.0);

         Vector3D cross1 = Vector3D.crossProduct(v, Vector3D.ZERO);
         Vector3D cross2 = Vector3D.crossProduct(Vector3D.ZERO, v);

         assertEquals(Vector3D.ZERO, cross1);
         assertEquals(Vector3D.ZERO, cross2);
     }

     // -----------------------------------------------------------------------
     // Test: cross product with orthogonal unit basis vectors
     // -----------------------------------------------------------------------

     @Test
     public void testCrossProductBasisVectors() {
         // PLUS_I × PLUS_J = PLUS_K
         assertEquals(Vector3D.PLUS_K, Vector3D.crossProduct(Vector3D.PLUS_I, Vector3D.PLUS_J));
         // PLUS_J × PLUS_K = PLUS_I
         assertEquals(Vector3D.PLUS_I, Vector3D.crossProduct(Vector3D.PLUS_J, Vector3D.PLUS_K));
         // PLUS_K × PLUS_I = PLUS_J
         assertEquals(Vector3D.PLUS_J, Vector3D.crossProduct(Vector3D.PLUS_K, Vector3D.PLUS_I));
     }

     // -----------------------------------------------------------------------
     // Test: cross-product magnitude equals |v1||v2|·sin(angle)
     // -----------------------------------------------------------------------

     @Test
     public void testCrossProductMagnitude() {
         Vector3D v1 = new Vector3D(3.0, 0.0, 0.0);
         Vector3D v2 = new Vector3D(0.0, 4.0, 0.0);

         Vector3D cross = Vector3D.crossProduct(v1, v2);

         double expectedMag = v1.getNorm() * v2.getNorm(); // sin(90°)=1
         double actualMag = cross.getNorm();
         assertEquals("Cross product magnitude for orthogonal vectors",
             expectedMag, actualMag, EPS);
     }

     // -----------------------------------------------------------------------
     // Test: cross product with NaN
     // -----------------------------------------------------------------------

     @Test
     public void testCrossProductNaN() {
         Vector3D v = new Vector3D(1.0, 2.0, 3.0);

         Vector3D cross1 = Vector3D.crossProduct(v, Vector3D.NaN);
         Vector3D cross2 = Vector3D.crossProduct(Vector3D.NaN, v);
         Vector3D cross3 = Vector3D.crossProduct(Vector3D.NaN, Vector3D.NaN);

         assertTrue("cross with NaN should contain NaN", cross1.isNaN());
         assertTrue("cross with NaN should contain NaN", cross2.isNaN());
         assertTrue("cross with NaN should contain NaN", cross3.isNaN());
     }

     // -----------------------------------------------------------------------
     // Test: cross product of vectors with large components where product
     //       differences nearly cancel (related to MATH-554)
     // -----------------------------------------------------------------------

     @Test
     public void testCrossProductLargeComponents() {
         // Vectors with large x,y components that are nearly equal.
         // The z-component of the cross product depends on x1*y2 - y1*x2,
         // which can suffer from catastrophic cancellation.
         double big = 1e15;
         double small = 5e-16;
         // With exact arithmetic: (big+small)*big - big*big = small*big = 0.5
         // Floating-point may lose the difference entirely.
         Vector3D v1 = new Vector3D(big + small, big, 0.0);
         Vector3D v2 = new Vector3D(big, big, 0.0);

         Vector3D cross = Vector3D.crossProduct(v1, v2);

         // The z-component should be approximately small*big = 0.5 (non-zero).
         double crossZ = cross.getZ();
         // Even with the bug, the result should be distinguishable from exactly zero
         // for a reasonable implementation.  We check that the cross product is not
         // identically the zero vector.
         assertFalse("Cross product of distinct nearly-parallel vectors should not be ZERO",
             Vector3D.ZERO.equals(cross));
         // Additionally, the cross product should be orthogonal to both vectors.
         double dot1 = Vector3D.dotProduct(cross, v1);
         double dot2 = Vector3D.dotProduct(cross, v2);
         assertEquals("Cross product not orthogonal to v1", 0.0, dot1, 1e-9);
         assertEquals("Cross product not orthogonal to v2", 0.0, dot2, 1e-9);
     }

     // -----------------------------------------------------------------------
     // Test: cross product of near-parallel vectors — direction preserved
     // (even if magnitude suffers, the sign of non-zero components is meaningful)
     // -----------------------------------------------------------------------

     @Test
     public void testCrossProductNearParallelDirection() {
         // Nearly-parallel vectors in the XY-plane with a tiny z-component.
         double tiny = 1e-8;
         Vector3D v1 = new Vector3D(2.0, 3.0, tiny);
         Vector3D v2 = new Vector3D(2.0, 3.0, 0.0);

         Vector3D cross = Vector3D.crossProduct(v1, v2);

         // cross should be along the direction perpendicular to the near-plane.
         // sign of z-component should be positive (right-hand rule from v2 to v1).
         double dotWithV1 = Vector3D.dotProduct(cross, v1);
         double dotWithV2 = Vector3D.dotProduct(cross, v2);
         assertTrue("Cross product should be orthogonal to v1", Math.abs(dotWithV1) < EPS);
         assertTrue("Cross product should be orthogonal to v2", Math.abs(dotWithV2) < EPS);

         // The result should not be exactly zero.
         assertFalse("Cross product of distinct nearly-parallel vectors must not be ZERO",
             Vector3D.ZERO.equals(cross));
     }
 }