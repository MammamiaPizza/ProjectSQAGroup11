package org.apache.commons.lang3.math;

import java.math.BigInteger;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NumberUtilsLang747Test {

    @Test
    public void createNumberReturnsIntegerForLargestPositiveEightDigitHexValue() {
        assertEquals(Integer.valueOf(Integer.MAX_VALUE),
                NumberUtils.createNumber("0x7fffffff"));
    }

    @Test
    public void createNumberReturnsLongForEightDigitHexValueBeyondIntegerRange() {
        assertEquals(Long.valueOf(0x80000000L),
                NumberUtils.createNumber("0x80000000"));
    }

    @Test
    public void createNumberReturnsLongForNegativeEightDigitHexMagnitudeBeyondIntegerRange() {
        assertEquals(Long.valueOf(-0x80000000L),
                NumberUtils.createNumber("-0x80000000"));
    }

    @Test
    public void createNumberReturnsLongForNineDigitHexValue() {
        assertEquals(Long.valueOf(0x100000000L),
                NumberUtils.createNumber("0x100000000"));
    }

    @Test
    public void createNumberReturnsBigIntegerForHexValueBeyondLongRange() {
        assertEquals(new BigInteger("8000000000000000", 16),
                NumberUtils.createNumber("0x8000000000000000"));
    }

    @Test(expected = NumberFormatException.class)
    public void createNumberRejectsMalformedHexValue() {
        NumberUtils.createNumber("0x8000000G");
    }
}
