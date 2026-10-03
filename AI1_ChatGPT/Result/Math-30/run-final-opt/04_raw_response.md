@Test
public void testCustomTiesStrategyIsApplied() {
    final MannWhitneyUTest test = new MannWhitneyUTest(
            org.apache.commons.math3.stat.ranking.NaNStrategy.FIXED,
            org.apache.commons.math3.stat.ranking.TiesStrategy.MAXIMUM);

    org.junit.Assert.assertEquals(1.0,
            test.mannWhitneyU(new double[] { 1.0 }, new double[] { 1.0 }), 0.0);
}

@Test(expected = org.apache.commons.math3.exception.NullArgumentException.class)
public void testMannWhitneyURejectsNullSecondSample() {
    new MannWhitneyUTest().mannWhitneyU(new double[] { 1.0 }, null);
}

@Test(expected = org.apache.commons.math3.exception.NoDataException.class)
public void testMannWhitneyUTestRejectsEmptySecondSample() {
    new MannWhitneyUTest().mannWhitneyUTest(new double[] { 1.0 }, new double[0]);
}