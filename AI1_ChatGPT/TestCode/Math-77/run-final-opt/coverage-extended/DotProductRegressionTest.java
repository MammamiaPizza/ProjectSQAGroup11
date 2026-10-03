package org.apache.commons.math.linear;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DotProductRegressionTest {

    @Test
    public void arrayRealVectorDotProductUsesCorrespondingEntries() {
        ArrayRealVector left = new ArrayRealVector(new double[] { 2.0, -1.0, 4.0 });
        ArrayRealVector right = new ArrayRealVector(new double[] { 3.0, 2.0, 0.5 });

        assertEquals(6.0, left.dotProduct(right), 0.0);
    }

    @Test
    public void arrayRealVectorDotProductWithRealVectorOperandIsCorrect() {
        ArrayRealVector left = new ArrayRealVector(new double[] { 1.0, 2.0, 3.0 });
        RealVector right = new ArrayRealVector(new double[] { 1.0, 1.0, 1.0 });

        assertEquals(6.0, left.dotProduct(right), 0.0);
    }

    @Test
    public void openMapRealVectorDotProductUsesStoredEntriesOnlyAtMatchingIndices() {
        OpenMapRealVector left =
                new OpenMapRealVector(new double[] { 2.0, 0.0, -1.0, 0.0, 4.0 });
        OpenMapRealVector right =
                new OpenMapRealVector(new double[] { 3.0, 99.0, 2.0, -8.0, 0.5 });

        assertEquals(6.0, left.dotProduct(right), 0.0);
    }

    @Test
    public void openMapRealVectorDotProductWithRealVectorOperandIsCorrect() {
        OpenMapRealVector left = new OpenMapRealVector(new double[] { 1.0, 2.0, 3.0 });
        RealVector right = new OpenMapRealVector(new double[] { 1.0, 1.0, 1.0 });

        assertEquals(6.0, left.dotProduct(right), 0.0);
    }

    @Test
    public void denseAndSparseDotProductsAgreeInBothOperandOrders() {
        ArrayRealVector dense = new ArrayRealVector(new double[] { 1.0, 0.0, 2.0, -1.0 });
        OpenMapRealVector sparse =
                new OpenMapRealVector(new double[] { 2.0, 7.0, 3.0, 2.0 });

        assertEquals(6.0, dense.dotProduct(sparse), 0.0);
        assertEquals(6.0, sparse.dotProduct(dense), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void arrayRealVectorDotProductRejectsDifferentDimensions() {
        ArrayRealVector left = new ArrayRealVector(new double[] { 1.0, 2.0 });
        ArrayRealVector right = new ArrayRealVector(new double[] { 1.0, 2.0, 3.0 });

        left.dotProduct(right);
    }

    @Test(expected = IllegalArgumentException.class)
    public void openMapRealVectorDotProductRejectsDifferentDimensions() {
        OpenMapRealVector left = new OpenMapRealVector(new double[] { 1.0, 0.0 });
        OpenMapRealVector right = new OpenMapRealVector(new double[] { 1.0, 0.0, 3.0 });

        left.dotProduct(right);
    }

@Test
public void arrayRealVectorConstructorsInitializeExpectedDimensionsAndValues() {
    org.apache.commons.math.linear.ArrayRealVector empty =
        new org.apache.commons.math.linear.ArrayRealVector();
    org.apache.commons.math.linear.ArrayRealVector zeros =
        new org.apache.commons.math.linear.ArrayRealVector(3);
    org.apache.commons.math.linear.ArrayRealVector preset =
        new org.apache.commons.math.linear.ArrayRealVector(3, -2.5);

    assertEquals(0, empty.getDimension());
    assertEquals(3, zeros.getDimension());
    assertEquals(0.0, zeros.getEntry(0), 0.0);
    assertEquals(0.0, zeros.getEntry(2), 0.0);
    assertEquals(-2.5, preset.getEntry(0), 0.0);
    assertEquals(-2.5, preset.getEntry(2), 0.0);
}

@Test
public void arrayRealVectorCopyAndConcatenationPreserveEntries() {
    org.apache.commons.math.linear.ArrayRealVector first =
        new org.apache.commons.math.linear.ArrayRealVector(new double[] { 1.0, -2.0 });
    org.apache.commons.math.linear.ArrayRealVector second =
        new org.apache.commons.math.linear.ArrayRealVector(new double[] { 3.5, 4.0 });
    org.apache.commons.math.linear.ArrayRealVector copy =
        new org.apache.commons.math.linear.ArrayRealVector(first);
    org.apache.commons.math.linear.ArrayRealVector combined =
        new org.apache.commons.math.linear.ArrayRealVector(first, second);

    assertEquals(2, copy.getDimension());
    assertEquals(1.0, copy.getEntry(0), 0.0);
    assertEquals(-2.0, copy.getEntry(1), 0.0);
    assertEquals(4, combined.getDimension());
    assertEquals(1.0, combined.getEntry(0), 0.0);
    assertEquals(-2.0, combined.getEntry(1), 0.0);
    assertEquals(3.5, combined.getEntry(2), 0.0);
    assertEquals(4.0, combined.getEntry(3), 0.0);
}

@Test
public void openMapRealVectorConstructorsExposeSparseAndZeroEntries() {
    org.apache.commons.math.linear.OpenMapRealVector empty =
        new org.apache.commons.math.linear.OpenMapRealVector();
    org.apache.commons.math.linear.OpenMapRealVector sized =
        new org.apache.commons.math.linear.OpenMapRealVector(4);
    org.apache.commons.math.linear.OpenMapRealVector values =
        new org.apache.commons.math.linear.OpenMapRealVector(
            new double[] { 0.0, 2.0, 0.0, -3.0 });

    assertEquals(0, empty.getDimension());
    assertEquals(4, sized.getDimension());
    assertEquals(0.0, sized.getEntry(3), 0.0);
    assertEquals(4, values.getDimension());
    assertEquals(0.0, values.getEntry(0), 0.0);
    assertEquals(2.0, values.getEntry(1), 0.0);
    assertEquals(0.0, values.getEntry(2), 0.0);
    assertEquals(-3.0, values.getEntry(3), 0.0);
}
}
