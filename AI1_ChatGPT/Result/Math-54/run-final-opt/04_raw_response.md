@org.junit.Test
public void nextAfterSmallestPositiveTowardZeroReturnsZero() {
    org.apache.commons.math.dfp.DfpField field =
        new org.apache.commons.math.dfp.DfpField(20);
    org.apache.commons.math.dfp.Dfp smallestPositive =
        field.getZero().newInstance("1e-131072");

    org.junit.Assert.assertTrue(field.getZero().lessThan(smallestPositive));
    org.junit.Assert.assertFalse(smallestPositive.isInfinite());
    org.junit.Assert.assertFalse(smallestPositive.isNaN());

    org.apache.commons.math.dfp.Dfp result =
        smallestPositive.nextAfter(field.getZero());

    org.junit.Assert.assertFalse(result.isInfinite());
    org.junit.Assert.assertFalse(result.isNaN());
    org.junit.Assert.assertEquals(0.0, result.toDouble(), 0.0);
}

@org.junit.Test
public void newInstanceFromDoubleClassifiesSpecialValues() {
    org.apache.commons.math.dfp.DfpField field =
        new org.apache.commons.math.dfp.DfpField(20);
    org.apache.commons.math.dfp.Dfp zero = field.getZero();

    org.apache.commons.math.dfp.Dfp positiveInfinity =
        zero.newInstance(Double.POSITIVE_INFINITY);
    org.apache.commons.math.dfp.Dfp negativeInfinity =
        zero.newInstance(Double.NEGATIVE_INFINITY);
    org.apache.commons.math.dfp.Dfp nan = zero.newInstance(Double.NaN);

    org.junit.Assert.assertTrue(positiveInfinity.isInfinite());
    org.junit.Assert.assertTrue(negativeInfinity.isInfinite());
    org.junit.Assert.assertTrue(negativeInfinity.lessThan(zero));
    org.junit.Assert.assertTrue(nan.isNaN());
}