package org.apache.commons.math3.geometry.euclidean.threed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.junit.Test;

public class SubLineIntersectionTest {

    @Test
    public void testThreeDimensionalSkewLinesReturnNullWhenEndpointsIncluded() {
        SubLine first = new SubLine(new Vector3D(0, 0, 0), new Vector3D(2, 0, 0));
        SubLine second = new SubLine(new Vector3D(0, 0, 1), new Vector3D(0, 2, 1));

        assertNull(first.intersection(second, true));
    }

    @Test
    public void testThreeDimensionalSkewLinesReturnNullWhenEndpointsExcluded() {
        SubLine first = new SubLine(new Vector3D(0, 0, 0), new Vector3D(2, 0, 0));
        SubLine second = new SubLine(new Vector3D(0, 0, 1), new Vector3D(0, 2, 1));

        assertNull(first.intersection(second, false));
    }

    @Test
    public void testTwoDimensionalParallelLinesReturnNullWhenEndpointsIncluded() {
        org.apache.commons.math3.geometry.euclidean.twod.SubLine first =
                new org.apache.commons.math3.geometry.euclidean.twod.SubLine(
                        new Vector2D(0, 0), new Vector2D(2, 0));
        org.apache.commons.math3.geometry.euclidean.twod.SubLine second =
                new org.apache.commons.math3.geometry.euclidean.twod.SubLine(
                        new Vector2D(0, 1), new Vector2D(2, 1));

        assertNull(first.intersection(second, true));
    }

    @Test
    public void testTwoDimensionalParallelLinesReturnNullWhenEndpointsExcluded() {
        org.apache.commons.math3.geometry.euclidean.twod.SubLine first =
                new org.apache.commons.math3.geometry.euclidean.twod.SubLine(
                        new Vector2D(0, 0), new Vector2D(2, 0));
        org.apache.commons.math3.geometry.euclidean.twod.SubLine second =
                new org.apache.commons.math3.geometry.euclidean.twod.SubLine(
                        new Vector2D(0, 1), new Vector2D(2, 1));

        assertNull(first.intersection(second, false));
    }

    @Test
    public void testThreeDimensionalInteriorIntersectionIsReturned() {
        SubLine first = new SubLine(new Vector3D(-1, 0, 0), new Vector3D(1, 0, 0));
        SubLine second = new SubLine(new Vector3D(0, -1, 0), new Vector3D(0, 1, 0));

        Vector3D intersection = first.intersection(second, false);

        assertNotNull(intersection);
        assertEquals(0.0, intersection.getX(), 1.0e-12);
        assertEquals(0.0, intersection.getY(), 1.0e-12);
        assertEquals(0.0, intersection.getZ(), 1.0e-12);
    }

    @Test
    public void testTwoDimensionalInteriorIntersectionIsReturned() {
        org.apache.commons.math3.geometry.euclidean.twod.SubLine first =
                new org.apache.commons.math3.geometry.euclidean.twod.SubLine(
                        new Vector2D(-1, 0), new Vector2D(1, 0));
        org.apache.commons.math3.geometry.euclidean.twod.SubLine second =
                new org.apache.commons.math3.geometry.euclidean.twod.SubLine(
                        new Vector2D(0, -1), new Vector2D(0, 1));

        Vector2D intersection = first.intersection(second, false);

        assertNotNull(intersection);
        assertEquals(0.0, intersection.getX(), 1.0e-12);
        assertEquals(0.0, intersection.getY(), 1.0e-12);
    }

    @Test
    public void testThreeDimensionalEndpointIntersectionDependsOnEndpointFlag() {
        SubLine first = new SubLine(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0));
        SubLine second = new SubLine(new Vector3D(1, 0, 0), new Vector3D(1, 1, 0));

        Vector3D included = first.intersection(second, true);

        assertNotNull(included);
        assertEquals(1.0, included.getX(), 1.0e-12);
        assertEquals(0.0, included.getY(), 1.0e-12);
        assertEquals(0.0, included.getZ(), 1.0e-12);
        assertNull(first.intersection(second, false));
    }

    @Test
    public void testTwoDimensionalEndpointIntersectionDependsOnEndpointFlag() {
        org.apache.commons.math3.geometry.euclidean.twod.SubLine first =
                new org.apache.commons.math3.geometry.euclidean.twod.SubLine(
                        new Vector2D(0, 0), new Vector2D(1, 0));
        org.apache.commons.math3.geometry.euclidean.twod.SubLine second =
                new org.apache.commons.math3.geometry.euclidean.twod.SubLine(
                        new Vector2D(1, 0), new Vector2D(1, 1));

        Vector2D included = first.intersection(second, true);

        assertNotNull(included);
        assertEquals(1.0, included.getX(), 1.0e-12);
        assertEquals(0.0, included.getY(), 1.0e-12);
        assertNull(first.intersection(second, false));
    }
}
