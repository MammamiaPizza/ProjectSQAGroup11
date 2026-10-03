package org.apache.commons.lang.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class NumberUtilsLang300Test {

    @Test
    public void createNumberParsesSingleDigitLowercaseLongSuffix() {
        assertLongNumber(1L, NumberUtils.createNumber("1l"));
    }

    @Test
    public void createNumberParsesSingleDigitUppercaseLongSuffix() {
        assertLongNumber(1L, NumberUtils.createNumber("1L"));
    }

    @Test
    public void createNumberParsesNegativeLongSuffix() {
        assertLongNumber(-1L, NumberUtils.createNumber("-1l"));
        assertLongNumber(-1L, NumberUtils.createNumber("-1L"));
    }

    @Test
    public void createNumberParsesLongBoundaryValuesWithSuffix() {
        assertLongNumber(Long.MAX_VALUE,
                NumberUtils.createNumber(Long.toString(Long.MAX_VALUE) + "L"));
        assertLongNumber(Long.MIN_VALUE,
                NumberUtils.createNumber(Long.toString(Long.MIN_VALUE) + "l"));
    }

    @Test
    public void createNumberRetainsUnsuffixedIntegerAndDecimalClassification() {
        Number integer = NumberUtils.createNumber("1");
        assertEquals(Integer.class, integer.getClass());
        assertEquals(1, integer.intValue());

        Number decimal = NumberUtils.createNumber("1.5");
        assertEquals(Float.class, decimal.getClass());
        assertEquals(1.5f, decimal.floatValue(), 0.0f);

        Number exponent = NumberUtils.createNumber("1e3");
        assertEquals(Float.class, exponent.getClass());
        assertEquals(1000.0f, exponent.floatValue(), 0.0f);
    }

    @Test
    public void createNumberRejectsDecimalLongLiteral() {
        try {
            NumberUtils.createNumber("1.0L");
            fail("A long suffix is not valid on a decimal literal");
        } catch (NumberFormatException expected) {
            assertTrue(expected.getMessage().length() > 0);
        }
    }

    private void assertLongNumber(long expected, Number actual) {
        assertEquals(Long.class, actual.getClass());
        assertEquals(expected, actual.longValue());
    }
}
