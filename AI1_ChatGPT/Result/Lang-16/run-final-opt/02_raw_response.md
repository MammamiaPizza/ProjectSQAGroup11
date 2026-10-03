package org.apache.commons.lang3.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class NumberUtilsLang16Test {

    @Test
    public void createNumberAcceptsUppercaseHexPrefix() {
        Number number = NumberUtils.createNumber("0Xfade");

        assertNotNull(number);
        assertEquals(0xfadeL, number.longValue());
    }
}