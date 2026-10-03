@org.junit.Test
public void testAcosSpecialValuesAndRegularArgument() {
    org.junit.Assert.assertTrue(Double.isNaN(FastMath.acos(Double.NaN)));
    org.junit.Assert.assertTrue(Double.isNaN(FastMath.acos(1.1)));
    org.junit.Assert.assertTrue(Double.isNaN(FastMath.acos(-1.1)));
    org.junit.Assert.assertEquals(Math.PI, FastMath.acos(-1.0), 0.0);
    org.junit.Assert.assertEquals(0.0, FastMath.acos(1.0), 0.0);
    org.junit.Assert.assertEquals(Math.PI / 2.0, FastMath.acos(0.0), 0.0);
    org.junit.Assert.assertEquals(Math.acos(0.5), FastMath.acos(0.5), 1.0e-14);
}

@org.junit.Test
public void testAbsForPositiveAndNegativePrimitiveValues() {
    org.junit.Assert.assertEquals(7, FastMath.abs(-7));
    org.junit.Assert.assertEquals(7, FastMath.abs(7));
    org.junit.Assert.assertEquals(9L, FastMath.abs(-9L));
    org.junit.Assert.assertEquals(9L, FastMath.abs(9L));
    org.junit.Assert.assertEquals(3.5f, FastMath.abs(-3.5f), 0.0f);
    org.junit.Assert.assertEquals(3.5f, FastMath.abs(3.5f), 0.0f);
    org.junit.Assert.assertEquals(2.25, FastMath.abs(-2.25), 0.0);
    org.junit.Assert.assertEquals(2.25, FastMath.abs(2.25), 0.0);
}