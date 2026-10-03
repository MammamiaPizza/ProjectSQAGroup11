package org.apache.commons.math.geometry.euclidean.threed;

 import org.junit.Test;
 import static org.junit.Assert.*;

 import org.apache.commons.math.exception.NotARotationMatrixException;

 public class RotationBug52Test {

     @Test(expected = IllegalArgumentException.class)
     public void testZeroNormVectorThrows() {
         // any zero-length defining vector must throw
         new Rotation(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0),
                      new Vector3D(0, 1, 0), new Vector3D(0, 0, 1));
     }

     @Test
     public void testIdentityAngleIsZero() {
         assertEquals(0.0, Rotation.IDENTITY.getAngle(), 1.0e-15);
     }

     @Test
     public void testIdentityApplyToIdentity() {
         Vector3D v = new Vector3D(1, 2, 3);
         Vector3D rv = Rotation.IDENTITY.applyTo(v);
         assertEquals(v.getX(), rv.getX(), 1.0e-15);
         assertEquals(v.getY(), rv.getY(), 1.0e-15);
         assertEquals(v.getZ(), rv.getZ(), 1.0e-15);
     }

     @Test
     public void testGetAngleFiniteStandard() {
         // rotate +I -> +J, +J -> +K  => 120° around (1,1,1)
         Rotation r = new Rotation(new Vector3D(1, 0, 0), new Vector3D(0, 1, 0),
                                   new Vector3D(0, 1, 0), new Vector3D(0, 0, 1));
         double angle = r.getAngle();
         assertFalse("angle must be finite", Double.isNaN(angle));
         assertTrue("angle >= 0", angle >= 0.0);
         assertTrue("angle <= PI", angle <= Math.PI);
         assertEquals(2.0 * Math.PI / 3.0, angle, 1.0e-12);
     }

     @Test
     public void testGetAngleFiniteNearParallelU() {
         Vector3D u1 = new Vector3D(1, 0, 0);
         Vector3D u2 = new Vector3D(1.0 - 1.0e-12, 1.0e-12, 0.0);
         Vector3D v1 = new Vector3D(0, 1, 0);
         Vector3D v2 = new Vector3D(0, 0, 1);
         Rotation r = new Rotation(u1, u2, v1, v2);
         double angle = r.getAngle();
         assertFalse("angle is NaN for near-parallel u-pair", Double.isNaN(angle));
         assertTrue(angle >= 0.0 && angle <= Math.PI);
         Vector3D t = new Vector3D(3, -1, 5);
         double nb = t.getNorm();
         assertEquals(nb, r.applyTo(t).getNorm(), 1.0e-12);
     }

     @Test
     public void testGetAngleFiniteNearParallelV() {
         Vector3D u1 = new Vector3D(1, 0, 0);
         Vector3D u2 = new Vector3D(0, 1, 0);
         Vector3D v1 = new Vector3D(0, 0, 1);
         Vector3D v2 = new Vector3D(0.0, 1.0 - 1.0e-12, 1.0e-12);
         Rotation r = new Rotation(u1, u2, v1, v2);
         double angle = r.getAngle();
         assertFalse("angle is NaN for near-parallel v-pair", Double.isNaN(angle));
         assertTrue(angle >= 0.0 && angle <= Math.PI);
     }

     @Test
     public void testAlignedPairsGivesIdentityAngle() {
         Vector3D u1 = new Vector3D(1, 2, 3);
         Vector3D u2 = new Vector3D(4, 5, 6);
         Rotation r = new Rotation(u1, u2, u1, u2);
         assertEquals(0.0, r.getAngle(), 1.0e-14);
     }

     @Test
     public void testAntiAlignedPairsRotation() {
         Vector3D u1 = new Vector3D(1, 0, 0);
         Vector3D u2 = new Vector3D(0, 1, 0);
         Vector3D v1 = new Vector3D(-1, 0, 0);
         Vector3D v2 = new Vector3D(0, -1, 0);
         Rotation r = new Rotation(u1, u2, v1, v2);
         double angle = r.getAngle();
         assertFalse(Double.isNaN(angle));
         assertTrue(angle >= 0.0 && angle <= Math.PI);
         assertEquals(Math.PI, angle, 1.0e-12);
         // axis must be parallel to +K (or -K)
         double dot = Math.abs(r.getAxis().dotProduct(new Vector3D(0, 0, 1)));
         assertTrue("axis should be parallel to Z", dot > 0.9999999999);
     }

     @Test
     public void testApplyToPreservesNorm() {
         Rotation r = new Rotation(new Vector3D(1, 0, 0), new Vector3D(0, 1, 0),
                                   new Vector3D(0, 1, 0), new Vector3D(0, 0, 1));
         Vector3D v = new Vector3D(7, -3, 2);
         double norm = v.getNorm();
         double normRotated = r.applyTo(v).getNorm();
         assertEquals(norm, normRotated, 1.0e-12);
     }

     @Test
     public void testDistanceFinite() {
         Rotation r1 = new Rotation(new Vector3D(1, 0, 0), new Vector3D(0, 1, 0),
                                    new Vector3D(0, 1, 0), new Vector3D(0, 0, 1));
         Rotation r2 = new Rotation(new Vector3D(1, 0, 0), new Vector3D(0, 0, 1),
                                    new Vector3D(0, 0, 1), new Vector3D(-1, 0, 0));
         double d = Rotation.distance(r1, r2);
         assertFalse("distance should be finite", Double.isNaN(d));
         assertTrue("distance should be non-negative", d >= 0.0);
     }

     @Test
     public void testMatrixConstructorThresholdEdge() {
         double[][] m = {
             {1.0, 0.0, 0.0},
             {0.0, 1.0, 1.0e-15},
             {0.0, -1.0e-15, 1.0}
         };
         Rotation r = new Rotation(m, 1.0e-10);
         double angle = r.getAngle();
         assertFalse("angle NaN from orthonormalized matrix", Double.isNaN(angle));
         assertTrue(angle >= 0.0 && angle <= Math.PI);
         Vector3D v = new Vector3D(1, 2, 3);
         double norm = v.getNorm();
         assertEquals(norm, r.applyTo(v).getNorm(), 1.0e-12);
     }

     @Test(expected = NotARotationMatrixException.class)
     public void testMatrixConstructorNegativeDetThrows() {
         double[][] m = {
             {1, 0, 0},
             {0, 1, 0},
             {0, 0, -1}
         };
         new Rotation(m, 1.0e-10);
     }

     @Test
     public void testNearSingularCrossProduct() {
         // force cross products that are extremely small to challenge numerical stability
         Vector3D u1 = new Vector3D(1.0, 0.0, 0.0);
         Vector3D u2 = new Vector3D(1.0 - 1.0e-15, 1.0e-15, 1.0e-15);
         Vector3D v1 = new Vector3D(0.0, 1.0, 0.0);
         Vector3D v2 = new Vector3D(0.0, 0.0, 1.0);
         Rotation r = new Rotation(u1, u2, v1, v2);
         double angle = r.getAngle();
         assertFalse("angle must be finite even for near-singular cross products",
                     Double.isNaN(angle));
         assertTrue(angle >= 0.0 && angle <= Math.PI);
     }
 }
