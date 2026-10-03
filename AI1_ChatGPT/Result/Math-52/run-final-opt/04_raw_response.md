@Test
public void testAxisAngleRotationMapsBasisVector() {
    org.apache.commons.math.geometry.euclidean.threed.Rotation rotation =
        new org.apache.commons.math.geometry.euclidean.threed.Rotation(
            new org.apache.commons.math.geometry.euclidean.threed.Vector3D(0.0, 0.0, 2.0),
            0.5 * Math.PI);

    org.apache.commons.math.geometry.euclidean.threed.Vector3D mapped =
        rotation.applyTo(new org.apache.commons.math.geometry.euclidean.threed.Vector3D(1.0, 0.0, 0.0));

    org.junit.Assert.assertEquals(0.0, mapped.getX(), 1.0e-15);
    org.junit.Assert.assertEquals(1.0, mapped.getY(), 1.0e-15);
    org.junit.Assert.assertEquals(0.0, mapped.getZ(), 1.0e-15);
}

@Test(expected = ArithmeticException.class)
public void testAxisAngleRotationRejectsZeroAxis() {
    new org.apache.commons.math.geometry.euclidean.threed.Rotation(
        new org.apache.commons.math.geometry.euclidean.threed.Vector3D(0.0, 0.0, 0.0),
        0.5);
}

@Test
public void testRotationOrderConstructorMatchesDocumentedComposition() {
    double alpha1 = 0.3;
    double alpha2 = -0.4;
    double alpha3 = 0.2;

    org.apache.commons.math.geometry.euclidean.threed.Rotation fromOrder =
        new org.apache.commons.math.geometry.euclidean.threed.Rotation(
            org.apache.commons.math.geometry.euclidean.threed.RotationOrder.XYZ,
            alpha1, alpha2, alpha3);

    org.apache.commons.math.geometry.euclidean.threed.Rotation r1 =
        new org.apache.commons.math.geometry.euclidean.threed.Rotation(
            new org.apache.commons.math.geometry.euclidean.threed.Vector3D(1.0, 0.0, 0.0),
            alpha1);
    org.apache.commons.math.geometry.euclidean.threed.Rotation r2 =
        new org.apache.commons.math.geometry.euclidean.threed.Rotation(
            new org.apache.commons.math.geometry.euclidean.threed.Vector3D(0.0, 1.0, 0.0),
            alpha2);
    org.apache.commons.math.geometry.euclidean.threed.Rotation r3 =
        new org.apache.commons.math.geometry.euclidean.threed.Rotation(
            new org.apache.commons.math.geometry.euclidean.threed.Vector3D(0.0, 0.0, 1.0),
            alpha3);

    org.apache.commons.math.geometry.euclidean.threed.Rotation expected =
        r1.applyTo(r2.applyTo(r3));

    org.junit.Assert.assertEquals(0.0,
        org.apache.commons.math.geometry.euclidean.threed.Rotation.distance(expected, fromOrder),
        1.0e-15);
}

@Test
public void testMatrixConstructorCreatesExpectedQuarterTurn() {
    org.apache.commons.math.geometry.euclidean.threed.Rotation rotation =
        new org.apache.commons.math.geometry.euclidean.threed.Rotation(
            new double[][] {
                { 0.0, -1.0, 0.0 },
                { 1.0,  0.0, 0.0 },
                { 0.0,  0.0, 1.0 }
            },
            1.0e-12);

    org.apache.commons.math.geometry.euclidean.threed.Vector3D mapped =
        rotation.applyTo(new org.apache.commons.math.geometry.euclidean.threed.Vector3D(1.0, 0.0, 0.0));

    org.junit.Assert.assertEquals(0.0, mapped.getX(), 1.0e-15);
    org.junit.Assert.assertEquals(1.0, mapped.getY(), 1.0e-15);
    org.junit.Assert.assertEquals(0.0, mapped.getZ(), 1.0e-15);
}