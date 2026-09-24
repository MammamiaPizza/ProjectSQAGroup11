ไฟล์นี้คือ **Prompt 02** ซึ่งต่างจากรอบแรกตรงที่ครั้งนี้ต้องสร้าง **Java Test Class ที่สมบูรณ์ 1 class**, ใช้ JUnit 4.12, ครอบคลุม normal/boundary/invalid/null/exceptional cases และเน้น LANG-747 โดยตรง 

ด้านล่างนี้คือผลลัพธ์ที่ฉันแนะนำให้ใช้เป็น `02_generated_test.txt` หรือเอาเฉพาะ Java class ไปสร้างไฟล์ `.java` ได้เลย

```java
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

    /*
     * LANG-747 regression test.
     * 0x80000000 has only eight hexadecimal digits, but its value is larger
     * than Integer.MAX_VALUE. It therefore must not be forced into Integer.
     */
    @Test
    public void testCreateNumberEightDigitHexTooLargeForInteger() {
        Number value = NumberUtils.createNumber("0x80000000");

        assertEquals(Long.class, value.getClass());
        assertEquals(2147483648L, value.longValue());
    }

    @Test
    public void testCreateNumberNegativeHexWithinIntegerRange() {
        Number value = NumberUtils.createNumber("-0x80000000");

        assertEquals(Integer.class, value.getClass());
        assertEquals(Integer.MIN_VALUE, value.intValue());
    }

    /*
     * Negative boundary corresponding to LANG-747.
     */
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
}
```

### สิ่งที่แต่ละกลุ่ม Test Case ทดสอบ

`toInt`, `toLong`, `toFloat`, `toDouble`, `toByte` และ `toShort` ทดสอบครบ 3 เส้นทางหลักของ conversion methods คือ input ปกติ, `null` และ input ที่ parse ไม่ได้ ซึ่ง implementation ระบุว่าจะคืนค่า default แทนการโยน exception

กลุ่ม `createNumber` เป็นส่วนสำคัญที่สุด เพราะ LANG-747 ระบุโดยตรงว่า `createNumber` ต้องรองรับ hexadecimal ที่มีค่าใหญ่เกิน `Integer` ไม่ใช่ตัดสินจากจำนวนหลักเพียงอย่างเดียว  โดย source ปัจจุบันเลือก `Integer` เมื่อมี hex digits ไม่เกิน 8 และเลือก `Long` เมื่อมากกว่า 8 หลัก  ดังนั้น `0x80000000` เป็น regression case สำคัญ เพราะมีเพียง 8 หลัก แต่มีค่า `2147483648` ซึ่งเกิน `Integer.MAX_VALUE`

`testCreateNumberHexWithinIntegerRange` ทดสอบค่าขอบบนที่ยังเป็น `Integer` ได้ คือ `0x7FFFFFFF` ส่วน `testCreateNumberEightDigitHexTooLargeForInteger` ทดสอบค่าถัดจากขอบเขต ซึ่งควรเลื่อนไปเป็น `Long` ตาม LANG-747 และ **test นี้มีโอกาส fail บน Lang-1b ซึ่งเป็นสิ่งที่ต้องการ** เพราะเรากำลังเขียน test เพื่อจับ bug ไม่ใช่เขียนให้ buggy version ผ่านทุก test

`testCreateNumberNegativeHexTooLargeForInteger` ใช้แนวคิดเดียวกันกับค่าติดลบ ส่วน 9-digit hex และ 17-digit hex ทดสอบเส้นทาง `Long` และ `BigInteger` ตาม contract ของ `createNumber` ที่ระบุว่าจะขยายชนิดตัวเลขเมื่อค่ามีขนาดใหญ่ขึ้น 

กลุ่ม `createFloat`, `createDouble`, `createInteger`, `createLong`, `createBigInteger` และ `createBigDecimal` ทดสอบ public factory methods โดยตรง รวมทั้ง `null`, hex/octal และ invalid blank input ตามพฤติกรรมที่ source ระบุ

กลุ่ม `min` และ `max` ทดสอบ overload ของ array ทุกชนิดที่มีใน class รวมทั้งเส้นทางที่ค่าต่ำสุด/สูงสุดเปลี่ยนระหว่าง loop และกรณี `NaN` สำหรับ `float`/`double` ส่วน null array และ empty array ทดสอบสอง branch ของ `validateArray` ซึ่ง contract ระบุให้โยน `IllegalArgumentException`

กลุ่ม three-value `min`/`max` เรียกทุก overload คือ `long`, `int`, `short`, `byte`, `double` และ `float` และใช้ลำดับค่าต่างกันเพื่อให้เงื่อนไขเปรียบเทียบได้ทั้งกรณี true และ false รวมถึง `NaN` สำหรับ floating-point

ท้ายสุด `isDigits` ทดสอบ digit, non-digit, empty และ null ส่วน `isNumber` ทดสอบ decimal, negative, decimal point, exponent, exponent sign, type qualifier, hexadecimal และ invalid branches เช่น `0x`, hexadecimal ที่มีอักษรผิด, exponent ที่ไม่มีตัวเลขต่อท้าย และ decimal point ซ้ำ

ชุดนี้ใช้เฉพาะ API ที่มีใน `NumberUtils.java`, package จริงคือ `org.apache.commons.lang3.math`, และใช้ JUnit 4 ตาม Project Context ที่กำหนดไว้ 
