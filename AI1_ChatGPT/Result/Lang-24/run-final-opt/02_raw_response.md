package org.apache.commons.lang3.math;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NumberUtilsLang664Test {

    @Test
    public void isNumberAcceptsPlusSignedHexadecimalNumber() {
        assertTrue(NumberUtils.isNumber("+0x1A"));
    }

    @Test
    public void isNumberAcceptsPlusSignedHexadecimalWithAllHexDigits() {
        assertTrue(NumberUtils.isNumber("+0xCafeBabe"));
    }

    @Test
    public void isNumberContinuesToAcceptOtherSignedNumberForms() {
        assertTrue(NumberUtils.isNumber("+123"));
        assertTrue(NumberUtils.isNumber("-123.45"));
        assertTrue(NumberUtils.isNumber("+1.25e-3"));
        assertTrue(NumberUtils.isNumber("-0xFF"));
    }

    @Test
    public void isNumberRejectsSignedHexPrefixWithoutDigits() {
        assertFalse(NumberUtils.isNumber("+0x"));
        assertFalse(NumberUtils.isNumber("-0x"));
    }

    @Test
    public void isNumberRejectsInvalidDigitsAfterSignedHexPrefix() {
        assertFalse(NumberUtils.isNumber("+0x1G"));
        assertFalse(NumberUtils.isNumber("+0x-1"));
    }

    @Test
    public void isNumberRejectsEmptyAndSignOnlyInputs() {
        assertFalse(NumberUtils.isNumber(null));
        assertFalse(NumberUtils.isNumber(""));
        assertFalse(NumberUtils.isNumber("+"));
        assertFalse(NumberUtils.isNumber("-"));
    }
}