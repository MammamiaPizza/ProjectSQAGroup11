package org.apache.commons.math3.geometry.euclidean.threed;

 import org.apache.commons.math3.exception.MathIllegalArgumentException;
 import org.apache.commons.math3.util.FastMath;

 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for Line containing regression tests for MATH-938 (revert giving
  * inconsistent origin).
  */
 public class LineTest {

     private static final double EPS = 1e-12;

     // Helper: assert two Vector3D are component-wise equal within delta
     private static void assertVectorEquals(Vector3D expected, Vector3D actual, double delta) {
         assertNotNull(expected);
         assertNotNull(actual);
         assertEquals("X mismatch", expected.getX(), actual.getX(), delta);
         assertEquals("Y mismatch", expected.getY(), actual.getY(), delta);
         assertEquals("Z mismatch", expected.getZ(), actual.getZ(), delta);
     }

     @Test
     public void testRevertOriginSame() {
         // after revert, origin must be the same as original origin
         Line line = new Line(new Vector3D(1, 2, 3), new Vector3D(4, 5, 6));
         Line reverted = line.revert();
         assertEquals("Reverted origin should equal original origin",
                 0.0, line.getOrigin().distance(reverted.getOrigin()), EPS);
     }

     @Test
     public void testRevertDirectionNegated() {
         // direction of reverted line must be the negation of the original direction
         Line line = new Line(new Vector3D(2, 0, 1), new Vector3D(2, 5, 1));
         Line reverted = line.revert();
         Vector3D expected = line.getDirection().negate();
         assertVectorEquals(expected, reverted.getDirection(), EPS);
     }

     @Test
     public void testRevertTwiceYieldsOriginal() {
         // revert(revert(line)) should give back the original line (same points)
         Line line = new Line(new Vector3D(-2, 3, 1), new Vector3D(1, -1, 0));
         Line twiceReverted = line.revert().revert();
         assertTrue("Double reverted line should be similar to original",
                 twiceReverted.isSimilarTo(line));
         // also check that origins match closely
         assertEquals(0.0, line.getOrigin().distance(twiceReverted.getOrigin()), EPS);
     }

     @Test
     public void testAbscissaSignFlip() {
         // abscissa of any point flips sign after revert
         Line line = new Line(new Vector3D(1, 1, 0), new Vector3D(1, 4, 0));
         Line reverted = line.revert();
         Vector3D point = new Vector3D(2, 0, 5);
         double abscissaOrig = line.getAbscissa(point);
         double abscissaRev = reverted.getAbscissa(point);
         assertEquals("Abscissa should flip sign",
                 0.0, abscissaOrig + abscissaRev, EPS);
     }

     @Test
     public void testContainsInvariant() {
         // a point on the line is contained by both original and reverted lines
         Line line = new Line(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0));
         Line reverted = line.revert();
         Vector3D onLine = new Vector3D(0.5, 0, 0);
         assertTrue("Original line must contain point", line.contains(onLine));
         assertTrue("Reverted line must contain the same point", reverted.contains(onLine));
     }

     @Test
     public void testDistanceInvariant() {
         // distance to an arbitrary point is the same before and after revert
         Line line = new Line(new Vector3D(-3, 1, 2), new Vector3D(1, -2, 2));
         Line reverted = line.revert();
         Vector3D p = new Vector3D(7, 8, 9);
         double d1 = line.distance(p);
         double d2 = reverted.distance(p);
         assertEquals("Distance to a point must be invariant under revert",
                 d1, d2, EPS);
     }

     @Test(expected = MathIllegalArgumentException.class)
     public void testConstructorWithIdenticalPointsThrows() {
         // constructing a line from two equal points must throw
         new Line(Vector3D.ZERO, Vector3D.ZERO);
     }

     @Test
     public void testRevertAfterCopyConstructor() {
         // revert of a copied line behaves identically to revert of original
         Line original = new Line(new Vector3D(1,0,0), new Vector3D(1,1,0));
         Line copy = new Line(original);
         Line revOrig = original.revert();
         Line revCopy = copy.revert();
         assertVectorEquals(revOrig.getOrigin(), revCopy.getOrigin(), EPS);
         assertVectorEquals(revOrig.getDirection(), revCopy.getDirection(), EPS);
     }

     @Test
     public void testRevertedDirectionIsUnit() {
         // reverted line must have a unit direction vector
         Line line = new Line(new Vector3D(3, 1, 2), new Vector3D(3, 1, 8));
         Line reverted = line.revert();
         double norm = reverted.getDirection().getNorm();
         assertEquals("Reverted direction must have norm 1", 1.0, norm, EPS);
     }

     @Test
     public void testPointAtAbscissaReverted() {
         // pointAt(abscissa) on reverted line corresponds to pointAt(-abscissa) on original line
         // when the reverted line has the same origin; they lie on the same set of points
         Line line = new Line(new Vector3D(2, 0, 0), new Vector3D(2, 3, 0));
         Line reverted = line.revert();
         double absc = 2.5;
         Vector3D pOriginal = line.pointAt(absc);
         Vector3D pReverted = reverted.pointAt(-absc);
         // both points should be the same point in space (since origin same, direction flipped)
         assertEquals("Points should coincide", 0.0, pOriginal.distance(pReverted), EPS);
     }

     @Test
     public void testRevertWithSmallDirection() {
         // line with a very small direction vector; revert must produce a unit, negated direction
         Vector3D p1 = new Vector3D(1, 1, 0);
         Vector3D p2 = new Vector3D(1 + 1e-8, 1 + 1e-8, 0);
         Line line = new Line(p1, p2);
         Line reverted = line.revert();
         // direction of reverted must be unit
         assertEquals(1.0, reverted.getDirection().getNorm(), 1e-10);
         // the direction must be opposite to original (cos ≈ -1)
         double dot = line.getDirection().dotProduct(reverted.getDirection());
         assertEquals(-1.0, dot, 1e-10);
     }

     @Test
     public void testRevertOriginIsClosestPointToOrigin() {
         // getOrigin() should give a point on the line that is closest to Vector3D.ZERO
         // so distance from origin to the line = distance(Vector3D.ZERO, line.getOrigin())
         Line line = new Line(new Vector3D(1, 2, 3), new Vector3D(4, 5, 6));
         Vector3D origin = Vector3D.ZERO;
         double distanceViaAPI = line.distance(origin);
         double distanceToOriginPoint = line.getOrigin().getNorm(); // distance from (0,0,0) to zero
         // line.distance(origin) computes distance from line to origin, which should equal |zero|
         assertEquals(distanceToOriginPoint, distanceViaAPI, 1e-12);
     }
 }

Wait: I notice there's a mistake in testDistanceInvariant: I wrote line.distance which is a typo,
should be line.distance. Actually the method is distance. I'll correct.

Also need to import org.apache.commons.math3.exception.MathIllegalArgumentException incorrectly
spelled? Actually it's MathIllegalArgumentException. I'll fix import.

Also Vector3D needs to be imported as org.apache.commons.math3.geometry.euclidean.threed.Vector3D.
Already in same package so no import needed if we define the class in the same package. The class is
in package ...threed so same as Line, Vector3D. So no import needed for those classes, except
Exception and Math. I'll include imports for JUnit and FastMath.

I'll also fix assertVectorEquals -> assertArrayEquals not needed.

Check distance method signature: In Line class, public double distance(final Vector3D p).

Also method d.isSimilarTo exists.

getAbscissa is correct.

Now I'll produce the final code without any explanation. Just the class code. Ensure syntax correct.

Potential compilation errors: assertVectorEquals is defined but not used? I used it. Fine.

I'll include a static import for org.junit.Assert.*.

Also note that Vector3D has getX(), getY(), getZ()? Yes, it does.

I'll output now```java
package org.apache.commons.math3.geometry.euclidean.threed;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.util.FastMath;

import org.junit.Test;
import static org.junit.Assert.*;

/**

 - Tests for Line covering the MATH-938 bug where revert() introduces a
 - floating-point drift in the origin of the reversed line.
  */
 public class LineTest {
  private static final double EPS = 1e-12;
  /**
  - Component-wise assertion with tolerance.
    */
   private static void assertVectorEquals(Vector3D expected, Vector3D actual, double delta) {
   assertNotNull(expected);
   assertNotNull(actual);
   assertEquals("X mismatch", expected.getX(), actual.getX(), delta);
   assertEquals("Y mismatch", expected.getY(), actual.getY(), delta);
   assertEquals("Z mismatch", expected.getZ(), actual.getZ(), delta);
   }
  @Test
  public void testRevertOriginSame() {
      // After revert the origin must be identical to the original line's origin.
      Line line = new Line(new Vector3D(1, 2, 3), new Vector3D(4, 5, 6));
      Line reverted = line.revert();
      assertEquals("Reverted origin should equal original origin",
              0.0, line.getOrigin().distance(reverted.getOrigin()), EPS);
  }
  @Test
  public void testRevertDirectionNegated() {
      // Reversed direction must be the negative of the original direction.
      Line line = new Line(new Vector3D(2, 0, 1), new Vector3D(2, 5, 1));
      Line reverted = line.revert();
      Vector3D expected = line.getDirection().negate();
      assertVectorEquals(expected, reverted.getDirection(), EPS);
  }
  @Test
  public void testRevertTwiceYieldsOriginal() {
      // revert(revert(line)) yields a line that contains the same points.
      Line line = new Line(new Vector3D(-2, 3, 1), new Vector3D(1, -1, 0));
      Line twiceReverted = line.revert().revert();
      assertTrue("Double reverted line should be similar to original",
              twiceReverted.isSimilarT(line));
      assertEquals(0.0, line.getOrigin().distance(twiceReverted.getOrigin()), EPS);
  }
  @Test
  public void testAbscissaSignFlip() {
      // Abscissa of any point must change sign after revert.
      Line line = new Line(new Vector3D(1, 1, 0), new Vector3D(1, 4, 0));
      Line reverted = line.revert();
      Vector3D point = new Vector3D(2, 0, 5);
      double aOrig = line.getAbscissa(point);
      double aRev  = reverted.getAbscissa(point);
      assertEquals("Abscissa should flip sign", 0.0, aOrig + aRev, EPS);
  }
  @Test
  public void testContainsInvariant() {
      // A point on the line belongs to both the original and the reverted line.
      Line line = new Line(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0));
      Line reverted = line.revert();
      Vector3D p = new Vector3D(0.5, 0, 0);
      assertTrue("Original line must contain the point", line.contains(p));
      assertTrue("Reverted line must contain the point", reverted.contains(p));
  }
  @Test
  public void testDistanceInvariant() {
      // Distance to any point is unchanged after revert.
      Line line = new Line(new Vector3D(-3, 1, 2), new Vector3D(1, -2, 2));
      Line reverted = line.revert();
      Vector3D p = new Vector3D(7, 8, 9);
      double d1 = line.distance(p);
      double d2 = reverted.distance(p);
      assertEquals("Distance to a point must be invariant under revert", d1, d2, EPS);
  }
  @Test(expected = MathIllegalArgumentException.class)
  public void testConstructorWithIdenticalPointsThrows() {
      // Two equal points must cause an exception.
      new Line(Vector3D.ZERO, Vector3D.ZERO);
  }
  @Test
  public void testRevertAfterCopyConstructor() {
      // revert of a deep-copied line behaves identically to revert of the original.
      Line original = new Line(new Vector3D(1, 0, 0), new Vector3D(1, 1, 0));
      Line copy = new Line(original);
      Line revOrig = original.revert();
      Line revCopy = copy.revert();
      assertVectorEquals(revOrig.getOrigin(), revCopy.getOrigin(), EPS);
      assertVectorEquals(revOrig.getDirection(), revCopy.getDirection(), EPS);
  }
  @Test
  public void testRevertedDirectionIsUnit() {
      // Reverted direction must be a unit vector.
      Line line = new Line(new Vector3D(3, 1, 2), new Vector3D(3, 1, 8));
      Line reverted = line.revert();
      assertEquals("Reverted direction must have norm 1",
              1.0, reverted.getDirection().getNorm(), EPS);
  }
  @Test
  public void testPointAtAbscissaReverted() {
      // pointAt(abscissa) on the reverted line equals pointAt(-abscissa) on the original.
      Line line = new Line(new Vector3D(2, 0, 0), new Vector3D(2, 3, 0));
      Line reverted = line.revert();
      double absc = 2.5;
      Vector3D pOriginal = line.pointAt(absc);
      Vector3D pReverted = reverted.pointAt(-absc);
      assertEquals("Points must coincide",
              0.0, pOriginal.distance(pReverted), EPS);
  }
  @Test
  public void testRevertWithSmallDirection() {
      // A line with a very short direction vector still yields a unit reversed direction.
      Vector3D p1 = new Vector3D(1, 1, 0);
      Vector3D p2 = new Vector3D(1 + 1e-8, 1 + 1e-8, 0);
      Line line = new Line(p1, p2);
      Line reverted = line.revert();
      assertEquals("Direction must be unit", 1.0, reverted.getDirection().getNorm(), 1e-10);
      double dot = line.getDirection().dotProduct(reverted.getDirection());
      assertEquals("Directions must be opposite", -1.0, dot, 1e-10);
  }
  @Test
  public void testOriginIsClosestPointToOrigin() {
      // getOrigin() must return the point on the line closest to Vector3D.ZERO.
      Line line = new Line(new Vector3D(1, 2, 3), new Vector3D(4, 5, 6));
      double distLineToOrigin = line.distance(Vector3D.ZERO);
      double distToZeroPoint   = line.getOrigin().getNorm(); // distance from (0,0,0) to zero
      assertEquals("Distance from origin to line should equal |zero|",
              distToZeroPoint, distLineToOrigin, 1e-12);
  }

}
```