@Test
    public void test3DConstructWithSegmentAndGetSegments() {
        org.apache.commons.math3.geometry.euclidean.threed.Vector3D start = new
org.apache.commons.math3.geometry.euclidean.threed.Vector3D(0, 0, 0);
        org.apache.commons.math3.geometry.euclidean.threed.Vector3D end = new
org.apache.commons.math3.geometry.euclidean.threed.Vector3D(1, 0, 0);
        org.apache.commons.math3.geometry.euclidean.threed.Line line = new
org.apache.commons.math3.geometry.euclidean.threed.Line(start, end);
        org.apache.commons.math3.geometry.euclidean.threed.Segment seg = new
org.apache.commons.math3.geometry.euclidean.threed.Segment(start, end, line);
        SubLine sub = new SubLine(seg);
        java.util.List<org.apache.commons.math3.geometry.euclidean.threed.Segment> segList =
sub.getSegments();
        assertNotNull(segList);
        assertEquals(1, segList.size());
        org.apache.commons.math3.geometry.euclidean.threed.Segment segOut = segList.get(0);
        assertEquals(0.0, segOut.getStart().getX(), 1e-15);
        assertEquals(0.0, segOut.getStart().getY(), 1e-15);
        assertEquals(0.0, segOut.getStart().getZ(), 1e-15);
        assertEquals(1.0, segOut.getEnd().getX(), 1e-15);
    }

 @Test
 public void test3DIntersectionLinesIntersectButSubLinesDoNot() {
     org.apache.commons.math3.geometry.euclidean.threed.Vector3D p1 = new
org.apache.commons.math3.geometry.euclidean.threed.Vector3D(1, 0, 0);
     org.apache.commons.math3.geometry.euclidean.threed.Vector3D p2 = new
org.apache.commons.math3.geometry.euclidean.threed.Vector3D(2, 0, 0);
     SubLine sub1 = new SubLine(p1, p2);
     org.apache.commons.math3.geometry.euclidean.threed.Vector3D p3 = new
org.apache.commons.math3.geometry.euclidean.threed.Vector3D(0, 1, 0);
     org.apache.commons.math3.geometry.euclidean.threed.Vector3D p4 = new
org.apache.commons.math3.geometry.euclidean.threed.Vector3D(0, 2, 0);
     SubLine sub2 = new SubLine(p3, p4);
     assertNull(sub1.intersection(sub2, false));
     assertNull(sub1.intersection(sub2, true));
 }

 @Test
 public void test2DConstructWithSegmentAndGetSegments() {
     org.apache.commons.math3.geometry.euclidean.twod.Vector2D start = new
org.apache.commons.math3.geometry.euclidean.twod.Vector2D(0, 0);
     org.apache.commons.math3.geometry.euclidean.twod.Vector2D end = new
org.apache.commons.math3.geometry.euclidean.twod.Vector2D(1, 0);
     org.apache.commons.math3.geometry.euclidean.twod.Line line = new
org.apache.commons.math3.geometry.euclidean.twod.Line(start, end);
     org.apache.commons.math3.geometry.euclidean.twod.Segment seg = new
org.apache.commons.math3.geometry.euclidean.twod.Segment(start, end, line);
     org.apache.commons.math3.geometry.euclidean.twod.SubLine sub = new
org.apache.commons.math3.geometry.euclidean.twod.SubLine(seg);
     java.util.List<org.apache.commons.math3.geometry.euclidean.twod.Segment> segList =
sub.getSegments();
     assertNotNull(segList);
     assertEquals(1, segList.size());
     org.apache.commons.math3.geometry.euclidean.twod.Segment segOut = segList.get(0);
     assertEquals(0.0, segOut.getStart().getX(), 1e-15);
     assertEquals(0.0, segOut.getStart().getY(), 1e-15);
     assertEquals(1.0, segOut.getEnd().getX(), 1e-15);
     assertEquals(0.0, segOut.getEnd().getY(), 1e-15);
 }

 @Test
 public void test2DSplitIntersectionNonParallel() {
     org.apache.commons.math3.geometry.euclidean.twod.Vector2D a = new
org.apache.commons.math3.geometry.euclidean.twod.Vector2D(0, 0);
     org.apache.commons.math3.geometry.euclidean.twod.Vector2D b = new
org.apache.commons.math3.geometry.euclidean.twod.Vector2D(2, 0);
     org.apache.commons.math3.geometry.euclidean.twod.SubLine sub1 = new
org.apache.commons.math3.geometry.euclidean.twod.SubLine(a, b);
     org.apache.commons.math3.geometry.euclidean.twod.Vector2D c = new
org.apache.commons.math3.geometry.euclidean.twod.Vector2D(1, -1);
     org.apache.commons.math3.geometry.euclidean.twod.Vector2D d = new
org.apache.commons.math3.geometry.euclidean.twod.Vector2D(1, 1);
     org.apache.commons.math3.geometry.euclidean.twod.SubLine sub2 = new
org.apache.commons.math3.geometry.euclidean.twod.SubLine(c, d);
     org.apache.commons.math3.geometry.partitioning.SubHyperplane.SplitSubHyperplane<org.apache.comm
ons.math3.geometry.euclidean.twod.Euclidean2D> split = sub1.split(sub2);
     assertNotNull(split.getPlus());
     assertNotNull(split.getMinus());
 }