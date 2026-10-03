package org.apache.commons.math3.geometry.euclidean.twod;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.junit.Test;

public class PolygonsSetMATH780Test {

    @Test
    public void testWholeSpaceTreeWithCutHasNoVerticesAndInfiniteSize() {
        final Line line = new Line(new Vector2D(0, 0), new Vector2D(1, 0));
        final BSPTree<Euclidean2D> tree =
                new BSPTree<Euclidean2D>(line.wholeHyperplane(),
                                         new BSPTree<Euclidean2D>(Boolean.TRUE),
                                         new BSPTree<Euclidean2D>(Boolean.TRUE),
                                         null);

        final PolygonsSet set = new PolygonsSet(tree);

        assertEquals(0, set.getVertices().length);
        assertTrue(Double.isInfinite(set.getSize()));
    }

    @Test
    public void testEmptySpaceTreeWithCutHasNoVerticesAndZeroSize() {
        final Line line = new Line(new Vector2D(0, 0), new Vector2D(1, 0));
        final BSPTree<Euclidean2D> tree =
                new BSPTree<Euclidean2D>(line.wholeHyperplane(),
                                         new BSPTree<Euclidean2D>(Boolean.FALSE),
                                         new BSPTree<Euclidean2D>(Boolean.FALSE),
                                         null);

        final PolygonsSet set = new PolygonsSet(tree);

        assertEquals(0, set.getVertices().length);
        assertEquals(0.0, set.getSize(), 0.0);
    }

    @Test
    public void testFiniteRectangleBuildsClosedVertexLoopAndArea() {
        final PolygonsSet set = new PolygonsSet(-1, 2, -2, 2);

        final Vector2D[][] vertices = set.getVertices();

        assertEquals(1, vertices.length);
        assertEquals(4, vertices[0].length);
        assertTrue(contains(vertices[0], new Vector2D(-1, -2)));
        assertTrue(contains(vertices[0], new Vector2D(-1, 2)));
        assertTrue(contains(vertices[0], new Vector2D(2, -2)));
        assertTrue(contains(vertices[0], new Vector2D(2, 2)));
        assertEquals(12.0, set.getSize(), 1.0e-12);
    }

    @Test
    public void testHalfPlaneProducesOpenBoundaryLoopAndInfiniteSize() {
        final Line line = new Line(new Vector2D(0, 0), new Vector2D(1, 0));
        final BSPTree<Euclidean2D> tree =
                new BSPTree<Euclidean2D>(line.wholeHyperplane(),
                                         new BSPTree<Euclidean2D>(Boolean.TRUE),
                                         new BSPTree<Euclidean2D>(Boolean.FALSE),
                                         null);

        final PolygonsSet set = new PolygonsSet(tree);
        final Vector2D[][] vertices = set.getVertices();

        assertEquals(1, vertices.length);
        assertEquals(3, vertices[0].length);
        assertNull(vertices[0][0]);
        assertNull(vertices[0][1]);
        assertNull(vertices[0][2]);
        assertTrue(Double.isInfinite(set.getSize()));
    }

    private boolean contains(final Vector2D[] points, final Vector2D expected) {
        for (final Vector2D point : points) {
            if (point != null && point.distance(expected) < 1.0e-12) {
                return true;
            }
        }
        return false;
    }
}
