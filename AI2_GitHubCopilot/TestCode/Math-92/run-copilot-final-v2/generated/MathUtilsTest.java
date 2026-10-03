package org.apache.commons.math.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;

import org.junit.Test;

public class MathUtilsTest {

 private static BigInteger binomialCoefficientBigInteger(int n, int k) {
     if (k < 0 || k > n) {
         throw new IllegalArgumentException("0 <= k <= n required, got n=" + n + " k=" + k);
     }
     // Take advantage of symmetry
     if (k > n / 2) {
         k = n - k;
     }
     BigInteger result = BigInteger.ONE;
     for (int i = 1; i <= k; i++) {
         result = result.multiply(BigInteger.valueOf(n - k + i));
         result = result.divide(BigInteger.valueOf(i));
     }
     return result;
 }

 // Known failing case from MATH-241
 @Test
 public void testBinomialCoefficient48_22() {
     assertEquals(27385657281648L, MathUtils.binomialCoefficient(48, 22));
 }

 // Basic identities: n=0,k=0; n,k=0; n,k=1; n,k=n; n,k=n-1
 @Test
 public void testBinomialCoefficientBasicIdentities() {
     assertEquals(1L, MathUtils.binomialCoefficient(0, 0));
     assertEquals(1L, MathUtils.binomialCoefficient(10, 0));
     assertEquals(10L, MathUtils.binomialCoefficient(10, 1));
     assertEquals(1L, MathUtils.binomialCoefficient(10, 10));
     assertEquals(10L, MathUtils.binomialCoefficient(10, 9));
     assertEquals(66L, MathUtils.binomialCoefficient(66, 1));
     assertEquals(1L, MathUtils.binomialCoefficient(66, 0));
     assertEquals(1L, MathUtils.binomialCoefficient(66, 66));
     assertEquals(66L, MathUtils.binomialCoefficient(66, 65));
 }

 @Test
 public void testBinomialCoefficientSymmetry() {
     assertEquals(MathUtils.binomialCoefficient(48, 22),
             MathUtils.binomialCoefficient(48, 26));
     assertEquals(MathUtils.binomialCoefficient(20, 7),
             MathUtils.binomialCoefficient(20, 13));
     assertEquals(MathUtils.binomialCoefficient(66, 30),
             MathUtils.binomialCoefficient(66, 36));
 }

 @Test
 public void testBinomialCoefficientExactWithBigInteger() {
     // test several pairs against exact BigInteger calculation
     assertEquals(
             binomialCoefficientBigInteger(48, 22).longValue(),
             MathUtils.binomialCoefficient(48, 22));
     assertEquals(
             binomialCoefficientBigInteger(49, 22).longValue(),
             MathUtils.binomialCoefficient(49, 22));
     assertEquals(
             binomialCoefficientBigInteger(50, 25).longValue(),
             MathUtils.binomialCoefficient(50, 25));
     assertEquals(
             binomialCoefficientBigInteger(66, 33).longValue(),
             MathUtils.binomialCoefficient(66, 33));
     assertEquals(
             binomialCoefficientBigInteger(30, 15).longValue(),
             MathUtils.binomialCoefficient(30, 15));
     assertEquals(
             binomialCoefficientBigInteger(61, 30).longValue(),
             MathUtils.binomialCoefficient(61, 30));
 }

 @Test(expected = IllegalArgumentException.class)
 public void testBinomialCoefficientNegativeN() {
     MathUtils.binomialCoefficient(-1, 0);
 }

 @Test(expected = IllegalArgumentException.class)
 public void testBinomialCoefficientKGreaterThanN() {
     MathUtils.binomialCoefficient(2, 5);
 }

 @Test(expected = IllegalArgumentException.class)
 public void testBinomialCoefficientNegativeK() {
     // Under correct contract k must be >= 0; the buggy version might not enforce it
     MathUtils.binomialCoefficient(5, -1);
 }

 @Test
 public void testBinomialCoefficientDoubleApproximate() {
     // binomialCoefficientDouble should be close to exact value for cases not
     // relying on the rounding improvement after the fix
     double exact = binomialCoefficientBigInteger(48, 22).doubleValue();
     double approx = MathUtils.binomialCoefficientDouble(48, 22);
     // allow a small relative error (the known bug causes off-by-one in the
     // rounded long but double may also be slightly off; we just check it's in the
     // right ballpark)
     assertEquals(exact, approx, exact * 1e-12);
 }

 @Test
 public void testBinomialCoefficientLog() {
     // log should be close to Math.log of exact value
     double exactLog = Math.log(binomialCoefficientBigInteger(48, 22).doubleValue());
     double computedLog = MathUtils.binomialCoefficientLog(48, 22);
     assertEquals(exactLog, computedLog, 1e-9);
 }

 @Test
 public void testBinomialCoefficientOverflowThrows() {
     // n large enough so that result exceeds Long.MAX_VALUE
     // C(1000, 10) > 2.6e23 > Long.MAX_VALUE
     try {
         MathUtils.binomialCoefficient(1000, 10);
         fail("ArithmeticException expected for overflow");
     } catch (ArithmeticException e) {
         assertTrue(e.getMessage().contains("too large"));
     }
 }

 @Test
 public void testBinomialCoefficientLargeButFits() {
     // C(66, 33) fits in a long, must not throw
     BigInteger exact = binomialCoefficientBigInteger(66, 33);
     long result = MathUtils.binomialCoefficient(66, 33);
     assertEquals(exact.longValue(), result);
 }

}
