package org.apache.commons.math.fraction;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;

 import org.junit.Test;

 /**
  * Tests {@link Fraction#compareTo(Fraction)} for correctness.
  * <p>
  * The known bug (MATH-252) is that {@code compareTo} uses
  * {@link Fraction#doubleValue()}, which can map distinct fractions to the same
  * {@code double} representation.  The correct implementation should use
  * cross-multiplication (with overflow protection).
  * </p>
  */
 public class FractionTest {

     @Test
     public void testCompareToEqualFractions() {
         // Fractions that reduce to the same value must return 0.
         assertEquals("1/2 vs 2/4", 0, new Fraction(1, 2).compareTo(new Fraction(2, 4)));
         assertEquals("6/8 vs 3/4", 0, new Fraction(6, 8).compareTo(new Fraction(3, 4)));
         assertEquals("0/5 vs 0/1", 0, new Fraction(0, 5).compareTo(new Fraction(0, 1)));
         assertEquals("self comparison", 0, new Fraction(3, 4).compareTo(new Fraction(3, 4)));
     }

     @Test
     public void testCompareToLessThan() {
         Fraction third    = new Fraction(1, 3);
         Fraction half     = new Fraction(1, 2);
         Fraction negHalf  = new Fraction(-1, 2);
         Fraction posThird = new Fraction(1, 3);
         Fraction zero     = Fraction.ZERO;

         assertTrue("1/3 < 1/2",   third.compareTo(half) < 0);
         assertTrue("-1/2 < 1/3",  negHalf.compareTo(posThird) < 0);
         assertTrue("0 < 1/2",     zero.compareTo(half) < 0);
         assertTrue("-1/3 < 0",    new Fraction(-1, 3).compareTo(zero) < 0);
         assertTrue("-1/2 < -1/3", negHalf.compareTo(new Fraction(-1, 3)) < 0);
     }

     @Test
     public void testCompareToGreaterThan() {
         Fraction half    = new Fraction(1, 2);
         Fraction third   = new Fraction(1, 3);
         Fraction one     = Fraction.ONE;
         Fraction negHalf = new Fraction(-1, 2);
         Fraction negOne  = Fraction.MINUS_ONE;

         assertTrue("1/2 > 1/3", half.compareTo(third) > 0);
         assertTrue("1 > 1/2",   one.compareTo(half) > 0);
         assertTrue("-1/2 > -1", negHalf.compareTo(negOne) > 0);
         assertTrue("1/3 > 0",   third.compareTo(Fraction.ZERO) > 0);
     }

     @Test
     public void testCompareToSymmetry() {
         Fraction a = new Fraction(3, 7);
         Fraction b = new Fraction(2, 5);
         int ab = a.compareTo(b);
         int ba = b.compareTo(a);
         if (ab == 0) {
             assertEquals("symmetry when equal", 0, ba);
         } else {
             assertTrue("opposite signs", Integer.signum(ab) == -Integer.signum(ba));
         }
     }

     @Test
     public void testCompareToConsistentWithEquals() {
         Fraction f1 = new Fraction(1, 2);
         Fraction f2 = new Fraction(2, 4);
         assertTrue("equals must be true for reduced equivalents", f1.equals(f2));
         assertEquals("compareTo must be 0 when equals returns true", 0, f1.compareTo(f2));

         Fraction f3 = new Fraction(-3, 4);
         Fraction f4 = new Fraction(3, -4); // constructor normalises sign
         assertTrue("f3 equals f4 after normalisation", f3.equals(f4));
         assertEquals("compareTo == 0 for sign-normalised equal fractions", 0, f3.compareTo(f4));
     }

     @Test(expected = NullPointerException.class)
     public void testCompareToNull() {
         new Fraction(1, 2).compareTo(null);
     }

     @Test
     public void testCompareToLargeCloseFractionsBug() {
         // Two distinct fractions whose doubleValue() result is identical
         // because the difference is far smaller than the double precision at
         // that magnitude.  compareTo must return < 0, not 0.
         Fraction a = new Fraction(Integer.MAX_VALUE,     Integer.MAX_VALUE - 1);
         Fraction b = new Fraction(Integer.MAX_VALUE - 1, Integer.MAX_VALUE - 2);

         int result = a.compareTo(b);
         assertTrue("Large close fractions must not be considered equal", result < 0);
         assertTrue("Reverse comparison must be > 0", b.compareTo(a) > 0);
     }

     @Test
     public void testCompareToWithReducedFraction() {
         // getReducedFraction yields the same reduced fraction.
         Fraction f1 = Fraction.getReducedFraction(6, 8);
         Fraction f2 = new Fraction(3, 4);
         assertEquals("getReducedFraction should be equal to reduced form", 0, f1.compareTo(f2));

         Fraction f3 = Fraction.getReducedFraction(-2, 4);
         Fraction f4 = new Fraction(-1, 2);
         assertEquals("negative reduced fraction", 0, f3.compareTo(f4));
     }

     @Test
     public void testCompareToFractionFromDouble() throws FractionConversionException {
         // Fractions constructed from double values should compare correctly
         // against explicitly constructed equivalent fractions.
         Fraction half = new Fraction(0.5);
         assertEquals("0.5 should yield 1/2", 0, half.compareTo(new Fraction(1, 2)));

         Fraction approxThird = new Fraction(1.0 / 3.0);
         Fraction third = new Fraction(1, 3);
         // close but possibly not identical -- must not throw and must be comparable
         int cmp = approxThird.compareTo(third);
         assertTrue("Should return -1, 0, or +1", cmp >= -1 && cmp <= 1);
     }

     @Test
     public void testCompareToNegativeDenominatorFractions() {
         // Creation with negative denominators is normalised by the constructor.
         Fraction a = new Fraction(1, -3);
         Fraction b = new Fraction(-1, 3);
         assertEquals("sign-normalised fractions are equal", 0, a.compareTo(b));

         Fraction c = new Fraction(2, -5);
         Fraction d = new Fraction(-2, 5);
         assertEquals("compareTo consistent with normalisation", 0, c.compareTo(d));

         assertTrue("negative fraction less than positive",
                    c.compareTo(new Fraction(1, 2)) < 0);
     }

     @Test
     public void testCompareToLargeDistinctFractions() {
         // Clearly different large fractions; double should still work here,
         // but we verify correct ordering.
         Fraction huge = new Fraction(Integer.MAX_VALUE, 1);
         Fraction smaller = new Fraction(Integer.MAX_VALUE - 1, 1);
         assertTrue(huge.compareTo(smaller) > 0);
         assertTrue(smaller.compareTo(huge) < 0);
     }
 }
