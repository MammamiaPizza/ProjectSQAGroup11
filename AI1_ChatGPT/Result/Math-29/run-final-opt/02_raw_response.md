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
}