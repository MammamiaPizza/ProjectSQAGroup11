@Test
public void chiSquareComputesStatisticForContingencyTable() {
    ChiSquareTestImpl test = new ChiSquareTestImpl();
    long[][] counts = {
        {40, 10},
        {20, 30}
    };

    assertEquals(16.666666666666668, test.chiSquare(counts), 1.0e-12);
}

@Test
public void chiSquareContingencyTablePValueAndAlphaDecisionAreConsistent()
    throws Exception {
    ChiSquareTestImpl test = new ChiSquareTestImpl();
    long[][] counts = {
        {40, 10},
        {20, 30}
    };

    double pValue = test.chiSquareTest(counts);
    assertTrue(pValue < 0.001);
    assertTrue(pValue > 0.00001);
    assertTrue(test.chiSquareTest(counts, 0.001));
    assertFalse(test.chiSquareTest(counts, 0.00001));
}

@Test
public void chiSquareDataSetsComparisonComputesStatisticForEqualTotals() {
    ChiSquareTestImpl test = new ChiSquareTestImpl();
    long[] observed1 = {10, 20, 30};
    long[] observed2 = {20, 20, 20};

    assertEquals(5.333333333333333, test.chiSquareDataSetsComparison(observed1, observed2),
        1.0e-12);
}

@Test(expected = IllegalArgumentException.class)
public void chiSquareRejectsNonRectangularContingencyTable() {
    ChiSquareTestImpl test = new ChiSquareTestImpl();
    test.chiSquare(new long[][] {
        {1, 2},
        {3}
    });
}