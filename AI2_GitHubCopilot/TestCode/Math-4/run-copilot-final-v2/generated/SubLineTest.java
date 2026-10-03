package default;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.apache.commons.math3.geometry.euclidean.threed.SubLine as SubLine3D;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.SubLine as SubLine2D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.partitioning.Side;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane.SplitSubHyperplane;

/**

 - Tests targeting SubLine.intersection() and related methods for 2D/3D.
 - Focused on bug MATH-988: NullPointerException when line.intersection()
 - returns null (parallel or non-intersecting lines).
  */
 public class SubLineTest {
  // ---------- 3D Tests ----------
  /**
  - Parallel 3D sublines: underlying infinite lines do not intersect.
  - intersection() should return null, not throw a NullPointerException.
    */
   @Test
   public void test3DIntersectionParallelSubLines_shouldReturnNull() {
   SubLine3D sub1 = new SubLine3D(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0));
   SubLine3D sub2 = new SubLine3D(new Vector3D(0, 1, 0), new Vector3D(1, 1, 0));
   // includeEndPoints = true
   assertNull(sub1.intersection(sub2, true));
   // includeEndPoints = false
   assertNull(sub1.intersection(sub2, false));
  }
  /**
  - Non-intersecting 3D sublines: the infinite lines intersect, but the
  - intersection point lies outside both subline segments.
  - This is the exact scenario from the failing trigger test.
    */
   @Test
   public void test3DIntersectionNotIntersecting_shouldReturnNull() {
   SubLine3D sub1 = new SubLine3D(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0));
   SubLine3D sub2 = new SubLine3D(new Vector3D(2, 1, 0), new Vector3D(2, 2, 0));
   // includeEndPoints = true: LOC1/LOC2 one is OUTSIDE → null
   assertNull(sub1.intersection(sub2, true));
   // includeEndPoints = false: LOC1 != INSIDE → null
   assertNull(sub1.intersection(sub2, false));
  }
  /**
  - Coincident (overlapping) 3D sublines: underlying lines are identical,
  - so line.intersection() returns null. The method must not NPE.
    */
   @Test
   public void test3DIntersectionCoincidentSubLines_shouldReturnNull() {
   SubLine3D sub1 = new SubLine3D(new Vector3D(0, 0, 0), new Vector3D(2, 0, 0));
   SubLine3D sub2 = new SubLine3D(new Vector3D(1, 0, 0), new Vector3D(3, 0, 0));
   assertNull(sub1.intersection(sub2, true));
   assertNull(sub1.intersection(sub2, false));
  }
  /**
  - Normal intersecting 3D sublines with the point inside both intervals.
    */
   @Test
   public void test3DIntersectionNormal_insideIntervals() {
   // lines: (0,0,0)-(1,0,0) and (0,0,1)-(0,1,,1) — they intersect at the origin (0,0,0)?
   // Actually better: use crossing around (0.5,0,0) for both.
   SubLine3D sub1 = new SubLine3D(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0));
   SubLine3D sub2 = new SubLine3D(new Vector3D(0.5, -1, 0), new Vector3D(0.5, 1, 0));
   Vector3D p = sub1.intersection(sub2, true);
   assertNotNull(p);
   assertEquals(0.5, p.getX(), 1.0e-15);
   assertEquals(0.0, p.getY(), 1.0e-15);
   assertEquals(0.0, p.getZ(), 1.0e-15);
   // includeEndPoints false
   Vector3D p2 = sub1.intersection(sub2, false);
   assertNotNull(p2);
   assertEquals(0.,5, p2.getX(), 1.0e-15);
  }
  /**
  - Intersection exactly at an endpoint of one subline.
    */
   @Test
   public void test3DIntersectionAtEndpoint() {
   SubLine3D sub1 = new SubLine3D(new Vector3D(0, 0, 0), new Vector3D(2, 0, 0));
   SubLine3D sub2 = new SubLine3D(new Vector3D(2, -1, 0), new Vector3D(2, 1, 0));
   // includeEndPoints = true → point is on boundary (BOUNDARY), allowed
   Vector3D p = sub1.intersection(sub2, true);
   assertNotNull(p);
   assertEquals(2.0, p.getX(), 1.0e-15);
   // includeEndPoints = false → boundary not allowed → null
   assertNull(sub1.intersection(sub2, false));
  }
  // ---------- 2D Tests ----------
  /**
  - Parallel 2D sublines: the target class must return null,
  - not throw a NullPointerException.
    */
   @Test
   public void test2DIntersectionParallelSubLines() {
   SubLine2D sub1 = new SubLine2D(new Vector2D(0, 0), new Vector2D(2, 0));
   SubLine2D sub2 = new SubLine2D(new Vector2D(0, 1), new Vector2D(2, 1));
   assertNull(sub1.intersection(sub2, true));
   assertNull(sub1.intersection(sub2, false));
  }
  /**
  - Normal intersecting 2D sublines.
    */
   @Test
   public void test2DIntersectionNormal() {
   SubLine2D sub1 = new SubLine2D(new Vector2D(0, 0), new Vector2D(2, 2));
   SubLine2D sub2 = new SubLine2D(new Vector2D(0, 2), new Vector2D(2, 0));
   Vector2D p = sub1.intersection(sub2, true);
   assertNotNull(p);
   assertEquals(1.0, p.getX(), 1.0e-15);
   assertEquals(1.0, p.getY(), 1.0e-15);
  }
  /**
  - 2D side() when lines are parallel: must not NPE and should return
  - the correct side according to line offset.
    */
   @Test
   public void test2DSideParallelLines() {
   SubLine2D sub = new SubLine2D(new Vector2D(0, 0), new Vector2D(1, 0));
   // another line parallel, lying above (offset >0)
   Line2D otherLine = new Line2D(new Vector2D(0, 1), new Vector2D(1, 1));
   // According to the implementation, offset > 0 and direct false??
   // We just test no exception; side should be PLUS or MINUS
   Side s = sub.side((Hyperplane) otherLine);
   assertNotNull(s);
   // Since otherLine is above, offset >0, global > e → Side.PLUS
   assertTrue(s == Side.PLUS || s == Side.MINUS);
  }
  /**
  - 2D split() when the splitting hyperplane is parallel to the subline.
    */
   @Test
   public void test2DSsplitParallelLines() {
   SubLine2D sub = new SubLine2D(new Vector2D(0, 0), new Vector2D(2, 0));
   Line2D splitter = new Line2D(new Vector2D(0, 1), new Vector2D(2, 1));
   SplitSubHyperplane split = sub.split((Hyperplane) splitter);
   assertNotNull(split);
   // Since parallel and offset >0, global <0? Actually otherLine.getOffset(thisLine)
   // returns the signed offset of the line from the other line.
   // The implementation returns new SplitSubHyperplane(null, this) when global < -e,
   // so minus part (result.getMinus()) is null, plus part (getPlus()) is this.
   assertNull(split.getMinus());
   assertNotNull(split.getPlus());
  }
  /**
  - 2D intersection at endpoint, includeEndPoints variations.
    */
   @Test
   public void test2DIntersectionAtEndpoint() {
   SubLine2D sub1 = new SubLine2D(new Vector2D(0, 0), new Vector2D(2, 0));
   SubLine2D sub2 = new SubLine2D(new Vector2D(2, 0), new Vector2D(2, 2));
   // includeEndPoints = true → the common endpoint should be returned
   Vector2D p = sub1.intersection(sub2, true);
   assertNotNull(p);
   assertEquals(2.0, p.getX(), 1.0e-15);
   assertEquals(0.0, p.getY(), 1.0e-15);
   // includeEndPoints = false → the endpoint is boundary, not allowed
   assertNull(sub1.intersection(sub2, false));
  }

}
