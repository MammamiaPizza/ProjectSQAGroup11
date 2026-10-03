@org.junit.Test
public void testSphericalConstructorProducesExpectedCoordinatesAndAngles() {
    org.apache.commons.math.geometry.Vector3D vector =
        new org.apache.commons.math.geometry.Vector3D(Math.PI / 2.0, 0.0);

    org.junit.Assert.assertEquals(0.0, vector.getX(), 1.0e-15);
    org.junit.Assert.assertEquals(1.0, vector.getY(), 1.0e-15);
    org.junit.Assert.assertEquals(0.0, vector.getZ(), 1.0e-15);
    org.junit.Assert.assertEquals(1.0, vector.getNorm(), 1.0e-15);
    org.junit.Assert.assertEquals(Math.PI / 2.0, vector.getAlpha(), 1.0e-15);
    org.junit.Assert.assertEquals(0.0, vector.getDelta(), 1.0e-15);
}

@org.junit.Test
public void testLinearConstructorsCombineAllSuppliedVectors() {
    org.apache.commons.math.geometry.Vector3D u =
        new org.apache.commons.math.geometry.Vector3D(1.0, -2.0, 3.0);
    org.apache.commons.math.geometry.Vector3D v =
        new org.apache.commons.math.geometry.Vector3D(-4.0, 5.0, -6.0);
    org.apache.commons.math.geometry.Vector3D w =
        new org.apache.commons.math.geometry.Vector3D(7.0, 8.0, -9.0);
    org.apache.commons.math.geometry.Vector3D q =
        new org.apache.commons.math.geometry.Vector3D(-2.0, 4.0, 1.0);

    org.apache.commons.math.geometry.Vector3D two =
        new org.apache.commons.math.geometry.Vector3D(2.0, u, -0.5, v);
    org.junit.Assert.assertEquals(4.0, two.getX(), 0.0);
    org.junit.Assert.assertEquals(-6.5, two.getY(), 0.0);
    org.junit.Assert.assertEquals(9.0, two.getZ(), 0.0);

    org.apache.commons.math.geometry.Vector3D three =
        new org.apache.commons.math.geometry.Vector3D(1.0, u, 2.0, v, -1.0, w);
    org.junit.Assert.assertEquals(-14.0, three.getX(), 0.0);
    org.junit.Assert.assertEquals(0.0, three.getY(), 0.0);
    org.junit.Assert.assertEquals(0.0, three.getZ(), 0.0);

    org.apache.commons.math.geometry.Vector3D four =
        new org.apache.commons.math.geometry.Vector3D(1.0, u, 2.0, v, -1.0, w, 0.5, q);
    org.junit.Assert.assertEquals(-15.0, four.getX(), 0.0);
    org.junit.Assert.assertEquals(2.0, four.getY(), 0.0);
    org.junit.Assert.assertEquals(0.5, four.getZ(), 0.0);
}

@org.junit.Test
public void testNormsDistancesAndScaledArithmetic() {
    org.apache.commons.math.geometry.Vector3D vector =
        new org.apache.commons.math.geometry.Vector3D(2.0, -3.0, 6.0);
    org.apache.commons.math.geometry.Vector3D other =
        new org.apache.commons.math.geometry.Vector3D(-1.0, 1.0, 2.0);

    org.junit.Assert.assertEquals(11.0, vector.getNorm1(), 0.0);
    org.junit.Assert.assertEquals(7.0, vector.getNorm(), 0.0);
    org.junit.Assert.assertEquals(49.0, vector.getNormSq(), 0.0);
    org.junit.Assert.assertEquals(6.0, vector.getNormInf(), 0.0);
    org.junit.Assert.assertEquals(11.0,
        org.apache.commons.math.geometry.Vector3D.distance1(vector, other), 0.0);
    org.junit.Assert.assertEquals(Math.sqrt(41.0),
        org.apache.commons.math.geometry.Vector3D.distance(vector, other), 1.0e-15);
    org.junit.Assert.assertEquals(4.0,
        org.apache.commons.math.geometry.Vector3D.distanceInf(vector, other), 0.0);
    org.junit.Assert.assertEquals(41.0,
        org.apache.commons.math.geometry.Vector3D.distanceSq(vector, other), 0.0);

    org.apache.commons.math.geometry.Vector3D result = vector.add(-2.0, other);
    org.junit.Assert.assertEquals(4.0, result.getX(), 0.0);
    org.junit.Assert.assertEquals(-5.0, result.getY(), 0.0);
    org.junit.Assert.assertEquals(2.0, result.getZ(), 0.0);
}

@org.junit.Test
public void testNormalizeAndOrthogonalProduceUnitPerpendicularVectors() {
    org.apache.commons.math.geometry.Vector3D vector =
        new org.apache.commons.math.geometry.Vector3D(2.0, -3.0, 6.0);

    org.apache.commons.math.geometry.Vector3D normalized = vector.normalize();
    org.junit.Assert.assertEquals(2.0 / 7.0, normalized.getX(), 1.0e-15);
    org.junit.Assert.assertEquals(-3.0 / 7.0, normalized.getY(), 1.0e-15);
    org.junit.Assert.assertEquals(6.0 / 7.0, normalized.getZ(), 1.0e-15);
    org.junit.Assert.assertEquals(1.0, normalized.getNorm(), 1.0e-15);

    org.apache.commons.math.geometry.Vector3D orthogonal = vector.orthogonal();
    org.junit.Assert.assertEquals(1.0, orthogonal.getNorm(), 1.0e-15);
    org.junit.Assert.assertEquals(0.0,
        org.apache.commons.math.geometry.Vector3D.dotProduct(vector, orthogonal), 1.0e-15);
}