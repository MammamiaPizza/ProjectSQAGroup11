package org.apache.commons.lang3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class StringUtilsJoinLang703Test {

    @Test
    public void joinObjectArrayWithCharSeparatesOrdinaryValues() {
        assertEquals("alpha,beta,gamma",
                StringUtils.join(new Object[] { "alpha", "beta", "gamma" }, ','));
    }

    @Test
    public void joinObjectArrayWithCharAcceptsNullElementsIncludingFirst() {
        assertEquals(";beta;",
                StringUtils.join(new Object[] { null, "beta", null }, ';'));
    }

    @Test
    public void joinObjectArrayWithCharReturnsNullForNullArray() {
        assertNull(StringUtils.join((Object[]) null, ','));
    }

    @Test
    public void joinObjectArrayWithCharReturnsEmptyForEmptyArray() {
        assertEquals("", StringUtils.join(new Object[0], ','));
    }

    @Test
    public void joinObjectArrayWithStringSeparatesOrdinaryValues() {
        assertEquals("alpha::beta::gamma",
                StringUtils.join(new Object[] { "alpha", "beta", "gamma" }, "::"));
    }

    @Test
    public void joinObjectArrayWithStringAcceptsNullElementsIncludingFirst() {
        assertEquals("|beta|",
                StringUtils.join(new Object[] { null, "beta", null }, "|"));
    }

    @Test
    public void joinObjectArrayWithNullStringSeparatorConcatenatesValues() {
        assertEquals("alphabeta",
                StringUtils.join(new Object[] { "alpha", "beta" }, (String) null));
    }

    @Test
    public void joinObjectArrayWithStringReturnsNullForNullArray() {
        assertNull(StringUtils.join((Object[]) null, "|"));
    }

    @Test
    public void joinObjectArrayRangeWithNullFirstSelectedElementDoesNotThrow() {
        assertEquals(";tail",
                StringUtils.join(new Object[] { "ignored", null, "tail" }, ';', 1, 3));
    }

    @Test
    public void joinObjectArrayRangeWithNoSelectedElementsReturnsEmpty() {
        assertEquals("",
                StringUtils.join(new Object[] { "value" }, ',', 0, 0));
    }
}