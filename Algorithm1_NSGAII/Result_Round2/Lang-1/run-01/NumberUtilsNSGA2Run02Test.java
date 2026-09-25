package org.apache.commons.lang3.math;
import java.math.BigInteger;
import java.math.BigDecimal;
import org.junit.Test;
import static org.junit.Assert.*;
public class NumberUtilsNSGA2Run02Test {
    @Test public void case_02_hex_hash() { assertEquals(2147483648L, NumberUtils.createNumber("#80000000").longValue()); }
    @Test public void case_05_decimal() { assertEquals(123, NumberUtils.createNumber("123").intValue()); }
    @Test public void case_11_to_long() { assertEquals(12345678901L, NumberUtils.toLong("12345678901")); assertEquals(7L, NumberUtils.toLong(null, 7L)); }
    @Test public void case_12_to_short() { assertEquals((short) 12, NumberUtils.toShort("12")); assertEquals((short) 3, NumberUtils.toShort("bad", (short) 3)); }
    @Test public void case_14_to_float() { assertEquals(1.5f, NumberUtils.toFloat("1.5"), 0.0f); assertEquals(2.0f, NumberUtils.toFloat(null, 2.0f), 0.0f); }
    @Test public void case_18_create_big_integer() { assertEquals(new BigInteger("15"), NumberUtils.createBigInteger("15")); }
    @Test public void case_19_create_big_decimal() { assertEquals(new BigDecimal("1.25"), NumberUtils.createBigDecimal("1.25")); }
    @Test public void case_20_create_float() { assertEquals(Float.valueOf(1.25f), NumberUtils.createFloat("1.25")); }
    @Test public void case_23_is_number() { assertTrue(NumberUtils.isNumber("123")); assertFalse(NumberUtils.isNumber("abc")); }
    @Test public void case_27_max_long_array() { assertEquals(7L, NumberUtils.max(new long[] {4L, -3L, 7L})); }
    @Test public void case_29_max_double_array() { assertEquals(7.0d, NumberUtils.max(new double[] {4.0d, -3.0d, 7.0d}), 0.0d); }
    @Test public void case_30_min_three() { assertEquals(-3, NumberUtils.min(4, -3, 7)); }
}
