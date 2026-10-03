@Test
public void testExternalSecondMomentConstructorsUseProvidedMomentWithoutIncrementingIt() {
    org.apache.commons.math.stat.descriptive.moment.SecondMoment moment =
        new org.apache.commons.math.stat.descriptive.moment.SecondMoment();
    moment.increment(1.0);
    moment.increment(3.0);
    moment.increment(5.0);

    Variance biasCorrected = new Variance(moment);
    Variance uncorrected = new Variance(false, moment);

    assertEquals(4.0, biasCorrected.getResult(), 0.0);
    assertEquals(8.0 / 3.0, uncorrected.getResult(), 0.0);

    biasCorrected.increment(7.0);
    assertEquals(3L, moment.getN());
    assertEquals(4.0, biasCorrected.getResult(), 0.0);
}

@Test
public void testCopiesPreserveConfigurationAndAreIndependentOfSourceChanges() {
    Variance source = new Variance(false);
    source.increment(1.0);
    source.increment(3.0);
    source.increment(5.0);

    Variance copiedByConstructor = new Variance(source);
    Variance copiedByMethod = source.copy();
    Variance copiedByStaticMethod = new Variance();
    Variance.copy(source, copiedByStaticMethod);

    source.increment(7.0);

    assertFalse(copiedByConstructor.isBiasCorrected());
    assertEquals(3L, copiedByConstructor.getN());
    assertEquals(8.0 / 3.0, copiedByConstructor.getResult(), 0.0);
    assertEquals(8.0 / 3.0, copiedByMethod.getResult(), 0.0);
    assertEquals(8.0 / 3.0, copiedByStaticMethod.getResult(), 0.0);
    assertEquals(5.0, source.getResult(), 0.0);
}

@Test
public void testEvaluateWithSuppliedMeanUsesWholeArray() {
    double[] values = {1.0, 2.0, 4.0};

    assertEquals(2.5, new Variance().evaluate(values, 2.0), 0.0);
}