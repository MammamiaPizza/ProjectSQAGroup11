package org.apache.commons.lang3.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

public class NumberUtilsLang693Test {

    @Test
    public void createNumberUsesBigDecimalWhenNeitherFloatNorDoubleRetainsDecimalPrecision() {
        final String value = "1.2345678901234567890123456789";

        final Number result = NumberUtils.createNumber(value);

        assertTrue("High precision decimal must not be narrowed", result instanceof BigDecimal);
        assertEquals(new BigDecimal(value), result);
    }

    @Test
    public void createNumberUsesDoubleWhenItPreservesMoreDigitsThanFloat() {
        final String value = "1.234567890123";

        final Number result = NumberUtils.createNumber(value);

        assertTrue("The value should not be narrowed to Float", result instanceof Double);
        assertEquals(new BigDecimal(value), BigDecimal.valueOf(result.doubleValue()));
    }

    @Test
    public void createNumberPreservesHighPrecisionExponentValue() {
        final String value = "1.2345678901234567890123456789E10";

        final Number result = NumberUtils.createNumber(value);

        assertTrue("High precision exponent value must not lose digits", result instanceof BigDecimal);
        assertEquals(0, new BigDecimal(value).compareTo((BigDecimal) result));
    }

    @Test
    public void createNumberSelectsIntegralTypesAndHexadecimalValues() {
        assertEquals(Integer.valueOf(123), NumberUtils.createNumber("123"));
        assertEquals(Long.valueOf(2147483648L), NumberUtils.createNumber("2147483648"));
        assertEquals(new BigInteger("9223372036854775808"),
                NumberUtils.createNumber("9223372036854775808"));
        assertEquals(Integer.valueOf(16), NumberUtils.createNumber("0x10"));
    }

    @Test
    public void createNumberHonorsExplicitFloatingPointSuffixes() {
        final Number floatResult = NumberUtils.createNumber("1.25F");
        final Number doubleResult = NumberUtils.createNumber("1.25D");

        assertTrue(floatResult instanceof Float);
        assertEquals(Float.valueOf(1.25F), floatResult);
        assertTrue(doubleResult instanceof Double);
        assertEquals(Double.valueOf(1.25D), doubleResult);
    }

    @Test
    public void createNumberReturnsNullForNullInput() {
        assertNull(NumberUtils.createNumber(null));
    }

    @Test
    public void createNumberRejectsBlankInput() {
        try {
            NumberUtils.createNumber("   ");
            fail("Blank input must not be accepted as a number");
        } catch (final NumberFormatException expected) {
            assertTrue(expected.getMessage().length() > 0);
        }
    }

    @Test
    public void createNumberRejectsMalformedInput() {
        try {
            NumberUtils.createNumber("1.2.3");
            fail("Malformed numeric input must be rejected");
        } catch (final NumberFormatException expected) {
            assertTrue(expected.getMessage().length() > 0);
        }
    }
}