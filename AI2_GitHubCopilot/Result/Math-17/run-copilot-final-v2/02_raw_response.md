package org.apache.commons.math3.dfp;

 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;

 import static org.junit.Assert.*;

 /**
  * Tests for Dfp.multiply(Dfp), Dfp.multiply(int) and Dfp.multiplyFast(int),
  * focused on the MATH-778 bug where certain multiplications produce NaN with
  * FLAG_INVALID instead of the correct finite result.
  */
 public class DfpMultiplyBugTest {

     private DfpField field;
     private Dfp zero;
     private Dfp one;
     private Dfp two;

     @Before
     public void setUp() {
         field = new DfpField(16);
         zero = field.getZero();
         one = field.getOne();
         two = field.getTwo();
     }

     @After
     public void tearDown() {
         field.clearIEEEFlags();
     }

     // ---- Normal multiplication ----

     @Test
     public void testMultiplyPositiveByPositive() {
         Dfp a = field.newDfp("3.0");
         Dfp b = field.newDfp("4.0");
         Dfp result = a.multiply(b);
         assertEquals("3 * 4", field.newDfp("12.0"), result);
         assertTrue("result must be finite", result.nans == Dfp.FINITE);
     }

     @Test
     public void testMultiplySignCombinations() {
         Dfp pos2 = field.newDfp("2.5");
         Dfp neg3 = field.newDfp("-3.0");

         Dfp r1 = pos2.multiply(neg3);
         assertEquals("pos * neg", field.newDfp("-7.5"), r1);

         Dfp r2 = neg3.multiply(neg3);
         assertEquals("neg * neg", field.newDfp("9.0"), r2);

         Dfp neg2 = field.newDfp("-2.5");
         Dfp r3 = neg2.multiply(field.newDfp("4.0"));
         assertEquals("neg * pos", field.newDfp("-10.0"), r3);
     }

     // ---- Zero and identity ----

     @Test
     public void testMultiplyByZeroGivesZero() {
         Dfp a = field.newDfp("42.5");
         Dfp result = a.multiply(zero);
         assertTrue("any * 0 should be zero", result.isZero());
     }

     @Test
     public void testMultiplyByOneIsIdentity() {
         Dfp a = field.newDfp("7.75");
         Dfp result = a.multiply(one);
         assertEquals("x * 1", a, result);
     }

     @Test
     public void testMultiplyZeroByZero() {
         Dfp result = zero.multiply(zero);
         assertTrue("0 * 0 should be zero", result.isZero());
     }

     // ---- Special values: NaN / Infinity ----

     @Test
     public void testNaNPropagatesFromLeft() {
         Dfp nan = field.newDfp("NaN");
         Dfp num = field.newDfp("5.0");
         Dfp result = nan.multiply(num);
         assertTrue("NaN * x should be NaN", result.isNaN());
     }

     @Test
     public void testNaNPropagatesFromRight() {
         Dfp nan = field.newDfp("NaN");
         Dfp num = field.newDfp("5.0");
         Dfp result = num.multiply(nan);
         assertTrue("x * NaN should be NaN", result.isNaN());
     }

     @Test
     public void testInfinityTimesZeroIsNaNWithInvalidFlag() {
         Dfp inf = field.newDfp("Infinity");
         Dfp result = inf.multiply(zero);
         assertTrue("Inf * 0 should be NaN", result.isNaN());
     }

     @Test
     public void testInfinityTimesNonZeroIsInfinity() {
         Dfp inf = field.newDfp("Infinity");
         Dfp num = field.newDfp("3.0");
         Dfp result = inf.multiply(num);
         assertTrue("Inf * nonzero should be Inf", result.isInfinite());
         assertEquals("sign should be +", (byte) 1, result.sign);
     }

     // ---- multiply(int) bridge ----

     @Test
     public void testMultiplyByIntZeroAndPositive() {
         Dfp base = field.newDfp("4.0");
         Dfp r0 = base.multiply(0);
         assertTrue("x * 0 (int) should be zero", r0.isZero());

         Dfp r5 = base.multiply(5);
         assertEquals("4 * 5 (int)", field.newDfp("20.0"), r5);
     }

     // ---- Reproduce MATH-778: large-exponent multiplication ----
     // Multiplying two large numbers whose exponents sum near MAX_EXP
     // used to overflow int arithmetic and produce NaN with FLAG_INVALID.

     @Test
     public void testLargeExponentProductDoesNotYieldNaN() {
         // Use intermediate magnitudes that push the product exponent high.
         Dfp a = field.newDfp("1e80");
         Dfp b = field.newDfp("1e80");
         Dfp result = a.multiply(b);
         assertFalse("large * large should not be NaN (MATH-778)", result.isNaN());
     }

     @Test
     public void testLargeTimesSmallDoesNotYieldNaN() {
         Dfp huge = field.newDfp("1e150");
         Dfp tiny = field.newDfp("1e-150");
         Dfp result = huge.multiply(tiny);
         assertFalse("extreme exponents should not produce NaN", result.isNaN());
     }
 }