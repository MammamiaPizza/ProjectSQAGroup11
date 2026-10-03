package org.apache.commons.math.util;

 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for {@link MathUtils#gcd(int, int)} and {@link MathUtils#lcm(int, int)}
  * focusing on overflow detection (MATH-243).
  */
 public class MathUtilsTest {

     // ------------------- gcd tests -------------------

     @Test
     public void testGcdBasic() {
         assertEquals(0, MathUtils.gcd(0, 0));
         assertEquals(7, MathUtils.gcd(7, 0));
         assertEquals(7, MathUtils.gcd(0, 7));
         assertEquals(6, MathUtils.gcd(18, 12));
         assertEquals(6, MathUtils.gcd(12, 18));
         assertEquals(1, MathUtils.gcd(7, 13));
         assertEquals(30, MathUtils.gcd(30, 30));
     }

     @Test
     public void testGcdNegative() {
         assertEquals(6, MathUtils.gcd(-18, 12));
         assertEquals(6, MathUtils.gcd(18, -12));
         assertEquals(6, MathUtils.gcd(-18, -12));
         assertEquals(1, MathUtils.gcd(-7, 13));
         assertEquals(1, MathUtils.gcd(7, -13));
         assertEquals(1, MathUtils.gcd(-7, -13));
     }

     @Test
     public void testGcdLargeCoprime() {
         assertEquals(1, MathUtils.gcd(Integer.MAX_VALUE, Integer.MAX_VALUE - 1));
         assertEquals(1, MathUtils.gcd(Integer.MAX_VALUE - 1, Integer.MAX_VALUE));
     }

     @Test(expected = ArithmeticException.class)
     public void testGcdMinValueAndZero() {
         // Math.abs(Integer.MIN_VALUE) == Integer.MIN_VALUE; gcd must throw
         MathUtils.gcd(Integer.MIN_VALUE, 0);
     }

     @Test(expected = ArithmeticException.class)
     public void testGcdZeroAndMinValue() {
         MathUtils.gcd(0, Integer.MIN_VALUE);
     }

     @Test(expected = ArithmeticException.class)
     public void testGcdBothMinValue() {
         MathUtils.gcd(Integer.MIN_VALUE, Integer.MIN_VALUE);
     }

     @Test(expected = ArithmeticException.class)
     public void testGcdMinValueAndOne() {
         // negating MIN_VALUE in the algorithm overflows
         MathUtils.gcd(Integer.MIN_VALUE, 1);
     }

     @Test(expected = ArithmeticException.class)
     public void testGcdMinValueAndNegative() {
         MathUtils.gcd(Integer.MIN_VALUE, -1);
     }

     // ------------------- lcm tests -------------------

     @Test
     public void testLcmBasic() {
         assertEquals(36, MathUtils.lcm(18, 12));
         assertEquals(36, MathUtils.lcm(12, 18));
         assertEquals(91, MathUtils.lcm(7, 13));
         assertEquals(30, MathUtils.lcm(30, 30));
     }

     @Test
     public void testLcmZero() {
         assertEquals(0, MathUtils.lcm(0, 0));
         assertEquals(0, MathUtils.lcm(0, 5));
         assertEquals(0, MathUtils.lcm(5, 0));
         assertEquals(0, MathUtils.lcm(0, Integer.MAX_VALUE));
     }

     @Test
     public void testLcmNegative() {
         assertEquals(36, MathUtils.lcm(-18, 12));
         assertEquals(36, MathUtils.lcm(18, -12));
         assertEquals(36, MathUtils.lcm(-18, -12));
     }

     @Test(expected = ArithmeticException.class)
     public void testLcmMinValueAndOne() {
         // lcm(MIN_VALUE, 1): gcd=1, a/gcd=MIN_VALUE, mulAndCheck(MIN_VALUE,1)=MIN_VALUE,
         // then Math.abs(MIN_VALUE)=MIN_VALUE (negative) which is invalid
         MathUtils.lcm(Integer.MIN_VALUE, 1);
     }

     @Test(expected = ArithmeticException.class)
     public void testLcmOneAndMinValue() {
         MathUtils.lcm(1, Integer.MIN_VALUE);
     }

     @Test(expected = ArithmeticException.class)
     public void testLcmMinValueAndMinValue() {
         MathUtils.lcm(Integer.MIN_VALUE, Integer.MIN_VALUE);
     }

     @Test(expected = ArithmeticException.class)
     public void testLcmProductOverflow() {
         // 46341 * 46342 > Integer.MAX_VALUE, gcd is 1,
         // product overflows int, mulAndCheck must throw ArithmeticException
         MathUtils.lcm(46341, 46342);
     }

     @Test(expected = ArithmeticException.class)
     public void testLcmProductOverflowLarge() {
         // 1073741824 * 2 = 2147483648 > Integer.MAX_VALUE, lcm overflows
         MathUtils.lcm(1073741824, 2);
     }
 }