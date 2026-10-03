package org.apache.commons.math.dfp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DfpNextAfterMath54Test {

    @Test
    public void nextAfterSmallestNegativeTowardZeroReturnsZero() {
        DfpField field = new DfpField(20);
        Dfp smallestNegative = field.getOne().power10K(Dfp.MIN_EXP).negate();

        assertFalse(smallestNegative.isInfinite());
        assertFalse(smallestNegative.isNaN());

        Dfp result = smallestNegative.nextAfter(field.getZero());

        assertFalse(result.isInfinite());
        assertFalse(result.isNaN());
        assertEquals(0.0, result.toDouble(), 0.0);
    }

    @Test
    public void nextAfterNegativeFiniteTowardZeroMovesUpWithoutCrossingZero() {
        DfpField field = new DfpField(20);
        Dfp negativeOne = field.newDfp(-1);

        Dfp result = negativeOne.nextAfter(field.getZero());

        assertTrue(negativeOne.lessThan(result));
        assertTrue(result.lessThan(field.getZero()));
        assertFalse(result.isInfinite());
    }

    @Test
    public void nextAfterPositiveFiniteTowardLargerValueMovesUp() {
        DfpField field = new DfpField(20);
        Dfp one = field.getOne();
        Dfp two = field.getTwo();

        Dfp result = one.nextAfter(two);

        assertTrue(one.lessThan(result));
        assertTrue(result.lessThan(two));
        assertFalse(result.isInfinite());
    }

    @Test
    public void nextAfterEqualValuesReturnsEqualValue() {
        DfpField field = new DfpField(20);
        Dfp value = field.newDfp("12.5");

        Dfp result = value.nextAfter(value);

        assertEquals(value, result);
        assertEquals(value.toDouble(), result.toDouble(), 0.0);
    }

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
}
