@Test
public void testFormatComplexWithPositiveImaginary() {
    ComplexFormat cf = new ComplexFormat();
    Complex c = new Complex(1.5, 2.5);
    StringBuffer sb = new StringBuffer();
    java.text.FieldPosition fp = new java.text.FieldPosition(0);
    StringBuffer result = cf.format(c, sb, fp);
    assertNotNull(result);
    assertTrue(result.toString().contains("1.5"));
    assertTrue(result.toString().contains("2.5"));
}

@Test
public void testFormatComplexWithNegativeImaginary() {
    ComplexFormat cf = new ComplexFormat();
    Complex c = new Complex(1.5, -2.5);
    StringBuffer sb = new StringBuffer();
    java.text.FieldPosition fp = new java.text.FieldPosition(0);
    StringBuffer result = cf.format(c, sb, fp);
    assertNotNull(result);
    assertTrue(result.toString().contains("-"));
}

@Test
public void testFormatObjectWithNumber() {
    ComplexFormat cf = new ComplexFormat();
    java.text.FieldPosition fp = new java.text.FieldPosition(0);
    StringBuffer sb = new StringBuffer();
    StringBuffer result = cf.format(Double.valueOf(42.0), sb, fp);
    assertNotNull(result);
    assertTrue(result.toString().contains("42"));
}

@Test
public void testFormatObjectWithInvalidTypeThrowsException() {
    ComplexFormat cf = new ComplexFormat();
    java.text.FieldPosition fp = new java.text.FieldPosition(0);
    StringBuffer sb = new StringBuffer();
    try {
        cf.format("not_a_complex", sb, fp);
        fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
        // expected
    }
}