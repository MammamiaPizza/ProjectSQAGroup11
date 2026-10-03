package org.apache.commons.math3.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MathArraysLinearCombinationTest {

    @Test
    public void testLinearCombinationSinglePositiveElement() {
        assertEquals(6.0,
                     MathArrays.linearCombination(new double[] { 2.0 },
                                                  new double[] { 3.0 }),
                     0.0);
    }

    @Test
    public void testLinearCombinationSingleZeroElement() {
        assertEquals(0.0,
                     MathArrays.linearCombination(new double[] { 0.0 },
                                                  new double[] { -7.5 }),
                     0.0);
    }

    @Test
    public void testLinearCombinationSingleNegativeFractionalElement() {
        assertEquals(-0.75,
                     MathArrays.linearCombination(new double[] { -1.5 },
                                                  new double[] { 0.5 }),
                     0.0);
    }

    @Test
    public void testLinearCombinationMultipleElementsComputesDotProduct() {
        assertEquals(12.0,
                     MathArrays.linearCombination(new double[] { 1.0, -2.0, 3.0 },
                                                  new double[] { 4.0, 5.0, 6.0 }),
                     0.0);
    }
}
