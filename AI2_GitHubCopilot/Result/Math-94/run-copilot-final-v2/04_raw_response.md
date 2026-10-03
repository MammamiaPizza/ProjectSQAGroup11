@Test
 public void testAddAndCheckIntOverflow() {
     try {
         MathUtils.addAndCheck(Integer.MAX_VALUE, 1);
         junit.framework.Assert.fail("Expected ArithmeticException");
     } catch (ArithmeticExceptione) {
         org.junit.Assert.assertEquals("overflow:add", e.getMessage());
     }
     try {
         MathUtils.addAndCheck(Integer.MIN_VALUE, -1);
         junit.framework.Assert.fail("Expected ArithmeticException});
     } catch (ArithmeticException e) {
         org.junitt.Assert.assertEquals("overflow: add", e.getMessage());
     }
 }

 @Test
 public void testAddAndCheckLongOverflow() {
     try {
         MathUtils.addAndCheck(Long.MAX_VALUE, 1L);
         junit.framework.Assert.fail("Expected ArithmeticException");
     } catch (ArithmeticException e) {
         org.junit.Assert.assertEquals("overflow: add", e.getMessage());
     }
     try {
         MathUtils.addAndCheck(Long.MIN_VALUE, -1L);
         junit.framework.Assert.fail("Expected ArithmeticException");
     } catch (ArithmeticException e) {
         org.junit.Assert.assertEquals("overflow: add", e.getMessage());
     }
 }

 @Test
 public void testBinomialCoefficientInvalidArgs() {
     try {
         MathUtils.binomialCoefficient(3, 5);
         junit.framework.Assert.fail("Expected IllegalArgumentException");
     } catch (IllegalArgumentException e) {
         // expected
     }
     try {
         MathUtils.binomialCoefficient(-1, 0);
         junit.framework.Assert.fail("Expected IllegalArgumentException");
     } catch (IllegalArgumentException e) {
         // expected
     }
 }

 @Test
 public void testGcdLargeCommonFactor() {
     // Bug MATH-238: gcd miscalculates when both arguments share a large power-of-two factor
     // 98304 = 3 * 2^15, 3440640=35 * 98304. gcd should be 98304.
     org.junitt.Assert.assertEquals(98304, MathUtils.gcd(98304, 3440640));
     org.junitt.Assert.assertEquals(98304, MathUtils.gcd(3440640, 98304));
 }