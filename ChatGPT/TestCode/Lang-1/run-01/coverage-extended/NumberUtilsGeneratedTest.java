package org.apache.commons.lang3.math;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

public class NumberUtilsGeneratedTest {

    // ---------------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------------

    @Test
    public void testConstructor() {
        NumberUtils utils = new NumberUtils();
        assertNotNull(utils);
    }

    // ---------------------------------------------------------------------
    // toInt
    // ---------------------------------------------------------------------

    @Test
    public void testToInt() {
        assertEquals(1, NumberUtils.toInt("1"));
        assertEquals(0, NumberUtils.toInt(null));
        assertEquals(0, NumberUtils.toInt("invalid"));
    }

    @Test
    public void testToIntWithDefault() {
        assertEquals(25, NumberUtils.toInt("25", 7));
        assertEquals(7, NumberUtils.toInt(null, 7));
        assertEquals(7, NumberUtils.toInt("abc", 7));
    }

    // ---------------------------------------------------------------------
    // toLong
    // ---------------------------------------------------------------------

    @Test
    public void testToLong() {
        assertEquals(123456789L, NumberUtils.toLong("123456789"));
        assertEquals(0L, NumberUtils.toLong(null));
        assertEquals(0L, NumberUtils.toLong("abc"));
    }

    @Test
    public void testToLongWithDefault() {
        assertEquals(123L, NumberUtils.toLong("123", 9L));
        assertEquals(9L, NumberUtils.toLong(null, 9L));
        assertEquals(9L, NumberUtils.toLong("bad", 9L));
    }

    // ---------------------------------------------------------------------
    // toFloat
    // ---------------------------------------------------------------------

    @Test
    public void testToFloat() {
        assertEquals(1.5f, NumberUtils.toFloat("1.5"), 0.0f);
        assertEquals(0.0f, NumberUtils.toFloat(null), 0.0f);
        assertEquals(0.0f, NumberUtils.toFloat("bad"), 0.0f);
    }

    @Test
    public void testToFloatWithDefault() {
        assertEquals(2.5f, NumberUtils.toFloat("2.5", 9.0f), 0.0f);
        assertEquals(9.0f, NumberUtils.toFloat(null, 9.0f), 0.0f);
        assertEquals(9.0f, NumberUtils.toFloat("bad", 9.0f), 0.0f);
    }

    // ---------------------------------------------------------------------
    // toDouble
    // ---------------------------------------------------------------------

    @Test
    public void testToDouble() {
        assertEquals(1.5d, NumberUtils.toDouble("1.5"), 0.0d);
        assertEquals(0.0d, NumberUtils.toDouble(null), 0.0d);
        assertEquals(0.0d, NumberUtils.toDouble("bad"), 0.0d);
    }

    @Test
    public void testToDoubleWithDefault() {
        assertEquals(2.5d, NumberUtils.toDouble("2.5", 9.0d), 0.0d);
        assertEquals(9.0d, NumberUtils.toDouble(null, 9.0d), 0.0d);
        assertEquals(9.0d, NumberUtils.toDouble("bad", 9.0d), 0.0d);
    }

    // ---------------------------------------------------------------------
    // toByte
    // ---------------------------------------------------------------------

    @Test
    public void testToByte() {
        assertEquals((byte) 12, NumberUtils.toByte("12"));
        assertEquals((byte) 0, NumberUtils.toByte(null));
        assertEquals((byte) 0, NumberUtils.toByte("200"));
    }

    @Test
    public void testToByteWithDefault() {
        assertEquals((byte) 12, NumberUtils.toByte("12", (byte) 5));
        assertEquals((byte) 5, NumberUtils.toByte(null, (byte) 5));
        assertEquals((byte) 5, NumberUtils.toByte("bad", (byte) 5));
    }

    // ---------------------------------------------------------------------
    // toShort
    // ---------------------------------------------------------------------

    @Test
    public void testToShort() {
        assertEquals((short) 123, NumberUtils.toShort("123"));
        assertEquals((short) 0, NumberUtils.toShort(null));
        assertEquals((short) 0, NumberUtils.toShort("40000"));
    }

    @Test
    public void testToShortWithDefault() {
        assertEquals((short) 123, NumberUtils.toShort("123", (short) 5));
        assertEquals((short) 5, NumberUtils.toShort(null, (short) 5));
        assertEquals((short) 5, NumberUtils.toShort("bad", (short) 5));
    }

    // ---------------------------------------------------------------------
    // createNumber - LANG-747
    // ---------------------------------------------------------------------

    @Test
    public void testCreateNumberHexWithinIntegerRange() {
        Number value = NumberUtils.createNumber("0x7FFFFFFF");

        assertEquals(Integer.class, value.getClass());
        assertEquals(Integer.MAX_VALUE, value.intValue());
    }

    @Test
    public void testCreateNumberEightDigitHexTooLargeForInteger() {
        Number value = NumberUtils.createNumber("0x80000000");

        assertEquals(Long.class, value.getClass());
        assertEquals(2147483648L, value.longValue());
    }

    @Test
    public void testCreateNumberNegativeHexWithinIntegerRange() {
        Number value = NumberUtils.createNumber("-0x80000000");

        assertNotNull(value);
        assertEquals((long) Integer.MIN_VALUE, value.longValue());
    }

    @Test
    public void testCreateNumberNegativeHexTooLargeForInteger() {
        Number value = NumberUtils.createNumber("-0x80000001");

        assertEquals(Long.class, value.getClass());
        assertEquals(-2147483649L, value.longValue());
    }

    @Test
    public void testCreateNumberNineDigitHexUsesLong() {
        Number value = NumberUtils.createNumber("0x100000000");

        assertEquals(Long.class, value.getClass());
        assertEquals(4294967296L, value.longValue());
    }

    @Test
    public void testCreateNumberLargeHexUsesBigInteger() {
        Number value =
                NumberUtils.createNumber("0x10000000000000000");

        assertEquals(BigInteger.class, value.getClass());
        assertEquals(new BigInteger("18446744073709551616"), value);
    }

    @Test
    public void testCreateNumberSupportedHexPrefixes() {
        assertEquals(Integer.valueOf(127),
                NumberUtils.createNumber("0X7F"));

        assertEquals(Integer.valueOf(127),
                NumberUtils.createNumber("#7F"));

        assertEquals(Integer.valueOf(-127),
                NumberUtils.createNumber("-#7F"));
    }

    // ---------------------------------------------------------------------
    // createNumber - normal types
    // ---------------------------------------------------------------------

    @Test
    public void testCreateNumberInteger() {
        Number value = NumberUtils.createNumber("123");

        assertEquals(Integer.class, value.getClass());
        assertEquals(123, value.intValue());
    }

    @Test
    public void testCreateNumberLong() {
        Number value = NumberUtils.createNumber("2147483648");

        assertEquals(Long.class, value.getClass());
        assertEquals(2147483648L, value.longValue());
    }

    @Test
    public void testCreateNumberBigInteger() {
        Number value =
                NumberUtils.createNumber("9223372036854775808");

        assertEquals(BigInteger.class, value.getClass());
        assertEquals(new BigInteger("9223372036854775808"), value);
    }

    @Test
    public void testCreateNumberFloat() {
        Number value = NumberUtils.createNumber("1.5");

        assertEquals(Float.class, value.getClass());
        assertEquals(1.5f, value.floatValue(), 0.0f);
    }

    @Test
    public void testCreateNumberFloatQualifier() {
        Number value = NumberUtils.createNumber("1.25F");

        assertEquals(Float.class, value.getClass());
        assertEquals(1.25f, value.floatValue(), 0.0f);
    }

    @Test
    public void testCreateNumberDoubleQualifier() {
        Number value = NumberUtils.createNumber("1.25D");

        assertEquals(Double.class, value.getClass());
        assertEquals(1.25d, value.doubleValue(), 0.0d);
    }

    @Test
    public void testCreateNumberLongQualifier() {
        Number value = NumberUtils.createNumber("123L");

        assertEquals(Long.class, value.getClass());
        assertEquals(123L, value.longValue());
    }

    @Test
    public void testCreateNumberLongQualifierFallsBackToBigInteger() {
        Number value =
                NumberUtils.createNumber("9223372036854775808L");

        assertEquals(BigInteger.class, value.getClass());
        assertEquals(new BigInteger("9223372036854775808"), value);
    }

    @Test
    public void testCreateNumberScientificNotation() {
        Number value = NumberUtils.createNumber("1.5E2");

        assertNotNull(value);
        assertEquals(150.0d, value.doubleValue(), 0.00001d);
    }

    @Test
    public void testCreateNumberNull() {
        assertNull(NumberUtils.createNumber(null));
    }

    @Test
    public void testCreateNumberBlankThrowsException() {
        try {
            NumberUtils.createNumber("");
            fail("Expected NumberFormatException");
        } catch (NumberFormatException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testCreateNumberWhitespaceThrowsException() {
        try {
            NumberUtils.createNumber(" 123 ");
            fail("Expected NumberFormatException");
        } catch (NumberFormatException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testCreateNumberInvalidQualifierThrowsException() {
        try {
            NumberUtils.createNumber("123X");
            fail("Expected NumberFormatException");
        } catch (NumberFormatException e) {
            assertNotNull(e);
        }
    }

    // ---------------------------------------------------------------------
    // Direct create methods
    // ---------------------------------------------------------------------

    @Test
    public void testCreateFloat() {
        assertEquals(Float.valueOf(1.5f), NumberUtils.createFloat("1.5"));
        assertNull(NumberUtils.createFloat(null));
    }

    @Test
    public void testCreateDouble() {
        assertEquals(Double.valueOf(1.5d), NumberUtils.createDouble("1.5"));
        assertNull(NumberUtils.createDouble(null));
    }

    @Test
    public void testCreateInteger() {
        assertEquals(Integer.valueOf(16), NumberUtils.createInteger("0x10"));
        assertEquals(Integer.valueOf(8), NumberUtils.createInteger("010"));
        assertNull(NumberUtils.createInteger(null));
    }

    @Test
    public void testCreateLong() {
        assertEquals(Long.valueOf(4294967296L),
                NumberUtils.createLong("0x100000000"));
        assertNull(NumberUtils.createLong(null));
    }

    @Test
    public void testCreateBigInteger() {
        assertEquals(new BigInteger("16"),
                NumberUtils.createBigInteger("0x10"));

        assertEquals(new BigInteger("16"),
                NumberUtils.createBigInteger("#10"));

        assertEquals(new BigInteger("-16"),
                NumberUtils.createBigInteger("-0x10"));

        assertNull(NumberUtils.createBigInteger(null));
    }

    @Test
    public void testCreateBigDecimal() {
        assertEquals(new BigDecimal("1.25"),
                NumberUtils.createBigDecimal("1.25"));

        assertNull(NumberUtils.createBigDecimal(null));
    }

    @Test
    public void testCreateBigDecimalBlankThrowsException() {
        try {
            NumberUtils.createBigDecimal("");
            fail("Expected NumberFormatException");
        } catch (NumberFormatException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testCreateBigDecimalDoubleMinusThrowsException() {
        try {
            NumberUtils.createBigDecimal("--1");
            fail("Expected NumberFormatException");
        } catch (NumberFormatException e) {
            assertNotNull(e);
        }
    }

    // ---------------------------------------------------------------------
    // Array min
    // ---------------------------------------------------------------------

    @Test
    public void testArrayMinMethods() {
        assertEquals(1L,
                NumberUtils.min(new long[] {3L, 1L, 2L}));

        assertEquals(1,
                NumberUtils.min(new int[] {3, 1, 2}));

        assertEquals((short) 1,
                NumberUtils.min(new short[] {3, 1, 2}));

        assertEquals((byte) 1,
                NumberUtils.min(new byte[] {3, 1, 2}));

        assertEquals(1.0d,
                NumberUtils.min(new double[] {3.0d, 1.0d, 2.0d}),
                0.0d);

        assertEquals(1.0f,
                NumberUtils.min(new float[] {3.0f, 1.0f, 2.0f}),
                0.0f);
    }

    @Test
    public void testArrayMinNaN() {
        assertTrue(Double.isNaN(
                NumberUtils.min(new double[] {1.0d, Double.NaN, 2.0d})));

        assertTrue(Float.isNaN(
                NumberUtils.min(new float[] {1.0f, Float.NaN, 2.0f})));
    }

    // ---------------------------------------------------------------------
    // Array max
    // ---------------------------------------------------------------------

    @Test
    public void testArrayMaxMethods() {
        assertEquals(3L,
                NumberUtils.max(new long[] {1L, 3L, 2L}));

        assertEquals(3,
                NumberUtils.max(new int[] {1, 3, 2}));

        assertEquals((short) 3,
                NumberUtils.max(new short[] {1, 3, 2}));

        assertEquals((byte) 3,
                NumberUtils.max(new byte[] {1, 3, 2}));

        assertEquals(3.0d,
                NumberUtils.max(new double[] {1.0d, 3.0d, 2.0d}),
                0.0d);

        assertEquals(3.0f,
                NumberUtils.max(new float[] {1.0f, 3.0f, 2.0f}),
                0.0f);
    }

    @Test
    public void testArrayMaxNaN() {
        assertTrue(Double.isNaN(
                NumberUtils.max(new double[] {1.0d, Double.NaN, 2.0d})));

        assertTrue(Float.isNaN(
                NumberUtils.max(new float[] {1.0f, Float.NaN, 2.0f})));
    }

    // ---------------------------------------------------------------------
    // Invalid arrays
    // ---------------------------------------------------------------------

    @Test
    public void testMinNullArrayThrowsException() {
        try {
            NumberUtils.min((int[]) null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testMinEmptyArrayThrowsException() {
        try {
            NumberUtils.min(new int[0]);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testMaxNullArrayThrowsException() {
        try {
            NumberUtils.max((long[]) null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testMaxEmptyArrayThrowsException() {
        try {
            NumberUtils.max(new long[0]);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    // ---------------------------------------------------------------------
    // Three-value min
    // ---------------------------------------------------------------------

    @Test
    public void testThreeValueMinMethods() {
        assertEquals(1L, NumberUtils.min(3L, 2L, 1L));
        assertEquals(1L, NumberUtils.min(1L, 2L, 3L));

        assertEquals(1, NumberUtils.min(3, 2, 1));
        assertEquals(1, NumberUtils.min(1, 2, 3));

        assertEquals((short) 1,
                NumberUtils.min((short) 3, (short) 2, (short) 1));
        assertEquals((short) 1,
                NumberUtils.min((short) 1, (short) 2, (short) 3));

        assertEquals((byte) 1,
                NumberUtils.min((byte) 3, (byte) 2, (byte) 1));
        assertEquals((byte) 1,
                NumberUtils.min((byte) 1, (byte) 2, (byte) 3));

        assertEquals(1.0d,
                NumberUtils.min(3.0d, 2.0d, 1.0d), 0.0d);

        assertEquals(1.0f,
                NumberUtils.min(3.0f, 2.0f, 1.0f), 0.0f);
    }

    @Test
    public void testThreeValueMinNaN() {
        assertTrue(Double.isNaN(
                NumberUtils.min(1.0d, Double.NaN, 2.0d)));

        assertTrue(Float.isNaN(
                NumberUtils.min(1.0f, Float.NaN, 2.0f)));
    }

    // ---------------------------------------------------------------------
    // Three-value max
    // ---------------------------------------------------------------------

    @Test
    public void testThreeValueMaxMethods() {
        assertEquals(3L, NumberUtils.max(1L, 2L, 3L));
        assertEquals(3L, NumberUtils.max(3L, 2L, 1L));

        assertEquals(3, NumberUtils.max(1, 2, 3));
        assertEquals(3, NumberUtils.max(3, 2, 1));

        assertEquals((short) 3,
                NumberUtils.max((short) 1, (short) 2, (short) 3));
        assertEquals((short) 3,
                NumberUtils.max((short) 3, (short) 2, (short) 1));

        assertEquals((byte) 3,
                NumberUtils.max((byte) 1, (byte) 2, (byte) 3));
        assertEquals((byte) 3,
                NumberUtils.max((byte) 3, (byte) 2, (byte) 1));

        assertEquals(3.0d,
                NumberUtils.max(1.0d, 2.0d, 3.0d), 0.0d);

        assertEquals(3.0f,
                NumberUtils.max(1.0f, 2.0f, 3.0f), 0.0f);
    }

    @Test
    public void testThreeValueMaxNaN() {
        assertTrue(Double.isNaN(
                NumberUtils.max(1.0d, Double.NaN, 2.0d)));

        assertTrue(Float.isNaN(
                NumberUtils.max(1.0f, Float.NaN, 2.0f)));
    }

    // ---------------------------------------------------------------------
    // isDigits
    // ---------------------------------------------------------------------

    @Test
    public void testIsDigits() {
        assertTrue(NumberUtils.isDigits("123456"));
        assertFalse(NumberUtils.isDigits("12a3"));
        assertFalse(NumberUtils.isDigits(""));
        assertFalse(NumberUtils.isDigits(null));
    }

    // ---------------------------------------------------------------------
    // isNumber
    // ---------------------------------------------------------------------

    @Test
    public void testIsNumberValidValues() {
        assertTrue(NumberUtils.isNumber("123"));
        assertTrue(NumberUtils.isNumber("-123"));
        assertTrue(NumberUtils.isNumber("1.5"));
        assertTrue(NumberUtils.isNumber("1E3"));
        assertTrue(NumberUtils.isNumber("1E-3"));
        assertTrue(NumberUtils.isNumber("123L"));
        assertTrue(NumberUtils.isNumber("1.5F"));
        assertTrue(NumberUtils.isNumber("0xFF"));
        assertTrue(NumberUtils.isNumber("-0xFF"));
    }

    @Test
    public void testIsNumberInvalidValues() {
        assertFalse(NumberUtils.isNumber(null));
        assertFalse(NumberUtils.isNumber(""));
        assertFalse(NumberUtils.isNumber("abc"));
        assertFalse(NumberUtils.isNumber("0x"));
        assertFalse(NumberUtils.isNumber("0xFG"));
        assertFalse(NumberUtils.isNumber("1E"));
        assertFalse(NumberUtils.isNumber("1E-"));
        assertFalse(NumberUtils.isNumber("1.2.3"));
        assertFalse(NumberUtils.isNumber("1E2L"));
    }

    // ---------------------------------------------------------------------
    // Coverage additions for createNumber
    // ---------------------------------------------------------------------

    @Test
    public void testCreateNumberExponentWithoutDecimal() {
        Number value = NumberUtils.createNumber("1E3");

        assertEquals(Float.class, value.getClass());
        assertEquals(1000.0f, value.floatValue(), 0.0f);
    }

    @Test
    public void testCreateNumberExponentBeforeDecimalThrowsException() {
        try {
            NumberUtils.createNumber("1E2.3");
            fail("Expected NumberFormatException");
        } catch (NumberFormatException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testCreateNumberDoubleExponentThrowsException() {
        try {
            NumberUtils.createNumber("123e4E5");
            fail("Expected NumberFormatException");
        } catch (NumberFormatException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testCreateNumberExponentWithFloatQualifier() {
        Number value = NumberUtils.createNumber("1E3F");

        assertEquals(Float.class, value.getClass());
        assertEquals(1000.0f, value.floatValue(), 0.0f);
    }

    @Test
    public void testCreateNumberInvalidLongQualifierForms() {
        try {
            NumberUtils.createNumber("1.0L");
            fail("Expected NumberFormatException");
        } catch (NumberFormatException e) {
            assertNotNull(e);
        }

        try {
            NumberUtils.createNumber("1E2L");
            fail("Expected NumberFormatException");
        } catch (NumberFormatException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testCreateNumberNegativeLongQualifier() {
        Number value = NumberUtils.createNumber("-123L");

        assertEquals(Long.class, value.getClass());
        assertEquals(-123L, value.longValue());
    }

    @Test
    public void testCreateNumberMalformedFloatQualifierThrowsException() {
        try {
            NumberUtils.createNumber("abcF");
            fail("Expected NumberFormatException");
        } catch (NumberFormatException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testCreateNumberFloatQualifierEscalatesToDouble() {
        Number value = NumberUtils.createNumber("1e40F");

        assertEquals(Double.class, value.getClass());
        assertEquals(1.0e40d, value.doubleValue(), 1.0e26d);
    }

    @Test
    public void testCreateNumberFloatQualifierEscalatesToBigDecimal() {
        Number value = NumberUtils.createNumber("1e400F");

        assertEquals(BigDecimal.class, value.getClass());
        assertEquals(new BigDecimal("1e400"), value);
    }

    @Test
    public void testCreateNumberInvalidDecimalFallsThroughAndThrows() {
        try {
            NumberUtils.createNumber("1..2");
            fail("Expected NumberFormatException");
        } catch (NumberFormatException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testCreateNumberEightDecimalDigitsUsesDouble() {
        Number value = NumberUtils.createNumber("1.12345678");

        assertEquals(Double.class, value.getClass());
        assertEquals(1.12345678d, value.doubleValue(), 0.0d);
    }

    @Test
    public void testCreateNumberHighPrecisionUsesBigDecimal() {
        Number value = NumberUtils.createNumber("1.12345678901234567");

        assertEquals(BigDecimal.class, value.getClass());
        assertEquals(new BigDecimal("1.12345678901234567"), value);
    }

    @Test
    public void testCreateNumberUnderflowEscalatesToDouble() {
        Number value = NumberUtils.createNumber("1e-50");

        assertEquals(Double.class, value.getClass());
        assertEquals(1.0e-50d, value.doubleValue(), 0.0d);
    }

    @Test
    public void testCreateNumberLargeExponentUsesBigDecimal() {
        Number value = NumberUtils.createNumber("1e400");

        assertEquals(BigDecimal.class, value.getClass());
        assertEquals(new BigDecimal("1e400"), value);
    }

    @Test
    public void testCreateNumberZeroAndTrailingDecimal() {
        Number zero = NumberUtils.createNumber("0.0");
        Number trailingDecimal = NumberUtils.createNumber("1.");

        assertEquals(Float.class, zero.getClass());
        assertEquals(0.0f, zero.floatValue(), 0.0f);

        assertEquals(Float.class, trailingDecimal.getClass());
        assertEquals(1.0f, trailingDecimal.floatValue(), 0.0f);
    }

    @Test
    public void testCreateNumberZeroExponentFloatQualifier() {
        Number value = NumberUtils.createNumber("0E0F");

        assertEquals(Float.class, value.getClass());
        assertEquals(0.0f, value.floatValue(), 0.0f);
    }

    // ---------------------------------------------------------------------
    // Coverage additions for createBigInteger
    // ---------------------------------------------------------------------

    @Test
    public void testCreateBigIntegerOctal() {
        assertEquals(new BigInteger("8"),
                NumberUtils.createBigInteger("010"));

        assertEquals(new BigInteger("-8"),
                NumberUtils.createBigInteger("-010"));
    }

    // ---------------------------------------------------------------------
    // Coverage additions for isNumber
    // ---------------------------------------------------------------------

    @Test
    public void testIsNumberAdditionalValidForms() {
        assertTrue(NumberUtils.isNumber("0x1a"));
        assertTrue(NumberUtils.isNumber("1e3"));
        assertTrue(NumberUtils.isNumber("1E+3"));
        assertTrue(NumberUtils.isNumber("1."));
        assertTrue(NumberUtils.isNumber("1D"));
        assertTrue(NumberUtils.isNumber("1f"));
        assertTrue(NumberUtils.isNumber("123l"));
    }

    @Test
    public void testIsNumberAdditionalInvalidForms() {
        assertFalse(NumberUtils.isNumber("1E2E3"));
        assertFalse(NumberUtils.isNumber("E1"));
        assertFalse(NumberUtils.isNumber("1+2"));
        assertFalse(NumberUtils.isNumber("1.2."));
        assertFalse(NumberUtils.isNumber("1E2."));
        assertFalse(NumberUtils.isNumber("-"));
        assertFalse(NumberUtils.isNumber("0a"));
        assertFalse(NumberUtils.isNumber("1.2L"));
        assertFalse(NumberUtils.isNumber("1e"));
    }
}
