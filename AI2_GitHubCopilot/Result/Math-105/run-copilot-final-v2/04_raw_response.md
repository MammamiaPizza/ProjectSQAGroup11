@Test
    public void testNoVariationInXReturnsNaN() {
        SimpleRegression regression = new SimpleRegression();
        regression.addData(5.0, 1.0);
        regression.addData(5.0, 2.0);
        assertTrue("Slope NaN with zero x-variance", Double.isNaN(regression.getSlope()));
    }

 @Test
 public void testGetSignificanceReturnsProbability() throws org.apache.commons.math.MathException {
     SimpleRegression regression = new SimpleRegression();
     regression.addData(1.0, 2.0);
     regression.addData(2.0, 3.0);
     regression.addData(3.0, 5.0);
     regression.addData(4.0, 7.0);
     regression.addData(5.0, 11.0);
     double sig = regression.getSignificance();
     assertTrue("Significance in [0,1]: " + sig, sig >= 0.0 && sig <= 1.0);
 }

 @Test
 public void testGetSlopeConfidenceIntervalReturnsFinite() throws
org.apache.commons.math.MathException {
     SimpleRegression regression = new SimpleRegression();
     regression.addData(1.0, 2.0);
     regression.addData(2.0, 3.0);
     regression.addData(3.0, 5.0);
     regression.addData(4.0, 7.0);
     regression.addData(5.0, 11.0);
     double ci = regression.getSlopeConfidenceInterval();
     assertFalse("Interval should be finite", Double.isNaN(ci) || Double.isInfinite(ci));
 }

 @Test(expected = IllegalArgumentException.class)
 public void testGetSlopeConfidenceIntervalInvalidAlpha() {
     new SimpleRegression().getSlopeConfidenceInterval(0.0);
 }