@Test
public void parseWithNonZeroPositionRejectsMissingImaginaryCharacterWithoutThrowing() {
    ComplexFormat format = new ComplexFormat();
    java.text.ParsePosition position = new java.text.ParsePosition(1);

    Complex result = format.parse("x1 + 2", position);

    assertNull(result);
    assertEquals(1, position.getIndex());
    assertTrue(position.getErrorIndex() >= 1);
}

@Test
public void formatsPositiveNegativeAndZeroImaginaryParts() {
    ComplexFormat format = ComplexFormat.getInstance(java.util.Locale.US);

    assertEquals("1 + 2i", format.format(new Complex(1.0, 2.0)));
    assertEquals("1 - 2i", format.format(new Complex(1.0, -2.0)));
    assertEquals("1", format.format(new Complex(1.0, 0.0)));
}

@Test
public void parsesCompleteNumberWithCustomMultiCharacterImaginarySuffix()
    throws java.text.ParseException {
    ComplexFormat format = new ComplexFormat(
        "imag",
        java.text.NumberFormat.getNumberInstance(java.util.Locale.US));

    Complex result = format.parse("1.5 + 2.25imag");

    assertEquals(1.5, result.getReal(), 0.0);
    assertEquals(2.25, result.getImaginary(), 0.0);
}

@Test
public void formatsNumberObjectAsRealComplexNumber() {
    ComplexFormat format = new ComplexFormat(
        java.text.NumberFormat.getNumberInstance(java.util.Locale.US));
    StringBuffer buffer = new StringBuffer();

    format.format(Double.valueOf(3.0), buffer, new java.text.FieldPosition(0));

    assertEquals("3", buffer.toString());
}