package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OpenMapRealVectorMATH803Test {

    @Test
    public void testEbeDivideMixedTypesProducesNaNForImplicitZeroDividedByZero() {
        OpenMapRealVector left = new OpenMapRealVector(new double[] { 8.0, 0.0, -6.0, 0.0 });
        RealVector right = new ArrayRealVector(new double[] { 2.0, 0.0, 3.0, 5.0 });

        OpenMapRealVector result = left.ebeDivide(right);

        assertEquals(4, result.getDimension());
        assertEquals(4.0, result.getEntry(0), 0.0);
        assertTrue("implicit zero divided by zero must be NaN",
                   Double.isNaN(result.getEntry(1)));
        assertEquals(-2.0, result.getEntry(2), 0.0);
        assertEquals(0.0, result.getEntry(3), 0.0);
    }

    @Test
    public void testEbeMultiplyMixedTypesProducesNaNForImplicitZeroTimesInfinity() {
        OpenMapRealVector left = new OpenMapRealVector(new double[] { 2.0, 0.0, -3.0, 0.0 });
        RealVector right = new ArrayRealVector(new double[] {
            4.0, Double.POSITIVE_INFINITY, 5.0, -2.0
        });

        OpenMapRealVector result = left.ebeMultiply(right);

        assertEquals(4, result.getDimension());
        assertEquals(8.0, result.getEntry(0), 0.0);
        assertTrue("implicit zero multiplied by infinity must be NaN",
                   Double.isNaN(result.getEntry(1)));
        assertEquals(-15.0, result.getEntry(2), 0.0);
        assertEquals(0.0, result.getEntry(3), 0.0);
    }

    @Test
    public void testEbeMultiplySameOpenMapTypeProducesNaNForImplicitZeroTimesInfinity() {
        OpenMapRealVector left = new OpenMapRealVector(new double[] { 0.0, 7.0, 0.0, -2.0 });
        OpenMapRealVector right = new OpenMapRealVector(new double[] {
            Double.NEGATIVE_INFINITY, 3.0, 4.0, 0.5
        });

        OpenMapRealVector result = left.ebeMultiply(right);

        assertEquals(4, result.getDimension());
        assertTrue("implicit zero multiplied by negative infinity must be NaN",
                   Double.isNaN(result.getEntry(0)));
        assertEquals(21.0, result.getEntry(1), 0.0);
        assertEquals(0.0, result.getEntry(2), 0.0);
        assertEquals(-1.0, result.getEntry(3), 0.0);
    }

    @Test
    public void testEbeMultiplyMixedTypesProducesNaNForImplicitZeroTimesNaN() {
        OpenMapRealVector left = new OpenMapRealVector(new double[] { 5.0, 0.0, 0.0 });
        RealVector right = new ArrayRealVector(new double[] { 2.0, Double.NaN, -4.0 });

        OpenMapRealVector result = left.ebeMultiply(right);

        assertEquals(10.0, result.getEntry(0), 0.0);
        assertTrue("implicit zero multiplied by NaN must be NaN",
                   Double.isNaN(result.getEntry(1)));
        assertEquals(0.0, result.getEntry(2), 0.0);
    }

    @Test(expected = DimensionMismatchException.class)
    public void testEbeDivideRejectsDifferentDimensions() {
        OpenMapRealVector left = new OpenMapRealVector(3);
        RealVector right = new ArrayRealVector(2);

        left.ebeDivide(right);
    }

@Test
public void testEbeDivideSameOpenMapTypeProducesNaNForImplicitZeroDividedByNaN() {
    org.apache.commons.math3.linear.OpenMapRealVector numerator =
        new org.apache.commons.math3.linear.OpenMapRealVector(new double[] { 3.0, 0.0, -4.0 });
    org.apache.commons.math3.linear.OpenMapRealVector denominator =
        new org.apache.commons.math3.linear.OpenMapRealVector(
            new double[] { 1.0, Double.NaN, 2.0 });

    org.apache.commons.math3.linear.OpenMapRealVector result = numerator.ebeDivide(denominator);

    assertEquals(3.0, result.getEntry(0), 0.0);
    assertTrue("implicit zero divided by NaN must be NaN",
               Double.isNaN(result.getEntry(1)));
    assertEquals(-2.0, result.getEntry(2), 0.0);
}

@Test
public void testConstructorsSetDimensionAndHonorZeroTolerance() {
    org.apache.commons.math3.linear.OpenMapRealVector empty =
        new org.apache.commons.math3.linear.OpenMapRealVector();
    assertEquals(0, empty.getDimension());

    org.apache.commons.math3.linear.OpenMapRealVector defaultTolerance =
        new org.apache.commons.math3.linear.OpenMapRealVector(1, 1);
    defaultTolerance.setEntry(0, 5.0e-13);
    assertEquals(0.0, defaultTolerance.getEntry(0), 0.0);
    assertEquals(0.0, defaultTolerance.getSparsity(), 0.0);

    org.apache.commons.math3.linear.OpenMapRealVector customTolerance =
        new org.apache.commons.math3.linear.OpenMapRealVector(4, 2, 0.5);
    customTolerance.setEntry(2, 0.25);
    assertEquals(4, customTolerance.getDimension());
    assertEquals(0.0, customTolerance.getEntry(2), 0.0);
    assertEquals(0.0, customTolerance.getSparsity(), 0.0);
}

@Test
public void testAppendOpenMapAndCopyAreIndependent() {
    org.apache.commons.math3.linear.OpenMapRealVector left =
        new org.apache.commons.math3.linear.OpenMapRealVector(new double[] { 1.0, 0.0 });
    org.apache.commons.math3.linear.OpenMapRealVector right =
        new org.apache.commons.math3.linear.OpenMapRealVector(new double[] { 0.0, -2.0 });

    org.apache.commons.math3.linear.OpenMapRealVector appended = left.append(right);
    left.setEntry(0, 7.0);

    assertEquals(4, appended.getDimension());
    assertEquals(1.0, appended.getEntry(0), 0.0);
    assertEquals(0.0, appended.getEntry(1), 0.0);
    assertEquals(0.0, appended.getEntry(2), 0.0);
    assertEquals(-2.0, appended.getEntry(3), 0.0);

    org.apache.commons.math3.linear.OpenMapRealVector copy = appended.copy();
    copy.setEntry(3, 9.0);
    assertEquals(-2.0, appended.getEntry(3), 0.0);
}

@Test
public void testConstructFromRealVectorCopiesEntriesIndependently() {
    org.apache.commons.math3.linear.OpenMapRealVector source =
        new org.apache.commons.math3.linear.OpenMapRealVector(new double[] { 0.0, 5.0, 0.0 });

    org.apache.commons.math3.linear.OpenMapRealVector copy =
        new org.apache.commons.math3.linear.OpenMapRealVector(
            (org.apache.commons.math3.linear.RealVector) source);
    source.setEntry(1, -8.0);

    assertEquals(3, copy.getDimension());
    assertEquals(0.0, copy.getEntry(0), 0.0);
    assertEquals(5.0, copy.getEntry(1), 0.0);
    assertEquals(0.0, copy.getEntry(2), 0.0);
}
}
