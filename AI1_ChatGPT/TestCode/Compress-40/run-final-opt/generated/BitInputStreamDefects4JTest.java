package org.apache.commons.compress.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteOrder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BitInputStreamDefects4JTest {

    @Test
    public void littleEndianRetainsBitsThatOverflowLongCache() throws IOException {
        final byte[] data = new byte[9];
        data[8] = (byte) 0x80;

        final BitInputStream input =
            new BitInputStream(new ByteArrayInputStream(data), ByteOrder.LITTLE_ENDIAN);

        assertEquals(0L, input.readBits(7));
        assertEquals(0L, input.readBits(60));
        assertEquals(16L, input.readBits(5));
    }

    @Test
    public void bigEndianRetainsLeadingBitThatOverflowLongCache() throws IOException {
        final byte[] data = new byte[9];
        data[0] = 0x01;

        final BitInputStream input =
            new BitInputStream(new ByteArrayInputStream(data), ByteOrder.BIG_ENDIAN);

        assertEquals(0L, input.readBits(7));
        assertEquals(1L << 59, input.readBits(60));
    }

    @Test
    public void littleEndianCombinesCachedBitsWithFollowingByte() throws IOException {
        final BitInputStream input = new BitInputStream(
            new ByteArrayInputStream(new byte[] { (byte) 0xCA, 0x35 }),
            ByteOrder.LITTLE_ENDIAN);

        assertEquals(10L, input.readBits(4));
        assertEquals(92L, input.readBits(8));
    }

    @Test
    public void bigEndianCombinesCachedBitsWithFollowingByte() throws IOException {
        final BitInputStream input = new BitInputStream(
            new ByteArrayInputStream(new byte[] { (byte) 0xCA, 0x35 }),
            ByteOrder.BIG_ENDIAN);

        assertEquals(12L, input.readBits(4));
        assertEquals(163L, input.readBits(8));
    }

    @Test
    public void clearBitCacheDiscardsUnreadBitsFromBufferedByte() throws IOException {
        final BitInputStream input = new BitInputStream(
            new ByteArrayInputStream(new byte[] { 0x0F, 0x55 }),
            ByteOrder.LITTLE_ENDIAN);

        assertEquals(7L, input.readBits(3));
        input.clearBitCache();

        assertEquals(0x55L, input.readBits(8));
    }

    @Test
    public void returnsMinusOneWhenEndOfStreamOccursBeforeRequestedBits() throws IOException {
        final BitInputStream input = new BitInputStream(
            new ByteArrayInputStream(new byte[] { 0x01 }),
            ByteOrder.LITTLE_ENDIAN);

        assertEquals(-1L, input.readBits(9));
    }

    @Test
    public void rejectsNegativeBitCount() throws IOException {
        final BitInputStream input = new BitInputStream(
            new ByteArrayInputStream(new byte[0]),
            ByteOrder.LITTLE_ENDIAN);

        try {
            input.readBits(-1);
            fail("Negative bit counts must be rejected");
        } catch (final IllegalArgumentException expected) {
            assertEquals("count must not be negative or greater than 63", expected.getMessage());
        }
    }

    @Test
    public void rejectsBitCountGreaterThanSixtyThree() throws IOException {
        final BitInputStream input = new BitInputStream(
            new ByteArrayInputStream(new byte[0]),
            ByteOrder.BIG_ENDIAN);

        try {
            input.readBits(64);
            fail("Bit counts greater than 63 must be rejected");
        } catch (final IllegalArgumentException expected) {
            assertEquals("count must not be negative or greater than 63", expected.getMessage());
        }
    }
}
