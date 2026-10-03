package org.apache.commons.math.util;

 import org.junit.Test;
 import static org.junit.Assert.*;

 public class MathUtilsTest {

     @Test
     public void testGcdPositive() {
         assertEquals(6, MathUtils.gcd(12, 18));
         assertEquals(12, MathUtils.gcd(12, 24));
         assertEquals(1, MathUtils.gcd(17, 13));
         assertEquals(5, MathUtils.gcd(15, 25));
     }

     @Test
     public void testGcdNegative() {
         assertEquals(6, MathUtils.gcd(-12, 18));
         assertEquals(6, MathUtils.gcd(12, -18));
         assertEquals(6, MathUtils.gcd(-12, -18));
     }

     @Test
     public void testGcdZero() {
         assertEquals(5, MathUtils.gcd(0, 5));
         assertEquals(5, MathUtils.gcd(5, 0));
         assertEquals(0, MathUtils.gcd(0, 0));
     }

     @Test
     public void testGcdOne() {
         assertEquals(1, MathUtils.gcd(1, 5));
         assertEquals(1, MathUtils.gcd(5, 1));
         assertEquals(1, MathUtils.gcd(1, 1));
     }

     @Test
     public void testGcdSame() {
         assertEquals(7, MathUtils.gcd(7, 7));
         assertEquals(42, MathUtils.gcd(42, 42));
     }

     @Test
     public void testGcdLarge() {
         assertEquals(1, MathUtils.gcd(Integer.MAX_VALUE, Integer.MAX_VALUE - 1));
         assertEquals(Integer.MAX_VALUE, MathUtils.gcd(Integer.MAX_VALUE, Integer.MAX_VALUE));
     }

     @Test
     public void testGcdBug238Trigger() {
         assertEquals(98304, MathUtils.gcd(491520, 688128));
     }

     @Test
     public void testGcdBug238Negatives() {
         assertEquals(98304, MathUtils.gcd(-491520, 688128));
         assertEquals(98304, MathUtils.gcd(491520, -688128));
         assertEquals(98304, MathUtils.gcd(-491520, -688128));
     }

     @Test
     public void testGcdCommutative() {
         assertEquals(MathUtils.gcd(30, 42), MathUtils.gcd(42, 30));
         assertEquals(MathUtils.gcd(491520, 688128), MathUtils.gcd(688128, 491520));
     }

     @Test
     public void testGcdPowersOfTwo() {
         assertEquals(256, MathUtils.gcd(65536, 768));
         assertEquals(1024, MathUtils.gcd(1024, 3072));
     }

     @Test
     public void testGcdMinValueOne() {
         assertEquals(1, MathUtils.gcd(Integer.MIN_VALUE, -1));
     }

     @Test
     public void testGcdMinValueZero() {
         int result = MathUtils.gcd(Integer.MIN_VALUE, 0);
         assertTrue("gcd(MIN_VALUE, 0) should be non-negative or MIN_VALUE",
                    result >= 0 || result == Integer.MIN_VALUE);
     }
 }