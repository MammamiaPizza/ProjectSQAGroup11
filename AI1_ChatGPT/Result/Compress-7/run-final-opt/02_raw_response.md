package org.apache.commons.compress.archivers.tar;

import junit.framework.TestCase;

public class TarUtilsGeneratedTest extends TestCase {

    public void testRoundTripTriggerLikeNameContainingPlusMinusCharacters() {
        String name = "0302-0601-3\u00b1\u00b1\u00b1F06\u00b1W220\u00b1ZB\u00b1LALALA\u00b1\u00b1\u00b1\u00b1\u00b1\u00b1\u00b1\u00b1\u00b1\u00b1CAN\u00b1\u00b1DC\u00b1\u00b1\u00b104\u00b1060302\u00b1MOE.model";
        byte[] buffer = new byte[name.length() + 10];

        int nextOffset = TarUtils.formatNameBytes(name, buffer, 0, buffer.length);

        assertEquals(buffer.length, nextOffset);
        assertEquals(name, TarUtils.parseName(buffer, 0, buffer.length));
    }

    public void testRoundTripSingleNonAsciiCharacter() {
        String name = "file\u00b1name";
        byte[] buffer = new byte[20];

        TarUtils.formatNameBytes(name, buffer, 0, buffer.length);

        assertEquals(name, TarUtils.parseName(buffer, 0, buffer.length));
    }

    public void testParseNameStopsAtTrailingNullPadding() {
        byte[] buffer = new byte[12];

        TarUtils.formatNameBytes("plain", buffer, 0, buffer.length);

        assertEquals("plain", TarUtils.parseName(buffer, 0, buffer.length));
        assertEquals(0, buffer[5]);
        assertEquals(0, buffer[11]);
    }

    public void testFormatNameBytesUsesOffsetAndReturnsEndOffset() {
        byte[] buffer = new byte[10];
        buffer[0] = 42;
        buffer[9] = 43;

        int nextOffset = TarUtils.formatNameBytes("a\u00b1c", buffer, 2, 6);

        assertEquals(8, nextOffset);
        assertEquals(42, buffer[0]);
        assertEquals(43, buffer[9]);
        assertEquals("a\u00b1c", TarUtils.parseName(buffer, 2, 6));
        assertEquals(0, buffer[5]);
        assertEquals(0, buffer[7]);
    }

    public void testNameAtFieldCapacityIsNotTruncated() {
        String name = "capacity";
        byte[] buffer = new byte[name.length()];

        assertEquals(buffer.length, TarUtils.formatNameBytes(name, buffer, 0, buffer.length));
        assertEquals(name, TarUtils.parseName(buffer, 0, buffer.length));
    }

    public void testNameLongerThanFieldIsTruncatedToFieldLength() {
        byte[] buffer = new byte[4];

        assertEquals(4, TarUtils.formatNameBytes("abcdef", buffer, 0, buffer.length));
        assertEquals("abcd", TarUtils.parseName(buffer, 0, buffer.length));
    }
}