@Test
public void constructorsUseConfiguredDimensionsAndZeroTolerance() {
    org.apache.commons.math.linear.OpenMapRealVector empty =
        new org.apache.commons.math.linear.OpenMapRealVector();
    org.junit.Assert.assertEquals(0, empty.getDimension());

    org.apache.commons.math.linear.OpenMapRealVector defaultTolerance =
        new org.apache.commons.math.linear.OpenMapRealVector(3, 2);
    defaultTolerance.setEntry(1,
        org.apache.commons.math.linear.OpenMapRealVector.DEFAULT_ZERO_TOLERANCE / 2.0);
    org.junit.Assert.assertEquals(3, defaultTolerance.getDimension());
    org.junit.Assert.assertEquals(0.0, defaultTolerance.getEntry(1), 0.0);

    org.apache.commons.math.linear.OpenMapRealVector customTolerance =
        new org.apache.commons.math.linear.OpenMapRealVector(3, 2, 0.5);
    customTolerance.setEntry(2, 0.25);
    org.junit.Assert.assertEquals(0.0, customTolerance.getEntry(2), 0.0);
}

@Test
public void copyConstructorCreatesAnIndependentVectorAndPreservesTolerance() {
    org.apache.commons.math.linear.OpenMapRealVector original =
        new org.apache.commons.math.linear.OpenMapRealVector(4, 2, 0.5);
    original.setEntry(0, 2.0);

    org.apache.commons.math.linear.OpenMapRealVector copy =
        new org.apache.commons.math.linear.OpenMapRealVector(original);
    original.setEntry(0, 7.0);
    copy.setEntry(2, 0.25);

    org.junit.Assert.assertEquals(2.0, copy.getEntry(0), 0.0);
    org.junit.Assert.assertEquals(0.0, original.getEntry(2), 0.0);
    org.junit.Assert.assertEquals(0.0, copy.getEntry(2), 0.0);
}

@Test
public void realVectorConstructorCopiesDimensionAndEntries() {
    org.apache.commons.math.linear.OpenMapRealVector source =
        new org.apache.commons.math.linear.OpenMapRealVector(new double[] { 0.0, 2.0, 0.0, -3.0 });

    org.apache.commons.math.linear.OpenMapRealVector copy =
        new org.apache.commons.math.linear.OpenMapRealVector(
            (org.apache.commons.math.linear.RealVector) source);

    org.junit.Assert.assertEquals(4, copy.getDimension());
    org.junit.Assert.assertEquals(0.0, copy.getEntry(0), 0.0);
    org.junit.Assert.assertEquals(2.0, copy.getEntry(1), 0.0);
    org.junit.Assert.assertEquals(0.0, copy.getEntry(2), 0.0);
    org.junit.Assert.assertEquals(-3.0, copy.getEntry(3), 0.0);
}

@Test
public void appendOpenMapVectorConcatenatesEntriesWithoutChangingOperands() {
    org.apache.commons.math.linear.OpenMapRealVector left =
        new org.apache.commons.math.linear.OpenMapRealVector(new double[] { 0.0, 2.0, 0.0 });
    org.apache.commons.math.linear.OpenMapRealVector right =
        new org.apache.commons.math.linear.OpenMapRealVector(new double[] { -4.0, 0.0 });

    org.apache.commons.math.linear.OpenMapRealVector appended = left.append(right);

    org.junit.Assert.assertEquals(5, appended.getDimension());
    org.junit.Assert.assertEquals(0.0, appended.getEntry(0), 0.0);
    org.junit.Assert.assertEquals(2.0, appended.getEntry(1), 0.0);
    org.junit.Assert.assertEquals(-4.0, appended.getEntry(3), 0.0);
    org.junit.Assert.assertEquals(0.0, appended.getEntry(4), 0.0);
    org.junit.Assert.assertEquals(3, left.getDimension());
    org.junit.Assert.assertEquals(2, right.getDimension());
}