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

@Test
public void testCreateNumberHandlesNullAndDoubleMinusPrefix() {
    assertTrue(NumberUtils.createNumber(null) == null);
    assertTrue(NumberUtils.createNumber("--2") == null);
}

@Test
public void testNumericFactoryMethodsReturnNullForNullInput() {
    assertTrue(NumberUtils.createFloat(null) == null);
    assertTrue(NumberUtils.createDouble(null) == null);
    assertTrue(NumberUtils.createInteger(null) == null);
    assertTrue(NumberUtils.createLong(null) == null);
    assertTrue(NumberUtils.createBigInteger(null) == null);
    assertTrue(NumberUtils.createBigDecimal(null) == null);
}

@Test
public void testCreateNumberAndBigDecimalRejectBlankStrings() {
    boolean createNumberRejected = false;
    try {
        NumberUtils.createNumber(" ");
    } catch (NumberFormatException expected) {
        createNumberRejected = true;
    }
    assertTrue(createNumberRejected);

    boolean createBigDecimalRejected = false;
    try {
        NumberUtils.createBigDecimal("");
    } catch (NumberFormatException expected) {
        createBigDecimalRejected = true;
    }
    assertTrue(createBigDecimalRejected);
}

@Test
public void testCreateNumberCreatesIntegersForHexadecimalValues() {
    assertEquals(16, NumberUtils.createNumber("0x10").intValue());
    assertEquals(-16, NumberUtils.createNumber("-0x10").intValue());
}
}
