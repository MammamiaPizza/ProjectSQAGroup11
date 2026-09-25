package org.apache.commons.lang3.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

public class NumberUtilsCopilotGeneratedTest {

    @Test
    public void constructorCreatesInstance() {
        assertNotNull(new NumberUtils());
    }

    @Test
    public void constantsHaveExpectedValues() {
        assertEquals(Long.valueOf(0L), NumberUtils.LONG_ZERO);
        assertEquals(Long.valueOf(1L), NumberUtils.LONG_ONE);
        assertEquals(Long.valueOf(-1L), NumberUtils.LONG_MINUS_ONE);
        assertEquals(Integer.valueOf(0), NumberUtils.INTEGER_ZERO);
        assertEquals(Integer.valueOf(1), NumberUtils.INTEGER_ONE);
        assertEquals(Integer.valueOf(-1), NumberUtils.INTEGER_MINUS_ONE);
        assertEquals(Short.valueOf((short) 0), NumberUtils.SHORT_ZERO);
        assertEquals(Short.valueOf((short) 1), NumberUtils.SHORT_ONE);
        assertEquals(Short.valueOf((short) -1), NumberUtils.SHORT_MINUS_ONE);
        assertEquals(Byte.valueOf((byte) 0), NumberUtils.BYTE_ZERO);
        assertEquals(Byte.valueOf((byte) 1), NumberUtils.BYTE_ONE);
        assertEquals(Byte.valueOf((byte) -1), NumberUtils.BYTE_MINUS_ONE);
        assertEquals(Double.valueOf(0.0d), NumberUtils.DOUBLE_ZERO);
        assertEquals(Double.valueOf(1.0d), NumberUtils.DOUBLE_ONE);
        assertEquals(Double.valueOf(-1.0d), NumberUtils.DOUBLE_MINUS_ONE);
        assertEquals(Float.valueOf(0.0f), NumberUtils.FLOAT_ZERO);
        assertEquals(Float.valueOf(1.0f), NumberUtils.FLOAT_ONE);
        assertEquals(Float.valueOf(-1.0f), NumberUtils.FLOAT_MINUS_ONE);
    }

    @Test
    public void toIntHandlesValidNullAndInvalidValues() {
        assertEquals(42, NumberUtils.toInt("42"));
        assertEquals(0, NumberUtils.toInt(null));
        assertEquals(0, NumberUtils.toInt(""));
        assertEquals(99, NumberUtils.toInt(null, 99));
        assertEquals(99, NumberUtils.toInt("invalid", 99));
        assertEquals(Integer.MAX_VALUE,
                NumberUtils.toInt(String.valueOf(Integer.MAX_VALUE)));
    }

    @Test
    public void toLongHandlesValidNullAndInvalidValues() {
        assertEquals(42L, NumberUtils.toLong("42"));
        assertEquals(0L, NumberUtils.toLong(null));
        assertEquals(0L, NumberUtils.toLong(""));
        assertEquals(99L, NumberUtils.toLong(null, 99L));
        assertEquals(99L, NumberUtils.toLong("invalid", 99L));
        assertEquals(Long.MAX_VALUE,
                NumberUtils.toLong(String.valueOf(Long.MAX_VALUE)));
    }

    @Test
    public void toFloatHandlesValidNullAndInvalidValues() {
        assertEquals(1.5f, NumberUtils.toFloat("1.5"), 0.0f);
        assertEquals(0.0f, NumberUtils.toFloat(null), 0.0f);
        assertEquals(0.0f, NumberUtils.toFloat(""), 0.0f);
        assertEquals(2.5f, NumberUtils.toFloat(null, 2.5f), 0.0f);
        assertEquals(2.5f, NumberUtils.toFloat("invalid", 2.5f), 0.0f);
    }

    @Test
    public void toDoubleHandlesValidNullAndInvalidValues() {
        assertEquals(1.5d, NumberUtils.toDouble("1.5"), 0.0d);
        assertEquals(0.0d, NumberUtils.toDouble(null), 0.0d);
        assertEquals(0.0d, NumberUtils.toDouble(""), 0.0d);
        assertEquals(2.5d, NumberUtils.toDouble(null, 2.5d), 0.0d);
        assertEquals(2.5d, NumberUtils.toDouble("invalid", 2.5d), 0.0d);
    }

    @Test
    public void toByteHandlesValidNullAndInvalidValues() {
        assertEquals((byte) 12, NumberUtils.toByte("12"));
        assertEquals((byte) 0, NumberUtils.toByte(null));
        assertEquals((byte) 7, NumberUtils.toByte(null, (byte) 7));
        assertEquals((byte) 7, NumberUtils.toByte("invalid", (byte) 7));
        assertEquals(Byte.MAX_VALUE,
                NumberUtils.toByte(String.valueOf(Byte.MAX_VALUE)));
    }

    @Test
    public void toShortHandlesValidNullAndInvalidValues() {
        assertEquals((short) 1234, NumberUtils.toShort("1234"));
        assertEquals((short) 0, NumberUtils.toShort(null));
        assertEquals((short) 7, NumberUtils.toShort(null, (short) 7));
        assertEquals((short) 7, NumberUtils.toShort("invalid", (short) 7));
        assertEquals(Short.MAX_VALUE,
                NumberUtils.toShort(String.valueOf(Short.MAX_VALUE)));
    }

    @Test
    public void createNumberHandlesNullAndIntegralTypes() {
        assertEquals(Integer.valueOf(123), NumberUtils.createNumber("123"));
        assertEquals(Long.valueOf(2147483648L),
                NumberUtils.createNumber("2147483648"));
        assertEquals(new BigInteger("9223372036854775808"),
                NumberUtils.createNumber("9223372036854775808"));
        assertEquals(null, NumberUtils.createNumber(null));
    }

    @Test
    public void createNumberHandlesFloatingPointTypes() {
        assertEquals(Float.valueOf(1.5f), NumberUtils.createNumber("1.5"));
        assertEquals(Double.valueOf(1.23456789d),
                NumberUtils.createNumber("1.23456789"));
        assertEquals(new BigDecimal("1.12345678901234567"),
                NumberUtils.createNumber("1.12345678901234567"));
        assertEquals(Float.valueOf(1.5f), NumberUtils.createNumber("1.5F"));
        assertEquals(Double.valueOf(1.5d), NumberUtils.createNumber("1.5D"));
        assertEquals(Float.valueOf(1.5f), NumberUtils.createNumber("1.5e0"));
    }

    @Test
    public void createNumberHandlesOctalAndLongSuffix() {
        assertEquals(Integer.valueOf(8), NumberUtils.createNumber("010"));
        assertEquals(Long.valueOf(123L), NumberUtils.createNumber("123L"));
        assertEquals(new BigInteger("9223372036854775808"),
                NumberUtils.createNumber("9223372036854775808L"));
    }

    @Test
    public void createNumberHandlesHexadecimalIntegerBoundary() {
        Number value = NumberUtils.createNumber("0x7FFFFFFF");

        assertTrue(value instanceof Integer);
        assertEquals(Integer.valueOf(Integer.MAX_VALUE), value);
    }

    @Test
    public void createNumberHandlesEightDigitHexadecimalLongValues() {
        Number firstLongValue = NumberUtils.createNumber("0x80000000");
        Number maximumEightDigitValue = NumberUtils.createNumber("0xFFFFFFFF");

        assertTrue(firstLongValue instanceof Long);
        assertEquals(Long.valueOf(2147483648L), firstLongValue);
        assertTrue(maximumEightDigitValue instanceof Long);
        assertEquals(Long.valueOf(4294967295L), maximumEightDigitValue);
    }

    @Test
    public void createNumberHandlesNegativeHexadecimalLongValues() {
        assertEquals(Long.valueOf(-2147483648L),
                NumberUtils.createNumber("-0x80000000"));
        assertEquals(Long.valueOf(-4294967295L),
                NumberUtils.createNumber("-0xFFFFFFFF"));
    }

    @Test
    public void createNumberHandlesHexadecimalPrefixesAndSizeBoundaries() {
        assertEquals(Long.valueOf(4294967295L),
                NumberUtils.createNumber("0XFFFFFFFF"));
        assertEquals(Long.valueOf(4294967295L),
                NumberUtils.createNumber("#FFFFFFFF"));
        assertEquals(4294967295L,
                NumberUtils.createNumber("-#FFFFFFFF").longValue() * -1L);

        Number maximumLong = NumberUtils.createNumber("0x7FFFFFFFFFFFFFFF");
        Number seventeenDigits =
                NumberUtils.createNumber("0x10000000000000000");

        assertTrue(maximumLong instanceof Long);
        assertEquals(Long.valueOf(Long.MAX_VALUE), maximumLong);
        assertTrue(seventeenDigits instanceof BigInteger);
        assertEquals(new BigInteger("10000000000000000", 16),
                seventeenDigits);
    }

    @Test
    public void createFloatAndDoubleHandleNullAndValues() {
        assertEquals(Float.valueOf(2.5f), NumberUtils.createFloat("2.5"));
        assertEquals(Double.valueOf(2.5d), NumberUtils.createDouble("2.5"));
        assertEquals(null, NumberUtils.createFloat(null));
        assertEquals(null, NumberUtils.createDouble(null));
    }

    @Test
    public void createIntegerAndLongHandleNullAndNotations() {
        assertEquals(Integer.valueOf(255), NumberUtils.createInteger("0xFF"));
        assertEquals(Long.valueOf(255L), NumberUtils.createLong("0xFF"));
        assertEquals(null, NumberUtils.createInteger(null));
        assertEquals(null, NumberUtils.createLong(null));
    }

    @Test
    public void createBigIntegerHandlesDecimalHexAndNull() {
        assertEquals(new BigInteger("12345678901234567890"),
                NumberUtils.createBigInteger("12345678901234567890"));
        assertEquals(new BigInteger("FFFFFFFF", 16),
                NumberUtils.createBigInteger("0xFFFFFFFF"));
        assertEquals(new BigInteger("-FF", 16),
                NumberUtils.createBigInteger("-#FF"));
        assertEquals(null, NumberUtils.createBigInteger(null));
    }

    @Test
    public void createBigDecimalHandlesValuesAndNull() {
        assertEquals(new BigDecimal("123.4500"),
                NumberUtils.createBigDecimal("123.4500"));
        assertEquals(null, NumberUtils.createBigDecimal(null));
    }

    @Test
    public void minAndMaxArrayMethodsReturnExpectedValues() {
        assertEquals(-3L, NumberUtils.min(new long[] {4L, -3L, 2L}));
        assertEquals(4L, NumberUtils.max(new long[] {4L, -3L, 2L}));
        assertEquals(-3, NumberUtils.min(new int[] {4, -3, 2}));
        assertEquals(4, NumberUtils.max(new int[] {4, -3, 2}));
        assertEquals((short) -3,
                NumberUtils.min(new short[] {4, -3, 2}));
        assertEquals((short) 4,
                NumberUtils.max(new short[] {4, -3, 2}));
        assertEquals((byte) -3,
                NumberUtils.min(new byte[] {4, -3, 2}));
        assertEquals((byte) 4,
                NumberUtils.max(new byte[] {4, -3, 2}));
        assertEquals(-3.0d,
                NumberUtils.min(new double[] {4.0d, -3.0d, 2.0d}), 0.0d);
        assertEquals(4.0d,
                NumberUtils.max(new double[] {4.0d, -3.0d, 2.0d}), 0.0d);
        assertEquals(-3.0f,
                NumberUtils.min(new float[] {4.0f, -3.0f, 2.0f}), 0.0f);
        assertEquals(4.0f,
                NumberUtils.max(new float[] {4.0f, -3.0f, 2.0f}), 0.0f);
    }

    @Test
    public void floatingPointArrayExtremaReturnNaNWhenEncountered() {
        assertTrue(Double.isNaN(NumberUtils.min(
                new double[] {1.0d, Double.NaN})));
        assertTrue(Double.isNaN(NumberUtils.max(
                new double[] {1.0d, Double.NaN})));
        assertTrue(Float.isNaN(NumberUtils.min(
                new float[] {1.0f, Float.NaN})));
        assertTrue(Float.isNaN(NumberUtils.max(
                new float[] {1.0f, Float.NaN})));
    }

    @Test
    public void minAndMaxThreeArgumentMethodsReturnExpectedValues() {
        assertEquals(-3L, NumberUtils.min(4L, -3L, 2L));
        assertEquals(4L, NumberUtils.max(4L, -3L, 2L));
        assertEquals(-3, NumberUtils.min(4, -3, 2));
        assertEquals(4, NumberUtils.max(4, -3, 2));
        assertEquals((short) -3,
                NumberUtils.min((short) 4, (short) -3, (short) 2));
        assertEquals((short) 4,
                NumberUtils.max((short) 4, (short) -3, (short) 2));
        assertEquals((byte) -3,
                NumberUtils.min((byte) 4, (byte) -3, (byte) 2));
        assertEquals((byte) 4,
                NumberUtils.max((byte) 4, (byte) -3, (byte) 2));
        assertEquals(-3.0d, NumberUtils.min(4.0d, -3.0d, 2.0d), 0.0d);
        assertEquals(4.0d, NumberUtils.max(4.0d, -3.0d, 2.0d), 0.0d);
        assertEquals(-3.0f, NumberUtils.min(4.0f, -3.0f, 2.0f), 0.0f);
        assertEquals(4.0f, NumberUtils.max(4.0f, -3.0f, 2.0f), 0.0f);
    }

    @Test
    public void threeArgumentFloatingPointExtremaPropagateNaN() {
        assertTrue(Double.isNaN(NumberUtils.min(1.0d, Double.NaN, 2.0d)));
        assertTrue(Double.isNaN(NumberUtils.max(1.0d, Double.NaN, 2.0d)));
        assertTrue(Float.isNaN(NumberUtils.min(1.0f, Float.NaN, 2.0f)));
        assertTrue(Float.isNaN(NumberUtils.max(1.0f, Float.NaN, 2.0f)));
    }

    @Test
    public void isDigitsRecognizesDigitsOnly() {
        assertTrue(NumberUtils.isDigits("0123456789"));
        assertTrue(NumberUtils.isDigits("\uFF11\uFF12\uFF13"));
        assertFalse(NumberUtils.isDigits(null));
        assertFalse(NumberUtils.isDigits(""));
        assertFalse(NumberUtils.isDigits("12a"));
        assertFalse(NumberUtils.isDigits("12.0"));
    }

    @Test
    public void isNumberRecognizesSupportedNumberForms() {
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
    public void isNumberRejectsInvalidForms() {
        assertFalse(NumberUtils.isNumber(null));
        assertFalse(NumberUtils.isNumber(""));
        assertFalse(NumberUtils.isNumber("0x"));
        assertFalse(NumberUtils.isNumber("1..2"));
        assertFalse(NumberUtils.isNumber("1E"));
        assertFalse(NumberUtils.isNumber("1E-"));
        assertFalse(NumberUtils.isNumber("1.0L"));
        assertFalse(NumberUtils.isNumber("abc"));
    }

    @Test
    public void createNumberRejectsBlankAndUntrimmedValues() {
        assertNumberFormatException(new Runnable() {
            @Override
            public void run() {
                NumberUtils.createNumber(" ");
            }
        });
        assertNumberFormatException(new Runnable() {
            @Override
            public void run() {
                NumberUtils.createNumber(" 1");
            }
        });
        assertNumberFormatException(new Runnable() {
            @Override
            public void run() {
                NumberUtils.createNumber("1 ");
            }
        });
    }

    @Test
    public void createIntegerLongAndBigDecimalRejectInvalidValues() {
        assertNumberFormatException(new Runnable() {
            @Override
            public void run() {
                NumberUtils.createInteger("invalid");
            }
        });
        assertNumberFormatException(new Runnable() {
            @Override
            public void run() {
                NumberUtils.createLong("invalid");
            }
        });
        assertNumberFormatException(new Runnable() {
            @Override
            public void run() {
                NumberUtils.createBigDecimal("--1");
            }
        });
    }

    @Test
    public void arrayMethodsRejectNullAndEmptyArrays() {
        assertIllegalArgumentException(new Runnable() {
            @Override
            public void run() {
                NumberUtils.min((int[]) null);
            }
        });
        assertIllegalArgumentException(new Runnable() {
            @Override
            public void run() {
                NumberUtils.max(new int[0]);
            }
        });
        assertIllegalArgumentException(new Runnable() {
            @Override
            public void run() {
                NumberUtils.min((double[]) null);
            }
        });
        assertIllegalArgumentException(new Runnable() {
            @Override
            public void run() {
                NumberUtils.max(new float[0]);
            }
        });
    }

    @Test
    public void createNumberCoversExponentAndDecimalFallbacks() {
        Number exponent = NumberUtils.createNumber("1E3");
        Number underflow = NumberUtils.createNumber("1E-400");
        Number floatOverflow = NumberUtils.createNumber("1E309F");
        Number doubleOverflow = NumberUtils.createNumber("1E309D");

        assertEquals(Float.valueOf(1000.0f), exponent);
        assertEquals(new BigDecimal("1E-400"), underflow);
        assertEquals(new BigDecimal("1E309"), floatOverflow);
        assertEquals(new BigDecimal("1E309"), doubleOverflow);
    }

    @Test
    public void createNumberCoversTypeSuffixValidationBranches() {
        assertEquals(Long.valueOf(-12L), NumberUtils.createNumber("-12l"));
        assertEquals(Float.valueOf(1.5f), NumberUtils.createNumber("1.5f"));
        assertEquals(Double.valueOf(1.5d), NumberUtils.createNumber("1.5d"));

        assertNumberFormatException(new Runnable() {
            @Override
            public void run() {
                NumberUtils.createNumber("1.0L");
            }
        });
        assertNumberFormatException(new Runnable() {
            @Override
            public void run() {
                NumberUtils.createNumber("1E2L");
            }
        });
        assertNumberFormatException(new Runnable() {
            @Override
            public void run() {
                NumberUtils.createNumber("1Q");
            }
        });
    }

    @Test
    public void createNumberRejectsMalformedHexadecimalValues() {
        assertNumberFormatException(new Runnable() {
            @Override
            public void run() {
                NumberUtils.createNumber("0x");
            }
        });
        assertNumberFormatException(new Runnable() {
            @Override
            public void run() {
                NumberUtils.createNumber("0xG");
            }
        });
        assertNumberFormatException(new Runnable() {
            @Override
            public void run() {
                NumberUtils.createNumber("#");
            }
        });
    }

    @Test
    public void arrayExtremaHandleSingleElementsAndAllComparisonDirections() {
        assertEquals(7L, NumberUtils.min(new long[] {7L}));
        assertEquals(7L, NumberUtils.max(new long[] {7L}));
        assertEquals(7, NumberUtils.min(new int[] {7}));
        assertEquals(7, NumberUtils.max(new int[] {7}));

        assertEquals(-3, NumberUtils.min(new int[] {1, 2, -3}));
        assertEquals(3, NumberUtils.max(new int[] {-1, 2, 3}));
        assertEquals(-3.0d,
                NumberUtils.min(new double[] {1.0d, 2.0d, -3.0d}), 0.0d);
        assertEquals(3.0d,
                NumberUtils.max(new double[] {-1.0d, 2.0d, 3.0d}), 0.0d);
    }

    @Test
    public void threeArgumentExtremaTakeBothLaterArgumentsWhenNeeded() {
        assertEquals(3L, NumberUtils.min(5L, 4L, 3L));
        assertEquals(5L, NumberUtils.max(3L, 4L, 5L));
        assertEquals(3, NumberUtils.min(5, 4, 3));
        assertEquals(5, NumberUtils.max(3, 4, 5));
        assertEquals((short) 3,
                NumberUtils.min((short) 5, (short) 4, (short) 3));
        assertEquals((short) 5,
                NumberUtils.max((short) 3, (short) 4, (short) 5));
        assertEquals((byte) 3,
                NumberUtils.min((byte) 5, (byte) 4, (byte) 3));
        assertEquals((byte) 5,
                NumberUtils.max((byte) 3, (byte) 4, (byte) 5));
    }

    @Test
    public void floatingPointExtremaHandleNaNAsTheFirstElement() {
        assertTrue(Double.isNaN(NumberUtils.min(
                new double[] {Double.NaN, 1.0d})));
        assertTrue(Double.isNaN(NumberUtils.max(
                new double[] {Double.NaN, 1.0d})));
        assertTrue(Float.isNaN(NumberUtils.min(
                new float[] {Float.NaN, 1.0f})));
        assertTrue(Float.isNaN(NumberUtils.max(
                new float[] {Float.NaN, 1.0f})));
    }

    @Test
    public void isNumberCoversTrailingPointsSignsAndSuffixes() {
        assertTrue(NumberUtils.isNumber("1."));
        assertTrue(NumberUtils.isNumber("1D"));
        assertTrue(NumberUtils.isNumber("1f"));
        assertTrue(NumberUtils.isNumber("1e+2"));

        assertFalse(NumberUtils.isNumber("+"));
        assertFalse(NumberUtils.isNumber("-"));
        assertFalse(NumberUtils.isNumber("1e2.0"));
        assertFalse(NumberUtils.isNumber("1e2e3"));
        assertFalse(NumberUtils.isNumber("0xG"));
        assertFalse(NumberUtils.isNumber("0XFF"));
    }

    @Test
    public void createBigIntegerHandlesOctalAndNegativeDecimalValues() {
        assertEquals(new BigInteger("10", 8),
                NumberUtils.createBigInteger("012"));
        assertEquals(new BigInteger("-123"),
                NumberUtils.createBigInteger("-123"));
        assertEquals(new BigInteger("0"),
                NumberUtils.createBigInteger("0"));
    }

    private static void assertNumberFormatException(Runnable action) {
        try {
            action.run();
            fail("Expected NumberFormatException");
        } catch (NumberFormatException expected) {
            assertTrue(true);
        }
    }

    private static void assertIllegalArgumentException(Runnable action) {
        try {
            action.run();
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }
    }
}
