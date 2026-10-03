@org.junit.Test
public void testThreeDimensionalSegmentConstructorReconstructsSegment() {
    final org.apache.commons.math3.geometry.euclidean.threed.Vector3D start =
        new org.apache.commons.math3.geometry.euclidean.threed.Vector3D(1.0, 2.0, 3.0);
    final org.apache.commons.math3.geometry.euclidean.threed.Vector3D end =
        new org.apache.commons.math3.geometry.euclidean.threed.Vector3D(4.0, 6.0, 3.0);
    final org.apache.commons.math3.geometry.euclidean.threed.Line line =
        new org.apache.commons.math3.geometry.euclidean.threed.Line(start, end);
    final org.apache.commons.math3.geometry.euclidean.threed.SubLine subLine =
        new org.apache.commons.math3.geometry.euclidean.threed.SubLine(
            new org.apache.commons.math3.geometry.euclidean.threed.Segment(start, end, line));

    final java.util.List<org.apache.commons.math3.geometry.euclidean.threed.Segment> segments =
        subLine.getSegments();

    org.junit.Assert.assertEquals(1, segments.size());
    org.junit.Assert.assertEquals(1.0, segments.get(0).getStart().getX(), 1.0e-12);
    org.junit.Assert.assertEquals(2.0, segments.get(0).getStart().getY(), 1.0e-12);
    org.junit.Assert.assertEquals(3.0, segments.get(0).getStart().getZ(), 1.0e-12);
    org.junit.Assert.assertEquals(4.0, segments.get(0).getEnd().getX(), 1.0e-12);
    org.junit.Assert.assertEquals(6.0, segments.get(0).getEnd().getY(), 1.0e-12);
    org.junit.Assert.assertEquals(3.0, segments.get(0).getEnd().getZ(), 1.0e-12);
}

@org.junit.Test
public void testTwoDimensionalSegmentConstructorReconstructsSegment() {
    final org.apache.commons.math3.geometry.euclidean.twod.Vector2D start =
        new org.apache.commons.math3.geometry.euclidean.twod.Vector2D(-2.0, 5.0);
    final org.apache.commons.math3.geometry.euclidean.twod.Vector2D end =
        new org.apache.commons.math3.geometry.euclidean.twod.Vector2D(3.0, 1.0);
    final org.apache.commons.math3.geometry.euclidean.twod.Line line =
        new org.apache.commons.math3.geometry.euclidean.twod.Line(start, end);
    final org.apache.commons.math3.geometry.euclidean.twod.SubLine subLine =
        new org.apache.commons.math3.geometry.euclidean.twod.SubLine(
            new org.apache.commons.math3.geometry.euclidean.twod.Segment(start, end, line));

    final java.util.List<org.apache.commons.math3.geometry.euclidean.twod.Segment> segments =
        subLine.getSegments();

    org.junit.Assert.assertEquals(1, segments.size());
    org.junit.Assert.assertEquals(-2.0, segments.get(0).getStart().getX(), 1.0e-12);
    org.junit.Assert.assertEquals(5.0, segments.get(0).getStart().getY(), 1.0e-12);
    org.junit.Assert.assertEquals(3.0, segments.get(0).getEnd().getX(), 1.0e-12);
    org.junit.Assert.assertEquals(1.0, segments.get(0).getEnd().getY(), 1.0e-12);
}