package org.apache.commons.lang3.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class NumberUtilsLang7Test {

    @Test
    public void createNumberRejectsDoubleLeadingSign() {
        assertNumberFormatException("--1");
    }

    @Test
    public void createNumberRejectsMalformedExponentSigns() {
        assertNumberFormatException("1e--2");
        assertNumberFormatException("1e+-2");
        assertNumberFormatException("1e-+2");
    }

    @Test
    public void createNumberRejectsIncompleteExponent() {
        assertNumberFormatException("1e");
        assertNumberFormatException("1e+");
        assertNumberFormatException("1e-");
    }

    @Test
    public void createNumberAcceptsIntegerAndLongSuffix() {
        Number integer = NumberUtils.createNumber("42");
        Number longValue = NumberUtils.createNumber("42L");

        assertTrue(integer instanceof Integer);
        assertEquals(42, integer.intValue());
        assertTrue(longValue instanceof Long);
        assertEquals(42L, longValue.longValue());
    }

    @Test
    public void createNumberAcceptsDecimalAndExponent() {
        Number decimal = NumberUtils.createNumber("1.5");
        Number exponent = NumberUtils.createNumber("1e3");

        assertEquals(1.5d, decimal.doubleValue(), 0.0d);
        assertEquals(1000.0d, exponent.doubleValue(), 0.0d);
    }

    @Test
    public void createNumberHonorsFloatingPointSuffixes() {
        Number floatValue = NumberUtils.createNumber("1.25F");
        Number doubleValue = NumberUtils.createNumber("1.25D");

        assertTrue(floatValue instanceof Float);
        assertEquals(1.25f, floatValue.floatValue(), 0.0f);
        assertTrue(doubleValue instanceof Double);
        assertEquals(1.25d, doubleValue.doubleValue(), 0.0d);
    }

    @Test
    public void createNumberReturnsNullForNullInput() {
        assertNull(NumberUtils.createNumber(null));
    }

    @Test
    public void createNumberRejectsBlankInput() {
        assertNumberFormatException("");
        assertNumberFormatException("   ");
    }

    private void assertNumberFormatException(String value) {
        try {
            NumberUtils.createNumber(value);
            fail("Expected NumberFormatException for: " + value);
        } catch (NumberFormatException expected) {
            assertFalse(expected.getMessage() == null && value == null);
        }
    }
}
