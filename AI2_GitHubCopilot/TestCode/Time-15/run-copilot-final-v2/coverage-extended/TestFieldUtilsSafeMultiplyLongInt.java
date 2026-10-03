package org.joda.time.field;

 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for {@link FieldUtils#safeMultiply(long, int)}.
  *
  * The bug (issue 147) is that safeMultiply(long, int) does not correctly
  * detect overflow in all cases. Specifically, the overflow check
  * "total / val2 != val1" can fail when val2 is a divisor of the overflowed
  * product (e.g. Long.MIN_VALUE * -1 wraps to Long.MIN_VALUE, and Long.MIN_VALUE / -1 ==
Long.MIN_VALUE).
  * The Javadoc contract requires that any multiplication whose mathematical
  * product exceeds the range [Long.MIN_VALUE, Long.MAX_VALUE] must throw
  * an ArithmeticException.
  */
 public class TestFieldUtilsSafeMultiplyLongInt {

     // -----------------------------------------------------------------------
     // Normal cases: exact products within long range
     // -----------------------------------------------------------------------

     @Test
     public void testSmallPositiveValues() {
         assertEquals(0L, FieldUtils.safeMultiply(0L, 5));
         assertEquals(0L, FieldUtils.safeMultiply(5L, 0));
         assertEquals(42L, FieldUtils.safeMultiply(6L, 7));
         assertEquals(100L, FieldUtils.safeMultiply(10L, 10));
         assertEquals(100L, FieldUtils.safeMultiply(25L, 4));
     }

     @Test
     public void testSmallNegativeValues() {
         assertEquals(-42L, FieldUtils.safeMultiply(6L, -7));
         assertEquals(-42L, FieldUtils.safeMultiply(-6L, 7));
         assertEquals(42L, FieldUtils.safeMultiply(-6L, -7));
     }

     @Test
     public void testIdentityAndZero() {
         assertEquals(99L, FieldUtils.safeMultiply(99L, 1));
         assertEquals(0L, FieldUtils.safeMultiply(99L, 0));
         assertEquals(-99L, FieldUtils.safeMultiply(99L, -1));
     }

     // -----------------------------------------------------------------------
     // Boundary cases: near Long.MAX_VALUE and Long.MIN_VALUE
     // -----------------------------------------------------------------------

     @Test
     public void testBoundaryNearMaxValue() {
         // Long.MAX_VALUE * 1 = Long.MAX_VALUE (exact, no overflow)
         assertEquals(Long.MAX_VALUE, FieldUtils.safeMultiply(Long.MAX_VALUE, 1));

         // Long.MAX_VALUE * 2 overflows
         try {
             FieldUtils.safeMultiply(Long.MAX_VALUE, 2);
             fail("Expected ArithmeticException for Long.MAX_VALUE * 2");
         } catch (ArithmeticException expected) {
         }

         // Long.MAX_VALUE * -1 = -Long.MAX_VALUE (exact)
         assertEquals(-Long.MAX_VALUE, FieldUtils.safeMultiply(Long.MAX_VALUE, -1));

         // Long.MAX_VALUE * -2 overflows (product < Long.MIN_VALUE)
         try {
             FieldUtils.safeMultiply(Long.MAX_VALUE, -2);
             fail("Expected ArithmeticException for Long.MAX_VALUE * -2");
         } catch (ArithmeticException expected) {
         }
     }

     @Test
     public void testBoundaryNearMinValue() {
         // Long.MIN_VALUE * 1 = Long.MIN_VALUE (exact)
         assertEquals(Long.MIN_VALUE, FieldUtils.safeMultiply(Long.MIN_VALUE, 1));

         // Long.MIN_VALUE * 2 overflows (product < Long.MIN_VALUE)
         try {
             FieldUtils.safeMultiply(Long.MIN_VALUE, 2);
             fail("Expected ArithmeticException for Long.MIN_VALUE * 2");
         } catch (ArithmeticException expected) {
         }

         // Long.MIN_VALUE * -1 overflows (product = -Long.MIN_VALUE > Long.MAX_VALUE)
         // THIS IS THE BUG: safeMultiply(Long.MIN_VALUE, -1) should throw but the
         // original code does not detect this overflow because
         // Long.MIN_VALUE * -1 == Long.MIN_VALUE (wraps around) and
         // Long.MIN_VALUE / -1 == Long.MIN_VALUE, so total / val2 == val1.
         try {
             FieldUtils.safeMultiply(Long.MIN_VALUE, -1);
             fail("Expected ArithmeticException for Long.MIN_VALUE * -1");
         } catch (ArithmeticException expected) {
         }

         // Long.MIN_VALUE * -2 overflows
         try {
             FieldUtils.safeMultiply(Long.MIN_VALUE, -2);
             fail("Expected ArithmeticException for Long.MIN_VALUE * -2");
         } catch (ArithmeticException expected) {
         }
     }

     // -----------------------------------------------------------------------
     // Overflow cases with larger multipliers
     // -----------------------------------------------------------------------

     @Test
     public void testOverflowWithLargeMultiplier() {
         // Long.MAX_VALUE / 2 + 1 = 4611686018427387904
         // Multiplying by 2 gives Long.MAX_VALUE + 2 which overflows
         long halfMax = Long.MAX_VALUE / 2;
         try {
             FieldUtils.safeMultiply(halfMax + 1, 2);
             fail("Expected ArithmeticException for (" + (halfMax + 1) + "L) * 2");
         } catch (ArithmeticException expected) {
         }

         // Long.MAX_VALUE / 3 + 1, multiplied by 3 overflows
         long thirdMax = Long.MAX_VALUE / 3;
         try {
             FieldUtils.safeMultiply(thirdMax + 1, 3);
             fail("Expected ArithmeticException for (" + (thirdMax + 1) + "L) * 3");
         } catch (ArithmeticException expected) {
         }
     }

     @Test
     public void testOverflowNegativeDirection() {
         // -Long.MAX_VALUE / 2 - 1 = -4611686018427387905
         // Multiplying by 2 gives -Long.MAX_VALUE - 2 which underflows
         long halfMin = Long.MIN_VALUE / 2;
         try {
             FieldUtils.safeMultiply(halfMin - 1, 2);
             fail("Expected ArithmeticException for negative overflow");
         } catch (ArithmeticException expected) {
         }
     }

     // -----------------------------------------------------------------------
     // Edge cases with extreme int values
     // -----------------------------------------------------------------------

     @Test
     public void testEdgeWithIntegerMaxValue() {
         // Long.MAX_VALUE * Integer.MAX_VALUE overflows
         try {
             FieldUtils.safeMultiply(Long.MAX_VALUE, Integer.MAX_VALUE);
             fail("Expected ArithmeticException for Long.MAX_VALUE * Integer.MAX_VALUE");
         } catch (ArithmeticException expected) {
         }

         // 2 * Integer.MAX_VALUE fits in long
         assertEquals(2L * Integer.MAX_VALUE, FieldUtils.safeMultiply(2L, Integer.MAX_VALUE));

         // -2 * Integer.MAX_VALUE fits in long
         assertEquals(-2L * Integer.MAX_VALUE, FieldUtils.safeMultiply(-2L, Integer.MAX_VALUE));
     }

     @Test
     public void testEdgeWithIntegerMinValue() {
         // Long.MAX_VALUE * Integer.MIN_VALUE underflows
         try {
             FieldUtils.safeMultiply(Long.MAX_VALUE, Integer.MIN_VALUE);
             fail("Expected ArithmeticException for Long.MAX_VALUE * Integer.MIN_VALUE");
         } catch (ArithmeticException expected) {
         }

         // 2 * Integer.MIN_VALUE fits in long
         assertEquals(2L * Integer.MIN_VALUE, FieldUtils.safeMultiply(2L, Integer.MIN_VALUE));

         // -2 * Integer.MIN_VALUE fits in long
         assertEquals(4294967296L, FieldUtils.safeMultiply(-2L, Integer.MIN_VALUE));
     }

     // -----------------------------------------------------------------------
     // Cases exercising the switch shortcut paths in the implementation
     // -----------------------------------------------------------------------

     @Test
     public void testSwitchShortcutPaths() {
         // case -1: should negate (and detect overflow for Long.MIN_VALUE)
         assertEquals(-12345L, FieldUtils.safeMultiply(12345L, -1));
         assertEquals(0L, FieldUtils.safeMultiply(0L, -1));

         // case 0: should return 0
         assertEquals(0L, FieldUtils.safeMultiply(Long.MAX_VALUE, 0));
         assertEquals(0L, FieldUtils.safeMultiply(Long.MIN_VALUE, 0));

         // case 1: should return val1
         assertEquals(Long.MAX_VALUE, FieldUtils.safeMultiply(Long.MAX_VALUE, 1));
         assertEquals(Long.MIN_VALUE, FieldUtils.safeMultiply(Long.MIN_VALUE, 1));
     }

     // -----------------------------------------------------------------------
     // Overflow when product wraps to a value that passes the division check
     // -----------------------------------------------------------------------

     @Test
     public void testOverflowWhereDivisionCheckPasses() {
         // Long.MIN_VALUE / 2 * 2 = Long.MIN_VALUE (exact, no overflow)
         assertEquals(Long.MIN_VALUE, FieldUtils.safeMultiply(Long.MIN_VALUE / 2, 2));

         // (Long.MIN_VALUE / 2 + 1) * 2 = Long.MIN_VALUE + 2 (exact, no overflow)
         long halfMinPlusOne = Long.MIN_VALUE / 2 + 1;
         assertEquals(Long.MIN_VALUE + 2, FieldUtils.safeMultiply(halfMinPlusOne, 2));
     }

@Test
public void testSafeAddOverflows() {
    try {
        FieldUtils.safeAdd(Integer.MAX_VALUE, 1);
        fail("Expected ArithmeticException for int overflow");
    } catch (ArithmeticException e) {
    }
    try {
        FieldUtils.safeAdd(Integer.MIN_VALUE, -1);
        fail("Expected ArithmeticException for int overflow");
    } catch (ArithmeticException e) {
    }
    try {
        FieldUtils.safeAdd(Long.MAX_VALUE, 1L);
        fail("Expected ArithmeticException for long overflow");
    } catch (ArithmeticException e) {
    }
    try {
        FieldUtils.safeAdd(Long.MIN_VALUE, -1L);
        fail("Expected ArithmeticException for long overflow");
    } catch (ArithmeticException e) {
    }
    assertEquals(0, FieldUtils.safeAdd(0, 0));
    assertEquals(100, FieldUtils.safeAdd(50, 50));
    assertEquals(Long.MAX_VALUE - 1, FieldUtils.safeAdd(Long.MAX_VALUE - 1, 0L));
}

@Test
public void testSafeMultiplyToInt() {
    assertEquals(200, FieldUtils.safeMultiplyToInt(10L, 20L));
    assertEquals(0, FieldUtils.safeMultiplyToInt(0L, 999L));
    assertEquals(-10, FieldUtils.safeMultiplyToInt(1L, -10L));
    assertEquals(Integer.MAX_VALUE, FieldUtils.safeMultiplyToInt(Integer.MAX_VALUE, 1L));
    assertEquals(Integer.MIN_VALUE, FieldUtils.safeMultiplyToInt(Integer.MIN_VALUE, 1L));
    try {
        FieldUtils.safeMultiplyToInt((long) Integer.MAX_VALUE + 1, 2L);
        fail("Expected ArithmeticException for overflow");
    } catch (ArithmeticException e) {
    }
    try {
        FieldUtils.safeMultiplyToInt((long) Integer.MIN_VALUE, 2L);
        fail("Expected ArithmeticException for overflow");
    } catch (ArithmeticException e) {
    }
}

@Test
public void testGetWrappedValue() {
    assertEquals(0, FieldUtils.getWrappedValue(6, 0, 5));
    assertEquals(1, FieldUtils.getWrappedValue(7, 0, 5));
    assertEquals(0, FieldUtils.getWrappedValue(-6, 0, 5));
    assertEquals(4, FieldUtils.getWrappedValue(-2, 0, 5));
    assertEquals(3, FieldUtils.getWrappedValue(3, 0, 5));
    assertEquals(0, FieldUtils.getWrappedValue(0, 0, 5));
    assertEquals(5, FieldUtils.getWrappedValue(5, 0, 5));
}

@Test
public void testEquals() {
    assertTrue(FieldUtils.equals(null, null));
    Object obj = new Integer(5);
    assertTrue(FieldUtils.equals(obj, obj));
    assertFalse(FieldUtils.equals(obj, null));
    assertFalse(FieldUtils.equals(null, obj));
    assertTrue(FieldUtils.equals(new Integer(5), new Integer(5)));
    assertFalse(FieldUtils.equals(new Integer(5), new Integer(6)));
}
}
