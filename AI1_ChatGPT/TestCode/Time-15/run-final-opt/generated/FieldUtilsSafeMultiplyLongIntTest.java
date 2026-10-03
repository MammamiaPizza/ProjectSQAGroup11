package org.joda.time.field;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FieldUtilsSafeMultiplyLongIntTest {

    @Test
    public void testSafeMultiplyLongIntWithOrdinaryPositiveAndNegativeValues() {
        assertEquals(42L, FieldUtils.safeMultiply(6L, 7));
        assertEquals(-42L, FieldUtils.safeMultiply(-6L, 7));
        assertEquals(42L, FieldUtils.safeMultiply(-6L, -7));
    }

    @Test
    public void testSafeMultiplyLongIntWithZeroAndIdentityMultipliers() {
        assertEquals(0L, FieldUtils.safeMultiply(Long.MAX_VALUE, 0));
        assertEquals(Long.MAX_VALUE, FieldUtils.safeMultiply(Long.MAX_VALUE, 1));
        assertEquals(-Long.MAX_VALUE, FieldUtils.safeMultiply(Long.MAX_VALUE, -1));
    }

    @Test
    public void testSafeMultiplyLongIntAtFittingBoundary() {
        assertEquals(Long.MAX_VALUE - 1L,
                FieldUtils.safeMultiply(Long.MAX_VALUE / 2L, 2));
        assertEquals(Long.MIN_VALUE,
                FieldUtils.safeMultiply(Long.MIN_VALUE / 2L, 2));
    }

    @Test(expected = ArithmeticException.class)
    public void testSafeMultiplyLongIntDetectsPositiveOverflow() {
        FieldUtils.safeMultiply(Long.MAX_VALUE, 2);
    }

    @Test(expected = ArithmeticException.class)
    public void testSafeMultiplyLongIntDetectsNegativeOverflow() {
        FieldUtils.safeMultiply(Long.MIN_VALUE, 2);
    }

    @Test(expected = ArithmeticException.class)
    public void testSafeMultiplyLongIntRejectsNegatingLongMinimumValue() {
        FieldUtils.safeMultiply(Long.MIN_VALUE, -1);
    }
}
