package org.apache.commons.math.complex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;

import org.junit.Test;

public class ComplexFormatMissingImaginaryCharacterTest {

    @Test
    public void parsesCompleteComplexNumberWithImaginaryCharacter() throws ParseException {
        ComplexFormat format = new ComplexFormat(NumberFormat.getInstance(Locale.US));

        Complex result = format.parse("1.5 + 2.25i");

        assertNotNull(result);
        assertEquals(1.5, result.getReal(), 0.0);
        assertEquals(2.25, result.getImaginary(), 0.0);
    }

    @Test
    public void parseStringRejectsMissingImaginaryCharacterWithParseException() {
        ComplexFormat format = new ComplexFormat(NumberFormat.getInstance(Locale.US));

        try {
            format.parse("1.5 + 2.25");
            fail("A complex number with an imaginary coefficient but no imaginary character must not parse");
        } catch (ParseException expected) {
            assertTrue(expected.getErrorOffset() >= 0);
        }
    }

    @Test
    public void parseWithPositionRejectsMissingImaginaryCharacterWithoutAdvancingPosition() {
        ComplexFormat format = new ComplexFormat(NumberFormat.getInstance(Locale.US));
        ParsePosition position = new ParsePosition(0);

        Complex result = format.parse("1.5 + 2.25", position);

        assertNull(result);
        assertEquals(0, position.getIndex());
        assertTrue(position.getErrorIndex() >= 0);
    }

    @Test
    public void frenchFormatRejectsMissingImaginaryCharacter() {
        ComplexFormat format = ComplexFormat.getInstance(Locale.FRANCE);
        ParsePosition position = new ParsePosition(0);

        Complex result = format.parse("1,5 + 2,25", position);

        assertNull(result);
        assertEquals(0, position.getIndex());
        assertTrue(position.getErrorIndex() >= 0);
    }

    @Test
    public void rejectsPartialMultiCharacterImaginarySuffix() {
        ComplexFormat format = new ComplexFormat("ij", NumberFormat.getInstance(Locale.US));
        ParsePosition position = new ParsePosition(0);

        Complex result = format.parse("1 + 2i", position);

        assertNull(result);
        assertEquals(0, position.getIndex());
        assertTrue(position.getErrorIndex() >= 0);
    }

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
}
