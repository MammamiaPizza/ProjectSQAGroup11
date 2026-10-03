@Test
public void testAcosSpecialValuesAndEndpoints() {
    org.junit.Assert.assertTrue(java.lang.Double.isNaN(org.apache.commons.math3.util.FastMath.acos(java.lang.Double.NaN)));
    org.junit.Assert.assertTrue(java.lang.Double.isNaN(org.apache.commons.math3.util.FastMath.acos(2.0)));
    org.junit.Assert.assertTrue(java.lang.Double.isNaN(org.apache.commons.math3.util.FastMath.acos(-2.0)));
    org.junit.Assert.assertEquals(java.lang.Math.PI, org.apache.commons.math3.util.FastMath.acos(-1.0), 0.0);
    org.junit.Assert.assertEquals(0.0, org.apache.commons.math3.util.FastMath.acos(1.0), 0.0);
    org.junit.Assert.assertEquals(java.lang.Math.PI / 2.0, org.apache.commons.math3.util.FastMath.acos(0.0), 0.0);
}

@Test
public void testAbsHandlesNegativeValuesAndSignedZero() {
    org.junit.Assert.assertEquals(7, org.apache.commons.math3.util.FastMath.abs(-7));
    org.junit.Assert.assertEquals(java.lang.Long.MIN_VALUE,
                                  org.apache.commons.math3.util.FastMath.abs(java.lang.Long.MIN_VALUE));
    org.junit.Assert.assertEquals(java.lang.Double.doubleToRawLongBits(0.0),
                                  java.lang.Double.doubleToRawLongBits(
                                      org.apache.commons.math3.util.FastMath.abs(-0.0)));
    org.junit.Assert.assertEquals(java.lang.Float.floatToRawIntBits(0.0f),
                                  java.lang.Float.floatToRawIntBits(
                                      org.apache.commons.math3.util.FastMath.abs(-0.0f)));
    org.junit.Assert.assertTrue(java.lang.Double.isNaN(
        org.apache.commons.math3.util.FastMath.abs(java.lang.Double.NaN)));
}

@Test
public void testIEEEremainderMatchesStrictMath() {
    org.junit.Assert.assertEquals(java.lang.StrictMath.IEEEremainder(5.5, 2.0),
                                  org.apache.commons.math3.util.FastMath.IEEEremainder(5.5, 2.0),
                                  0.0);
    org.junit.Assert.assertEquals(java.lang.StrictMath.IEEEremainder(-7.0, 2.0),
                                  org.apache.commons.math3.util.FastMath.IEEEremainder(-7.0, 2.0),
                                  0.0);
    org.junit.Assert.assertTrue(java.lang.Double.isNaN(
        org.apache.commons.math3.util.FastMath.IEEEremainder(1.0, 0.0)));
}