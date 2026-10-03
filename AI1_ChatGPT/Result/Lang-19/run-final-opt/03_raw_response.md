package org.apache.commons.lang3.text.translate;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.junit.Test;

public class NumericEntityUnescaperLang19Test {

    @Test
    public void testCompleteDecimalEntityIsTranslated() throws Exception {
        final NumericEntityUnescaper unescaper = new NumericEntityUnescaper();
        final StringWriter writer = new StringWriter();

        final int consumed = unescaper.translate("&#65;", 0, writer);

        assertEquals(5, consumed);
        assertEquals("A", writer.toString());
    }

    @Test
    public void testCompleteHexadecimalEntityIsTranslated() throws Exception {
        final NumericEntityUnescaper unescaper = new NumericEntityUnescaper();
        final StringWriter writer = new StringWriter();

        final int consumed = unescaper.translate("&#x3f;", 0, writer);

        assertEquals(6, consumed);
        assertEquals("?", writer.toString());
    }

    @Test
    public void testUnfinishedEntityContainingOnlyPrefixIsNotTranslated() throws Exception {
        final NumericEntityUnescaper unescaper = new NumericEntityUnescaper();
        final StringWriter writer = new StringWriter();

        final int consumed = unescaper.translate("&#", 0, writer);

        assertEquals(0, consumed);
        assertEquals("", writer.toString());
    }

    @Test
    public void testUnfinishedHexEntityIsNotTranslated() throws Exception {
        final NumericEntityUnescaper unescaper = new NumericEntityUnescaper();
        final StringWriter writer = new StringWriter();

        final int consumed = unescaper.translate("&#x", 0, writer);

        assertEquals(0, consumed);
        assertEquals("", writer.toString());
    }

    @Test
    public void testEntityWithoutTerminatingSemicolonAtEndIsNotTranslated() throws Exception {
        final NumericEntityUnescaper unescaper = new NumericEntityUnescaper();
        final StringWriter writer = new StringWriter();

        final int consumed = unescaper.translate("&#123", 0, writer);

        assertEquals(5, consumed);
        assertEquals("{", writer.toString());
    }

    @Test
    public void testUnfinishedEntityAtNonZeroIndexIsNotTranslated() throws Exception {
        final NumericEntityUnescaper unescaper = new NumericEntityUnescaper();
        final StringWriter writer = new StringWriter();

        final int consumed = unescaper.translate("a&#x", 1, writer);

        assertEquals(0, consumed);
        assertEquals("", writer.toString());
    }

    @Test
    public void testMalformedEntityDoesNotWriteOutput() throws Exception {
        final NumericEntityUnescaper unescaper = new NumericEntityUnescaper();
        final StringWriter writer = new StringWriter();

        final int consumed = unescaper.translate("&#not-a-number;", 0, writer);

        assertEquals(0, consumed);
        assertEquals("", writer.toString());
    }

    @Test
    public void testNonEntityAtIndexIsIgnored() throws Exception {
        final NumericEntityUnescaper unescaper = new NumericEntityUnescaper();
        final StringWriter writer = new StringWriter();

        final int consumed = unescaper.translate("&amp;", 0, writer);

        assertEquals(0, consumed);
        assertEquals("", writer.toString());
    }

    @Test
    public void testSupplementaryCodePointIsWrittenAsSurrogatePair() throws Exception {
        final NumericEntityUnescaper unescaper = new NumericEntityUnescaper();
        final StringWriter writer = new StringWriter();

        final int consumed = unescaper.translate("&#x1F600;", 0, writer);

        assertEquals(9, consumed);
        assertEquals(new String(Character.toChars(0x1F600)), writer.toString());
    }
}