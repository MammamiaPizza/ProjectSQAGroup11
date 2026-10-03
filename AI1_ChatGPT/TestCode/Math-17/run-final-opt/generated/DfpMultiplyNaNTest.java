package org.apache.commons.math3.dfp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DfpMultiplyNaNTest {

    @Test
    public void testFiniteMultiplyNaNSignalsInvalidAndReturnsNaN() {
        DfpField field = new DfpField(20);
        Dfp finite = field.newDfp(7);
        Dfp nan = field.newDfp(Double.NaN);

        field.clearIEEEFlags();
        Dfp result = finite.multiply(nan);

        assertTrue(result.isNaN());
        assertEquals(Dfp.QNAN, result.classify());
        assertEquals(DfpField.FLAG_INVALID, field.getIEEEFlags());
    }

    @Test
    public void testNaNMultiplyFiniteSignalsInvalidAndReturnsNaN() {
        DfpField field = new DfpField(20);
        Dfp nan = field.newDfp(Double.NaN);
        Dfp finite = field.newDfp(7);

        field.clearIEEEFlags();
        Dfp result = nan.multiply(finite);

        assertTrue(result.isNaN());
        assertEquals(Dfp.QNAN, result.classify());
        assertEquals(DfpField.FLAG_INVALID, field.getIEEEFlags());
    }

    @Test
    public void testFiniteMultiplicationProducesExactFiniteResult() {
        DfpField field = new DfpField(20);

        field.clearIEEEFlags();
        Dfp result = field.newDfp(3).multiply(field.newDfp(4));

        assertEquals(field.newDfp(12), result);
        assertEquals(Dfp.FINITE, result.classify());
        assertEquals(0, field.getIEEEFlags());
    }

    @Test
    public void testZeroTimesFiniteIsZeroWithoutException() {
        DfpField field = new DfpField(20);

        field.clearIEEEFlags();
        Dfp result = field.newDfp(0).multiply(field.newDfp(-9));

        assertTrue(result.isZero());
        assertEquals(Dfp.FINITE, result.classify());
        assertEquals(0, field.getIEEEFlags());
    }

    @Test
    public void testInfinityTimesFiniteIsInfinityWithoutException() {
        DfpField field = new DfpField(20);

        field.clearIEEEFlags();
        Dfp result = field.newDfp(Double.POSITIVE_INFINITY).multiply(field.newDfp(-2));

        assertTrue(result.isInfinite());
        assertTrue(result.strictlyNegative());
        assertEquals(0, field.getIEEEFlags());
    }

    @Test
    public void testZeroTimesInfinityIsInvalidNaN() {
        DfpField field = new DfpField(20);

        field.clearIEEEFlags();
        Dfp result = field.newDfp(0).multiply(field.newDfp(Double.POSITIVE_INFINITY));

        assertTrue(result.isNaN());
        assertEquals(Dfp.QNAN, result.classify());
        assertEquals(DfpField.FLAG_INVALID, field.getIEEEFlags());
    }
}
