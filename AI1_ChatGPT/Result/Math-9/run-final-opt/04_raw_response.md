@org.junit.Test
public void testParallelLinesDistanceClosestPointAndSimilarity() {
    final org.apache.commons.math3.geometry.euclidean.threed.Line line =
        new org.apache.commons.math3.geometry.euclidean.threed.Line(
            new org.apache.commons.math3.geometry.euclidean.threed.Vector3D(0.0, 0.0, 0.0),
            new org.apache.commons.math3.geometry.euclidean.threed.Vector3D(2.0, 0.0, 0.0));
    final org.apache.commons.math3.geometry.euclidean.threed.Line parallel =
        new org.apache.commons.math3.geometry.euclidean.threed.Line(
            new org.apache.commons.math3.geometry.euclidean.threed.Vector3D(5.0, 3.0, 0.0),
            new org.apache.commons.math3.geometry.euclidean.threed.Vector3D(7.0, 3.0, 0.0));
    final org.apache.commons.math3.geometry.euclidean.threed.Line reversedSameLine =
        new org.apache.commons.math3.geometry.euclidean.threed.Line(
            new org.apache.commons.math3.geometry.euclidean.threed.Vector3D(4.0, 0.0, 0.0),
            new org.apache.commons.math3.geometry.euclidean.threed.Vector3D(-1.0, 0.0, 0.0));

    final org.apache.commons.math3.geometry.euclidean.threed.Vector3D closest =
        line.closestPoint(parallel);

    org.junit.Assert.assertEquals(3.0, line.distance(parallel), 0.0);
    org.junit.Assert.assertEquals(0.0, closest.getX(), 0.0);
    org.junit.Assert.assertEquals(0.0, closest.getY(), 0.0);
    org.junit.Assert.assertEquals(0.0, closest.getZ(), 0.0);
    org.junit.Assert.assertTrue(line.isSimilarTo(reversedSameLine));
    org.junit.Assert.assertFalse(line.isSimilarTo(parallel));
}

@org.junit.Test
public void testSkewLinesDistanceAndClosestPoint() {
    final org.apache.commons.math3.geometry.euclidean.threed.Line line =
        new org.apache.commons.math3.geometry.euclidean.threed.Line(
            new org.apache.commons.math3.geometry.euclidean.threed.Vector3D(0.0, 0.0, 0.0),
            new org.apache.commons.math3.geometry.euclidean.threed.Vector3D(1.0, 0.0, 0.0));
    final org.apache.commons.math3.geometry.euclidean.threed.Line skew =
        new org.apache.commons.math3.geometry.euclidean.threed.Line(
            new org.apache.commons.math3.geometry.euclidean.threed.Vector3D(0.0, 1.0, 4.0),
            new org.apache.commons.math3.geometry.euclidean.threed.Vector3D(0.0, 2.0, 4.0));

    final org.apache.commons.math3.geometry.euclidean.threed.Vector3D closest =
        line.closestPoint(skew);

    org.junit.Assert.assertEquals(4.0, line.distance(skew), 1.0e-15);
    org.junit.Assert.assertEquals(0.0, closest.getX(), 1.0e-15);
    org.junit.Assert.assertEquals(0.0, closest.getY(), 1.0e-15);
    org.junit.Assert.assertEquals(0.0, closest.getZ(), 1.0e-15);
}

@org.junit.Test
public void testIntersectionReturnsPointOnlyForMeetingLines() {
    final org.apache.commons.math3.geometry.euclidean.threed.Line line =
        new org.apache.commons.math3.geometry.euclidean.threed.Line(
            new org.apache.commons.math3.geometry.euclidean.threed.Vector3D(0.0, 0.0, 0.0),
            new org.apache.commons.math3.geometry.euclidean.threed.Vector3D(1.0, 0.0, 0.0));
    final org.apache.commons.math3.geometry.euclidean.threed.Line crossing =
        new org.apache.commons.math3.geometry.euclidean.threed.Line(
            new org.apache.commons.math3.geometry.euclidean.threed.Vector3D(0.0, -1.0, 0.0),
            new org.apache.commons.math3.geometry.euclidean.threed.Vector3D(0.0, 1.0, 0.0));
    final org.apache.commons.math3.geometry.euclidean.threed.Line skew =
        new org.apache.commons.math3.geometry.euclidean.threed.Line(
            new org.apache.commons.math3.geometry.euclidean.threed.Vector3D(0.0, -1.0, 1.0),
            new org.apache.commons.math3.geometry.euclidean.threed.Vector3D(0.0, 1.0, 1.0));

    final org.apache.commons.math3.geometry.euclidean.threed.Vector3D intersection =
        line.intersection(crossing);

    org.junit.Assert.assertEquals(0.0, intersection.getX(), 0.0);
    org.junit.Assert.assertEquals(0.0, intersection.getY(), 0.0);
    org.junit.Assert.assertEquals(0.0, intersection.getZ(), 0.0);
    org.junit.Assert.assertNull(line.intersection(skew));
}

@org.junit.Test
public void testContainsUsesDistanceTolerance() {
    final org.apache.commons.math3.geometry.euclidean.threed.Line line =
        new org.apache.commons.math3.geometry.euclidean.threed.Line(
            new org.apache.commons.math3.geometry.euclidean.threed.Vector3D(0.0, 0.0, 0.0),
            new org.apache.commons.math3.geometry.euclidean.threed.Vector3D(1.0, 0.0, 0.0));

    org.junit.Assert.assertTrue(line.contains(
        new org.apache.commons.math3.geometry.euclidean.threed.Vector3D(2.0, 5.0e-11, 0.0)));
    org.junit.Assert.assertFalse(line.contains(
        new org.apache.commons.math3.geometry.euclidean.threed.Vector3D(2.0, 2.0e-10, 0.0)));
}