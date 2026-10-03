@Test
    public void testAbs() {
        Fraction negative = Fraction.getFraction(-3, 5);
        assertEquals(Fraction.getFraction(3, 5), negative.abs());
        assertSame(Fraction.ONE_HALF, Fraction.ONE_HALF.abs());
    }

 @Test
 public void testSubtractCoprime() {
     Fraction f1 = Fraction.getFraction(3, 5);
     Fraction f2 = Fraction.getFraction(1, 7);
     Fraction result = f1.subtract(f2);
     assertEquals(16, result.getNumerator());
     assertEquals(35, result.getDenominator());
 }

 @Test
 public void testSubtractCommonFactor() {
     Fraction f1 = Fraction.getFraction(1, 4);
     Fraction f2 = Fraction.getFraction(1, 6);
     Fraction result = f1.subtract(f2);
     assertEquals(1, result.getNumerator());
     assertEquals(12, result.getDenominator());
 }

 @Test
 public void testAddSubtractZero() {
     Fraction f = Fraction.getFraction(2, 3);
     assertEquals(f, f.add(Fraction.ZERO));
     assertEquals(f, f.subtract(Fraction.ZERO));
 }

But assertSame may not be imported. I'll use assertEquals for both, as the positive abs returns the
same instance; we can still check assertEquals. So:

 @Test
 public void testAbs() {
     Fraction negative = Fraction.getFraction(-3, 5);
     assertEquals(Fraction.getFraction(3, 5), negative.abs());
     assertEquals(Fraction.ONE_HALF, Fraction.ONE_HALF.abs());
 }

That covers abs.

Thus four@Test
    public void testAbs() {
        Fraction negative = Fraction.getFraction(-3, 5);
        assertEquals(Fraction.getFraction(3, 5), negative.abs());
        assertEquals(Fraction.ONE_HALF, Fraction.ONE_HALF.abs());
    }

 @Test
 public void testSubtractCoprime() {
     Fraction f1 = Fraction.getFraction(3, 5);
     Fraction f2 = Fraction.getFraction(1, 7);
     Fraction result = f1.subtract(f2);
     assertEquals(16, result.getNumerator());
     assertEquals(35, result.getDenominator());
 }

 @Test
 public void testSubtractCommonFactor() {
     Fraction f1 = Fraction.getFraction(1, 4);
     Fraction f2 = Fraction.getFraction(1, 6);
     Fraction result = f1.subtract(f2);
     assertEquals(1, result.getNumerator());
     assertEquals(12, result.getDenominator());
 }

 @Test
 public void testAddSubtractZero() {
     Fraction f = Fraction.getFraction(2, 3);
     assertEquals(f, f.add(Fraction.ZERO));
     assertEquals(f, f.subtract(Fraction.ZERO));
 }