package org.apache.commons.lang3.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NumberUtilsCreateNumberLang638Test {

    @Test
    public void createNumberReturnsNullForNullInput() {
        assertNull(NumberUtils.createNumber(null));
    }

    @Test
    public void createNumberCreatesIntegerForPlainInteger() {
        Number number = NumberUtils.createNumber("123");

        assertTrue(number instanceof Integer);
        assertEquals(123, number.intValue());
    }

    @Test
    public void createNumberCreatesLongForLongSuffix() {
        Number number = NumberUtils.createNumber("12345678901L");

        assertTrue(number instanceof Long);
        assertEquals(12345678901L, number.longValue());
    }

    @Test
    public void createNumberCreatesFloatingValueForDecimal() {
        Number number = NumberUtils.createNumber("123.45");

        assertEquals(123.45d, number.doubleValue(), 0.000001d);
    }

    @Test
    public void createNumberAcceptsDecimalFractionsOfDifferentLengths() {
        assertEquals(0.1d, NumberUtils.createNumber("0.1").doubleValue(), 0.000001d);
        assertEquals(0.12d, NumberUtils.createNumber("0.12").doubleValue(), 0.000001d);
        assertEquals(0.123d, NumberUtils.createNumber("0.123").doubleValue(), 0.000001d);
        assertEquals(0.1234d, NumberUtils.createNumber("0.1234").doubleValue(), 0.000001d);
    }

    @Test
    public void createNumberAcceptsExponentWithNegativeSign() {
        Number number = NumberUtils.createNumber("1e-3");

        assertEquals(0.001d, number.doubleValue(), 0.0000001d);
    }

    @Test
    public void createNumberAcceptsExponentWithPositiveSign() {
        Number number = NumberUtils.createNumber("1E+3");

        assertEquals(1000.0d, number.doubleValue(), 0.0d);
    }

    @Test
    public void createNumberAcceptsDecimalWithSignedExponent() {
        Number number = NumberUtils.createNumber("1.25e-2");

        assertEquals(0.0125d, number.doubleValue(), 0.0000001d);
    }

    @Test
    public void createNumberAcceptsFloatingSuffixWithExponent() {
        Number number = NumberUtils.createNumber("1.5e2F");

        assertTrue(number instanceof Float);
        assertEquals(150.0f, number.floatValue(), 0.0f);
    }

    @Test(expected = NumberFormatException.class)
    public void createNumberRejectsBlankInput() {
        NumberUtils.createNumber(" ");
    }

    @Test(expected = NumberFormatException.class)
    public void createNumberRejectsExponentWithoutDigits() {
        NumberUtils.createNumber("1e");
    }
}