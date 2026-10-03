package org.apache.commons.lang3.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class NumberUtilsLang822Test {

    @Test
    public void createNumberReturnsNullForNullInput() {
        assertNull(NumberUtils.createNumber(null));
    }

    @Test
    public void createNumberCreatesIntegerForPlainInteger() {
        Number number = NumberUtils.createNumber("42");

        assertTrue(number instanceof Integer);
        assertEquals(42, number.intValue());
    }

    @Test
    public void createNumberCreatesDecimalForFraction() {
        Number number = NumberUtils.createNumber("12.5");

        assertEquals(12.5d, number.doubleValue(), 0.0d);
    }

    @Test
    public void createNumberCreatesNumberForExponentNotation() {
        Number number = NumberUtils.createNumber("1e3");

        assertEquals(1000.0d, number.doubleValue(), 0.0d);
    }

    @Test
    public void createNumberHonorsLongSuffix() {
        Number number = NumberUtils.createNumber("123L");

        assertTrue(number instanceof Long);
        assertEquals(123L, number.longValue());
    }

    @Test
    public void createNumberRejectsIncompleteExponent() {
        assertInvalidNumber("1e");
        assertInvalidNumber("1E");
        assertInvalidNumber("1e+");
        assertInvalidNumber("1e-");
    }

    @Test
    public void createNumberRejectsMultipleExponentMarkers() {
        assertInvalidNumber("1eE2");
        assertInvalidNumber("1E2e3");
        assertInvalidNumber("1e2E3");
        assertInvalidNumber("1e+2E3");
    }

    @Test
    public void createNumberRejectsExponentForLongSuffix() {
        assertInvalidNumber("1e2L");
        assertInvalidNumber("1E2l");
    }

    private void assertInvalidNumber(String value) {
        try {
            NumberUtils.createNumber(value);
            fail("Expected NumberFormatException for " + value);
        } catch (NumberFormatException expected) {
            assertTrue(expected.getMessage() == null || expected.getMessage().length() >= 0);
        }
    }
}