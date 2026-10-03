    @org.junit.Test
    public void testLogGammaHandlesInvalidArgumentsAndUnitValue() {
        org.junit.Assert.assertTrue(Double.isNaN(Gamma.logGamma(Double.NaN)));
        org.junit.Assert.assertTrue(Double.isNaN(Gamma.logGamma(0.0)));
        org.junit.Assert.assertEquals(0.0, Gamma.logGamma(1.0), 0.0);
    }

    @org.junit.Test
    public void testRegularizedGammaQReturnsNaNForInvalidArguments() {
        org.junit.Assert.assertTrue(Double.isNaN(Gamma.regularizedGammaQ(Double.NaN, 1.0)));
        org.junit.Assert.assertTrue(Double.isNaN(Gamma.regularizedGammaQ(1.0, Double.NaN)));
        org.junit.Assert.assertTrue(Double.isNaN(Gamma.regularizedGammaQ(0.0, 1.0)));
        org.junit.Assert.assertTrue(Double.isNaN(Gamma.regularizedGammaQ(1.0, -1.0)));
    }

    @org.junit.Test
    public void testRegularizedGammaQUsesComplementForLowerXAndSubunitShape() {
        org.junit.Assert.assertEquals(0.6065306597126334,
                Gamma.regularizedGammaQ(1.0, 0.5), 1.0e-14);
        org.junit.Assert.assertEquals(0.15729920705028513,
                Gamma.regularizedGammaQ(0.5, 1.0), 1.0e-14);
    }