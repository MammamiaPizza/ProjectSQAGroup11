import static org.junit.Assert.*;
 import org.apache.commons.math.complex.Complex;
 import org.apache.commons.math.complex.ComplexFormat;
 import java.text.ParseException;
 import org.junit.Test;

 /**
  * Tests for ComplexFormat.parse focusing on bug MATH-198: StringIndexOutOfBoundsException
  * when the imaginary character is missing from the end of the input string.
  */
 public class ComplexFormatTest {

     @Test
     public void testParseNormal() throws ParseException {
         ComplexFormat fmt = new ComplexFormat();
         Complex c = fmt.parse("1+2i");
         assertEquals(1.0, c.getReal(), 1e-12);
         assertEquals(2.0, c.getImaginary(), 1e-12);
     }

     @Test
     public void testParseNegativeImaginary() throws ParseException {
         ComplexFormat fmt = new ComplexFormat();
         Complex c = fmt.parse("1-2i");
         assertEquals(1.0, c.getReal(), 1e-12);
         assertEquals(-2.0, c.getImaginary(), 1e-12);
     }

     @Test
     public void testParseRealOnly() throws ParseException {
         ComplexFormat fmt = new ComplexFormat();
         Complex c = fmt.parse("123");
         assertEquals(123.0, c.getReal(), 1e-12);
         assertEquals(0.0, c.getImaginary(), 1e-12);
     }

     @Test(expected = ParseException.class)
     public void testParseEmpty() throws ParseException {
         new ComplexFormat().parse("");
     }

     @Test(expected = ParseException.class)
     public void testParseLeadingSignOnly() throws ParseException {
         new ComplexFormat().parse(" + i");
     }

     @Test(expected = ParseException.class)
     public void testParseSignAndImaginaryCharacterOnly() throws ParseException {
         new ComplexFormat().parse("1 - i");
     }

     /**
      * Core regression test for MATH-198: Missing imaginary character at end of string
      * must throw ParseException, not StringIndexOutOfBoundsException.
      */
     @Test
     public void testParseMissingImaginaryCharacter() {
         ComplexFormat fmt = new ComplexFormat();
         try {
             fmt.parse("1+2");
             fail("Expected ParseException for missing imaginary character 'i'");
         } catch (ParseException e) {
             assertEquals(3, e.getErrorOffset());
         }
     }

     /**
      * Same bug with a multi-character imaginary symbol, e.g., "ab".
      * Input "1+2a" is missing the second character; must not cause
      * StringIndexOutOfBoundsException.
      */
     @Test
     public void testParseMissingImaginaryCharacterMultiChar() {
         ComplexFormat fmt = new ComplexFormat();
         fmt.setImaginaryCharacter("ab");
         try {
             fmt.parse("1+2a");
             fail("Expected ParseException for missing imaginary character 'ab'");
         } catch (ParseException e) {
             assertEquals(3, e.getErrorOffset());
         }
     }

     @Test
     public void testParseCustomImaginaryCharacter() throws ParseException {
         ComplexFormat fmt = new ComplexFormat();
         fmt.setImaginaryCharacter("j");
         Complex c = fmt.parse("1+2j");
         assertEquals(1.0, c.getReal(), 1e-12);
         assertEquals(2.0, c.getImaginary(), 1e-12);
     }

     @Test
     public void testParseWithWhitespace() throws ParseException {
         ComplexFormat fmt = new ComplexFormat();
         Complex c = fmt.parse("  1  +   2i  ");
         assertEquals(1.0, c.getReal(), 1e-12);
         assertEquals(2.0, c.getImaginary(), 1e-12);
     }

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
}
