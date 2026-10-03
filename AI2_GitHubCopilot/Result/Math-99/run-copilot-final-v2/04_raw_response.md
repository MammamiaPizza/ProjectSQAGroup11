@Test(expected = ArithmeticException.class)
 public void testAddAndCheckIntPositiveOverflow() {
     MathUtils.addAndCheck(Integer.MAX_VALUE, 1);
 }

 @Test(expected = ArithmeticException.class)
 public void testAddAndCheckIntNegativeOverflow() {
     MathUtils.addAndCheck(Integer.MIN_VALUE, -1);
 }

 @Test(expected = ArithmeticException.class)
 public void testAddAndCheckLongPositiveOverflow() {
     MathUtils.addAndCheck(Long.MAX_VALUE, 1L);
 }

 @Test(expected = IllegalArgumentException.class)
 public void testBinomialCoefficientNLessThanK() {
     MathUtils.binomialCoefficient(3, 5);
 }