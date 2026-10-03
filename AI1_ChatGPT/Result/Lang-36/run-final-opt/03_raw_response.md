package org.apache.commons.lang3.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NumberUtilsLang521Test {

    @Test
    public void testCreateNumberAcceptsTrailingDecimalPoint() {
        Number number = NumberUtils.createNumber("2.");

        assertNotNull(number);
        assertEquals(2.0d, number.doubleValue(), 0.0d);
    }

    @Test
    public void testCreateNumberAcceptsDecimalPointBoundaries() {
        Number leadingPoint = NumberUtils.createNumber(".2");
        Number fractionalPart = NumberUtils.createNumber("2.0");

        assertNotNull(leadingPoint);
        assertEquals(0.2f, leadingPoint.floatValue(), 0.0f);
        assertNotNull(fractionalPart);
        assertEquals(2.0d, fractionalPart.doubleValue(), 0.0d);
    }

    @Test
    public void testIsNumberAcceptsTrailingDecimalPoint() {
        assertTrue(NumberUtils.isNumber("2."));
    }

    @Test
    public void testIsNumberAcceptsDecimalPointBoundaries() {
        assertTrue(NumberUtils.isNumber(".2"));
        assertTrue(NumberUtils.isNumber("2.0"));
    }
}