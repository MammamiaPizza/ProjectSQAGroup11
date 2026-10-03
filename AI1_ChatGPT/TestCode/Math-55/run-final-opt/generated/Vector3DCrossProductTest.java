package org.apache.commons.math.geometry;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Vector3DCrossProductTest {

    @Test
    public void testCrossProductPreservesUnitResultAfterLargeProductCancellation() {
        final double n = 1 << 27;

        Vector3D first = new Vector3D(0.0, n, n - 1.0);
        Vector3D second = new Vector3D(0.0, n + 1.0, n);

        Vector3D cross = Vector3D.crossProduct(first, second);

        assertEquals(1.0, cross.getX(), 0.0);
        assertEquals(0.0, cross.getY(), 0.0);
        assertEquals(0.0, cross.getZ(), 0.0);
    }

    @Test
    public void testCrossProductUsesRightHandedBasisOrientation() {
        Vector3D ij = Vector3D.crossProduct(Vector3D.PLUS_I, Vector3D.PLUS_J);
        Vector3D jk = Vector3D.crossProduct(Vector3D.PLUS_J, Vector3D.PLUS_K);
        Vector3D ki = Vector3D.crossProduct(Vector3D.PLUS_K, Vector3D.PLUS_I);
        Vector3D ji = Vector3D.crossProduct(Vector3D.PLUS_J, Vector3D.PLUS_I);

        assertEquals(0.0, ij.getX(), 0.0);
        assertEquals(0.0, ij.getY(), 0.0);
        assertEquals(1.0, ij.getZ(), 0.0);

        assertEquals(1.0, jk.getX(), 0.0);
        assertEquals(0.0, jk.getY(), 0.0);
        assertEquals(0.0, jk.getZ(), 0.0);

        assertEquals(0.0, ki.getX(), 0.0);
        assertEquals(1.0, ki.getY(), 0.0);
        assertEquals(0.0, ki.getZ(), 0.0);

        assertEquals(0.0, ji.getX(), 0.0);
        assertEquals(0.0, ji.getY(), 0.0);
        assertEquals(-1.0, ji.getZ(), 0.0);
    }

    @Test
    public void testCrossProductOfParallelVectorsIsZero() {
        Vector3D first = new Vector3D(2.0, -3.0, 5.0);
        Vector3D second = new Vector3D(-8.0, 12.0, -20.0);

        Vector3D cross = Vector3D.crossProduct(first, second);

        assertEquals(0.0, cross.getX(), 0.0);
        assertEquals(0.0, cross.getY(), 0.0);
        assertEquals(0.0, cross.getZ(), 0.0);
    }
}
