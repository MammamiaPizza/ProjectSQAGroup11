package org.apache.commons.math3.geometry.euclidean.twod;

 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;

 import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
 import org.apache.commons.math3.geometry.euclidean.oned.IntervalsSet;
 import org.apache.commons.math3.geometry.partitioning.BSPTree;
 import org.apache.commons.math3.geometry.partitioning.BoundaryAttribute;
 import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
 import org.junit.Test;
 import static org.junit.Assert.*;

 public class PolygonsSetTest {

  @Test
  public void testSquareArea() {
      PolygonsSet square = new PolygonsSet(-1.0, 1.0, -1.0, 1.0);
      assertEquals(4.0, square.getSize(), 1.0e-10);
  }

  @Test
  public void testSquareBarycenter() {
      PolygonsSet square = new PolygonsSet(-2.0, 2.0, -2.0, 2.0);
      Vector2D barycenter = (Vector2D) square.getBarycenter();
      assertEquals(0.0, barycenter.getX(), 1.0e-10);
      assertEquals(0.0, barycenter.getY(), 1.0e-10);
  }

  @Test
  public void testZeroSizedBoxArea() {
      PolygonsSet degenerate = new PolygonsSet(0.0, 0.0, 0.0, 0.0);
      assertEquals(0.0, degenerate.getSize(), 1.0e-10);
  }

  @Test
  public void testGetVerticesSquare() {
      PolygonsSet square = new PolygonsSet(-1.0, 1.0, -1.0, 1.0);
      Vector2D[][] vertices = square.getVertices();
      assertEquals(1, vertices.length);
      assertEquals(4, vertices[0].length);
      for (Vector2D v : vertices[0]) {
          assertNotNull(v);
      }
  }

  @Test
  public void testGetVerticesDegenerate() {
      PolygonsSet degenerate = new PolygonsSet(0.0, 0.0, 0.0, 0.0);
      Vector2D[][] vertices = degenerate.getVertices();
      assertEquals(0, vertices.length);
  }

  @Test
  public void testInfiniteHalfPlaneArea() {
      Line xAxis = new Line(new Vector2D(0, 0), new Vector2D(1, 0), 1.0e-10);
      IntervalsSet wholeLine = new IntervalsSet(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
 1.0e-10);
      SubLine xAxisSub = new SubLine(xAxis, wholeLine);
      PolygonsSet halfPlane = new PolygonsSet(Collections.singletonList(xAxisSub));
      assertEquals(Double.POSITIVE_INFINITY, halfPlane.getSize(), 0.0);
  }

  @Test
  public void testInfiniteHalfPlaneVertices() {
      Line xAxis = new Line(new Vector2D(0, 0), new Vector2D(1, 0), 1.0e-10);
      IntervalsSet wholeLine = new IntervalsSet(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
 1.0e-10);
      SubLine xAxisSub = new SubLine(xAxis, wholeLine);
      PolygonsSet halfPlane = new PolygonsSet(Collections.singletonList(xAxisSub));
      Vector2D[][] vertices = halfPlane.getVertices();
      assertTrue(vertices.length > 0);
      assertNull(vertices[0][0]);
  }

  @Test(expected = ClassCastException.class)
  public void testBugClassCastException() {
      BSPTree<Euclidean2D> tree = new BSPTree<Euclidean2D>();
      tree.setAttribute(new BoundaryAttribute<Euclidean2D>(null, null));
      PolygonsSet set = new PolygonsSet(tree);
      set.getSize();
  }

  @Test(expected = NullPointerException.class)
  public void testNullAttributeCausesNPE() {
      BSPTree<Euclidean2D> tree = new BSPTree<Euclidean2D>();
      PolygonsSet set = new PolygonsSet(tree);
      set.getSize();
  }

  @Test
  public void testSquareFromBoundaryCollection() {
      Vector2D v0 = new Vector2D(0, 0);
      Vector2D v1 = new Vector2D(1, 0);
      Vector2D v2 = new Vector2D(1, 1);
      Vector2D v3 = new Vector2D(0, 1);
      Line bottom = new Line(v0, v1, 1.0e-10);
      Line right  = new Line(v1, v2, 1.0e-10);
      Line top    = new Line(v2, v3, 1.0e-10);
      Line left   = new Line(v3, v0, 1.0e-10);
      IntervalsSet seg = new IntervalsSet(0.0, 1.0, 1.0e-10);
      List<SubHyperplane<Euclidean2D>> boundary = new ArrayList<SubHyperplane<Euclidean2D>>();
      boundary.add(new SubLine(bottom, seg));
      boundary.add(new SubLine(right,  seg));
      boundary.add(new SubLine(top,    seg));
      boundary.add(new SubLine(left,   seg));
      PolygonsSet square = new PolygonsSet(boundary);
      assertEquals(1.0, square.getSize(), 1.0e-10);
  }

 }
