package org.apache.commons.math3.geometry.euclidean.threed;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.geometry.euclidean.oned.Vector1D;
import org.junit.Assert;
import org.junit.Test;

public class LineRevertPrecisionTest {

    @Test
    public void testRevertPreservesExactOriginForLargeNonAxisLine() {
        Line line = new Line(new Vector3D(1000000.0, 1000000.0, 1000000.0),
                             new Vector3D(1000001.0, 1000002.0, 1000003.0));

        Vector3D origin = line.getOrigin();
        Line reverted = line.revert();

        Assert.assertEquals(origin.getX(), reverted.getOrigin().getX(), 0.0);
        Assert.assertEquals(origin.getY(), reverted.getOrigin().getY(), 0.0);
        Assert.assertEquals(origin.getZ(), reverted.getOrigin().getZ(), 0.0);
        Assert.assertEquals(-line.getDirection().getX(), reverted.getDirection().getX(), 0.0);
        Assert.assertEquals(-line.getDirection().getY(), reverted.getDirection().getY(), 0.0);
        Assert.assertEquals(-line.getDirection().getZ(), reverted.getDirection().getZ(), 0.0);
    }

    @Test
    public void testRevertNegatesAbscissasAndKeepsPointsOnSameLine() {
        Line line = new Line(new Vector3D(1.0, 2.0, 3.0),
                             new Vector3D(4.0, -2.0, 5.0));
        Line reverted = line.revert();

        double[] abscissas = { -7.5, -1.0, 0.0, 2.75, 9.0 };
        for (double abscissa : abscissas) {
            Vector3D point = line.pointAt(abscissa);

            Assert.assertTrue(reverted.contains(point));
            Assert.assertEquals(-abscissa, reverted.getAbscissa(point), 1.0e-12);
            Assert.assertEquals(0.0, line.distance(point), 1.0e-12);
            Assert.assertEquals(0.0, reverted.distance(point), 1.0e-12);
        }
    }

    @Test
    public void testRevertToSubSpaceAndToSpaceUseReversedCoordinateSystem() {
        Line line = new Line(new Vector3D(-2.0, 3.0, 1.0),
                             new Vector3D(5.0, -1.0, 4.0));
        Line reverted = line.revert();

        Vector3D point = line.pointAt(4.25);
        Vector1D originalCoordinate = line.toSubSpace(point);
        Vector1D reversedCoordinate = reverted.toSubSpace(point);

        Assert.assertEquals(4.25, originalCoordinate.getX(), 1.0e-12);
        Assert.assertEquals(-4.25, reversedCoordinate.getX(), 1.0e-12);

        Vector3D reconstructed = reverted.toSpace(reversedCoordinate);
        Assert.assertEquals(point.getX(), reconstructed.getX(), 1.0e-12);
        Assert.assertEquals(point.getY(), reconstructed.getY(), 1.0e-12);
        Assert.assertEquals(point.getZ(), reconstructed.getZ(), 1.0e-12);
    }

    @Test
    public void testConstructionWithIdenticalPointsThrowsException() {
        Vector3D point = new Vector3D(1.0, -2.0, 3.0);

        try {
            new Line(point, point);
            Assert.fail("A line cannot be constructed from two identical points");
        } catch (MathIllegalArgumentException expected) {
            Assert.assertNotNull(expected);
        }
    }

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
}
