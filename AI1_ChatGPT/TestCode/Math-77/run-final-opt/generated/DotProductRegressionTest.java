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
}
