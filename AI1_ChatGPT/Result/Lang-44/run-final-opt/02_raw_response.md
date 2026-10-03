package org.apache.commons.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class NumberUtilsLang457Test {

    @Test
    public void testLang457BareLongQualifierThrowsNumberFormatException() {
        try {
            NumberUtils.createNumber("L");
            fail("A type qualifier without a numeric value must be rejected");
        } catch (NumberFormatException expected) {
            assertTrue(expected instanceof NumberFormatException);
        }
    }

    @Test
    public void testCreateNumberCreatesIntegerForOrdinaryIntegralValue() {
        Number number = NumberUtils.createNumber("123");

        assertTrue(number instanceof Integer);
        assertEquals(Integer.valueOf(123), number);
    }

    @Test
    public void testCreateNumberCreatesLongForLongQualifier() {
        Number number = NumberUtils.createNumber("42L");

        assertTrue(number instanceof Long);
        assertEquals(Long.valueOf(42L), number);
    }

    @Test
    public void testCreateNumberCreatesFloatAndDoubleForQualifiedDecimals() {
        Number floatNumber = NumberUtils.createNumber("1.25F");
        Number doubleNumber = NumberUtils.createNumber("1.25D");

        assertTrue(floatNumber instanceof Float);
        assertEquals(1.25f, floatNumber.floatValue(), 0.0f);
        assertTrue(doubleNumber instanceof Double);
        assertEquals(1.25d, doubleNumber.doubleValue(), 0.0d);
    }

    @Test
    public void testCreateNumberParsesScientificNotationWithQualifier() {
        Number number = NumberUtils.createNumber("1e3F");

        assertTrue(number instanceof Float);
        assertEquals(1000.0f, number.floatValue(), 0.0f);
    }

    @Test
    public void testCreateNumberRejectsIncompleteExponent() {
        try {
            NumberUtils.createNumber("1e");
            fail("An exponent without exponent digits must be rejected");
        } catch (NumberFormatException expected) {
            assertTrue(expected instanceof NumberFormatException);
        }
    }

    @Test
    public void testCreateNumberRejectsUnknownTypeQualifier() {
        try {
            NumberUtils.createNumber("12Q");
            fail("An unknown numeric type qualifier must be rejected");
        } catch (NumberFormatException expected) {
            assertTrue(expected instanceof NumberFormatException);
        }
    }

    @Test
    public void testCreateNumberReturnsNullForNullInput() {
        assertNull(NumberUtils.createNumber(null));
    }
}