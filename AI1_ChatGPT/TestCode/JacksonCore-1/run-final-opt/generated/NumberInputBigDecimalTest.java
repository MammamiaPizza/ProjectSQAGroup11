package com.fasterxml.jackson.core.io;

import java.math.BigDecimal;

import org.junit.Test;

import com.fasterxml.jackson.core.util.TextBuffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class NumberInputBigDecimalTest
{
    private static final String BAD_DECIMAL_MESSAGE =
            "can not be represented as BigDecimal";

    @Test
    public void parsesValidBigDecimalFromString()
    {
        BigDecimal value = NumberInput.parseBigDecimal("-12345.6700");

        assertEquals(new BigDecimal("-12345.6700"), value);
    }

    @Test
    public void parsesValidBigDecimalFromCharArraySlice()
    {
        char[] input = "xx+0.00120yy".toCharArray();

        BigDecimal value = NumberInput.parseBigDecimal(input, 2, 8);

        assertEquals(new BigDecimal("0.00120"), value);
    }

    @Test
    public void rejectsNaNStringWithUsefulBigDecimalMessage()
    {
        try {
            NumberInput.parseBigDecimal("NaN");
            fail("Expected NumberFormatException for NaN");
        } catch (NumberFormatException e) {
            assertHasBigDecimalMessage(e);
        }
    }

    @Test
    public void rejectsNaNCharArraySliceWithUsefulBigDecimalMessage()
    {
        char[] input = "xxNaNyy".toCharArray();

        try {
            NumberInput.parseBigDecimal(input, 2, 3);
            fail("Expected NumberFormatException for NaN slice");
        } catch (NumberFormatException e) {
            assertHasBigDecimalMessage(e);
        }
    }

    @Test
    public void textBufferContentsAsDecimalParsesValidCopiedNumber()
    {
        TextBuffer buffer = new TextBuffer(null);
        char[] input = "--42.5000++".toCharArray();
        buffer.resetWithCopy(input, 2, 7);

        assertEquals(new BigDecimal("42.5000"), buffer.contentsAsDecimal());
    }

    @Test
    public void textBufferStringContentsRejectNaNWithUsefulMessage()
    {
        TextBuffer buffer = new TextBuffer(null);
        buffer.resetWithString("NaN");

        assertTextBufferRejectsNaN(buffer);
    }

    @Test
    public void textBufferSharedContentsRejectNaNWithUsefulMessage()
    {
        TextBuffer buffer = new TextBuffer(null);
        char[] input = "xxNaNyy".toCharArray();
        buffer.resetWithShared(input, 2, 3);

        assertTextBufferRejectsNaN(buffer);
    }

    @Test
    public void textBufferCopiedContentsRejectNaNWithUsefulMessage()
    {
        TextBuffer buffer = new TextBuffer(null);
        char[] input = "xxNaNyy".toCharArray();
        buffer.resetWithCopy(input, 2, 3);

        assertTextBufferRejectsNaN(buffer);
    }

    private static void assertTextBufferRejectsNaN(TextBuffer buffer)
    {
        try {
            buffer.contentsAsDecimal();
            fail("Expected NumberFormatException for NaN");
        } catch (NumberFormatException e) {
            assertHasBigDecimalMessage(e);
        }
    }

    private static void assertHasBigDecimalMessage(NumberFormatException e)
    {
        assertNotNull("Invalid BigDecimal exception should provide a message", e.getMessage());
        assertTrue("Unexpected exception message: " + e.getMessage(),
                e.getMessage().contains(BAD_DECIMAL_MESSAGE));
    }
}
