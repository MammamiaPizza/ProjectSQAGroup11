package org.apache.commons.math.util;

 import static org.junit.Assert.*;
 import org.junit.Test;

 public class MathUtilsTest {

     // Exact long factorial values for n = 0..20
     private static final long[] FACTORIALS = {
         1L,                     // 0!
         1L,                     // 1!
         2L,                     // 2!
         6L,                     // 3!
         24L,                    // 4!
         120L,                   // 5!
         720L,                   // 6!
         5040L,                  // 7!
         40320L,                 // 8!
         362880L,                // 9!
         3628800L,               // 10!
         39916800L,              // 11!
         479001600L,             // 12!
         6227020800L,            // 13!
         87178291200L,           // 14!
         1307674368000L,         // 15!
         20922789888000L,        // 16!
         355687428096000L,       // 17!
         6402373705728000L,      // 18!
         121645100408832000L,    // 19!
         2432902008176640000L    // 20!
     };

     @Test
     public void testFactorialDoubleSmallExact() {
         // For n <= 20, factorialDouble must return exact double representation of the long
factorial
         for (int i = 0; i <= 20; i++) {
             double expected = (double) FACTORIALS[i];
             double actual = MathUtils.factorialDouble(i);
             assertEquals("factorialDouble(" + i + ")", expected, actual, 0.0);
         }
     }

     @Test(expected = IllegalArgumentException.class)
     public void testFactorialDoubleNegativeThrows() {
         MathUtils.factorialDouble(-1);
     }

     @Test
     public void testFactorialDoubleLargeFinitePositive() {
         // For n > 20, the return value must be finite and positive (no overflow to infinity)
         int[] testValues = {21, 30, 50, 100};
         for (int n : testValues) {
             double val = MathUtils.factorialDouble(n);
             assertFalse("factorialDouble(" + n + ") should be finite",
                         Double.isNaN(val) || Double.isInfinite(val));
             assertTrue("factorialDouble(" + n + ") should be positive", val > 0);
         }
     }

     @Test
     public void testFactorialDoubleMonotonicLarge() {
         // factorialDouble should increase (or at least not decrease) for larger n
         double prev = MathUtils.factorialDouble(20);
         for (int n = 21; n <= 30; n++) {
             double curr = MathUtils.factorialDouble(n);
             assertTrue("factorialDouble(" + n + ") should be >= factorialDouble(" + (n-1) + ")",
                        curr > prev);
             prev = curr;
         }
     }

     @Test
     public void testFactorialSmallExact() {
         // factorial(n) for n=0..20 must return the exact long factorial
         for (int i = 0; i <= 20; i++) {
             assertEquals("factorial(" + i + ")", FACTORIALS[i], MathUtils.factorial(i));
         }
     }

     @Test(expected = ArithmeticException.class)
     public void testFactorialOverflow() {
         // 21! exceeds Long.MAX_VALUE and must throw
         MathUtils.factorial(21);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testFactorialNegativeThrows() {
         MathUtils.factorial(-1);
     }

     @Test
     public void testFactorialDoubleExactBoundary0And1() {
         // Edge values 0 and 1 are common sources of off-by-one errors
         assertEquals(1.0, MathUtils.factorialDouble(0), 0.0);
         assertEquals(1.0, MathUtils.factorialDouble(1), 0.0);
     }

     @Test
     public void testFactorialDoubleUplsRegression17() {
         // Assert 17! does not suffer from the reported 1‑ULP rounding error
         assertEquals((double) FACTORIALS[17], MathUtils.factorialDouble(17), 0.0);
     }
 }
