package org.apache.commons.math.linear;

import static org.junit.Assert.assertEquals;

import org.apache.commons.math.exception.NumberIsTooLargeException;
import org.junit.Test;

public class OpenMapRealMatrixDimensionValidationTest {

    @Test
    public void testConstructorAcceptsNormalDimensions() {
        OpenMapRealMatrix matrix = new OpenMapRealMatrix(3, 4);

        assertEquals(3, matrix.getRowDimension());
        assertEquals(4, matrix.getColumnDimension());
    }

    @Test
    public void testConstructorAcceptsDimensionProductAtIntegerMaximum() {
        OpenMapRealMatrix matrix = new OpenMapRealMatrix(1, Integer.MAX_VALUE);

        assertEquals(1, matrix.getRowDimension());
        assertEquals(Integer.MAX_VALUE, matrix.getColumnDimension());
    }

    @Test(expected = NumberIsTooLargeException.class)
    public void testConstructorRejectsDimensionProductAboveIntegerMaximum() {
        new OpenMapRealMatrix(2, (Integer.MAX_VALUE / 2) + 1);
    }
}
