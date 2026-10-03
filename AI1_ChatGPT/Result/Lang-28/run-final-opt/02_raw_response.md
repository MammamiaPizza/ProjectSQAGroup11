package org.apache.commons.lang3.text.translate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NumericEntityUnescaperSupplementaryTest {

    private final NumericEntityUnescaper unescaper = new NumericEntityUnescaper();

    @Test
    public void testDecimalSupplementaryCharacterIsDecodedAsSurrogatePair() {
        String result = unescaper.translate("&#68642;");

        assertEquals("\uD803\uDC22", result);
        assertEquals(2, result.length());
        assertTrue(Character.isHighSurrogate(result.charAt(0)));
        assertTrue(Character.isLowSurrogate(result.charAt(1)));
    }

    @Test
    public void testHexSupplementaryCharacterIsDecodedAsSurrogatePair() {
        String result = unescaper.translate("&#x10C22;");

        assertEquals("\uD803\uDC22", result);
        assertEquals(0x10C22, Character.toCodePoint(result.charAt(0), result.charAt(1)));
    }

    @Test
    public void testUppercaseHexPrefixIsAccepted() {
        assertEquals("A", unescaper.translate("&#X41;"));
    }

    @Test
    public void testBmpNumericEntityIsDecoded() {
        assertEquals("\u00A9", unescaper.translate("&#169;"));
    }

    @Test
    public void testZeroCodePointIsDecoded() {
        assertEquals("\u0000", unescaper.translate("&#0;"));
    }

    @Test
    public void testEntitiesAreDecodedWithinOrdinaryText() {
        assertEquals("before A after", unescaper.translate("before &#65; after"));
    }

    @Test
    public void testPlainNonEntityTextIsUnchanged() {
        assertEquals("text & more text", unescaper.translate("text & more text"));
    }
}