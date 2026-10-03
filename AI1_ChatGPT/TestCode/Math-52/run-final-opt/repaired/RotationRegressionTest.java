package org.apache.commons.math.geometry.euclidean.threed;

import org.junit.Assert;
import org.junit.Test;

public class RotationRegressionTest {

    @Test
    public void testIssue639LargeVectorsProduceFiniteExpectedQuaternionScalar() {
        final double expectedQ0 = 0.6228370359608201;
        final double dot = 2.0 * expectedQ0 * expectedQ0 - 1.0;
        final double perpendicular = Math.sqrt(1.0 - dot * dot);
        final double scale = 1.0e100;

        Rotation rotation = new Rotation(
                new Vector3D(scale, 0.0, 0.0),
                new Vector3D(scale * dot, scale * perpendicular, 0.0));

        Assert.assertFalse(Double.isNaN(rotation.getQ0()));
        Assert.assertFalse(Double.isInfinite(rotation.getQ0()));
        Assert.assertEquals(expectedQ0, rotation.getQ0(), 1.0e-12);
    }

    @Test
    public void testTwoVectorRotationMapsSourceDirectionToTargetDirection() {
        Rotation rotation = new Rotation(Vector3D.PLUS_I, Vector3D.PLUS_J);

        Vector3D mapped = rotation.applyTo(Vector3D.PLUS_I);
        Assert.assertEquals(0.0, mapped.getX(), 1.0e-15);
        Assert.assertEquals(1.0, mapped.getY(), 1.0e-15);
        Assert.assertEquals(0.0, mapped.getZ(), 1.0e-15);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTwoVectorRotationRejectsZeroDefiningVector() {
        new Rotation(Vector3D.ZERO, Vector3D.PLUS_I);
    }
}
