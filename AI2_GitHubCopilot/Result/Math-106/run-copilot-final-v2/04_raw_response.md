@Test
    public void testFormatProperFractionWithWholePositive() {
        ProperFractionFormat format = new ProperFractionFormat();
        Fraction f = new Fraction(5, 3);
        StringBuffer sb = new StringBuffer();
        format.format(f, sb, new FieldPosition(0));
        assertEquals("1 2/3", sb.toString());
    }

 @Test
 public void testFormatProperFractionWithWholeNegative() {
     ProperFractionFormat format = new ProperFractionFormat();
     Fraction f = new Fraction(-5, 3);
     StringBuffer sb = new StringBuffer();
     format.format(f, sb, new FieldPosition(0));
     assertEquals("-1 2/3", sb.toString());
 }

 @Test
 public void testFormatProperFractionWithoutWhole() {
     ProperFractionFormat format = new ProperFractionFormat();
     Fraction f = new Fraction(2, 3);
     StringBuffer sb = new StringBuffer();
     format.format(f, sb, new FieldPosition(0));
     assertEquals("2/3", sb.toString());
 }

 @Test
 public void testParseInvalidSlashAfterWhole() {
     ProperFractionFormat format = new ProperFractionFormat();
     ParsePosition pos = new ParsePosition(0);
     Fraction result = format.parse("1 /3", pos);
     assertNull(result);
 }